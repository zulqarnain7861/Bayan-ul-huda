package com.example.data.model

data class ReflectionItem(
  val id: String,
  val category: ReflectionCategory,
  val title: String,
  val theme: String,
  val arabicOrOriginalText: String,
  val translationEnglish: String,
  val translationUrdu: String,
  val sourceReference: String,
  val contextNotes: String = ""
)

enum class ReflectionCategory {
  PROPHETIC_GUIDANCE,
  COMPANIONS_WISDOM
}

object ReflectionDataProvider {
  val items = listOf(
    ReflectionItem(
      id = "ref_1",
      category = ReflectionCategory.PROPHETIC_GUIDANCE,
      theme = "Mercy & Compassion",
      title = "Mercy to All of Creation",
      arabicOrOriginalText = "الرَّاحِمُونَ يَرْحَمُهُمُ الرَّحْمَنُ، ارْحَمُوا مَنْ فِي الأَرْضِ يَرْحَمْكُمْ مَنْ فِي السَّمَاءِ",
      translationEnglish = "Those who show mercy will be shown mercy by the Most Merciful. Show mercy to those on earth, and the One in the heavens will show mercy to you.",
      translationUrdu = "رحم کرنے والوں پر رحمٰن رحم فرماتا ہے۔ تم زمین والوں پر رحم کرو، آسمان والا تم پر رحم کرے گا۔",
      sourceReference = "Jami' at-Tirmidhi 1924 (Sahih)",
      contextNotes = "A foundational principle of Prophetic ethics governing mercy toward all creation."
    ),
    ReflectionItem(
      id = "ref_2",
      category = ReflectionCategory.PROPHETIC_GUIDANCE,
      theme = "Contentment & Gratitude",
      title = "True Wealth of the Soul",
      arabicOrOriginalText = "لَيْسَ الْغِنَى عَنْ كَثْرَةِ الْعَرَضِ، وَلَكِنَّ الْغِنَى غِنَى النَّفْسِ",
      translationEnglish = "Richness does not lie in an abundance of worldly goods; true richness is the richness of the soul.",
      translationUrdu = "مالداری ساز و سامان کی کثرت سے نہیں، بلکہ حقیقی مالداری دل کی بے نیازی ہے۔",
      sourceReference = "Sahih al-Bukhari 6446, Sahih Muslim 1051",
      contextNotes = "Direct Prophetic guidance on prioritizing inner tranquility and spiritual contentment."
    ),
    ReflectionItem(
      id = "ref_3",
      category = ReflectionCategory.PROPHETIC_GUIDANCE,
      theme = "Kindness & Gentleness",
      title = "The Beauty of Gentleness (Al-Rifq)",
      arabicOrOriginalText = "إِنَّ الرِّفْقَ لاَ يَكُونُ فِي شَيْءٍ إِلاَّ زَانَهُ، وَلاَ يُنْزَعُ مِنْ شَيْءٍ إِلاَّ شَانَهُ",
      translationEnglish = "Indeed, gentleness is not in anything except that it beautifies it, and it is not removed from anything except that it disgraces it.",
      translationUrdu = "نرمی جس چیز میں بھی ہو اسے خوبصورت بنا دیتی ہے، اور جس چیز سے نکال دی جائے اسے عیب دار کر دیتی ہے۔",
      sourceReference = "Sahih Muslim 2594",
      contextNotes = "Narrated by Umm al-Mu'minin Aisha (RA) regarding speech, character, and conduct."
    ),
    ReflectionItem(
      id = "ref_4",
      category = ReflectionCategory.PROPHETIC_GUIDANCE,
      theme = "Purification of the Heart",
      title = "The Soundness of the Heart (Al-Qalb)",
      arabicOrOriginalText = "أَلَا وَإِنَّ فِي الْجَسَدِ مُضْغَةً إِذَا صَلَحَتْ صَلَحَ الْجَسَدُ كُلُّهُ، وَإِذَا فَسَدَتْ فَسَدَ الْجَسَدُ كُلُّهُ، أَلَا وَهِيَ الْقَلْبُ",
      translationEnglish = "Truly in the body there is a morsel of flesh which, if it be whole, all the body is whole, and which, if it be diseased, all of it is diseased. Truly, it is the heart.",
      translationUrdu = "خبردار! جسم میں گوشت کا ایک ٹکڑا ہے، جب وہ سنور جائے تو پورا جسم سنور جاتا ہے اور جب وہ بگڑ جائے تو پورا جسم بگڑ جاتا ہے، سن لو وہ دل ہے۔",
      sourceReference = "Sahih al-Bukhari 52, Sahih Muslim 1599",
      contextNotes = "Part of the comprehensive Hadith of An-Nu'man ibn Bashir (RA) on lawful and doubtful matters."
    ),
    ReflectionItem(
      id = "ref_5",
      category = ReflectionCategory.COMPANIONS_WISDOM,
      theme = "Self-Accountability (Muhasabah)",
      title = "Take Account of Yourselves",
      arabicOrOriginalText = "حَاسِبُوا أَنْفُسَكُمْ قَبْلَ أَنْ تُحَاسَبُوا، وَزِنُوا أَعْمَالَكُمْ قَبْلَ أَنْ تُوزَنَ عَلَيْكُمْ",
      translationEnglish = "Take account of yourselves before you are taken to account, and weigh your deeds before they are weighed for you.",
      translationUrdu = "اپنا محاسبہ کرو اس سے پہلے کہ تمہارا حساب لیا جائے، اور اپنے اعمال کو تولو اس سے پہلے کہ وہ تمہارے لیے تولے جائیں۔",
      sourceReference = "Amir al-Mu'minin Umar ibn al-Khattab (RA) — Jami' at-Tirmidhi 2459",
      contextNotes = "Classical counsel of Umar ibn al-Khattab (RA) on daily spiritual reflection and preparation for the Hereafter."
    )
  )
}
