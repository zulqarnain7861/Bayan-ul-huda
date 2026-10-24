package com.example.data.model

enum class DuaCategory(val label: String, val arabicLabel: String) {
  MORNING_EVENING("Morning & Evening", "أذكار الصباح والمساء"),
  SLEEP_WAKE("Sleep, Wake & Dress", "أذكار النوم والاستيقاظ"),
  HOME_MASJID("Home, Masjid & Daily", "أذكار المنزل والمسجد"),
  QURANIC_RABBANA("Quranic & Prophetic", "الأدعية القرآنية والنبوية"),
  SPECIAL_OCCASIONS("Special Occasions", "أدعية المناسبات الخاصة")
}

enum class SpecialOccasionType(val label: String, val badgeText: String) {
  ALL("All Occasions", "All Special Occasions"),
  JUMUAH_FRIDAY("Jumu'ah (Friday)", "Blessed Friday & Salawat"),
  RAMADAN_IFTAR("Ramadan & Iftar", "Fasting & Breaking Fast"),
  LAYLATUL_QADR("Laylatul Qadr", "Night of Decree"),
  EID_AND_HILAL("Eid & New Moon", "Hilal & Eid Celebration"),
  SAFAR_TRAVEL("Safar (Travel)", "Journey & Riding"),
  RAIN_WEATHER("Rain & Weather", "Beneficial Rain & Wind"),
  ILLNESS_SHIFA("Illness & Shifa", "Healing & Visiting the Sick"),
  DISTRESS_ANXIETY("Distress & Relief", "Anxiety, Sorrow & Debt"),
  ISTIKHARAH("Istikharah", "Seeking Allah's Guidance"),
  NIKAH_MARRIAGE("Marriage & Family", "Nikah & Newlyweds"),
  GATHERING_MAJLIS("Gathering (Majlis)", "Closing a Gathering")
}

data class DailyDuaItem(
  val id: String,
  val title: String,
  val category: DuaCategory,
  val occasionType: SpecialOccasionType = SpecialOccasionType.ALL,
  val occasionBadge: String,
  val arabicText: String,
  val transliteration: String,
  val englishTranslation: String,
  val urduTranslation: String,
  val authenticReference: String,
  val virtueAndTiming: String,
  val recommendedCount: Int = 1,
  val quranSurahAyah: Pair<Int, Int>? = null
)

data class FridaySunnahChecklistItem(
  val id: String,
  val title: String,
  val arabicTitle: String,
  val description: String,
  val authenticReference: String,
  val actionSurahNumber: Int? = null,
  val actionDuaId: String? = null
)

enum class ReminderFrequency(val label: String) {
  WEEKLY_FRIDAY("Every Friday (Jumu'ah)"),
  DAILY("Every Day"),
  SUNNAH_FASTING_DAYS("Sunday & Wednesday Evenings (For Mon/Thu Fast)"),
  MONTHLY_HIJRI("13th, 14th & 15th Hijri (Ayyam al-Beed)")
}

data class NecessaryReminderSpec(
  val id: String,
  val title: String,
  val arabicSnippet: String,
  val subtitle: String,
  val authenticReference: String,
  val frequency: ReminderFrequency,
  val defaultTime24h: String,
  val timeOptions24h: List<String>,
  val defaultEnabled: Boolean = true,
  val linkedSurahNumber: Int? = null,
  val linkedDuaCategory: DuaCategory? = null
)

data class WeekdaySunnahSpec(
  val calendarDayOfWeek: Int,
  val dayNameEnglish: String,
  val shortDayLabel: String,
  val islamicDayTitle: String,
  val arabicDayName: String,
  val primaryReminderTitle: String,
  val arabicHadithSnippet: String,
  val englishGuidance: String,
  val urduGuidance: String,
  val authenticReference: String,
  val keySunnahBullets: List<String>,
  val isFridayJumuah: Boolean = false,
  val isSunnahFastingDay: Boolean = false,
  val linkedSurahNumber: Int? = null,
  val linkedReminderId: String = "rem_calculated_daily_day_sunnah"
)

object DailyDuasDataProvider {

