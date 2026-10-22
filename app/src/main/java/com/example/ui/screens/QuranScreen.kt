package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.local.ExtendedHadithDataProvider
import com.example.data.local.OfflineQuranAssetLoader
import com.example.data.local.entities.AyahEntity
import com.example.data.local.entities.BookmarkEntity
import com.example.data.local.entities.HadithEntity
import com.example.data.local.entities.SurahEntity
import com.example.data.repository.TafsirRepository
import com.example.ui.components.AyahNumberBadge
import com.example.ui.components.BismillahHeader
import com.example.ui.components.RevelationTypeBadge
import com.example.ui.components.SahihGradeBadge
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ScheherazadeFontFamily
import kotlinx.coroutines.launch

@Composable
fun QuranScreen(
  surahs: List<SurahEntity>,
  isReaderOpen: Boolean,
  selectedSurah: SurahEntity?,
  currentAyahs: List<AyahEntity>,
  bookmarks: List<BookmarkEntity>,
  showEnglish: Boolean,
  showUrdu: Boolean,
  showTransliteration: Boolean,
  arabicFontSizeSp: Float,
  translationFontSizeSp: Float,
  onOpenSurah: (SurahEntity) -> Unit,
  onCloseReader: () -> Unit,
  onNavigateAdjacentSurah: (Int) -> Unit = {},
  onToggleEnglish: () -> Unit,
  onToggleUrdu: () -> Unit,
  onToggleTransliteration: () -> Unit,
  onSetArabicFontSize: (Float) -> Unit,
  onPlayAyahRecitation: (AyahEntity, String) -> Unit,
  onBookmarkAyah: (AyahEntity, String) -> Unit,
  onAddNote: (String) -> Unit,
  onOpenOfflineDownloads: () -> Unit = {},
  allHadithsForTafsir: List<HadithEntity> = ExtendedHadithDataProvider.getExtendedHadiths(),
  modifier: Modifier = Modifier
) {
  if (isReaderOpen && selectedSurah != null) {
    SurahReaderView(
      surah = selectedSurah,
      ayahs = currentAyahs,
      bookmarks = bookmarks,
      showEnglish = showEnglish,
      showUrdu = showUrdu,
      showTransliteration = showTransliteration,
      arabicFontSizeSp = arabicFontSizeSp,
      translationFontSizeSp = translationFontSizeSp,
      onBack = onCloseReader,
      onPreviousSurah = { onNavigateAdjacentSurah(-1) },
      onNextSurah = { onNavigateAdjacentSurah(1) },
      onToggleEnglish = onToggleEnglish,
      onToggleUrdu = onToggleUrdu,
      onToggleTransliteration = onToggleTransliteration,
      onSetArabicFontSize = onSetArabicFontSize,
      onPlayAyahRecitation = onPlayAyahRecitation,
      onBookmarkAyah = onBookmarkAyah,
      onAddNote = onAddNote,
      allHadithsForTafsir = allHadithsForTafsir,
      modifier = modifier
    )
  } else {
    SurahCatalogView(
      surahs = surahs,
      onOpenSurah = onOpenSurah,
      onOpenOfflineDownloads = onOpenOfflineDownloads,
      modifier = modifier
    )
  }
}

