# Bayan al-Huda — Complete Quran, Sunni & Shia Hadith Platform Enhancement Plan

Deliver a high-efficiency, zero-regression upgrade to **Bayan al-Huda** that completes the distinct Sunni and Shia Hadith libraries, adds the Quran Reciters selector (with Mishari Rashid Alafasy completely removed), integrates the Contact Us & US$25 Play Console community support section, protects technical API URLs behind a verified developer view (`sheikhzulqar@gmail.com`), expands the persistent theme selector to all 8 required themes, and validates the complete 6,236-verse Quran text and security posture.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural and product decisions were confirmed during interactive clarification to maximize efficiency and preserve all existing working features:

- **Confirmed Decision 1 — Execution Scope**: Perform a **delta-only upgrade** on the existing codebase that preserves all working Quran, Hadith, Offline Download Manager, Islam with Evidence, Prayer Times, Fajr Wake-Up Alarm, Reading Mode, and Qibla Compass features without destructive rewrites.
- **Confirmed Decision 2 — Developer Maintenance View Authorization**: Technical API endpoints, documentation URLs, host-pinning allowlists, and internal diagnostics in the About & Source Audit screen are hidden from ordinary/guest users and unlocked **exclusively** when the user is authenticated via Firebase Auth with the verified developer account (`sheikhzulqar@gmail.com`).
- **Confirmed Decision 3 — Quran Reciters Catalogue Organization**: Completely remove **Mishari Rashid Alafasy** from all reciter lists, recommendations, and offline catalogues, and organize the new **Reciters** button modal into clearly labelled **Sunni Reciters** (Al-Husary, Abdul Basit, Al-Minshawi, Al-Sudais, Al-Shatri) and **Verified Shia Quran Reciters** sections.

---

### 1. Overview & Core Concept

- **What It Does**: Enhances Bayan al-Huda into a comprehensive, source-verified Islamic digital library featuring the complete 114-Surah Uthmani Quran, two distinct Hadith libraries (**Sunni Hadith Library** and **Shia Hadith Library**) with explicit coverage manifests, a dedicated Quran **Reciters** selector, a **Contact Us & Support Our App** section, role-gated developer diagnostics, and 8 persistent appearance themes.
- **Target Audience / Persona**: Muslims, students of knowledge, and researchers seeking authentic, traceable Quranic and Hadith primary sources in Arabic, English, and Urdu with full offline capability and zero fabricated text.
- **Key Value**: Absolute fidelity to canonical texts (zero changes to original Quranic characters, diacritics, or source numbering), respectful scholarly separation between Sunni and Shia collections, and hardened API/UI security.

---

### 2. Visual Direction & Theme System (8 Persistent Themes)

- **Centralized Design Tokens**: Expand the existing theme engine so users can switch seamlessly between all 8 required appearance modes in Settings / About, persisted across restarts:
  1. **System Default (Auto)**: Adapts automatically to system dark/light state and time of day.
  2. **Light**: Warm Cream & Sanctuary Emerald (`#FAF6EE` / `#0F3D2E`).
  3. **Dark**: Deep Night Obsidian & Burnished Gold (`#0E1713` / `#D4AF37`).
  4. **Pure Black / AMOLED Black**: True `#000000` background and `#070A08` elevated surfaces for zero-glare OLED power efficiency.
  5. **Warm Paper**: Soft natural cotton paper (`#F5EFE2`) with high-contrast ink typography.
  6. **Sepia (Eye-Comfort)**: Low-glare amber parchment (`#F1E5C9`) for extended reading sessions.
  7. **Green (Royal Emerald)**: Deep Haramain Sanctuary Emerald (`#08241B`) with burnished gold accents.
  8. **Blue (Sapphire Sanctuary)**: Classical Islamic cobalt/lapis manuscript blue (`#0B1D33`) with warm gold highlights.
