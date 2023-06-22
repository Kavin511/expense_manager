package com.devstudio.expensemanager.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.widget.Toast
import androidx.compose.material3.Snackbar
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.devstudio.core_data.repository.TransactionDataBackupWorker
import com.devstudio.core_model.models.BackupStatus
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_STATUS_KEY
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_STATUS_MESSAGE
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_WORK_NAME
import com.devstudio.utils.utils.AppConstants.StringConstants.WORK_TRIGGERING_MODE_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val application: Application) : ViewModel() {
    private val workManager: WorkManager = WorkManager.getInstance(application)
    val transactionBackUpStatus = MutableStateFlow<BackupStatus?>(null)

    fun exportTransactions(isManuallyTriggered: Boolean, resultEvent: (BackupStatus) -> Unit) {
        val transactionDataBackupWorker =
            OneTimeWorkRequest.Builder(TransactionDataBackupWorker::class.java)
                .addTag(BACK_UP_WORK_NAME).setInputData(
                    Data.Builder().putBoolean(WORK_TRIGGERING_MODE_KEY, isManuallyTriggered).build()
                ).build()
        val transactionBackupWork = workManager.beginUniqueWork(
            BACK_UP_WORK_NAME, ExistingWorkPolicy.REPLACE, transactionDataBackupWorker
        )
        transactionBackupWork.enqueue()
        viewModelScope.launch {
            transactionBackupWork.workInfosLiveData.observeForever { result ->
                if (result.isNullOrEmpty().not() && result[0].state.isFinished) {
                    if (result[0].outputData.getBoolean(BACK_UP_STATUS_KEY, false)) {
                        resultEvent.invoke(
                            BackupStatus.success(
                                result[0].outputData.getString(
                                    BACK_UP_STATUS_MESSAGE
                                ) ?: ""
                            )
                        )
                    } else {
                        resultEvent.invoke(
                            BackupStatus.failure(
                                result[0].outputData.getString(
                                    BACK_UP_STATUS_MESSAGE
                                ) ?: ""
                            )
                        )
                    }
                }
            }
        }
        return
    }


    companion object {
        const val CSV_INTENT_TYPE = "text/csv"
        const val SHARE = "Share"
    }
}
