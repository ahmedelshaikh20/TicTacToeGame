package com.example.tictactoe.data.client

import android.util.Log
import com.example.tictactoe.data.GameState
import com.example.tictactoe.data.MakeTurn
import com.example.tictactoe.data.RealtimeMessagingClient
import io.ktor.client.*
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.*
import io.ktor.http.HttpMethod
import io.ktor.http.cio.*
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorMessagingClient(
  val client: HttpClient
) : RealtimeMessagingClient {
  private var session: WebSocketSession? = null
  override  fun getGameStateStream(): Flow<GameState> {
    return flow {
      session = client.webSocketSession {
        url("ws://10.0.2.2:8080/play")
      }
      val gameStates = session!!
        .incoming
        .consumeAsFlow()
        .filterIsInstance<Frame.Text>()
        .mapNotNull { Json.decodeFromString<GameState>(it.readText()) }

    emitAll(gameStates)
      Log.d("LOOL" , gameStates.toString())

    }
  }

  override suspend fun sendAction(action: MakeTurn) {
    session?.outgoing?.send(
      Frame.Text("make_turn#${Json.encodeToString(action)}")
    )
  }

  override suspend fun close() {
    session?.close()
    session = null
  }
}
