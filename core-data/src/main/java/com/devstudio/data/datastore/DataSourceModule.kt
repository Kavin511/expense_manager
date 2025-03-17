package com.devstudio.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.devstudio.data.model.Theme
import com.devstudio.data.model.Theme.DARK
import com.devstudio.data.model.Theme.LIGHT
import com.devstudio.data.model.Theme.SYSTEM_DEFAULT
import com.devstudio.data.model.TransactionFilterType
import com.devstudio.data.model.TransactionFilterType.ALL
import com.devstudio.data.model.TransactionFilterType.DateRange
import com.devstudio.data.model.UserPreferencesData
import com.devstudio.data.repository.BooksRepositoryImpl
import com.devstudio.database.models.Books
import com.devstudio.utils.utils.AppConstants.StringConstants.DEFAULT_BOOK_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.userPreferencesDataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)
const val SELECTED_BOOK_ID = "selectedBookId"
const val THEME = "theme"
const val FILTER_TYPE = "filterType"
const val FILTER_START_DATE = "filterStartDate"
const val FILTER_END_DATE = "filterEndDate"

val selectedBookId = intPreferencesKey(SELECTED_BOOK_ID)
val themePreference = stringPreferencesKey(THEME)
val filterType = stringPreferencesKey(FILTER_TYPE)
val filterStartDate = stringPreferencesKey(FILTER_START_DATE)
val filterEndDate = stringPreferencesKey(FILTER_END_DATE)

class DataSourceModule : KoinComponent {
    private val DEFAULT_BOOK_ID: Long = 1
    private val context: Context by inject()

    suspend fun updateSelectedBookId(id: Long) {
        context.userPreferencesDataStore.edit {
            it[selectedBookId] = id.toInt()
        }
    }

    suspend fun updateTheme(theme: Theme) {
        context.userPreferencesDataStore.edit {
            it[themePreference] = when (theme) {
                LIGHT -> ThemeProto.LIGHT.name
                DARK -> ThemeProto.DARK.name
                SYSTEM_DEFAULT -> ThemeProto.SYSTEM_DEFAULT.name
            }
        }
    }

    fun getSelectedBookId(): Flow<Long> {
        insertDefaultBookIfNotPresent()
        return context.userPreferencesDataStore.data.map { userData ->
            userData[selectedBookId]?.toLong() ?: DEFAULT_BOOK_ID
        }
    }

    private fun insertDefaultBookIfNotPresent() {
        val booksRepository: BooksRepositoryImpl by inject()
        if (booksRepository.getBooks().isEmpty()) {
            booksRepository.insertBook(Books(DEFAULT_BOOK_ID, DEFAULT_BOOK_NAME))
        }
    }

    suspend fun updateTransactionFilter(filterItem: TransactionFilterType) {
        context.userPreferencesDataStore.edit {
            var updatedFilterStartDate: String = it[filterStartDate] ?: ""
            var updatedFilterEndDate: String = it[filterEndDate] ?: ""
            val updatedFilterType = when (filterItem) {
                is DateRange -> {
                    updatedFilterStartDate = filterItem.additionalData.first.toString()
                    updatedFilterEndDate = filterItem.additionalData.second.toString()
                    FilterType.DATE_RANGE
                }

                is ALL -> {
                    FilterType.ALL
                }

                else -> FilterType.CURRENT_MONTH
            }.name

            it[filterType] = updatedFilterType
            it[filterStartDate] = updatedFilterStartDate
            it[filterEndDate] = updatedFilterEndDate

        }
    }

    fun getCurrentTransactionFilter(): Flow<TransactionFilterType> {
        return context.userPreferencesDataStore.data.distinctUntilChanged().map {
            when (it[filterType]) {
                FilterType.ALL.name -> ALL
                FilterType.DATE_RANGE.name -> DateRange(
                    Pair(
                        it[filterStartDate]?.toLong() ?: 0,
                        it[filterEndDate]?.toLong() ?: 0,
                    ),
                )

                else -> TransactionFilterType.CurrentMonth
            }
        }
    }

    fun userData(): Flow<UserPreferencesData> =
        context.userPreferencesDataStore.data.map { userData ->
            val currentSelectedBookId = userData[selectedBookId] ?: DEFAULT_BOOK_ID
            UserPreferencesData(
                theme = when (userData[themePreference]) {
                    LIGHT.name -> LIGHT
                    DARK.name -> DARK
                    else -> SYSTEM_DEFAULT
                },
                selectedBookId = currentSelectedBookId.toLong(),
                filterType = when (userData[filterType]) {
                    FilterType.ALL.name -> ALL
                    FilterType.DATE_RANGE.name -> DateRange(
                        additionalData = Pair(
                            userData[filterStartDate]?.ifEmpty { "0" }?.toLong() ?: 0,
                            userData[filterEndDate]?.ifEmpty { "0" }?.toLong() ?: 0,
                        ),
                    )

                    else -> TransactionFilterType.CurrentMonth
                },
            )
        }
}

fun Long.orDefault(default: Long): Long {
    return if (this == 0L) {
        default
    } else {
        this
    }
}