package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tictactoe.presentation.viewmodel.TicTacViewModel
import com.example.tictactoe.ui.screen.TicTacField
import com.example.tictactoe.ui.theme.TicTacToeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TicTacToeTheme {
        val viewModel = hiltViewModel<TicTacViewModel>()
        val state by viewModel.state.collectAsState()
        val isConnecting by viewModel.isConnecting.collectAsState()
        val showConnectionError by viewModel.showConnectionError.collectAsState()

        if(showConnectionError) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Couldn't connect to the server",
              color = Color.Red
            )
          }
          return@TicTacToeTheme
        }
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Column(
            modifier = Modifier
              .padding(top = 32.dp)
              .align(Alignment.TopCenter)
          ) {
            if(!state.connectedPlayers.contains('X')) {
              Text(
                text = "Waiting for player X",
                fontSize = 32.sp
              )
            } else if(!state.connectedPlayers.contains('O')) {
              Text(
                text = "Waiting for player O",
                fontSize = 32.sp
              )
            }
          }
          if(
            state.connectedPlayers.size == 2 && state.winningPlayer == null &&
            !state.isBoardFull
          ) {
            Text(
              text = if(state.playerAtTurn == 'X') {
                "X is next"
              } else "O is next",
              fontSize = 32.sp,
              modifier = Modifier
                .align(Alignment.TopCenter)
            )
          }
          TicTacField(
            state = state,
            onTapInField = viewModel::finishTurn,
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(1f)
              .padding(16.dp)
          )

          if(state.isBoardFull || state.winningPlayer != null) {
            Text(
              text = when(state.winningPlayer) {
                'X' -> "Player X won!"
                'O' -> "Player O won!"
                else -> "It's a draw!"
              },
              fontSize = 32.sp,
              modifier = Modifier
                .padding(bottom = 32.dp)
                .align(Alignment.BottomCenter)
            )
          }

          if(isConnecting) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator()
            }
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  TicTacToeTheme {
    Greeting("Android")
  }
}
