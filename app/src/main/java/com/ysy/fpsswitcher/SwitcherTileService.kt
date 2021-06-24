package com.ysy.fpsswitcher

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

class SwitcherTileService : TileService() {

    companion object {
        private const val TAG = "SwitcherTileService"
    }

    private var mActiveIcon: Icon? = null
    private var mInActiveIcon: Icon? = null
    private var mHighFpsSupport = false

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        Log.d(TAG, "attachBaseContext")
        mActiveIcon = mActiveIcon ?: Icon.createWithResource(this, R.drawable.icon_60fps_white_24dp)
        mInActiveIcon =
            mInActiveIcon ?: Icon.createWithResource(this, R.drawable.icon_60fps_white_24dp)
                .setTint(0x80FFFFFF.toInt())
        mHighFpsSupport = FpsUtils.isHighFpsSupport()
    }

    override fun onStartListening() {
        super.onStartListening()
        Log.d(TAG, "onStartListening")
        if (mHighFpsSupport) {
            updateTile(FpsUtils.isDefaultFpsEnabled())
        } else {
            qsTile?.apply {
                state = Tile.STATE_UNAVAILABLE
                updateTile()
            }
        }
    }

    override fun onClick() {
        super.onClick()
        Log.d(TAG, "onClick ${qsTile?.state}")
        if (!mHighFpsSupport) return
        if (isLocked) {
            unlockAndRun { toggle() }
        } else {
            toggle()
        }
    }

    private fun toggle() {
        // 首次使用或进程被杀后，需要检查无障碍服务是否运行
        if (FpsUtils.isAccessibilityServiceEnabled()) {
            val enabled = FpsUtils.isDefaultFpsEnabled()
            if (!FpsUtils.switchToHighFpsRoot()) {
                startActivityAndCollapse(Intent(this, FpsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
            updateTile(!enabled)
        } else {
            startActivityAndCollapse(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    private fun updateTile(active: Boolean) {
        qsTile?.apply {
            icon = if (active) mActiveIcon else mInActiveIcon
            label = getString(R.string.fps_tile_label)
            state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            updateTile()
        }
    }
}
