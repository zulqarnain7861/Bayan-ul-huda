package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AyahDao
import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.HadithCollectionDao
import com.example.data.local.dao.HadithDao
import com.example.data.local.dao.NoteDao
import com.example.data.local.dao.PrayerScheduleDao
import com.example.data.local.dao.SourceRegistryDao
import com.example.data.local.dao.SurahDao
import com.example.data.local.dao.TranslationDao
import com.example.data.local.entities.AyahEntity
import com.example.data.local.entities.BookmarkEntity
import com.example.data.local.entities.HadithCollectionEntity
import com.example.data.local.entities.HadithEntity
import com.example.data.local.entities.NoteEntity
import com.example.data.local.entities.PrayerScheduleEntity
import com.example.data.local.entities.SurahEntity
import com.example.data.local.entities.TranslationEntity

typealias AppDatabase = BayanDatabase

@Database(
  entities = [
    SurahEntity::class,
    AyahEntity::class,
    HadithCollectionEntity::class,
    HadithEntity::class,
    TranslationEntity::class,
    PrayerScheduleEntity::class,
    BookmarkEntity::class,
    NoteEntity::class,
    SourceRegistry::class
  ],
  version = 5,
  exportSchema = false
)
@TypeConverters(SourceRegistryConverters::class)
abstract class BayanDatabase : RoomDatabase() {
  abstract fun surahDao(): SurahDao
  abstract fun ayahDao(): AyahDao
  abstract fun hadithCollectionDao(): HadithCollectionDao
  abstract fun hadithDao(): HadithDao
  abstract fun translationDao(): TranslationDao
  abstract fun prayerScheduleDao(): PrayerScheduleDao
  abstract fun bookmarkDao(): BookmarkDao
  abstract fun noteDao(): NoteDao
  abstract fun sourceRegistryDao(): SourceRegistryDao

  companion object {
    @Volatile
    private var INSTANCE: BayanDatabase? = null

    private const val CREATE_SOURCE_REGISTRY_SQL =
      "CREATE TABLE IF NOT EXISTS `source_registry` (`sourceId` TEXT NOT NULL, `title` TEXT NOT NULL, `author` TEXT NOT NULL, `tradition` TEXT NOT NULL, `language` TEXT NOT NULL, `translationLanguages` TEXT NOT NULL, `officialUrl` TEXT NOT NULL, `datasetLocation` TEXT NOT NULL, `license` TEXT NOT NULL, `lastSyncTime` INTEGER NOT NULL, `totalAvailableRecords` INTEGER NOT NULL, `totalImportedRecords` INTEGER NOT NULL, `duplicateCount` INTEGER NOT NULL, `invalidCount` INTEGER NOT NULL, `verificationStatus` TEXT NOT NULL, `importStatus` TEXT NOT NULL, PRIMARY KEY(`sourceId`))"

    val MIGRATION_3_5 = object : Migration(3, 5) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `source_registry`")
        db.execSQL(CREATE_SOURCE_REGISTRY_SQL)
      }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `source_registry`")
        db.execSQL(CREATE_SOURCE_REGISTRY_SQL)
      }
    }

    fun getDatabase(context: Context): BayanDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          BayanDatabase::class.java,
          "bayan_al_huda.db"
        )
        .addMigrations(MIGRATION_3_5, MIGRATION_4_5)
        .fallbackToDestructiveMigration(true)
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
