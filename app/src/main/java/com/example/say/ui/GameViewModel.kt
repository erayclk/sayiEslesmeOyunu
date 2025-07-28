package com.example.say.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.say.data.Score
import com.example.say.data.ScoreRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CardData(val id: Int, val value: Int, var isFlipped: Boolean = false, var isMatched: Boolean = false)
enum class GameResult { IN_PROGRESS, WON, LOST_TIME }
data class GameState(
    val cards: List<CardData> = emptyList(), 
    val score: Int = 0, 
    val timeLeft: Int = 60, 
    val gameResult: GameResult = GameResult.IN_PROGRESS
)

class GameViewModel(
    private val username: String, 
    private val difficulty: Difficulty, 
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    private var timerJob: Job? = null
    private var openCards = mutableListOf<CardData>()

    init {
        startGame()
    }

    fun startGame() {
        timerJob?.cancel()
        val cardCount = if (difficulty == Difficulty.Kolay) 8 else 12
        val numbers = (1..100).shuffled().take(cardCount)
        val cardPairs = (numbers + numbers).shuffled()
        _gameState.value = GameState(
            cards = cardPairs.mapIndexed { index, value -> CardData(id = index, value = value) },
            timeLeft = 60
        )
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_gameState.value.timeLeft > 0 && _gameState.value.gameResult == GameResult.IN_PROGRESS) {
                delay(1000)
                _gameState.value = _gameState.value.copy(timeLeft = _gameState.value.timeLeft - 1)
            }
            if (_gameState.value.gameResult == GameResult.IN_PROGRESS) {
                _gameState.value = _gameState.value.copy(gameResult = GameResult.LOST_TIME)
            }
        }
    }

    fun onCardClicked(card: CardData) {
        if (card.isFlipped || card.isMatched || openCards.size >= 2) return

        val updatedCards = _gameState.value.cards.map {
            if (it.id == card.id) it.copy(isFlipped = true) else it
        }
        _gameState.value = _gameState.value.copy(cards = updatedCards)
        openCards.add(card)

        if (openCards.size == 2) {
            checkForMatch()
        }
    }

    private fun checkForMatch() {
        val (firstCard, secondCard) = openCards
        if (firstCard.value == secondCard.value) {
            val matchedCards = _gameState.value.cards.map {
                if (it.id == firstCard.id || it.id == secondCard.id) it.copy(isMatched = true) else it
            }
            _gameState.value = _gameState.value.copy(cards = matchedCards, score = _gameState.value.score + 10)
            openCards.clear()
            checkIfGameIsWon()
        } else {
            viewModelScope.launch {
                delay(1000)
                val resetCards = _gameState.value.cards.map {
                    if (!it.isMatched) it.copy(isFlipped = false) else it
                }
                _gameState.value = _gameState.value.copy(cards = resetCards)
                openCards.clear()
            }
        }
    }

    private fun checkIfGameIsWon() {
        if (_gameState.value.cards.all { it.isMatched }) {
            timerJob?.cancel()
            _gameState.value = _gameState.value.copy(gameResult = GameResult.WON)
        }
    }

    fun saveScore() {
        if (username.isNotBlank()) {
            val score = Score(username = username, score = _gameState.value.score)
            scoreRepository.saveScore(score)
        }
    }
}

class GameViewModelFactory(
    private val username: String, 
    private val difficulty: Difficulty, 
    private val scoreRepository: ScoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(username, difficulty, scoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
