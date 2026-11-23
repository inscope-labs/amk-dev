package com.inscopelabs.amk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inscopelabs.amk.di.GameModule
import com.inscopelabs.amk.game.standardgo.ui.screen.GameScreen
import com.inscopelabs.amk.game.standardgo.ui.screen.MenuScreen
import com.inscopelabs.amk.game.supergo.ui.screen.TripleGameScreen
import kotlinx.coroutines.launch

object Routes {
  const val MENU = "menu"
  const val STANDARD_GO = "standard_go"
  const val SUPER_GO = "super_go"
}

@Composable
fun AppNavGraph(
  gameModule: GameModule,
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
) {
  val savedJson by gameModule.repository.savedStandardSnapshotFlow.collectAsStateWithLifecycle(initialValue = null)
  val scope = rememberCoroutineScope()

  NavHost(
    navController = navController,
    startDestination = Routes.MENU,
    modifier = modifier,
  ) {
    composable(Routes.MENU) {
      MenuScreen(
        hasSavedStandardGame = !savedJson.isNullOrBlank(),
        onStartStandardGo = { config ->
          gameModule.standardGameController.newGame(config)
          navController.navigate(Routes.STANDARD_GO)
        },
        onResumeStandardGo = {
          scope.launch {
            val json = gameModule.repository.loadStandardSnapshot()
            if (json != null && gameModule.standardGameController.importJson(json)) {
              navController.navigate(Routes.STANDARD_GO)
            }
          }
        },
        onStartSuperGo = { superConfig ->
          gameModule.tripleGameController.newGame(superConfig)
          navController.navigate(Routes.SUPER_GO)
        },
      )
    }

    composable(Routes.STANDARD_GO) {
      GameScreen(
        controller = gameModule.standardGameController,
        onNavigateBack = { navController.popBackStack() },
      )
    }

    composable(Routes.SUPER_GO) {
      TripleGameScreen(
        controller = gameModule.tripleGameController,
        onNavigateBack = { navController.popBackStack() },
      )
    }
  }
}