  val allDuas: List<DailyDuaItem> = listOf(
    // =========================================================================
    // 1. MORNING & EVENING MASNOON DUAS
    // =========================================================================
    DailyDuaItem(
      id = "dua_ayat_al_kursi",
      title = "Ayat al-Kursi (Verse of the Throne)",
      category = DuaCategory.MORNING_EVENING,
      occasionBadge = "Morning, Evening, After Salah & Before Sleep",
      arabicText = "ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ٱلْحَىُّ ٱلْقَيُّومُ ۚ لَا تَأْخُذُهُۥ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُۥ مَا فِى ٱلسَّمَٰوَٰتِ وَمَا فِى ٱلْأَرْضِ ۗ مَن ذَا ٱلَّذِى يَشْفَعُ عِندَهُۥٓ إِلَّا بِإِذْنِهِۦ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَىْءٍ مِّنْ عِلْمِهِۦٓ إِلَّا بِمَا شَآءَ ۚ وَسِعَ كُرْسِيُّهُ ٱلسَّمَٰوَٰتِ وَٱلْأَرْضَ ۖ وَلَا يَـُٔودُهُۥ حِفْظُهُمَا ۚ وَهُوَ ٱلْعَلِىُّ ٱلْعَظِيمُ",
      transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyum, la ta'khudhuhu sinatun wa la nawm, lahu ma fis-samawati wa ma fil-ard, man dhalladhi yashfa'u 'indahu illa bi-idhnih, ya'lamu ma bayna aydihim wa ma khalfahum, wa la yuhituna bishay'im-min 'ilmihi illa bima sha'a, wasi'a kursiyyuhus-samawati wal-ard, wa la ya'uduhu hifzuhuma, wa Huwal-'Aliyyul-'Azim.",
      englishTranslation = "Allah — there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
      urduTranslation = "اللہ کے سوا کوئی معبود نہیں، وہ زندہ ہے، سب کا تھامنے والا ہے، نہ اسے اونگھ آتی ہے اور نہ نیند، اسی کا ہے جو کچھ آسمانوں میں ہے اور جو کچھ زمین میں ہے، کون ہے جو اس کی اجازت کے بغیر اس کے حضور شفاعت کر سکے؟ وہ جانتا ہے جو ان کے آگے ہے اور جو ان کے پیچھے ہے، اور وہ اس کے علم میں سے کسی چیز کا احاطہ نہیں کر سکتے مگر جتنا وہ چاہے، اس کی کرسی آسمانوں اور زمین کو گھیرے ہوئے ہے، اور ان دونوں کی حفاظت اسے تھکاتی نہیں، اور وہی بلند مرتبہ، عظمت والا ہے۔",
      authenticReference = "Surah Al-Baqarah 2:255 • Sahih al-Bukhari 2311 • An-Nasa'i (Al-Kubra 9848)",
      virtueAndTiming = "Recite after every obligatory prayer, in the morning, in the evening, and before sleeping for continuous divine protection.",
      recommendedCount = 1,
      quranSurahAyah = 2 to 255
    ),
    DailyDuaItem(
      id = "dua_sayyidul_istighfar",
      title = "Sayyidul Istighfar (The Chief Supplication for Forgiveness)",
      category = DuaCategory.MORNING_EVENING,
      occasionBadge = "Morning & Evening Master Istighfar",
      arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي، فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
      transliteration = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'udhu bika min sharri ma sana'tu, abu'u laka bini'matika 'alayya, wa abu'u laka bidhanbi faghfir li, fa innahu la yaghfirudh-dhunuba illa Anta.",
      englishTranslation = "O Allah, You are my Lord; there is no deity worthy of worship except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me, and I acknowledge my sin, so forgive me, for verily none forgives sins except You.",
      urduTranslation = "اے اللہ! تو ہی میرا رب ہے، تیرے سوا کوئی معبود نہیں، تو نے مجھے پیدا کیا اور میں تیرا بندہ ہوں، اور میں اپنی طاقت کے مطابق تیرے عہد اور وعدے پر قائم ہوں، میں نے جو کچھ کیا اس کے شر سے تیری پناہ مانگتا ہوں، میں اپنے اوپر تیری نعمت کا اقرار کرتا ہوں اور اپنے گناہ کا اعتراف کرتا ہوں، پس مجھے بخش دے کیونکہ تیرے سوا کوئی گناہوں کو نہیں بخش سکتا۔",
      authenticReference = "Sahih al-Bukhari 6306 (Narrated by Shaddad ibn Aws, may Allah be pleased with him)",
      virtueAndTiming = "Whoever recites this with conviction in the morning and passes away before evening enters Paradise; whoever recites it with conviction in the evening and passes away before morning enters Paradise.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_morning_remembrance",
      title = "Entering the Morning in Allah's Sovereignty",
      category = DuaCategory.MORNING_EVENING,
      occasionBadge = "Daily Morning Adhkar",
      arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
      transliteration = "Asbahna wa asbahal-mulku lillahi, walhamdu lillahi, la ilaha illallahu wahdahu la sharika lahu, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadir.",
      englishTranslation = "We have reached the morning and at this very time all sovereignty belongs to Allah, and all praise is for Allah. None has the right to be worshipped except Allah, alone, without partner; to Him belongs all sovereignty and praise, and He is over all things Omnipotent.",
      urduTranslation = "ہم نے صبح کی اور اللہ کے ملک نے صبح کی، اور سب تعریف اللہ ہی کے لیے ہے، اللہ کے سوا کوئی معبود نہیں، وہ اکیلا ہے اس کا کوئی شریک نہیں، اسی کی بادشاہی ہے اور اسی کے لیے تعریف ہے اور وہ ہر چیز پر قادر ہے۔",
      authenticReference = "Sahih Muslim 2723 (Narrated by Abdullah ibn Mas'ud, may Allah be pleased with him)",
      virtueAndTiming = "Recite every morning after Fajr to begin the day affirming Tawhid and gratitude.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_protection_harm",
      title = "Protection from Sudden Harm & Affliction",
      category = DuaCategory.MORNING_EVENING,
      occasionBadge = "Morning & Evening (3 Times)",
      arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
      transliteration = "Bismillahilladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim.",
      englishTranslation = "In the Name of Allah, with Whose Name nothing on earth or in the heavens can cause harm, and He is the All-Hearing, the All-Knowing.",
      urduTranslation = "اللہ کے نام کے ساتھ جس کے نام کی برکت سے زمین اور آسمان میں کوئی چیز نقصان نہیں پہنچا سکتی، اور وہ خوب سننے والا، خوب جاننے والا ہے۔",
      authenticReference = "Sunan Abi Dawud 5088 • Jami' at-Tirmidhi 3388 (Sahih)",
      virtueAndTiming = "Recite 3 times every morning and 3 times every evening; nothing will harm the reciter.",
      recommendedCount = 3
    ),
    DailyDuaItem(
      id = "dua_hasbiyallah",
      title = "Sufficiency & Reliance Upon Allah (HasbiyAllah)",
      category = DuaCategory.MORNING_EVENING,
      occasionBadge = "Morning & Evening (7 Times)",
      arabicText = "حَسْبِىَ ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ۖ عَلَيْهِ تَوَكَّلْتُ ۖ وَهُوَ رَبُّ ٱلْعَرْشِ ٱلْعَظِيمِ",
      transliteration = "HasbiyAllahu la ilaha illa Huwa, 'alayhi tawakkaltu wa Huwa Rabbul-'Arshil-'Azim.",
      englishTranslation = "Sufficient for me is Allah; there is no deity except Him. On Him I have relied, and He is the Lord of the Great Throne.",
      urduTranslation = "میرے لیے اللہ ہی کافی ہے، اس کے سوا کوئی معبود نہیں، میں نے اسی پر بھروسہ کیا اور وہ عرشِ عظیم کا رب ہے۔",
      authenticReference = "Surah At-Tawbah 9:129 • Sunan Abi Dawud 5081",
      virtueAndTiming = "Recite 7 times in the morning and evening; Allah suffices the servant regarding all worldly and hereafter concerns.",
      recommendedCount = 7,
      quranSurahAyah = 9 to 129
    ),

    // =========================================================================
    // 2. SLEEP, WAKE & DRESS MASNOON DUAS
    // =========================================================================
    DailyDuaItem(
      id = "dua_waking_up",
      title = "Dua Upon Waking Up from Sleep",
      category = DuaCategory.SLEEP_WAKE,
      occasionBadge = "Upon Waking Up",
      arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
      transliteration = "Alhamdu lillahilladhi ahyana ba'da ma amatana wa ilayhin-nushur.",
      englishTranslation = "All praise is for Allah Who gave us life after having taken it from us, and unto Him is the resurrection.",
      urduTranslation = "سب تعریفیں اللہ کے لیے ہیں جس نے ہمیں مارنے (سلانے) کے بعد زندہ (بیدار) کیا اور اسی کی طرف اٹھ کر جانا ہے۔",
      authenticReference = "Sahih al-Bukhari 6312 • Sahih Muslim 2711",
      virtueAndTiming = "Recite immediately upon opening your eyes from sleep.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_before_sleeping",
      title = "Dua Before Going to Sleep",
      category = DuaCategory.SLEEP_WAKE,
      occasionBadge = "Before Sleeping",
      arabicText = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
      transliteration = "Bismika Allahumma amutu wa ahya.",
      englishTranslation = "In Your Name, O Allah, I die and I live.",
      urduTranslation = "اے اللہ! تیرے ہی نام کے ساتھ میں مرتا (سوتا) ہوں اور جیتا (جاگتا) ہوں۔",
      authenticReference = "Sahih al-Bukhari 6324 (Narrated by Hudhayfah, may Allah be pleased with him)",
      virtueAndTiming = "Perform Wudu before sleeping, lie on the right side, dust the bed, and recite this supplication.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_wearing_garment",
      title = "Dua When Wearing Clothes",
      category = DuaCategory.SLEEP_WAKE,
      occasionBadge = "Dressing / Wearing Garment",
      arabicText = "الْحَمْدُ لِلَّهِ الَّذِي كَسَانِي هَذَا الثَّوْبَ وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
      transliteration = "Alhamdu lillahilladhi kasani hadhath-thawba wa razaqanihi min ghayri hawlin minni wa la quwwah.",
      englishTranslation = "All praise is for Allah Who has clothed me with this garment and provided it for me, without any power or might from myself.",
      urduTranslation = "تمام تعریفیں اس اللہ کے لیے ہیں جس نے مجھے یہ لباس پہنایا اور میری کسی طاقت اور قوت کے بغیر مجھے یہ عطا فرمایا۔",
      authenticReference = "Sunan Abi Dawud 4023 • Jami' at-Tirmidhi 3458 (Hasan)",
      virtueAndTiming = "Whoever recites this when putting on a garment has their previous minor sins forgiven.",
      recommendedCount = 1
    ),

    // =========================================================================
    // 3. HOME, MASJID, WUDU & DINING MASNOON DUAS
    // =========================================================================
    DailyDuaItem(
      id = "dua_leaving_home",
      title = "Dua When Leaving the Home",
      category = DuaCategory.HOME_MASJID,
      occasionBadge = "Stepping Out of Home",
      arabicText = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
      transliteration = "Bismillahi, tawakkaltu 'alallahi, wa la hawla wa la quwwata illa billah.",
      englishTranslation = "In the Name of Allah, I place my trust in Allah; and there is no might nor power except with Allah.",
      urduTranslation = "اللہ کے نام کے ساتھ، میں نے اللہ پر بھروسہ کیا، اور گناہ سے بچنے کی ہمت اور نیکی کرنے کی طاقت اللہ ہی کی توفیق سے ہے۔",
      authenticReference = "Sunan Abi Dawud 5095 • Jami' at-Tirmidhi 3426 (Sahih)",
      virtueAndTiming = "It is said to the one who recites this upon leaving home: 'You are guided, sufficed, and protected,' and Satan steps aside.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_entering_home",
      title = "Dua When Entering the Home",
      category = DuaCategory.HOME_MASJID,
      occasionBadge = "Entering Home",
      arabicText = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى رَبِّنَا تَوَكَّلْنَا",
      transliteration = "Bismillahi walajna, wa bismillahi kharajna, wa 'ala Rabbina tawakkalna.",
      englishTranslation = "In the Name of Allah we enter, in the Name of Allah we leave, and upon our Lord we rely.",
      urduTranslation = "اللہ ہی کے نام کے ساتھ ہم داخل ہوئے، اور اللہ ہی کے نام کے ساتھ ہم نکلے، اور اپنے رب ہی پر ہم نے بھروسہ کیا۔",
      authenticReference = "Sunan Abi Dawud 5096 • Sahih Muslim 2018 (Mentioning Allah's Name upon entering)",
      virtueAndTiming = "Mention Allah's Name when entering the home and greet your family with 'As-Salamu Alaykum' so blessing descends upon the household.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_entering_masjid",
      title = "Dua When Entering the Masjid",
      category = DuaCategory.HOME_MASJID,
      occasionBadge = "Entering Mosque (Right Foot First)",
      arabicText = "بِسْمِ اللَّهِ وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
      transliteration = "Bismillahi was-salatu was-salamu 'ala Rasulillahi, Allahummaftah li abwaba rahmatik.",
      englishTranslation = "In the Name of Allah, and peace and blessings be upon the Messenger of Allah. O Allah, open for me the gates of Your mercy.",
      urduTranslation = "اللہ کے نام کے ساتھ، اور رسول اللہ ﷺ پر درود و سلام ہو۔ اے اللہ! میرے لیے اپنی رحمت کے دروازے کھول دے۔",
      authenticReference = "Sahih Muslim 713 • Sunan Abi Dawud 465",
      virtueAndTiming = "Enter the Masjid with the right foot first while reciting this supplication.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_leaving_masjid",
      title = "Dua When Leaving the Masjid",
      category = DuaCategory.HOME_MASJID,
      occasionBadge = "Leaving Mosque (Left Foot First)",
      arabicText = "بِسْمِ اللَّهِ وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
      transliteration = "Bismillahi was-salatu was-salamu 'ala Rasulillahi, Allahumma inni as'aluka min fadlik.",
      englishTranslation = "In the Name of Allah, and peace and blessings be upon the Messenger of Allah. O Allah, I ask You from Your bounty.",
      urduTranslation = "اللہ کے نام کے ساتھ، اور رسول اللہ ﷺ پر درود و سلام ہو۔ اے اللہ! میں تجھ سے تیرے فضل کا سوال کرتا ہوں۔",
      authenticReference = "Sahih Muslim 713 • Sunan Ibn Majah 773",
      virtueAndTiming = "Step out of the Masjid with the left foot first and ask Allah for His lawful bounty.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_after_wudu",
      title = "Dua After Completing Wudu (Ablution)",
      category = DuaCategory.HOME_MASJID,
      occasionBadge = "After Wudu",
      arabicText = "أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ، اللَّهُمَّ اجْعَلْنِي مِنَ التَّوَّابِينَ وَاجْعَلْنِي مِنَ الْمُتَطَهِّرِينَ",
      transliteration = "Ash-hadu an la ilaha illallahu wahdahu la sharika lahu, wa ash-hadu anna Muhammadan 'abduhu wa Rasuluhu. Allahummaj'alni minat-tawwabina waj'alni minal-mutatahhirin.",
      englishTranslation = "I bear witness that none has the right to be worshipped except Allah alone, Who has no partner; and I bear witness that Muhammad is His servant and His Messenger. O Allah, make me among those who turn to You in repentance and make me among those who purify themselves.",
      urduTranslation = "میں گواہی دیتا ہوں کہ اللہ کے سوا کوئی معبود نہیں، وہ اکیلا ہے اس کا کوئی شریک نہیں، اور میں گواہی دیتا ہوں کہ محمد ﷺ اس کے بندے اور رسول ہیں۔ اے اللہ! مجھے کثرت سے توبہ کرنے والوں میں شامل فرما اور مجھے پاک صاف رہنے والوں میں بنا دے۔",
      authenticReference = "Sahih Muslim 234 • Jami' at-Tirmidhi 55",
      virtueAndTiming = "All eight gates of Paradise are opened for the one who performs Wudu thoroughly and recites this testimony.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_after_eating",
      title = "Dua After Finishing a Meal",
      category = DuaCategory.HOME_MASJID,
      occasionBadge = "After Eating & Drinking",
      arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
      transliteration = "Alhamdu lillahilladhi at'amani hadha wa razaqanihi min ghayri hawlin minni wa la quwwah.",
      englishTranslation = "All praise is for Allah Who fed me this food and provided it for me without any might or power on my part.",
      urduTranslation = "تمام تعریفیں اس اللہ کے لیے ہیں جس نے مجھے یہ کھانا کھلایا اور میری کسی طاقت اور قوت کے بغیر مجھے یہ رزق عطا فرمایا۔",
      authenticReference = "Jami' at-Tirmidhi 3458 • Sunan Abi Dawud 4023 (Hasan)",
      virtueAndTiming = "Say 'Bismillah' before eating, eat with the right hand, and recite this praise after finishing.",
      recommendedCount = 1
    ),

    // =========================================================================
    // 4. QURANIC RABBANA & PROPHETIC DUAS
    // =========================================================================
    DailyDuaItem(
      id = "dua_rabbana_fid_dunya",
      title = "Rabbana Atina Fid-Dunya Hasanah (Good in Both Worlds)",
      category = DuaCategory.QURANIC_RABBANA,
      occasionBadge = "Most Frequent Dua of the Prophet ﷺ",
      arabicText = "رَبَّنَآ ءَاتِنَا فِى ٱلدُّنْيَا حَسَنَةً وَفِى ٱلْـَٔاخِرَةِ حَسَنَةً وَقِنَا عَذَابَ ٱلنَّارِ",
      transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
      englishTranslation = "Our Lord, grant us good in this world and good in the Hereafter, and protect us from the punishment of the Fire.",
      urduTranslation = "اے ہمارے رب! ہمیں دنیا میں بھی بھلائی عطا فرما اور آخرت میں بھی بھلائی عطا فرما، اور ہمیں آگ کے عذاب سے بچا۔",
      authenticReference = "Surah Al-Baqarah 2:201 • Sahih al-Bukhari 6389",
      virtueAndTiming = "Anas (RA) reported that this was the most frequent supplication of the Prophet ﷺ.",
      recommendedCount = 3,
      quranSurahAyah = 2 to 201
    ),
    DailyDuaItem(
      id = "dua_rabbana_la_tuzigh",
      title = "Rabbana La Tuzigh Qulubana (Steadfastness of Heart)",
      category = DuaCategory.QURANIC_RABBANA,
      occasionBadge = "Firmness Upon Guidance",
      arabicText = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ ٱلْوَهَّابُ",
      transliteration = "Rabbana la tuzigh qulubana ba'da idh hadaytana wa hab lana min ladunka rahmah, innaka Antal-Wahhab.",
      englishTranslation = "Our Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy. Indeed, You are the Bestower.",
      urduTranslation = "اے ہمارے رب! ہمارے دلوں کو ٹیڑھا نہ کر بعد اس کے کہ تو نے ہمیں ہدایت دی، اور ہمیں اپنے پاس سے رحمت عطا فرما، بے شک تو ہی بڑا عطا کرنے والا ہے۔",
      authenticReference = "Surah Ali 'Imran 3:8",
      virtueAndTiming = "The supplication of those firmly grounded in knowledge asking Allah for steadfastness upon truth.",
      recommendedCount = 3,
      quranSurahAyah = 3 to 8
    ),
    DailyDuaItem(
      id = "dua_rabbij_alni_muqimas_salah",
      title = "Rabbij'alni Muqimas-Salah (For Prayer & Parents)",
      category = DuaCategory.QURANIC_RABBANA,
      occasionBadge = "Steadfastness in Salah & Forgiveness for Parents",
      arabicText = "رَبِّ ٱجْعَلْنِى مُقِيمَ ٱلصَّلَوٰةِ وَمِن ذُرِّيَّتِى ۚ رَبَّنَا وَتَقَبَّلْ دُعَآءِ ۝ رَبَّنَا ٱغْفِرْ لِى وَلِوَٰلِدَىَّ وَلِلْمُؤْمِنِينَ يَوْمَ يَقُومُ ٱلْحِسَابُ",
      transliteration = "Rabbij'alni muqimas-salati wa min dhurriyyati, Rabbana wa taqabbal du'a. Rabbanaghfir li wa liwalidayya wa lil-mu'minina yawma yaqumul-hisab.",
      englishTranslation = "My Lord, make me an establisher of prayer, and from my descendants. Our Lord, and accept my supplication. Our Lord, forgive me and my parents and the believers the Day the account is established.",
      urduTranslation = "اے میرے رب! مجھے نماز قائم کرنے والا بنا اور میری اولاد میں سے بھی، اے ہمارے رب! اور میری دعا قبول فرما۔ اے ہمارے رب! مجھے، میرے والدین کو اور تمام مومنوں کو اس دن بخش دے جب حساب قائم ہوگا۔",
      authenticReference = "Surah Ibrahim 14:40-41",
      virtueAndTiming = "Prophet Ibrahim's (AS) comprehensive prayer for establishing Salah, righteous offspring, and forgiveness for parents.",
      recommendedCount = 1,
      quranSurahAyah = 14 to 40
    ),
    DailyDuaItem(
      id = "dua_rabbana_hab_lana",
      title = "Rabbana Hab Lana Min Azwajina (Righteous Family)",
      category = DuaCategory.QURANIC_RABBANA,
      occasionBadge = "Spouses, Children & Leadership in Taqwa",
      arabicText = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَٰجِنَا وَذُرِّيَّٰتِنَا قُرَّةَ أَعْيُنٍ وَٱجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
      transliteration = "Rabbana hab lana min azwajina wa dhurriyyatina qurrata a'yunin waj'alna lil-muttaqina imama.",
      englishTranslation = "Our Lord, grant us from among our spouses and offspring comfort to our eyes and make us an example for the righteous.",
      urduTranslation = "اے ہمارے رب! ہمیں ہماری بیویوں اور ہماری اولاد سے آنکھوں کی ٹھنڈک عطا فرما اور ہمیں پرہیزگاروں کا پیشوا بنا۔",
      authenticReference = "Surah Al-Furqan 25:74",
      virtueAndTiming = "The supplication of 'Ibad ar-Rahman (the Servants of the Most Merciful) for family harmony and piety.",
      recommendedCount = 1,
      quranSurahAyah = 25 to 74
    ),
    DailyDuaItem(
      id = "dua_rabbishrah_li_sadri",
      title = "Rabbishrah Li Sadri (Ease, Knowledge & Speech)",
      category = DuaCategory.QURANIC_RABBANA,
      occasionBadge = "Study, Confidence & Beneficial Knowledge",
      arabicText = "رَبِّ ٱشْرَحْ لِى صَدْرِى ۝ وَيَسِّرْ لِىٓ أَمْرِى ۝ وَٱحْلُلْ عُقْدَةً مِّن لِّسَانِى ۝ يَفْقَهُوا۟ قَوْلِى",
      transliteration = "Rabbishrah li sadri, wa yassir li amri, wahlul 'uqdatan min lisani, yafqahu qawli.",
      englishTranslation = "My Lord, expand for me my breast [with assurance], and ease for me my task, and untie the knot from my tongue, that they may understand my speech.",
      urduTranslation = "اے میرے رب! میرے لیے میرا سینہ کھول دے، اور میرے لیے میرا کام آسان فرما دے، اور میری زبان کی گرہ کھول دے، تاکہ لوگ میری بات سمجھ سکیں۔",
      authenticReference = "Surah Ta-Ha 20:25-28",
      virtueAndTiming = "Prophet Musa's (AS) supplication before speaking, studying, teaching, or undertaking an important task.",
      recommendedCount = 1,
      quranSurahAyah = 20 to 25
    ),

    // =========================================================================
    // 5. SPECIAL OCCASIONS DUAS
    // =========================================================================
    DailyDuaItem(
      id = "dua_jumuah_salawat_ibrahimiyyah",
      title = "Salawat Ibrahimiyyah (Abundant Friday Blessings Upon the Prophet ﷺ)",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.JUMUAH_FRIDAY,
      occasionBadge = "Jumu'ah (Friday) Special • Salawat",
      arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ، اللَّهُمَّ بَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا بَارَكْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ",
      transliteration = "Allahumma salli 'ala Muhammadin wa 'ala ali Muhammadin, kama sallayta 'ala Ibrahima wa 'ala ali Ibrahima, innaka Hamidun Majid. Allahumma barik 'ala Muhammadin wa 'ala ali Muhammadin, kama barakta 'ala Ibrahima wa 'ala ali Ibrahima, innaka Hamidun Majid.",
      englishTranslation = "O Allah, send peace and blessings upon Muhammad and upon the family of Muhammad, as You sent peace and blessings upon Ibrahim and upon the family of Ibrahim; indeed, You are Praiseworthy, Glorious. O Allah, bless Muhammad and the family of Muhammad, as You blessed Ibrahim and the family of Ibrahim; indeed, You are Praiseworthy, Glorious.",
      urduTranslation = "اے اللہ! محمد ﷺ پر اور آلِ محمد ﷺ پر رحمت نازل فرما جس طرح تو نے ابراہیم علیہ السلام اور آلِ ابراہیم پر رحمت نازل فرمائی، بے شک تو تعریف والا، بزرگی والا ہے۔ اے اللہ! محمد ﷺ پر اور آلِ محمد ﷺ پر برکت نازل فرما جس طرح تو نے ابراہیم علیہ السلام اور آلِ ابراہیم پر برکت نازل فرمائی، بے شک تو تعریف والا، بزرگی والا ہے۔",
      authenticReference = "Sahih al-Bukhari 3370 • Sunan Abi Dawud 1047 (Increasing Salawat on Friday)",
      virtueAndTiming = "The Prophet ﷺ said: 'Increase your supplications for blessings upon me on Friday and the night of Friday, for your Salawat is presented to me.'",
      recommendedCount = 10
    ),
    DailyDuaItem(
      id = "dua_ramadan_iftar",
      title = "Dua When Breaking the Fast (Iftar)",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.RAMADAN_IFTAR,
      occasionBadge = "Ramadan & Sunnah Fasting • Iftar",
      arabicText = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ، وَثَبَتَ الْأَجْرُ إِنْ شَاءَ اللَّهُ",
      transliteration = "Dhahabadh-dhama'u wabtallatil-'uruqu, wa thabatal-ajru in sha'Allah.",
      englishTranslation = "The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills.",
      urduTranslation = "پیاس چلی گئی، رگیں تر ہو گئیں، اور ان شاء اللہ اجر ثابت ہو گیا۔",
      authenticReference = "Sunan Abi Dawud 2357 (Narrated by Abdullah ibn Umar, may Allah be pleased with them — Hasan)",
      virtueAndTiming = "Break the fast with fresh or dry dates, or water, saying 'Bismillah', and then recite this authentic Prophetic supplication.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_laylatul_qadr",
      title = "Dua for Laylatul Qadr (The Night of Decree)",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.LAYLATUL_QADR,
      occasionBadge = "Laylatul Qadr • Last 10 Nights of Ramadan",
      arabicText = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
      transliteration = "Allahumma innaka 'Afuwwun tuhibbul-'afwa fa'fu 'anni.",
      englishTranslation = "O Allah, indeed You are Pardoning; You love pardon, so pardon me.",
      urduTranslation = "اے اللہ! بے شک تو بہت معاف کرنے والا ہے، معاف کرنے کو پسند فرماتا ہے، پس مجھے معاف فرما دے۔",
      authenticReference = "Jami' at-Tirmidhi 3513 • Sunan Ibn Majah 3850 (Narrated by Aishah, may Allah be pleased with her — Sahih)",
      virtueAndTiming = "Taught directly by the Prophet ﷺ to Aishah (RA) when she asked what to say if she knew which night was Laylatul Qadr.",
      recommendedCount = 7
    ),
    DailyDuaItem(
      id = "dua_new_moon_hilal",
      title = "Dua Upon Sighting the New Moon (Hilal of Ramadan, Eid & New Month)",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.EID_AND_HILAL,
      occasionBadge = "New Hijri Month & Eid Crescent",
      arabicText = "اللَّهُمَّ أَهِلَّهُ عَلَيْنَا بِالْيُمْنِ وَالْإِيمَانِ، وَالسَّلَامَةِ وَالْإِسْلَامِ، رَبِّي وَرَبُّكَ اللَّهُ",
      transliteration = "Allahumma ahillahu 'alayna bil-yumni wal-iman, was-salamati wal-Islam, Rabbi wa RabbukAllah.",
      englishTranslation = "O Allah, bring this crescent upon us with blessing, faith, safety, and Islam. My Lord and your Lord is Allah.",
      urduTranslation = "اے اللہ! اس چاند کو ہم پر برکت، ایمان، سلامتی اور اسلام کے ساتھ طلوع فرما۔ (اے چاند!) میرا اور تیرا رب اللہ ہے۔",
      authenticReference = "Jami' at-Tirmidhi 3451 (Narrated by Talhah ibn Ubaydullah, may Allah be pleased with him — Hasan Sahih)",
      virtueAndTiming = "Recited whenever sighting the new crescent moon at the start of Ramadan, Shawwal (Eid al-Fitr), Dhul-Hijjah, or any Hijri month.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_eid_greeting",
      title = "Dua & Greeting on the Day of Eid",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.EID_AND_HILAL,
      occasionBadge = "Eid al-Fitr & Eid al-Adha",
      arabicText = "تَقَبَّلَ اللَّهُ مِنَّا وَمِنْكُمْ",
      transliteration = "Taqabbalallahu minna wa minkum.",
      englishTranslation = "May Allah accept [good deeds] from us and from you.",
      urduTranslation = "اللہ تعالیٰ ہماری اور آپ کی (نیک عبادات) قبول فرمائے۔",
      authenticReference = "Al-Bayhaqi (As-Sunan al-Kubra 3/319) • Fath al-Bari (2/446 — Authenticated by Ibn Hajar)",
      virtueAndTiming = "The Companions of the Prophet ﷺ used to greet one another with these words when meeting on the day of Eid.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_safar_travel",
      title = "Dua for Travelling & Boarding a Vehicle (Dua as-Safar)",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.SAFAR_TRAVEL,
      occasionBadge = "Safar • Beginning a Journey",
      arabicText = "سُبْحَٰنَ ٱلَّذِى سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُۥ مُقْرِنِينَ ۝ وَإِنَّآ إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
      transliteration = "Subhanalladhi sakh-khara lana hadha wa ma kunna lahu muqrinin, wa inna ila Rabbina lamunqalibun.",
      englishTranslation = "Glory be to Him Who has subjected this to us, and we could never have it by our efforts. And verily, to our Lord we indeed are to return.",
      urduTranslation = "پاک ہے وہ ذات جس نے اس سواری کو ہمارے تابع کر دیا حالانکہ ہم اسے قابو میں لانے کی طاقت نہیں رکھتے تھے، اور بے شک ہم اپنے رب ہی کی طرف لوٹ کر جانے والے ہیں۔",
      authenticReference = "Surah Az-Zukhruf 43:13-14 • Sahih Muslim 1342",
      virtueAndTiming = "Say 'Bismillah', praise Allah, say 'Allahu Akbar' (3x), and recite this Quranic & Prophetic travel supplication when embarking on any journey.",
      recommendedCount = 1,
      quranSurahAyah = 43 to 13
    ),
    DailyDuaItem(
      id = "dua_rain_falling",
      title = "Dua When Rain Falls (Beneficial Rain)",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.RAIN_WEATHER,
      occasionBadge = "Rain & Weather",
      arabicText = "اللَّهُمَّ صَيِّبًا نَافِعًا",
      transliteration = "Allahumma sayyiban nafi'an.",
      englishTranslation = "O Allah, make it a beneficial raincloud.",
      urduTranslation = "اے اللہ! اسے نفع بخش اور فائدہ مند بارش بنا دے۔",
      authenticReference = "Sahih al-Bukhari 1032 (Narrated by Aishah, may Allah be pleased with her)",
      virtueAndTiming = "The time of rainfall is a blessed time when supplications are accepted by Allah.",
      recommendedCount = 3
    ),
    DailyDuaItem(
      id = "dua_illness_shifa",
      title = "Dua for Healing & Visiting the Sick (Ruqyah of the Prophet ﷺ)",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.ILLNESS_SHIFA,
      occasionBadge = "Illness, Pain & Shifa",
      arabicText = "اللَّهُمَّ رَبَّ النَّاسِ، أَذْهِبِ الْبَأْسَ، اشْفِ أَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا",
      transliteration = "Allahumma Rabban-nasi, adh-hibil-ba'sa, ishfi Antash-Shafi, la shifa'a illa shifa'uka, shifa'an la yughadiru saqama.",
      englishTranslation = "O Allah, Lord of mankind, remove the affliction; cure, for You are the Healer. There is no cure except Your cure — a cure that leaves no illness behind.",
      urduTranslation = "اے اللہ! انسانوں کے رب! تکلیف کو دور فرما دے، شفا عطا فرما کیونکہ تو ہی شفا دینے والا ہے، تیری شفا کے سوا کوئی شفا نہیں، ایسی شفا عطا فرما جو کسی بیماری کو باقی نہ چھوڑے۔",
      authenticReference = "Sahih al-Bukhari 5743 • Sahih Muslim 2191",
      virtueAndTiming = "The Prophet ﷺ used to place his right hand on the patient and recite this supplication for complete healing.",
      recommendedCount = 3
    ),
    DailyDuaItem(
      id = "dua_distress_yunus",
      title = "Dua of Prophet Yunus (AS) in Distress & Hardship",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.DISTRESS_ANXIETY,
      occasionBadge = "Relief from Distress & Anxiety",
      arabicText = "لَّآ إِلَٰهَ إِلَّآ أَنتَ سُبْحَٰنَكَ إِنِّى كُنتُ مِنَ ٱلظَّٰلِمِينَ",
      transliteration = "La ilaha illa Anta Subhanaka inni kuntu minaz-zalimin.",
      englishTranslation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
      urduTranslation = "تیرے سوا کوئی معبود نہیں، تو پاک ہے، بے شک میں ہی ظالموں (قصور واروں) میں سے تھا۔",
      authenticReference = "Surah Al-Anbiya 21:87 • Jami' at-Tirmidhi 3505 (Sahih)",
      virtueAndTiming = "The Prophet ﷺ said: 'No Muslim supplicates with this for anything, ever, except that Allah responds to him.'",
      recommendedCount = 7,
      quranSurahAyah = 21 to 87
    ),
    DailyDuaItem(
      id = "dua_anxiety_debt",
      title = "Dua for Relief from Worry, Sorrow & Debt",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.DISTRESS_ANXIETY,
      occasionBadge = "Anxiety, Grief & Financial Relief",
      arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
      transliteration = "Allahumma inni a'udhu bika minal-hammi wal-hazan, wal-'ajzi wal-kasal, wal-bukhli wal-jubn, wa dala'id-dayni wa ghalabatir-rijal.",
      englishTranslation = "O Allah, I seek refuge in You from worry and grief, from incapacity and laziness, from miserliness and cowardice, and from being heavily in debt and being overpowered by men.",
      urduTranslation = "اے اللہ! میں فقر و پریشانی اور غم سے، عاجزی اور سستی سے، بخل اور بزدلی سے، اور قرض کے بوجھ اور لوگوں کے غلبے سے تیری پناہ مانگتا ہوں۔",
      authenticReference = "Sahih al-Bukhari 6369 (Narrated by Anas ibn Malik, may Allah be pleased with him)",
      virtueAndTiming = "Anas (RA) served the Prophet ﷺ for years and heard him recite this comprehensive protection frequently.",
      recommendedCount = 3
    ),
    DailyDuaItem(
      id = "dua_istikharah",
      title = "Dua al-Istikharah (Seeking Allah's Guidance in Important Decisions)",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.ISTIKHARAH,
      occasionBadge = "Salatul Istikharah • Important Decisions",
      arabicText = "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ، وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ، وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ، فَإِنَّكَ تَقْدِرُ وَلَا أَقْدِرُ، وَتَعْلَمُ وَلَا أَعْلَمُ، وَأَنْتَ عَلَّامُ الْغُيُوبِ، اللَّهُمَّ إِنْ كُنْتَ تَعْلَمُ أَنَّ هَذَا الْأَمْرَ خَيْرٌ لِي فِي دِينِي وَمَعَاشِي وَعَاقِبَةِ أَمْرِي فَاقْدُرْهُ لِي وَيَسِّرْهُ لِي ثُمَّ بَارِكْ لِي فِيهِ، وَإِنْ كُنْتَ تَعْلَمُ أَنَّ هَذَا الْأَمْرَ شَرٌّ لِي فِي دِينِي وَمَعَاشِي وَعَاقِبَةِ أَمْرِي فَاصْرِفْهُ عَنِّي وَاصْرِفْنِي عَنْهُ وَاقْدُرْ لِيَ الْخَيْرَ حَيْثُ كَانَ ثُمَّ أَرْضِنِي بِهِ",
      transliteration = "Allahumma inni astakhiruka bi'ilmika, wa astaqdiruka biqudratika, wa as'aluka min fadlikal-'azim, fa innaka taqdiru wa la aqdiru, wa ta'lamu wa la a'lamu, wa Anta 'Allamul-ghuyub. Allahumma in kunta ta'lamu anna hadhal-amra khayrun li fi dini wa ma'ashi wa 'aqibati amri faqdurhu li wa yassirhu li thumma barik li fihi, wa in kunta ta'lamu anna hadhal-amra sharrun li fi dini wa ma'ashi wa 'aqibati amri fasrifhu 'anni wasrifni 'anhu waqdur liyal-khayra haythu kana thumma ardini bihi.",
      englishTranslation = "O Allah, I seek Your guidance by virtue of Your knowledge, and I seek ability by virtue of Your power, and I ask You of Your great bounty. You have power, and I have none. You know, and I know not. You are the Knower of hidden things. O Allah, if in Your knowledge this matter is good for my religion, my livelihood, and the outcome of my affairs, then ordain it for me, make it easy for me, and bless me in it. And if in Your knowledge this matter is bad for my religion, my livelihood, and the outcome of my affairs, then turn it away from me, turn me away from it, ordain for me the good wherever it may be, and make me pleased with it.",
      urduTranslation = "اے اللہ! میں تیرے علم کے ذریعے تجھ سے خیر طلب کرتا ہوں اور تیری قدرت کے ذریعے تجھ سے طاقت مانگتا ہوں اور تیرے فضلِ عظیم کا سوال کرتا ہوں، کیونکہ تو قدرت رکھتا ہے اور میں قدرت نہیں رکھتا، تو جانتا ہے اور میں نہیں جانتا، اور تو غیبوں کا خوب جاننے والا ہے۔ اے اللہ! اگر تیرے علم میں یہ کام میرے دین، میری معاش اور میرے انجامِ کار کے اعتبار سے بہتر ہے تو اسے میرے مقدر میں کر دے، میرے لیے آسان بنا دے اور اس میں برکت دے۔ اور اگر تیرے علم میں یہ کام میرے دین، میری معاش اور میرے انجامِ کار کے اعتبار سے برا ہے تو اسے مجھ سے پھیر دے اور مجھے اس سے پھیر دے، اور میرے لیے خیر مقدر فرما جہاں کہیں بھی ہو، پھر مجھے اس پر راضی کر دے۔",
      authenticReference = "Sahih al-Bukhari 1166 (Narrated by Jabir ibn Abdullah, may Allah be pleased with them)",
      virtueAndTiming = "Pray two Rak'ahs of voluntary (Nafl) prayer and then recite this supplication when making any major permissible life decision.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_nikah_newlyweds",
      title = "Dua for Newlyweds & Marriage Blessing (Nikah)",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.NIKAH_MARRIAGE,
      occasionBadge = "Marriage (Nikah) & Newlyweds",
      arabicText = "بَارَكَ اللَّهُ لَكَ، وَبَارَكَ عَلَيْكَ، وَجَمَعَ بَيْنَكُمَا فِي خَيْرٍ",
      transliteration = "Barakallahu laka, wa baraka 'alayka, wa jama'a baynakuma fi khayr.",
      englishTranslation = "May Allah bless you, and shower His blessings upon you, and join you together in goodness.",
      urduTranslation = "اللہ تعالیٰ تمہیں برکت دے، اور تم پر برکت نازل فرمائے، اور تم دونوں کو خیر اور بھلائی میں یکجا رکھے۔",
      authenticReference = "Sunan Abi Dawud 2130 • Jami' at-Tirmidhi 1091 (Sahih)",
      virtueAndTiming = "The Prophetic congratulatory supplication taught for blessing a newly married couple.",
      recommendedCount = 1
    ),
    DailyDuaItem(
      id = "dua_kaffaratul_majlis",
      title = "Kaffaratul Majlis (Supplication Before Leaving a Gathering)",
      category = DuaCategory.SPECIAL_OCCASIONS,
      occasionType = SpecialOccasionType.GATHERING_MAJLIS,
      occasionBadge = "Closing a Gathering / Study Circle",
      arabicText = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا أَنْتَ، أَسْتَغْفِرُكَ وَأَتُوبُ إِلَيْكَ",
      transliteration = "Subhanaka Allahumma wa bihamdika, ash-hadu an la ilaha illa Anta, astaghfiruka wa atubu ilayk.",
      englishTranslation = "Glory is to You, O Allah, and praise; I bear witness that there is no deity worthy of worship except You; I seek Your forgiveness and turn to You in repentance.",
      urduTranslation = "اے اللہ! تو اپنی حمد کے ساتھ پاک ہے، میں گواہی دیتا ہوں کہ تیرے سوا کوئی معبود نہیں، میں تجھ سے بخشش مانگتا ہوں اور تیری طرف توبہ کرتا ہوں۔",
      authenticReference = "Jami' at-Tirmidhi 3433 • Sunan Abi Dawud 4859 (Sahih)",
      virtueAndTiming = "Recite at the conclusion of any sitting, meeting, or study circle to expiate any unintentional shortcomings in speech.",
      recommendedCount = 1
    )
  )

