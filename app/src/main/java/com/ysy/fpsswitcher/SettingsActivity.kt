package com.ysy.fpsswitcher

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.blankj.utilcode.util.ToastUtils
import com.ysy.fpsswitcher.FpsUtils.DEFAULT_FPS_VALUE
import java.net.URLEncoder
import kotlin.math.abs

class SettingsActivity : AppCompatActivity() {

    companion object {
        val SP_KEY_BASIC_INFO by lazy { FSApp.getContext().getString(R.string.basic_info) }
        val SP_KEY_TO_ACCESS_SETTINGS by lazy {
            FSApp.getContext().getString(R.string.to_access_settings)
        }
        val SP_KEY_MASTER_SWITCH by lazy { FSApp.getContext().getString(R.string.master_switch) }
        val SP_KEY_TO_PERM_CENTER by lazy { FSApp.getContext().getString(R.string.to_perm_center) }
        val SP_KEY_SET_USER_REFRESH_RATE by lazy {
            FSApp.getContext().getString(R.string.set_user_refresh_rate)
        }
        val SP_KEY_TO_AUTO_START by lazy { FSApp.getContext().getString(R.string.to_auto_start) }
        val SP_KEY_LOVE_SUPPORT by lazy { FSApp.getContext().getString(R.string.love_support) }
        val SP_KEY_DEVELOPER_HOME by lazy { FSApp.getContext().getString(R.string.developer_home) }
        val SP_KEY_APP_HELP_TIPS by lazy { FSApp.getContext().getString(R.string.app_help_tips) }
    }

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

            findPreference<Preference>(SP_KEY_BASIC_INFO)?.apply {
                summary = if (FpsUtils.isHighFpsSupport()) {
                    """
                        ???????????????
                        ??????????????????????????????${FpsUtils.getAllRefreshRates().joinToString()} Hz???
                        ????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    """.trimIndent()
                } else {
                    getString(R.string.not_support_high_fps)
                }
            }

            findPreference<Preference>(SP_KEY_TO_ACCESS_SETTINGS)?.apply {
                setOnPreferenceClickListener {
                    // ???????????????????????????????????????????????????????????????fragment????????????????????????????????????????????????
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    true
                }
            }

            findPreference<Preference>(SP_KEY_TO_PERM_CENTER)?.apply {
                if (!FpsUtils.isDeviceRooted) {
                    isEnabled = false
                    return@apply
                } else {
                    isEnabled = true
                }
                setOnPreferenceClickListener {
                    if (FpsUtils.isAppRooted()) {
                        // ROOT????????????????????????????????????
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

            findPreference<SeekBarPreference>(SP_KEY_SET_USER_REFRESH_RATE)?.apply {
                max = FpsUtils.getPeakRefreshRate()
                setOnPreferenceChangeListener { _, newValue ->
                    if (newValue is Int && FpsUtils.isRefreshRateValid(newValue)) {
                        if (FpsUtils.isAppRooted()) {
                            if (newValue < FpsUtils.getUserRefreshRate()) {
                                FpsUtils.putUserRefreshRateRoot(newValue)
                            }
                            true
                        } else {
                            val rates = FpsUtils.getAllRefreshRates()
                            val diffs = rates.map { abs(newValue - it) }
                            value = rates[diffs.indexOf(diffs.minOrNull() ?: DEFAULT_FPS_VALUE)]
                            false
                        }
                    } else {
                        ToastUtils.showShort("???????????????????????????????????????")
                        false
                    }
                }
            }

            findPreference<Preference>(SP_KEY_TO_AUTO_START)?.setOnPreferenceClickListener {
                startActivity(Intent().apply {
                    setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                true
            }

            findPreference<Preference>(SP_KEY_LOVE_SUPPORT)?.setOnPreferenceClickListener {
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

            findPreference<Preference>(SP_KEY_DEVELOPER_HOME)?.setOnPreferenceClickListener {
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

            findPreference<Preference>(SP_KEY_APP_HELP_TIPS)?.apply {
                summary = """
                    1. ?????????????????? MIUI ??????
                    2. ???????????????FPS???????????????????????????????????????
                    3. ?????? MIUI ???????????????????????? BUG ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                """.trimIndent()
            }
        }

        override fun onResume() {
            super.onResume()
            findPreference<Preference>(SP_KEY_TO_ACCESS_SETTINGS)?.apply {
                updateAccessSettingsSummary(this)
            }
            findPreference<Preference>(SP_KEY_TO_PERM_CENTER)?.apply {
                updateRootSettingsSummary(this)
            }
            findPreference<SeekBarPreference>(SP_KEY_SET_USER_REFRESH_RATE)?.apply {
                isVisible = FpsUtils.isHighFpsSupport()
                if (isVisible) value = FpsUtils.getUserSetHighFps()
            }
        }

        private fun updateAccessSettingsSummary(preference: Preference) {
            val masterSwitch = findPreference<Preference>(SP_KEY_MASTER_SWITCH)
            preference.summary = if (FpsUtils.isAccessibilityServiceEnabled()) {
                masterSwitch?.isVisible = true
                "???????????????"
            } else {
                masterSwitch?.isVisible = false
                "??????????????????????????????????????????????????????->FPS?????????"
            }
        }

        private fun updateRootSettingsSummary(preference: Preference) {
            preference.summary = if (FpsUtils.isAppRooted()) {
                "???????????????"
            } else {
                "??????????????????????????????ROOT??????????????????????????????????????????????????????????????????????????????"
            }
        }
    }
}