@Composable
fun SurahCatalogView(
  surahs: List<SurahEntity>,
  onOpenSurah: (SurahEntity) -> Unit,
  onOpenOfflineDownloads: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }

  val filteredSurahs = remember(surahs, searchQuery) {
    if (searchQuery.isBlank()) {
      surahs
    } else {
      surahs.filter {
        it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
          it.nameArabic.contains(searchQuery) ||
          it.nameUrdu.contains(searchQuery) ||
          it.nameEnglishTranslation.contains(searchQuery, ignoreCase = true) ||
          it.number.toString() == searchQuery.trim()
      }
    }
  }

  Column(modifier = modifier.fillMaxSize()) {
    TabRow(
      selectedTabIndex = selectedTabIndex,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.primary
    ) {
      Tab(
        selected = selectedTabIndex == 0,
        onClick = { selectedTabIndex = 0 },
        text = { Text("All 114 Surahs (Offline)", fontWeight = FontWeight.Bold) }
      )
      Tab(
        selected = selectedTabIndex == 1,
        onClick = { selectedTabIndex = 1 },
        text = { Text("Juz Index (30)", fontWeight = FontWeight.Bold) }
      )
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Select Surahs, Ayahs, Translations, Tafsirs & Audio",
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.weight(1f)
      )
      Button(
        onClick = onOpenOfflineDownloads,
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        modifier = Modifier.testTag("quran_download_for_offline_use_button")
      ) {
        Text("Download for Offline Use", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .testTag("surah_catalog_search_input"),
      placeholder = { Text("Search Surah by number (1-114) or name...") },
      singleLine = true,
      shape = RoundedCornerShape(12.dp)
    )

    if (selectedTabIndex == 0) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filteredSurahs, key = { it.number }) { surah ->
          SurahListItem(
            surah = surah,
            onClick = { onOpenSurah(surah) }
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items((1..30).toList(), key = { it }) { juzNumber ->
          val startingSurah = surahs.find { it.juzStart == juzNumber } ?: surahs.first()
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onOpenSurah(startingSurah) },
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "$juzNumber",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                  Text(
                    text = "Juz $juzNumber (پارہ $juzNumber)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "Starts at ${startingSurah.nameEnglish} (${startingSurah.nameArabic})",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun SurahListItem(
  surah: SurahEntity,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("surah_item_${surah.number}"),
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${surah.number}",
            style = MaterialTheme.typography.labelLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = surah.nameEnglish,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            RevelationTypeBadge(type = surah.revelationType)
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = "${surah.nameEnglishTranslation} • ${surah.nameUrdu}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          )

          Text(
            text = "${surah.totalVerses} Verses • Offline Ready + 8 Tafsirs",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.secondary,
              fontSize = 11.sp
            )
          )
        }
      }

      Text(
        text = surah.nameArabic,
        style = MaterialTheme.typography.titleLarge.copy(
          fontFamily = ScheherazadeFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 24.sp,
          color = MaterialTheme.colorScheme.primary
        )
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahReaderView(
  surah: SurahEntity,
  ayahs: List<AyahEntity>,
  bookmarks: List<BookmarkEntity>,
  showEnglish: Boolean,
  showUrdu: Boolean,
  showTransliteration: Boolean,
  arabicFontSizeSp: Float,
  translationFontSizeSp: Float,
  onBack: () -> Unit,
  onPreviousSurah: () -> Unit,
  onNextSurah: () -> Unit,
  onToggleEnglish: () -> Unit,
  onToggleUrdu: () -> Unit,
  onToggleTransliteration: () -> Unit,
  onSetArabicFontSize: (Float) -> Unit,
  onPlayAyahRecitation: (AyahEntity, String) -> Unit,
  onBookmarkAyah: (AyahEntity, String) -> Unit,
  onAddNote: (String) -> Unit,
  allHadithsForTafsir: List<HadithEntity>,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  var showSettingsSheet by remember { mutableStateOf(false) }
  var selectedAyahForTafsir by remember { mutableStateOf<AyahEntity?>(null) }
  var focusedVerseIndex by remember(surah.number) { mutableIntStateOf(0) }
  var verseJumpText by remember(surah.number) { mutableStateOf("") }

  Column(modifier = modifier.fillMaxSize()) {
    // Reader Toolbar with Prev / Next Surah
    TopAppBar(
      title = {
        Column {
          Text(
            text = "${surah.number}. ${surah.nameEnglish} • ${surah.nameArabic}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "${surah.revelationType} • ${surah.totalVerses} Verses • Juz ${surah.juzStart}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      },
      navigationIcon = {
        IconButton(onClick = onBack, modifier = Modifier.testTag("surah_reader_back_button")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
      },
      actions = {
        IconButton(
          onClick = onPreviousSurah,
          enabled = surah.number > 1,
          modifier = Modifier.testTag("prev_surah_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Surah")
        }
        IconButton(
          onClick = onNextSurah,
          enabled = surah.number < 114,
          modifier = Modifier.testTag("next_surah_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Surah")
        }
        IconButton(
          onClick = { showSettingsSheet = true },
          modifier = Modifier.testTag("reader_settings_button")
        ) {
          Icon(Icons.Default.FormatSize, contentDescription = "Font & Display Settings")
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface
      )
    )

    // Quick translation toggle bar + Verse Jump / Prev-Next Ayah controls
    Surface(
      color = MaterialTheme.colorScheme.surfaceVariant,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilterChip(
            selected = showEnglish,
            onClick = onToggleEnglish,
            label = { Text("Saheeh Intl", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EmeraldPrimary,
              selectedLabelColor = Color.White
            )
          )
          FilterChip(
            selected = showUrdu,
            onClick = onToggleUrdu,
            label = { Text("Urdu", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EmeraldPrimary,
              selectedLabelColor = Color.White
            )
          )
          FilterChip(
            selected = showTransliteration,
            onClick = onToggleTransliteration,
            label = { Text("Transliteration", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EmeraldPrimary,
              selectedLabelColor = Color.White
            )
          )
        }

        if (ayahs.isNotEmpty()) {
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = {
                focusedVerseIndex = (focusedVerseIndex - 1).coerceAtLeast(0)
                coroutineScope.launch {
                  listState.animateScrollToItem(focusedVerseIndex)
                }
              },
              enabled = focusedVerseIndex > 0,
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("prev_ayah_button")
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Prev Ayah", fontSize = 11.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              OutlinedTextField(
                value = verseJumpText,
                onValueChange = { input ->
                  verseJumpText = input.filter { it.isDigit() }
                  val vNum = verseJumpText.toIntOrNull()
                  if (vNum != null && vNum in 1..ayahs.size) {
                    focusedVerseIndex = vNum - 1
                    coroutineScope.launch {
                      listState.animateScrollToItem(focusedVerseIndex)
                    }
                  }
                },
                placeholder = { Text("Ayah # (1-${ayahs.size})", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier
                  .width(128.dp)
                  .height(46.dp)
                  .testTag("jump_to_ayah_input")
              )
            }

            OutlinedButton(
              onClick = {
                focusedVerseIndex = (focusedVerseIndex + 1).coerceAtMost((ayahs.size - 1).coerceAtLeast(0))
                coroutineScope.launch {
                  listState.animateScrollToItem(focusedVerseIndex)
                }
              },
              enabled = focusedVerseIndex < ayahs.size - 1,
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("next_ayah_button")
            ) {
              Text("Next Ayah", fontSize = 11.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
            }
          }
        }
      }
    }

    // Ayah List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      if (surah.number != 9 && surah.number != 1) {
        item {
          BismillahHeader()
        }
      }

      if (ayahs.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              CircularProgressIndicator(color = EmeraldPrimary)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Loading offline verses for Surah ${surah.nameEnglish} (${surah.nameArabic})...",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }
      } else {
        items(ayahs, key = { "${it.surahNumber}_${it.verseNumber}" }) { ayah ->
          val isBookmarked = bookmarks.any { it.itemKey == "ayah_${ayah.surahNumber}_${ayah.verseNumber}" }

          AyahCardItem(
            ayah = ayah,
            surahName = surah.nameEnglish,
            isBookmarked = isBookmarked,
            showEnglish = showEnglish,
            showUrdu = showUrdu,
            showTransliteration = showTransliteration,
            arabicFontSizeSp = arabicFontSizeSp,
            translationFontSizeSp = translationFontSizeSp,
            onPlay = { onPlayAyahRecitation(ayah, surah.nameEnglish) },
            onOpenTafsir = { selectedAyahForTafsir = ayah },
            onBookmark = { onBookmarkAyah(ayah, surah.nameEnglish) },
            onCopy = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val text = "${ayah.textArabic}\n\n${ayah.transliteration}\n\n${ayah.translationEnglish}\n\n${ayah.translationUrdu}\n\n[${surah.nameEnglish} ${ayah.surahNumber}:${ayah.verseNumber}]"
              clipboard.setPrimaryClip(ClipData.newPlainText("Ayah", text))
              Toast.makeText(context, "Ayah copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            onAddNote = { onAddNote("${surah.nameEnglish} ${ayah.surahNumber}:${ayah.verseNumber}") }
          )
        }

        // Surah Footer with Previous / Next Surah buttons
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            OutlinedButton(
              onClick = onPreviousSurah,
              enabled = surah.number > 1
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Surah ${(surah.number - 1).coerceAtLeast(1)}")
            }
            Button(
              onClick = onNextSurah,
              enabled = surah.number < 114,
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
              Text("Surah ${(surah.number + 1).coerceAtMost(114)}")
              Spacer(modifier = Modifier.width(6.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
          }
        }
      }
    }
  }

  // 8-Tafsir & Linked Hadith Modal Bottom Sheet
  selectedAyahForTafsir?.let { activeAyah ->
    TafsirModalSheet(
      ayah = activeAyah,
      surah = surah,
      allHadiths = allHadithsForTafsir,
      hasPrevAyah = activeAyah.verseNumber > 1,
      hasNextAyah = activeAyah.verseNumber < ayahs.size,
      onPrevAyahTafsir = {
        ayahs.find { it.verseNumber == activeAyah.verseNumber - 1 }?.let {
          selectedAyahForTafsir = it
        }
      },
      onNextAyahTafsir = {
        ayahs.find { it.verseNumber == activeAyah.verseNumber + 1 }?.let {
          selectedAyahForTafsir = it
        }
      },
      onDismiss = { selectedAyahForTafsir = null }
    )
  }

  // Font & Display Bottom Sheet
  if (showSettingsSheet) {
    ModalBottomSheet(
      onDismissRequest = { showSettingsSheet = false }
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 16.dp)
      ) {
        Text(
          text = "Reader Settings",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Arabic Font Size: ${arabicFontSizeSp.toInt()} sp",
          style = MaterialTheme.typography.titleSmall
        )
        Slider(
          value = arabicFontSizeSp,
          onValueChange = onSetArabicFontSize,
          valueRange = 18f..38f,
          modifier = Modifier.fillMaxWidth()
        )

        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontFamily = AmiriFontFamily,
              fontSize = arabicFontSizeSp.sp,
              textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(16.dp)
          )
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TafsirModalSheet(
  ayah: AyahEntity,
  surah: SurahEntity,
  allHadiths: List<HadithEntity>,
  hasPrevAyah: Boolean,
  hasNextAyah: Boolean,
  onPrevAyahTafsir: () -> Unit,
  onNextAyahTafsir: () -> Unit,
  onDismiss: () -> Unit
) {
  var selectedEditionKey by remember { mutableStateOf(TafsirRepository.editions.first().key) }
  val tafsirResult = remember(ayah.surahNumber, ayah.verseNumber, selectedEditionKey) {
    TafsirRepository.getTafsirForAyah(
      ayah = ayah,
      surah = surah,
      editionKey = selectedEditionKey,
      allHadiths = allHadiths
    )
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      // Header + Prev/Next Verse Tafsir Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Authentic Tafsir & Linked Hadith",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          Text(
            text = tafsirResult.revelationContext,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
        Row {
          IconButton(onClick = onPrevAyahTafsir, enabled = hasPrevAyah) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Verse Tafsir")
          }
          IconButton(onClick = onNextAyahTafsir, enabled = hasNextAyah) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Verse Tafsir")
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 8 Tafsir Edition Selector Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(TafsirRepository.editions, key = { it.key }) { edition ->
          val isSelected = edition.key == selectedEditionKey
          FilterChip(
            selected = isSelected,
            onClick = { selectedEditionKey = edition.key },
            label = {
              Text(
                text = "${edition.titleEnglish} (${edition.eraBadge})",
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EmeraldPrimary,
              selectedLabelColor = Color.White
            ),
            modifier = Modifier.testTag("tafsir_chip_${edition.key}")
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Verse Text + Transliteration Box
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          val cleanTafsirArabic = OfflineQuranAssetLoader.stripDuplicateBismillahPrefix(
            surahNumber = ayah.surahNumber,
            verseNumber = ayah.verseNumber,
            rawArabic = tafsirResult.arabicText
          )
          Text(
            text = cleanTafsirArabic,
            style = MaterialTheme.typography.headlineSmall.copy(
              fontFamily = AmiriFontFamily,
              fontWeight = FontWeight.Normal,
              fontSize = 24.sp,
              lineHeight = 46.sp,
              textAlign = TextAlign.Right
            ),
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Transliteration: ${tafsirResult.transliteration}",
            style = MaterialTheme.typography.bodySmall.copy(
              fontStyle = FontStyle.Italic,
              color = EmeraldPrimary,
              fontWeight = FontWeight.SemiBold
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = tafsirResult.englishTranslation,
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Selected Scholar & Methodology Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "${tafsirResult.edition.titleEnglish} • ${tafsirResult.edition.titleArabic}",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary
            )
          )
          Text(
            text = tafsirResult.edition.scholarName,
            style = MaterialTheme.typography.labelMedium.copy(
              color = GoldPrimary,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = tafsirResult.edition.methodology,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )

          HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

          // English Tafsir
          Text(
            text = tafsirResult.commentaryEnglish,
            style = MaterialTheme.typography.bodyMedium.copy(
              lineHeight = 22.sp
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Urdu Tafsir
          Text(
            text = tafsirResult.commentaryUrdu,
            style = MaterialTheme.typography.bodyMedium.copy(
              lineHeight = 24.sp,
              textAlign = TextAlign.Right,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Cross-Referenced Quranic Verses
      Text(
        text = "Cross-Referenced Quranic Verses (Tafsir al-Qur'an bil-Qur'an)",
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      )
      Spacer(modifier = Modifier.height(6.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(tafsirResult.relatedQuranVerses) { ref ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = EmeraldPrimary.copy(alpha = 0.12f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Surah $ref",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = EmeraldPrimary,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Linked Authentic Hadiths Section
      Text(
        text = "Linked Authentic Hadiths Explaining This Verse (${tafsirResult.linkedHadiths.size})",
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      )
      Spacer(modifier = Modifier.height(8.dp))

      tafsirResult.linkedHadiths.forEach { hadith ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "${hadith.reference} (Intl #${hadith.hadithNumber})",
                  style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                  )
                )
                if (hadith.inBookReference.isNotEmpty()) {
                  Text(
                    text = hadith.inBookReference,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }
              }
              SahihGradeBadge(grade = hadith.grade)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = hadith.arabicText,
              style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = AmiriFontFamily,
                textAlign = TextAlign.Right,
                fontSize = 18.sp,
                lineHeight = 30.sp
              ),
              modifier = Modifier.fillMaxWidth()
            )
            if (hadith.transliteration.isNotEmpty()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = hadith.transliteration,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontStyle = FontStyle.Italic,
                  color = EmeraldPrimary
                )
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = hadith.englishText,
              style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = hadith.urduText,
              style = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun AyahCardItem(
  ayah: AyahEntity,
  surahName: String,
  isBookmarked: Boolean,
  showEnglish: Boolean,
  showUrdu: Boolean,
  showTransliteration: Boolean,
  arabicFontSizeSp: Float,
  translationFontSizeSp: Float,
  onPlay: () -> Unit,
  onOpenTafsir: () -> Unit,
  onBookmark: () -> Unit,
  onCopy: () -> Unit,
  onAddNote: () -> Unit,
  modifier: Modifier = Modifier
) {
  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("ayah_card_${ayah.surahNumber}_${ayah.verseNumber}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.5.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Verse number + Tafsir button & Action icons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          AyahNumberBadge(verseNumber = ayah.verseNumber)
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            modifier = Modifier
              .clickable(onClick = onOpenTafsir)
              .testTag("ayah_tafsir_button_${ayah.surahNumber}_${ayah.verseNumber}"),
            shape = RoundedCornerShape(8.dp),
            color = EmeraldPrimary.copy(alpha = 0.12f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = "Read Tafsir & Linked Hadith",
                tint = EmeraldPrimary,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "8 Tafsirs & Hadith",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = EmeraldPrimary
                )
              )
            }
          }
        }

        Row {
          IconButton(onClick = onPlay, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.VolumeUp, contentDescription = "Play Audio", tint = EmeraldPrimary)
          }
          IconButton(onClick = onBookmark, modifier = Modifier.size(36.dp)) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = GoldPrimary
            )
          }
          IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          IconButton(onClick = onAddNote, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.NoteAdd, contentDescription = "Note", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Arabic Uthmani Text in Amiri Quran Uthmani Mushaf Font (without duplicate Bismillah on Verse 1)
      val cleanVerseArabic = OfflineQuranAssetLoader.stripDuplicateBismillahPrefix(
        surahNumber = ayah.surahNumber,
        verseNumber = ayah.verseNumber,
        rawArabic = ayah.textArabic
      )
      Text(
        text = cleanVerseArabic,
        style = MaterialTheme.typography.headlineMedium.copy(
          fontFamily = AmiriFontFamily,
          fontWeight = FontWeight.Normal,
          fontSize = arabicFontSizeSp.sp,
          lineHeight = (arabicFontSizeSp * 2.0f).sp,
          textAlign = TextAlign.Right,
          color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth()
      )

      if (showTransliteration && ayah.transliteration.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = ayah.transliteration,
          style = MaterialTheme.typography.bodySmall.copy(
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 13.sp
          )
        )
      }

      if (showEnglish && ayah.translationEnglish.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = ayah.translationEnglish,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = translationFontSizeSp.sp,
            lineHeight = (translationFontSizeSp * 1.45f).sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        )
      }

      if (showUrdu && ayah.translationUrdu.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = ayah.translationUrdu,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = translationFontSizeSp.sp,
            lineHeight = (translationFontSizeSp * 1.55f).sp,
            textAlign = TextAlign.Right,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}
