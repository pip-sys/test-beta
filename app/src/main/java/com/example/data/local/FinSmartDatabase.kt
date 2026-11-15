package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FinSmartDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: FinSmartDatabase? = null

        fun getInstance(context: Context): FinSmartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinSmartDatabase::class.java,
                    "finsmart_edu_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