  // =========================================================================
  // FRIDAY (JUMU'AH) BATH REMINDER & SUNNAH CHECKLIST
  // =========================================================================
  val fridaySunnahChecklist: List<FridaySunnahChecklistItem> = listOf(
    FridaySunnahChecklistItem(
      id = "friday_ghusl_bath",
      title = "Perform Friday Bath (Jumu'ah Ghusl) & Complete Purification",
      arabicTitle = "غُسْلُ يَوْمِ الْجُمُعَةِ",
      description = "The Prophet ﷺ said: 'Taking a bath (Ghusl) on Friday is essential for every adult Muslim.' Perform Ghusl before departing for Jumu'ah prayer.",
      authenticReference = "Sahih al-Bukhari 877 • Sahih Muslim 846"
    ),
    FridaySunnahChecklistItem(
      id = "friday_miswak_grooming",
      title = "Use Miswak (Clean Teeth) & Trim Nails",
      arabicTitle = "السِّوَاكُ وَتَقْلِيمُ الأَظْفَارِ",
      description = "Clean the mouth thoroughly with Miswak or brush and maintain personal cleanliness for the blessed Jumu'ah congregation.",
      authenticReference = "Sahih al-Bukhari 880 • Sahih Muslim 846"
    ),
    FridaySunnahChecklistItem(
      id = "friday_clean_attire_attar",
      title = "Wear Clean Best Garments & Apply Perfume (Attar)",
      arabicTitle = "لُبْسُ أَحْسَنِ الثِّيَابِ وَالطِّيبُ",
      description = "Wear clean, dignified garments (white is recommended) and apply pleasant non-alcoholic fragrance before heading to the Masjid.",
      authenticReference = "Sahih al-Bukhari 883 • Sunan Abi Dawud 343"
    ),
    FridaySunnahChecklistItem(
      id = "friday_surah_al_kahf",
      title = "Recite Surah Al-Kahf (Surah 18)",
      arabicTitle = "قِرَاءَةُ سُورَةِ الْكَهْفِ",
      description = "Whoever recites Surah Al-Kahf on Friday will have a light shining for him between this Friday and the next.",
      authenticReference = "Al-Hakim 3392 • Al-Bayhaqi (Sahih)",
      actionSurahNumber = 18
    ),
    FridaySunnahChecklistItem(
      id = "friday_abundant_salawat",
      title = "Send Abundant Salawat (Durood) Upon the Prophet ﷺ",
      arabicTitle = "الإِكْثَارُ مِنَ الصَّلَاةِ عَلَى النَّبِيِّ ﷺ",
      description = "Increase sending blessings upon Prophet Muhammad ﷺ throughout Thursday night and Friday.",
      authenticReference = "Sunan Abi Dawud 1047 • Sunan an-Nasa'i 1374",
      actionDuaId = "dua_jumuah_salawat_ibrahimiyyah"
    ),
    FridaySunnahChecklistItem(
      id = "friday_early_masjid_khutbah",
      title = "Arrive Early at the Masjid & Listen Attentively to the Khutbah",
      arabicTitle = "التَّبْكِيرُ إِلَى الْمَسْجِدِ وَالإِنْصَاتُ لِلْخُطْبَةِ",
      description = "Angels stand at the gates of the Masjid recording those who arrive early in order of arrival until the Imam ascends the Minbar.",
      authenticReference = "Sahih al-Bukhari 881 • Sahih Muslim 850"
    ),
    FridaySunnahChecklistItem(
      id = "friday_saa_at_al_istijabah",
      title = "Make Earnest Dua in the Last Hour Before Maghrib (Saa'at al-Istijabah)",
      arabicTitle = "سَاعَةُ الإِجَابَةِ يَوْمَ الْجُمُعَةِ",
      description = "Seek the special hour of acceptance on Friday (especially after Asr until Maghrib) when no Muslim asks Allah for good except that He grants it.",
      authenticReference = "Sahih al-Bukhari 935 • Sunan Abi Dawud 1048"
    )
  )

