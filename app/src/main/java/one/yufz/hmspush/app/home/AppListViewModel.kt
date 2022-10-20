package one.yufz.hmspush.app.home

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import one.yufz.hmspush.app.HmsPushClient
import one.yufz.hmspush.app.util.registerPackageChangeFlow
import one.yufz.hmspush.common.model.PushHistoryModel
import one.yufz.hmspush.common.model.PushSignModel

class AppListViewModel(val context: Application) : AndroidViewModel(context) {
    companion object {
        private const val TAG = "AppListViewModel"
    }

    private val filterKeywords = MutableStateFlow<String>("")

    private val supportedAppListFlow: MutableSharedFlow<List<String>> = MutableStateFlow(emptyList())

    private val registeredListFlow = HmsPushClient.getPushSignFlow()

    private val historyListFlow = HmsPushClient.getPushHistoryFlow()
    val appListFlow: Flow<List<AppInfo>> = combine(supportedAppListFlow, registeredListFlow, historyListFlow, ::mergeSource)
        .combine(filterKeywords, ::filterAppList)

    init {
        viewModelScope.launch {
            supportedAppListFlow.emit(loadSupportedAppList())
            context.registerPackageChangeFlow().collect {
                supportedAppListFlow.emit(loadSupportedAppList())
            }
        }
    }

    private suspend fun loadSupportedAppList(): List<String> {
        return withContext(Dispatchers.IO) {
            val intent = Intent("com.huawei.push.msg.NOTIFY_MSG")
            context.packageManager.queryIntentServices(
                intent,
                PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS
                        or PackageManager.MATCH_DISABLED_COMPONENTS
            ).map { it.serviceInfo.packageName }
        }
    }


    private fun filterAppList(list: List<AppInfo>, keywords: String): List<AppInfo> {
        if (keywords.isEmpty()) return list

        return list.filter {
            it.name.contains(keywords, true) || it.packageName.contains(keywords, true)
        }
    }

    private fun mergeSource(appList: List<String>, registered: List<PushSignModel>, history: List<PushHistoryModel>): List<AppInfo> {
        val pm = context.packageManager
        val registeredSet = registered.map { it.packageName }
        val historyMap = history.associateBy { it.packageName }
        return appList.map { packageName ->
            AppInfo(
                packageName = packageName,
                name = pm.getApplicationInfo(packageName, 0).loadLabel(pm).toString(),
                registered = registeredSet.contains(packageName),
                lastPushTime = historyMap[packageName]?.pushTime
            )
        }
            .sortedWith(compareBy({ !it.registered }, { Long.MAX_VALUE - (it.lastPushTime ?: 0L) }))
    }

    fun filter(keywords: String) {
        viewModelScope.launch {
            filterKeywords.emit(keywords)
        }
    }

    fun unregisterPush(packageName: String) {
        HmsPushClient.unregisterPush(packageName)
    }
}