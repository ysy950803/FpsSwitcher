package com.ysy.fpsswitcher

import android.provider.Settings
import androidx.preference.PreferenceManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.ShellUtils

internal object FpsUtils {

    private const val DEFAULT_FPS_VALUE = 60

    fun isAccessibilityServiceEnabled() =
        ServiceUtils.isServiceRunning(FpsAccessibilityService::class.java)

    fun isAccessibilityServiceAllowed() =
        PreferenceManager.getDefaultSharedPreferences(FSApp.getContext())
            .getBoolean("master_switch", true)

    /**
     * 获取当前屏幕刷新率
     */
    fun getUserRefreshRate(): Int =
        Settings.System.getInt(
            FSApp.getContext().contentResolver,
            "user_refresh_rate",
            DEFAULT_FPS_VALUE
        )

    /**
     * 获取最高屏幕刷新率
     */
    fun getPeakRefreshRate(): Int =
        Settings.System.getInt(
            FSApp.getContext().contentResolver,
            "peak_refresh_rate",
            DEFAULT_FPS_VALUE
        )

    fun getMinRefreshRate(): Int =
        Settings.System.getInt(
            FSApp.getContext().contentResolver,
            "min_refresh_rate",
            DEFAULT_FPS_VALUE
        )

    fun isDefaultFpsEnabled() = getUserRefreshRate() <= DEFAULT_FPS_VALUE

    fun isHighFpsSupport() = getPeakRefreshRate() > DEFAULT_FPS_VALUE

    val isDeviceRooted by lazy { DeviceUtils.isDeviceRooted() }

    fun isAppRooted() = isDeviceRooted && AppUtils.isAppRoot()

    fun isRefreshRateValid(value: Int) =
        getMinRefreshRate() <= value && value <= getPeakRefreshRate()

    // 以下为ROOT后才能调用的方法

    fun execCmdRoot(cmd: String) = if (isAppRooted()) {
        ShellUtils.execCmd(cmd, true).result == 0
    } else {
        false
    }

    fun putUserRefreshRateRoot(value: Int) = if (isAppRooted()) {
        execCmdRoot("settings put system user_refresh_rate $value")
    } else {
        false
    }

    fun switchToHighFpsRoot() = if (isAppRooted()) {
        val rootUserRefreshRate = PreferenceManager.getDefaultSharedPreferences(FSApp.getContext())
            .getInt("root_user_refresh_rate", DEFAULT_FPS_VALUE)
        execCmdRoot("settings put system user_refresh_rate $rootUserRefreshRate")
    } else {
        false
    }
}
