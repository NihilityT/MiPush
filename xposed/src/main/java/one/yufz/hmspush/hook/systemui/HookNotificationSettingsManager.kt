package one.yufz.hmspush.hook.systemui

import de.robv.android.xposed.XposedHelpers
import one.yufz.hmspush.hook.XLog
import one.yufz.xposed.hook

class HookNotificationSettingsManager : ISystemUIPluginHooker {
    companion object {
        private const val TAG = "FocusNotification"
    }

    override fun hook(pluginLoader: ClassLoader) {
        try {
            XLog.d(TAG, "hook start")
            val classNotificationSettingsManager = XposedHelpers.findClass(
                "miui.systemui.notification.NotificationSettingsManager",
                pluginLoader
            )

            XLog.d(TAG, "hook method")
            classNotificationSettingsManager.declaredMethods.find { it.name == "canCustomFocus" }!!
                .hook {
                    replace {
                        true
                    }
                }
            classNotificationSettingsManager.declaredMethods.find { it.name == "canShowFocus" }!!
                .hook {
                    replace {
                        true
                    }
                }
            XLog.d(TAG, "hook end")
        } catch (e: Throwable) {
            XLog.e(
                TAG,
                "hook NotificationSettingsManager failure: " + e.message,
                e
            )
        }
    }
}