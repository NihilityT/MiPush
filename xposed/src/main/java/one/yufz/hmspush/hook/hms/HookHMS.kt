package one.yufz.hmspush.hook.hms

import de.robv.android.xposed.callbacks.XC_LoadPackage
import one.yufz.xposed.*

class HookHMS {
    companion object {
        private const val TAG = "HookHMS"
    }

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (HookPushNC.canHook(lpparam.classLoader)) {
            HookPushNC.hook(lpparam.classLoader)
        }
    }

}
