package com.ysy.fpsswitcher

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

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
        val fps = (text?.split(" ")?.firstOrNull()?.toInt()) ?: 0
        val cur = FpsUtils.getUserRefreshRate()
        fps != 0 && fps != cur
    } catch (e: Exception) {
        false
    }
}
