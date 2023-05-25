package com.devstudio.expensemanager.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.devstudio.core_data.repository.TransactionDataBackupWorker
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_WORK_NAME
import com.devstudio.utils.utils.AppConstants.StringConstants.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val application: Application) :
    ViewModel() {
    val workManager: WorkManager = WorkManager.getInstance(application)
    val outputWorkInformation: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(TAG)

    fun exportTransactions() {
        workManager
            .beginUniqueWork(
                BACK_UP_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(TransactionDataBackupWorker::class.java)
            ).enqueue()
    }
}
