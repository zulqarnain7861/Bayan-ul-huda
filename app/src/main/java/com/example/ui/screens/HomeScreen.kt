package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AyahEntity
import com.example.data.local.entities.HadithEntity
import com.example.data.local.entities.PrayerScheduleEntity
import com.example.data.local.entities.SurahEntity
import com.example.data.model.ReflectionCategory
import com.example.data.model.ReflectionDataProvider
import com.example.data.prayer.PrayerAndAlarmConfig
import com.example.ui.components.BismillahHeader
import com.example.ui.components.SahihGradeBadge
import com.example.ui.components.SalahAndFajrAlarmCard
import com.example.ui.prayer.FajrAlarmPlaybackState
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ScheherazadeFontFamily

@Composable
fun HomeScreen(
  dailyAyah: AyahEntity?,
  dailyHadith: HadithEntity?,
  surahs: List<SurahEntity>,
  prayerSchedule: PrayerScheduleEntity? = null,
  prayerConfig: PrayerAndAlarmConfig = PrayerAndAlarmConfig(),
  alarmPlaybackState: FajrAlarmPlaybackState = FajrAlarmPlaybackState(),
  onUpdatePrayerConfig: (PrayerAndAlarmConfig) -> Unit = {},
  onRefreshPrayerSchedule: () -> Unit = {},
  onTestAzanOrFajrAlarm: (String, Boolean) -> Unit = { _, _ -> },
  onStopActiveAlarm: () -> Unit = {},
  onSnoozeActiveAlarm: (Int) -> Unit = {},
  onOpenSurah: (SurahEntity) -> Unit,
  onPlayRecitation: (AyahEntity, String) -> Unit,
  onBookmarkAyah: (AyahEntity, String) -> Unit,
  onBookmarkHadith: (HadithEntity) -> Unit,
  onNavigateToQuran: () -> Unit,
  onNavigateToHadith: () -> Unit,
  onNavigateToLibrary: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var reflectionCategory by remember { mutableStateOf(ReflectionCategory.PROPHETIC_GUIDANCE) }
  var reflectionIndex by remember { mutableIntStateOf(0) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 12.dp)
  ) {
    // Sacred Bismillah Greeting Header
    item {
      BismillahHeader()
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Five Daily Salah Times, Azan & Special 15-Minute Fajr Wake-Up Alarm
    item {
      SalahAndFajrAlarmCard(
        schedule = prayerSchedule,
        config = prayerConfig,
        alarmPlaybackState = alarmPlaybackState,
        onUpdateConfig = onUpdatePrayerConfig,
        onRefreshSchedule = onRefreshPrayerSchedule,
        onTestAzanOrFajrAlarm = onTestAzanOrFajrAlarm,
        onStopActiveAlarm = onStopActiveAlarm,
        onSnoozeActiveAlarm = onSnoozeActiveAlarm
      )
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Complete Offline Corpus Summary Card (114 Surahs, 8 Tafsirs, 7 Primary Hadith Books, 32 Islamic Books)
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = EmeraldPrimary.copy(alpha = 0.1f)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "100% Offline Quran (114 Surahs • 6,236 Verses)",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
              )
            )
            Text(
              text = "8 Authentic Tafsirs • 7 Primary Hadith Books • 32 Classical Islamic Books",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
          FilledTonalButton(
            onClick = onNavigateToLibrary,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("home_open_32_books_btn")
          ) {
            Icon(Icons.Default.Bookmarks, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("32 Books", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Daily Quranic Ayah Card
    item {
      dailyAyah?.let { ayah ->
        val surah = surahs.find { it.number == ayah.surahNumber }
        val surahName = surah?.nameEnglish ?: "Surah ${ayah.surahNumber}"

        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_ayah_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(16.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Daily Quranic Ayah",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer
              ) {
                Text(
                  text = "$surahName ${ayah.surahNumber}:${ayah.verseNumber}",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Verse
            Text(
              text = ayah.textArabic,
              style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = AmiriFontFamily,
                fontSize = 23.sp,
                lineHeight = 38.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface
              ),
              modifier = Modifier.fillMaxWidth()
            )

            if (ayah.transliteration.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = ayah.transliteration,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 12.sp,
                  fontStyle = FontStyle.Italic,
                  color = MaterialTheme.colorScheme.secondary
                )
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // English Translation (Saheeh International)
            Text(
              text = ayah.translationEnglish,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Urdu Translation (Fateh Muhammad Jalandhry)
            Text(
              text = ayah.translationUrdu,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Card Action Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row {
                FilledTonalButton(
                  onClick = { onPlayRecitation(ayah, surahName) },
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(Icons.Default.VolumeUp, contentDescription = "Play", modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Listen", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                surah?.let { s ->
                  OutlinedButton(
                    onClick = { onOpenSurah(s) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Text("Read Surah & 8 Tafsirs", fontSize = 12.sp)
                  }
                }
              }

              IconButton(
                onClick = { onBookmarkAyah(ayah, surahName) },
                modifier = Modifier.size(36.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.BookmarkBorder,
                  contentDescription = "Bookmark Ayah",
                  tint = GoldPrimary
                )
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
    }

    // Daily Authentic Hadith Card
    item {
      dailyHadith?.let { hadith ->
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_hadith_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Prophetic Tradition",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              )
              SahihGradeBadge(grade = hadith.grade)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "${hadith.reference} • ${hadith.inBookReference}",
              style = MaterialTheme.typography.labelMedium.copy(
                color = GoldPrimary,
                fontWeight = FontWeight.Bold
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Narrator
            Text(
              text = "Narrated: ${hadith.narrator}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Hadith in bundled Amiri Naskh font
            Text(
              text = hadith.arabicText,
              style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = AmiriFontFamily,
                fontSize = 20.sp,
                lineHeight = 34.sp,
                textAlign = TextAlign.Right
              ),
              modifier = Modifier.fillMaxWidth()
            )

            if (hadith.transliteration.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = hadith.transliteration,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 12.sp,
                  fontStyle = FontStyle.Italic,
                  color = MaterialTheme.colorScheme.secondary
                )
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // English Hadith
            Text(
              text = hadith.englishText,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
              )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Urdu Hadith
            Text(
              text = hadith.urduText,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = onNavigateToHadith,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Explore 7 Hadith Books", fontSize = 12.sp)
              }

              Row {
                IconButton(
                  onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Hadith", "${hadith.englishText}\n\n[${hadith.reference}]")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Hadith copied to clipboard", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                IconButton(
                  onClick = { onBookmarkHadith(hadith) },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = GoldPrimary
                  )
                }
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
    }

    // Moment of Reflection Section (Purely Islamic: Prophetic Guidance & Companions' Wisdom)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Prophetic & Sahabah Reflections",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Category Selector Chips
          Row {
            FilterChip(
              selected = reflectionCategory == ReflectionCategory.PROPHETIC_GUIDANCE,
              onClick = {
                reflectionCategory = ReflectionCategory.PROPHETIC_GUIDANCE
                reflectionIndex = 0
              },
              label = { Text("Prophetic Guidance", fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color.White
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
              selected = reflectionCategory == ReflectionCategory.COMPANIONS_WISDOM,
              onClick = {
                reflectionCategory = ReflectionCategory.COMPANIONS_WISDOM
                reflectionIndex = 0
              },
              label = { Text("Companions' Wisdom (Sahabah)", fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color.White
              )
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          val filteredReflections = ReflectionDataProvider.items.filter { it.category == reflectionCategory }
          val safeIndex = if (filteredReflections.isNotEmpty()) reflectionIndex.coerceIn(0, filteredReflections.lastIndex) else 0
          val item = filteredReflections.getOrElse(safeIndex) { ReflectionDataProvider.items.first() }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = item.theme,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                  )
                )
                Text(
                  text = item.sourceReference,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = item.arabicOrOriginalText,
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontFamily = AmiriFontFamily,
                  fontSize = 19.sp,
                  lineHeight = 32.sp,
                  textAlign = TextAlign.Right
                ),
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = item.translationEnglish,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = item.translationUrdu,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 13.sp,
                  textAlign = TextAlign.Right,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
              )

              if (filteredReflections.size > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  IconButton(
                    onClick = { if (safeIndex > 0) reflectionIndex = safeIndex - 1 },
                    enabled = safeIndex > 0,
                    modifier = Modifier.size(30.dp)
                  ) {
                    Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Previous Reflection")
                  }
                  Text(
                    text = "${safeIndex + 1} / ${filteredReflections.size}",
                    style = MaterialTheme.typography.labelSmall
                  )
                  IconButton(
                    onClick = { if (safeIndex < filteredReflections.lastIndex) reflectionIndex = safeIndex + 1 },
                    enabled = safeIndex < filteredReflections.lastIndex,
                    modifier = Modifier.size(30.dp)
                  ) {
                    Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next Reflection")
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // Quick Jump Grid (Essential Surahs from the 114 Offline Surahs)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Quick Jump to Surahs (All 114 Offline)",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        )
        OutlinedButton(
          onClick = onNavigateToQuran,
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("All 114 Surahs", fontSize = 11.sp)
        }
      }
      Spacer(modifier = Modifier.height(10.dp))

      val quickSurahs = surahs.filter { it.number in listOf(1, 2, 18, 36, 55, 67, 112, 114) }

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        quickSurahs.forEach { surah ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onOpenSurah(surah) },
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${surah.number}",
                  style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                  ),
                  modifier = Modifier.width(32.dp)
                )
                Column {
                  Text(
                    text = surah.nameEnglish,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "${surah.totalVerses} Verses • ${surah.revelationType} • Offline + 8 Tafsirs",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }
              }

              Text(
                text = surah.nameArabic,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = ScheherazadeFontFamily,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
