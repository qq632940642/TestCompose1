package com.example.testcompose1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcompose1.data.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // 把 DataStore 流（Flow）转换成 ViewModel 里的状态（State） .
    // stateIn：把冷流 Flow 转成热流 State
    val isDarkTheme = preferencesManager.darkModeFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000), // UI 离开页面 5 秒后自动停止订阅，节省资源
        false
    )

    fun toggleDarkMode() { // 切换主题
        viewModelScope.launch {
            preferencesManager.setDarkMode(!(isDarkTheme.value ?: false))
        }
    }
}