package com.ysy.fpsswitcher

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class FSApp : Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var CONTEXT: Context? = null

        fun getContext(): Context = CONTEXT!!
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        CONTEXT = this
    }
}
