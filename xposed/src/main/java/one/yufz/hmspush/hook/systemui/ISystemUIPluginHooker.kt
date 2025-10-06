package one.yufz.hmspush.hook.systemui

fun interface ISystemUIPluginHooker {
    fun hook(pluginLoader: ClassLoader)
}