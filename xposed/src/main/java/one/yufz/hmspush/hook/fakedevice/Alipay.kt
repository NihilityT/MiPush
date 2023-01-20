package one.yufz.hmspush.hook.fakedevice

import de.robv.android.xposed.callbacks.XC_LoadPackage
import one.yufz.xposed.findClass
import one.yufz.xposed.hook

class Alipay : IFakeDevice {
    override fun fake(lpparam: XC_LoadPackage.LoadPackageParam): Boolean {
        lpparam.classLoader.findClass("com.alipay.pushsdk.thirdparty.xiaomi.XiaoMIPushWorker")
            .declaredMethods
            .find { it.returnType == Boolean::class.java }
            ?.hook { replace { true } }

        return true
    }
}
