package com.example.data.local

import com.example.data.local.entities.HadithEntity

data class HadithCollectionMeta(
  val key: String,
  val nameEnglish: String,
  val nameArabic: String,
  val nameUrdu: String,
  val compiler: String,
  val deathHijri: String,
  val totalHadithsInfo: String,
  val numberingSystemInfo: String,
  val totalCanonicalCount: Int = 0,
  val totalBooksCount: Int = 1,
  val darussalamEditionLabel: String = "Darussalam Canonical",
  val inBookEditionLabel: String = "In-Book (Book & Hadith)",
  val tradition: String = "SUNNI",
  val subTradition: String = "Ahl al-Sunnah wal-Jama'ah",
  val categoryGroup: String = "KUTUB_AL_SITTAH",
  val coverageStatusBadge: String = "Foundational Verified Selection + On-Demand API"
)

object ExtendedHadithDataProvider {

  val primaryCollections: List<HadithCollectionMeta> = listOf(
    HadithCollectionMeta(
      key = "bukhari",
      nameEnglish = "Sahih al-Bukhari",
      nameArabic = "صحيح البخاري",
      nameUrdu = "صحیح بخاری",
      compiler = "Imam Muhammad ibn Isma'il al-Bukhari",
      deathHijri = "256 AH",
      totalHadithsInfo = "7,563 Hadiths (97 Books)",
      numberingSystemInfo = "Darussalam / Fath al-Bari & USC-MSA In-Book",
      totalCanonicalCount = 7563,
      totalBooksCount = 97,
      darussalamEditionLabel = "Darussalam / Fath al-Bari",
      inBookEditionLabel = "In-Book & USC-MSA Reference",
      tradition = "SUNNI",
      subTradition = "Ahl al-Sunnah (Kutub al-Sittah)",
      categoryGroup = "KUTUB_AL_SITTAH"
    ),
    HadithCollectionMeta(
      key = "muslim",
      nameEnglish = "Sahih Muslim",
      nameArabic = "صحيح مسلم",
      nameUrdu = "صحیح مسلم",
      compiler = "Imam Muslim ibn al-Hajjaj al-Naysaburi",
      deathHijri = "261 AH",
      totalHadithsInfo = "7,563 / 3,033 Hadiths (56 Books)",
      numberingSystemInfo = "Darussalam / Fu'ad Abd al-Baqi & In-Book",
      totalCanonicalCount = 7563,
      totalBooksCount = 56,
      darussalamEditionLabel = "Darussalam / Fu'ad Abd al-Baqi",
      inBookEditionLabel = "In-Book & USC-MSA Reference",
      tradition = "SUNNI",
      subTradition = "Ahl al-Sunnah (Kutub al-Sittah)",
      categoryGroup = "KUTUB_AL_SITTAH"
    ),
    HadithCollectionMeta(
      key = "abudawud",
      nameEnglish = "Sunan Abu Dawud",
      nameArabic = "سنن أبي داود",
      nameUrdu = "سنن ابی داؤد",
      compiler = "Imam Abu Dawud Sulayman ibn al-Ash'ath as-Sijistani",
      deathHijri = "275 AH",
      totalHadithsInfo = "5,274 Hadiths (43 Books)",
      numberingSystemInfo = "Darussalam International & Muhyi al-Din Abd al-Hamid In-Book",
      totalCanonicalCount = 5274,
      totalBooksCount = 43,
      darussalamEditionLabel = "Darussalam / Muhyi al-Din",
      inBookEditionLabel = "In-Book Reference",
      tradition = "SUNNI",
      subTradition = "Ahl al-Sunnah (Kutub al-Sittah)",
      categoryGroup = "KUTUB_AL_SITTAH"
    ),
    HadithCollectionMeta(
      key = "tirmidhi",
      nameEnglish = "Jami` at-Tirmidhi",
      nameArabic = "جامع الترمذي",
      nameUrdu = "جامع ترمذی",
      compiler = "Imam Abu `Isa Muhammad ibn `Isa at-Tirmidhi",
      deathHijri = "279 AH",
      totalHadithsInfo = "3,956 Hadiths (49 Books)",
      numberingSystemInfo = "Darussalam International & Ahmad Shakir In-Book",
      totalCanonicalCount = 3956,
      totalBooksCount = 49,
      darussalamEditionLabel = "Darussalam / Ahmad Shakir",
      inBookEditionLabel = "In-Book Reference",
      tradition = "SUNNI",
      subTradition = "Ahl al-Sunnah (Kutub al-Sittah)",
      categoryGroup = "KUTUB_AL_SITTAH"
    ),
    HadithCollectionMeta(
      key = "nasai",
      nameEnglish = "Sunan an-Nasa'i",
      nameArabic = "سنن النسائي",
      nameUrdu = "سنن نسائی",
      compiler = "Imam Ahmad ibn Shu`ayb an-Nasa'i",
      deathHijri = "303 AH",
      totalHadithsInfo = "5,758 Hadiths (51 Books)",
      numberingSystemInfo = "Darussalam International & Abu Ghuddah In-Book",
      totalCanonicalCount = 5758,
      totalBooksCount = 51,
      darussalamEditionLabel = "Darussalam / Abu Ghuddah",
      inBookEditionLabel = "In-Book Reference",
      tradition = "SUNNI",
      subTradition = "Ahl al-Sunnah (Kutub al-Sittah)",
      categoryGroup = "KUTUB_AL_SITTAH"
    ),
    HadithCollectionMeta(
      key = "ibnmajah",
      nameEnglish = "Sunan Ibn Majah",
      nameArabic = "سنن ابن ماجه",
      nameUrdu = "سنن ابن ماجہ",
      compiler = "Imam Muhammad ibn Yazid Ibn Majah al-Qazwini",
      deathHijri = "273 AH",
      totalHadithsInfo = "4,341 Hadiths (37 Books)",
      numberingSystemInfo = "Darussalam International & Fu'ad Abd al-Baqi In-Book",
      totalCanonicalCount = 4341,
      totalBooksCount = 37,
      darussalamEditionLabel = "Darussalam / Fu'ad Abd al-Baqi",
      inBookEditionLabel = "In-Book Reference",
      tradition = "SUNNI",
      subTradition = "Ahl al-Sunnah (Kutub al-Sittah)",
      categoryGroup = "KUTUB_AL_SITTAH"
    ),
    HadithCollectionMeta(
      key = "malik",
      nameEnglish = "Muwatta Imam Malik",
      nameArabic = "موطأ الإمام مالك",
      nameUrdu = "موطا امام مالک",
      compiler = "Imam Malik ibn Anas",
      deathHijri = "179 AH",
      totalHadithsInfo = "1,858 Narrations (61 Books)",
      numberingSystemInfo = "Yahya ibn Yahya al-Laythi Edition & Book:Hadith",
      totalCanonicalCount = 1858,
      totalBooksCount = 61,
      darussalamEditionLabel = "Yahya al-Laythi Canonical",
      inBookEditionLabel = "Book & Hadith In-Book",
      tradition = "SUNNI",
      subTradition = "Ahl al-Sunnah (Major Classical Collection)",
      categoryGroup = "SUNNI_OTHER_MAJOR"
    ),
    HadithCollectionMeta(
      key = "nawawi",
      nameEnglish = "40 Hadith Nawawi",
      nameArabic = "الأربعون النووية",
      nameUrdu = "اربعین نووی",
      compiler = "Imam Yahya ibn Sharaf an-Nawawi",
      deathHijri = "676 AH",
      totalHadithsInfo = "42 Jawami' al-Kalim Hadiths",
      numberingSystemInfo = "Standard Nawawi 1–42 & Primary Source Cross-Ref",
      totalCanonicalCount = 42,
      totalBooksCount = 1,
      darussalamEditionLabel = "Nawawi Canonical 1–42",
      inBookEditionLabel = "Primary Source Cross-Ref",
      tradition = "SUNNI",
      subTradition = "Ahl al-Sunnah (Major Classical Collection)",
      categoryGroup = "SUNNI_OTHER_MAJOR"
    ),
    HadithCollectionMeta(
      key = "riyadussalihin",
      nameEnglish = "Riyad as-Salihin",
      nameArabic = "رياض الصالحين",
      nameUrdu = "ریاض الصالحین",
      compiler = "Imam Yahya ibn Sharaf an-Nawawi",
      deathHijri = "676 AH",
      totalHadithsInfo = "1,896 Hadiths (19 Books)",
      numberingSystemInfo = "Darussalam Continuous 1–1896 & Book:Chapter",
      totalCanonicalCount = 1896,
      totalBooksCount = 19,
      darussalamEditionLabel = "Darussalam Continuous 1–1896",
      inBookEditionLabel = "Book & Chapter In-Book",
      tradition = "SUNNI",
      subTradition = "Ahl al-Sunnah (Major Classical Collection)",
      categoryGroup = "SUNNI_OTHER_MAJOR"
    )
  ) + CompleteHadithCollectionsCatalog.additionalCollections

  val sunniCollections: List<HadithCollectionMeta>
    get() = primaryCollections.filter { it.tradition == "SUNNI" }

  val shiaCollections: List<HadithCollectionMeta>
    get() = primaryCollections.filter { it.tradition == "SHIA" }

  fun getHadithCollectionEntities(
    cachedCounts: Map<String, Int> = emptyMap()
  ): List<com.example.data.local.entities.HadithCollectionEntity> {
    val defaultCounts = getExtendedHadiths().groupingBy { it.collectionKey }.eachCount()
    return primaryCollections.map { meta ->
      com.example.data.local.entities.HadithCollectionEntity(
        collectionKey = meta.key,
        nameEnglish = meta.nameEnglish,
        nameArabic = meta.nameArabic,
        nameUrdu = meta.nameUrdu,
        compiler = meta.compiler,
        deathHijri = meta.deathHijri,
        totalCanonicalHadiths = meta.totalCanonicalCount,
        totalBooks = meta.totalBooksCount,
        darussalamEditionName = meta.darussalamEditionLabel,
        inBookEditionName = meta.inBookEditionLabel,
        apiEndpointSlug = meta.key,
        locallyCachedCount = cachedCounts[meta.key] ?: defaultCounts[meta.key] ?: 0,
        hasCompleteArabic = true,
        hasCompleteEnglish = true,
        hasCompleteUrdu = true,
        paginationStatus = "Curated Offline + Live Sunnah.com / Hadith API Lookup"
      )
    }
  }