  // =========================================================================
  // NECESSARY SUNNAH & DAILY REMINDERS
  // =========================================================================
  val defaultNecessaryReminders: List<NecessaryReminderSpec> = listOf(
    NecessaryReminderSpec(
      id = "rem_friday_bath_ghusl",
      title = "Friday Bath (Jumu'ah Ghusl) & Preparation Reminder",
      arabicSnippet = "غُسْلُ يَوْمِ الجُمُعَةِ وَاجِبٌ عَلَى كُلِّ مُحْتَلِمٍ",
      subtitle = "Reminds you every Friday morning to perform Jumu'ah Ghusl (bath), use Miswak, wear clean clothes, and prepare for Jumu'ah prayer.",
      authenticReference = "Sahih al-Bukhari 877 • Sahih Muslim 846",
      frequency = ReminderFrequency.WEEKLY_FRIDAY,
      defaultTime24h = "09:30",
      timeOptions24h = listOf("08:00", "09:00", "09:30", "10:30", "11:15"),
      defaultEnabled = true,
      linkedDuaCategory = DuaCategory.SPECIAL_OCCASIONS
    ),
    NecessaryReminderSpec(
      id = "rem_friday_surah_kahf",
      title = "Friday Surah Al-Kahf Recitation Reminder",
      arabicSnippet = "مَنْ قَرَأَ سُورَةَ الْكَهْفِ فِي يَوْمِ الْجُمُعَةِ أَضَاءَ لَهُ مِنَ النُّورِ",
      subtitle = "Weekly Friday reminder to recite Surah Al-Kahf (Surah 18) for illumination and protection between the two Fridays.",
      authenticReference = "Al-Mustadrak al-Hakim 3392 • Sunan al-Bayhaqi",
      frequency = ReminderFrequency.WEEKLY_FRIDAY,
      defaultTime24h = "10:30",
      timeOptions24h = listOf("08:30", "10:30", "11:30", "14:00", "16:00"),
      defaultEnabled = true,
      linkedSurahNumber = 18
    ),
    NecessaryReminderSpec(
      id = "rem_friday_istijabah_dua",
      title = "Friday Hour of Acceptance (Asr to Maghrib Dua Reminder)",
      arabicSnippet = "فِيهِ سَاعَةٌ لَا يُوَافِقُهَا عَبْدٌ مُسْلِمٌ يَسْأَلُ اللَّهَ شَيْئًا إِلَّا أَعْطَاهُ إِيَّاهُ",
      subtitle = "Reminds you on Friday afternoon before Maghrib to devote time to earnest Dua during Saa'at al-Istijabah.",
      authenticReference = "Sahih al-Bukhari 935 • Sunan Abi Dawud 1048",
      frequency = ReminderFrequency.WEEKLY_FRIDAY,
      defaultTime24h = "16:30",
      timeOptions24h = listOf("15:45", "16:15", "16:30", "17:00", "17:30"),
      defaultEnabled = true,
      linkedDuaCategory = DuaCategory.QURANIC_RABBANA
    ),
    NecessaryReminderSpec(
      id = "rem_daily_morning_adhkar",
      title = "Daily Morning Adhkar (Subah ke Masnoon Azkar)",
      arabicSnippet = "وَسَبِّحْ بِحَمْدِ رَبِّكَ قَبْلَ طُلُوعِ الشَّمْسِ",
      subtitle = "Daily morning reminder after Fajr for Ayat al-Kursi, Sayyidul Istighfar, 3 Quls, and morning protection Duas.",
      authenticReference = "Surah Qaf 50:39 • Sahih Muslim 2723",
      frequency = ReminderFrequency.DAILY,
      defaultTime24h = "06:30",
      timeOptions24h = listOf("05:45", "06:15", "06:30", "07:00", "07:30"),
      defaultEnabled = true,
      linkedDuaCategory = DuaCategory.MORNING_EVENING
    ),
    NecessaryReminderSpec(
      id = "rem_daily_evening_adhkar",
      title = "Daily Evening Adhkar (Shaam ke Masnoon Azkar)",
      arabicSnippet = "وَقَبْلَ الْغُرُوبِ",
      subtitle = "Daily afternoon/evening reminder after Asr for evening protection supplications and Istighfar.",
      authenticReference = "Surah Qaf 50:39 • Sunan Abi Dawud 5088",
      frequency = ReminderFrequency.DAILY,
      defaultTime24h = "17:30",
      timeOptions24h = listOf("16:45", "17:15", "17:30", "18:00", "18:30"),
      defaultEnabled = true,
      linkedDuaCategory = DuaCategory.MORNING_EVENING
    ),
    NecessaryReminderSpec(
      id = "rem_nightly_sleep_sunnah",
      title = "Before-Sleep Sunnah Reminder (Wudu, Ayat al-Kursi & Surah Al-Mulk)",
      arabicSnippet = "تَبَٰرَكَ ٱلَّذِى بِيَدِهِ ٱلْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَىْءٍ قَدِيرٌ",
      subtitle = "Nightly reminder to sleep in a state of Wudu, recite Surah Al-Mulk (Surah 67), Ayat al-Kursi (2:255), and the last two verses of Surah Al-Baqarah.",
      authenticReference = "Sahih al-Bukhari 247 & 5017 • Jami' at-Tirmidhi 2891",
      frequency = ReminderFrequency.DAILY,
      defaultTime24h = "21:45",
      timeOptions24h = listOf("20:30", "21:00", "21:45", "22:15", "22:45"),
      defaultEnabled = true,
      linkedSurahNumber = 67
    ),
    NecessaryReminderSpec(
      id = "rem_mon_thu_fasting",
      title = "Monday & Thursday Sunnah Fasting Reminder",
      arabicSnippet = "تُعْرَضُ الأَعْمَالُ يَوْمَ الاِثْنَيْنِ وَالخَمِيسِ",
      subtitle = "Evening reminder on Sundays and Wednesdays to prepare for voluntary Sunnah fasting on Monday and Thursday.",
      authenticReference = "Jami' at-Tirmidhi 747 • Sahih Muslim 1162",
      frequency = ReminderFrequency.SUNNAH_FASTING_DAYS,
      defaultTime24h = "20:30",
      timeOptions24h = listOf("19:30", "20:00", "20:30", "21:00"),
      defaultEnabled = true,
      linkedDuaCategory = DuaCategory.SPECIAL_OCCASIONS
    ),
    NecessaryReminderSpec(
      id = "rem_ayyam_al_beed",
      title = "Ayyam al-Beed (13th, 14th & 15th Hijri White Days) Reminder",
      arabicSnippet = "صِيَامُ ثَلَاثَةِ أَيَّامٍ مِنْ كُلِّ شَهْرٍ",
      subtitle = "Monthly reminder for the three blessed lunar days of every Islamic month recommended by the Prophet ﷺ.",
      authenticReference = "Sahih al-Bukhari 1978 • Sahih Muslim 721",
      frequency = ReminderFrequency.MONTHLY_HIJRI,
      defaultTime24h = "20:00",
      timeOptions24h = listOf("19:00", "20:00", "21:00"),
      defaultEnabled = true,
      linkedDuaCategory = DuaCategory.SPECIAL_OCCASIONS
    ),
    NecessaryReminderSpec(
      id = "rem_calculated_daily_day_sunnah",
      title = "Calculated Today & Weekday Sunnah Notification (Friday & All 7 Days)",
      arabicSnippet = "خَيْرُ يَوْمٍ طَلَعَتْ عَلَيْهِ الشَّمْسُ يَوْمُ الْجُمُعَةِ",
      subtitle = "Automatically calculates whether today is Friday (Jumu'ah), Monday/Thursday Fasting Day, Ayyam al-Beed, or another weekday and sends the day's tailored Sunnah notification.",
      authenticReference = "Sahih Muslim 854 • Sahih al-Bukhari 877 • Jami' at-Tirmidhi 747",
      frequency = ReminderFrequency.DAILY,
      defaultTime24h = "08:00",
      timeOptions24h = listOf("07:00", "08:00", "08:30", "09:00", "10:00"),
      defaultEnabled = true,
      linkedDuaCategory = DuaCategory.MORNING_EVENING
    )
  )

