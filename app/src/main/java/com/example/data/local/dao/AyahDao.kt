package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.AyahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AyahDao {
  @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY verseNumber ASC")
  fun getAyahsBySurah(surahNumber: Int): Flow<List<AyahEntity>>

  @Query("SELECT * FROM ayahs WHERE juzNumber = :juzNumber ORDER BY surahNumber ASC, verseNumber ASC")
  fun getAyahsByJuz(juzNumber: Int): Flow<List<AyahEntity>>

  @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber LIMIT 1")
  suspend fun getAyah(surahNumber: Int, verseNumber: Int): AyahEntity?

  @Query("""
    SELECT * FROM ayahs 
    WHERE (CAST(surahNumber AS TEXT) || ':' || CAST(verseNumber AS TEXT)) = :query
       OR CAST(verseNumber AS TEXT) = :query
       OR textArabic LIKE '%' || :query || '%' 
       OR translationEnglish LIKE '%' || :query || '%' 
       OR translationUrdu LIKE '%' || :query || '%'
       OR transliteration LIKE '%' || :query || '%'
    ORDER BY surahNumber ASC, verseNumber ASC
    LIMIT 150
  """)
  fun searchAyahs(query: String): Flow<List<AyahEntity>>

  @Query("SELECT * FROM ayahs ORDER BY RANDOM() LIMIT 1")
  suspend fun getRandomAyah(): AyahEntity?

  @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY verseNumber ASC")
  suspend fun getAyahsBySurahSnapshot(surahNumber: Int): List<AyahEntity>

  @Query("""
    SELECT * FROM ayahs 
    WHERE (CAST(surahNumber AS TEXT) || ':' || CAST(verseNumber AS TEXT)) = :query
       OR CAST(verseNumber AS TEXT) = :query
       OR textArabic LIKE '%' || :query || '%' 
       OR translationEnglish LIKE '%' || :query || '%' 
       OR translationUrdu LIKE '%' || :query || '%'
       OR transliteration LIKE '%' || :query || '%'
    ORDER BY surahNumber ASC, verseNumber ASC
    LIMIT 150
  """)
  suspend fun searchAyahsSnapshot(query: String): List<AyahEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAyahs(ayahs: List<AyahEntity>)

  @Query("SELECT COUNT(*) FROM ayahs")
  suspend fun getAyahCount(): Int
}
