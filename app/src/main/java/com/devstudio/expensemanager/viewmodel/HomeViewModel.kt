package com.devstudio.expensemanager.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.devstudio.core_data.repository.TransactionDataBackupWorker
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_WORK_NAME
import com.devstudio.utils.utils.AppConstants.StringConstants.WORK_TRIGGERING_MODE_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val application: Application) :
    ViewModel() {
    private val workManager: WorkManager = WorkManager.getInstance(application)

    fun exportTransactions(isManuallyTriggered: Boolean) {
        val transactionDataBackupWorker =
            OneTimeWorkRequest.Builder(TransactionDataBackupWorker::class.java)
                .addTag(BACK_UP_WORK_NAME)
                .setInputData(
                    Data.Builder().putBoolean(WORK_TRIGGERING_MODE_KEY, isManuallyTriggered).build()
                )
                .build()
        val transactionBackupWork = workManager
            .beginUniqueWork(
                BACK_UP_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                transactionDataBackupWorker
            )
        transactionBackupWork.enqueue()
        viewModelScope.launch {
            transactionBackupWork.workInfosLiveData.observeForever { result->
                if (result.isNullOrEmpty().not() && result[0].state.isFinished) {
                    Toast.makeText(
                        application.applicationContext,
                        result[0].outputData.getString("is_success"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return
    }
}
