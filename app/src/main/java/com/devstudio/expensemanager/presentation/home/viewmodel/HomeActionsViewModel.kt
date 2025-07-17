package com.devstudio.expensemanager.presentation.home.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.devstudio.data.model.BackupStatus
import com.devstudio.data.model.BackupStatus.Companion.failure
import com.devstudio.data.model.BackupStatus.Companion.success
import com.devstudio.data.repository.TransactionDataBackupWorker
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_STATUS_KEY
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_STATUS_MESSAGE
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_WORK_NAME
import com.devstudio.utils.utils.AppConstants.StringConstants.WORK_TRIGGERING_MODE_KEY
import kotlinx.coroutines.launch

class HomeActionsViewModel(application: Application) : ViewModel() {
    private val workManager: WorkManager = WorkManager.getInstance(application)

    fun exportTransactions(isManuallyTriggered: Boolean, resultEvent: (BackupStatus) -> Unit) {
        val transactionDataBackupWorker =
            OneTimeWorkRequest.Builder(TransactionDataBackupWorker::class.java)
                .addTag(BACK_UP_WORK_NAME).setInputData(
                    Data.Builder().putBoolean(WORK_TRIGGERING_MODE_KEY, isManuallyTriggered).build(),
                ).build()
        val transactionBackupWork = workManager.beginUniqueWork(
            BACK_UP_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            transactionDataBackupWorker,
        )
        transactionBackupWork.enqueue()
        viewModelScope.launch {
            transactionBackupWork.workInfosLiveData.observeForever { result ->
                if (result.isNullOrEmpty().not() && result[0].state.isFinished) {
                    if (result[0].outputData.getBoolean(BACK_UP_STATUS_KEY, false)) {
                        resultEvent.invoke(
                            success(
                                result[0].outputData.getString(
                                    BACK_UP_STATUS_MESSAGE,
                                ) ?: "",
                            ),
                        )
                    } else {
                        resultEvent.invoke(
                            failure(
                                result[0].outputData.getString(
                                    BACK_UP_STATUS_MESSAGE,
                                ) ?: "",
                            ),
                        )
                    }
                }
            }
        }
        return
    }

    companion object {
        const val CSV_INTENT_TYPE = "text/*"
        const val OPEN = "Open"
    }
}
