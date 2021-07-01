package com.ysy.fpsswitcher

import android.content.Context
import android.provider.Settings
import android.view.WindowManager
import androidx.preference.PreferenceManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.ShellUtils
import com.ysy.fpsswitcher.SettingsActivity.Companion.SP_KEY_MASTER_SWITCH
import com.ysy.fpsswitcher.SettingsActivity.Companion.SP_KEY_SET_USER_REFRESH_RATE

internal object FpsUtils {

    const val DEFAULT_FPS_VALUE = 60
    private const val TEST_DISPLAY_MODE = false

    fun isAccessibilityServiceEnabled() =
        ServiceUtils.isServiceRunning(FpsAccessibilityService::class.java)

    fun isAccessibilityServiceAllowed() =
        PreferenceManager.getDefaultSharedPreferences(FSApp.getContext())
            .getBoolean(SP_KEY_MASTER_SWITCH, true)

    fun getAllRefreshRates() =
        (FSApp.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.supportedModes.map { it.refreshRate.toInt() }

    /**
     * 获取当前屏幕刷新率
     */
    fun getUserRefreshRate(): Int {
        var r = Settings.System.getInt(
            // 接口不稳定，部分MIUI版本拿不到
            FSApp.getContext().contentResolver,
            "user_refresh_rate",
            0
        )
        if (TEST_DISPLAY_MODE || r == 0) {
            r = (FSApp.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.refreshRate.toInt()
        }
        return r
    }

    /**
     * 获取最高屏幕刷新率
     */
    fun getPeakRefreshRate(): Int {
        var r = Settings.System.getInt(
            // 接口不稳定，部分MIUI版本拿不到
            FSApp.getContext().contentResolver,
            "peak_refresh_rate",
            0
        )
        if (TEST_DISPLAY_MODE || r == 0) {
            r = (FSApp.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.supportedModes.map { it.refreshRate }.maxOrNull()?.toInt() ?: 0
        }
        return r
    }


    /**
     * 获取最低屏幕刷新率
     */
    fun getMinRefreshRate(): Int {
        var r = Settings.System.getInt(
            // 接口不稳定，部分MIUI版本拿不到
            FSApp.getContext().contentResolver,
            "min_refresh_rate",
            0
        )
        if (TEST_DISPLAY_MODE || r == 0) {
            r = (FSApp.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.supportedModes.map { it.refreshRate }.minOrNull()?.toInt() ?: 0
        }
        return r
    }

    fun getUserSetHighFps() = PreferenceManager.getDefaultSharedPreferences(FSApp.getContext())
        .getInt(SP_KEY_SET_USER_REFRESH_RATE, getPeakRefreshRate())

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
        execCmdRoot("settings put system user_refresh_rate ${getUserSetHighFps()}")
    } else {
        false
    }
}