  fun getExtendedHadiths(): List<HadithEntity> {
    val rawList = listOf(
      // ==================== 1. SAHIH AL-BUKHARI ====================
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 1,
        bookName = "Book 1: Revelation (كتاب بدء الوحي)",
        chapterNumber = 1,
        chapterName = "How Divine Revelation started",
        hadithNumber = 1,
        darussalamReference = "Darussalam: #1",
        inBookReference = "Book 1, Hadith 1 • Sahih Intl / Cross-Edition Ref: #70 • USC-MSA Vol. 1, Book 1, #1",
        arabicNumber = 70,
        arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ",
        transliteration = "Innamal-a'malu bin-niyyat, wa innama likulli imri'in ma nawa, faman kanat hijratuhu ila dunya yusibuha aw ila imra'atin yankihuha fahijratuhu ila ma hajara ilayh.",
        englishText = "The reward of deeds depends upon the intentions and every person will get the reward according to what he has intended. So whoever emigrated for worldly benefits or for a woman to marry, his emigration was for what he emigrated for.",
        urduText = "اعمال کا دارومدار صرف نیت پر ہے اور ہر شخص کو وہی ملے گا جس کی اس نے نیت کی، چنانچہ جس کی ہجرت دنیا حاصل کرنے کے لیے ہو یا کسی عورت سے شادی کے لیے تو اس کی ہجرت اسی مقصد کے لیے شمار ہوگی۔",
        narrator = "'Umar ibn al-Khattab (RA)",
        grade = "Sahih (Darussalam / Original Sahih al-Bukhari)",
        gradingScholar = "Imam al-Bukhari (Darussalam / Sahih International Verified)",
        reference = "Sahih al-Bukhari 1",
        linkedQuranVerses = "4:100, 33:5, 98:5"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 1,
        bookName = "Book 1: Revelation (كتاب بدء الوحي)",
        chapterNumber = 3,
        chapterName = "First revelation in Cave Hira",
        hadithNumber = 3,
        darussalamReference = "Darussalam: #3",
        inBookReference = "Book 1, Hadith 3 • Sahih Intl / USC-MSA Vol. 1, Book 1, #3",
        arabicNumber = 3,
        arabicText = "جَاءَهُ الْمَلَكُ فَقَالَ اقْرَأْ‏.‏ قَالَ مَا أَنَا بِقَارِئٍ... فَقَالَ اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ * خَلَقَ الإِنسَانَ مِنْ عَلَقٍ * اقْرَأْ وَرَبُّكَ الأَكْرَمُ",
        transliteration = "Ja'ahul-malaku faqala: Iqra'. Qala: Ma ana bi-qari'... Faqala: Iqra' bismi Rabbikal-ladhi khalaq, khalaqal-insana min 'alaq, Iqra' wa Rabbukal-Akram.",
        englishText = "The angel came to him in the cave of Hira and asked him to read. The Prophet (ﷺ) replied, 'I do not know how to read.' The angel caught me and pressed me three times, then released me and said: 'Read in the name of your Lord Who created; created man from a clot. Read, and your Lord is the Most Generous' (96:1-3).",
        urduText = "غارِ حرا میں فرشتہ آپ ﷺ کے پاس آیا اور کہا: پڑھو۔ آپ ﷺ نے فرمایا: میں پڑھنا نہیں جانتا۔ تیسری بار چھوڑ کر فرشتے نے کہا: پڑھو اپنے رب کے نام سے جس نے پیدا کیا، جس نے انسان کو جمے ہوئے خون سے پیدا کیا، پڑھو اور تمہارا رب سب سے بڑا کریم ہے۔",
        narrator = "'Aisha Umm al-Mu'minin (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 3",
        linkedQuranVerses = "96:1, 96:2, 96:3, 96:4, 96:5"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 1,
        bookName = "Book 1: Revelation (كتاب بدء الوحي)",
        chapterNumber = 4,
        chapterName = "Recitation of the Qur'an with Jibril in Ramadan",
        hadithNumber = 5,
        darussalamReference = "Darussalam: #5",
        inBookReference = "Book 1, Hadith 5 • Sahih Intl / USC-MSA Vol. 1, Book 1, #5",
        arabicNumber = 5,
        arabicText = "كَانَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ أَجْوَدَ النَّاسِ، وَكَانَ أَجْوَدُ مَا يَكُونُ فِي رَمَضَانَ حِينَ يَلْقَاهُ جِبْرِيلُ، وَكَانَ يَلْقَاهُ فِي كُلِّ لَيْلَةٍ مِنْ رَمَضَانَ فَيُدَارِسُهُ الْقُرْآنَ",
        transliteration = "Kana Rasulullah (ﷺ) ajwadan-nas, wa kana ajwadu ma yakunu fi Ramadana hina yalqahu Jibril, wa kana yalqahu fi kulli laylatin min Ramadana fayudarisuhul-Qur'an.",
        englishText = "Allah's Messenger (ﷺ) was the most generous of all the people, and he used to reach the peak of generosity in the month of Ramadan when Jibril met him. Jibril used to meet him every night of Ramadan to study the Qur'an together with him.",
        urduText = "رسول اللہ ﷺ تمام لوگوں میں سب سے زیادہ سخی تھے، اور رمضان میں جب حضرت جبرائیل علیہ السلام آپ سے ملتے تو آپ کی سخاوت انتہا کو پہنچ جاتی۔ حضرت جبرائیل رمضان کی ہر رات آپ سے ملتے اور آپ کے ساتھ قرآن کا دور کرتے تھے۔",
        narrator = "'Abdullah ibn 'Abbas (RA)",
        grade = "Sahih (Darussalam / Original Sahih al-Bukhari)",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 5",
        linkedQuranVerses = "75:16, 75:17, 2:185"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 2,
        bookName = "Book 2: Belief (كتاب الإيمان)",
        chapterNumber = 3,
        chapterName = "A Muslim is the One from Whose Tongue and Hands Muslims are Safe",
        hadithNumber = 10,
        darussalamReference = "Darussalam: #10",
        inBookReference = "Book 2, Hadith 3 • Sahih Intl / USC-MSA Vol. 1, Book 2, #9",
        arabicNumber = 10,
        arabicText = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ، وَالْمُهَاجِرُ مَنْ هَجَرَ مَا نَهَى اللَّهُ عَنْهُ",
        transliteration = "Al-Muslimu man salimal-Muslimuna min lisanihi wa yadih, wal-Muhajiru man hajara ma nahallahu 'anh.",
        englishText = "The Prophet (ﷺ) said: 'A Muslim is the one from whose tongue and hands the Muslims are safe; and a Muhajir (emigrant) is the one who abandons what Allah has forbidden.'",
        urduText = "نبی کریم ﷺ نے فرمایا: کامل مسلمان وہ ہے جس کی زبان اور ہاتھ سے دوسرے مسلمان محفوظ رہیں، اور حقیقی مہاجر وہ ہے جو ان کاموں کو چھوڑ دے جن سے اللہ نے منع فرمایا ہے۔",
        narrator = "'Abdullah ibn 'Amr (RA)",
        grade = "Sahih (Darussalam / Original Sahih al-Bukhari)",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 10",
        linkedQuranVerses = "49:11, 33:58"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 2,
        bookName = "Book 2: Belief (كتاب الإيمان)",
        chapterNumber = 17,
        chapterName = "Sanctity of Life and Property through Testimony and Prayer",
        hadithNumber = 25,
        darussalamReference = "Darussalam: #25",
        inBookReference = "Book 2, Hadith 18 • Sahih Intl / USC-MSA Vol. 1, Book 2, #24",
        arabicNumber = 25,
        arabicText = "أُمِرْتُ أَنْ أُقَاتِلَ النَّاسَ حَتَّى يَشْهَدُوا أَنْ لَا إِلَهَ إِلَّا اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَيُقِيمُوا الصَّلَاةَ، وَيُؤْتُوا الزَّكَاةَ، فَإِذَا فَعَلُوا ذَلِكَ عَصَمُوا مِنِّي دِمَاءَهُمْ وَأَمْوَالَهُمْ إِلَّا بِحَقِّ الْإِسْلَامِ، وَحِسَابُهُمْ عَلَى اللَّهِ",
        transliteration = "Umirtu an uqatilan-nasa hatta yashhadu an la ilaha illallahu wa anna Muhammadan Rasulullah, wa yuqimus-salata, wa yu'tuz-zakah...",
        englishText = "Allah's Messenger (ﷺ) said: 'I have been ordered that people testify that none has the right to be worshipped but Allah and that Muhammad is Allah's Messenger, and establish the prayer and give the Zakat; when they do so, they protect their blood and property from me except by Islamic law, and their reckoning is with Allah.'",
        urduText = "رسول اللہ ﷺ نے فرمایا: مجھے حکم دیا گیا ہے کہ لوگ گواہی دیں کہ اللہ کے سوا کوئی معبود نہیں اور محمد ﷺ اللہ کے رسول ہیں، اور نماز قائم کریں اور زکوٰۃ ادا کریں۔ جب وہ ایسا کر لیں تو ان کے جان و مال محفوظ ہو گئے سوائے حقِ اسلام کے، اور ان کے دلوں کا حساب اللہ پر ہے۔",
        narrator = "'Abdullah ibn 'Umar (RA)",
        grade = "Sahih (Darussalam / Original Sahih al-Bukhari)",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 25",
        linkedQuranVerses = "9:5, 88:21, 88:22"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 2,
        bookName = "Book 2: Belief (كتاب الإيمان)",
        chapterNumber = 1,
        chapterName = "The Five Pillars of Islam",
        hadithNumber = 8,
        inBookReference = "Book 2, Hadith 1 • USC-MSA Vol. 1, Book 2, #7",
        arabicText = "بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ شَهَادَةِ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَإِقَامِ الصَّلاَةِ، وَإِيتَاءِ الزَّكَاةِ، وَالحَجِّ، وَصَوْمِ رَمَضَانَ",
        transliteration = "Buniyal-Islamu 'ala khamsin: shahadati an la ilaha illallahu wa anna Muhammadan Rasulullahi, wa iqamis-salati, wa ita'iz-zakati, wal-hajji, wa sawmi Ramadan.",
        englishText = "Islam is built upon five pillars: testifying that none has the right to be worshipped but Allah and Muhammad is Allah's Messenger, establishing the prayer, paying Zakat, performing Hajj to the House, and fasting the month of Ramadan.",
        urduText = "اسلام کی بنیاد پانچ چیزوں پر ہے: گواہی دینا کہ اللہ کے سوا کوئی معبود نہیں اور محمد ﷺ اللہ کے سچے رسول ہیں، نماز قائم کرنا، زکوٰۃ دینا، حج کرنا، اور رمضان کے روزے رکھنا۔",
        narrator = "'Abdullah ibn 'Umar (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 8",
        linkedQuranVerses = "2:43, 2:183, 3:97"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 2,
        bookName = "Book 2: Belief (كتاب الإيمان)",
        chapterNumber = 39,
        chapterName = "Superiority of one who leaves doubtful matters",
        hadithNumber = 52,
        inBookReference = "Book 2, Hadith 45 • USC-MSA Vol. 1, Book 2, #49",
        arabicText = "الْحَلاَلُ بَيِّنٌ وَالْحَرَامُ بَيِّنٌ وَبَيْنَهُمَا مُشَبَّهَاتٌ لاَ يَعْلَمُهَا كَثِيرٌ مِنَ النَّاسِ... أَلاَ وَإِنَّ فِي الْجَسَدِ مُضْغَةً إِذَا صَلَحَتْ صَلَحَ الْجَسَدُ كُلُّهُ وَإِذَا فَسَدَتْ فَسَدَ الْجَسَدُ كُلُّهُ أَلاَ وَهِيَ الْقَلْبُ",
        transliteration = "Al-halalu bayyinun wal-haramu bayyinun wa baynahuma mushabbahatun la ya'lamuha kathirun minan-nas... Ala wa inna fil-jasadi mudghatan idha salahat salahal-jasadu kulluhu wa idha fasadat fasadal-jasadu kulluhu, ala wa hiyal-qalb.",
        englishText = "Both legal and illegal things are obvious, and in between them are doubtful matters unknown to many people. So whoever saves himself from doubtful matters saves his religion and his honor... Beware! There is a piece of flesh in the body if it becomes good the whole body becomes good, and if it gets spoilt the whole body gets spoilt — and that is the heart.",
        urduText = "حلال بھی واضح ہے اور حرام بھی واضح ہے اور ان دونوں کے درمیان کچھ مشتبہ چیزیں ہیں جنہیں بہت سے لوگ نہیں جانتے... خبردار! جسم میں گوشت کا ایک ٹکڑا ہے، جب وہ سنور جائے تو پورا جسم سنور جاتا ہے اور جب وہ بگڑ جائے تو پورا جسم بگڑ جاتا ہے، سن لو وہ دل ہے۔",
        narrator = "An-Nu'man ibn Bashir (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 52",
        linkedQuranVerses = "2:168, 26:88, 26:89"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 3,
        bookName = "Book 3: Knowledge (كتاب العلم)",
        chapterNumber = 10,
        chapterName = "Make things easy and do not make them difficult",
        hadithNumber = 69,
        inBookReference = "Book 3, Hadith 11 • USC-MSA Vol. 1, Book 3, #69",
        arabicText = "يَسِّرُوا وَلاَ تُعَسِّرُوا، وَبَشِّرُوا وَلاَ تُنَفِّرُوا",
        transliteration = "Yassiru wa la tu'assiru, wa bashshiru wa la tunaffiru.",
        englishText = "Make things easy for the people, and do not make it difficult for them, and give them good tidings and do not repel them.",
        urduText = "لوگوں کے لیے آسانیاں پیدا کرو اور تنگی میں نہ ڈالو، خوشخبری سناؤ اور نفرت نہ دلاؤ۔",
        narrator = "Anas ibn Malik (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 69",
        linkedQuranVerses = "2:185, 22:78"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 40,
        bookName = "Book 40: Representation & Agency (كتاب الوكالة)",
        chapterNumber = 10,
        chapterName = "Virtue of Ayat al-Kursi (2:255)",
        hadithNumber = 2311,
        inBookReference = "Book 40, Hadith 11 • USC-MSA Vol. 3, Book 38, #505",
        arabicText = "إِذَا أَوَيْتَ إِلَى فِرَاشِكَ فَاقْرَأْ آيَةَ الْكُرْسِيِّ لَنْ يَزَالَ عَلَيْكَ مِنَ اللَّهِ حَافِظٌ، وَلاَ يَقْرَبُكَ شَيْطَانٌ حَتَّى تُصْبِحَ",
        transliteration = "Idha awayta ila firashika faqra' Ayatal-Kursi, lan yazala 'alayka minallahi hafizun, wa la yaqrabuka shaytanun hatta tusbih.",
        englishText = "When you go to bed, recite Ayat al-Kursi (Allahu la ilaha illa Huwal-Hayyul-Qayyum) till you finish the verse; then a guardian from Allah will keep guarding you, and no Satan will approach you until morning. The Prophet (ﷺ) said: 'He told you the truth, though he is a liar.'",
        urduText = "جب تم اپنے بستر پر جاؤ تو آیت الکرسی پڑھ لیا کرو، اللہ کی طرف سے ایک محافظ ہمیشہ تمہاری حفاظت کرتا رہے گا اور صبح تک شیطان تمہارے قریب نہیں آ سکے گا۔",
        narrator = "Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 2311",
        linkedQuranVerses = "2:255"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 65,
        bookName = "Book 65: Prophetic Commentary on the Qur'an (كتاب التفسير)",
        chapterNumber = 1,
        chapterName = "Surah Al-Fatihah — The Greatest Surah in the Qur'an",
        hadithNumber = 4474,
        inBookReference = "Book 65, Hadith 1 • USC-MSA Vol. 6, Book 60, #1",
        arabicText = "لأُعَلِّمَنَّكَ سُورَةً هِيَ أَعْظَمُ السُّوَرِ فِي الْقُرْآنِ... الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ هِيَ السَّبْعُ الْمَثَانِي وَالْقُرْآنُ الْعَظِيمُ الَّذِي أُوتِيتُهُ",
        transliteration = "La-u'allimannaka suratan hiya a'zamus-suwari fil-Qur'an... Al-hamdu lillahi Rabbil-'alamin, hiyas-Sab'ul-Mathani wal-Qur'anul-'Azimul-ladhi utituh.",
        englishText = "The Prophet (ﷺ) said to Abu Sa'id ibn al-Mu'alla: 'I will teach you a Surah which is the greatest Surah in the Qur'an before you leave the mosque.' He said: 'Al-hamdu lillahi Rabbil-'alamin (Surah Al-Fatihah); it is the Seven Oft-Repeated Verses (Al-Sab' Al-Mathani) and the Grand Qur'an which has been given to me.'",
        urduText = "نبی کریم ﷺ نے فرمایا: میں تمہیں مسجد سے نکلنے سے پہلے قرآن کی سب سے عظیم سورت سکھاؤں گا۔ پھر فرمایا: الحمد للہ رب العالمین (سورۃ الفاتحہ)، یہی سبع مثانی ہے اور یہی قرآن عظیم ہے جو مجھے عطا کیا گیا ہے۔",
        narrator = "Abu Sa'id ibn al-Mu'alla (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 4474",
        linkedQuranVerses = "1:1, 1:2, 15:87"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 66,
        bookName = "Book 66: Virtues of the Qur'an (كتاب فضائل القرآن)",
        chapterNumber = 21,
        chapterName = "The Best Among You Learn and Teach the Qur'an",
        hadithNumber = 5027,
        inBookReference = "Book 66, Hadith 49 • USC-MSA Vol. 6, Book 61, #545",
        arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
        transliteration = "Khayrukum man ta'allamal-Qur'ana wa 'allamahu.",
        englishText = "The best among you (Muslims) are those who learn the Qur'an and teach it.",
        urduText = "تم میں سے بہترین شخص وہ ہے جو قرآن سیکھے اور دوسروں کو سکھائے۔",
        narrator = "'Uthman ibn 'Affan (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 5027",
        linkedQuranVerses = "35:29, 54:17, 73:4"
      ),

      // ==================== 2. SAHIH MUSLIM ====================
      HadithEntity(
        collectionKey = "muslim",
        collectionName = "Sahih Muslim",
        bookNumber = 1,
        bookName = "Book 1: Faith (كتاب الإيمان)",
        chapterNumber = 1,
        chapterName = "Inquiry on Faith and Pillars of Islam",
        hadithNumber = 5,
        darussalamReference = "Darussalam: #5",
        inBookReference = "Book 1, Hadith 5 • Sahih Intl / Fu'ad Abd al-Baqi #9",
        arabicNumber = 9,
        arabicText = "كَانَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ يَوْمًا بَارِزًا لِلنَّاسِ فَأَتَاهُ رَجُلٌ فَقَالَ يَا رَسُولَ اللَّهِ مَا الإِيمَانُ قَالَ أَنْ تُؤْمِنَ بِاللَّهِ وَمَلاَئِكَتِهِ وَكِتَابِهِ وَلِقَائِهِ وَرُسُلِهِ وَتُؤْمِنَ بِالْبَعْثِ الآخِرِ",
        transliteration = "Kana Rasulullah (ﷺ) yawman barizan lin-nasi fa-atahu rajulun faqala: Ya Rasulallah, ma al-Iman? Qala: An tu'mina billahi wa mala'ikatihi wa kitabihi wa liqa'ihi wa rusulihi wa tu'mina bil-ba'thil-akhir.",
        englishText = "One day the Messenger of Allah (ﷺ) appeared before the public; a man came to him and asked: 'O Messenger of Allah, what is Iman (Faith)?' He replied: 'That you believe in Allah, His angels, His Book, the meeting with Him, His Messengers, and that you believe in the final Resurrection.'",
        urduText = "ایک دن رسول اللہ ﷺ لوگوں کے درمیان تشریف فرما تھے کہ ایک شخص آیا اور عرض کیا: یا رسول اللہ! ایمان کیا ہے؟ آپ ﷺ نے فرمایا: یہ کہ تم اللہ پر، اس کے فرشتوں پر، اس کی کتاب پر، اس سے ملاقات پر، اس کے رسولوں پر اور آخرت میں دوبارہ زندہ کیے جانے پر ایمان لاؤ۔",
        narrator = "Abu Hurairah (RA)",
        grade = "Sahih (Darussalam / Original Sahih Muslim)",
        gradingScholar = "Imam Muslim",
        reference = "Sahih Muslim 5",
        linkedQuranVerses = "2:285, 4:136"
      ),
      HadithEntity(
        collectionKey = "muslim",
        collectionName = "Sahih Muslim",
        bookNumber = 1,
        bookName = "Book 1: Faith (كتاب الإيمان)",
        chapterNumber = 1,
        chapterName = "Hadith of Jibril — Islam, Iman, Ihsan",
        hadithNumber = 8,
        darussalamReference = "Darussalam: #8",
        inBookReference = "Book 1, Hadith 1 • Fu'ad Abd al-Baqi #8",
        arabicNumber = 8,
        arabicText = "الإِسْلامُ أَنْ تَشْهَدَ أَنْ لا إِلَهَ إِلا اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَتُقِيمَ الصَّلاةَ، وَتُؤْتِيَ الزَّكَاةَ، وَتَصُومَ رَمَضَانَ، وَتَحُجَّ الْبَيْتَ... الإِحْسَانُ أَنْ تَعْبُدَ اللَّهَ كَأَنَّكَ تَرَاهُ، فَإِنْ لَمْ تَكُنْ تَرَاهُ فَإِنَّهُ يَرَاكَ",
        transliteration = "Al-Islamu an tashhada an la ilaha illallah wa anna Muhammadan Rasulullah... Al-Ihsanu an ta'budallaha ka'annaka tarahu, fa in lam takun tarahu fa innahu yarak.",
        englishText = "Jibril asked about Islam, Iman (Faith), and Ihsan (Spiritual Excellence). The Prophet (ﷺ) defined Islam by the five pillars, Iman by belief in Allah, His angels, His books, His messengers, the Last Day, and Divine Decree, and Ihsan as worshipping Allah as though you see Him, for if you do not see Him, He truly sees you.",
        urduText = "حضرت جبرائیل علیہ السلام نے اسلام، ایمان اور احسان کے بارے میں سوال کیا۔ آپ ﷺ نے فرمایا: احسان یہ ہے کہ تم اللہ کی عبادت اس طرح کرو گویا تم اسے دیکھ رہے ہو، پس اگر تم اسے نہیں دیکھ رہے تو وہ یقیناً تمہیں دیکھ رہا ہے۔",
        narrator = "'Umar ibn al-Khattab (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Muslim",
        reference = "Sahih Muslim 8",
        linkedQuranVerses = "2:177, 2:285, 4:136, 31:34"
      ),
      HadithEntity(
        collectionKey = "muslim",
        collectionName = "Sahih Muslim",
        bookNumber = 1,
        bookName = "Book 1: Faith (كتاب الإيمان)",
        chapterNumber = 2,
        chapterName = "The Five Daily Prayers, Fasting Ramadan, and Zakat",
        hadithNumber = 10,
        darussalamReference = "Darussalam: #10",
        inBookReference = "Book 1, Hadith 8 • Sahih Intl / Fu'ad Abd al-Baqi #11",
        arabicNumber = 11,
        arabicText = "جَاءَ رَجُلٌ إِلَى رَسُولِ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ مِنْ أَهْلِ نَجْدٍ ثَائِرُ الرَّأْسِ... فَقَالَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ: أَفْلَحَ وَأَبِيهِ إِنْ صَدَقَ، أَوْ دَخَلَ الْجَنَّةَ وَأَبِيهِ إِنْ صَدَقَ",
        transliteration = "Ja'a rajulun ila Rasulillahi (ﷺ) min ahli Najdin tha'irur-ra's... Faqala Rasulullah (ﷺ): Aflaha wa abihi in sadaq, aw dakhalal-Jannata wa abihi in sadaq.",
        englishText = "A man from the people of Najd with disheveled hair came to the Messenger of Allah (ﷺ) asking about Islam. When told of the five prayers in the day and night, fasting Ramadan, and Zakat, he said: 'By Allah, I will neither add to this nor decrease from it.' The Messenger of Allah (ﷺ) said: 'He has succeeded, if he is truthful.'",
        urduText = "اہلِ نجد میں سے ایک شخص رسول اللہ ﷺ کی خدمت میں حاضر ہوا اور اسلام کے بارے میں سوال کیا۔ جب آپ ﷺ نے اسے دن رات کی پانچ نمازیں، رمضان کے روزے اور زکوٰۃ بتائیں تو اس نے کہا کہ میں اس میں نہ کمی کروں گا نہ زیادتی۔ رسول اللہ ﷺ نے فرمایا: اگر یہ سچ کہہ رہا ہے تو کامیاب ہو گیا۔",
        narrator = "Talhah ibn 'Ubaydullah (RA)",
        grade = "Sahih (Darussalam / Original Sahih Muslim)",
        gradingScholar = "Imam Muslim",
        reference = "Sahih Muslim 10",
        linkedQuranVerses = "23:1, 23:2, 23:9"
      ),
      HadithEntity(
        collectionKey = "muslim",
        collectionName = "Sahih Muslim",
        bookNumber = 1,
        bookName = "Book 1: Faith (كتاب الإيمان)",
        chapterNumber = 10,
        chapterName = "Glad Tidings for the Sincere Testimony of Tawhid",
        hadithNumber = 25,
        darussalamReference = "Darussalam: #25",
        inBookReference = "Book 1, Hadith 46 • Sahih Intl / Fu'ad Abd al-Baqi #29",
        arabicNumber = 29,
        arabicText = "مَنْ شَهِدَ أَنْ لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، وَأَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ، وَأَنَّ عِيسَى عَبْدُ اللَّهِ وَابْنُ أَمَتِهِ وَكَلِمَتُهُ أَلْقَاهَا إِلَى مَرْيَمَ وَرُوحٌ مِنْهُ، وَأَنَّ الْجَنَّةَ حَقٌّ، وَأَنَّ النَّارَ حَقٌّ، أَدْخَلَهُ اللَّهُ مِنْ أَيِّ أَبْوَابِ الْجَنَّةِ الثَّمَانِيَةِ شَاءَ",
        transliteration = "Man shahida an la ilaha illallahu wahdahu la sharika lahu, wa anna Muhammadan 'abduhu wa rasuluhu... adkhalahullahu min ayyi abwabil-Jannatith-thamaniyati sha'.",
        englishText = "Whoever testifies that there is no deity worthy of worship except Allah alone with no partner, that Muhammad is His servant and Messenger, that 'Isa (Jesus) is the servant of Allah, the son of His maidservant, and His Word which He bestowed upon Maryam and a spirit from Him, and that Paradise is true and Hellfire is true, Allah will admit him through whichever of the eight gates of Paradise he wishes.",
        urduText = "جو شخص گواہی دے کہ اللہ کے سوا کوئی معبود نہیں، وہ اکیلا ہے اس کا کوئی شریک نہیں، اور محمد ﷺ اس کے بندے اور رسول ہیں، اور عیسیٰ علیہ السلام اللہ کے بندے اور اس کا کلمہ ہیں، اور جنت حق ہے اور دوزخ حق ہے، تو اللہ اسے جنت کے آٹھوں دروازوں میں سے جس سے چاہے داخل فرمائے گا۔",
        narrator = "'Ubadah ibn as-Samit (RA)",
        grade = "Sahih (Darussalam / Original Sahih Muslim)",
        gradingScholar = "Imam Muslim",
        reference = "Sahih Muslim 25",
        linkedQuranVerses = "4:171, 3:45"
      ),
      HadithEntity(
        collectionKey = "muslim",
        collectionName = "Sahih Muslim",
        bookNumber = 2,
        bookName = "Book 2: Purification (كتاب الطهارة)",
        chapterNumber = 1,
        chapterName = "Purity is Half of Faith",
        hadithNumber = 223,
        inBookReference = "Book 2, Hadith 1 • Fu'ad Abd al-Baqi #223",
        arabicText = "الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ، وَسُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ تَمْلآنِ مَا بَيْنَ السَّمَاوَاتِ وَالأَرْضِ، وَالصَّلاَةُ نُورٌ، وَالصَّدَقَةُ بُرْهَانٌ، وَالصَّبْرُ ضِيَاءٌ، وَالْقُرْآنُ حُجَّةٌ لَكَ أَوْ عَلَيْكَ",
        transliteration = "At-tuhuru shatrul-iman, wal-hamdu lillahi tamla'ul-mizan, wa SubhanAllahi wal-hamdu lillahi tamla'ani ma baynas-samawati wal-ard, was-salatu nur, was-sadaqatu burhan, was-sabru diya', wal-Qur'anu hujjatun laka aw 'alayk.",
        englishText = "Purity is half of faith, Alhamdulillah fills the scale, SubhanAllah and Alhamdulillah fill what is between the heavens and the earth, prayer is light, charity is proof, patience is illumination, and the Qur'an is a proof for you or against you.",
        urduText = "پاکیزگی نصف ایمان ہے، اور الحمد للہ میزان کو بھر دیتا ہے، اور سبحان اللہ اور الحمد للہ آسمان و زمین کے درمیانی خلا کو بھر دیتے ہیں، اور نماز نور ہے، صدقہ دلیل ہے، صبر روشنی ہے، اور قرآن تمہارے حق میں یا تمہارے خلاف حجت ہے۔",
        narrator = "Abu Malik al-Ash'ari (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Muslim",
        reference = "Sahih Muslim 223",
        linkedQuranVerses = "5:6, 2:222, 17:82"
      ),
      HadithEntity(
        collectionKey = "muslim",
        collectionName = "Sahih Muslim",
        bookNumber = 6,
        bookName = "Book 6: Travelers' Prayer & Virtues of Qur'an (كتاب صلاة المسافرين)",
        chapterNumber = 42,
        chapterName = "Protection of First Ten Verses of Surah Al-Kahf",
        hadithNumber = 809,
        inBookReference = "Book 6, Hadith 311 • Fu'ad Abd al-Baqi #809",
        arabicText = "مَنْ حَفِظَ عَشْرَ آيَاتٍ مِنْ أَوَّلِ سُورَةِ الْكَهْفِ عُصِمَ مِنَ الدَّجَّالِ",
        transliteration = "Man hafiza 'ashra ayatin min awwali Suratil-Kahfi 'usima minad-Dajjal.",
        englishText = "Whoever memorizes ten verses from the beginning of Surah Al-Kahf will be protected from the False Messiah (Ad-Dajjal).",
        urduText = "جس شخص نے سورۃ الکہف کی ابتدائی دس آیات حفظ کر لیں وہ دجال کے فتنے سے محفوظ رکھا جائے گا۔",
        narrator = "Abu ad-Darda' (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Muslim",
        reference = "Sahih Muslim 809",
        linkedQuranVerses = "18:1, 18:2, 18:3, 18:10"
      ),
      HadithEntity(
        collectionKey = "muslim",
        collectionName = "Sahih Muslim",
        bookNumber = 45,
        bookName = "Book 45: Virtue and Good Manners (كتاب البر والصلة)",
        chapterNumber = 10,
        chapterName = "Brotherhood and Prohibition of Mutual Envy",
        hadithNumber = 2564,
        inBookReference = "Book 45, Hadith 32 • Fu'ad Abd al-Baqi #2564",
        arabicText = "لاَ تَحَاسَدُوا، وَلاَ تَنَاجَشُوا، وَلاَ تَبَاغَضُوا، وَلاَ تَدَابَرُوا... الْمُسْلِمُ أَخُو الْمُسْلِمِ، لاَ يَظْلِمُهُ وَلاَ يَخْذُلُهُ وَلاَ يَحْقِرُهُ، التَّقْوَى هَاهُنَا",
        transliteration = "La tahasadu, wa la tanajashu, wa la tabaghadu, wa la tadabaru... Al-Muslimu akhul-Muslim, la yazlimuhu wa la yakhdhuluhu wa la yahqiruh. At-taqwa hahuna.",
        englishText = "Do not envy one another, do not inflate prices to deceive one another, do not hate one another, and do not turn your backs on one another. Be servants of Allah as brothers. A Muslim is the brother of a Muslim: he does not oppress him, nor does he forsake him, nor does he look down upon him. Piety (Taqwa) is right here (pointing to his chest three times).",
        urduText = "ایک دوسرے سے حسد نہ کرو، باہم بغض نہ رکھو، ایک دوسرے سے منہ نہ موڑو اور اللہ کے بندے بن کر بھائی بھائی ہو جاؤ۔ مسلمان مسلمان کا بھائی ہے، نہ اس پر ظلم کرے، نہ اسے بےیارومددگار چھوڑے اور نہ اسے حقیر سمجھے۔ تقویٰ یہاں ہے۔",
        narrator = "Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Muslim",
        reference = "Sahih Muslim 2564",
        linkedQuranVerses = "49:10, 49:12, 49:13"
      ),
      HadithEntity(
        collectionKey = "muslim",
        collectionName = "Sahih Muslim",
        bookNumber = 48,
        bookName = "Book 48: Remembrance & Supplication (كتاب الذكر والدعاء)",
        chapterNumber = 11,
        chapterName = "Relieving Hardship and Seeking Sacred Knowledge",
        hadithNumber = 2699,
        inBookReference = "Book 48, Hadith 38 • Fu'ad Abd al-Baqi #2699",
        arabicText = "مَنْ نَفَّسَ عَنْ مُؤْمِنٍ كُرْبَةً مِنْ كُرَبِ الدُّنْيَا نَفَّسَ اللَّهُ عَنْهُ كُرْبَةً مِنْ كُرَبِ يَوْمِ الْقِيَامَةِ... وَمَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ",
        transliteration = "Man naffasa 'an mu'minin kurbatan min kurabid-dunya naffasallahu 'anhu kurbatan min kurabi yawmil-qiyamah... wa man salaka tariqan yaltamisu fihi 'ilman sahhalallahu lahu bihi tariqan ilal-Jannah.",
        englishText = "Whoever relieves a believer of a hardship of this world, Allah will relieve him of a hardship of the Day of Resurrection... And whoever treads a path in search of sacred knowledge, Allah makes easy for him a path to Paradise.",
        urduText = "جو شخص کسی مومن کی دنیاوی تکلیفوں میں سے کوئی تکلیف دور کرے، اللہ تعالیٰ قیامت کے دن اس کی تکلیفوں میں سے ایک بڑی تکلیف دور فرمائے گا... اور جو شخص حصولِ علم کے لیے کسی راستے پر چلے، اللہ اس کے لیے جنت کا راستہ آسان فرما دیتا ہے۔",
        narrator = "Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Muslim",
        reference = "Sahih Muslim 2699",
        linkedQuranVerses = "20:114, 39:9, 58:11"
      ),

      // ==================== 3. SUNAN ABU DAWUD ====================
      HadithEntity(
        collectionKey = "abudawud",
        collectionName = "Sunan Abu Dawud",
        bookNumber = 1,
        bookName = "Book 1: Purification (كتاب الطهارة)",
        chapterNumber = 1,
        chapterName = "No Prayer is Accepted Without Purification",
        hadithNumber = 61,
        inBookReference = "Book 1, Hadith 61 • Darussalam Intl #61",
        arabicText = "مِفْتَاحُ الصَّلاَةِ الطُّهُورُ وَتَحْرِيمُهَا التَّكْبِيرُ وَتَحْلِيلُهَا التَّسْلِيمُ",
        transliteration = "Miftahus-salatit-tuhur, wa tahrimuhat-takbir, wa tahliluhat-taslim.",
        englishText = "The key to prayer is purification, its consecration is the Takbir (Allahu Akbar), and its conclusion is the Taslim (As-salamu alaykum).",
        urduText = "نماز کی کنجی پاکیزگی (وضو) ہے، اس کی تحریم تکبیر (اللہ اکبر) ہے اور اس کی تحلیل سلام پھیرنا ہے۔",
        narrator = "'Ali ibn Abi Talib (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Abu Dawud & Shaykh al-Albani",
        reference = "Sunan Abu Dawud 61",
        linkedQuranVerses = "5:6, 74:4"
      ),
      HadithEntity(
        collectionKey = "abudawud",
        collectionName = "Sunan Abu Dawud",
        bookNumber = 8,
        bookName = "Book 8: Prayer & Witr (كتاب الوتر)",
        chapterNumber = 20,
        chapterName = "Elevation in Paradise Through Reciting the Qur'an",
        hadithNumber = 1464,
        inBookReference = "Book 8, Hadith 49 • Darussalam Intl #1464",
        arabicText = "يُقَالُ لِصَاحِبِ الْقُرْآنِ اقْرَأْ وَارْتَقِ وَرَتِّلْ كَمَا كُنْتَ تُرَتِّلُ فِي الدُّنْيَا فَإِنَّ مَنْزِلَتَكَ عِنْدَ آخِرِ آيَةٍ تَقْرَؤُهَا",
        transliteration = "Yuqalu li-sahibil-Qur'ani: Iqra' wartaqi wa rattil kama kunta turattilu fid-dunya, fa-inna manzilataka 'inda akhiri ayatin taqra'uha.",
        englishText = "It will be said to the companion of the Qur'an: 'Read, ascend in ranks, and recite with measured beauty (Tartil) just as you used to recite in the world, for your station in Paradise is at the last verse you recite.'",
        urduText = "صاحبِ قرآن سے (قیامت کے دن) کہا جائے گا: پڑھتا جا اور درجات چڑھتا جا اور اسی طرح ٹھہر ٹھہر کر (ترتیل سے) پڑھ جیسے تو دنیا میں پڑھتا تھا، پس تیرا مقام آخری آیت کے پاس ہوگا جو تو پڑھے گا۔",
        narrator = "'Abdullah ibn 'Amr (RA)",
        grade = "Hasan Sahih",
        gradingScholar = "Imam at-Tirmidhi & Abu Dawud",
        reference = "Sunan Abu Dawud 1464",
        linkedQuranVerses = "73:4, 35:29"
      ),
      HadithEntity(
        collectionKey = "abudawud",
        collectionName = "Sunan Abu Dawud",
        bookNumber = 26,
        bookName = "Book 26: Knowledge (كتاب العلم)",
        chapterNumber = 1,
        chapterName = "Scholars are the Inheritors of the Prophets",
        hadithNumber = 3641,
        inBookReference = "Book 26, Hadith 1 • Darussalam Intl #3641",
        arabicText = "وَإِنَّ الْعُلَمَاءَ وَرَثَةُ الأَنْبِيَاءِ وَإِنَّ الأَنْبِيَاءَ لَمْ يُوَرِّثُوا دِينَارًا وَلاَ دِرْهَمًا وَإِنَّمَا وَرَّثُوا الْعِلْمَ فَمَنْ أَخَذَ بِهِ أَخَذَ بِحَظٍّ وَافِرٍ",
        transliteration = "Wa innal-'ulama'a warathatul-anbiya', wa innal-anbiya'a lam yuwarrithu dinaran wa la dirhaman, wa innama warrathul-'ilma faman akhadha bihi akhadha bi-hazzin wafir.",
        englishText = "Indeed the scholars are the heirs of the Prophets, and the Prophets did not leave behind gold dinars or silver dirhams; rather they left behind sacred knowledge, so whoever takes hold of it has acquired an abundant share.",
        urduText = "بے شک علماء انبیاء کے وارث ہیں، اور انبیاء نے درہم و دینار کی میراث نہیں چھوڑی بلکہ انہوں نے علم ہی کی میراث چھوڑی ہے، پس جس نے اسے حاصل کیا اس نے وافر حصہ پا لیا۔",
        narrator = "Abu ad-Darda' (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Abu Dawud",
        reference = "Sunan Abu Dawud 3641",
        linkedQuranVerses = "35:28, 27:16"
      ),
      HadithEntity(
        collectionKey = "abudawud",
        collectionName = "Sunan Abu Dawud",
        bookNumber = 43,
        bookName = "Book 43: General Behavior / Adab (كتاب الأدب)",
        chapterNumber = 7,
        chapterName = "Guarantee of a House in Paradise for Good Character",
        hadithNumber = 4800,
        inBookReference = "Book 43, Hadith 28 • Darussalam Intl #4800",
        arabicText = "أَنَا زَعِيمٌ بِبَيْتٍ فِي رَبَضِ الْجَنَّةِ لِمَنْ تَرَكَ الْمِرَاءَ وَإِنْ كَانَ مُحِقًّا وَبِبَيْتٍ فِي وَسَطِ الْجَنَّةِ لِمَنْ تَرَكَ الْكَذِبَ وَإِنْ كَانَ مَازِحًا وَبِبَيْتٍ فِي أَعْلَى الْجَنَّةِ لِمَنْ حَسَّنَ خُلُقَهُ",
        transliteration = "Ana za'imun bi-baytin fi rabadil-Jannati liman tarakal-mira'a wa in kana muhiqqa, wa bi-baytin fi wasatil-Jannati liman tarakal-kadhiba wa in kana maziha, wa bi-baytin fi a'lal-Jannati liman hassana khuluqah.",
        englishText = "I guarantee a house in the surroundings of Paradise for one who gives up arguing even if he is in the right; and a house in the middle of Paradise for one who abandons lying even when joking; and a house in the highest part of Paradise for one who perfects his character.",
        urduText = "میں اس شخص کے لیے جنت کے اطراف میں ایک گھر کا ضامن ہوں جو حق پر ہونے کے باوجود جھگڑا چھوڑ دے، اور جنت کے وسط میں ایک گھر کا ضامن ہوں جو مذاق میں بھی جھوٹ بولنا چھوڑ دے، اور جنت کے اعلیٰ ترین درجے میں ایک گھر کا ضامن ہوں جو اپنے اخلاق کو عمدہ بنا لے۔",
        narrator = "Abu Umamah al-Bahili (RA)",
        grade = "Hasan",
        gradingScholar = "Imam Abu Dawud & Imam an-Nawawi",
        reference = "Sunan Abu Dawud 4800",
        linkedQuranVerses = "68:4, 9:119"
      ),

      // ==================== 4. JAMI` AT-TIRMIDHI ====================
      HadithEntity(
        collectionKey = "tirmidhi",
        collectionName = "Jami` at-Tirmidhi",
        bookNumber = 27,
        bookName = "Book 27: Righteousness & Maintaining Ties (كتاب البر والصلة)",
        chapterNumber = 16,
        chapterName = "Mercy to Those on Earth",
        hadithNumber = 1924,
        inBookReference = "Book 27, Hadith 30 • Darussalam Intl #1924",
        arabicText = "الرَّاحِمُونَ يَرْحَمُهُمُ الرَّحْمَنُ ارْحَمُوا مَنْ فِي الأَرْضِ يَرْحَمْكُمْ مَنْ فِي السَّمَاءِ",
        transliteration = "Ar-rahimuna yarhamuhumur-Rahman, irhamu man fil-ardi yarhamkum man fis-sama'.",
        englishText = "The merciful are shown mercy by the Most Merciful (Ar-Rahman). Be merciful to those on the earth, and the One above the heavens will have mercy upon you.",
        urduText = "رحم کرنے والوں پر رحمٰن رحم فرماتا ہے۔ تم زمین والوں پر رحم کرو، آسمان والا تم پر رحم فرمائے گا۔",
        narrator = "'Abdullah ibn 'Amr (RA)",
        grade = "Hasan Sahih",
        gradingScholar = "Imam at-Tirmidhi",
        reference = "Jami` at-Tirmidhi 1924",
        linkedQuranVerses = "1:1, 7:156, 21:107"
      ),
      HadithEntity(
        collectionKey = "tirmidhi",
        collectionName = "Jami` at-Tirmidhi",
        bookNumber = 37,
        bookName = "Book 37: Description of the Day of Judgment (كتاب صفة القيامة)",
        chapterNumber = 59,
        chapterName = "Be Mindful of Allah and He Will Protect You",
        hadithNumber = 2516,
        inBookReference = "Book 37, Hadith 102 • Darussalam Intl #2516",
        arabicText = "يَا غُلاَمُ إِنِّي أُعَلِّمُكَ كَلِمَاتٍ احْفَظِ اللَّهَ يَحْفَظْكَ احْفَظِ اللَّهَ تَجِدْهُ تُجَاهَكَ إِذَا سَأَلْتَ فَاسْأَلِ اللَّهَ وَإِذَا اسْتَعَنْتَ فَاسْتَعِنْ بِاللَّهِ",
        transliteration = "Ya ghulamu inni u'allimuka kalimat: Ihfazillaha yahfazka, ihfazillaha tajidhu tujahaka, idha sa'alta fas'alillaha wa idhasta'anta fasta'in billah.",
        englishText = "O young man, I shall teach you some words of counsel: Be mindful of Allah and He will protect you. Be mindful of Allah and you shall find Him with you. When you ask, ask of Allah; and when you seek help, seek help from Allah.",
        urduText = "اے لڑکے! میں تمہیں چند نصیحت کے کلمات سکھاتا ہوں: تم اللہ کے احکام کی حفاظت کرو، اللہ تمہاری حفاظت فرمائے گا۔ اللہ کے حقوق کا خیال رکھو، تم اسے اپنے سامنے پاؤ گے۔ جب کچھ مانگو تو اللہ ہی سے مانگو اور جب مدد چاہو تو اللہ ہی سے مدد طلب کرو۔",
        narrator = "'Abdullah ibn 'Abbas (RA)",
        grade = "Hasan Sahih",
        gradingScholar = "Imam at-Tirmidhi",
        reference = "Jami` at-Tirmidhi 2516",
        linkedQuranVerses = "1:5, 65:2, 65:3"
      ),
      HadithEntity(
        collectionKey = "tirmidhi",
        collectionName = "Jami` at-Tirmidhi",
        bookNumber = 45,
        bookName = "Book 45: Virtues of the Qur'an (كتاب فضائل القرآن)",
        chapterNumber = 16,
        chapterName = "Ten Rewards for Every Single Letter of the Qur'an",
        hadithNumber = 2910,
        inBookReference = "Book 45, Hadith 36 • Darussalam Intl #2910",
        arabicText = "مَنْ قَرَأَ حَرْفًا مِنْ كِتَابِ اللَّهِ فَلَهُ بِهِ حَسَنَةٌ وَالْحَسَنَةُ بِعَشْرِ أَمْثَالِهَا لاَ أَقُولُ الم حَرْفٌ وَلَكِنْ أَلِفٌ حَرْفٌ وَلاَمٌ حَرْفٌ وَمِيمٌ حَرْفٌ",
        transliteration = "Man qara'a harfan min Kitabillahi falahu bihi hasanah, wal-hasanatu bi-'ashri amthaliha. La aqulu Alif-Lam-Mim harfun, walakin Alifun harfun wa Lamun harfun wa Mimun harfun.",
        englishText = "Whoever recites a single letter from the Book of Allah will be credited with a good deed, and a good deed receives a tenfold reward. I do not say that Alif-Lam-Mim is one letter, but Alif is a letter, Lam is a letter, and Mim is a letter.",
        urduText = "جو شخص کتاب اللہ کا ایک حرف پڑھے گا اس کے لیے ایک نیکی ہے اور ہر نیکی کا ثواب دس گنا ہے۔ میں یہ نہیں کہتا کہ الم ایک حرف ہے، بلکہ الف ایک حرف ہے، لام ایک حرف ہے اور میم ایک حرف ہے۔",
        narrator = "'Abdullah ibn Mas'ud (RA)",
        grade = "Hasan Sahih",
        gradingScholar = "Imam at-Tirmidhi",
        reference = "Jami` at-Tirmidhi 2910",
        linkedQuranVerses = "2:1, 6:160, 35:29"
      ),
      HadithEntity(
        collectionKey = "tirmidhi",
        collectionName = "Jami` at-Tirmidhi",
        bookNumber = 48,
        bookName = "Book 48: Supplications / Da'awat (كتاب الدعوات)",
        chapterNumber = 1,
        chapterName = "Supplication is the Essence of Worship",
        hadithNumber = 3371,
        inBookReference = "Book 48, Hadith 2 • Darussalam Intl #3371",
        arabicText = "الدُّعَاءُ هُوَ الْعِبَادَةُ ثُمَّ قَرَأَ (وَقَالَ رَبُّكُمُ ادْعُونِي أَسْتَجِبْ لَكُمْ)",
        transliteration = "Ad-du'a'u huwal-'ibadah, thumma qara'a: Wa qala Rabbukumud-'uni astajib lakum.",
        englishText = "Supplication (Du'a) is worship itself. Then the Prophet (ﷺ) recited the verse: 'And your Lord says: Call upon Me; I will respond to you' (Surah Ghafir 40:60).",
        urduText = "دعا ہی اصل عبادت ہے۔ پھر نبی کریم ﷺ نے یہ آیت تلاوت فرمائی: اور تمہارے رب نے فرمایا مجھ سے دعا کرو میں تمہاری دعا قبول کروں گا (غافر 40:60)۔",
        narrator = "An-Nu'man ibn Bashir (RA)",
        grade = "Hasan Sahih",
        gradingScholar = "Imam at-Tirmidhi",
        reference = "Jami` at-Tirmidhi 3371",
        linkedQuranVerses = "40:60, 2:186"
      ),

      // ==================== 5. SUNAN AN-NASA'I ====================
      HadithEntity(
        collectionKey = "nasai",
        collectionName = "Sunan an-Nasa'i",
        bookNumber = 14,
        bookName = "Book 14: Forgetfulness in Prayer (كتاب السهو)",
        chapterNumber = 58,
        chapterName = "Asking Allah for Guidance, Forgiveness, and Steadfastness",
        hadithNumber = 1305,
        inBookReference = "Book 14, Hadith 88 • Darussalam Intl #1305",
        arabicText = "اللَّهُمَّ بِعِلْمِكَ الْغَيْبَ وَقُدْرَتِكَ عَلَى الْخَلْقِ أَحْيِنِي مَا عَلِمْتَ الْحَيَاةَ خَيْرًا لِي وَتَوَفَّنِي إِذَا عَلِمْتَ الْوَفَاةَ خَيْرًا لِي",
        transliteration = "Allahumma bi-'ilmikal-ghayba wa qudratika 'alal-khalqi ahyini ma 'alimtal-hayata khayran li, wa tawaffani idha 'alimtal-wafata khayran li.",
        englishText = "O Allah, by Your knowledge of the unseen and Your power over creation, keep me alive so long as You know life is best for me, and take me unto Yourself when You know death is best for me; and I ask You for awe of You in secret and in public.",
        urduText = "اے اللہ! اپنے علمِ غیب اور مخلوق پر اپنی قدرت کے وسیلے سے مجھے اس وقت تک زندہ رکھ جب تک تو جانتا ہے کہ زندگی میرے لیے بہتر ہے، اور مجھے اس وقت وفات دے جب تو جانے کہ وفات میرے لیے بہتر ہے۔",
        narrator = "'Ammar ibn Yasir (RA)",
        grade = "Sahih",
        gradingScholar = "Imam an-Nasa'i",
        reference = "Sunan an-Nasa'i 1305",
        linkedQuranVerses = "6:59, 67:2"
      ),
      HadithEntity(
        collectionKey = "nasai",
        collectionName = "Sunan an-Nasa'i",
        bookNumber = 22,
        bookName = "Book 22: Fasting (كتاب الصيام)",
        chapterNumber = 2,
        chapterName = "Opening of the Gates of Paradise in Ramadan",
        hadithNumber = 2106,
        inBookReference = "Book 22, Hadith 17 • Darussalam Intl #2106",
        arabicText = "قَدْ جَاءَكُمْ رَمَضَانُ شَهْرٌ مُبَارَكٌ افْتَرَضَ اللَّهُ عَزَّ وَجَلَّ عَلَيْكُمْ صِيَامَهُ تُفْتَحُ فِيهِ أَبْوَابُ السَّمَاءِ وَتُغْلَقُ فِيهِ أَبْوَابُ الْجَحِيمِ... فِيهِ لَيْلَةٌ خَيْرٌ مِنْ أَلْفِ شَهْرٍ",
        transliteration = "Qad ja'akum Ramadanu shahrun mubarakun iftaradallahu 'azza wa jalla 'alaykum siyamahu, tuftahu fihi abwabus-sama'i wa tughlaqu fihi abwabul-jahim... fihi laylatun khayrun min alfi shahr.",
        englishText = "Ramadan has come to you, a blessed month in which Allah Mighty and Sublime has obligated you to fast. In it the gates of heaven are opened, the gates of Hellfire are closed, and the rebellious devils are chained. In it is a night (Laylat al-Qadr) better than a thousand months.",
        urduText = "تمہارے پاس رمضان کا بابرکت مہینہ آیا ہے جس کے روزے اللہ عزوجل نے تم پر فرض کیے ہیں، اس میں آسمان کے دروازے کھول دیے جاتے ہیں اور دوزخ کے دروازے بند کر دیے جاتے ہیں، اور اس میں ایک رات ہے جو ہزار مہینوں سے بہتر ہے۔",
        narrator = "Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam an-Nasa'i",
        reference = "Sunan an-Nasa'i 2106",
        linkedQuranVerses = "2:183, 2:185, 97:1, 97:3"
      ),
      HadithEntity(
        collectionKey = "nasai",
        collectionName = "Sunan an-Nasa'i",
        bookNumber = 25,
        bookName = "Book 25: Jihad & Striving (كتاب الجهاد)",
        chapterNumber = 6,
        chapterName = "Paradise Lies at the Feet of Mothers",
        hadithNumber = 3104,
        inBookReference = "Book 25, Hadith 20 • Darussalam Intl #3104",
        arabicText = "هَلْ لَكَ مِنْ أُمٍّ؟ قَالَ نَعَمْ. قَالَ فَالْزَمْهَا فَإِنَّ الْجَنَّةَ تَحْتَ رِجْلَيْهَا",
        transliteration = "Hal laka min umm? Qala: Na'am. Qala: Falzamha fa-innal-Jannata tahta rijlayha.",
        englishText = "The Prophet (ﷺ) asked a companion seeking to join an expedition: 'Do you have a mother?' He replied: 'Yes.' The Prophet (ﷺ) said: 'Then stay with her and serve her, for Paradise is beneath her feet.'",
        urduText = "نبی کریم ﷺ نے پوچھا: کیا تمہاری والدہ زندہ ہیں؟ اس نے عرض کیا: جی ہاں۔ آپ ﷺ نے فرمایا: تو ان کی خدمت کو لازم پکڑو کیونکہ جنت ان کے قدموں کے نیچے ہے۔",
        narrator = "Mu'awiyah ibn Jahimah as-Sulami (RA)",
        grade = "Hasan Sahih",
        gradingScholar = "Imam an-Nasa'i",
        reference = "Sunan an-Nasa'i 3104",
        linkedQuranVerses = "17:23, 17:24, 31:14"
      ),

      // ==================== 6. SUNAN IBN MAJAH ====================
      HadithEntity(
        collectionKey = "ibnmajah",
        collectionName = "Sunan Ibn Majah",
        bookNumber = 1,
        bookName = "Book 1: The Sunnah & Seeking Knowledge (المقدمة)",
        chapterNumber = 17,
        chapterName = "Seeking Knowledge is an Obligation Upon Every Muslim",
        hadithNumber = 224,
        inBookReference = "Book 1, Hadith 224 • Fu'ad Abd al-Baqi #224",
        arabicText = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
        transliteration = "Talabul-'ilmi faridatun 'ala kulli Muslim.",
        englishText = "Seeking sacred knowledge is an obligation upon every Muslim.",
        urduText = "علمِ دین حاصل کرنا ہر مسلمان پر فرض ہے۔",
        narrator = "Anas ibn Malik (RA)",
        grade = "Sahih",
        gradingScholar = "Imam as-Suyuti & Shaykh al-Albani",
        reference = "Sunan Ibn Majah 224",
        linkedQuranVerses = "20:114, 96:1"
      ),
      HadithEntity(
        collectionKey = "ibnmajah",
        collectionName = "Sunan Ibn Majah",
        bookNumber = 13,
        bookName = "Book 13: Rulings & Judgments (كتاب الأحكام)",
        chapterNumber = 17,
        chapterName = "There Should Be Neither Harm Nor Reciprocating Harm",
        hadithNumber = 2340,
        inBookReference = "Book 13, Hadith 33 • Fu'ad Abd al-Baqi #2340",
        arabicText = "لاَ ضَرَرَ وَلاَ ضِرَارَ",
        transliteration = "La darara wa la dirar.",
        englishText = "There should be neither inflicting of harm nor reciprocating of harm.",
        urduText = "نہ کسی کو ابتدا میں نقصان پہنچانا جائز ہے اور نہ بدلے میں ناجائز نقصان پہنچانا۔",
        narrator = "'Ubadah ibn as-Samit & Ibn 'Abbas (RA)",
        grade = "Hasan Sahih",
        gradingScholar = "Imam an-Nawawi & Ibn Majah",
        reference = "Sunan Ibn Majah 2340",
        linkedQuranVerses = "2:231, 2:282"
      ),
      HadithEntity(
        collectionKey = "ibnmajah",
        collectionName = "Sunan Ibn Majah",
        bookNumber = 37,
        bookName = "Book 37: Asceticism / Zuhd (كتاب الزهد)",
        chapterNumber = 30,
        chapterName = "Every Son of Adam Makes Mistakes, and the Best Repent",
        hadithNumber = 4251,
        inBookReference = "Book 37, Hadith 152 • Fu'ad Abd al-Baqi #4251",
        arabicText = "كُلُّ ابْنِ آدَمَ خَطَّاءٌ وَخَيْرُ الْخَطَّائِينَ التَّوَّابُونَ",
        transliteration = "Kullu Bni Adama khatta'un, wa khayrul-khatta'inat-tawwabun.",
        englishText = "Every son of Adam errs frequently, and the best of those who err are those who constantly turn in repentance (At-Tawwabun).",
        urduText = "آدم کی ہر اولاد خطا کار ہے، اور بہترین خطا کار وہ ہیں جو کثرت سے توبہ کرنے والے ہیں۔",
        narrator = "Anas ibn Malik (RA)",
        grade = "Hasan",
        gradingScholar = "Imam Ibn Majah & Imam at-Tirmidhi",
        reference = "Sunan Ibn Majah 4251",
        linkedQuranVerses = "2:222, 39:53, 66:8"
      ),

      // ==================== 7. MUWATTA IMAM MALIK ====================
      HadithEntity(
        collectionKey = "malik",
        collectionName = "Muwatta Imam Malik",
        bookNumber = 15,
        bookName = "Book 15: The Qur'an (كتاب القرآن)",
        chapterNumber = 8,
        chapterName = "Surah Al-Ikhlas Equals One-Third of the Qur'an",
        hadithNumber = 486,
        inBookReference = "Book 15, Hadith 19 • Muwatta Malik #486",
        arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ تَعْدِلُ ثُلُثَ الْقُرْآنِ",
        transliteration = "Qul Huwallahu Ahad ta'dilu thuluthal-Qur'an.",
        englishText = "Reciting 'Qul Huwallahu Ahad' (Surah Al-Ikhlas) is equivalent in reward to one-third of the Qur'an.",
        urduText = "قل ھو اللہ احد (سورۃ الاخلاص) ثواب میں تہائی قرآن کے برابر ہے۔",
        narrator = "Abu Sa'id al-Khudri & Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Malik ibn Anas",
        reference = "Muwatta Imam Malik 486",
        linkedQuranVerses = "112:1, 112:2, 112:3, 112:4"
      ),
      HadithEntity(
        collectionKey = "malik",
        collectionName = "Muwatta Imam Malik",
        bookNumber = 46,
        bookName = "Book 46: Decree / Al-Qadar (كتاب القدر)",
        chapterNumber = 1,
        chapterName = "Holding Fast to the Book of Allah and the Sunnah",
        hadithNumber = 1594,
        inBookReference = "Book 46, Hadith 3 • Muwatta Malik #1594",
        arabicText = "تَرَكْتُ فِيكُمْ أَمْرَيْنِ لَنْ تَضِلُّوا مَا تَمَسَّكْتُمْ بِهِمَا كِتَابَ اللَّهِ وَسُنَّةَ نَبِيِّهِ",
        transliteration = "Taraktu fikum amrayni lan tadillu ma tamassaktum bihima: Kitaballahi wa Sunnata Nabiyyihi.",
        englishText = "I have left among you two matters; you will never go astray so long as you hold fast to them: the Book of Allah and the Sunnah of His Prophet (ﷺ).",
        urduText = "میں تمہارے درمیان دو چیزیں چھوڑے جا رہا ہوں، جب تک تم انہیں مضبوطی سے تھامے رہو گے ہرگز گمراہ نہ ہو گے: اللہ کی کتاب اور اس کے نبی ﷺ کی سنت۔",
        narrator = "Malik ibn Anas (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Malik & Ibn Abd al-Barr",
        reference = "Muwatta Imam Malik 1594",
        linkedQuranVerses = "3:103, 4:59, 59:7"
      ),
      HadithEntity(
        collectionKey = "malik",
        collectionName = "Muwatta Imam Malik",
        bookNumber = 47,
        bookName = "Book 47: Good Character (كتاب حسن الخلق)",
        chapterNumber = 1,
        chapterName = "Sent to Perfect Noble Character",
        hadithNumber = 1609,
        inBookReference = "Book 47, Hadith 8 • Muwatta Malik #1609",
        arabicText = "إِنَّمَا بُعِثْتُ لأُتَمِّمَ حُسْنَ الأَخْلاَقِ",
        transliteration = "Innama bu'ithtu li-utammima husnal-akhlaq.",
        englishText = "Indeed, I was sent only to perfect noble character and moral excellence.",
        urduText = "بے شک مجھے اعلیٰ اخلاق کی تکمیل کے لیے ہی مبعوث کیا گیا ہے۔",
        narrator = "Imam Malik / Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Malik & Imam Ahmad",
        reference = "Muwatta Imam Malik 1609",
        linkedQuranVerses = "68:4, 33:21"
      ),

      // ==================== 8. 40 HADITH AN-NAWAWI ====================
      HadithEntity(
        collectionKey = "nawawi",
        collectionName = "40 Hadith an-Nawawi",
        bookNumber = 1,
        bookName = "The 40 Hadith Collection",
        chapterNumber = 1,
        chapterName = "Actions are by Intentions",
        hadithNumber = 1,
        inBookReference = "Nawawi #1 • Bukhari 1, Muslim 1907",
        arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى اللَّهِ وَرَسُولِهِ فَهِجْرَتُهُ إِلَى اللَّهِ وَرَسُولِهِ، وَمَنْ كَانَتْ هِجْرَتُهُ لِدُنْيَا يُصِيبُهَا أَوِ امْرَأَةٍ يَنْكِحُهَا فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ.",
        transliteration = "Innamal-a'malu bin-niyyat, wa innama likulli imri'in ma nawa...",
        englishText = "Actions are according to intentions, and everyone will get what was intended for him. Whoever migrated for Allah and His Messenger, his migration is for Allah and His Messenger.",
        urduText = "اعمال کا دارومدار نیتوں پر ہے اور ہر انسان کے لیے وہی ہے جس کی اس نے نیت کی۔",
        narrator = "'Umar ibn al-Khattab (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari & Imam Muslim",
        reference = "40 Hadith Nawawi 1",
        linkedQuranVerses = "98:5, 33:5"
      ),
      HadithEntity(
        collectionKey = "nawawi",
        collectionName = "40 Hadith an-Nawawi",
        bookNumber = 1,
        bookName = "The 40 Hadith Collection",
        chapterNumber = 2,
        chapterName = "Islam, Iman, and Ihsan",
        hadithNumber = 2,
        inBookReference = "Nawawi #2 • Sahih Muslim 8",
        arabicText = "الإِسْلامُ أَنْ تَشْهَدَ أَنْ لا إِلَهَ إِلا اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَتُقِيمَ الصَّلاةَ، وَتُؤْتِيَ الزَّكَاةَ، وَتَصُومَ رَمَضَانَ، وَتَحُجَّ الْبَيْتَ إِنِ اسْتَطَعْتَ إِلَيْهِ سَبِيلا... الإِحْسَانُ أَنْ تَعْبُدَ اللَّهَ كَأَنَّكَ تَرَاهُ",
        transliteration = "Al-Islamu an tashhada an la ilaha illallah... Al-Ihsanu an ta'budallaha ka'annaka tarah.",
        englishText = "Islam is that you testify that there is no deity worthy of worship except Allah and that Muhammad is the Messenger of Allah, establish prayer, pay zakah, fast Ramadan, and perform Hajj... Ihsan is that you worship Allah as if you see Him.",
        urduText = "اسلام یہ ہے کہ تم گواہی دو کہ اللہ کے سوا کوئی معبود نہیں اور محمد ﷺ اللہ کے رسول ہیں... احسان یہ ہے کہ تم اللہ کی عبادت اس طرح کرو گویا تم اسے دیکھ رہے ہو۔",
        narrator = "'Umar ibn al-Khattab (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Muslim",
        reference = "40 Hadith Nawawi 2",
        linkedQuranVerses = "2:177, 4:136"
      ),
      HadithEntity(
        collectionKey = "nawawi",
        collectionName = "40 Hadith an-Nawawi",
        bookNumber = 1,
        bookName = "The 40 Hadith Collection",
        chapterNumber = 13,
        chapterName = "Loving for Your Brother What You Love for Yourself",
        hadithNumber = 13,
        inBookReference = "Nawawi #13 • Bukhari 13, Muslim 45",
        arabicText = "لا يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ.",
        transliteration = "La yu'minu ahadukum hatta yuhibba li-akhihi ma yuhibbu li-nafsih.",
        englishText = "None of you truly believes until he loves for his brother what he loves for himself.",
        urduText = "تم میں سے کوئی شخص اس وقت تک کامل مومن نہیں ہو سکتا جب تک وہ اپنے بھائی کے لیے وہی پسند نہ کرے جو اپنے لیے پسند کرتا ہے۔",
        narrator = "Anas ibn Malik (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari & Imam Muslim",
        reference = "40 Hadith Nawawi 13",
        linkedQuranVerses = "49:10, 59:9"
      ),
      HadithEntity(
        collectionKey = "nawawi",
        collectionName = "40 Hadith an-Nawawi",
        bookNumber = 1,
        bookName = "The 40 Hadith Collection",
        chapterNumber = 15,
        chapterName = "Good Speech, Honoring Neighbors and Guests",
        hadithNumber = 15,
        inBookReference = "Nawawi #15 • Bukhari 6018, Muslim 47",
        arabicText = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيُكْرِمْ جَارَهُ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيُكْرِمْ ضَيْفَهُ.",
        transliteration = "Man kana yu'minu billahi wal-yawmil-akhiri fal-yaqul khayran aw li-yasmut...",
        englishText = "Whoever believes in Allah and the Last Day should speak good or remain silent; whoever believes in Allah and the Last Day should honor his neighbor; and whoever believes in Allah and the Last Day should honor his guest.",
        urduText = "جو شخص اللہ اور یومِ آخرت پر ایمان رکھتا ہو اسے چاہیے کہ اچھی بات کہے یا خاموش رہے، اپنے پڑوسی کی عزت کرے اور اپنے مہمان کا اکرام کرے۔",
        narrator = "Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari & Imam Muslim",
        reference = "40 Hadith Nawawi 15",
        linkedQuranVerses = "4:36, 17:53, 50:18"
      ),
      HadithEntity(
        collectionKey = "nawawi",
        collectionName = "40 Hadith an-Nawawi",
        bookNumber = 1,
        bookName = "The 40 Hadith Collection",
        chapterNumber = 16,
        chapterName = "Do Not Become Angry",
        hadithNumber = 16,
        inBookReference = "Nawawi #16 • Sahih al-Bukhari 6116",
        arabicText = "أَنَّ رَجُلاً قَالَ لِلنَّبِيِّ ﷺ: أَوْصِنِي. قَالَ: لا تَغْضَبْ. فَرَدَّدَ مِرَارًا، قَالَ: لا تَغْضَبْ.",
        transliteration = "Anna rajulan qala lin-Nabiyyi (ﷺ): Awsini. Qala: La taghdab. Faraddada miraran, qala: La taghdab.",
        englishText = "A man said to the Prophet (ﷺ): 'Advise me.' He said: 'Do not become angry.' The man repeated his request several times, and each time he replied: 'Do not become angry.'",
        urduText = "ایک شخص نے نبی اکرم ﷺ سے عرض کیا: مجھے نصیحت فرمائیے۔ آپ ﷺ نے فرمایا: غصہ نہ کرو۔ اس نے کئی بار پوچھا اور آپ ﷺ نے ہر بار فرمایا: غصہ نہ کرو۔",
        narrator = "Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari",
        reference = "40 Hadith Nawawi 16",
        linkedQuranVerses = "3:134, 42:37"
      ),

      // ==================== 9. RIYAD AS-SALIHIN ====================
      HadithEntity(
        collectionKey = "riyadussalihin",
        collectionName = "Riyad as-Salihin",
        bookNumber = 1,
        bookName = "Book 1: Sincerity & Repentance (كتاب المقدمات)",
        chapterNumber = 2,
        chapterName = "Chapter on Repentance (باب التوبة)",
        hadithNumber = 14,
        inBookReference = "Book 1, Hadith 14 • Riyad as-Salihin #14",
        arabicText = "وَاللَّهِ إِنِّي لأَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ فِي الْيَوْمِ أَكْثَرَ مِنْ سَبْعِينَ مَرَّةً",
        transliteration = "Wallahi inni la-astaghfirullaha wa atubu ilayhi fil-yawmi akthara min sab'ina marrah.",
        englishText = "By Allah, I seek the forgiveness of Allah and turn to Him in repentance more than seventy times every single day.",
        urduText = "اللہ کی قسم! میں دن میں ستر بار سے زیادہ اللہ سے استغفار کرتا ہوں اور اس کے حضور توبہ کرتا ہوں۔",
        narrator = "Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari (6307) & Imam an-Nawawi",
        reference = "Riyad as-Salihin 14",
        linkedQuranVerses = "24:31, 66:8, 110:3"
      ),
      HadithEntity(
        collectionKey = "riyadussalihin",
        collectionName = "Riyad as-Salihin",
        bookNumber = 1,
        bookName = "Book 1: Patience (باب الصبر)",
        chapterNumber = 3,
        chapterName = "Wondrous is the Affair of the Believer",
        hadithNumber = 27,
        inBookReference = "Book 1, Hadith 27 • Riyad as-Salihin #27",
        arabicText = "عَجَبًا لأَمْرِ الْمُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ، وَلَيْسَ ذَاكَ لأَحَدٍ إِلاَّ لِلْمُؤْمِنِ: إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ، وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ",
        transliteration = "'Ajaban li-amril-mu'min, inna amrahu kullahu khayr, wa laysa dhaka li-ahadin illa lil-mu'min: in asabathu sarra'u shakara fakana khayran lahu, wa in asabathu darra'u sabara fakana khayran lah.",
        englishText = "Wondrous is the affair of the believer, for there is good for him in every matter, and this is for no one except the believer: if prosperity comes to him, he gives thanks and that is good for him; and if adversity befalls him, he is patient and that is good for him.",
        urduText = "مومن کا معاملہ بھی عجیب ہے، اس کے ہر معاملے میں اس کے لیے بھلائی ہی بھلائی ہے اور یہ نعمت مومن کے سوا کسی کو حاصل نہیں: اگر اسے خوشحالی ملے تو شکر کرتا ہے اور یہ اس کے لیے بہتر ہے، اور اگر اسے تکلیف پہنچے تو صبر کرتا ہے اور یہ بھی اس کے لیے بہتر ہے۔",
        narrator = "Suhayb ar-Rumi (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Muslim (2999) & Imam an-Nawawi",
        reference = "Riyad as-Salihin 27",
        linkedQuranVerses = "2:153, 14:7, 39:10"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 9,
        bookName = "Book 9: Times of the Prayers (كتاب مواقيت الصلاة)",
        chapterNumber = 6,
        chapterName = "The Five Daily Prayers Expiate Sins Like a River",
        hadithNumber = 528,
        inBookReference = "Book 9, Hadith 6 • USC-MSA Vol. 1, Book 10, #506",
        arabicText = "أَرَأَيْتُمْ لَوْ أَنَّ نَهَرًا بِبَابِ أَحَدِكُمْ يَغْتَسِلُ فِيهِ كُلَّ يَوْمٍ خَمْسًا، مَا تَقُولُ ذَلِكَ يُبْقِي مِنْ دَرَنِهِ؟ قَالُوا لاَ يُبْقِي مِنْ دَرَنِهِ شَيْئًا. قَالَ فَذَلِكَ مِثْلُ الصَّلَوَاتِ الْخَمْسِ يَمْحُو اللَّهُ بِهِنَّ الْخَطَايَا",
        transliteration = "Ara'aytum law anna naharan bi-babi ahadikum yaghtasilu fihi kulla yawmin khamsan, ma taqulu dhalika yubqi min daranihi? Qalu: La yubqi min daranihi shay'an. Qala: Fadhalika mithlus-salawatil-khamsi yamhullahu bihinnal-khataya.",
        englishText = "If there was a river at the door of anyone of you and he took a bath in it five times a day, would you notice any dirt on him? They said, 'Not a trace of dirt would be left.' The Prophet (ﷺ) said: 'That is the example of the five daily prayers with which Allah blots out evil deeds.'",
        urduText = "بتاؤ اگر تم میں سے کسی کے دروازے پر نہر بہتی ہو اور وہ اس میں روزانہ پانچ مرتبہ غسل کرے تو کیا اس کے جسم پر کچھ میل باقی رہے گا؟ صحابہ نے عرض کیا: بالکل نہیں۔ آپ ﷺ نے فرمایا: یہی مثال پانچوں فرض نمازوں کی ہے جن کے ذریعے اللہ تعالیٰ گناہوں کو مٹا دیتا ہے۔",
        narrator = "Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 528",
        linkedQuranVerses = "11:114, 29:45"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 10,
        bookName = "Book 10: Call to Prayer / Adhan (كتاب الأذان)",
        chapterNumber = 8,
        chapterName = "Supplication After Hearing the Adhan",
        hadithNumber = 614,
        inBookReference = "Book 10, Hadith 12 • USC-MSA Vol. 1, Book 11, #588",
        arabicText = "مَنْ قَالَ حِينَ يَسْمَعُ النِّدَاءَ اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ وَالصَّلاَةِ الْقَائِمَةِ آتِ مُحَمَّدًا الْوَسِيلَةَ وَالْفَضِيلَةَ وَابْعَثْهُ مَقَامًا مَحْمُودًا الَّذِي وَعَدْتَهُ، حَلَّتْ لَهُ شَفَاعَتِي يَوْمَ الْقِيَامَةِ",
        transliteration = "Man qala hina yasma'un-nida'a: Allahumma Rabba hadhihid-da'watit-tammati was-salatil-qa'imati, ati Muhammadanil-wasilata wal-fadilata wab'ath-hu maqaman mahmudanil-ladhi wa'adtahu, hallat lahu shafa'ati yawmal-qiyamah.",
        englishText = "Whoever says after listening to the Adhan: 'O Allah, Lord of this complete call and established prayer, grant Muhammad Al-Wasilah and Al-Fadilah, and raise him to the praised station (Maqam Mahmud) which You have promised him,' my intercession will be assured for him on the Day of Resurrection.",
        urduText = "جو شخص اذان سن کر یہ دعا پڑھے: اے اللہ! اس کامل پکار اور قائم ہونے والی نماز کے رب، محمد ﷺ کو وسیلہ اور فضیلت عطا فرما اور انہیں اس مقامِ محمود پر فائز فرما جس کا تو نے ان سے وعدہ فرمایا ہے — تو قیامت کے دن اس کے لیے میری شفاعت واجب ہو جائے گی۔",
        narrator = "Jabir ibn 'Abdullah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari",
        reference = "Sahih al-Bukhari 614",
        linkedQuranVerses = "17:79, 5:35"
      ),
      HadithEntity(
        collectionKey = "bukhari",
        collectionName = "Sahih al-Bukhari",
        bookNumber = 19,
        bookName = "Book 19: Prayer at Night / Tahajjud (كتاب التهجد)",
        chapterNumber = 14,
        chapterName = "Divine Descent in the Final Third of the Night",
        hadithNumber = 1145,
        inBookReference = "Book 19, Hadith 26 • USC-MSA Vol. 2, Book 21, #246",
        arabicText = "يَنْزِلُ رَبُّنَا تَبَارَكَ وَتَعَالَى كُلَّ لَيْلَةٍ إِلَى السَّمَاءِ الدُّنْيَا حِينَ يَبْقَى ثُلُثُ اللَّيْلِ الآخِرُ يَقُولُ مَنْ يَدْعُونِي فَأَسْتَجِيبَ لَهُ مَنْ يَسْأَلُنِي فَأُعْطِيَهُ مَنْ يَسْتَغْفِرُنِي فَأَغْفِرَ لَهُ",
        transliteration = "Yanzilu Rabbuna Tabaraka wa Ta'ala kulla laylatin ilas-sama'id-dunya hina yabqa thuluthul-laylil-akhiru yaqulu: Man yad'uni fa-astajiba lahu, man yas'aluni fa-u'tiyahu, man yastaghfiruni fa-aghfira lah.",
        englishText = "Our Lord, the Blessed and Exalted, descends every night to the lowest heaven when the final third of the night remains, saying: 'Who is calling upon Me that I may answer him? Who is asking of Me that I may give him? Who is seeking My forgiveness that I may forgive him?'",
        urduText = "ہمارا رب تبارک و تعالیٰ ہر رات جب رات کا آخری تہائی حصہ باقی رہ جاتا ہے تو آسمانِ دنیا کی طرف نزولِ رحمت فرماتا ہے اور فرماتا ہے: کون ہے جو مجھ سے دعا کرے تو میں اس کی دعا قبول کروں؟ کون ہے جو مجھ سے مانگے تو میں اسے عطا کروں؟ کون ہے جو مجھ سے بخشش طلب کرے تو میں اسے بخش دوں؟",
        narrator = "Abu Hurairah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam al-Bukhari & Imam Muslim (758)",
        reference = "Sahih al-Bukhari 1145",
        linkedQuranVerses = "17:79, 51:17, 51:18, 73:2"
      ),
      HadithEntity(
        collectionKey = "abudawud",
        collectionName = "Sunan Abu Dawud",
        bookNumber = 2,
        bookName = "Book 2: Prayer / Kitab as-Salah (كتاب الصلاة)",
        chapterNumber = 28,
        chapterName = "As-Salatu Khayrun Minan-Nawm in the Fajr Adhan",
        hadithNumber = 500,
        inBookReference = "Book 2, Hadith 110 • Darussalam Intl #500",
        arabicText = "فَإِنْ كَانَ صَلَاةُ الصُّبْحِ قُلْتَ الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ اللَّهُ أَكْبَرُ اللَّهُ أَكْبَرُ لَا إِلَهَ إِلَّا اللَّهُ",
        transliteration = "Fa-in kana salatus-Subhi qulta: As-salatu khayrun minan-nawm, As-salatu khayrun minan-nawm, Allahu Akbar, Allahu Akbar, La ilaha illallah.",
        englishText = "When it is the Fajr (morning) prayer, say in the Adhan: 'As-salatu khayrun minan-nawm, As-salatu khayrun minan-nawm (Prayer is better than sleep, Prayer is better than sleep), Allahu Akbar, Allahu Akbar, La ilaha illallah.'",
        urduText = "اور جب فجر کی نماز کی اذان ہو تو کہو: الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ، الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ (نماز نیند سے بہتر ہے، نماز نیند سے بہتر ہے)، اللہ اکبر اللہ اکبر، لا الہ الا اللہ۔",
        narrator = "Abu Mahdhurah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam Abu Dawud & Shaykh al-Albani",
        reference = "Sunan Abu Dawud 500",
        linkedQuranVerses = "17:78, 20:14"
      ),
      HadithEntity(
        collectionKey = "nasai",
        collectionName = "Sunan an-Nasa'i",
        bookNumber = 7,
        bookName = "Book 7: The Adhan / Call to Prayer (كتاب الأذان)",
        chapterNumber = 15,
        chapterName = "Tathwib (As-Salatu Khayrun Minan-Nawm) in the Fajr Adhan",
        hadithNumber = 647,
        inBookReference = "Book 7, Hadith 22 • Darussalam Intl #647",
        arabicText = "وَفِي الْفَجْرِ الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ",
        transliteration = "Wa fil-Fajri: As-salatu khayrun minan-nawm.",
        englishText = "The Prophet (ﷺ) taught Abu Mahdhurah the Adhan and said: 'And in the Fajr call to prayer, say: As-salatu khayrun minan-nawm (Prayer is better than sleep).'",
        urduText = "نبی کریم ﷺ نے حضرت ابو محذورہ رضی اللہ عنہ کو اذان سکھائی اور فرمایا: اور فجر کی اذان میں کہو: الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ (نماز نیند سے بہتر ہے)۔",
        narrator = "Abu Mahdhurah (RA)",
        grade = "Sahih",
        gradingScholar = "Imam an-Nasa'i",
        reference = "Sunan an-Nasa'i 647",
        linkedQuranVerses = "17:78, 62:9"
      )
    ) + CompleteHadithCollectionsCatalog.getAdditionalFoundationalHadiths()
    return rawList.map { h ->
      val isShia = isShiaCollection(h.collectionKey)
      val normalizedGrade = h.grade.ifBlank { "Not graded in this source" }
      if (isShia) {
        val shiaRef = if (h.darussalamReference.isBlank() || h.darussalamReference.contains("Darussalam", ignoreCase = true)) {
          "Shia Canonical: #${h.hadithNumber}"
        } else {
          h.darussalamReference
        }
        h.copy(
          darussalamReference = shiaRef,
          grade = normalizedGrade,
          arabicNumber = if (h.arabicNumber > 0) h.arabicNumber else h.hadithNumber
        )
      } else {
        val sunniDarussalam = h.darussalamReference.ifBlank { "Darussalam: #${h.hadithNumber}" }
        val secondaryNum = if (h.arabicNumber > 0) h.arabicNumber else h.hadithNumber
        val sunniInBook = if (h.inBookReference.contains("Sahih Intl", ignoreCase = true)) {
          h.inBookReference
        } else {
          "${h.inBookReference} • Sahih Intl Ref: #$secondaryNum"
        }
        h.copy(
          darussalamReference = sunniDarussalam,
          inBookReference = sunniInBook,
          grade = normalizedGrade,
          arabicNumber = secondaryNum
        )
      }
    }
  }

  fun isShiaCollection(collectionKey: String): Boolean {
    return shiaCollections.any { it.key.equals(collectionKey, ignoreCase = true) }
  }

  /**
   * Matches a Hadith by exact number across supported numbering conventions
   * (Darussalam / Primary number, Sahih International / Arabic edition number, or exact In-Book Hadith number).
   * Never performs random substring matching (e.g. searching 5 will NOT match 5027 or 1145).
   */
  fun matchesExactHadithNumber(hadith: HadithEntity, targetNumber: Int): Boolean {
    if (targetNumber <= 0) return false
    if (hadith.hadithNumber == targetNumber || hadith.arabicNumber == targetNumber) return true
    val exactTokenRegex = Regex("""(?:hadith|#|ref:|darussalam:)\s*0*${targetNumber}(?!\d)""", RegexOption.IGNORE_CASE)
    return exactTokenRegex.containsMatchIn(hadith.inBookReference) ||
      exactTokenRegex.containsMatchIn(hadith.darussalamReference)
  }

  data class HadithLibraryIntegrityReport(
    val isValid: Boolean,
    val totalSunniHadiths: Int,
    val totalShiaHadiths: Int,
    val duplicateIds: List<String>,
    val invalidNumberIds: List<String>,
    val missingContentIds: List<String>,
    val gradingCrossContaminationIds: List<String>
  )

  fun validateHadithLibraryIntegrity(
    hadiths: List<HadithEntity> = getExtendedHadiths()
  ): HadithLibraryIntegrityReport {
    val seenKeys = HashSet<String>()
    val duplicateIds = mutableListOf<String>()
    val invalidNumberIds = mutableListOf<String>()
    val missingContentIds = mutableListOf<String>()
    val gradingCrossContaminationIds = mutableListOf<String>()
    var sunniCount = 0
    var shiaCount = 0

    for (h in hadiths) {
      val id = "${h.collectionKey}:${h.hadithNumber}"
      if (!seenKeys.add(id)) {
        duplicateIds.add(id)
      }
      if (h.hadithNumber <= 0 || h.bookNumber <= 0) {
        invalidNumberIds.add(id)
      }
      if (h.arabicText.isBlank() || h.englishText.isBlank() || h.reference.isBlank() || h.grade.isBlank()) {
        missingContentIds.add(id)
      }
      if (isShiaCollection(h.collectionKey)) {
        shiaCount++
        if (h.darussalamReference.contains("Darussalam", ignoreCase = true) ||
          h.grade.contains("Darussalam", ignoreCase = true) ||
          h.gradingScholar.contains("Darussalam", ignoreCase = true)
        ) {
          gradingCrossContaminationIds.add(id)
        }
      } else {
        sunniCount++
      }
    }

    val isValid = duplicateIds.isEmpty() &&
      invalidNumberIds.isEmpty() &&
      missingContentIds.isEmpty() &&
      gradingCrossContaminationIds.isEmpty() &&
      sunniCount > 0 &&
      shiaCount > 0

    return HadithLibraryIntegrityReport(
      isValid = isValid,
      totalSunniHadiths = sunniCount,
      totalShiaHadiths = shiaCount,
      duplicateIds = duplicateIds,
      invalidNumberIds = invalidNumberIds,
      missingContentIds = missingContentIds,
      gradingCrossContaminationIds = gradingCrossContaminationIds
    )
  }
}
