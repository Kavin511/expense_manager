package com.devstudio.expensemanager.db.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devstudio.expensemanager.db.ExpenseManagerDataBase
import com.devstudio.expensemanager.db.dao.CategoryDao
import com.devstudio.expensemanager.db.dao.TransactionDao
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
    fun providesExpenseManagerDatabase(@ApplicationContext applicationContext: Context): ExpenseManagerDataBase {
        val rdc = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                CoroutineScope(SupervisorJob()).launch {
                    providesExpenseManagerDatabase(applicationContext).categoryDao()
                        .insertCategories(TransactionMode.EXPENSE.categoryList.map {
                            Category(
                                name = it,
                                categoryType = TransactionMode.EXPENSE.name
                            )
                        })
                    providesExpenseManagerDatabase(applicationContext).categoryDao()
                        .insertCategories(TransactionMode.INCOME.categoryList.map {
                            Category(
                                name = it,
                                categoryType = TransactionMode.INCOME.name
                            )
                        })
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
            }
        }
        return Room.databaseBuilder(
            applicationContext,
            ExpenseManagerDataBase::class.java,
            "expense_manager_database"
        ).allowMainThreadQueries().addCallback(rdc).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
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

val MIGRATION_2_3 = object : Migration(2,3){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE TRANSACTIONS_TABLE ADD COLUMN paymentStatus TEXT not null default 'COMPLETED'")
    }

}