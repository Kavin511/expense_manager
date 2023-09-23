package com.devstudio.expensemanager.db.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devstudio.expensemanager.db.ExpenseManagerDataBase
import com.devstudio.expensemanager.db.dao.BooksDao
import com.devstudio.expensemanager.db.dao.CategoryDao
import com.devstudio.expensemanager.db.dao.TransactionDao
import com.devstudio.expensemanager.db.di.DatabaseModule.Companion.DEFAULT_BOOK_NAME
import com.devstudio.expensemanager.db.models.Books
import com.devstudio.expensemanager.db.models.Category
import com.devstudio.expensemanager.db.models.TransactionMode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun providesTransactionDao(expenseManagerDataBase: ExpenseManagerDataBase): TransactionDao {
        return expenseManagerDataBase.transactionsDao()
    }

    @Provides
    fun providesCategoryDao(expenseManagerDataBase: ExpenseManagerDataBase): CategoryDao {
        return expenseManagerDataBase.categoryDao()
    }

    @Provides
    fun providesBooksDao(expenseManagerDataBase: ExpenseManagerDataBase): BooksDao {
        return expenseManagerDataBase.booksDao()
    }

    @Provides
    fun providesExpenseManagerDatabase(@ApplicationContext applicationContext: Context): ExpenseManagerDataBase {
        var INSTANCE: ExpenseManagerDataBase? = null
        val rdc = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                prepopulateDatabase(INSTANCE)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
            }
        }
        return INSTANCE ?: synchronized(this@DatabaseModule) {
            Room.databaseBuilder(
                applicationContext, ExpenseManagerDataBase::class.java, "expense_manager_database"
            ).allowMainThreadQueries().addCallback(rdc)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
                .also { INSTANCE = it }
        }
    }

    private fun prepopulateDatabase(expenseManagerDataBase: ExpenseManagerDataBase?) {
        CoroutineScope(SupervisorJob()).launch {
            expenseManagerDataBase?.categoryDao()?.let { categoryDao ->
                categoryDao.insertCategories(TransactionMode.EXPENSE.categoryList.map {
                    Category(
                        name = it, categoryType = TransactionMode.EXPENSE.name
                    )
                })
                categoryDao.insertCategories(TransactionMode.INCOME.categoryList.map {
                    Category(
                        name = it, categoryType = TransactionMode.INCOME.name
                    )
                })
            }
            expenseManagerDataBase?.booksDao()?.insertBooks(
                Books(
                    name = DEFAULT_BOOK_NAME,
                    timeStamp = Calendar.getInstance().timeInMillis
                )
            )
        }
    }

    companion object {
        const val DEFAULT_BOOK_NAME = "Daily Book"
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE TRANSACTIONS_TABLE ADD COLUMN categoryId TEXT not null default ''")
        database.execSQL("CREATE TABLE CATEGORY_TABLE (id TEXT NOT NULL,\n" + "name TEXT not null,\n" + "status integer NOT NULL DEFAULT null,\n" + "timeStamp INTEGER NOT NULL default null,categoryType TEXT NOT NULL DEFAULT NULL, PRIMARY KEY (id))")
        TransactionMode.EXPENSE.categoryList.forEachIndexed { _, it ->
            database.execSQL("INSERT INTO CATEGORY_TABLE (id,name,timestamp,status,CATEGORYTYPE) VALUES ('${UUID.randomUUID()}','${it}',${System.currentTimeMillis()},1,'EXPENSE')")
        }
        TransactionMode.INCOME.categoryList.forEachIndexed { _, it ->
            database.execSQL("INSERT INTO CATEGORY_TABLE (id,name,timestamp,status,CATEGORYTYPE) VALUES ('${UUID.randomUUID()}','${it}',${System.currentTimeMillis()},1,'INCOME')")
        }
        database.execSQL("UPDATE TRANSACTIONS_TABLE   SET CATEGORYID= coalesce((SELECT id FROM CATEGORY_TABLE WHERE NAME like CATEGORY),CATEGORY)")
        database.execSQL("create table transactions_table_backup (id INTEGER NOT NULL default 0,\n" + "note TEXT not null default '',\n" + "amount REAL NOT NULL default 0.0 ,\n" + "categoryId TEXT not null default '',\n" + "isEditingOldTransaction TEXT not null default '',\n" + "transactionDate TEXT not null default '',\n" + "PRIMARY KEY (id))")
        database.execSQL(
            "INSERT INTO TRANSACTIONS_TABLE_Backup SELECT id,\n" +
                    "note,\n" +
                    "amount,\n" +
                    "categoryId,\n" +
                    "isEditingOldTransaction,\n" +
                    "transactionDate FROM TRANSACTIONS_TABLE"
        )
        database.execSQL("DROP TABLE TRANSACTIONS_TABLE")
        database.execSQL("ALTER TABLE TRANSACTIONS_TABLE_Backup RENAME to TRANSACTIONS_TABLE")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE TRANSACTIONS_TABLE ADD COLUMN paymentStatus TEXT not null default 'COMPLETED'")
    }

}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE BOOKS_TABLE (ID INTEGER NOT NULL,NAME TEXT NOT NULL,TIMESTAMP INTEGER NOT NULL DEFAULT 0,PRIMARY KEY (ID))")
        database.execSQL("INSERT INTO BOOKS_TABLE (NAME,TIMESTAMP) VALUES (1,'$DEFAULT_BOOK_NAME',${System.currentTimeMillis()})")
        database.execSQL("ALTER TABLE TRANSACTIONS_TABLE ADD COLUMN bookId  INTEGER  not null default null CONSTRAINT bookId REFERENCES BOOKS_TABLE(ID) on delete cascade ")
        database.execSQL("UPDATE TRANSACTIONS_TABLE SET bookId=1")
    }
}