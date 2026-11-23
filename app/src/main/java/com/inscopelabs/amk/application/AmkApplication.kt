package com.inscopelabs.amk.application

import android.app.Application
import com.inscopelabs.amk.di.GameModule

class AmkApplication : Application() {
  lateinit var gameModule: GameModule
    private set

  override fun onCreate() {
    super.onCreate()
    gameModule = GameModule(this)
  }
}
