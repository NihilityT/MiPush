package one.yufz.hmspush.hook

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import one.yufz.hmspush.xposed.BuildConfig
import java.lang.reflect.Method

object XLog {
    fun t(tag: String, message: String?) {
        if (BuildConfig.DEBUG) {
            XposedBridge.log("[MiPush][T][$tag] $message")
        }
    }
    fun d(tag: String, message: String?) {
        XposedBridge.log("[MiPush][D][$tag] $message")
    }

    fun i(tag: String, message: String?) {
        XposedBridge.log("[MiPush][I][$tag] $message")
    }

    fun e(tag: String, message: String?, throwable: Throwable?) {
        XposedBridge.log("[MiPush][E][$tag] $message")
        XposedBridge.log(throwable)
    }

    fun XC_MethodHook.MethodHookParam.logMethod(tag: String, stackTrace: Boolean = false) {
        d(tag, "╔═══════════════════════════════════════════════════════")
        d(tag, method.toString())
        d(tag, "${method.name} called with ${args.contentDeepToString()}")
        if (stackTrace) {
            d(tag, Log.getStackTraceString(Throwable()))
        }
        if (hasThrowable()) {
            e(tag, "${method.name} thrown", throwable)
        } else if (method is Method && (method as Method).returnType != Void.TYPE) {
            d(tag, "${method.name} return $result")
        }
        d(tag, "╚═══════════════════════════════════════════════════════")
    }
}