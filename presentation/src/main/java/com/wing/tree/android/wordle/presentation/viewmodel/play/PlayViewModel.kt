package com.wing.tree.android.wordle.presentation.viewmodel.play

import android.app.Application
import android.media.MediaPlayer
import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.wing.tree.android.wordle.domain.model.Result
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.model.playstate.Keyboard as DomainKeyboard
import com.wing.tree.android.wordle.domain.model.playstate.PlayBoard as DomainPlayBoard
import com.wing.tree.android.wordle.domain.model.playstate.PlayState
import com.wing.tree.android.wordle.domain.usecase.core.getOrNull
import com.wing.tree.android.wordle.domain.usecase.playstate.GetPlayStateUseCase
import com.wing.tree.android.wordle.domain.usecase.playstate.UpdatePlayStateUseCase
import com.wing.tree.android.wordle.domain.usecase.statistics.UpdateStatisticsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.ContainsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetCountUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetWordUseCase
import com.wing.tree.android.wordle.domain.util.notNull
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH
import com.wing.tree.android.wordle.presentation.delegate.play.*
import com.wing.tree.android.wordle.presentation.mapper.PlayStateMapper.toDomainModel
import com.wing.tree.android.wordle.presentation.model.play.*
import com.wing.tree.android.wordle.presentation.util.setValueWith
import com.wing.tree.android.wordle.presentation.view.play.PlayFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val containsUseCase: ContainsUseCase,
    private val updatePlayStateUseCase: UpdatePlayStateUseCase,
    private val updateStatisticsUseCase: UpdateStatisticsUseCase,
    getPlayStateUseCase: GetPlayStateUseCase,
    getCountUseCase: GetCountUseCase,
    getWordUseCase: GetWordUseCase,
    application: Application
) : AndroidViewModel(application),
    KeyboardActionDelegate by KeyboardActionDelegateImpl(),
    LettersChecker by LettersCheckerImpl(containsUseCase),
    WordLoader by WordLoaderImpl(getCountUseCase, getWordUseCase)
{
    private lateinit var _word: Word
    val word: Word get() = _word

    private val defaultDispatcher = Dispatchers.Default
    private val ioDispatcher = Dispatchers.IO

    private val mediaPlayer = MediaPlayer.create(application, R.raw.sound)

    init {
        viewModelScope.launch {
            getPlayStateUseCase(Unit).collect {
               it.getOrNull()?.let {
                   println("aaa word :${it.word}")
                   println("aaa playboard.lines :${it.playBoard.lines.toList()}")
                   println("aaa keyboard alphabets :${it.keyboard.alphabets.toList()}")
               }
            }
        }
    }

    private val _state = MutableLiveData<State>(State.Ready)
    val state: LiveData<State> get() = _state

    val isAnimating = MutableLiveData<Boolean>()

    private val _board = MutableLiveData(PlayBoard())
    val playBoard: LiveData<PlayBoard> get() = _board

    // 키보드 상태 네이밍 .. todo.
//    private val _keys = MediatorLiveData<List<Letter>>()
//    val keys: LiveData<List<Letter>> get() = _keys

    private val _keyboard = MediatorLiveData<Keyboard>()
    val keyboard: LiveData<Keyboard> get() = _keyboard

    private val _result = MutableLiveData<Result>()
    val result: LiveData<Result> get() = _result

    // todo. 인터페이스 구현할것 delegate.. + 네이밍 체크. navigator 등. 쫌 이상하네 여기 없는게 맞다.
    // 상태 머신으로 처리해야함. 위에 다이얼로그 스테이트도..
    private val _directions = MediatorLiveData<NavDirections>()
    val directions: LiveData<NavDirections> get() = _directions

    private val round: Int get() = playBoard.value?.round ?: 0

    init {
        _keyboard.value = Keyboard()

        _keyboard.addSource(playBoard) { board ->
            val value = _keyboard.value ?: return@addSource
            val alphabetKeys = value.alphabets

            board.notUnknownLetters.forEach { letter ->
                alphabetKeys.find { it.letter == letter.value }?.updateState(letter.state)
            }

            _keyboard.value = value
        }

        _directions.addSource(isAnimating) { isRunning ->
            if (isRunning.not() && result.value.notNull) {
                _directions.value = PlayFragmentDirections.actionPlayFragmentToResultFragment()
            }
        }

        _directions.addSource(result) {
            if (isAnimating.value == false) {
                _directions.value = PlayFragmentDirections.actionPlayFragmentToResultFragment()
            }
        }
    }

    fun add(letter: String) {
        _board.setValueWith { add(letter) }
    }

    fun removeAt(attempt: Int, index: Int) {
        _board.setValueWith { removeAt(attempt, index) }
    }

    fun removeLast() {
        _board.setValueWith { removeLast() }
    }

    // 콜백 너무많다.. todo 콜백 좀 줄입시더.
    fun submit(@MainThread onSuccess: (Line) -> Unit) {
        val word = _word.value
        val currentLetters = playBoard.value?.currentLine ?: return

        if (currentLetters.notBlankCount < LENGTH) return

        viewModelScope.launch(ioDispatcher) {
            submit(
                word,
                currentLetters,
                onFailure = {

                },
                onSuccess = {
                    playBoard.value?.let { board ->
                        board.submit()

                        _board.value = board

                        onSuccess(it) //todo 안쓰는거같은뎅.

                        if (it.matches(word)) {
                            win()
                        } else {
                            if (board.isRoundExceeded) {
                                _state.value = State.Finish.RoundOver(board.isRoundAdded)
                                // todo 확인 및 제거.
//                                if (board.attemptIncremented.get()) {
//                                    lose()
//                                } else {
//                                    showAddAttemptDialog.value = true
//                                }
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

    fun load(@MainThread onLoaded: (Word) -> Unit) {
        // 서스팬드로 넘기는게 맞지 않은가????
        viewModelScope.launch(ioDispatcher) {
            load(
                onSuccess = {
                    Timber.d(it.value) // todo 제거... 개발자 용임. 나중에 필요업으면..
                    _word = it
                    onLoaded(_word)
                },
                onFailure = {
                    Timber.e(it)
                    //_error.value = it // todo state 처리.
                    _state.postValue(State.Error(it))
                }
            )
        }
    }

    private fun win() {
        updateStatistics(Result.Win(round)) {
            _result.postValue(Result.Win(round))
        }
    }

    private fun updateStatistics(result: Result, onComplete: () -> Unit) {
        val attempt = playBoard.value?.round ?: 0

        val parameter = UpdateStatisticsUseCase.Parameter(result, attempt) {
            // todo finally 달아줘야함.
            onComplete.invoke()
        }

        viewModelScope.launch(ioDispatcher) {
            updateStatisticsUseCase.invoke(parameter)
        }
    }

    private fun submitLetter(letter: Letter) {
        _board.setValueWith {
            currentLine.submit(letter)
        }
    }

    @DelicateCoroutinesApi
    fun updatePlayState() {
        GlobalScope.launch(defaultDispatcher) {
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

            updatePlayStateUseCase.invoke(playState)
        }
    }

    fun useHint() {
        playBoard.value?.let {
            if (it.filterWithState<Letter.State.In.Matched>().distinct().count() < LENGTH.dec()) {
                submitLetter(it.getNotMatchedYetLetters(_word).random())
            }
        }
    }

    fun useEraser() {
        keyboard.value?.let {
            it.erase(_word)

            _keyboard.value = it
        }
    }

    fun addRound() {
        playBoard.value?.let {
            it.addRound()
            _board.value = it
        }
    }

    fun tryAgain() {
        _board.value = PlayBoard()
        _keyboard.value = Keyboard()
    }


    fun playSound() {
        mediaPlayer.start()
    }
}