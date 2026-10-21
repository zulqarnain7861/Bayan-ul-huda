# Bayan al-Huda — Jetpack Compose Navigation Controller, Authentic Arabic Typography & 7-Day Guest Access

Upgrade **Bayan al-Huda — Quran & Hadith** with a full **Jetpack Compose Navigation Controller (`NavHost` & `NavHostController`)** managing the **Home**, **Quran**, **Hadith**, **Search**, and **Library** screens with bottom navigation state preservation and deep-linking, bundle authentic licensed Arabic fonts (**Amiri** for Quranic Naskh text and **Scheherazade New** for opening Bismillah & header calligraphy), and enable **7-Day Guest Access** backed by local offline storage alongside optional Google Sign-In cloud sync.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> All three architectural and design decisions below reflect your confirmed choices and will be implemented directly upon approval.

- **Confirmed Decision 1 — Licensed Arabic Typography Pairing**: Bundle **Scheherazade New** locally for the opening Bismillah calligraphy (`بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ`), Arabic emblems (`بَيَانُ الهُدَى`), and decorative Surah headers, paired with **Amiri** classical Naskh for all Quranic verses, Hadith narrations, Tafsir Arabic excerpts, and classical Islamic book texts.
- **Confirmed Decision 2 — Jetpack Compose `NavHostController` with State Preservation & Deep-Linking**: Replace manual tab switching with Jetpack Navigation Compose (`NavHost`, `rememberNavController()`, and `NavigationBar` synced via `currentBackStackEntryAsState()`), using `saveState = true` and `restoreState = true` on bottom navigation transitions and supporting direct route navigation to specific Surahs and Hadiths.
- **Confirmed Decision 3 — 7-Day Guest Access Mode + Optional Google Sign-In**: Provide an explicit **7-Day Guest Access** option on the Authentication screen (displaying remaining guest days and storing bookmarks/notes locally on-device) alongside one-tap Google Sign-In for permanent cloud backup and cross-device sync.

---

## 1. Overview & Core Concept

- **What It Does**:
  1. **Jetpack Compose Navigation Graph (`NavHost` & `NavHostController`)**: Connects the 5 primary destinations (`home`, `quran`, `hadith`, `search`, `library`) plus the `about` destination and deep-link parameters (`quran?surahNumber={surahNumber}`, `hadith?collection={collection}&number={number}`) to a persistent Material 3 Bottom Navigation Bar and system back stack.
  2. **Authentic Bundled Arabic Fonts (`Scheherazade New` & `Amiri`)**: Downloads and embeds static `.ttf` Arabic fonts directly into application resources so sacred Quranic diacritics (Tashkeel), Uthmani ligatures, and the opening Bismillah render with authentic calligraphic precision offline on every Android device.
  3. **7-Day Guest Mode with Seamless Cloud Upgrade**: Lets users explore all 114 Surahs, 8 Tafsirs, 7 Hadith books, and 32 Islamic books immediately in Guest Mode for 7 days with local persistence, while allowing them to sign in with Google at any time from the Auth screen or About/Profile screen to sync their library to the cloud.
- **Target Audience / Persona**: Muslims, students of sacred knowledge, and researchers seeking a responsive, typographically authentic, offline-capable Quran, Hadith, and Islamic library experience.
- **Key Value**: True back-stack and tab state preservation across all five screens, authentic manuscript-grade Arabic script on both the opening screen and scripture readers, and friction-free 7-day guest onboarding.

---

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Sacred Opening Experience (`Scheherazade New` Calligraphy)**:
     - Displays `بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ` and `بَيَانُ الهُدَى` rendered in **Scheherazade New** bold calligraphy over the Deep Emerald and Gold gradient backdrop.
  2. **Authentication & 7-Day Guest Access Gate**:
     - Users can sign in with Google (for permanent cloud sync) or tap **"Continue as Guest (7 Days Full Access)"**.
     - Active 7-day guest sessions automatically remember their start timestamp, display the remaining guest days badge in the profile/settings view, and prompt for Google Sign-In once the 7-day window expires.
  3. **Bottom Navigation Bar + `NavHostController` Flow**:
     - Tapping **Home**, **Quran**, **Hadith**, **Search**, or **Library** navigates via `navController.navigate(route)` with `launchSingleTop = true`, `restoreState = true`, and `popUpTo(findStartDestination().id) { saveState = true }`.
     - Tapping a Surah from **Home** or **Search** navigates directly into the **Quran** route and opens that Surah in the reader; tapping "Explore 7 Hadith Books" navigates directly to the **Hadith** route; tapping "32 Books" navigates directly to the **Library** route.
- **Visual Identity & Theme**:
  - *Aesthetic Direction*: Classical Islamic Manuscript meets Modern Material 3 Editorial.
  - *Color Palette & Mood*: Deep Emerald (`#0B3B2B` / `#0F523B`), Burnished Gold (`#D4AF37` / `#F3E5AB`), and Warm Cream (`#FBF9F4`) surfaces.
  - *Typography & Hierarchy*:
    - **Calligraphic Display (`ScheherazadeNewFontFamily`)**: Used on the Opening Screen Bismillah, Arabic app emblem,Bismillah Surah headers, and Surah Arabic titles (`30sp–36sp`, generous `44sp–52sp` line height).
    - **Sacred Scripture Body (`AmiriFontFamily`)**: Used on all 6,236 Quranic verses, Hadith Arabic narrations, Tafsir Arabic texts, and Classical Book Arabic excerpts (`20sp–34sp` user-adjustable with full diacritic clearance).
