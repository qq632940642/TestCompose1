package com.example.testcompose1

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.testcompose1.work.CleanupWorker
import java.util.concurrent.TimeUnit

class TodoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleCleanupWork()
    }

    private fun scheduleCleanupWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) // 设备电量不低时才执行
            .build()

        // 周期性任务：每天执行一次 CleanupWorker 清理任务
        val workRequest = PeriodicWorkRequestBuilder<CleanupWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints) // 绑定执行条件
            .build()

        // 提交任务到 WorkManager
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "cleanup_work", // 任务唯一标识名
            ExistingPeriodicWorkPolicy.KEEP, // 已有任务则保留，不重复创建
            workRequest
        )

        // 单次任务：10秒后立即执行 CleanupWorker 清理任务
        val oneTimeRequest = OneTimeWorkRequestBuilder<CleanupWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS) // 10秒后执行
//            .setInputData(Data.Builder().putString("key", "value").build()) // 任务可以传参
            .build()
        WorkManager.getInstance(this).enqueue(oneTimeRequest)
    }
}