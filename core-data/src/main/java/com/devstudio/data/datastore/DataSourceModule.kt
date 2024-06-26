package com.devstudio.data.datastore

import androidx.datastore.core.DataStore
import com.devstudio.core.data.FilterType
import com.devstudio.core.data.Theme_proto
import com.devstudio.core.data.UserPreferences
import com.devstudio.core.data.copy
import com.devstudio.data.model.Theme
import com.devstudio.data.model.Theme.DARK
import com.devstudio.data.model.Theme.LIGHT
import com.devstudio.data.model.Theme.SYSTEM_DEFAULT
import com.devstudio.data.model.TransactionFilterType
import com.devstudio.data.model.TransactionFilterType.ALL
import com.devstudio.data.model.TransactionFilterType.DateRange
import com.devstudio.data.model.UserPreferencesData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataSourceModule @Inject constructor(private val userPreferencesDataStore: DataStore<UserPreferences>) {
    private val DEFAULT_BOOK_ID: Long = 1
    suspend fun updateSelectedBookId(id: Long) {
        userPreferencesDataStore.updateData {
            it.copy { this.selectedBookId = id }
        }
    }

    suspend fun updateTheme(theme: Theme) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.theme = when (theme) {
                    LIGHT -> Theme_proto.LIGHT
                    DARK -> Theme_proto.DARK
                    SYSTEM_DEFAULT -> Theme_proto.SYSTEM_DEFAULT
                }
            }
        }
    }

    fun getSelectedBookId(): Flow<Long> {
        return userPreferencesDataStore.data.map { userData -> userData.selectedBookId.orDefault(DEFAULT_BOOK_ID) }
    }

    suspend fun updateTransactionFilter(filterItem: TransactionFilterType) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.filterType = when (filterItem) {
                    is DateRange -> {
                        this.filterStartDate = filterItem.additionalData.first.toString()
                        this.filterEndDate = filterItem.additionalData.second.toString()
                        FilterType.DATE_RANGE
                    }

                    is ALL -> {
                        FilterType.ALL
                    }

                    else -> FilterType.CURRENT_MONTH
                }
            }
        }
    }

    fun getCurrentTransactionFilter(): Flow<TransactionFilterType> {
        return userPreferencesDataStore.data.distinctUntilChanged().map {
            when (it.filterType) {
                FilterType.ALL -> ALL
                FilterType.DATE_RANGE -> DateRange(
                    Pair(
                        it.filterStartDate.toLong(),
                        it.filterEndDate.toLong(),
                    ),
                )

                else -> TransactionFilterType.CurrentMonth
            }
        }
    }

    val userData: Flow<UserPreferencesData>
        get() {
            return userPreferencesDataStore.data.map { userData ->
                UserPreferencesData(
                    theme = when (userData.theme) {
                        Theme_proto.LIGHT -> LIGHT
                        Theme_proto.DARK -> DARK
                        else -> SYSTEM_DEFAULT
                    },
                    selectedBookId = userData.selectedBookId.orDefault(DEFAULT_BOOK_ID),
                    filterType = when (userData.filterType) {
                        FilterType.ALL -> ALL
                        FilterType.DATE_RANGE -> DateRange(
                            additionalData = Pair(
                                userData.filterStartDate.ifEmpty { "0" }.toLong(),
                                userData.filterEndDate.ifEmpty { "0" }.toLong(),
                            ),
                        )

                        else -> TransactionFilterType.CurrentMonth
                    },
                )
            }
        }
}

fun Long.orDefault(default: Long): Long {
    return if (this == 0L) {
        default
    } else {
        this
    }
}
