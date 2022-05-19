package com.wing.tree.android.wordle.presentation.viewmodel.play

import android.app.Application
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
import com.wing.tree.android.wordle.presentation.delegate.play.*
import com.wing.tree.android.wordle.presentation.mapper.PlayStateMapper.toDomainModel
import com.wing.tree.android.wordle.presentation.model.play.*
import com.wing.tree.android.wordle.presentation.util.setValueAfter
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
    ItemConsumer by ItemConsumerImpl(
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

    private val isAnimating = MutableLiveData<Boolean>()

    private val _keyboard = MediatorLiveData<Keyboard>()
    val keyboard: LiveData<Keyboard> get() = _keyboard

    private val _playBoard = MutableLiveData<PlayBoard>()
    val playBoard: LiveData<PlayBoard> get() = _playBoard

    private val playResult = MutableLiveData<PlayResult>(PlayResult.Undefined)

    private val _viewState = MediatorLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState

    val isEraserAvailable: Boolean get() = keyboard.value?.erasableAlphabets(word)?.isNotEmpty() ?: false
    val isHintAvailable: Boolean get() = (playBoard.value?.availableHintCount(word) ?: 0) > 1
    val isOneMoreTryAvailable: Boolean get() = true

    val round: Int get() = playBoard.value?.round ?: 0

    init {
        _viewState.value = ViewState.Ready

        viewModelScope.launch(ioDispatcher) {
            getPlayStateUseCase().collect { result ->
                _viewState.postValue(ViewState.Loading)

                if (viewState.value is ViewState.Finish) {
                    cancel()
                } else {
                    result.getOrNull()?.let { playState ->
                        _word = playState.word
                        _playBoard.postValue(PlayBoard.from(playState.playBoard))
                        _keyboard.postValue(Keyboard.from(playState.keyboard))
                    } ?: run {
                        _playBoard.postValue(PlayBoard())
                        _keyboard.postValue(Keyboard())
                    }

                    if (word.value.isBlank()) {
                        _word = loadAtRandom() ?: run {
                            Timber.e(NullPointerException())
                            Word.Sorry
                        }
                    }

                    _viewState.postValue(ViewState.Play)

                    cancel()
                }
            }
        }

        _keyboard.addSource(playBoard) { board ->
            val value = _keyboard.value ?: return@addSource
            val alphabetKeys = value.alphabets

            board.notUnknownLetters.forEach { letter ->
                alphabetKeys.find { it.letter == letter.value }?.updateState(Key.State.from(letter.state))
            }

            _keyboard.value = value
        }

        _viewState.addSource(isAnimating) { isAnimating ->
            val playResult = playResult.value ?: return@addSource

            if (isAnimating.not()) {
                when(playResult) {
                    is PlayResult.Lose ->  _viewState.value = ViewState.Finish.Lose(playResult)
                    is PlayResult.RoundOver -> {
                        val isRoundAdded = playBoard.value?.isRoundAdded ?: false

                        _viewState.value = ViewState.RoundOver(isRoundAdded)
                    }
                    is PlayResult.Win -> _viewState.value = ViewState.Finish.Win(playResult)
                    is PlayResult.Undefined -> Unit
                }
            }
        }

        _viewState.addSource(playResult) {
            val isAnimating = isAnimating.value ?: return@addSource
            val playResult = playResult.value ?: return@addSource

            if (isAnimating.not()) {
                when(playResult) {
                    is PlayResult.Lose ->  _viewState.value = ViewState.Finish.Lose(playResult)
                    is PlayResult.RoundOver -> {
                        val isRoundAdded = playBoard.value?.isRoundAdded ?: false

                        _viewState.value = ViewState.RoundOver(isRoundAdded)
                    }
                    is PlayResult.Win -> _viewState.value = ViewState.Finish.Win(playResult)
                    is PlayResult.Undefined -> Unit
                }
            }
        }
    }

    fun add(letter: String) {
        _playBoard.setValueAfter { add(letter) }
    }

    fun removeAt(round: Int, index: Int) {
        _playBoard.setValueAfter { removeAt(round, index) }
    }

    fun removeLast() {
        _playBoard.setValueAfter { removeLast() }
    }

    fun requestFocus() {
        //round.inc() >= maximumRound todo 여기.. 다 해결됨 ㅋㅋ.
        if (playBoard.value?.isRoundOver == true) {
            return
        }

        _playBoard.setValueAfter {
            if (currentLine.isFocused) {
                return@setValueAfter
            } else {
                currentLine.requestFocus()
            }
        }

    }

    fun setAnimating(value: Boolean) {
        isAnimating.value = value
    }

    fun setRunsAnimation(value: Boolean) {
        keyboard.value?.runsAnimation?.set(value)
        playBoard.value?.runsAnimation?.set(value)
    }

    // 콜백 너무많다.. todo 콜백 좀 줄입시더.
    fun submit(@MainThread commitCallback: (kotlin.Result<Line>) -> Unit) {
        val currentLine = playBoard.value?.currentLine ?: return

        if (currentLine.notBlankCount < WORD_LENGTH) {
            return
        }

        viewModelScope.launch(ioDispatcher) {
            submit(
                word,
                currentLine,
                onFailure = {
                    commitCallback(kotlin.Result.failure(it))
                },
                onSuccess = { line ->
                    playBoard.value?.let { playBoard ->
                        _playBoard.setValueAfter { submit() }
                        isAnimating.value = true

                        commitCallback(kotlin.Result.success(line))

                        if (line.matches(word.value)) {
                            win()
                        } else {
                            if (playBoard.isRoundOver) {
                                playResult.value = PlayResult.RoundOver(playBoard.isRoundAdded)
                            } else {
                                playBoard.incrementRound()
                                enableKeyboard()
                            }
                        }
                    }
                }
            )
        }
    }

    @DelicateCoroutinesApi
    fun lose() {
        updateStatistics(Result.Lose(round))

        playBoard.value?.let { playBoard ->
            val closest = playBoard.closest
            val letters = closest.string
            val round = round
            val states = closest.map { it.state.toInt() }
            val word = word.value

            val lose = PlayResult.Lose(
                letters = letters,
                round = round,
                states = states,
                word = word
            )

            _viewState
            playResult.value = lose
        }
    }

    @DelicateCoroutinesApi
    private fun win() {
        updateStatistics(Result.Win(round))
        playResult.value = PlayResult.Win(round, word.value)
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
        _playBoard.setValueAfter { currentLine.submit(letter) }
    }

    @DelicateCoroutinesApi
    fun updatePlayState() {
        GlobalScope.launch(ioDispatcher) {
            if (viewState.value is ViewState.Finish) {
                cancel()
            } else {
                val keyboard = keyboard.value?.toDomainModel() ?: return@launch
                val playBoard = playBoard.value?.toDomainModel() ?: return@launch
                val word = Word.from(word)

                val playState = object : PlayState {
                    override val keyboard: DomainKeyboard = keyboard
                    override val playBoard: DomainPlayBoard = playBoard
                    override val word: Word = word
                }

                updatePlayStateUseCase(playState)
            }
        }
    }

    fun consumeItem(@ItemType itemType: Int) {
        if(isItemAvailable(itemType).not()) {
            return
        }

        viewModelScope.launch {
            consume(itemType)
                .onFailure { Timber.e(it) }
                .onSuccess {
                    when(itemType) {
                        Item.Type.ERASER -> onEraserConsumed()
                        Item.Type.HINT -> onHintConsumed()
                        Item.Type.ONE_MORE_TRY -> onOneMoreTryConsumed()
                    }
                }
        }
    }

    private fun isItemAvailable(@ItemType itemType: Int) = when(itemType) {
        Item.Type.ERASER -> isEraserAvailable
        Item.Type.HINT -> isHintAvailable
        Item.Type.ONE_MORE_TRY -> isOneMoreTryAvailable
        else -> throw IllegalArgumentException("$itemType")
    }

    private fun onEraserConsumed() {
        _keyboard.setValueAfter { erase(word) }
    }

    private fun onHintConsumed() {
        playBoard.value?.let {
            if (it.matched().distinct().count() < WORD_LENGTH.dec()) {
                submitLetter(it.availableHints(word).random())
            }
        }
    }

    private fun onOneMoreTryConsumed() {
        _playBoard.setValueAfter { addRound() }
    }

    fun tryAgain() {
        _keyboard.value = Keyboard()
        _playBoard.value = PlayBoard()
        playResult.value = PlayResult.Undefined
    }
}