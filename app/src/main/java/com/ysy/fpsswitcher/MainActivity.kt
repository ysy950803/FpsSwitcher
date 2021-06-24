package com.ysy.fpsswitcher

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn_to_perm_center).setOnClickListener {
            if (FpsUtils.isAppRooted()) {
                // ROOT权限下直接启动具体设置页
                FpsUtils.execCmdRoot(
                    "am start " +
                            "-n com.miui.securitycenter/com.miui.permcenter.root.RootManagementActivity " +
                            "-f ${Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK}"
                )
            } else {
                startActivity(Intent().apply {
                    setClassName("com.miui.securitycenter", "com.miui.permcenter.MainAcitivty")
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }

        findViewById<View>(R.id.btn_to_access_settings).setOnClickListener {
            // 无障碍服务权限对应每个应用的具体设置页是个fragment，无法直接启动，只能启动设置主页
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }

        findViewById<View>(R.id.btn_to_auto_start).setOnClickListener {
            if (FpsUtils.isAppRooted()) {
                // TODO ROOT权限下直接开关自启动
            } else {
                startActivity(Intent().apply {
                    setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                })
            }
        }

        findViewById<View>(R.id.btn_love_support).setOnClickListener {
            try {
                startActivity(
                    Intent.parseUri(
                        "alipays://platformapi/startapp?saId=10000007&qrcode=${
                            URLEncoder.encode(
                                "https://qr.alipay.com/fkx12362diu95oh2aweaac5",
                                "UTF-8"
                            )
                        }",
                        Intent.URI_INTENT_SCHEME
                    )
                )
            } catch (e: Exception) {
                startActivity(
                    Intent.parseUri(
                        "https://qr.alipay.com/fkx12362diu95oh2aweaac5",
                        Intent.URI_INTENT_SCHEME
                    )
                )
            }
        }
    }
}
