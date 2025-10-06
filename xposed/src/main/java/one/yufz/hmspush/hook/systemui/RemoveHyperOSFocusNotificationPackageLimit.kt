package one.yufz.hmspush.hook.systemui

import android.content.pm.ApplicationInfo
import de.robv.android.xposed.XposedHelpers
import one.yufz.hmspush.hook.XLog
import one.yufz.xposed.hook

class RemoveHyperOSFocusNotificationPackageLimit {
    companion object {
        private const val TAG = "FocusNotification"
    }

    fun hook(classLoader: ClassLoader) {
        try {
            val classFactory = XposedHelpers.findClass(
                "com.android.systemui.shared.plugins.PluginInstance\$Factory",
                classLoader
            )
            var appInfo: ApplicationInfo? = null
            classFactory.declaredMethods.find { it.name == "create" }!!.hook {
                doBefore {
                    appInfo = args[1] as ApplicationInfo
                }
            }
            val classExternalSyntheticLambda0 = XposedHelpers.findClass(
                "com.android.systemui.shared.plugins.PluginInstance\$Factory$\$ExternalSyntheticLambda0",
                classLoader
            )
            classExternalSyntheticLambda0.declaredMethods.find { it.name == "get" }!!.hook {
                var isHooked = false;
                doAfter {
                    if (appInfo == null) {
                        XLog.d(TAG, "appInfo is null")
                        return@doAfter
                    }
                    if ("miui.systemui.plugin" == appInfo!!.packageName && !isHooked) {
                        isHooked = true
                        HookNotificationSettingsManager().doHook(result as ClassLoader)
                    }
                }
            }
        } catch (e: Throwable) {
            XLog.e(TAG, "hook PluginInstance failure: " + e.message, e)
        }
    }

}