package example.com.models

import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class TicTacToeGame {

    private val state = MutableStateFlow(GameState())

    private val playerSockets = ConcurrentHashMap<Char, WebSocketSession>()

    private val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var delayGameJob: Job? = null

    init {
        state.onEach {
            gameScope.launch {
                broadcast(it)
            }
        }
    }

    fun connectPlayer(session: WebSocketSession): Char? {
        val isPlayerX = state.value.connectedPlayers.any {  it == 'X' }
        val player = if(isPlayerX) 'O' else 'X'

        state.update {
            if(state.value.connectedPlayers.contains(player)) return null
            if(!playerSockets.containsKey(player)) {
                playerSockets[player] = session
            }
            it.copy(
                connectedPlayers = it.connectedPlayers + player
            )
        }
        return player
    }

    fun disconnectPlayer(player: Char){
        playerSockets.remove(player)
        state.update {
            it.copy(
                connectedPlayers = it.connectedPlayers - player
            )
        }
    }

    suspend fun broadcast(state: GameState){
        playerSockets.values.forEach { socket ->
            socket.send(Json.encodeToString(state))
        }
    }

    fun finishTurn(player: Char, x: Int, y: Int){
        if(state.value.field[y][x]!=null || state.value.winningPlayer!=null) return
        if(state.value.playerAtTurn!=player) return

        val currentPlayer = state.value.playerAtTurn
        state.update {
            val newField = it.field.also {field ->
                field[y][x] = currentPlayer
            }
            val isBoardFull = newField.all { it.all { it!=null }}
            if(isBoardFull){
                startNewRoundDelayed()
            }
            it.copy(
                playerAtTurn = if(currentPlayer == 'X') 'O' else 'X',
                field = newField,
                isBoardFull = isBoardFull,
                winningPlayer = getWinningPlayer()?.also{
                    startNewRoundDelayed()
                }
            )
        }
    }

    private fun getWinningPlayer(): Char? {
        val field = state.value.field
        // Check rows for a win
        for (i in 0..2) {
            if (field[i][0] != null && field[i][0] == field[i][1] && field[i][1] == field[i][2]) {
                return field[i][0]
            }
        }

        // Check columns for a win
        for (i in 0..2) {
            if (field[0][i] != null && field[0][i] == field[1][i] && field[1][i] == field[2][i]) {
                return field[0][i]
            }
        }

        // Check diagonals for a win
        if (field[0][0] != null && field[0][0] == field[1][1] && field[1][1] == field[2][2]) {
            return field[0][0]
        }
        if (field[0][2] != null && field[0][2] == field[1][1] && field[1][1] == field[2][0]) {
            return field[0][2]
        }

        // No winner
        return null
    }

    private fun startNewRoundDelayed() {
        delayGameJob?.cancel()
        delayGameJob = gameScope.launch {
            delay(5000)
            state.update {
                it.copy(
                    playerAtTurn = 'X',
                    field = GameState.emptyField(),
                    winningPlayer = null,
                    isBoardFull = false
                )
            }
        }
    }
}