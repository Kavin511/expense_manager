package com.devstudio.expensemanager.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.devstudio.core_data.repository.BooksRepository
import com.devstudio.core_data.repository.TransactionDataBackupWorker
import com.devstudio.core_data.repository.UserDataRepository
import com.devstudio.core_model.models.BackupStatus
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_STATUS_KEY
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_STATUS_MESSAGE
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_WORK_NAME
import com.devstudio.utils.utils.AppConstants.StringConstants.WORK_TRIGGERING_MODE_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application,
    val booksRepository: BooksRepository,
    val userDataRepository: UserDataRepository
) : ViewModel() {
    private val workManager: WorkManager = WorkManager.getInstance(application)
    val homeScreenUiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    init {
        getSelectedBook()
    }

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

    fun getSelectedBook() {
        viewModelScope.launch {
            userDataRepository.getSelectedBookId().collectLatest {
                homeScreenUiState.value = HomeUiState.Success(
                    HomeUiData(
                        selectedBookId = booksRepository.getBookById(it)
                    )
                )
            }
        }
    }


    companion object {
        const val CSV_INTENT_TYPE = "text/*"
        const val SHARE = "Share"
    }
}

