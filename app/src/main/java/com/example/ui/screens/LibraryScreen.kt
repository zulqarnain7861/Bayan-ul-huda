package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.entities.BookmarkEntity
import com.example.data.local.entities.NoteEntity
import com.example.data.model.IslamicBooksCatalog
import com.example.data.model.IslamicClassicalBook
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ScheherazadeFontFamily
import com.example.ui.theme.UrduNastaliqFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LibraryScreen(
  bookmarks: List<BookmarkEntity>,
  notes: List<NoteEntity>,
  onRemoveBookmark: (String) -> Unit,
  onSaveNote: (String, String, String) -> Unit,
  onDeleteNote: (Long) -> Unit,
  downloadManager: com.example.data.download.DownloadManager? = null,
  currentThemeMode: com.example.data.prayer.AppThemeMode = com.example.data.prayer.AppThemeMode.LIGHT,
  onSelectThemeMode: (com.example.data.prayer.AppThemeMode) -> Unit = {},
  onPlayDownloadedAudio: (Int, Int, String) -> Unit = { _, _, _ -> },
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) }
  var showAddNoteDialog by remember { mutableStateOf(false) }
  var initialNoteReference by remember { mutableStateOf("") }

  // Classical Islamic Books state (32 Sunni + 6 Shia = 38 verified books)
  var selectedTraditionFilter by remember { mutableStateOf("BOTH") } // "SUNNI", "SHIA", "BOTH"
  var selectedCategory by remember { mutableStateOf("All") }
  var bookSearchQuery by remember { mutableStateOf("") }
  var showEntryTimeOnCards by remember { mutableStateOf(true) }
  var sortByEntryTimeDesc by remember { mutableStateOf(false) }
  var currentPageIndex by remember { mutableIntStateOf(0) }
  var itemsPerPage by remember { mutableIntStateOf(10) }
  var retryTriggerCount by remember { mutableIntStateOf(0) }
  var openedBook by remember { mutableStateOf<IslamicClassicalBook?>(null) }
  var selectedChapterIndex by remember { mutableIntStateOf(0) }

  // If a Classical Book is open in full reader view
  openedBook?.let { book ->
    BackHandler {
      openedBook = null
    }
    IslamicBookReaderView(
      book = book,
      selectedChapterIndex = selectedChapterIndex.coerceIn(0, (book.chapters.size - 1).coerceAtLeast(0)),
      onSelectChapter = { selectedChapterIndex = it },
      onJumpToBookAndChapter = { targetBook, targetChapterIdx ->
        openedBook = targetBook
        selectedChapterIndex = targetChapterIdx.coerceIn(0, (targetBook.chapters.size - 1).coerceAtLeast(0))
      },
      onClose = { openedBook = null },
      onNavigateBook = { delta ->
        val allBooks = IslamicBooksCatalog.allSunniAndShiaBooks
        val idx = allBooks.indexOfFirst { it.id == book.id }
        val nextIdx = idx + delta
        if (nextIdx in allBooks.indices) {
          openedBook = allBooks[nextIdx]
          selectedChapterIndex = 0
        }
      },
      onAddBookNote = { ref ->
        initialNoteReference = ref
        showAddNoteDialog = true
      }
    )

    if (showAddNoteDialog) {
      AddNoteDialog(
        initialReference = initialNoteReference,
        onDismiss = { showAddNoteDialog = false },
        onSave = { ref, title, content ->
          onSaveNote(ref, title, content)
          showAddNoteDialog = false
          Toast.makeText(context, "Study note saved", Toast.LENGTH_SHORT).show()
        }
      )
    }
    return
  }

  val filteredBooks = remember(selectedTraditionFilter, selectedCategory, bookSearchQuery, sortByEntryTimeDesc, retryTriggerCount) {
    val base = IslamicBooksCatalog.getBooksByTradition(selectedTraditionFilter).filter { book ->
      val matchesCat = selectedCategory == "All" ||
        selectedCategory.startsWith("All ") ||
        book.category.equals(selectedCategory, ignoreCase = true)
      val q = bookSearchQuery.trim()
      val matchesQuery = q.isEmpty() ||
        book.titleEnglish.contains(q, ignoreCase = true) ||
        book.titleArabic.contains(q) ||
        book.titleUrdu.contains(q) ||
        book.author.contains(q, ignoreCase = true) ||
        book.summary.contains(q, ignoreCase = true) ||
        book.numberingSystem.contains(q, ignoreCase = true) ||
        book.number.toString() == q
      matchesCat && matchesQuery
    }
    if (sortByEntryTimeDesc) {
      base.sortedByDescending { it.number }
    } else {
      base.sortedBy { it.number }
    }
  }

  val totalPages = remember(filteredBooks.size, itemsPerPage) {
    ((filteredBooks.size + itemsPerPage - 1) / itemsPerPage).coerceAtLeast(1)
  }
  val safePageIndex = currentPageIndex.coerceIn(0, (totalPages - 1).coerceAtLeast(0))
  val paginatedBooks = remember(filteredBooks, safePageIndex, itemsPerPage) {
    val start = safePageIndex * itemsPerPage
    if (start >= filteredBooks.size) emptyList()
    else filteredBooks.subList(start, (start + itemsPerPage).coerceAtMost(filteredBooks.size))
  }

  Box(modifier = modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          modifier = Modifier.testTag("library_tab_islamic_books"),
          text = {
            Text(
              text = "Islamic Books (${IslamicBooksCatalog.allSunniAndShiaBooks.size})",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          modifier = Modifier.testTag("library_tab_bookmarks"),
          text = {
            Text(
              text = "Bookmarks (${bookmarks.size})",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          modifier = Modifier.testTag("library_tab_notes"),
          text = {
            Text(
              text = "Notes (${notes.size})",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        )
        Tab(
          selected = selectedTab == 3,
          onClick = { selectedTab = 3 },
          modifier = Modifier.testTag("library_tab_offline_downloads"),
          text = {
            Text(
              text = "Offline Library",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        )
      }

      when (selectedTab) {
        0 -> {
          // Classical & Authentic Sunni + Shia Islamic Books Tab
          Column(modifier = Modifier.fillMaxSize()) {
            // Sunni / Shia / Both Tradition Filter + Entry Time + Retry Bar
            LazyRow(
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              item {
                FilterChip(
                  selected = selectedTraditionFilter == "SUNNI",
                  onClick = {
                    selectedTraditionFilter = "SUNNI"
                    selectedCategory = "All"
                    currentPageIndex = 0
                  },
                  label = { Text("Sunni Books (32)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldPrimary,
                    selectedLabelColor = Color.White
                  ),
                  modifier = Modifier.testTag("library_books_sunni_chip")
                )
              }
              item {
                FilterChip(
                  selected = selectedTraditionFilter == "SHIA",
                  onClick = {
                    selectedTraditionFilter = "SHIA"
                    selectedCategory = "All"
                    currentPageIndex = 0
                  },
                  label = { Text("Shia Books (6)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldPrimary,
                    selectedLabelColor = Color.White
                  ),
                  modifier = Modifier.testTag("library_books_shia_chip")
                )
              }
              item {
                FilterChip(
                  selected = selectedTraditionFilter == "BOTH",
                  onClick = {
                    selectedTraditionFilter = "BOTH"
                    selectedCategory = "All"
                    currentPageIndex = 0
                  },
                  label = { Text("All Books (38)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                  ),
                  modifier = Modifier.testTag("library_books_both_chip")
                )
              }
              item {
                FilterChip(
                  selected = showEntryTimeOnCards,
                  onClick = {
                    if (!showEntryTimeOnCards) {
                      showEntryTimeOnCards = true
                    } else {
                      sortByEntryTimeDesc = !sortByEntryTimeDesc
                    }
                    currentPageIndex = 0
                  },
                  leadingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp))
                  },
                  label = {
                    Text(
                      text = if (sortByEntryTimeDesc) "Entry Time (Newest)" else "Entry Time",
                      fontSize = 11.sp
                    )
                  },
                  modifier = Modifier.testTag("library_entry_time_toggle_chip")
                )
              }
              item {
                OutlinedButton(
                  onClick = {
                    retryTriggerCount++
                    currentPageIndex = 0
                    Toast.makeText(
                      context,
                      "Verified ${filteredBooks.size} Islamic Books reloaded (Arabic, English & Urdu ready)",
                      Toast.LENGTH_SHORT
                    ).show()
                  },
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  modifier = Modifier
                    .height(32.dp)
                    .testTag("library_retry_books_button")
                ) {
                  Icon(Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Retry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            OutlinedTextField(
              value = bookSearchQuery,
              onValueChange = {
                bookSearchQuery = it
                currentPageIndex = 0
              },
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .testTag("islamic_books_search_input"),
              placeholder = {
                Text(
                  text = if (selectedTraditionFilter == "SHIA") "Search any hadees, book, or imam..." else "Search Sunni & Shia books by title, imam, or number..."
                )
              },
              leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
              trailingIcon = {
                if (bookSearchQuery.isNotEmpty()) {
                  IconButton(onClick = {
                    bookSearchQuery = ""
                    currentPageIndex = 0
                  }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                  }
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )

            LazyRow(
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(IslamicBooksCatalog.categories) { cat ->
                FilterChip(
                  selected = selectedCategory == cat || (selectedCategory == "All" && cat.startsWith("All")),
                  onClick = {
                    selectedCategory = if (cat.startsWith("All")) "All" else cat
                    currentPageIndex = 0
                  },
                  label = { Text(cat, fontSize = 12.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldPrimary,
                    selectedLabelColor = Color.White
                  )
                )
              }
            }

            // Fast Pagination Bar for Books
            Surface(
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .testTag("library_books_pagination_bar")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedButton(
                  onClick = { if (safePageIndex > 0) currentPageIndex = safePageIndex - 1 },
                  enabled = safePageIndex > 0,
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier
                    .height(30.dp)
                    .testTag("library_pagination_prev_button")
                ) {
                  Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Prev Page", modifier = Modifier.size(15.dp))
                  Text("Prev", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "Page ${safePageIndex + 1} of $totalPages • ${filteredBooks.size} Books",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("library_pagination_status_label")
                  )
                  TextButton(
                    onClick = {
                      itemsPerPage = when (itemsPerPage) {
                        10 -> 20
                        20 -> 38
                        else -> 10
                      }
                      currentPageIndex = 0
                    },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    modifier = Modifier
                      .height(20.dp)
                      .testTag("library_page_size_toggle_button")
                  ) {
                    Text(
                      text = "$itemsPerPage/page (Tap to change)",
                      fontSize = 10.sp,
                      color = GoldPrimary
                    )
                  }
                }

                OutlinedButton(
                  onClick = { if (safePageIndex < totalPages - 1) currentPageIndex = safePageIndex + 1 },
                  enabled = safePageIndex < totalPages - 1,
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier
                    .height(30.dp)
                    .testTag("library_pagination_next_button")
                ) {
                  Text("Next", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next Page", modifier = Modifier.size(15.dp))
                }
              }
            }

            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(12.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(paginatedBooks, key = { it.id }) { book ->
                val isShiaBook = book.tradition.equals("SHIA", ignoreCase = true)
                ElevatedCard(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      selectedChapterIndex = 0
                      openedBook = book
                    }
                    .testTag("islamic_book_card_${book.number}"),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                  ),
                  elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.Top
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                      ) {
                        Box(
                          modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isShiaBook) GoldPrimary else EmeraldPrimary),
                          contentAlignment = Alignment.Center
                        ) {
                          Text(
                            text = "${book.number}",
                            style = MaterialTheme.typography.labelLarge.copy(
                              color = if (isShiaBook) Color.White else GoldPrimary,
                              fontWeight = FontWeight.Bold
                            )
                          )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                          Text(
                            text = book.titleEnglish,
                            style = MaterialTheme.typography.titleMedium.copy(
                              fontWeight = FontWeight.Bold,
                              color = MaterialTheme.colorScheme.primary
                            )
                          )
                          Text(
                            text = "${book.author} (${book.eraHijri})",
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = MaterialTheme.colorScheme.secondary,
                              fontWeight = FontWeight.SemiBold
                            )
                          )
                        }
                      }

                      Column(horizontalAlignment = Alignment.End) {
                        Text(
                          text = book.titleArabic,
                          style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = ScheherazadeFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                          )
                        )
                        Text(
                          text = book.titleUrdu,
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = UrduNastaliqFontFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                          )
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isShiaBook) GoldPrimary.copy(alpha = 0.16f) else EmeraldPrimary.copy(alpha = 0.12f)
                      ) {
                        Text(
                          text = "${book.tradition} • ${book.category}",
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isShiaBook) GoldPrimary else EmeraldPrimary
                          )
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldPrimary.copy(alpha = 0.14f)
                      ) {
                        Text(
                          text = if (isShiaBook) {
                            book.numberingSystem.replace("Darussalam", "Classical Reference", ignoreCase = true)
                          } else {
                            book.numberingSystem
                          },
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GoldPrimary
                          )
                        )
                      }
                    }

                    if (showEntryTimeOnCards) {
                      Spacer(modifier = Modifier.height(6.dp))
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "Entry Time: ${book.entryTimestamp}",
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                          ),
                          modifier = Modifier.testTag("book_entry_time_badge_${book.number}")
                        )
                        Text(
                          text = "Arabic • English • Urdu",
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                          )
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                      text = "Methodology: ${book.gradingMethodology}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.secondary
                      )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                      text = book.summary,
                      style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = "${book.chapters.size} Offline Chapters Bundled",
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                          )
                        )
                        Text(
                          text = "Source: ${book.officialSourceUrl}",
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                          ),
                          modifier = Modifier.clickable {
                            runCatching {
                              context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(book.officialSourceUrl)))
                            }
                          }
                        )
                      }

                      Button(
                        onClick = {
                          selectedChapterIndex = 0
                          openedBook = book
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.testTag("open_book_btn_${book.number}")
                      ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Read Book", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        1 -> {
          // Bookmarks Tab
          if (bookmarks.isEmpty()) {
            EmptyStateView(
              icon = Icons.Default.Bookmark,
              title = "No Bookmarks Yet",
              subtitle = "Save inspiring verses and authentic hadiths while reading to easily revisit them here."
            )
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              items(bookmarks, key = { it.itemKey }) { bookmark ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bookmark_item_${bookmark.itemKey}"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (bookmark.itemType == "QURAN") EmeraldPrimary.copy(alpha = 0.15f) else GoldPrimary.copy(alpha = 0.15f)
                      ) {
                        Text(
                          text = bookmark.itemType,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (bookmark.itemType == "QURAN") EmeraldPrimary else GoldPrimary
                          )
                        )
                      }

                      Row {
                        IconButton(
                          onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val text = "${bookmark.title}\n\n${bookmark.arabicSnippet}\n\n${bookmark.subtitle}"
                            clipboard.setPrimaryClip(ClipData.newPlainText("Saved Item", text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                          },
                          modifier = Modifier.size(32.dp)
                        ) {
                          Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(
                          onClick = { onRemoveBookmark(bookmark.itemKey) },
                          modifier = Modifier.size(32.dp)
                        ) {
                          Icon(Icons.Default.BookmarkRemove, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = bookmark.title,
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    if (bookmark.arabicSnippet.isNotEmpty()) {
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(
                        text = bookmark.arabicSnippet,
                        style = MaterialTheme.typography.bodyLarge.copy(
                          fontFamily = AmiriFontFamily,
                          fontSize = 19.sp,
                          lineHeight = 32.sp,
                          textAlign = TextAlign.Right
                        ),
                        modifier = Modifier.fillMaxWidth()
                      )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                      text = bookmark.subtitle,
                      style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    )
                  }
                }
              }
            }
          }
        }

        2 -> {
          // Study Notes Tab
          if (notes.isEmpty()) {
            EmptyStateView(
              icon = Icons.Default.Note,
              title = "No Personal Notes",
              subtitle = "Record personal reflections, tafsir notes, and lessons from verses, hadiths, and classical books."
            )
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 80.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              items(notes, key = { it.id }) { note ->
                val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(note.updatedAt))

                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_item_${note.id}"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = note.referenceKey,
                        style = MaterialTheme.typography.labelMedium.copy(
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.primary
                        )
                      )
                      IconButton(
                        onClick = { onDeleteNote(note.id) },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                      }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                      text = note.title,
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                      text = note.content,
                      style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = dateStr,
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    )
                  }
                }
              }
            }
          }
        }
        3 -> {
          val resolvedManager = downloadManager ?: remember(context) {
            com.example.data.download.DownloadManager.getInstance(context)
          }
          DownloadCenterScreen(
            downloadManager = resolvedManager,
            currentThemeMode = currentThemeMode,
            onSelectThemeMode = onSelectThemeMode,
            onPlayDownloadedAudio = onPlayDownloadedAudio
          )
        }
      }
    }

    // Add Note FAB
    if (selectedTab == 2) {
      FloatingActionButton(
        onClick = {
          initialNoteReference = ""
          showAddNoteDialog = true
        },
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(24.dp)
          .testTag("add_study_note_fab"),
        containerColor = EmeraldPrimary,
        contentColor = Color.White
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Note")
      }
    }

    // Add Note Dialog
    if (showAddNoteDialog) {
      AddNoteDialog(
        initialReference = initialNoteReference,
        onDismiss = { showAddNoteDialog = false },
        onSave = { ref, title, content ->
          onSaveNote(ref, title, content)
          showAddNoteDialog = false
        }
      )
    }
  }
}

