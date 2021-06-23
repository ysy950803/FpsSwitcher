package com.ysy.fpsswitcher

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Process
import kotlin.system.exitProcess

class FSApp : Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var CONTEXT: Context? = null

        fun killSelf() {
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }

        fun getContext(): Context = CONTEXT!!
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        CONTEXT = this
    }
}
