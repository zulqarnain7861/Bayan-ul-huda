package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.TranslationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
  @Query("SELECT * FROM translations WHERE sourceType = :sourceType AND referenceKey = :referenceKey ORDER BY editionKey ASC")
  fun getTranslationsForReference(sourceType: String, referenceKey: String): Flow<List<TranslationEntity>>

  @Query("SELECT * FROM translations WHERE sourceType = :sourceType AND referenceKey = :referenceKey AND editionKey = :editionKey LIMIT 1")
  suspend fun getTranslation(sourceType: String, referenceKey: String, editionKey: String): TranslationEntity?

  @Query("""
    SELECT * FROM translations
    WHERE text LIKE '%' || :query || '%'
       OR referenceKey = :query
    LIMIT 100
  """)
  fun searchTranslations(query: String): Flow<List<TranslationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTranslations(translations: List<TranslationEntity>)

  @Query("SELECT COUNT(*) FROM translations")
  suspend fun getTranslationCount(): Int

  @Query("SELECT COUNT(*) FROM translations WHERE sourceType = :sourceType AND languageCode = :languageCode")
  suspend fun getTranslationCountByLanguage(sourceType: String, languageCode: String): Int
}
