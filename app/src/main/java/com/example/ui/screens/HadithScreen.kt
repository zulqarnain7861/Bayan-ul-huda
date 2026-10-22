package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExtendedHadithDataProvider
import com.example.data.local.entities.AyahEntity
import com.example.data.local.entities.BookmarkEntity
import com.example.data.local.entities.HadithEntity
import com.example.ui.components.SahihGradeBadge
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ScheherazadeFontFamily
import com.example.ui.viewmodels.HadithEditionFilterMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HadithScreen(
  selectedCollectionKey: String,
  hadiths: List<HadithEntity>,
  bookmarks: List<BookmarkEntity>,
  selectedEditionFilterMode: HadithEditionFilterMode = HadithEditionFilterMode.ALL_EDITIONS,
  isHadithLookupLoading: Boolean = false,
  selectedHadithDetail: HadithEntity? = null,
  previewedQuranVerse: AyahEntity? = null,
  onSelectCollection: (String) -> Unit,
  onSelectEditionFilterMode: (HadithEditionFilterMode) -> Unit = {},
  onLookupHadithByNumber: (String, Int) -> Unit = { _, _ -> },
  onLoadBookSection: (Int) -> Unit = {},
  onShowHadithDetail: (HadithEntity) -> Unit = {},
  onCloseHadithDetail: () -> Unit = {},
  onClickQuranVerseRef: (String, Boolean) -> Unit = { _, _ -> },
  onDismissQuranVersePreview: () -> Unit = {},
  onBookmarkHadith: (HadithEntity) -> Unit,
  onAddNote: (String) -> Unit,
  onOpenOfflineDownloads: () -> Unit = {},
  onOpenEvidenceVerification: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  var searchQuery by remember { mutableStateOf("") }
  var hadithNumberInput by remember { mutableStateOf("") }
  var selectedBookNumber by remember(selectedCollectionKey) { mutableStateOf(0) }

  // All 26 Hadith Collections (Sunni Kutub al-Sittah, Muwatta, Musnad, Musannaf + Shia Kutub al-Arba'ah & Reference Works)
  val collections = remember {
    ExtendedHadithDataProvider.primaryCollections.map {
      Triple(it.key, it.nameEnglish, it.nameArabic)
    }
  }

  val activeCollectionMeta = remember(selectedCollectionKey) {
    ExtendedHadithDataProvider.primaryCollections.find { it.key == selectedCollectionKey }
  }

  val totalBooksInCollection = (activeCollectionMeta?.totalBooksCount ?: 1).coerceAtLeast(1)

  val filteredHadiths = remember(hadiths, searchQuery, selectedEditionFilterMode, selectedBookNumber) {
    val bookFiltered = if (selectedBookNumber <= 0) {
      hadiths
    } else {
      hadiths.filter { it.bookNumber == selectedBookNumber }.ifEmpty { hadiths }
    }

    val modeFiltered = when (selectedEditionFilterMode) {
      HadithEditionFilterMode.ALL_EDITIONS -> bookFiltered
      HadithEditionFilterMode.DARUSSALAM -> bookFiltered.filter { it.hadithNumber > 0 }
      HadithEditionFilterMode.IN_BOOK -> bookFiltered.filter {
        it.inBookReference.isNotBlank() && !it.inBookReference.contains("Not available", ignoreCase = true)
      }
      HadithEditionFilterMode.SAHIH_INTERNATIONAL_QURAN -> bookFiltered.filter {
        it.linkedQuranVerses.isNotBlank()
      }
    }

    if (searchQuery.isBlank()) {
      modeFiltered
    } else {
      val q = searchQuery.trim()
      modeFiltered.filter {
        it.englishText.contains(q, ignoreCase = true) ||
          it.urduText.contains(q) ||
          it.arabicText.contains(q) ||
          it.narrator.contains(q, ignoreCase = true) ||
          it.bookName.contains(q, ignoreCase = true) ||
          it.reference.contains(q, ignoreCase = true) ||
          it.darussalamReference.contains(q, ignoreCase = true) ||
          it.inBookReference.contains(q, ignoreCase = true) ||
          it.transliteration.contains(q, ignoreCase = true) ||
          it.linkedQuranVerses.contains(q, ignoreCase = true) ||
          it.hadithNumber.toString() == q
      }
    }
  }

  fun triggerNumberSearch(rawInput: String = hadithNumberInput) {
    val trimmed = rawInput.trim()
    val num = trimmed.filter { it.isDigit() }.toIntOrNull()
    if (num == null || num <= 0) {
      Toast.makeText(context, "Enter a valid Hadith number (e.g. 1, 223, 5027)", Toast.LENGTH_SHORT).show()
      return
    }
    val localIdx = filteredHadiths.indexOfFirst {
      it.hadithNumber == num || it.inBookReference.endsWith("Hadith $num")
    }
    if (localIdx >= 0) {
      val matched = filteredHadiths[localIdx]
      coroutineScope.launch {
        listState.animateScrollToItem(localIdx)
      }
      onShowHadithDetail(matched)
    } else {
      onLookupHadithByNumber(selectedCollectionKey, num)
    }
  }

  Column(modifier = modifier.fillMaxSize()) {
    // All 26 Hadith Collections Tabs
    ScrollableTabRow(
      selectedTabIndex = collections.indexOfFirst { it.first == selectedCollectionKey }.coerceAtLeast(0),
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.primary,
      edgePadding = 12.dp
    ) {
      collections.forEach { (key, englishTitle, arabicTitle) ->
        val isSelected = selectedCollectionKey == key
        Tab(
          selected = isSelected,
          onClick = { onSelectCollection(key) },
          modifier = Modifier.testTag("hadith_tab_$key"),
          text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = englishTitle,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
              )
              Text(
                text = arabicTitle,
                fontFamily = ScheherazadeFontFamily,
                fontSize = 13.sp,
                color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        )
      }
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      FilledTonalButton(
        onClick = onOpenOfflineDownloads,
        modifier = Modifier.weight(1f).testTag("hadith_download_offline_button")
      ) {
        Text("Download for Offline Use", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
      OutlinedButton(
        onClick = onOpenEvidenceVerification,
        modifier = Modifier.weight(1f).testTag("hadith_verify_with_evidence_button")
      ) {
        Text("Islam with Evidence", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    // Book / Kitab Browser Row (All Books + Book 1 .. Book N for the selected collection)
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      item {
        FilterChip(
          selected = selectedBookNumber == 0,
          onClick = { selectedBookNumber = 0 },
          label = { Text("All Books ($totalBooksInCollection)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GoldPrimary.copy(alpha = 0.22f),
            selectedLabelColor = EmeraldPrimary
          ),
          modifier = Modifier.testTag("hadith_book_chip_all")
        )
      }
      items(totalBooksInCollection) { idx ->
        val bookNum = idx + 1
        FilterChip(
          selected = selectedBookNumber == bookNum,
          onClick = {
            selectedBookNumber = bookNum
            onLoadBookSection(bookNum)
          },
          label = { Text("Book $bookNum", fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = EmeraldPrimary,
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.testTag("hadith_book_chip_$bookNum")
        )
      }
    }

    // Clickable Edition & Reference Mode Selector Row (Darussalam # | In-Book # | Sahih International Quran Refs)
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(HadithEditionFilterMode.entries.size) { idx ->
        val mode = HadithEditionFilterMode.entries[idx]
        FilterChip(
          selected = selectedEditionFilterMode == mode,
          onClick = { onSelectEditionFilterMode(mode) },
          label = { Text(mode.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = EmeraldPrimary,
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.testTag("hadith_edition_chip_${mode.name.lowercase()}")
        )
      }
    }

    // Search by Hadith Number (Darussalam / Sunnah.com & In-Book Numbering)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = hadithNumberInput,
        onValueChange = { hadithNumberInput = it.filter { ch -> ch.isDigit() } },
        modifier = Modifier
          .weight(1f)
          .testTag("hadith_number_search_input"),
        placeholder = { Text("Hadith # (Darussalam / In-Book e.g. 5027)", fontSize = 12.sp) },
        leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, modifier = Modifier.size(18.dp)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Number,
          imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = { triggerNumberSearch() }),
        shape = RoundedCornerShape(12.dp)
      )

      Button(
        onClick = { triggerNumberSearch() },
        enabled = !isHadithLookupLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        modifier = Modifier
          .height(52.dp)
          .testTag("hadith_number_lookup_button")
      ) {
        if (isHadithLookupLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp,
            color = Color.White
          )
        } else {
          Text("Find #", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }

    // Topic, Narrator, Book, or Keyword filter (also supports pressing Enter to fetch a Hadith # from API)
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .testTag("hadith_library_search_input"),
      placeholder = { Text("Filter by narrator, topic, Quran verse, or Hadith #...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Close, contentDescription = "Clear")
          }
        }
      },
      singleLine = true,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions = KeyboardActions(
        onSearch = {
          val maybeNum = searchQuery.trim().toIntOrNull()
          if (maybeNum != null && maybeNum > 0) {
            triggerNumberSearch(searchQuery)
          }
        }
      ),
      shape = RoundedCornerShape(12.dp)
    )

    // If user typed a Hadith number in the filter box that isn't cached yet, offer 1-tap live fetch from Sunnah.com API
    val typedNumberInFilter = searchQuery.trim().filter { it.isDigit() }.toIntOrNull()
    if (filteredHadiths.isEmpty() && typedNumberInFilter != null && typedNumberInFilter > 0) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = EmeraldPrimary.copy(alpha = 0.1f),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Fetch Hadith #$typedNumberInFilter from Verified Sunnah.com API?",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(1f)
          )
          Button(
            onClick = { onLookupHadithByNumber(selectedCollectionKey, typedNumberInFilter) },
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Text("Fetch #$typedNumberInFilter", fontSize = 12.sp)
          }
        }
      }
    }

    // Collection stats & Quick Prev / Next Hadith navigation bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "${filteredHadiths.size} Cached Locally • ${activeCollectionMeta?.totalHadithsInfo ?: "Verified Collection"}",
          style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
          )
        )
        Text(
          text = "Separate Edition Labels: Darussalam # • In-Book # • Sahih International (Quran)",
          style = MaterialTheme.typography.labelSmall.copy(
            color = GoldPrimary,
            fontWeight = FontWeight.Bold
          )
        )
      }

      if (filteredHadiths.isNotEmpty()) {
        FilledTonalButton(
          onClick = { onShowHadithDetail(filteredHadiths.first()) },
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("open_hadith_reader_button")
        ) {
          Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Reader Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Hadith List
    LazyColumn(
      state = listState,
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      itemsIndexed(
        items = filteredHadiths,
        key = { _, it -> "${it.collectionKey}_${it.hadithNumber}" }
      ) { index, hadith ->
        val isBookmarked = bookmarks.any { it.itemKey == "hadith_${hadith.collectionKey}_${hadith.hadithNumber}" }

        HadithCardItem(
          hadith = hadith,
          isBookmarked = isBookmarked,
          hasPrevious = index > 0,
          hasNext = index < filteredHadiths.lastIndex,
          onPrevious = {
            if (index > 0) {
              coroutineScope.launch { listState.animateScrollToItem(index - 1) }
            }
          },
          onNext = {
            if (index < filteredHadiths.lastIndex) {
              coroutineScope.launch { listState.animateScrollToItem(index + 1) }
            }
          },
          onOpenDetail = { onShowHadithDetail(hadith) },
          onClickDarussalamBadge = {
            hadithNumberInput = hadith.hadithNumber.toString()
            onSelectEditionFilterMode(HadithEditionFilterMode.DARUSSALAM)
            onShowHadithDetail(hadith)
          },
          onClickInBookBadge = {
            onSelectEditionFilterMode(HadithEditionFilterMode.IN_BOOK)
            if (hadith.bookNumber > 0) {
              onLoadBookSection(hadith.bookNumber)
            }
            onShowHadithDetail(hadith)
          },
          onClickQuranVerseBadge = { verseRef ->
            onClickQuranVerseRef(verseRef, false)
          },
          onBookmark = { onBookmarkHadith(hadith) },
          onCopy = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val darussalamLabel = hadith.darussalamReference.ifBlank { "Darussalam: #${hadith.hadithNumber}" }
            val text = buildString {
              append(hadith.arabicText)
              if (hadith.transliteration.isNotBlank()) {
                append("\n\nTransliteration: ").append(hadith.transliteration)
              }
              append("\n\n").append(hadith.englishText)
              append("\n\n").append(hadith.urduText)
              append("\n\n[${hadith.reference} | $darussalamLabel | ${hadith.inBookReference} - Grade: ${hadith.grade}]")
            }
            clipboard.setPrimaryClip(ClipData.newPlainText("Hadith", text))
            Toast.makeText(context, "Hadith copied to clipboard", Toast.LENGTH_SHORT).show()
          },
          onShare = {
            val darussalamLabel = hadith.darussalamReference.ifBlank { "Darussalam: #${hadith.hadithNumber}" }
            val sendIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(
                Intent.EXTRA_TEXT,
                "${hadith.arabicText}\n\n${hadith.englishText}\n\n${hadith.urduText}\n\n[${hadith.reference} | $darussalamLabel | ${hadith.inBookReference} - Grade: ${hadith.grade}]\nShared via Bayan al-Huda"
              )
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share Hadith"))
          },
          onAddNote = { onAddNote(hadith.reference) }
        )
      }
    }
  }

  // Clickable Sahih International Quran Verse Preview Modal Sheet
  previewedQuranVerse?.let { ayah ->
    val verseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
      onDismissRequest = onDismissQuranVersePreview,
      sheetState = verseSheetState,
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Quran ${ayah.surahNumber}:${ayah.verseNumber}",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )
            Text(
              text = "Translation: Sahih International (Verified Quran Edition)",
              style = MaterialTheme.typography.labelMedium.copy(
                color = GoldPrimary,
                fontWeight = FontWeight.Bold
              )
            )
          }
          IconButton(onClick = onDismissQuranVersePreview) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = ayah.textArabic,
            style = MaterialTheme.typography.headlineSmall.copy(
              fontFamily = AmiriFontFamily,
              fontSize = 23.sp,
              lineHeight = 38.sp,
              textAlign = TextAlign.Right
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          )
        }

        if (ayah.transliteration.isNotBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = ayah.transliteration,
            style = MaterialTheme.typography.bodySmall.copy(
              fontStyle = FontStyle.Italic,
              color = MaterialTheme.colorScheme.secondary
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Sahih International (English Translation):",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = EmeraldPrimary
          )
        )
        Text(
          text = ayah.translationEnglish,
          style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = ayah.translationUrdu,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 15.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Right,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
          onClick = { onClickQuranVerseRef("${ayah.surahNumber}:${ayah.verseNumber}", true) },
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("open_full_surah_from_hadith_btn")
        ) {
          Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Open Surah ${ayah.surahNumber} in Full Quran Reader")
        }
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Focused Hadith Reader Modal with Separate Edition Labels & Previous / Next Navigation
  selectedHadithDetail?.let { currentHadith ->
    val currentIndex = filteredHadiths.indexOfFirst {
      it.collectionKey == currentHadith.collectionKey && it.hadithNumber == currentHadith.hadithNumber
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val darussalamLabel = currentHadith.darussalamReference.ifBlank { "Darussalam: #${currentHadith.hadithNumber}" }
    val inBookLabel = currentHadith.inBookReference.ifBlank { "In-Book: Not available in this source" }

    ModalBottomSheet(
      onDismissRequest = onCloseHadithDetail,
      sheetState = sheetState,
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Top Header with Close button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = currentHadith.reference,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )
            Text(
              text = "$darussalamLabel • $inBookLabel",
              style = MaterialTheme.typography.labelMedium.copy(
                color = GoldPrimary,
                fontWeight = FontWeight.Bold
              )
            )
          }
          SahihGradeBadge(grade = currentHadith.grade)
          IconButton(onClick = onCloseHadithDetail) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Previous / Next Hadith Bar inside Reader Modal
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = {
              if (currentIndex > 0) {
                onShowHadithDetail(filteredHadiths[currentIndex - 1])
              } else if (currentHadith.hadithNumber > 1) {
                onLookupHadithByNumber(currentHadith.collectionKey, currentHadith.hadithNumber - 1)
              }
            },
            enabled = currentIndex > 0 || currentHadith.hadithNumber > 1,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("modal_prev_hadith_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Previous Hadith", fontSize = 12.sp)
          }

          Text(
            text = if (currentIndex >= 0) "${currentIndex + 1} / ${filteredHadiths.size}" else "#${currentHadith.hadithNumber}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )

          OutlinedButton(
            onClick = {
              if (currentIndex in 0 until filteredHadiths.lastIndex) {
                onShowHadithDetail(filteredHadiths[currentIndex + 1])
              } else {
                onLookupHadithByNumber(currentHadith.collectionKey, currentHadith.hadithNumber + 1)
              }
            },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("modal_next_hadith_button")
          ) {
            Text("Next Hadith", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null, modifier = Modifier.size(18.dp))
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        if (currentHadith.bookName.isNotBlank()) {
          Text(
            text = "Chapter / Book: ${currentHadith.bookName}",
            style = MaterialTheme.typography.labelLarge.copy(
              color = MaterialTheme.colorScheme.secondary,
              fontWeight = FontWeight.SemiBold
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
          text = "Narrator (Sanad): ${currentHadith.narrator}",
          style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
          )
        )
        if (currentHadith.gradingScholar.isNotBlank()) {
          Text(
            text = "Grading Source: ${currentHadith.grade} (${currentHadith.gradingScholar})",
            style = MaterialTheme.typography.labelSmall.copy(
              color = EmeraldPrimary,
              fontWeight = FontWeight.SemiBold
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Arabic Text
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = currentHadith.arabicText,
            style = MaterialTheme.typography.headlineSmall.copy(
              fontFamily = AmiriFontFamily,
              fontSize = 22.sp,
              lineHeight = 38.sp,
              textAlign = TextAlign.Right,
              color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          )
        }

        // Transliteration
        if (currentHadith.transliteration.isNotBlank()) {
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Transliteration",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = GoldPrimary
            )
          )
          Text(
            text = currentHadith.transliteration,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontSize = 14.sp,
              fontStyle = FontStyle.Italic,
              color = MaterialTheme.colorScheme.secondary
            )
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // English Translation (Verified Collection Source)
        Text(
          text = "English Translation (${currentHadith.collectionName} Verified Edition)",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = EmeraldPrimary
          )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = currentHadith.englishText.ifBlank { "Not available in this source" },
          style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 15.sp,
            lineHeight = 23.sp
          )
        )

        // Urdu Translation
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "اردو ترجمہ (Urdu Translation)",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = GoldPrimary,
            textAlign = TextAlign.Right
          ),
          modifier = Modifier.fillMaxWidth()
        )
        Text(
          text = currentHadith.urduText.ifBlank { "Not available in this source" },
          style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 16.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Right,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          modifier = Modifier.fillMaxWidth()
        )

        // Clickable Linked Quran Verses (Sahih International Quran Translation)
        if (currentHadith.linkedQuranVerses.isNotBlank()) {
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "Linked Quranic Verses (Tap for Sahih International Translation):",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            currentHadith.linkedQuranVerses.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { verseRef ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmeraldPrimary.copy(alpha = 0.14f),
                modifier = Modifier.clickable {
                  onCloseHadithDetail()
                  onClickQuranVerseRef(verseRef, false)
                }
              ) {
                Text(
                  text = "Quran $verseRef (Sahih International)",
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(28.dp))
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HadithCardItem(
  hadith: HadithEntity,
  isBookmarked: Boolean,
  hasPrevious: Boolean = false,
  hasNext: Boolean = false,
  onPrevious: () -> Unit = {},
  onNext: () -> Unit = {},
  onOpenDetail: () -> Unit = {},
  onClickDarussalamBadge: () -> Unit = {},
  onClickInBookBadge: () -> Unit = {},
  onClickQuranVerseBadge: (String) -> Unit = {},
  onBookmark: () -> Unit,
  onCopy: () -> Unit,
  onShare: () -> Unit,
  onAddNote: () -> Unit,
  modifier: Modifier = Modifier
) {
  val darussalamText = hadith.darussalamReference.ifBlank { "Darussalam: #${hadith.hadithNumber}" }
  val inBookText = hadith.inBookReference.ifBlank { "In-Book: Not available in this source" }

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onOpenDetail() }
      .testTag("hadith_card_${hadith.collectionKey}_${hadith.hadithNumber}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.5.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Reference, Chapter/Book, and Authenticity Grade
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = hadith.reference,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          if (hadith.bookName.isNotEmpty()) {
            Text(
              text = hadith.bookName,
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }
        SahihGradeBadge(grade = hadith.grade)
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Clickable Separate Edition Numbering Badges (Darussalam # + In-Book Reference)
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = EmeraldPrimary.copy(alpha = 0.14f),
          modifier = Modifier
            .clickable { onClickDarussalamBadge() }
            .testTag("darussalam_badge_${hadith.collectionKey}_${hadith.hadithNumber}")
        ) {
          Text(
            text = darussalamText,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = GoldPrimary.copy(alpha = 0.16f),
          modifier = Modifier
            .clickable { onClickInBookBadge() }
            .testTag("inbook_badge_${hadith.collectionKey}_${hadith.hadithNumber}")
        ) {
          Text(
            text = if (inBookText.startsWith("In-Book:", ignoreCase = true)) inBookText else "In-Book: $inBookText",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = GoldPrimary
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "Narrator: ${hadith.narrator}",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.secondary
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Arabic Hadith Text in Amiri Classical Naskh Font
      Text(
        text = hadith.arabicText.ifBlank { "Not available in this source" },
        style = MaterialTheme.typography.headlineSmall.copy(
          fontFamily = AmiriFontFamily,
          fontSize = 21.sp,
          lineHeight = 36.sp,
          textAlign = TextAlign.Right,
          color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth()
      )

      // Transliteration
      if (hadith.transliteration.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
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

      // English Translation
      Text(
        text = hadith.englishText.ifBlank { "Not available in this source" },
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 14.sp,
          lineHeight = 21.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      )

      Spacer(modifier = Modifier.height(8.dp))
      // Urdu Translation
      Text(
        text = hadith.urduText.ifBlank { "Not available in this source" },
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 14.sp,
          lineHeight = 22.sp,
          textAlign = TextAlign.Right,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
      )

      // Clickable Linked Quran Verses (Sahih International Quran Translation)
      if (hadith.linkedQuranVerses.isNotBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "Sahih International Quran Refs:",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.align(Alignment.CenterVertically)
          )
          hadith.linkedQuranVerses.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { vRef ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = EmeraldPrimary.copy(alpha = 0.12f),
              modifier = Modifier
                .clickable { onClickQuranVerseBadge(vRef) }
                .testTag("sahih_intl_quran_ref_${hadith.hadithNumber}_$vRef")
            ) {
              Text(
                text = "Quran $vRef (Sahih Intl)",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = EmeraldPrimary
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action row with Prev / Next and Reader Mode
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onPrevious,
            enabled = hasPrevious,
            modifier = Modifier
              .size(34.dp)
              .testTag("card_prev_hadith_${hadith.hadithNumber}")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.NavigateBefore,
              contentDescription = "Previous Hadith",
              tint = if (hasPrevious) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
            )
          }
          Text(
            text = "Prev / Next",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
              .clickable { onOpenDetail() }
              .padding(horizontal = 4.dp)
          )
          IconButton(
            onClick = onNext,
            enabled = hasNext,
            modifier = Modifier
              .size(34.dp)
              .testTag("card_next_hadith_${hadith.hadithNumber}")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.NavigateNext,
              contentDescription = "Next Hadith",
              tint = if (hasNext) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          IconButton(onClick = onAddNote, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.NoteAdd, contentDescription = "Note", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          IconButton(onClick = onBookmark, modifier = Modifier.size(36.dp)) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = GoldPrimary
            )
          }
        }
      }
    }
  }
}
