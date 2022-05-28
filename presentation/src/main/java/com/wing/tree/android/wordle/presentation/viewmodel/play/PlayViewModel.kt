package com.wing.tree.android.wordle.presentation.viewmodel.play

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.wing.tree.android.wordle.domain.model.Result
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.model.item.Item
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
import com.wing.tree.android.wordle.presentation.eventbus.Event
import com.wing.tree.android.wordle.presentation.eventbus.EventBus
import com.wing.tree.android.wordle.presentation.mapper.PlayStateMapper.toDomainModel
import com.wing.tree.android.wordle.presentation.model.play.*
import com.wing.tree.android.wordle.presentation.util.setValueAfter
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import com.wing.tree.wordle.core.exception.NotEnoughCreditException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
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
    private lateinit var word: Word
    private val answer: String get() = word.value

    private val _keyboard = MediatorLiveData<Keyboard>()
    val keyboard: LiveData<Keyboard> get() = _keyboard

    private val _playBoard = MutableLiveData<PlayBoard>()
    val playBoard: LiveData<PlayBoard> get() = _playBoard

    private val playResult = MutableLiveData<PlayResult>(PlayResult.Undefined)

    private val _viewState = MediatorLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState

    private val currentLine: Line? get() = playBoard.value?.currentLine

    private val isAnimating = MutableLiveData<Boolean>()

    private val ioDispatcher = Dispatchers.IO
    private val mainDispatcher = Dispatchers.Main

    private val isEraserAvailable: Boolean get() = keyboard.value?.erasable(answer)?.isNotEmpty() ?: false
    private val isHintAvailable: Boolean get() = playBoard.value?.isHintAvailable(answer) ?: false
    private val isOneMoreTryAvailable: Boolean get() = true

    val round: Int get() = playBoard.value?.round ?: 0

    init {
        _viewState.value = ViewState.Ready

        viewModelScope.launch(ioDispatcher) {
            getPlayStateUseCase().collectLatest { result ->
                _viewState.postValue(ViewState.Loading)

                if (viewState.value is ViewState.Finish) {
                    cancel()
                } else {
                    result.getOrNull()?.let { playState ->
                        postPlayState(playState)
                    } ?: run {
                        _keyboard.postValue(Keyboard())
                        _playBoard.postValue(PlayBoard())
                    }

                    if (word.isBlank()) {
                        word = loadAtRandom() ?: run {
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
            val alphabets = value.alphabets

            board.notUnknownLetters.forEach { letter ->
                alphabets.find { it.letter == letter.value }?.updateState(Key.State.from(letter.state))
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
        currentLine?.let {
            if (it.isSubmitted) { return@let }

            _playBoard.setValueAfter { it.requestFocus() }
        }
    }

    fun setAnimating(value: Boolean) {
        isAnimating.value = value
    }

    fun setRunsAnimation(value: Boolean) {
        keyboard.value?.runsAnimation?.set(value)
        playBoard.value?.runsAnimation?.set(value)
    }

    @DelicateCoroutinesApi
    fun submit(@MainThread commitCallback: (kotlin.Result<Line>) -> Unit) {
        val currentLine = playBoard.value?.currentLine ?: return

        if (currentLine.notBlankCount < WORD_LENGTH) {
            return
        }

        viewModelScope.launch(ioDispatcher) {
            val result = submit(answer, currentLine)

            withContext(mainDispatcher) {
                if (result.isSuccess) {
                    playBoard.value?.let { playBoard ->
                        _playBoard.setValueAfter { submit() }
                        isAnimating.value = true

                        if (currentLine.matches(answer)) {
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

                commitCallback(result)
            }
        }
    }

    @DelicateCoroutinesApi
    fun lose() {
        updateStatistics(Result.Lose(round))

        playBoard.value?.let { playBoard ->
            val closest = playBoard.closest
            val letters = closest.letters
            val round = round
            val states = closest.map { it.state.toInt() }
            val word = word.value

            val lose = PlayResult.Lose(
                letters = letters,
                round = round,
                states = states,
                word = word
            )

            playResult.value = lose
        }
    }

    private fun postPlayState(playState: PlayState) {
        _keyboard.postValue(Keyboard.from(playState.keyboard))
        _playBoard.postValue(PlayBoard.from(playState.playBoard))
        word = playState.word
    }

    @DelicateCoroutinesApi
    private fun win() {
        updateStatistics(Result.Win(round))

        playResult.value = PlayResult.Win(round, answer)
    }

    @DelicateCoroutinesApi
    private fun updateStatistics(result: Result) {
        val parameter = UpdateStatisticsUseCase.Parameter(result, round)

        GlobalScope.launch(ioDispatcher) {
            updateStatisticsUseCase.invoke(parameter)
        }
    }

    private fun submitLetter(letter: Letter) {
        _playBoard.setValueAfter { currentLine.submitLetter(letter) }
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

    fun consumeItem(item: Item) {
        if (isItemAvailable(item).not()) { return }

        viewModelScope.launch {
            consume(item)
                .onFailure { throwable ->
                    when(throwable) {
                        is NotEnoughCreditException -> {
                            val event = Event.Exception.NotEnoughCredits

                            EventBus.getInstance().produceEvent(event)
                        }
                    }

                    Timber.e(throwable)
                }
                .onSuccess {
                    when(it) {
                        Item.Eraser -> onEraserConsumed()
                        Item.Hint -> onHintConsumed()
                        Item.OneMoreTry -> onOneMoreTryConsumed()
                    }
                }
        }
    }

    private fun isItemAvailable(item: Item) = when(item) {
        Item.Eraser -> isEraserAvailable
        Item.Hint -> isHintAvailable
        Item.OneMoreTry -> isOneMoreTryAvailable
        else -> throw IllegalArgumentException("$item")
    }

    private fun onEraserConsumed() {
        _keyboard.setValueAfter { erase(answer) }
    }

    private fun onHintConsumed() {
        playBoard.value?.let { submitLetter(it.hints(answer).random()) }
    }

    private fun onOneMoreTryConsumed() {
        _playBoard.setValueAfter { addRound() }
        _viewState.value = ViewState.Play
        playResult.value = PlayResult.Undefined
    }

    fun playAgain() {
        _playBoard.value = PlayBoard()
        _viewState.value = ViewState.Play
        playResult.value = PlayResult.Undefined
    }
}