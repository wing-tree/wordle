package com.wing.tree.android.wordle.presentation.viewmodel.play

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.usecase.core.Result
import com.wing.tree.android.wordle.domain.usecase.core.map
import com.wing.tree.android.wordle.domain.usecase.statistics.UpdateStatisticsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.ContainUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetCountUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetWordUseCase
import com.wing.tree.android.wordle.presentation.constant.Try
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH
import com.wing.tree.android.wordle.presentation.delegate.play.KeyboardActionDelegate
import com.wing.tree.android.wordle.presentation.delegate.play.KeyboardActionDelegateImpl
import com.wing.tree.android.wordle.presentation.delegate.play.LettersChecker
import com.wing.tree.android.wordle.presentation.delegate.play.LettersCheckerImpl
import com.wing.tree.android.wordle.presentation.model.play.GameResult
import com.wing.tree.android.wordle.presentation.model.play.Letters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val containUseCase: ContainUseCase,
    private val getCountUseCase: GetCountUseCase,
    private val getWordUseCase: GetWordUseCase,
    private val updateStatisticsUseCase: UpdateStatisticsUseCase,
    application: Application
) : AndroidViewModel(application),
    KeyboardActionDelegate by KeyboardActionDelegateImpl(),
    LettersChecker by LettersCheckerImpl(containUseCase)
{
    private lateinit var word: Word

    private val ioDispatcher = Dispatchers.IO
    private val mainDispatcher = Dispatchers.Main

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _letters = MutableLiveData(Array(Try.MAXIMUM) { Letters() })
    val letters: LiveData<Array<Letters>> get() = _letters

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult> get() = _gameResult

    // todo trial 단어 체크.
    private var _try = 0
    val `try`: Int get() = _try

    val currentLetters: Letters?
        get() = letters.value?.get(`try`)

    fun add(letter: String) {
        _letters.value?.let {
            with(it[`try`]) {
                if (length < LENGTH) {
                    add(letter)

                    _letters.value = it
                }
            }
        }
    }

    fun removeAt(`try`: Int, index: Int) {
        _letters.value?.let {
            try {
                with(it[`try`]) {
                    if (isNotEmpty) {
                        removeAt(index)

                        _letters.value = it
                    }
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
                Timber.e(e)
            }
        }
    }

    fun removeLast() {
        _letters.value?.let {
            with(it[`try`]) {
                if (isNotEmpty) {
                    removeLast()
                    _letters.value = it
                }
            }
        }
    }

    fun submit(letters: Letters) {
        val word = word.word

        viewModelScope.launch(ioDispatcher) {
            submit(
                word,
                letters,
                onError = {

                },
                onSuccess = {
                    _letters.value?.let {
                        it[`try`] = letters.apply { submitted = true }

                        _letters.value = it
                    }
                }
            )
        }
    }

    fun match() {
        currentLetters?.let {
            if (it.matches(word.word)) {
                win()
            } else {
                if (`try` >= Try.MAXIMUM) {
                    lose()
                } else {
                    incrementTry()
                    enableKeyboard()
                }
            }
        }
    }

    fun load(@MainThread onLoaded: (Word) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            getCountUseCase.invoke(Unit).map { result ->
                when (result) {
                    is Result.Error -> _error.postValue(result.throwable)
                    is Result.Success -> {
                        withContext(mainDispatcher) {
                            val index = Random.nextInt(result.data).inc()

                            setWord(index, onLoaded)
                        }
                    }
                }
            }
        }
    }

    private fun setWord(index: Int, @MainThread onSuccess: (word: Word) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            getWordUseCase.invoke(GetWordUseCase.Parameter(index)).map { result ->
                when(result) {
                    is Result.Error -> _error.postValue(result.throwable)
                    is Result.Success -> {
                        word = result.data

                        Timber.d("aaaaaaa $word")

                        withContext(mainDispatcher) {
                            onSuccess(word)
                        }
                    }
                }
            }
        }
    }

    private fun incrementTry() {
        ++_try
    }

    private fun lose() {
        _gameResult.value = GameResult.Lose
    }

    private fun win() {
        _gameResult.value = GameResult.Win
    }
}