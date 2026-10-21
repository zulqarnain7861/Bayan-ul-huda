package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AudioMiniPlayerBar
import com.example.ui.components.TopIslamicBar
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AddNoteDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HadithScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.OpeningExperienceScreen
import com.example.ui.screens.QuranScreen
import com.example.ui.screens.ReportErrorDialog
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodels.BayanViewModel
import com.example.ui.viewmodels.MainNavigationTab

object BayanNavRoutes {
  const val HOME = "home"
  const val QURAN = "quran"
  const val HADITH = "hadith"
  const val SEARCH = "search"
  const val LIBRARY = "library"
  const val ABOUT = "about"
}

data class BottomNavDestination(
  val route: String,
  val tab: MainNavigationTab,
  val label: String,
  val icon: ImageVector,
  val testTag: String
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val bayanViewModel: BayanViewModel = viewModel()
      val uiState by bayanViewModel.uiState.collectAsStateWithLifecycle()
      MyApplicationTheme(themeMode = uiState.prayerConfig.themeMode) {
        BayanApp(viewModel = bayanViewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BayanApp(
  viewModel: BayanViewModel = viewModel(),
  navController: NavHostController = rememberNavController()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val audioState by viewModel.audioState.collectAsStateWithLifecycle()
  val alarmPlaybackState by viewModel.alarmPlaybackState.collectAsStateWithLifecycle()
  val todayPrayerSchedule by viewModel.todayPrayerSchedule.collectAsStateWithLifecycle()
  val auditReport by viewModel.auditReport.collectAsStateWithLifecycle()
  val sourceRegistryList by viewModel.sourceRegistryList.collectAsStateWithLifecycle()
  val surahs by viewModel.allSurahs.collectAsStateWithLifecycle()
  val currentAyahs by viewModel.currentAyahs.collectAsStateWithLifecycle()
  val hadiths by viewModel.collectionHadiths.collectAsStateWithLifecycle()
  val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
  val notes by viewModel.notes.collectAsStateWithLifecycle()
  val searchAyahs by viewModel.searchAyahResults.collectAsStateWithLifecycle()
  val searchHadiths by viewModel.searchHadithResults.collectAsStateWithLifecycle()

  var pendingNoteRef by remember { mutableStateOf<String?>(null) }

  val bottomDestinations = remember {
    listOf(
      BottomNavDestination(
        route = BayanNavRoutes.HOME,
        tab = MainNavigationTab.HOME,
        label = "Home",
        icon = Icons.Default.Home,
        testTag = "nav_tab_home"
      ),
      BottomNavDestination(
        route = BayanNavRoutes.QURAN,
        tab = MainNavigationTab.QURAN,
        label = "Quran",
        icon = Icons.Default.MenuBook,
        testTag = "nav_tab_quran"
      ),
      BottomNavDestination(
        route = BayanNavRoutes.HADITH,
        tab = MainNavigationTab.HADITH,
        label = "Hadith",
        icon = Icons.Default.AutoStories,
        testTag = "nav_tab_hadith"
      ),
      BottomNavDestination(
        route = BayanNavRoutes.SEARCH,
        tab = MainNavigationTab.SEARCH,
        label = "Search",
        icon = Icons.Default.Search,
        testTag = "nav_tab_search"
      ),
      BottomNavDestination(
        route = BayanNavRoutes.LIBRARY,
        tab = MainNavigationTab.LIBRARY,
        label = "Library",
        icon = Icons.Default.Bookmarks,
        testTag = "nav_tab_library"
      )
    )
  }

  fun navigateToTab(route: String, tab: MainNavigationTab) {
    viewModel.selectTab(tab)
    navController.navigate(route) {
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
      }
      launchSingleTop = true
      restoreState = true
    }
  }

  // Step 1: Sacred Opening Experience Screen with bundled Scheherazade New Arabic calligraphy
  if (uiState.isOpeningScreenVisible) {
    OpeningExperienceScreen(
      onEnterApp = { viewModel.dismissOpeningScreen() }
    )
    return
  }

  // Step 2: Authentication or 7-Day Guest Access Mode
  if (currentUser == null && !uiState.isGuestModeActive) {
    AuthScreen(
      remainingGuestDays = uiState.remainingGuestDays,
      onContinueAsGuest = { viewModel.continueAsGuest() }
    )
    return
  }

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: BayanNavRoutes.HOME
  val isAboutOpen = currentRoute == BayanNavRoutes.ABOUT

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      if (isAboutOpen) {
        TopAppBar(
          title = {
            Text(
              text = "About, Themes & Source Audit",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          },
          navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
          )
        )
      } else if (!(currentRoute == BayanNavRoutes.QURAN && uiState.isReaderOpen)) {
        TopIslamicBar(
          themeMode = uiState.prayerConfig.themeMode,
          onCycleThemeMode = { viewModel.cycleThemeMode() },
          onSearchClick = { navigateToTab(BayanNavRoutes.SEARCH, MainNavigationTab.SEARCH) },
          onAboutClick = {
            viewModel.refreshAuditReport(checkNetworkLive = true)
            navController.navigate(BayanNavRoutes.ABOUT) {
              launchSingleTop = true
            }
          }
        )
      }
    },
    bottomBar = {
      Column(modifier = Modifier.navigationBarsPadding()) {
        // Global Azan / Fajr Alarm Popup Control when user is on tabs other than Home
        if (alarmPlaybackState.isActive && currentRoute != BayanNavRoutes.HOME) {
          androidx.compose.material3.Surface(
            modifier = Modifier
              .padding(horizontal = 12.dp, vertical = 4.dp)
              .testTag("global_azan_active_popup"),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            color = androidx.compose.ui.graphics.Color(0xFF101915),
            tonalElevation = 6.dp
          ) {
            androidx.compose.foundation.layout.Row(
              modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
              horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = alarmPlaybackState.prayerOrAlarmTitle,
                  style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                  )
                )
                Text(
                  text = alarmPlaybackState.currentStageLabel,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = androidx.compose.ui.graphics.Color(0xFFD8CCB2)
                  )
                )
              }
              androidx.compose.material3.TextButton(
                onClick = { viewModel.snoozeActiveAzanOrAlarm(5) }
              ) {
                Text("Snooze 5m", color = GoldPrimary, fontSize = 11.sp)
              }
              androidx.compose.material3.Button(
                onClick = { viewModel.stopActiveAzanOrAlarm() },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                  containerColor = EmeraldPrimary
                ),
                modifier = Modifier.testTag("global_azan_stop_button")
              ) {
                Text("Stop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Persistent Audio Reciter Mini Player (when recitation is playing or buffering)
        AudioMiniPlayerBar(
          audioState = audioState,
          onTogglePlayPause = { viewModel.audioPlayerManager.togglePlayPause() },
          onStop = { viewModel.audioPlayerManager.stop() }
        )

        // Main Bottom Navigation Bar controlled by NavHostController
        if (!(currentRoute == BayanNavRoutes.QURAN && uiState.isReaderOpen) && !isAboutOpen) {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
          ) {
            bottomDestinations.forEach { dest ->
              val isSelected = currentRoute == dest.route
              NavigationBarItem(
                selected = isSelected,
                onClick = { navigateToTab(dest.route, dest.tab) },
                icon = { Icon(dest.icon, contentDescription = dest.label) },
                label = { Text(dest.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = EmeraldPrimary,
                  indicatorColor = GoldPrimary.copy(alpha = 0.25f)
                ),
                modifier = Modifier.testTag(dest.testTag)
              )
            }
          }
        }
      }
    }
  ) { paddingValues ->
    NavHost(
      navController = navController,
      startDestination = BayanNavRoutes.HOME,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      composable(BayanNavRoutes.HOME) {
        HomeScreen(
          dailyAyah = uiState.dailyAyah,
          dailyHadith = uiState.dailyHadith,
          surahs = surahs,
          prayerSchedule = todayPrayerSchedule,
          prayerConfig = uiState.prayerConfig,
          alarmPlaybackState = alarmPlaybackState,
          onUpdatePrayerConfig = { viewModel.updatePrayerAndAlarmConfig(it) },
          onRefreshPrayerSchedule = { viewModel.refreshPrayerSchedule(forceRemote = true) },
          onTestAzanOrFajrAlarm = { prayer, isFajr -> viewModel.testAzanOrFajrAlarm(prayer, isFajr) },
          onStopActiveAlarm = { viewModel.stopActiveAzanOrAlarm() },
          onSnoozeActiveAlarm = { mins -> viewModel.snoozeActiveAzanOrAlarm(mins) },
          onOpenSurah = { surah ->
            viewModel.openSurahReader(surah)
            navigateToTab(BayanNavRoutes.QURAN, MainNavigationTab.QURAN)
          },
          onPlayRecitation = { ayah, sName -> viewModel.playAyahRecitation(ayah, sName) },
          onBookmarkAyah = { ayah, sName -> viewModel.toggleBookmarkAyah(ayah, sName) },
          onBookmarkHadith = { hadith -> viewModel.toggleBookmarkHadith(hadith) },
          onNavigateToQuran = { navigateToTab(BayanNavRoutes.QURAN, MainNavigationTab.QURAN) },
          onNavigateToHadith = { navigateToTab(BayanNavRoutes.HADITH, MainNavigationTab.HADITH) },
          onNavigateToLibrary = { navigateToTab(BayanNavRoutes.LIBRARY, MainNavigationTab.LIBRARY) }
        )
      }

      composable(BayanNavRoutes.QURAN) {
        QuranScreen(
          surahs = surahs,
          isReaderOpen = uiState.isReaderOpen,
          selectedSurah = uiState.selectedSurah,
          currentAyahs = currentAyahs,
          bookmarks = bookmarks,
          showEnglish = uiState.showEnglish,
          showUrdu = uiState.showUrdu,
          showTransliteration = uiState.showTransliteration,
          arabicFontSizeSp = uiState.arabicFontSizeSp,
          translationFontSizeSp = uiState.translationFontSizeSp,
          onOpenSurah = { viewModel.openSurahReader(it) },
          onCloseReader = { viewModel.closeSurahReader() },
          onNavigateAdjacentSurah = { offset -> viewModel.navigateToAdjacentSurah(offset) },
          onToggleEnglish = { viewModel.toggleEnglishTranslation() },
          onToggleUrdu = { viewModel.toggleUrduTranslation() },
          onToggleTransliteration = { viewModel.toggleTransliteration() },
          onSetArabicFontSize = { viewModel.setArabicFontSize(it) },
          onPlayAyahRecitation = { ayah, sName -> viewModel.playAyahRecitation(ayah, sName) },
          onBookmarkAyah = { ayah, sName -> viewModel.toggleBookmarkAyah(ayah, sName) },
          onAddNote = { ref -> pendingNoteRef = ref },
          allHadithsForTafsir = viewModel.allHadithsForLinks
        )
      }

      composable(BayanNavRoutes.HADITH) {
        HadithScreen(
          selectedCollectionKey = uiState.selectedCollectionKey,
          hadiths = hadiths,
          bookmarks = bookmarks,
          selectedEditionFilterMode = uiState.selectedEditionFilterMode,
          isHadithLookupLoading = uiState.isFetchingHadithByNumber,
          selectedHadithDetail = uiState.selectedHadithDetail,
          previewedQuranVerse = uiState.previewedQuranVerse,
          onSelectCollection = { viewModel.selectHadithCollection(it) },
          onSelectEditionFilterMode = { viewModel.selectHadithEditionFilterMode(it) },
          onLookupHadithByNumber = { colKey, num -> viewModel.lookupHadithByNumber(num, colKey) },
          onLoadBookSection = { bookNum -> viewModel.loadHadithBookSection(bookNum) },
          onShowHadithDetail = { viewModel.showHadithDetail(it) },
          onCloseHadithDetail = { viewModel.closeHadithDetail() },
          onClickQuranVerseRef = { verseRef, openFullReader ->
            viewModel.openOrPreviewQuranReference(verseRef, openFullReader)
            if (openFullReader) {
              navigateToTab(BayanNavRoutes.QURAN, MainNavigationTab.QURAN)
            }
          },
          onDismissQuranVersePreview = { viewModel.dismissPreviewedQuranVerse() },
          onBookmarkHadith = { viewModel.toggleBookmarkHadith(it) },
          onAddNote = { ref -> pendingNoteRef = ref }
        )
      }

      composable(BayanNavRoutes.SEARCH) {
        SearchScreen(
          searchQuery = uiState.searchQuery,
          searchCategory = uiState.searchCategory,
          ayahResults = searchAyahs,
          hadithResults = searchHadiths,
          surahs = surahs,
          isSearchingRemote = uiState.isSearchingRemote,
          onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
          onSearchCategoryChanged = { viewModel.setSearchCategory(it) },
          onOpenSurah = { surah ->
            viewModel.openSurahReader(surah)
            navigateToTab(BayanNavRoutes.QURAN, MainNavigationTab.QURAN)
          },
          onOpenHadith = { hadith ->
            viewModel.selectHadithCollection(hadith.collectionKey)
            viewModel.showHadithDetail(hadith)
            navigateToTab(BayanNavRoutes.HADITH, MainNavigationTab.HADITH)
          }
        )
      }

      composable(BayanNavRoutes.LIBRARY) {
        LibraryScreen(
          bookmarks = bookmarks,
          notes = notes,
          onRemoveBookmark = { viewModel.removeBookmarkByKey(it) },
          onSaveNote = { ref, title, content -> viewModel.saveNote(ref, title, content) },
          onDeleteNote = { viewModel.deleteNote(it) }
        )
      }

      composable(BayanNavRoutes.ABOUT) {
        AboutScreen(
          currentReciter = audioState.reciter,
          themeMode = uiState.prayerConfig.themeMode,
          auditReport = auditReport,
          sourceRegistryList = sourceRegistryList,
          onSelectThemeMode = { viewModel.setThemeMode(it) },
          onRefreshAuditReport = { viewModel.refreshAuditReport(checkNetworkLive = true) },
          onSelectReciter = { viewModel.setReciter(it) },
          onOpenErrorReport = { viewModel.openErrorReportDialog("General App Experience") },
          userEmail = currentUser?.email,
          userName = currentUser?.displayName,
          isGuestMode = uiState.isGuestModeActive,
          remainingGuestDays = uiState.remainingGuestDays,
          onSignOut = {
            navController.popBackStack()
            viewModel.signOut()
          }
        )
      }
    }
  }

  // Report Error Dialog
  if (uiState.showErrorReportDialog) {
    ReportErrorDialog(
      initialContext = uiState.errorReportContext,
      onDismiss = { viewModel.closeErrorReportDialog() }
    )
  }

  // Add Note Dialog (triggered from Ayah or Hadith item)
  pendingNoteRef?.let { ref ->
    AddNoteDialog(
      initialReference = ref,
      onDismiss = { pendingNoteRef = null },
      onSave = { noteRef, title, content ->
        viewModel.saveNote(noteRef, title, content)
        pendingNoteRef = null
      }
    )
  }
}
