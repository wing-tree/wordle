package com.wing.tree.android.wordle.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wing.tree.android.wordle.data.dao.WordDao
import com.wing.tree.android.wordle.data.database.migration.MIGRATION_1_2
import com.wing.tree.android.wordle.data.entity.Word

@androidx.room.Database(
    entities = [Word::class],
    exportSchema = false,
    version = 2
)
abstract class Database: RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        private const val DATABASE_FILE_PATH = "Word.db"
        private const val NAME = "Word"
        private const val VERSION = "1.1"

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
                        .createFromAsset(DATABASE_FILE_PATH)
                        .addMigrations(MIGRATION_1_2)
                        .build()
                        .also { INSTANCE = it }
                }
            }
        }
    }
}