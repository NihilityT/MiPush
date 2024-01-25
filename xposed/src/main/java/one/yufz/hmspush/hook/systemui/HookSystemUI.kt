package one.yufz.hmspush.hook.systemui

import android.app.AndroidAppHelper
import android.app.Notification
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Icon
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import de.robv.android.xposed.XposedHelpers
import one.yufz.hmspush.hook.XLog
import one.yufz.xposed.callMethod
import one.yufz.xposed.findClass
import one.yufz.xposed.get
import one.yufz.xposed.hook
import one.yufz.xposed.hookAllMethods
import one.yufz.xposed.hookMethod

class HookSystemUI {
    companion object {
        private const val TAG = "HookSystemUI"
    }

    private val ID_ICON_IS_PRE_L: Int by lazy {
        val app = AndroidAppHelper.currentApplication()
        app.resources.getIdentifier("icon_is_pre_L", "id", app.packageName)
    }

    fun hook(classLoader: ClassLoader) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            classLoader.findClass("com.android.systemui.statusbar.notification.icon.IconManager")
                .hookAllMethods("setIcon") {
                    doAfter {
                        val iconView = args[2] as View
                        iconView.setTag(ID_ICON_IS_PRE_L, true)
                    }
                }
        } else {
            classLoader.findClass("com.android.systemui.statusbar.notification.collection.NotificationEntry")
                .hookMethod("setIconTag", Int::class.java, Any::class.java) {
                    doBefore {
                        if (args[0] == ID_ICON_IS_PRE_L) {
                            args[1] = true
                        }
                    }
                }
        }

        Notification.Builder::class.java.hookAllMethods("processSmallIconColor") {
            doBefore {
                val context: Context = thisObject["mContext"]
                val smallIcon = args[0] as Icon
                val contentView = args[1] as RemoteViews
                val p = args[2]

                val isGrayscaleIcon = thisObject.callMethod("getColorUtil")!!
                    .callMethod("isGrayscaleIcon", context, smallIcon) as Boolean

                if (!isGrayscaleIcon) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        contentView.setInt(android.R.id.icon, "setBackgroundColor", thisObject.callMethod("getBackgroundColor", p) as Int)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        contentView.setInt(android.R.id.icon, "setOriginalIconColor", 1)
                    }
                    result = true
                }
            }
        }
    }


    fun removeHyperOSFocusNotificationPackageLimit(classLoader: ClassLoader) {
        val tag = "FocusNotification"
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
                        XLog.d(tag, "appInfo is null")
                        return@doAfter
                    }
                    if ("miui.systemui.plugin" == appInfo!!.packageName && !isHooked) {
                        try {
                            isHooked = true
                            var pluginLoader = result as ClassLoader;

                            XLog.d(tag, "hook start")
                            val classNotificationSettingsManager = XposedHelpers.findClass(
                                "miui.systemui.notification.NotificationSettingsManager",
                                pluginLoader
                            )

                            XLog.d(tag, "hook method")
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
                            XLog.d(tag, "hook end")
                        } catch (e: Throwable) {
                            XLog.e(
                                tag,
                                "hook NotificationSettingsManager failure: " + e.message,
                                e
                            )
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            XLog.e(tag, "hook PluginInstance failure: " + e.message, e)
        }
    }
}