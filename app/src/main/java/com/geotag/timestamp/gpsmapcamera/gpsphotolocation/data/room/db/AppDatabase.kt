package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.Note
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.room.dao.MediaDao
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.room.dao.NoteDao

@Database(entities = [MediaModel::class, Note::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun noteDao(): NoteDao

    companion object {
        private const val DATABASE_NAME = "GPS_database"

        // Define a migration from version 1 to version 2
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Define the SQL statements to perform the migration.
                // For example, you can add or modify tables here.
                // Refer to the Room documentation for details on writing migrations.
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2) // Add your migration here
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}