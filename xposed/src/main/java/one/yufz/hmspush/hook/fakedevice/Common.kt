package one.yufz.hmspush.hook.fakedevice

import de.robv.android.xposed.callbacks.XC_LoadPackage
import one.yufz.hmspush.hook.XLog
import one.yufz.xposed.hookMethod
import miui.external.SdkHelper

open class Common : IFakeDevice {
    companion object {
        private const val TAG = "Common"
    }

    override fun fake(lpparam: XC_LoadPackage.LoadPackageParam): Boolean {
        XLog.d(TAG, "fake() called with: packageName = ${lpparam.packageName}")
        fakeAllBuildInProperties()
        fakeClass(lpparam)
        return true
    }

    private fun fakeClass(lpparam: XC_LoadPackage.LoadPackageParam) {
        var isMIUI = false
        try {
            // check MIUI environment
            Class.forName("miui.os.Build", false, lpparam.classLoader)
            isMIUI = true
        } catch (_: Throwable) {
        }
        if (isMIUI) {
            return
        }

        val classMap: Map<String, Class<out Any>> = mapOf(
            "miui.os.Build" to Object::class.java,
            SdkHelper::class.java.name to SdkHelper::class.java,
        )
        Class::class.java.hookMethod(
            "forName",
            String::class.java,
            Boolean::class.java,
            ClassLoader::class.java
        ) {
            doBefore {
                var requestClass = args[0]
                val returnClass = classMap[requestClass]
                if (returnClass != null) {
                    XLog.d(TAG, "forHook $requestClass")
                    result = returnClass
                } else {
                    XLog.t(TAG, "forName $requestClass")
                }
            }
        }
    }
}