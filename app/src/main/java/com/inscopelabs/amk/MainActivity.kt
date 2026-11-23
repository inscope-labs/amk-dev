package com.inscopelabs.amk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.inscopelabs.amk.application.AmkApplication
import com.inscopelabs.amk.game.standardgo.ui.theme.StandardGoTheme
import com.inscopelabs.amk.ui.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val app = application as AmkApplication
    setContent {
      StandardGoTheme {
        AppNavGraph(gameModule = app.gameModule)
      }
    }
  }
}
