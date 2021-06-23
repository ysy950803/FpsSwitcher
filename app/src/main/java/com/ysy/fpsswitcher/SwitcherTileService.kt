package com.ysy.fpsswitcher

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class SwitcherTileService : TileService() {

    private var mActiveIcon: Icon? = null
    private var mInActiveIcon: Icon? = null
    private var mFpsSupport = false

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        mActiveIcon = mActiveIcon ?: Icon.createWithResource(this, R.drawable.icon_60fps_white_24dp)
        mInActiveIcon =
            mInActiveIcon ?: Icon.createWithResource(this, R.drawable.icon_60fps_white_24dp)
                .setTint(0x80FFFFFF.toInt())
        mFpsSupport = true
    }

    override fun onStartListening() {
        super.onStartListening()
        if (mFpsSupport) {
            updateTile(FpsUtils.isNormalFpsEnabled())
        } else {
            qsTile?.apply {
                state = Tile.STATE_UNAVAILABLE
                updateTile()
            }
        }
    }

    override fun onClick() {
        super.onClick()
        if (!mFpsSupport) return
        if (isLocked) {
            unlockAndRun { toggle() }
        } else {
            toggle()
        }
    }

    private fun toggle() {
        val enabled = FpsUtils.isNormalFpsEnabled()
        startActivityAndCollapse(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        updateTile(!enabled)
    }

    private fun updateTile(active: Boolean) {
        qsTile?.apply {
            icon = if (active) mActiveIcon else mInActiveIcon
            label = getString(R.string.fps_tile_label)
            state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            updateTile()
        }
    }

    override fun onStopListening() {
        super.onStopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onTileAdded() {
        super.onTileAdded()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
    }
}
