package com.devstudio.transactions.viewmodel

import com.devstudio.data.model.TransactionFilterType
import com.devstudio.data.repository.BooksRepository
import com.devstudio.data.repository.TransactionsRepository
import com.devstudio.data.repository.UserDataRepository
import com.devstudio.expensemanager.db.di.DatabaseModule.Companion.DEFAULT_BOOK_NAME
import com.devstudio.expensemanager.db.models.Books
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTransactionBook @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val userDataRepository: UserDataRepository,
    private val booksRepository: BooksRepository,
) {
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