- **Interactive Feedback & Motion**:
  - Smooth crossfade transitions between `NavHost` destinations, active gold-tinted pill indicators in the Material 3 `NavigationBar`, and predictive back navigation from reader views and secondary routes back to the start destination.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Jetpack Navigation Compose (`NavHostController`) with Saved State**
  - *Chosen Approach*: Use official Jetpack Navigation Compose (`NavHost`, `NavGraph.Companion.findStartDestination`, `currentBackStackEntryAsState`) integrated with `BayanViewModel`.
  - *Why*: Preserves scroll positions and active sub-tabs across **Home**, **Quran**, **Hadith**, **Search**, and **Library**, while ensuring Android system back gestures always unwind the navigation stack predictably.
  - *Alternatives Considered*: Manual `when (currentTab)` state switching, which lacks standard Android back-stack graph semantics and deep-link route arguments.
- **Decision 2: Local Bundled Static `.ttf` Arabic Fonts (`Amiri` + `Scheherazade New`)**
  - *Chosen Approach*: Bundle static `.ttf` font files directly inside app resources and expose dedicated `AmiriFontFamily` and `ScheherazadeFontFamily` tokens alongside Material 3 `Typography`.
  - *Why*: Guarantees 100% offline availability and crisp rendering of Quranic Tashkeel and ligatures without relying on runtime network font providers.
  - *Alternatives Considered*: System default sans-serif fonts, which flatten traditional Arabic Naskh proportions and crowd diacritical marks.
- **Decision 3: 7-Day Guest Access with Local Persistence & Cloud Sync Upgrade**
  - *Chosen Approach*: Track the initial guest activation timestamp locally (`7 * 24 * 60 * 60 * 1000L` ms window); allow full app access and local SQLite bookmark/note storage while the 7-day window is active, and seamlessly merge with Cloud Firestore whenever the user signs in with Google.
  - *Why*: Respects the requirement to keep guest access available for 7 days while maintaining zero-trust authenticated cloud persistence for signed-in users.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

- **Architecture & Component Diagram**:
```
┌────────────────────────────────────────────────────────────────────────────┐
│                        MainActivity / BayanApp                             │
│  ┌──────────────────────┐   ┌───────────────────────────────────────────┐  │
│  │ OpeningExperience    │──▶│ Auth & 7-Day Guest Access Gate            │  │
│  │ (Scheherazade Font)  │   │ • Google Sign-In (CredentialManager)      │  │
│  └──────────────────────┘   │ • 7-Day Guest Session (Local Preferences) │  │
│                             └─────────────────────┬─────────────────────┘  │
│                                                   ▼                        │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ Scaffold (TopIslamicBar + AudioMiniPlayerBar + M3 NavigationBar)     │  │
│  │  ┌────────────────────────────────────────────────────────────────┐  │  │
│  │  │ Jetpack Compose NavHost (NavHostController)                    │  │  │
│  │  │                                                                │  │  │
│  │  │  ├─ "home"    ──▶ HomeScreen (Daily Ayah/Hadith, Reflections)  │  │  │
│  │  │  ├─ "quran"   ──▶ QuranScreen (114 Surahs, 8 Tafsirs, Amiri)   │  │  │
│  │  │  ├─ "hadith"  ──▶ HadithScreen (7 Books, Dual #s, Prev/Next)   │  │  │
│  │  │  ├─ "search"  ──▶ SearchScreen (Unified Arabic/EN/UR Search)   │  │  │
│  │  │  ├─ "library" ──▶ LibraryScreen (32 Books, Bookmarks, Notes)   │  │  │
│  │  │  └─ "about"   ──▶ AboutScreen (Reciter, Guest/Auth Status)     │  │  │
│  │  └────────────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────────┘
```

- **Data Model & State**:
  - **Navigation Routes (`BayanDestination`)**: Sealed/enum route definitions for `home`, `quran`, `hadith`, `search`, `library`, and `about`, with bottom bar metadata (route, title, icon, testTag).
  - **Arabic Typography Tokens**:
    - `ScheherazadeFontFamily`: Bundled `Scheherazade New` font family for the opening Bismillah (`بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ`), Arabic logo (`بَيَانُ الهُدَى`), and ornamental headers.
    - `AmiriFontFamily`: Bundled `Amiri` font family applied to all Arabic scripture composables (`OpeningExperienceScreen`, `BismillahHeader`, `HomeScreen`, `QuranScreen`, `HadithScreen`, `SearchScreen`, and `LibraryScreen`).
  - **7-Day Guest Session Manager**: Stores `guest_access_start_epoch_ms` and `is_guest_active` in device preferences, computing `isGuestSessionValid()` (`elapsed < 7 days`) and `remainingGuestDays()` (`1..7`), exposed via `BayanViewModel`.
- **Interactive Component & State Mapping**:
  - Selecting any item in the bottom `NavigationBar` inspects `navController.currentBackStackEntryAsState()` and navigates cleanly via `NavHostController`, closing any open overlay reader if the user taps the active tab or back button.
  - Selecting a Surah on `HomeScreen` or `SearchScreen` calls `viewModel.openSurahReader(surah)` and navigates `navController` to the `quran` route with state restoration.
  - Tapping **"Continue as Guest (7 Days)"** on `AuthScreen` activates or resumes the 7-day guest window and transitions immediately into the `NavHost`.