@Composable
fun IslamicBookReaderView(
  book: IslamicClassicalBook,
  selectedChapterIndex: Int,
  onSelectChapter: (Int) -> Unit,
  onJumpToBookAndChapter: (IslamicClassicalBook, Int) -> Unit = { _, _ -> },
  onClose: () -> Unit,
  onNavigateBook: (Int) -> Unit,
  onAddBookNote: (String) -> Unit
) {
  val context = LocalContext.current
  val currentChapter = book.chapters.getOrElse(selectedChapterIndex) { book.chapters.first() }
  var inBookSearchQuery by remember { mutableStateOf("") }
  var searchAllBooksScope by remember { mutableStateOf(false) }

  // Matching chapters within this book or across all 38 books
  data class BookSearchMatch(
    val book: IslamicClassicalBook,
    val chapterIndex: Int,
    val chapterNumber: Int,
    val chapterTitle: String,
    val snippet: String
  )

  val inBookSearchResults = remember(inBookSearchQuery, searchAllBooksScope, book) {
    val q = inBookSearchQuery.trim()
    if (q.isBlank()) emptyList()
    else {
      val targetBooks = if (searchAllBooksScope) IslamicBooksCatalog.allSunniAndShiaBooks else listOf(book)
      val results = mutableListOf<BookSearchMatch>()
      for (targetBook in targetBooks) {
        targetBook.chapters.forEachIndexed { idx, ch ->
          val matchesTitle = ch.titleEnglish.contains(q, ignoreCase = true) ||
            ch.titleArabic.contains(q) ||
            ch.chapterNumber.toString() == q
          val matchesEnglish = ch.englishContent.contains(q, ignoreCase = true)
          val matchesUrdu = ch.urduContent.contains(q)
          val matchesArabic = ch.arabicExcerpt.contains(q)
          if (matchesTitle || matchesEnglish || matchesUrdu || matchesArabic) {
            val snippet = when {
              matchesEnglish -> ch.englishContent.take(120) + "..."
              matchesUrdu -> ch.urduContent.take(120) + "..."
              matchesArabic -> ch.arabicExcerpt.take(80) + "..."
              else -> ch.scholarlyNotes.take(100)
            }
            results.add(
              BookSearchMatch(
                book = targetBook,
                chapterIndex = idx,
                chapterNumber = ch.chapterNumber,
                chapterTitle = ch.titleEnglish,
                snippet = snippet
              )
            )
          }
        }
      }
      results
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Top Book Reader Header
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(onClick = onClose, modifier = Modifier.testTag("close_book_reader_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Books")
          }
          Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "${book.number}. ${book.titleEnglish}",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              maxLines = 1
            )
            Text(
              text = "${book.author} • ${book.titleArabic}",
              style = MaterialTheme.typography.labelSmall.copy(color = GoldPrimary)
            )
          }
          IconButton(
            onClick = { onAddBookNote("${book.titleEnglish} - Ch. ${currentChapter.chapterNumber}") }
          ) {
            Icon(Icons.Default.NoteAdd, contentDescription = "Add Note", tint = EmeraldPrimary)
          }
        }

        // Previous Book / Next Book Navigation Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = { onNavigateBook(-1) },
            enabled = book.number > 1,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("prev_book_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = null, modifier = Modifier.size(16.dp))
            Text("Prev Book", fontSize = 11.sp)
          }

          Text(
            text = "Book ${book.number}/${IslamicBooksCatalog.allSunniAndShiaBooks.size} • ${book.tradition}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
          )

          OutlinedButton(
            onClick = { onNavigateBook(1) },
            enabled = book.number < IslamicBooksCatalog.allSunniAndShiaBooks.size,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("next_book_button")
          ) {
            Text("Next Book", fontSize = 11.sp)
            Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        }

        // In-Book Search Bar & Scope Toggle
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = inBookSearchQuery,
            onValueChange = { inBookSearchQuery = it },
            placeholder = {
              Text(
                text = if (book.tradition == "SHIA") "Search any hadees or text..." else (if (searchAllBooksScope) "Search all 38 books..." else "Search within this book..."),
                fontSize = 12.sp
              )
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
              if (inBookSearchQuery.isNotEmpty()) {
                IconButton(onClick = { inBookSearchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("book_reader_search_input")
          )
          FilterChip(
            selected = searchAllBooksScope,
            onClick = { searchAllBooksScope = !searchAllBooksScope },
            label = { Text(if (searchAllBooksScope) "All 38" else "This Book", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GoldPrimary,
              selectedLabelColor = Color(0xFF101915)
            ),
            modifier = Modifier.testTag("book_reader_search_scope_chip")
          )
        }
      }
    }

    // In-Book Search Results Overlay (if user is searching)
    if (inBookSearchQuery.isNotBlank()) {
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${inBookSearchResults.size} matching chapters for '$inBookSearchQuery':",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
            )
            TextButton(
              onClick = { inBookSearchQuery = "" },
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
            ) {
              Text("Close Search", fontSize = 10.sp)
            }
          }
          if (inBookSearchResults.isEmpty()) {
            Text(
              text = "No matching passages found in ${if (searchAllBooksScope) "the 38 classical books" else book.titleEnglish}. Try different keywords or switch to 'All 38' books.",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
              modifier = Modifier.padding(vertical = 4.dp)
            )
          } else {
            LazyColumn(
              modifier = Modifier.height(180.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              items(inBookSearchResults) { match ->
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surface,
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      onJumpToBookAndChapter(match.book, match.chapterIndex)
                      inBookSearchQuery = ""
                    }
                    .testTag("book_search_match_${match.book.number}_${match.chapterNumber}")
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                      text = "${match.book.titleEnglish} • Ch. ${match.chapterNumber}: ${match.chapterTitle}",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                      text = match.snippet,
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                      maxLines = 2
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // Chapter Tabs
    ScrollableTabRow(
      selectedTabIndex = selectedChapterIndex,
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
      contentColor = EmeraldPrimary,
      edgePadding = 12.dp
    ) {
      book.chapters.forEachIndexed { idx, chapter ->
        Tab(
          selected = selectedChapterIndex == idx,
          onClick = { onSelectChapter(idx) },
          text = {
            Text(
              text = "Ch. ${chapter.chapterNumber}: ${chapter.titleEnglish}",
              fontWeight = if (selectedChapterIndex == idx) FontWeight.Bold else FontWeight.Normal,
              fontSize = 12.sp
            )
          }
        )
      }
    }

    // Chapter Content Body
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Chapter ${currentChapter.chapterNumber}",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = GoldPrimary,
                  fontWeight = FontWeight.Bold
                )
              )
              Text(
                text = currentChapter.titleEnglish,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              )
            }
            IconButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val text = buildString {
                  append("${book.titleEnglish} — ${currentChapter.titleEnglish}\n\n")
                  append("${currentChapter.arabicExcerpt}\n\n")
                  append("${currentChapter.englishContent}\n\n")
                  append("${currentChapter.urduContent}\n\n")
                  append("Refs: ${currentChapter.crossReferences}")
                }
                clipboard.setPrimaryClip(ClipData.newPlainText("Islamic Book Chapter", text))
                Toast.makeText(context, "Chapter copied to clipboard", Toast.LENGTH_SHORT).show()
              }
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = "Copy Chapter")
            }
          }

          if (currentChapter.titleArabic.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = currentChapter.titleArabic,
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = ScheherazadeFontFamily,
                fontSize = 20.sp,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Right
              ),
              modifier = Modifier.fillMaxWidth()
            )
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

          // Arabic Primary Text in Amiri Classical Naskh Font
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = currentChapter.arabicExcerpt,
              style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = AmiriFontFamily,
                fontSize = 22.sp,
                lineHeight = 38.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface
              ),
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
            )
          }

          // Transliteration
          if (currentChapter.transliteration.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Transliteration:",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
              )
            )
            Text(
              text = currentChapter.transliteration,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.secondary
              )
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // English Translation & Exposition
          Text(
            text = "English Translation & Exposition",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = IslamicBooksCatalog.formatBookEnglishContent(currentChapter.englishContent),
            style = MaterialTheme.typography.bodyLarge.copy(
              fontSize = 15.sp,
              lineHeight = 23.sp
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Urdu Translation & Exposition
          Text(
            text = "اردو ترجمہ و تشریح",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = GoldPrimary,
              textAlign = TextAlign.Right
            ),
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = IslamicBooksCatalog.formatBookUrduContent(currentChapter.urduContent),
            style = MaterialTheme.typography.bodyLarge.copy(
              fontFamily = UrduNastaliqFontFamily,
              fontSize = 15.sp,
              lineHeight = 25.sp,
              textAlign = TextAlign.Right,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Scholarly Notes & Verified Attribution Links
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = EmeraldPrimary.copy(alpha = 0.08f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "Scholarly Commentary & Grading Methodology (${book.tradition}):",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = EmeraldPrimary
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = book.gradingMethodology,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontStyle = FontStyle.Italic,
                  color = MaterialTheme.colorScheme.secondary
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = currentChapter.scholarlyNotes,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 13.sp,
                  lineHeight = 19.sp
                )
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Cross-References (Quran & Hadith): ${currentChapter.crossReferences}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = GoldPrimary
                )
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Entry Time: ${book.entryTimestamp} • ${book.sourceTypeLabel}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                  runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(book.officialSourceUrl)))
                  }
                }
              ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Official Source: ${book.officialSourceUrl}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Previous Chapter / Next Chapter Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = {
                if (selectedChapterIndex > 0) {
                  onSelectChapter(selectedChapterIndex - 1)
                }
              },
              enabled = selectedChapterIndex > 0,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("prev_chapter_button")
            ) {
              Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Previous Chapter", fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = {
                if (selectedChapterIndex < book.chapters.lastIndex) {
                  onSelectChapter(selectedChapterIndex + 1)
                }
              },
              enabled = selectedChapterIndex < book.chapters.lastIndex,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("next_chapter_button")
            ) {
              Text("Next Chapter", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun AddNoteDialog(
  initialReference: String,
  onDismiss: () -> Unit,
  onSave: (String, String, String) -> Unit
) {
  var reference by remember { mutableStateOf(initialReference.ifEmpty { "Quran / Hadith Ref" }) }
  var title by remember { mutableStateOf("") }
  var content by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Study Note", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = reference,
          onValueChange = { reference = it },
          label = { Text("Scripture / Book Reference") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Note Title") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Reflection / Notes") },
          minLines = 4,
          maxLines = 8,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank() && content.isNotBlank()) {
            onSave(reference, title, content)
          }
        }
      ) {
        Text("Save Note")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun EmptyStateView(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(32.dp),
        tint = MaterialTheme.colorScheme.primary
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodyMedium.copy(
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    )
  }
}
