package com.ysy.fpsswitcher

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import java.net.URLEncoder

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            findPreference<Preference>("basic_info")?.apply {
                summary = if (FpsUtils.isHighFpsSupport()) {
                    getString(R.string.support_high_fps, FpsUtils.getPeakRefreshRate())
                } else {
                    getString(R.string.not_support_high_fps)
                }
            }

            findPreference<Preference>("btn_to_access_settings")?.apply {
                updateAccessSettingsSummary(this)
                setOnPreferenceClickListener {
                    // 无障碍服务权限对应每个应用的具体设置页是个fragment，无法直接启动，只能启动设置主页
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    true
                }
            }

            findPreference<Preference>("to_perm_center")?.apply {
                updateRootSettingsSummary(this)
                if (!FpsUtils.isDeviceRooted) {
                    isEnabled = false
                    return@apply
                } else {
                    isEnabled = true
                }
                setOnPreferenceClickListener {
                    if (FpsUtils.isAppRooted()) {
                        // ROOT权限下直接启动具体设置页
                        FpsUtils.execCmdRoot(
                            "am start " +
                                    "-n com.miui.securitycenter/com.miui.permcenter.root.RootManagementActivity " +
                                    "-f ${Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK}"
                        )
                    } else {
                        startActivity(Intent().apply {
                            setClassName(
                                "com.miui.securitycenter",
                                "com.miui.permcenter.MainAcitivty"
                            )
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    }
                    true
                }
            }

            findPreference<Preference>("to_auto_start")?.setOnPreferenceClickListener {
                startActivity(Intent().apply {
                    setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                true
            }

            findPreference<Preference>("love_support")?.setOnPreferenceClickListener {
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
                true
            }

            findPreference<Preference>("developer_home")?.setOnPreferenceClickListener {
                try {
                    startActivity(
                        Intent.parseUri(
                            "coolmarket://u/4617184",
                            Intent.URI_INTENT_SCHEME
                        )
                    )
                } catch (e: Exception) {
                    startActivity(
                        Intent.parseUri(
                            "https://www.coolapk.com/u/4617184",
                            Intent.URI_INTENT_SCHEME
                        )
                    )
                }
                true
            }

            findPreference<Preference>("app_help_tips")?.apply {
                summary = """
                    1. 此工具只支持 MIUI 哦；
                    2. 长按通知栏FPS快捷开关可再次进入此页面；
                    3. 由于 MIUI 部分版本和机型的 BUG 可能导致开关点击偶尔反应延迟，故请尽量保证开关放置在通知栏首页（下拉即可见），能缓解问题。
                """.trimIndent()
            }
        }

        override fun onResume() {
            super.onResume()
            findPreference<Preference>("btn_to_access_settings")?.apply {
                updateAccessSettingsSummary(this)
            }
            findPreference<Preference>("to_perm_center")?.apply {
                updateRootSettingsSummary(this)
            }
        }

        private fun updateAccessSettingsSummary(preference: Preference) {
            preference.summary = if (FpsUtils.isAccessibilityServiceEnabled()) {
                "当前已开启"
            } else {
                "当前未开启，请依次点击“已下载的应用->FPS开关”"
            }
        }

        private fun updateRootSettingsSummary(preference: Preference) {
            preference.summary = if (FpsUtils.isAppRooted()) {
                "当前已开启"
            } else {
                "当前未开启，授予应用ROOT权限后，将获得更好的体验，且无需再开启无障碍服务权限"
            }
        }
    }
}