- **Typography & RTL Integrity**: Preserve bundled `Scheherazade New` and `Amiri Quran` fonts with `includeFontPadding = true` and `LineHeightStyle.Trim.None` so Uthmani diacritics, maddah (`ٓ`), shaddah (`ّ`), and superscript dagger alif (`ٰ` in Surah Ar-Ra'd 13:1 `الٓـمّٓرٰ`) render without clipping across every theme and screen size.

---

### 3. System Architecture & Component Hierarchy

```text
┌────────────────────────────────────────────────────────────────────────────┐
│                        BAYAN AL-HUDA PRESENTATION LAYER                    │
├──────────────────┬──────────────────┬──────────────────┬───────────────────┤
│   Quran Reader   │  Hadith Library  │   Search Engine  │  About & Contact  │
│  • 114 Surahs    │  • Sunni Entry   │  • Sunni Filter  │  • Public About   │
│  • Reading Mode  │    - Kutub Sittah│  • Shia Filter   │    (No Raw URLs)  │
│  • Reciters Btn  │    - 15+ Works   │  • Search Both   │  • Contact Us &   │
│    (Sunni & Shia │    - Commentaries│    Libraries     │    $25 Play Store │
│     No Alafasy)  │  • Shia Entry    │  • Arabic/EN/UR  │    Support Card   │
│  • Audio Player  │    - Al-Kutub    │    Exact & Norm  │  • Developer View │
│                  │      al-Arba'a   │    Lookup        │    (Gated by Auth │
│                  │    - 21+ Works   │                  │     email check)  │
│                  │    - Twelver /   │                  │  • 8 Theme Picker │
│                  │      Zaydi /     │                  │                   │
│                  │      Ismaili     │                  │                   │
└────────┬─────────┴────────┬─────────┴────────┬─────────┴─────────┬─────────┘
         │                  │                  │                   │
         ▼                  ▼                  ▼                   ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                     VIEWMODEL & DOMAIN VERIFICATION LAYER                  │
│  • Tradition & Sub-Tradition Separation (SUNNI / SHIA / COMMENTARY)        │
│  • Collection Coverage Manifests (Expected vs Local Verified Counts)       │
│  • Developer Role Guard (Firebase Auth User == sheikhzulqar@gmail.com)     │
│  • Full 6,236-Ayah SHA-256 & Character-Sequence Integrity Auditor          │
└────────┬─────────────────────────────────────────────────────────┬─────────┘
         │                                                         │
         ▼                                                         ▼
┌───────────────────────────────────────┐ ┌──────────────────────────────────┐
│     LOCAL ROOM & OFFLINE STORAGE      │ │   PROVIDER ADAPTER & SECURITY    │
│  • 114 Surahs & 6,236 Uthmani Ayahs   │ │  • Trusted Host Allowlist Guard  │
│  • Sunni & Shia Hadith Entities       │ │  • HTTPS/TLS Enforcement         │
│  • SourceRegistry & Import Manifests  │ │  • Resumable Paginated Importer  │
│  • Scoped Offline Audio & Text Packs  │ │  • Zero Client-Side Private Keys │
└───────────────────────────────────────┘ └──────────────────────────────────┘
```

---

### 4. Core Functional Enhancements (Delta Specification)

#### 4.1 Two Distinct Hadith Libraries (Sunni & Shia Separation)
- **Prominent Dual Entry Points**: The Hadith screen displays two distinct top-level library mode cards/tabs:
  - **Sunni Hadith Library**:
    - *The Six Major Sunni Collections (Kutub al-Sittah)*: Sahih al-Bukhari, Sahih Muslim, Sunan Abi Dawud, Jami' al-Tirmidhi, Sunan al-Nasa'i (identifying Al-Mujtaba / Al-Sughra edition), and Sunan Ibn Majah.
    - *Other Major Sunni Collections (15 Works)*: Al-Muwatta of Imam Malik, Musnad Ahmad ibn Hanbal, Sunan al-Darimi, Musannaf Abd al-Razzaq, Musannaf Ibn Abi Shaybah, Sahih Ibn Khuzaymah, Sahih Ibn Hibban, Al-Mustadrak ala al-Sahihayn (al-Hakim), Sunan al-Daraqutni, Al-Sunan al-Kubra (al-Bayhaqi), Al-Adab al-Mufrad, Al-Shama'il al-Muhammadiyyah, Riyad al-Salihin, Bulugh al-Maram, and Mishkat al-Masabih.
    - *Hadith Scholarship & Commentary (Separate Category)*: Fath al-Bari, Sharh Sahih Muslim (al-Nawawi), Tuhfat al-Ahwadhi, Awn al-Ma'bud, Silsilat al-Ahadith al-Sahihah, Silsilat al-Ahadith al-Da'ifah, Sahih al-Jami, and Da'if al-Jami — clearly marked as commentary/grading reference works rather than primary Hadith collections, with honest copyright/access status indicators.
  - **Shia Hadith Library**:
    - *The Four Principal Twelver Shia Hadith Books (Al-Kutub al-Arba'a)*: Al-Kafi (al-Kulayni), Man La Yahduruhu al-Faqih (Shaykh al-Saduq), Tahdhib al-Ahkam (Shaykh al-Tusi), and Al-Istibsar (Shaykh al-Tusi).
    - *Other Important Twelver Shia Collections & Categorized Works*: Bihar al-Anwar, Wasa'il al-Shia, Mustadrak al-Wasa'il, Nahj al-Balagha (distinguished as sermons, letters, and sayings), Al-Sahifa al-Sajjadiyya (distinguished as supplications), Kitab al-Tawhid, Uyun Akhbar al-Rida, Al-Khisal, Al-Amali of Shaykh al-Saduq, Al-Amali of Shaykh al-Tusi (distinguished by author), Al-Mahasin (al-Barqi), Qurb al-Isnad (al-Himyari), Tuhaf al-Uqul (ibn Shu'ba al-Harrani), Kamil al-Ziyarat (Ibn Qulawayh), Thawab al-A'mal wa Iqab al-A'mal, Ma'ani al-Akhbar, Kitab al-Ghayba by al-Nu'mani, Kitab al-Ghayba by Shaykh al-Tusi (kept distinct), Al-Ihtijaj (al-Tabarsi), plus Shia Rijal & Commentary reference entries.
    - *Sub-Tradition Metadata Filter*: Explicit categorization for **Twelver / Ithna Ashari**, **Zaydi**, and **Ismaili** traditions so works are never mixed into an undifferentiated list.
- **Explicit Coverage Status**: Every catalogue item displays its honest verification status (`Complete in Local Cache`, `Foundational Verified Selection + On-Demand Pagination`, `Metadata & Scholarly Reference Only`, or `Awaiting Lawful Dataset Access`) alongside expected canonical counts.
- **Search Both Libraries Option**: Search allows filtering by **Sunni Hadith Only**, **Shia Hadith Only**, or an explicitly labelled **Search Both Libraries** mode where every result badge displays its tradition, collection, book/chapter, original numbering, and grader attribution.

#### 4.2 Reciters Button & Verified Audio Library (Alafasy Removed)
- **Complete Removal of Mishari Rashid Alafasy**: Remove `alafasy` from `availableReciters` in the audio player, `OfflineCatalogRegistry.audioReciters` in the download manager, and any UI labels or tests.
- **New "Reciters" Button in Quran & Audio Views**:
  - Add a clean **Reciters** button (`quran_reciters_button`) in the Quran screen header and Surah reader toolbar.
  - Clicking it opens a structured **Verified Quran Reciters Sheet** organized into:
    - **Verified Sunni Reciters**: Mahmoud Khalil Al-Husary, Abdul Basit Abdul Samad (Murattal), Muhammad Siddiq Al-Minshawi (Murattal), Abdul Rahman Al-Sudais, and Abu Bakr Al-Shatri. (Also document availability status for Islam Sobhi, Abdullah Shehab, and Abdul Rahman Masoud honestly without fabricating unsupported ayah streams).
    - **Verified Shia Quran Reciters**: Add verified Shia Quran reciters (e.g., Karim Mansouri and Maytham al-Tammar with verified Quranic recitation streams and offline cadence support), clearly labelled under the **Verified Shia Quran Reciters** section and kept strictly separate from duas, ziyarat, or lectures.

#### 4.3 Contact Us & Community Support Section
- Add a **Contact Us & Support Our App** card and dialog/section accessible from the About screen and Home screen:
  - **Email Us**: Opens email intent for `sheikhzulqar@gmail.com` (friendly label, no raw technical clutter).
  - **Instagram**: Opens `@zulqarnainah` (`https://www.instagram.com/zulqarnainah/`) in the Instagram app with browser fallback.
  - **Facebook Messenger**: Friendly button **"Message on Facebook"** that opens `https://www.facebook.com/share/1PiLFRT6jv/` via native app or browser fallback.
  - **Support Our App (US$25 Google Play Console Goal)**: Clearly explains that developer Zulqarnain Ahmad Sheikh hopes to raise **US$25** for the Google Play Console registration fee to publish **Bayan al-Huda** on the Google Play Store, and invites anyone wishing to help with the fee, development, or testing to reach out via the contact buttons above. Explicitly states that **Bayan al-Huda will remain 100% free and ad-free**, with zero payment gateways or donation forms.

#### 4.4 Protect API Links & Developer-Only Maintenance View in About
- **Public View (Ordinary & Guest Users)**:
  - Retains all normal About information: app description, developer credits, Islamic book & source names, translation names, record counts, verification status badges, and theme selector.
  - Hides all raw API URLs (such as `https://quranenc.com/`, `https://api.quran.com/`, `https://sunnah.stoplight.io/`, `https://everyayah.com/`), internal repository links, host-pinning allowlists, and technical vulnerability/security config strings.
- **Developer Maintenance View (`sheikhzulqar@gmail.com`)**:
  - Unlocked **only** when `currentUser?.email?.equals("sheikhzulqar@gmail.com", ignoreCase = true) == true && currentUser?.isEmailVerified != false` via authenticated Firebase session (never unlocked by typing an email into a text box).
  - Displays a dedicated **Developer Maintenance & Protected API Diagnostics** panel showing the sanitized provider endpoint status, allowlist verification, and collection sync diagnostics without exposing private keys or secrets.

#### 4.5 Complete Quran Text Integrity Audit & Automated Tests
- Verify across all 114 Surahs and 6,236 verses that zero canonical Arabic characters, diacritics, waqf marks, or superscript dagger alifs (including Surah Ar-Ra'd 13:1 `الٓـمّٓرٰ` with `\u0670`) have been altered.
- Execute Robolectric tests verifying:
  - Sunni vs Shia Hadith library separation, the Four Principal Twelver Shia Books, the Six Major Sunni Collections, and "Search Both Libraries" attribution.
  - Complete removal of Mishari Rashid Alafasy and working Reciters selector.
  - Ordinary user vs verified `sheikhzulqar@gmail.com` Developer View URL redaction in `AboutScreen`.
  - All 8 persistent themes (`SYSTEM_DEFAULT`, `LIGHT`, `DARK`, `PURE_BLACK`, `WARM_PAPER`, `SEPIA`, `GREEN`, `BLUE`).
  - Rebuild and refresh `/quran.apk` in the workspace root so the installable APK contains every update.