  // =========================================================================
  // CALCULATED 7-DAY WEEKDAY SUNNAH & REMINDER SPECIFICATIONS (FRI..THU)
  // =========================================================================
  val weekdaySunnahSpecs: List<WeekdaySunnahSpec> = listOf(
    WeekdaySunnahSpec(
      calendarDayOfWeek = java.util.Calendar.FRIDAY,
      dayNameEnglish = "Friday",
      shortDayLabel = "Fri",
      islamicDayTitle = "Yawm al-Jumu'ah (Master of Days)",
      arabicDayName = "يَوْمُ الْجُمُعَةِ",
      primaryReminderTitle = "Blessed Friday (Jumu'ah): Perform Ghusl (Bath), Recite Surah Al-Kahf & Abundant Salawat",
      arabicHadithSnippet = "«خَيْرُ يَوْمٍ طَلَعَتْ عَلَيْهِ الشَّمْسُ يَوْمُ الْجُمُعَةِ» • «غُسْلُ يَوْمِ الجُمُعَةِ وَاجِبٌ عَلَى كُلِّ مُحْتَلِمٍ»",
      englishGuidance = "Today is Blessed Friday (Yawm al-Jumu'ah). Perform the Friday Bath (Ghusl), use Miswak, wear clean garments & Attar, arrive early for Khutbah, recite Surah Al-Kahf (Surah 18), send abundant Salawat upon the Prophet ﷺ, and make earnest Dua in Saa'at al-Istijabah before Maghrib.",
      urduGuidance = "آج جمعۃ المبارک ہے: غسلِ جمعہ کریں، مسواک اور خوشبو استعمال کریں، سورۃ الکہف کی تلاوت کریں، نبی کریم ﷺ پر کثرت سے درود شریف بھیجیں، اور عصر تا مغرب قبولیت کی گھڑی میں دعا کا اہتمام کریں۔",
      authenticReference = "Sahih al-Bukhari 877 & 935 • Sahih Muslim 846 & 854 • Sunan Abi Dawud 1047",
      keySunnahBullets = listOf(
        "Perform Friday Bath (Jumu'ah Ghusl) & use Miswak (Bukhari 877)",
        "Recite Surah Al-Kahf (Surah 18) for light between the two Fridays (Al-Hakim 3392)",
        "Send abundant Salawat (Durood) upon Prophet Muhammad ﷺ (Abu Dawud 1047)",
        "Make earnest Dua in the last hour after Asr before Maghrib (Bukhari 935)"
      ),
      isFridayJumuah = true,
      isSunnahFastingDay = false,
      linkedSurahNumber = 18,
      linkedReminderId = "rem_friday_bath_ghusl"
    ),
    WeekdaySunnahSpec(
      calendarDayOfWeek = java.util.Calendar.SATURDAY,
      dayNameEnglish = "Saturday",
      shortDayLabel = "Sat",
      islamicDayTitle = "Yawm as-Sabt (Salatud-Duha & Masjid Visitation)",
      arabicDayName = "يَوْمُ السَّبْتِ",
      primaryReminderTitle = "Saturday Sunnah: Pray Salatud-Duha, Morning/Evening Adhkar & Uphold Family Ties",
      arabicHadithSnippet = "«كَانَ النَّبِيُّ ﷺ يَأْتِي مَسْجِدَ قُبَاءٍ كُلَّ سَبْتٍ رَاكِبًا وَمَاشِيًا فَيُصَلِّي فِيهِ رَكْعَتَيْنِ»",
      englishGuidance = "On Saturdays, the Prophet ﷺ regularly went to Masjid Quba and prayed two Rak'ahs. Observe Salatud-Duha (the Forenoon Prayer of the Oft-Repentant), complete Morning & Evening Masnoon Adhkar, and strengthen kinship ties (Silat ar-Rahim).",
      urduGuidance = "ہفتے کے دن سنتِ نبوی ﷺ کے مطابق چاشت (صلاۃ الضحیٰ) کی دو رکعتیں ادا کریں، صبح و شام کے مسنون اذکار پڑھیں اور صلہ رحمی (رشتہ داروں سے حسنِ سلوک) کا اہتمام کریں۔",
      authenticReference = "Sahih al-Bukhari 1193 • Sahih Muslim 1399 & 748",
      keySunnahBullets = listOf(
        "Pray 2 or 4 Rak'ahs of Salatud-Duha (Charity for every joint — Muslim 720)",
        "Recite Morning & Evening Masnoon Adhkar (Ayat al-Kursi & Sayyidul Istighfar)",
        "Check on parents, family, and neighbors (Silat ar-Rahim — Bukhari 5985)"
      ),
      isFridayJumuah = false,
      isSunnahFastingDay = false,
      linkedSurahNumber = 67
    ),
    WeekdaySunnahSpec(
      calendarDayOfWeek = java.util.Calendar.SUNDAY,
      dayNameEnglish = "Sunday",
      shortDayLabel = "Sun",
      islamicDayTitle = "Yawm al-Ahad (Tawhid Reflection & Prepare for Monday Fast)",
      arabicDayName = "يَوْمُ الأَحَدِ",
      primaryReminderTitle = "Sunday Reminder: Daily Quranic Wird, Evening Adhkar & Prepare for Tomorrow's Monday Sunnah Fast",
      arabicHadithSnippet = "«تُعْرَضُ الأَعْمَالُ يَوْمَ الاِثْنَيْنِ وَالخَمِيسِ، فَأُحِبُّ أَنْ يُعْرَضَ عَمَلِي وَأَنَا صَائِمٌ»",
      englishGuidance = "Complete your daily Quranic recitation, Morning & Evening Adhkar, and before-sleep Surah Al-Mulk (Surah 67). Tonight (Sunday evening), form the intention for tomorrow's voluntary Monday Sunnah fast if able.",
      urduGuidance = "آج قرآنِ کریم کا روزانہ ورد اور صبح و شام کے مسنون اذکار مکمل کریں، رات کو سورۃ الملک پڑھیں، اور کل پیر (دوشنبہ) کے نفلی روزے کی نیت اور سحری کی تیاری کریں۔",
      authenticReference = "Jami' at-Tirmidhi 747 • Sahih al-Bukhari 5017 • Jami' at-Tirmidhi 2891",
      keySunnahBullets = listOf(
        "Prepare tonight for Monday's Sunnah Fast (Tirmidhi 747)",
        "Recite Quranic Rabbana Duas & Sayyidul Istighfar (Bukhari 6306)",
        "Recite Surah Al-Mulk (Surah 67) & Ayat al-Kursi before sleeping"
      ),
      isFridayJumuah = false,
      isSunnahFastingDay = false,
      linkedSurahNumber = 67,
      linkedReminderId = "rem_mon_thu_fasting"
    ),
    WeekdaySunnahSpec(
      calendarDayOfWeek = java.util.Calendar.MONDAY,
      dayNameEnglish = "Monday",
      shortDayLabel = "Mon",
      islamicDayTitle = "Yawm al-Ithnayn (Sunnah Fast & Presentation of Deeds)",
      arabicDayName = "يَوْمُ الإِثْنَيْنِ",
      primaryReminderTitle = "Monday Sunnah: Voluntary Fasting, Presentation of Deeds & Prophetic Remembrance",
      arabicHadithSnippet = "«ذَاكَ يَوْمٌ وُلِدْتُ فِيهِ، وَيَوْمٌ بُعِثْتُ أَوْ أُنْزِلَ عَلَيَّ فِيهِ»",
      englishGuidance = "Monday is a blessed day on which the Prophet ﷺ was born, received revelation, and on which deeds are presented to Allah. Observe the Monday Sunnah fast if able, guard the tongue, and increase Istighfar and Salawat.",
      urduGuidance = "پیر (دوشنبہ) مبارک دن ہے جس میں نبی کریم ﷺ کی ولادت ہوئی، وحی کا آغاز ہوا اور اللہ کے حضور اعمال پیش ہوتے ہیں۔ سنت روزہ، استغفار اور درود شریف کا اہتمام کریں۔",
      authenticReference = "Sahih Muslim 1162 • Jami' at-Tirmidhi 747",
      keySunnahBullets = listOf(
        "Observe Monday voluntary Sunnah fast & break fast with Dua at Maghrib (Muslim 1162)",
        "Increase Istighfar & righteous deeds as actions are presented to Allah today (Tirmidhi 747)",
        "Maintain Morning & Evening Masnoon Adhkar after Fajr and Asr"
      ),
      isFridayJumuah = false,
      isSunnahFastingDay = true,
      linkedSurahNumber = 2,
      linkedReminderId = "rem_mon_thu_fasting"
    ),
    WeekdaySunnahSpec(
      calendarDayOfWeek = java.util.Calendar.TUESDAY,
      dayNameEnglish = "Tuesday",
      shortDayLabel = "Tue",
      islamicDayTitle = "Yawm ath-Thulatha' (Steadfast Salah & Protection Adhkar)",
      arabicDayName = "يَوْمُ الثُّلَاثَاءِ",
      primaryReminderTitle = "Tuesday Sunnah: 12 Sunnah Rawatib Rak'ahs, Protection Duas & Nightly Surah Al-Mulk",
      arabicHadithSnippet = "«مَنْ صَلَّى اثْنَتَيْ عَشْرَةَ رَكْعَةً فِي يَوْمٍ وَلَيْلَةٍ بُنِيَ لَهُ بِهِنَّ بَيْتٌ فِي الْجَنَّةِ»",
      englishGuidance = "Guard the five daily obligatory prayers and the 12 daily Sunnah Rawatib Rak'ahs (for which a house is built in Paradise). Recite the 3x Morning/Evening protection Dua ('Bismillahilladhi la yadurru...') and Surah Al-Mulk at night.",
      urduGuidance = "فرض نمازوں کے ساتھ بارہ رکعت سنتِ مؤکدہ (رواتب) کی پابندی کریں جس پر جنت میں گھر کی بشارت ہے، اور صبح و شام حفاظت کی مسنون دعائیں پڑھیں۔",
      authenticReference = "Sahih Muslim 728 • Sunan Abi Dawud 5088 • Jami' at-Tirmidhi 2891",
      keySunnahBullets = listOf(
        "Pray the 12 Sunnah Rawatib Rak'ahs (2 before Fajr, 4+2 Dhuhr, 2 Maghrib, 2 Isha — Muslim 728)",
        "Recite 'Bismillahilladhi la yadurru...' 3x morning & evening (Abu Dawud 5088)",
        "Sleep in Wudu after reciting Ayat al-Kursi and Surah Al-Mulk (Surah 67)"
      ),
      isFridayJumuah = false,
      isSunnahFastingDay = false,
      linkedSurahNumber = 67
    ),
    WeekdaySunnahSpec(
      calendarDayOfWeek = java.util.Calendar.WEDNESDAY,
      dayNameEnglish = "Wednesday",
      shortDayLabel = "Wed",
      islamicDayTitle = "Yawm al-Arbi'a' (Dua Between Dhuhr & Asr + Prepare for Thursday Fast)",
      arabicDayName = "يَوْمُ الأَرْبِعَاءِ",
      primaryReminderTitle = "Wednesday Sunnah: Earnest Dua Between Dhuhr & Asr + Prepare for Thursday Sunnah Fast",
      arabicHadithSnippet = "«فَاسْتُجِيبَ لَهُ يَوْمَ الأَرْبِعَاءِ بَيْنَ الصَّلَاتَيْنِ الظُّهْرِ وَالعَصْرِ»",
      englishGuidance = "Jabir ibn Abdullah (RA) reported that the Prophet ﷺ supplicated between Dhuhr and Asr on Wednesday and his supplication was answered. Devote time to Dua in the afternoon, and prepare tonight for tomorrow's Thursday Sunnah fast.",
      urduGuidance = "بدھ کے دن ظہر اور عصر کے درمیان دعا قبول ہونے کی سنتِ نبوی ﷺ ثابت ہے، اس وقت دعا کا اہتمام کریں اور کل جمعرات کے مسنون روزے کی تیاری کریں۔",
      authenticReference = "Musnad Ahmad 14563 • Al-Bukhari (Al-Adab al-Mufrad 704) • Jami' at-Tirmidhi 747",
      keySunnahBullets = listOf(
        "Make earnest Dua between Dhuhr and Asr prayer today (Adab al-Mufrad 704)",
        "Prepare tonight (Wednesday evening) for Thursday's Sunnah Fast (Tirmidhi 747)",
        "Complete Evening Masnoon Adhkar & Istighfar (100x daily — Muslim 2702)"
      ),
      isFridayJumuah = false,
      isSunnahFastingDay = false,
      linkedSurahNumber = 18,
      linkedReminderId = "rem_mon_thu_fasting"
    ),
    WeekdaySunnahSpec(
      calendarDayOfWeek = java.util.Calendar.THURSDAY,
      dayNameEnglish = "Thursday",
      shortDayLabel = "Thu",
      islamicDayTitle = "Yawm al-Khamis (Sunnah Fast, Deeds Presented & Jumu'ah Eve at Maghrib)",
      arabicDayName = "يَوْمُ الْخَمِيسِ",
      primaryReminderTitle = "Thursday Sunnah: Fast Today & Welcome Blessed Laylatul Jumu'ah (Friday Eve) at Maghrib",
      arabicHadithSnippet = "«أَكْثِرُوا الصَّلَاةَ عَلَيَّ يَوْمَ الْجُمُعَةِ وَلَيْلَةَ الْجُمُعَةِ»",
      englishGuidance = "Deeds are presented to Allah on Thursday, making it a beloved Sunnah fasting day. At Maghrib tonight, the blessed Night of Friday (Laylatul Jumu'ah) begins — start sending abundant Salawat upon the Prophet ﷺ and prepare for tomorrow's Friday Bath (Ghusl) and Surah Al-Kahf.",
      urduGuidance = "جمعرات کے دن اللہ کے حضور اعمال پیش ہوتے ہیں اور روزہ رکھنا سنت ہے۔ آج مغرب سے شبِ جمعہ کا آغاز ہوتا ہے: کثرت سے درود شریف پڑھیں اور کل کے غسلِ جمعہ و سورۃ الکہف کی تیاری کریں۔",
      authenticReference = "Jami' at-Tirmidhi 747 • Sunan Abi Dawud 1047 • Al-Bayhaqi 5994",
      keySunnahBullets = listOf(
        "Observe Thursday voluntary Sunnah fast & break fast with Masnoon Dua (Tirmidhi 747)",
        "Begin abundant Salawat on the Prophet ﷺ from Maghrib tonight (Laylatul Jumu'ah)",
        "Confirm tomorrow's Friday Bath (Jumu'ah Ghusl) morning reminder time"
      ),
      isFridayJumuah = false,
      isSunnahFastingDay = true,
      linkedSurahNumber = 18,
      linkedReminderId = "rem_friday_bath_ghusl"
    )
  )

  fun getWeekdaySpec(calendarDayOfWeek: Int): WeekdaySunnahSpec {
    return weekdaySunnahSpecs.find { it.calendarDayOfWeek == calendarDayOfWeek }
      ?: weekdaySunnahSpecs.first()
  }
}
