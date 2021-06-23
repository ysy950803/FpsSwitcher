package com.ysy.fpsswitcher

import android.provider.Settings

internal object FpsUtils {

    fun getUserRefreshRate(): Int =
        Settings.System.getInt(FSApp.getContext().contentResolver, "user_refresh_rate", 60)

    fun isNormalFpsEnabled() = getUserRefreshRate() <= 60
}
