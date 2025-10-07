package one.yufz.hmspush.hook

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import one.yufz.hmspush.common.ANDROID_PACKAGE_NAME
import one.yufz.hmspush.common.HMS_CORE_PROCESS
import one.yufz.hmspush.common.HMS_PACKAGE_NAME
import one.yufz.hmspush.common.doOnce
import one.yufz.hmspush.hook.fakedevice.FakeDevice
import one.yufz.hmspush.hook.hms.HookHMS
import one.yufz.hmspush.hook.system.HookSystemService
import one.yufz.hmspush.hook.systemui.HookNotificationSettingsManager
import one.yufz.hmspush.hook.systemui.HookSystemUIPlugin
import one.yufz.xposed.hook


class XposedMod : IXposedHookLoadPackage {
    companion object {
        private const val TAG = "XposedMod"
    }

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        doOnce(lpparam.classLoader) {
            hook(lpparam)
        }
    }

    private fun hook(lpparam: LoadPackageParam) {
        XLog.d(TAG, "Loaded app: " + lpparam.packageName + " process:" + lpparam.processName)

        if (lpparam.processName == ANDROID_PACKAGE_NAME) {
            if (lpparam.packageName == ANDROID_PACKAGE_NAME) {
                HookSystemService().hook(lpparam.classLoader)
            }
            return
        }

        if (lpparam.packageName == "com.android.systemui") {
            removeHyperOSFocusNotificationPackageLimit(lpparam)
            return
        }

        if (lpparam.packageName == HMS_PACKAGE_NAME) {
            if (lpparam.processName == HMS_CORE_PROCESS) {
                HookHMS().hook(lpparam)
            }
            return
        }

//        if (lpparam.packageName == "com.android.systemui") {
//            HookSystemUI().hook(lpparam.classLoader)
//            return
//        }

        FakeDevice.fake(lpparam)
    }

    private fun removeHyperOSFocusNotificationPackageLimit(lpparam: LoadPackageParam) {
        HookSystemUIPlugin(
            "miui.systemui.plugin",
            HookNotificationSettingsManager()
        ).hook(lpparam.classLoader)

        HookSystemUIPlugin("miui.systemui.plugin") { pluginLoader ->
            val tag = "HookFocusNotifUtils"
            try {
                val classFocusNotifUtils = XposedHelpers.findClass(
                    "miui.systemui.notification.focus.FocusNotifUtils",
                    pluginLoader
                )

                XLog.d(tag, "hooking canShowFocus method")
                classFocusNotifUtils.declaredMethods.find { it.name == "canShowFocus" }!!
                    .hook {
                        replace {
                            true
                        }
                    }
            } catch (e: Throwable) {
                XLog.e(
                    tag,
                    "hook failure: " + e.message,
                    e
                )
            }
        }.hook(lpparam.classLoader)
    }
}