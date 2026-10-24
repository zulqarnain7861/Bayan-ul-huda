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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.ui.theme.UrduNastaliqFontFamily
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
  var selectedTraditionFilter by remember {
    mutableStateOf(
      if (ExtendedHadithDataProvider.isShiaCollection(selectedCollectionKey)) "SHIA" else "SUNNI"
    )
  }
  var currentPage by remember(selectedCollectionKey, selectedBookNumber, searchQuery, selectedTraditionFilter) { mutableStateOf(1) }
  var pageSize by remember { mutableStateOf(10) }
  var showEntryTimeBadges by remember { mutableStateOf(true) }
  var sortByEntryTimeDesc by remember { mutableStateOf(false) }
  var showVerifiedSourcesSheet by remember { mutableStateOf(false) }
  var userRecalledSourceInput by remember { mutableStateOf("") }

  val activeCollectionMeta = remember(selectedCollectionKey) {
    ExtendedHadithDataProvider.primaryCollections.find { it.key == selectedCollectionKey }
  }
  val isShiaActive = selectedTraditionFilter == "SHIA" || ExtendedHadithDataProvider.isShiaCollection(selectedCollectionKey)

  LaunchedEffect(selectedCollectionKey) {
    val isShiaKey = ExtendedHadithDataProvider.isShiaCollection(selectedCollectionKey)
    if (isShiaKey && selectedTraditionFilter == "SUNNI") {
      selectedTraditionFilter = "SHIA"
    } else if (!isShiaKey && selectedTraditionFilter == "SHIA") {
      selectedTraditionFilter = "SUNNI"
    }
    if (isShiaKey && selectedEditionFilterMode == HadithEditionFilterMode.DARUSSALAM) {
      onSelectEditionFilterMode(HadithEditionFilterMode.ALL_EDITIONS)
    }
  }

  // Keep tradition filter aligned if user externally selects a collection
  val visibleCollectionsMeta = remember(selectedTraditionFilter) {
    when (selectedTraditionFilter) {
      "SUNNI" -> ExtendedHadithDataProvider.sunniCollections
      "SHIA" -> ExtendedHadithDataProvider.shiaCollections
      else -> ExtendedHadithDataProvider.primaryCollections
    }
  }

  val collections = remember(visibleCollectionsMeta) {
    visibleCollectionsMeta.map {
      Triple(it.key, it.nameEnglish, it.nameArabic)
    }
  }

  val totalBooksInCollection = (activeCollectionMeta?.totalBooksCount ?: 1).coerceAtLeast(1)

  val filteredHadiths = remember(
    hadiths,
    searchQuery,
    selectedEditionFilterMode,
    selectedBookNumber,
    selectedCollectionKey,
    selectedTraditionFilter
  ) {
    // Merge Room collection items with offline verified catalog for the active collection so zero items are missing
    val baseCollectionItems = ExtendedHadithDataProvider.searchHadithsSmartOffline(
      localCollectionHadiths = hadiths,
      rawQuery = "",
      activeCollectionKey = selectedCollectionKey,
      traditionFilter = selectedTraditionFilter,
      includeCrossCollectionMatches = false
    )

    val bookFiltered = if (selectedBookNumber <= 0) {
      baseCollectionItems
    } else {
      baseCollectionItems.filter { it.bookNumber == selectedBookNumber }.ifEmpty { baseCollectionItems }
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
      ExtendedHadithDataProvider.searchHadithsSmartOffline(
        localCollectionHadiths = modeFiltered,
        rawQuery = searchQuery,
        activeCollectionKey = selectedCollectionKey,
        traditionFilter = selectedTraditionFilter,
        includeCrossCollectionMatches = true
      )
    }
  }.let { list ->
    if (sortByEntryTimeDesc) list.reversed() else list
  }

  val totalPages = remember(filteredHadiths.size, pageSize) {
    if (pageSize <= 0 || filteredHadiths.isEmpty()) 1
    else ((filteredHadiths.size + pageSize - 1) / pageSize).coerceAtLeast(1)
  }
  val safeCurrentPage = currentPage.coerceIn(1, totalPages)
  val paginatedHadiths = remember(filteredHadiths, safeCurrentPage, pageSize) {
    if (pageSize <= 0) filteredHadiths
    else {
      val start = ((safeCurrentPage - 1) * pageSize).coerceAtLeast(0)
      val end = (start + pageSize).coerceAtMost(filteredHadiths.size)
      if (start < end) filteredHadiths.subList(start, end) else filteredHadiths
    }
  }

  fun triggerNumberSearch(rawInput: String = hadithNumberInput) {
    val trimmed = rawInput.trim()
    val num = trimmed.filter { it.isDigit() }.toIntOrNull()
    if (num == null || num <= 0) {
      Toast.makeText(context, "Enter a valid Hadith number (e.g. 1, 2, 5, 201, 5027)", Toast.LENGTH_SHORT).show()
      return
    }
    val localIdx = filteredHadiths.indexOfFirst {
      ExtendedHadithDataProvider.matchesExactHadithNumber(it, num)
    }
    if (localIdx >= 0) {
      val matched = filteredHadiths[localIdx]
      onShowHadithDetail(matched)
      return
    }
    val smartOfflineMatch = ExtendedHadithDataProvider.searchHadithsSmartOffline(
      localCollectionHadiths = hadiths,
      rawQuery = num.toString(),
      activeCollectionKey = selectedCollectionKey,
      traditionFilter = selectedTraditionFilter,
      includeCrossCollectionMatches = false
    ).firstOrNull()
    if (smartOfflineMatch != null) {
      onShowHadithDetail(smartOfflineMatch)
    } else {
      onLookupHadithByNumber(selectedCollectionKey, num)
    }
  }

  // Clickable Edition & Reference Mode Selector Row (In Shia mode, NEVER display Darussalam)
  val visibleEditionModes = remember(isShiaActive) {
    if (isShiaActive) {
      HadithEditionFilterMode.entries.filter { it != HadithEditionFilterMode.DARUSSALAM }
    } else {
      HadithEditionFilterMode.entries
    }
  }

  Column(modifier = modifier.fillMaxSize()) {
    // 1. Compact Single-Line Entry Points: Sunni | Shia | Both (LazyRow prevents vertical letter wrapping on small screens)
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      item {
        FilterChip(
          selected = selectedTraditionFilter == "SUNNI",
          onClick = {
            selectedTraditionFilter = "SUNNI"
            selectedBookNumber = 0
            if (activeCollectionMeta?.tradition != "SUNNI") {
              ExtendedHadithDataProvider.sunniCollections.firstOrNull()?.let { onSelectCollection(it.key) }
            }
          },
          label = {
            Text(
              text = "Sunni Collections",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              softWrap = false
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = EmeraldPrimary,
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.height(32.dp).testTag("hadith_library_sunni_button")
        )
      }

      item {
        FilterChip(
          selected = selectedTraditionFilter == "SHIA",
          onClick = {
            selectedTraditionFilter = "SHIA"
            selectedBookNumber = 0
            if (selectedEditionFilterMode == HadithEditionFilterMode.DARUSSALAM) {
              onSelectEditionFilterMode(HadithEditionFilterMode.ALL_EDITIONS)
            }
            if (activeCollectionMeta?.tradition != "SHIA") {
              ExtendedHadithDataProvider.shiaCollections.firstOrNull()?.let { onSelectCollection(it.key) }
            }
          },
          label = {
            Text(
              text = "Shia Collections",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              softWrap = false
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GoldPrimary,
            selectedLabelColor = Color.Black
          ),
          modifier = Modifier.height(32.dp).testTag("hadith_library_shia_button")
        )
      }

      item {
        FilterChip(
          selected = selectedTraditionFilter == "BOTH",
          onClick = {
            selectedTraditionFilter = "BOTH"
            selectedBookNumber = 0
          },
          label = {
            Text(
              text = "All Libraries",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              softWrap = false
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
          ),
          modifier = Modifier.height(32.dp).testTag("hadith_library_both_button")
        )
      }
    }

    // 2. Hadith Collections Tabs for Selected Library
    ScrollableTabRow(
      selectedTabIndex = collections.indexOfFirst { it.first == selectedCollectionKey }.coerceAtLeast(0),
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.primary,
      edgePadding = 8.dp
    ) {
      collections.forEach { (key, englishTitle, arabicTitle) ->
        val isSelected = selectedCollectionKey == key
        Tab(
          selected = isSelected,
          onClick = {
            selectedBookNumber = 0
            onSelectCollection(key)
          },
          modifier = Modifier.testTag("hadith_tab_$key"),
          text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = englishTitle,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = arabicTitle,
                fontFamily = ScheherazadeFontFamily,
                fontSize = 12.sp,
                maxLines = 1,
                color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        )
      }
    }

    // 3. Compact Search & Hadith Number Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
          .weight(1.35f)
          .testTag("hadith_library_search_input"),
        placeholder = {
          Text(
            text = if (isShiaActive) "Search any hadees..." else "Search any Hadith, topic, narrator, or #...",
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(28.dp)) {
              Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
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
        shape = RoundedCornerShape(10.dp)
      )

      OutlinedTextField(
        value = hadithNumberInput,
        onValueChange = { hadithNumberInput = it.filter { ch -> ch.isDigit() } },
        modifier = Modifier
          .weight(0.75f)
          .testTag("hadith_number_search_input"),
        placeholder = {
          Text(
            text = if (isShiaActive) "Hadith # (1, 201)" else "Hadith # (5027)",
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        },
        leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, modifier = Modifier.size(16.dp)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Number,
          imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = { triggerNumberSearch() }),
        shape = RoundedCornerShape(10.dp)
      )

      Button(
        onClick = { triggerNumberSearch() },
        enabled = !isHadithLookupLoading,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        modifier = Modifier
          .height(48.dp)
          .testTag("hadith_number_lookup_button")
      ) {
        if (isHadithLookupLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = Color.White
          )
        } else {
          Text("Find #", fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
        }
      }
    }

    // 4. Scrollable Hadith List + Compact Metadata/Filter Header Items (No vertical crowding!)
    LazyColumn(
      state = listState,
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
      verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
      // Compact Header Item inside LazyColumn so Hadith cards have full screen space
      item(key = "hadith_compact_header_controls") {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          // Tradition, Sub-Tradition & Scholarly Attribution Banner
          activeCollectionMeta?.let { meta ->
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (meta.tradition == "SHIA") GoldPrimary.copy(alpha = 0.14f) else EmeraldPrimary.copy(alpha = 0.10f),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("hadith_tradition_attribution_banner")
            ) {
              Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
                Text(
                  text = "${meta.tradition} HADITH LIBRARY • ${meta.subTradition}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (meta.tradition == "SHIA") GoldPrimary else EmeraldPrimary
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "Compiler: ${meta.compiler} (${meta.deathHijri}) • ${meta.coverageStatusBadge}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }

          // Action Row: Offline Library | Official Sources & Links | Retry
          Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            FilledTonalButton(
              onClick = onOpenOfflineDownloads,
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier
                .weight(1f)
                .height(32.dp)
                .testTag("hadith_download_offline_button")
            ) {
              Text("Offline Library", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            OutlinedButton(
              onClick = { showVerifiedSourcesSheet = true },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier
                .weight(1.2f)
                .height(32.dp)
                .testTag("hadith_verified_sources_links_button")
            ) {
              Text("Official Sources & Links", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            OutlinedButton(
              onClick = {
                onSelectCollection(selectedCollectionKey)
                if (selectedBookNumber > 0) {
                  onLoadBookSection(selectedBookNumber)
                }
                Toast.makeText(context, "Retried and verified ${activeCollectionMeta?.nameEnglish ?: selectedCollectionKey}", Toast.LENGTH_SHORT).show()
              },
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
              modifier = Modifier
                .height(32.dp)
                .testTag("hadith_retry_load_button")
            ) {
              Text("Retry", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
          }

          // Book / Kitab Browser Row (All Books + Book 1 .. Book N)
          LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            item {
              FilterChip(
                selected = selectedBookNumber == 0,
                onClick = { selectedBookNumber = 0 },
                label = { Text("All Books ($totalBooksInCollection)", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = GoldPrimary.copy(alpha = 0.22f),
                  selectedLabelColor = EmeraldPrimary
                ),
                modifier = Modifier.height(30.dp).testTag("hadith_book_chip_all")
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
                label = { Text("Book $bookNum", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = EmeraldPrimary,
                  selectedLabelColor = Color.White
                ),
                modifier = Modifier.height(30.dp).testTag("hadith_book_chip_$bookNum")
              )
            }
          }

          // Edition & Entry Time Chips Row
          LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            items(visibleEditionModes.size) { idx ->
              val mode = visibleEditionModes[idx]
              val chipLabel = if (isShiaActive && mode == HadithEditionFilterMode.SAHIH_INTERNATIONAL_QURAN) {
                "Linked Quran Verses"
              } else {
                mode.label
              }
              FilterChip(
                selected = selectedEditionFilterMode == mode,
                onClick = { onSelectEditionFilterMode(mode) },
                label = { Text(chipLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = EmeraldPrimary,
                  selectedLabelColor = Color.White
                ),
                modifier = Modifier.height(30.dp).testTag("hadith_edition_chip_${mode.name.lowercase()}")
              )
            }
            item {
              FilterChip(
                selected = showEntryTimeBadges,
                onClick = {
                  if (showEntryTimeBadges) {
                    sortByEntryTimeDesc = !sortByEntryTimeDesc
                  } else {
                    showEntryTimeBadges = true
                  }
                },
                label = {
                  Text(
                    text = if (sortByEntryTimeDesc) "Entry Time (Latest First)" else "Entry Time: ON",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = GoldPrimary.copy(alpha = 0.22f),
                  selectedLabelColor = EmeraldPrimary
                ),
                modifier = Modifier.height(30.dp).testTag("hadith_entry_time_toggle_chip")
              )
            }
          }

          // If user typed a Hadith number in the filter box that isn't cached yet, offer 1-tap live fetch
          val typedNumberInFilter = searchQuery.trim().filter { it.isDigit() }.toIntOrNull()
          if (filteredHadiths.isEmpty() && typedNumberInFilter != null && typedNumberInFilter > 0) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = EmeraldPrimary.copy(alpha = 0.1f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (isShiaActive) {
                    "Fetch Hadith #$typedNumberInFilter from Verified Shia Hadith API (Thaqalayn / Al-Islam)?"
                  } else {
                    "Fetch Hadith #$typedNumberInFilter from Verified Sunnah.com / Hadith API?"
                  },
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                  modifier = Modifier.weight(1f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Button(
                    onClick = { onLookupHadithByNumber(selectedCollectionKey, typedNumberInFilter) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                  ) {
                    Text("Fetch #$typedNumberInFilter", fontSize = 11.sp)
                  }
                  OutlinedButton(
                    onClick = { onLookupHadithByNumber(selectedCollectionKey, typedNumberInFilter) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("hadith_number_retry_button")
                  ) {
                    Text("Retry", fontSize = 11.sp)
                  }
                }
              }
            }
          }

          // Collection stats, Tradition-Appropriate Grading Info & Reader Mode button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (searchQuery.isNotBlank()) {
                  "${filteredHadiths.size} Search Matches (${if (isShiaActive) "Shia Library" else "Hadith Library"})"
                } else {
                  "${filteredHadiths.size} Cached Locally • ${activeCollectionMeta?.totalHadithsInfo ?: "Verified Collection"}"
                },
                style = MaterialTheme.typography.labelMedium.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = if (isShiaActive) {
                  "Shia Scholarly Grading (Mir'at al-'Uqul / Al-Saduq / Al-Tusi) • In-Book # • Quran Refs"
                } else {
                  "Separate Edition Labels: Darussalam # • In-Book # • Sahih International (Quran)"
                },
                style = MaterialTheme.typography.labelSmall.copy(
                  color = GoldPrimary,
                  fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.testTag("hadith_grading_system_subtitle")
              )
            }

            if (filteredHadiths.isNotEmpty()) {
              FilledTonalButton(
                onClick = { onShowHadithDetail(paginatedHadiths.first()) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(30.dp).testTag("open_hadith_reader_button")
              ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reader Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
              }
            }
          }

          // Fast, Reliable Pagination Bar
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("hadith_fast_pagination_bar")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = { if (safeCurrentPage > 1) currentPage = safeCurrentPage - 1 },
                enabled = safeCurrentPage > 1,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(28.dp).testTag("hadith_pagination_prev_button")
              ) {
                Text("Prev Page", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
              }

              Text(
                text = "Page $safeCurrentPage/$totalPages (${paginatedHadiths.size} of ${filteredHadiths.size})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                modifier = Modifier.testTag("hadith_pagination_status_label")
              )

              Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                  onClick = {
                    pageSize = when (pageSize) {
                      10 -> 25
                      25 -> 50
                      else -> 10
                    }
                    currentPage = 1
                  },
                  contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                  modifier = Modifier.height(28.dp).testTag("hadith_page_size_toggle_button")
                ) {
                  Text("${pageSize}/pg", fontSize = 10.sp, maxLines = 1)
                }

                OutlinedButton(
                  onClick = { if (safeCurrentPage < totalPages) currentPage = safeCurrentPage + 1 },
                  enabled = safeCurrentPage < totalPages,
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                  modifier = Modifier.height(28.dp).testTag("hadith_pagination_next_button")
                ) {
                  Text("Next Page", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
              }
            }
          }
        }
      }

      itemsIndexed(
        items = paginatedHadiths,
        key = { _, it -> "${it.collectionKey}_${it.hadithNumber}" }
      ) { index, hadith ->
        val isBookmarked = bookmarks.any { it.itemKey == "hadith_${hadith.collectionKey}_${hadith.hadithNumber}" }
        val primaryEditionLabel = ExtendedHadithDataProvider.formatPrimaryEditionBadge(hadith)
        val inBookEditionLabel = ExtendedHadithDataProvider.formatInBookBadge(hadith)

        HadithCardItem(
          hadith = hadith,
          isBookmarked = isBookmarked,
          showEntryTimeBadge = showEntryTimeBadges,
          hasPrevious = index > 0,
          hasNext = index < paginatedHadiths.lastIndex,
          onPrevious = {
            if (index > 0) {
              coroutineScope.launch { listState.animateScrollToItem(index - 1) }
            }
          },
          onNext = {
            if (index < paginatedHadiths.lastIndex) {
              coroutineScope.launch { listState.animateScrollToItem(index + 1) }
            }
          },
          onOpenDetail = { onShowHadithDetail(hadith) },
          onClickDarussalamBadge = {
            hadithNumberInput = hadith.hadithNumber.toString()
            if (!ExtendedHadithDataProvider.isShiaCollection(hadith.collectionKey)) {
              onSelectEditionFilterMode(HadithEditionFilterMode.DARUSSALAM)
            }
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
            val text = buildString {
              append(hadith.arabicText)
              if (hadith.transliteration.isNotBlank()) {
                append("\n\nTransliteration: ").append(hadith.transliteration)
              }
              append("\n\n").append(ExtendedHadithDataProvider.formatEnglishTranslation(hadith.englishText))
              append("\n\n").append(ExtendedHadithDataProvider.formatUrduTranslation(hadith.urduText))
              append("\n\n[${hadith.reference} | $primaryEditionLabel | $inBookEditionLabel - ${ExtendedHadithDataProvider.formatGradingMethodologyLabel(hadith)}]")
            }
            clipboard.setPrimaryClip(ClipData.newPlainText("Hadith", text))
            Toast.makeText(context, "Hadith copied to clipboard", Toast.LENGTH_SHORT).show()
          },
          onShare = {
            val sendIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(
                Intent.EXTRA_TEXT,
                "${hadith.arabicText}\n\n${ExtendedHadithDataProvider.formatEnglishTranslation(hadith.englishText)}\n\n${ExtendedHadithDataProvider.formatUrduTranslation(hadith.urduText)}\n\n[${hadith.reference} | $primaryEditionLabel | $inBookEditionLabel - ${ExtendedHadithDataProvider.formatGradingMethodologyLabel(hadith)}]\nShared via Bayan al-Huda"
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
    val isShiaDetail = ExtendedHadithDataProvider.isShiaCollection(currentHadith.collectionKey)
    val primaryEditionLabel = ExtendedHadithDataProvider.formatPrimaryEditionBadge(currentHadith)
    val inBookLabel = ExtendedHadithDataProvider.formatInBookBadge(currentHadith)
    var modalSearchQuery by remember { mutableStateOf("") }

    val modalSearchResults = remember(modalSearchQuery, filteredHadiths) {
      val q = modalSearchQuery.trim()
      if (q.isBlank()) emptyList()
      else {
        filteredHadiths.filter { h ->
          h.hadithNumber.toString() == q ||
            h.reference.contains(q, ignoreCase = true) ||
            h.arabicText.contains(q) ||
            h.englishText.contains(q, ignoreCase = true) ||
            h.urduText.contains(q) ||
            h.narrator.contains(q, ignoreCase = true) ||
            h.bookName.contains(q, ignoreCase = true)
        }
      }
    }

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
        // Top Header with Close button (Two rows to prevent horizontal squishing on narrow screens)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = currentHadith.reference,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.weight(1f)
          )
          IconButton(onClick = onCloseHadithDetail) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
              text = "$primaryEditionLabel • $inBookLabel",
              style = MaterialTheme.typography.labelSmall.copy(
                color = GoldPrimary,
                fontWeight = FontWeight.Bold
              )
            )
            Text(
              text = "Entry Time: ${ExtendedHadithDataProvider.getEntryTimestampForHadith(currentHadith)}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          SahihGradeBadge(grade = currentHadith.grade.replace(Regex("Darussalam", RegexOption.IGNORE_CASE), if (isShiaDetail) "Imami Rijal" else "Darussalam"))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // In-Reader Hadith Search Bar
        OutlinedTextField(
          value = modalSearchQuery,
          onValueChange = { modalSearchQuery = it },
          placeholder = { Text(if (isShiaDetail) "Search any hadees in this collection..." else "Search any Hadith in this collection by #, text, or narrator...", fontSize = 11.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
          trailingIcon = {
            if (modalSearchQuery.isNotEmpty()) {
              IconButton(onClick = { modalSearchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("hadith_reader_modal_search_input")
        )

        if (modalSearchQuery.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(6.dp)) {
              Text(
                text = "${modalSearchResults.size} matching narrations:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
              )
              if (modalSearchResults.isEmpty()) {
                Text(
                  text = "No narrations matching '$modalSearchQuery'. Try a number or different word.",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                  modifier = Modifier.padding(vertical = 4.dp)
                )
              } else {
                modalSearchResults.take(5).forEach { matchHadith ->
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 2.dp)
                      .clickable {
                        onShowHadithDetail(matchHadith)
                        modalSearchQuery = ""
                      }
                      .testTag("modal_search_match_${matchHadith.hadithNumber}")
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "${matchHadith.reference} (Hadith #${matchHadith.hadithNumber})",
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                          text = matchHadith.englishText.take(65) + "...",
                          style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                          maxLines = 1
                        )
                      }
                      Text("Jump", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    }
                  }
                }
              }
            }
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
        Text(
          text = ExtendedHadithDataProvider.formatGradingMethodologyLabel(currentHadith),
          style = MaterialTheme.typography.labelSmall.copy(
            color = EmeraldPrimary,
            fontWeight = FontWeight.SemiBold
          )
        )

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
          text = ExtendedHadithDataProvider.formatEnglishTranslation(currentHadith.englishText),
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
          text = ExtendedHadithDataProvider.formatUrduTranslation(currentHadith.urduText),
          style = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = UrduNastaliqFontFamily,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Right,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          modifier = Modifier.fillMaxWidth()
        )

        // Clickable Linked Quran Verses
        if (currentHadith.linkedQuranVerses.isNotBlank()) {
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = if (isShiaDetail) {
              "Linked Quranic Verses (Tap to Preview Verse):"
            } else {
              "Linked Quranic Verses (Tap for Sahih International Translation):"
            },
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
                  text = if (isShiaDetail) "Quran $verseRef" else "Quran $verseRef (Sahih International)",
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

  // Verified Official Source Links & User Recall Sheet (Sunni & Shia Libraries)
  if (showVerifiedSourcesSheet) {
    val sourcesSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val linksToShow = remember(selectedTraditionFilter) {
      when (selectedTraditionFilter) {
        "SUNNI" -> ExtendedHadithDataProvider.verifiedOfficialSourceLinks.filter { it.tradition == "SUNNI" || it.tradition == "SHARED" }
        "SHIA" -> ExtendedHadithDataProvider.verifiedOfficialSourceLinks.filter { it.tradition == "SHIA" || it.tradition == "SHARED" }
        else -> ExtendedHadithDataProvider.verifiedOfficialSourceLinks
      }
    }

    ModalBottomSheet(
      onDismissRequest = { showVerifiedSourcesSheet = false },
      sheetState = sourcesSheetState,
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 10.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Verified Sunni & Shia Sources, APIs & Direct Links",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )
            Text(
              text = "Labelled by Tradition • Collection • Language • Source Type • Grading System",
              style = MaterialTheme.typography.labelSmall.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
            )
          }
          IconButton(onClick = { showVerifiedSourcesSheet = false }) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        // Recall & Restore Previously Used Sources Card
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = GoldPrimary.copy(alpha = 0.14f),
          modifier = Modifier.fillMaxWidth().testTag("recall_previous_sources_card")
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "Recall & Restore Previously Used Sources, Repositories, or APIs",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
            )
            Text(
              text = "Previously used sources already restored & verified: Sunnah.com API, Fawazahmed0 Hadith API CDN, Thaqalayn.net REST API, Al-Islam.org (AELD), Al-Mizan.org, ShiaQuran.in, Al-Maktabah al-Shi'iyyah, Quran.com v4 API, and Tanzil.net. Do you recall any other repository, API, book, or reference used previously?",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = userRecalledSourceInput,
                onValueChange = { userRecalledSourceInput = it },
                placeholder = { Text("Enter recalled source URL, API, or book title...", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("recall_source_input_field")
              )
              Button(
                onClick = {
                  if (userRecalledSourceInput.isNotBlank()) {
                    onAddNote("Recalled Source Attribution: ${userRecalledSourceInput.trim()}")
                    Toast.makeText(context, "Saved recalled source for verification & attribution", Toast.LENGTH_SHORT).show()
                    userRecalledSourceInput = ""
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("submit_recalled_source_button")
              ) {
                Text("Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        linksToShow.forEach { link ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth().testTag("verified_source_link_${link.id}")
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${link.tradition} • ${link.sourceType}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (link.tradition == "SHIA") GoldPrimary else EmeraldPrimary
                  )
                )
                Text(
                  text = link.languages,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                  )
                )
              }
              Text(
                text = link.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Collections: ${link.collectionName}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
              )
              Text(
                text = "Grading / Authentication: ${link.gradingMethodology}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = EmeraldPrimary)
              )
              Text(
                text = "Official URL: ${link.officialUrl}" + if (link.apiOrRepoUrl.isNotBlank()) " • API/Repo: ${link.apiOrRepoUrl}" else "",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
              Text(
                text = "Entry Time: ${link.entryTimestamp}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = GoldPrimary)
              )
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                  onClick = {
                    runCatching {
                      val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(link.officialUrl))
                      context.startActivity(intent)
                    }
                  },
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  modifier = Modifier.height(30.dp)
                ) {
                  Text("Open Official Page", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                if (link.apiOrRepoUrl.isNotBlank()) {
                  OutlinedButton(
                    onClick = {
                      runCatching {
                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(link.apiOrRepoUrl))
                        context.startActivity(intent)
                      }
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                  ) {
                    Text("Open API / Repo", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HadithCardItem(
  hadith: HadithEntity,
  isBookmarked: Boolean,
  showEntryTimeBadge: Boolean = true,
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
  val isShia = ExtendedHadithDataProvider.isShiaCollection(hadith.collectionKey)
  val primaryEditionText = ExtendedHadithDataProvider.formatPrimaryEditionBadge(hadith)
  val inBookText = ExtendedHadithDataProvider.formatInBookBadge(hadith)
  val displayGrade = hadith.grade
    .replace(Regex("Darussalam", RegexOption.IGNORE_CASE), if (isShia) "Imami Rijal" else "Darussalam")
    .ifBlank { "Not graded in this source" }
  val entryTimestamp = ExtendedHadithDataProvider.getEntryTimestampForHadith(hadith)

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onOpenDetail() }
      .testTag("hadith_card_${hadith.collectionKey}_${hadith.hadithNumber}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      // Header: Reference, Chapter/Book, and Authenticity Grade
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${hadith.collectionName} • ${hadith.reference}",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          if (hadith.bookName.isNotEmpty()) {
            Text(
              text = if (hadith.chapterName.isNotBlank()) "${hadith.bookName} — ${hadith.chapterName}" else hadith.bookName,
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
          Text(
            text = ExtendedHadithDataProvider.formatGradingMethodologyLabel(hadith),
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = EmeraldPrimary
            )
          )
          if (showEntryTimeBadge) {
            Text(
              text = "Entry Time: $entryTimestamp",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.testTag("hadith_entry_time_badge_${hadith.collectionKey}_${hadith.hadithNumber}")
            )
          }
        }
        SahihGradeBadge(grade = displayGrade, modifier = Modifier.padding(start = 4.dp))
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Clickable Separate Dual Edition Numbering Badges on Top (Never show Darussalam in Shia mode)
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
            text = primaryEditionText,
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
            text = inBookText,
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

      Spacer(modifier = Modifier.height(6.dp))

      // Arabic Hadith Text in Amiri Classical Naskh Font
      Text(
        text = hadith.arabicText.ifBlank { "النص العربي قريبا" },
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

      Spacer(modifier = Modifier.height(8.dp))

      // English Translation (displays "English Translation Coming Soon" if unavailable)
      Text(
        text = ExtendedHadithDataProvider.formatEnglishTranslation(hadith.englishText),
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 14.sp,
          lineHeight = 21.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      )

      Spacer(modifier = Modifier.height(6.dp))
      // Urdu Translation (displays "Urdu Translation Coming Soon" if unavailable)
      Text(
        text = ExtendedHadithDataProvider.formatUrduTranslation(hadith.urduText),
        style = MaterialTheme.typography.bodyMedium.copy(
          fontFamily = UrduNastaliqFontFamily,
          fontSize = 14.sp,
          lineHeight = 22.sp,
          textAlign = TextAlign.Right,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
      )

      // Clickable Linked Quran Verses
      if (hadith.linkedQuranVerses.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = if (isShia) "Linked Quran Refs:" else "Sahih International Quran Refs:",
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
                text = if (isShia) "Quran $vRef" else "Quran $vRef (Sahih Intl)",
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
