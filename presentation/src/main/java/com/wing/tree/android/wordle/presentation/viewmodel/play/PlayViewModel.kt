package com.wing.tree.android.wordle.presentation.viewmodel.play

import android.app.Application
import android.media.MediaPlayer
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.wing.tree.android.wordle.domain.model.Result
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.model.item.Item
import com.wing.tree.android.wordle.domain.model.item.ItemType
import com.wing.tree.android.wordle.domain.model.playstate.PlayState
import com.wing.tree.android.wordle.domain.usecase.billing.ConsumeCreditsUseCase
import com.wing.tree.android.wordle.domain.usecase.core.getOrNull
import com.wing.tree.android.wordle.domain.usecase.item.ConsumeItemCountUseCase
import com.wing.tree.android.wordle.domain.usecase.item.GetItemCountUseCase
import com.wing.tree.android.wordle.domain.usecase.playstate.GetPlayStateUseCase
import com.wing.tree.android.wordle.domain.usecase.playstate.UpdatePlayStateUseCase
import com.wing.tree.android.wordle.domain.usecase.statistics.UpdateStatisticsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.ContainsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetWordUseCase
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.delegate.play.*
import com.wing.tree.android.wordle.presentation.mapper.PlayStateMapper.toDomainModel
import com.wing.tree.android.wordle.presentation.model.play.*
import com.wing.tree.android.wordle.presentation.util.setValueWith
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import com.wing.tree.android.wordle.domain.model.playstate.Keyboard as DomainKeyboard
import com.wing.tree.android.wordle.domain.model.playstate.PlayBoard as DomainPlayBoard

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val containsUseCase: ContainsUseCase,
    private val updatePlayStateUseCase: UpdatePlayStateUseCase,
    private val updateStatisticsUseCase: UpdateStatisticsUseCase,
    consumeCreditsUseCase: ConsumeCreditsUseCase,
    consumeItemCountUseCase: ConsumeItemCountUseCase,
    getItemCountUseCase: GetItemCountUseCase,
    getPlayStateUseCase: GetPlayStateUseCase,
    getWordUseCase: GetWordUseCase,
    application: Application
) : AndroidViewModel(application),
    ItemHandler by ItemHandlerImpl(
        consumeCreditsUseCase = consumeCreditsUseCase,
        consumeItemCountUseCase = consumeItemCountUseCase,
        getItemCountUseCase = getItemCountUseCase
    ),
    KeyboardActionDelegate by KeyboardActionDelegateImpl(),
    LettersChecker by LettersCheckerImpl(containsUseCase),
    WordLoader by WordLoaderImpl(getWordUseCase)
{
    private lateinit var _word: Word
    val word: Word get() = _word

    private val ioDispatcher = Dispatchers.IO

    private val mediaPlayer = MediaPlayer.create(application, R.raw.sound)

    init {
        viewModelScope.launch {
            getPlayStateUseCase().collect { result ->
                _viewState.value = ViewState.Loading

                if (viewState.value is ViewState.Finish) {
                    cancel()
                } else {
                    result.getOrNull()?.let { playState ->
                        _playBoard.value = PlayBoard.from(playState.playBoard)
                        _keyboard.value = Keyboard.from(playState.keyboard)
                        _word = playState.word
                    } ?: run {
                        _playBoard.value = PlayBoard()
                        _keyboard.value = Keyboard()
                    }

                    if (_word.value.isBlank()) {
                        _word = loadAtRandom() ?: run {
                            Timber.e(NullPointerException())
                            Word.Sorry
                        }
                    }

                    _viewState.value = ViewState.Play
                }
            }
        }
    }

    private val _viewState = MutableLiveData<ViewState>(ViewState.Ready)
    val viewState: LiveData<ViewState> get() = _viewState

    val isAnimating = MutableLiveData<Boolean>()

    private val _playBoard = MutableLiveData<PlayBoard>()
    val playBoard: LiveData<PlayBoard> get() = _playBoard

    private val _keyboard = MediatorLiveData<Keyboard>()
    val keyboard: LiveData<Keyboard> get() = _keyboard

    val round: Int get() = playBoard.value?.round ?: 0

    init {
        _keyboard.value = Keyboard()

        _keyboard.addSource(playBoard) { board ->
            val value = _keyboard.value ?: return@addSource
            val alphabetKeys = value.alphabetKeys

            board.notUnknownLetters.forEach { letter ->
                alphabetKeys.find { it.letter == letter.value }?.updateState(Key.State.from(letter.state))
            }

            _keyboard.value = value
        }
    }

    fun add(letter: String) {
        _playBoard.setValueWith { add(letter) }
    }

    fun removeAt(attempt: Int, index: Int) {
        _playBoard.setValueWith { removeAt(attempt, index) }
    }

    fun removeLast() {
        _playBoard.setValueWith { removeLast() }
    }

    // 콜백 너무많다.. todo 콜백 좀 줄입시더.
    fun submit(@MainThread commitCallback: (kotlin.Result<Line>) -> Unit) {
        val currentLetters = playBoard.value?.currentLine ?: return

        if (currentLetters.notBlankCount < WORD_LENGTH) return

        viewModelScope.launch(ioDispatcher) {
            submit(
                word,
                currentLetters,
                onFailure = {
                    commitCallback(kotlin.Result.failure(it))
                },
                onSuccess = {
                    playBoard.value?.let { board ->
                        board.submit()

                        _playBoard.value = board

                        commitCallback(kotlin.Result.success(it)) //todo 안쓰는거같은뎅.

                        if (it.matches(word.value)) {
                            win()
                        } else {
                            if (board.isRoundOver) {
                                _viewState.value = ViewState.Finish.RoundOver(board.isRoundAdded)
                            } else {
                                board.incrementRound()
                                enableKeyboard()
                            }
                        }
                    }
                }
            )
        }
    }

    @DelicateCoroutinesApi
    private fun win() {
        updateStatistics(Result.Win(round))
        _viewState.postValue(ViewState.Finish.Win)
    }

    @DelicateCoroutinesApi
    private fun updateStatistics(result: Result) {
        val round = playBoard.value?.round ?: 0

        val parameter = UpdateStatisticsUseCase.Parameter(result, round)

        GlobalScope.launch(ioDispatcher) {
            updateStatisticsUseCase.invoke(parameter)
        }
    }

    private fun submitLetter(letter: Letter) {
        _playBoard.setValueWith {
            currentLine.submit(letter)
        }
    }

    @DelicateCoroutinesApi
    fun updatePlayState() {
        GlobalScope.launch(ioDispatcher) {
            if (viewState.value is ViewState.Finish) {
                cancel()
            } else {
                val keyboard = keyboard.value?.toDomainModel() ?: Keyboard().toDomainModel()
                val playBoard = playBoard.value?.toDomainModel() ?: PlayBoard().toDomainModel()
                val word = object : Word {
                    override val index: Int = word.index
                    override val value: String = word.value
                }

                val playState = object : PlayState {
                    override val keyboard: DomainKeyboard = keyboard
                    override val playBoard: DomainPlayBoard = playBoard
                    override val word: Word = word
                }

                updatePlayStateUseCase(playState)
            }
        }
    }

    fun useItem(@ItemType itemType: Int) {
        viewModelScope.launch {
            if (use(itemType).isSuccess) {
                when(itemType) {
                    Item.Type.ERASER -> useEraser()
                    Item.Type.HINT -> useHint()
                    Item.Type.ONE_MORE_TRY -> useOneMoreTry()
                }
            } else {
                println("wwwww")
            }
        }
    }

    private fun useEraser() {
        keyboard.value?.let {
            it.erase(word)

            _keyboard.value = it
        }
    }

    private fun useHint() {
        playBoard.value?.let {
            if (it.filterWithState<Letter.State.In.Matched>().distinct().count() < WORD_LENGTH.dec()) {
                submitLetter(it.getNotMatchedYetLetters(_word).random())
            }
        }
    }

    private fun useOneMoreTry() {
        playBoard.value?.let {
            it.addRound()
            _playBoard.value = it
        }
    }

    fun tryAgain() {
        _playBoard.value = PlayBoard()
        _keyboard.value = Keyboard()
    }


    fun playSound() {
        mediaPlayer.start()
    }
}