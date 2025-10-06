package one.yufz.hmspush.hook.systemui

interface ISystemUIPluginHooker {
    fun hook(pluginLoader: ClassLoader)
}