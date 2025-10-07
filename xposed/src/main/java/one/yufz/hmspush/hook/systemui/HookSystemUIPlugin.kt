package one.yufz.hmspush.hook.systemui

import android.content.ComponentName
import android.content.ContextWrapper
import de.robv.android.xposed.XposedHelpers
import one.yufz.hmspush.hook.XLog
import one.yufz.xposed.getField
import one.yufz.xposed.hook

class HookSystemUIPlugin(
    private val pluginPackageName: String, private val hooker: ISystemUIPluginHooker
) {
    companion object {
        private const val TAG = "HookSystemUIPlugin"
    }

    fun hook(classLoader: ClassLoader) {
        try {
            val classPluginFactory = XposedHelpers.findClass(
                "com.android.systemui.shared.plugins.PluginInstance\$PluginFactory",
                classLoader
            )
            classPluginFactory.declaredMethods.find { it.name == "createPluginContext" }!!.hook {
                doAfter {
                    val componentName =
                        thisObject.getField("mComponentName", ComponentName::class.java)
                    if (componentName!!.packageName == pluginPackageName) {
                        unhook()
                        val pluginContext = result as ContextWrapper
                        val pluginLoader = pluginContext.classLoader
                        XLog.d(TAG, "hook [$pluginPackageName] by Plugin ClassLoader: [$pluginLoader]")
                        hooker.hook(pluginLoader)
                    }
                }
            }
        } catch (e: Throwable) {
            XLog.e(
                TAG,
                "hook SystemUI Plugin [$pluginPackageName] with [${hooker.javaClass.name}] failure: " + e.message,
                e
            )
        }
    }

}