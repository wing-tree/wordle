package com.wing.tree.android.wordle.presentation.viewmodel.play

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.wing.tree.android.wordle.domain.model.Result
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.usecase.statistics.UpdateStatisticsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.ContainUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetCountUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetWordUseCase
import com.wing.tree.android.wordle.domain.util.notNull
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH
import com.wing.tree.android.wordle.presentation.delegate.play.*
import com.wing.tree.android.wordle.presentation.model.play.*
import com.wing.tree.android.wordle.presentation.util.alphabet
import com.wing.tree.android.wordle.presentation.util.setValueWith
import com.wing.tree.android.wordle.presentation.view.play.PlayFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val containUseCase: ContainUseCase,
    private val updateStatisticsUseCase: UpdateStatisticsUseCase,
    getCountUseCase: GetCountUseCase,
    getWordUseCase: GetWordUseCase,
    application: Application
) : AndroidViewModel(application),
    KeyboardActionDelegate by KeyboardActionDelegateImpl(),
    LettersChecker by LettersCheckerImpl(containUseCase),
    WordLoader by WordLoaderImpl(getCountUseCase, getWordUseCase)
{
    private lateinit var word: Word

    private val ioDispatcher = Dispatchers.IO

    private val _state = MutableLiveData<State>(State.Ready)
    val state: LiveData<State> get() = _state

    val isAnimating = MutableLiveData<Boolean>()

    private val _board = MutableLiveData(Board())
    val board: LiveData<Board> get() = _board

    private val excludedLetters = MutableLiveData<List<Letter>>()

    // 키보드 상태 네이밍 .. todo.
//    private val _keys = MediatorLiveData<List<Letter>>()
//    val keys: LiveData<List<Letter>> get() = _keys

    private val _keyboard = MediatorLiveData<KeyBoard>()
    val keyboard: LiveData<KeyBoard> get() = _keyboard

    private val _result = MutableLiveData<Result>()
    val result: LiveData<Result> get() = _result

    // todo. 인터페이스 구현할것 delegate.. + 네이밍 체크. navigator 등. 쫌 이상하네 여기 없는게 맞다.
    // 상태 머신으로 처리해야함. 위에 다이얼로그 스테이트도..
    private val _directions = MediatorLiveData<NavDirections>()
    val directions: LiveData<NavDirections> get() = _directions

    init {
        _keyboard.addSource(board) { board ->
            // state 반영하기. 레터와 같은 스테이트로 변경.
            val value = _keyboard.value ?: KeyBoard()
            val alphabetKeys = value.alphabetKeys

            board.notUnknownLetters.forEach { letter ->
                alphabetKeys.find { it.letter == letter.value }?.updateState(letter.state)
            }

            _keyboard.value = value
        }

        _keyboard.addSource(excludedLetters) { letters ->
            val value = _keyboard.value ?: KeyBoard()
            val alphabetKeys = value.alphabetKeys

            letters.forEach { letter ->
                if (letter.state.notUnknown) {
                    alphabetKeys.find { it.letter == letter.value }?.updateState(letter.state)
                }
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
        val word = word.value
        val currentLetters = board.value?.currentLine ?: return

        if (currentLetters.notBlankCount < LENGTH) return

        viewModelScope.launch(ioDispatcher) {
            submit(
                word,
                currentLetters,
                onFailure = {

                },
                onSuccess = {
                    board.value?.let { board ->
                        board.submit()

                        _board.value = board

                        onSuccess(it)

                        if (it.matches(word)) {
                            win()
                        } else {
                            if (board.attemptExceeded) {
                                _state.value = State.Finish.RoundOver(board.attemptIncremented.get())
                                // todo 확인 및 제거.
//                                if (board.attemptIncremented.get()) {
//                                    lose()
//                                } else {
//                                    showAddAttemptDialog.value = true
//                                }
                            } else {
                                board.incrementAttempt()
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
                    word = it
                    onLoaded(word)
                },
                onFailure = {
                    Timber.e(it)
                    //_error.value = it // todo state 처리.
                }
            )
        }
    }

    fun lose() {
        updateStatistics(Result.Lose) {
            _result.postValue(Result.Lose)
        }
    }

    private fun win() {
        updateStatistics(Result.Win) {
            _result.postValue(Result.Win)
        }
    }

    private fun updateStatistics(result: Result, onComplete: () -> Unit) {
        val attempt = board.value?.attempt ?: 0

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
            currentLine[letter.position] = letter.apply {
                state = Letter.State.Included.Matched()
                submitted = true
            }
        }
    }

    fun useHint() {
        board.value?.let {
            if (it.filterWithState<Letter.State.Included.Matched>().distinct().count() < LENGTH.dec()) {
                submitLetter(it.getNotMatchedYetLetters(word).random())
            }
        }
    }

    fun useEraser() {
        val ex = excludedLetters.value?.map { letter -> letter.value } ?: emptyList()
        with(
            alphabet
            .filterNot { word.value.contains(it) }
            .filterNot { ex.contains(it) }
                .shuffled()
        ) {
            val value = excludedLetters.value?.toMutableList() ?: mutableListOf()

            value.addAll(take(3).map { Letter(0, it, Letter.State.Excluded()) })
            excludedLetters.value = value
        }
    }

    fun addAttempt() {
        board.value?.let {
            it.addAttempt()
            _board.value = it
        }
    }

    // keys 가 기본 스테이트를 가진 key 여야한다.
    fun tryAgain() {
        _board.value = Board()
        _keyboard.value = KeyBoard()
        excludedLetters.value = emptyList()
    }
}