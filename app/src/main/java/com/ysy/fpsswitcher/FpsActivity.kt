package com.ysy.fpsswitcher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FpsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fps)

        FpsAccessibilityService.hasFpsChanged = false
        startActivity(Intent("miui.intent.action.SCREEN_REFRESH"))
        finish()
    }
}
