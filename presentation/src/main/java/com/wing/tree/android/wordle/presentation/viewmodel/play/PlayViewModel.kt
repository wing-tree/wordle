package com.wing.tree.android.wordle.presentation.viewmodel.play

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.domain.model.Result
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.usecase.statistics.UpdateStatisticsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.ContainUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetCountUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetWordUseCase
import com.wing.tree.android.wordle.presentation.constant.Try
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH
import com.wing.tree.android.wordle.presentation.delegate.play.*
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.model.play.Letters
import com.wing.tree.android.wordle.presentation.model.play.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val containUseCase: ContainUseCase,
    getCountUseCase: GetCountUseCase,
    getWordUseCase: GetWordUseCase,
    private val updateStatisticsUseCase: UpdateStatisticsUseCase,
    application: Application
) : AndroidViewModel(application),
    KeyboardActionDelegate by KeyboardActionDelegateImpl(),
    LettersChecker by LettersCheckerImpl(containUseCase),
    WordLoader by WordLoaderImpl(getCountUseCase, getWordUseCase)
{
    private lateinit var word: Word

    private val ioDispatcher = Dispatchers.IO

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    // todo 더 좋은 네이밍 구상.
    private val _letters = MutableLiveData(Array(Try.MAXIMUM) { Letters() })
    val letters: LiveData<Array<Letters>> get() = _letters

    private val excludedLetters = MutableLiveData<List<Letter>>()

    // 키보드 상태 네이밍 .. todo.
    private val _keys = MediatorLiveData<List<Letter>>()
    val keys: LiveData<List<Letter>> get() = _keys

    private val result = AtomicReference<Result?>(null)

    private var `try` = AtomicInteger(0)

    val currentLetters: Letters?
        get() = letters.value?.get(`try`.get())

    // todo 청소.. 정답 레터들.
    val matchedLetters: List<Letter> = letters.value?.map { it.filterIsState<State.Correct.InRightPlace>() }?.flatten()?.distinct() ?: emptyList()
    val notMatchedYetLetters by lazy {
        run {
            val w = word.word // 이걸로 레터 구성, 매치드 레터의 인덱스 위치의 값을 제거. 리스트 반환
            val arr = mutableListOf<Letter>()
            val mi = matchedLetters.map { it.position }


            w.forEachIndexed { index, c ->
                if (mi.contains(index).not()) {
                    arr.add(Letter(index, c))
                }
            }

            arr
        }
    }

    init {
        // 레터의 변경을 통지 받는다. 레터테이블. 플레이 테이블?? 뭔가 깔삼한 클래스 만드는것도 todo
        _keys.addSource(letters) { lettersArray ->
            val value = _keys.value?.toMutableList() ?: mutableListOf()

            lettersArray.flatMap { it }.forEach {
                if (it.state.notUnknown) {
                    value.add(it)
                }
            }

            _keys.value = value
        }

        _keys.addSource(excludedLetters) { letters ->
            val value = _keys.value?.toMutableList() ?: mutableListOf()

            letters.forEach {
                if (it.state.notUnknown) {
                    value.add(it)
                }
            }

            _keys.value = value
        }
    }

    fun add(letter: String) {
        _letters.value?.let { letters ->
            with(letters[`try`.get()]) {
                if (length < LENGTH) {
                    add(letter)

                    _letters.value = letters
                }
            }
        }
    }

    fun checkResult() = result.get()

    fun removeAt(`try`: Int, index: Int) {
        _letters.value?.let {
            try {
                with(it[`try`]) {
                    removeAt(index)
                    _letters.value = it
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
                Timber.e(e)
            }
        }
    }

    fun removeLast() {
        _letters.value?.let {
            with(it[`try`.get()]) {
                removeLast()
                _letters.value = it
            }
        }
    }

    fun submit(letters: Letters, @MainThread onSuccess: (Letters) -> Unit) {
        val word = word.word

        viewModelScope.launch(ioDispatcher) {
            submit(
                word,
                letters,
                onFailure = {

                },
                onSuccess = {
                    _letters.value?.let {
                        it[`try`.get()] = letters.apply { submitted = true }

                        _letters.value = it // 이게 보고가 들어가면 animation 동작함.
                    }

                    onSuccess(it)

                    // 여기서 리절트 등록 해줘야함.
                    currentLetters?.let {
                        if (it.matches(word)) {
                            win()
                        } else {
                            if (`try`.get() >= Try.MAXIMUM.dec()) {
                                lose()
                            } else {
                                // 여기서 뭐 해줘야함.
                                incrementTry()
                                enableKeyboard()
                            }
                        }
                    }
                }
            )
        }
    }

    fun load(@MainThread onLoaded: (Word) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            load(
                onSuccess = {
                    Timber.d(it.word)
                    word = it
                    onLoaded(word)
                },
                onFailure = {
                    Timber.e(it)
                    _error.value = it
                }
            )
        }
    }

    private fun incrementTry() {
        Timber.d("${`try`.incrementAndGet()}")
    }

    private fun lose() {
        updateStatistics(Result.Lose) {
            result.set(Result.Lose)
        }
    }

    private fun win() {
        updateStatistics(Result.Win) {
            result.set(Result.Win)
        }
    }

    private fun updateStatistics(result: Result, onComplete: () -> Unit) {
        val parameter = UpdateStatisticsUseCase.Parameter(result, `try`.get()) {
            // todo finally 달아줘야함.
            onComplete.invoke()
        }

        viewModelScope.launch(ioDispatcher) {
            updateStatisticsUseCase.invoke(parameter)
        }
    }

    private fun submitLetter(letter: Letter) {
        letters.value?.let {
            with(it[`try`.get()]) {
                set(
                    letter.position,
                    letter.apply {
                        state = State.Correct.InRightPlace()
                        submitted = true
                    }
                )

                _letters.value = it
            }
        }
    }

    fun useHint() {
        submitLetter(notMatchedYetLetters.random())
    }
}