package com.maunc.chronograph

import android.app.Application

class App : Application() {

    companion object {
        lateinit var app: Application
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}