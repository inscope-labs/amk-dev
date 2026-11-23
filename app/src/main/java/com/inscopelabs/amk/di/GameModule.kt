package com.inscopelabs.amk.di

import android.content.Context
import com.inscopelabs.amk.data.repository.GameRepository
import com.inscopelabs.amk.game.standardgo.controller.GameController
import com.inscopelabs.amk.game.supergo.controller.TripleGameController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GameModule(context: Context) {
  private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  val repository: GameRepository = GameRepository(context.applicationContext)

  val standardGameController: GameController by lazy {
    GameController(
      onPersistSnapshot = { json ->
        ioScope.launch { repository.saveStandardSnapshot(json) }
      }
    )
  }

  val tripleGameController: TripleGameController by lazy {
    TripleGameController()
  }
}
