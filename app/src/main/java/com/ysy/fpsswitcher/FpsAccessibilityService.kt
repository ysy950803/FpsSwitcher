package com.ysy.fpsswitcher

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.ysy.fpsswitcher.FpsUtils.DEFAULT_FPS_VALUE

class FpsAccessibilityService : AccessibilityService() {

    companion object {
        var hasFpsChanged = false

        private const val TAG = "FpsAccessibilityService"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "onServiceConnected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (hasFpsChanged || !FpsUtils.isAccessibilityServiceAllowed()) return
        event.source?.let { findNextNode(it) }
    }

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt")
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    private fun findNextNode(node: AccessibilityNodeInfo): Boolean {
        node.apply {
            if (hasDifferentFps(text)) {
                hasFpsChanged = true
                parent?.let {
                    it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    it.recycle()
                }
                performGlobalAction(GLOBAL_ACTION_BACK)
                recycle()
                return true
            }
            for (i in 0 until node.childCount) {
                val c = node.getChild(i)
                if (c != null && findNextNode(c)) {
                    return true
                }
            }
            recycle()
            return false
        }
    }

    /**
     * 检查是否存在与当前刷新率不同的选项
     * MIUI刷新率修改界面的文案格式：n Hz
     */
    private fun hasDifferentFps(text: CharSequence?) = try {
        // 屏幕中遍历的帧率
        val fps = (text?.split(" ")?.firstOrNull()?.toInt()) ?: 0
        // 用户当前帧率
        val cur = FpsUtils.getUserRefreshRate()
        // 用户设置的切换最高帧率
        val user = FpsUtils.getUserSetHighFps()
        // 由于开关只有2种状态，因此只能在user和60之间切换
        if (cur == DEFAULT_FPS_VALUE) {
            // 若当前为60，则需要切换到user
            fps != 0 && fps == user
        } else {
            // 若当前不为60，则直接切到60
            fps != 0 && fps == DEFAULT_FPS_VALUE
        }
    } catch (e: Exception) {
        false
    }
}
