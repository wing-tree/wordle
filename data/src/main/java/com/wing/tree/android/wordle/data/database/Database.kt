package com.wing.tree.android.wordle.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wing.tree.android.wordle.data.dao.HistoryDao
import com.wing.tree.android.wordle.data.dao.WordDao
import com.wing.tree.android.wordle.data.entity.History
import com.wing.tree.android.wordle.data.entity.Word
import com.wing.tree.android.wordle.data.typeconverter.TypeConverters

@androidx.room.Database(
    entities = [History::class, Word::class],
    exportSchema = false,
    version = 1
)
@androidx.room.TypeConverters(TypeConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun historyDao(): HistoryDao

    companion object {
        private const val DATABASE_FILE_PATH = "Word.db"
        private const val NAME = "Word"
        private const val VERSION = "1.0"

        @Volatile
        private var INSTANCE: Database? = null

        fun getInstance(context: Context): Database {
            synchronized(this) {
                return INSTANCE ?: let {
                    Room.databaseBuilder(
                        context.applicationContext,
                        Database::class.java,
                        "$NAME:$VERSION"
                    )
                        .allowMainThreadQueries()
                        .createFromAsset(DATABASE_FILE_PATH)
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { INSTANCE = it }
                }
            }
        }
    }
}