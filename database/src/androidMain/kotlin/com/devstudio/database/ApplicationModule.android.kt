package com.devstudio.database

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devstudio.database.dao.CategoryDao
import com.devstudio.database.models.Books
import com.devstudio.database.models.Category
import com.devstudio.utils.utils.AppConstants.StringConstants.DEFAULT_BOOK_NAME
import com.devstudio.utils.utils.TransactionMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.UUID

actual class Factory(val app: Application) {
    actual fun getRoomInstance(): ExpenseManagerDataBase {
        var INSTANCE: ExpenseManagerDataBase? = null
        val rdc = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                prepopulateDatabase(INSTANCE)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
            }
        }
        return INSTANCE ?: synchronized(this) {
            val dbFile = app.getDatabasePath("expense_manager_database")
            return Room.databaseBuilder<ExpenseManagerDataBase>(app, dbFile.absolutePath)
                .allowMainThreadQueries()
                .addCallback(rdc)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build()
        }
    }

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE TRANSACTIONS_TABLE ADD COLUMN categoryId TEXT not null default ''")
            database.execSQL("CREATE TABLE CATEGORY_TABLE (id TEXT NOT NULL,\n" + "name TEXT not null,\n" + "status integer NOT NULL DEFAULT null,\n" + "timeStamp INTEGER NOT NULL default null,categoryType TEXT NOT NULL DEFAULT NULL, PRIMARY KEY (id))")
            TransactionMode.EXPENSE.categoryList.forEachIndexed { _, it ->
                database.execSQL("INSERT INTO CATEGORY_TABLE (id,name,timestamp,status,CATEGORYTYPE) VALUES ('${UUID.randomUUID()}','$it',${System.currentTimeMillis()},1,'EXPENSE')")
            }
            TransactionMode.INCOME.categoryList.forEachIndexed { _, it ->
                database.execSQL("INSERT INTO CATEGORY_TABLE (id,name,timestamp,status,CATEGORYTYPE) VALUES ('${UUID.randomUUID()}','$it',${System.currentTimeMillis()},1,'INCOME')")
            }
            database.execSQL("UPDATE TRANSACTIONS_TABLE   SET CATEGORYID= coalesce((SELECT id FROM CATEGORY_TABLE WHERE NAME like CATEGORY),CATEGORY)")
            database.execSQL("create table transactions_table_backup (id INTEGER NOT NULL default 0,\n" + "note TEXT not null default '',\n" + "amount REAL NOT NULL default 0.0 ,\n" + "categoryId TEXT not null default '',\n" + "isEditingOldTransaction TEXT not null default '',\n" + "transactionDate TEXT not null default '',\n" + "PRIMARY KEY (id))")
            database.execSQL(
                "INSERT INTO TRANSACTIONS_TABLE_Backup SELECT id,\n" + "note,\n" + "amount,\n" + "categoryId,\n" + "isEditingOldTransaction,\n" + "transactionDate FROM TRANSACTIONS_TABLE",
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
            database.execSQL("CREATE TABLE BOOKS_TABLE (id INTEGER NOT NULL default 0,name TEXT NOT NULL,timeStamp INTEGER NOT NULL DEFAULT 0,PRIMARY KEY (id))")
            database.execSQL("INSERT INTO BOOKS_TABLE (ID,NAME,TIMESTAMP) VALUES (1,'$DEFAULT_BOOK_NAME',${System.currentTimeMillis()})")
            database.execSQL("ALTER TABLE TRANSACTIONS_TABLE ADD COLUMN bookId INTEGER not null default 0 CONSTRAINT bookId REFERENCES books_table(id) on delete cascade ")
            database.execSQL("ALTER TABLE CATEGORY_TABLE ADD COLUMN bookId INTEGER not null default 0")
            database.execSQL("UPDATE TRANSACTIONS_TABLE SET bookId=1")
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            TransactionMode.INVESTMENT.categoryList.forEachIndexed { _, it ->
                database.execSQL("INSERT INTO CATEGORY_TABLE (id,name,timestamp,status,CATEGORYTYPE) VALUES ('${UUID.randomUUID()}','$it',${System.currentTimeMillis()},1,'INVESTMENT')")
            }
        }
    }

    private fun prepopulateDatabase(expenseManagerDataBase: ExpenseManagerDataBase?) {
        CoroutineScope(Dispatchers.IO).launch {
            expenseManagerDataBase?.booksDao()?.insertBook(
                Books(
                    name = DEFAULT_BOOK_NAME,
                    timeStamp = Calendar.getInstance().timeInMillis,
                ),
            )
            expenseManagerDataBase?.categoryDao()?.let { categoryDao ->
                insertCategories(
                    categoryDao,
                    expenseManagerDataBase,
                    TransactionMode.INCOME,
                )
                insertCategories(
                    categoryDao,
                    expenseManagerDataBase,
                    TransactionMode.EXPENSE,
                )
                insertCategories(
                    categoryDao,
                    expenseManagerDataBase,
                    TransactionMode.INVESTMENT,
                )
            }
        }
    }

    private fun insertCategories(
        categoryDao: CategoryDao,
        expenseManagerDataBase: ExpenseManagerDataBase,
        income: TransactionMode,
    ) {
        categoryDao.insertCategories(
            income.categoryList.map {
                Category(
                    name = it,
                    categoryType = income.name,
                    bookId = expenseManagerDataBase.booksDao().getBooks()[0].id,
                )
            },
        )
    }
}

actual object AppContext {

    private var value: WeakReference<Context?>? = null

    fun set(context: Context) {
        value = WeakReference(context)
    }

    fun get(): Context? {
        return value?.get()
    }

}