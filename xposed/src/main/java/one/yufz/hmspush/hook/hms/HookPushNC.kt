package one.yufz.hmspush.hook.hms

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.service.notification.StatusBarNotification
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError
import one.yufz.hmspush.hook.XLog
import one.yufz.hmspush.hook.hms.nm.SystemNotificationManager
import one.yufz.hmspush.hook.system.HookSystemService
import one.yufz.xposed.findClass
import one.yufz.xposed.hookMethod
import java.lang.reflect.InvocationTargetException

object HookPushNC {
    private const val TAG = "HookPushNC"

    private const val TargetClass = "com.nihility.notification.NotificationManagerEx"

    private val hookCheck = { HookSystemService.isSystemHookReady }

    fun canHook(classLoader: ClassLoader): Boolean {
        return try {
            classLoader.findClass(TargetClass)
            true
        } catch (e: ClassNotFoundError) {
            false
        }
    }

    fun hook(classLoader: ClassLoader) {
        XLog.d(TAG, "hookPushNC() called with: classLoader = $classLoader")

//        FakeHsf.hook(classLoader)

//        PushSignWatcher.watch()

        val notificationController = classLoader.findClass("com.xiaomi.xmsf.push.notification.NotificationController")

        notificationController.hookMethod(
                "isMiPushInstalled"
        ) {
            replace(hookCheck) {
                tryInvoke {
                    return@replace true
                }
            }
        }

        val classNotificationManager = classLoader.findClass(TargetClass)

        //notify(
        //        packageName: String,
        //        tag: String?, id: Int, notification: Notification
        //    )
        classNotificationManager.hookMethod(
            "notify",
            String::class.java,
            String::class.java,
            Int::class.java,
            Notification::class.java
        ) {
            replace(hookCheck) {
                tryInvoke {
                    SystemNotificationManager.notify(
                        args[0] as String,
                        args[1] as String?,
                        args[2] as Int,
                        args[3] as Notification
                    )
                }
            }
        }

        //cancel(
        //        packageName: String,
        //        tag: String?, id: Int
        //    )
        classNotificationManager.hookMethod(
            "cancel",
            String::class.java,
            String::class.java,
            Int::class.java
        ) {
            replace(hookCheck) {
                tryInvoke {
                    SystemNotificationManager.cancel(
                        args[0] as String,
                        args[1] as String?,
                        args[2] as Int
                    )
                }
            }
        }

        //createNotificationChannels(
        //        packageName: String,
        //        channels: List<NotificationChannel?>
        //    )
        classNotificationManager.hookMethod(
            "createNotificationChannels",
            String::class.java,
            List::class.java
        ) {
            replace(hookCheck) {
                tryInvoke {
                    SystemNotificationManager.createNotificationChannels(
                        args[0] as String,
                        args[1] as List<NotificationChannel>
                    )
                }
            }
        }

        //getNotificationChannel(
        //        packageName: String,
        //        channelId: String?
        //    ): NotificationChannel?
        classNotificationManager.hookMethod(
            "getNotificationChannel",
            String::class.java,
            String::class.java
        ) {
            replace() {
                tryInvoke {
                    return@replace SystemNotificationManager.getNotificationChannel(
                        args[0] as String,
                        args[1] as String
                    ) as NotificationChannel?
                }
            }
        }

        //getNotificationChannels(
        //        packageName: String
        //    ): List<NotificationChannel?>?
        classNotificationManager.hookMethod("getNotificationChannels", String::class.java) {
            replace(hookCheck) {
                tryInvoke {
                    return@replace SystemNotificationManager.getNotificationChannels(args[0] as String) as List<NotificationChannel?>?
                }
            }
        }

        //deleteNotificationChannel(
        //        packageName: String,
        //        channelId: String?
        //    )
        classNotificationManager.hookMethod(
            "deleteNotificationChannel",
            String::class.java,
            String::class.java
        ) {
            replace(hookCheck) {
                tryInvoke {
                    SystemNotificationManager.deleteNotificationChannel(
                        args[0] as String,
                        args[1] as String
                    )
                }
            }
        }

        //createNotificationChannelGroups(
        //        packageName: String,
        //        groups: List<NotificationChannelGroup?>
        //    )
        classNotificationManager.hookMethod(
            "createNotificationChannelGroups",
            String::class.java,
            List::class.java
        ) {
            replace(hookCheck) {
                tryInvoke {
                    SystemNotificationManager.createNotificationChannelGroups(
                        args[0] as String,
                        args[1] as List<NotificationChannelGroup>
                    )
                }
            }
        }

        //getNotificationChannelGroup(
        //        packageName: String,
        //        groupId: String?
        //    ): NotificationChannelGroup?
        classNotificationManager.hookMethod(
            "getNotificationChannelGroup",
            String::class.java,
            String::class.java
        ) {
            replace(hookCheck) {
                tryInvoke {
                    return@replace SystemNotificationManager.getNotificationChannelGroup(
                        args[0] as String,
                        args[1] as String
                    ) as NotificationChannelGroup?
                }
            }
        }

        //getNotificationChannelGroups(
        //        packageName: String
        //    ): List<NotificationChannelGroup?>?
        classNotificationManager.hookMethod("getNotificationChannelGroups", String::class.java) {
            replace(hookCheck) {
                tryInvoke {
                    return@replace SystemNotificationManager.getNotificationChannelGroups(args[0] as String) as List<NotificationChannelGroup?>?
                }
            }
        }

        //deleteNotificationChannelGroup(
        //        packageName: String,
        //        groupId: String?
        //    )
        classNotificationManager.hookMethod(
            "deleteNotificationChannelGroup",
            String::class.java,
            String::class.java
        ) {
            replace(hookCheck) {
                tryInvoke {
                    SystemNotificationManager.deleteNotificationChannelGroup(
                        args[0] as String,
                        args[1] as String
                    )
                }
            }
        }

        //areNotificationsEnabled(
        //        packageName: String
        //    ): Boolean
        classNotificationManager.hookMethod("areNotificationsEnabled", String::class.java) {
            replace(hookCheck) {
                tryInvoke {
                    return@replace SystemNotificationManager.areNotificationsEnabled(args[0] as String)
                }
            }
        }

        //getActiveNotifications(
        //        packageName: String
        //    ): Array<StatusBarNotification?>?
        classNotificationManager.hookMethod("getActiveNotifications", String::class.java) {
            replace(hookCheck) {
                tryInvoke {
                    return@replace SystemNotificationManager.getActiveNotifications(args[0] as String) as Array<StatusBarNotification?>?
                }
            }
        }

    }

    private inline fun <R> tryInvoke(invoke: () -> R): R {
        try {
            return invoke()
        } catch (e: XposedHelpers.InvocationTargetError) {
            XLog.e(TAG, "tryInvoke: ", e)
            XLog.e(TAG, "tryInvoke targetException: ", e.cause)
            throw e.cause ?: e
        } catch (e: InvocationTargetException) {
            XLog.e(TAG, "tryInvoke: ", e)
            XLog.e(TAG, "tryInvoke targetException: ", e.targetException)
            throw e.targetException ?: e
        } catch (e: Throwable) {
            XLog.e(TAG, "tryInvoke: ", e)
            XLog.e(TAG, "tryInvoke cause: ", e.cause)
            throw e.cause ?: e
        }
    }
}
