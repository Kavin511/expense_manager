package com.devstudio.transactions.viewmodel

import com.devstudio.data.model.TransactionFilterType
import com.devstudio.data.repository.BooksRepository
import com.devstudio.data.repository.TransactionsRepository
import com.devstudio.data.repository.UserDataRepository
import com.devstudio.database.models.Books
import com.devstudio.utils.utils.AppConstants.StringConstants.DEFAULT_BOOK_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

class GetTransactionBook : KoinComponent {
    private val transactionsRepository: TransactionsRepository by inject()
    private val userDataRepository: UserDataRepository by inject()
    private val booksRepository: BooksRepository by inject()

    operator fun invoke(): Flow<TransactionBook> {
        val userData = userDataRepository.userData
        return userData.distinctUntilChanged().map {
            val selectedBookId = it.selectedBookId
            val transactionFilterType = it.filterType
            val transactions = when (transactionFilterType) {
                is TransactionFilterType.ALL -> {
                    transactionsRepository.allTransactionsStream(selectedBookId)
                        .distinctUntilChanged()
                }

                is TransactionFilterType.DateRange -> {
                    transactionsRepository.filterTransactionFromDateRange(
                        transactionFilterType.additionalData,
                        selectedBookId,
                    )
                }

                else -> {
                    transactionsRepository.getTransactionsForCurrentMonth(selectedBookId)
                }
            }
            val book =
                booksRepository.getBookById(selectedBookId) ?: Books(name = DEFAULT_BOOK_NAME)
            TransactionBook(
                transactions = transactions,
                bookId = book.id,
                bookName = book.name,
                filterType = transactionFilterType,
            )
        }
    }
}
