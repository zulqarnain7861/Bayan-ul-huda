package com.example.data.prayer

import android.content.Context

enum class AzanNotificationMode(val label: String) {
  FULL_AZAN("Full Azan Audio"),
  SHORT_TONE("Short Takbir / Notification"),
  SILENT("Silent Reminder"),
  OFF("Disabled")
}

enum class MuezzinAudioOption(
  val id: String,
  val title: String,
  val subtitle: String,
  val streamUrl: String,
  val includesFajrTathwib: Boolean = false
) {
  MAKKAH_HARAM(
    id = "makkah_haram",
    title = "Makkah al-Mukarramah (Masjid al-Haram)",
    subtitle = "Pure Vocal Haramain Azan Recording (No Music)",
    streamUrl = "https://www.islamcan.com/audio/adhan/azan1.mp3"
  ),
  MADINAH_NABAWI(
    id = "madinah_nabawi",
    title = "Al-Madinah al-Munawwarah (Masjid an-Nabawi)",
    subtitle = "Pure Vocal Madinah Classical Azan Recording (No Music)",
    streamUrl = "https://www.islamcan.com/audio/adhan/azan2.mp3"
  ),
  AL_AQSA(
    id = "al_aqsa",
    title = "Al-Quds (Masjid al-Aqsa Vocal Azan)",
    subtitle = "Pure Vocal Al-Aqsa Azan Recording (No Music)",
    streamUrl = "https://www.islamcan.com/audio/adhan/azan3.mp3"
  ),
  FAJR_TATHWIB(
    id = "fajr_tathwib",
    title = "Verified Fajr Azan (With As-salatu khayrun minan-nawm)",
    subtitle = "Pure Vocal Fajr Azan with الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ (No Music)",
    streamUrl = "https://www.islamcan.com/audio/adhan/azan1.mp3",
    includesFajrTathwib = true
  )
}

enum class AppThemeMode(val label: String, val description: String) {
  AUTO("System Default", "Automatically switches based on system & time"),
  LIGHT("Light", "Warm Cream & Emerald for daytime clarity"),
  DARK("Dark", "Deep Night Obsidian & Soft Gold for nighttime"),
  PURE_BLACK("Pure Black (AMOLED)", "True #000000 OLED black for zero-glare night reading"),
  MADINAH_PARCHMENT("Warm Paper", "Soft Warm Paper & Illuminated Manuscript Ivory"),
  EYE_COMFORT("Sepia", "Warm Sepia Low-Glare Amber for extended reading"),
  ROYAL_EMERALD("Green", "Deep Haramain Sanctuary Emerald & Burnished Gold"),
  SAPPHIRE_BLUE("Blue", "Classical Islamic Lapis Blue & Warm Gold");

  companion object {
    val SYSTEM_DEFAULT = AUTO
    val WARM_PAPER = MADINAH_PARCHMENT
    val SEPIA = EYE_COMFORT
    val GREEN = ROYAL_EMERALD
    val BLUE = SAPPHIRE_BLUE
  }
}

data class PrayerAndAlarmConfig(
  val city: String = "Srinagar",
  val districtOrRegion: String = "Srinagar District, Kashmir",
  val country: String = "India",
  val latitude: Double = 34.0837,
  val longitude: Double = 74.7973,
  val timeZoneId: String = "Asia/Kolkata",
  val useAutoLocation: Boolean = false,
  val calculationMethodId: Int = 1, // Karachi (18°, 18°) default for Kashmir/India
  val asrMadhhab: Int = 1, // 1 = Hanafi, 0 = Shafi'i
  val highLatitudeRule: Int = 0,
  // Per-prayer Azan modes
  val fajrAzanMode: AzanNotificationMode = AzanNotificationMode.FULL_AZAN,
  val dhuhrAzanMode: AzanNotificationMode = AzanNotificationMode.FULL_AZAN,
  val asrAzanMode: AzanNotificationMode = AzanNotificationMode.FULL_AZAN,
  val maghribAzanMode: AzanNotificationMode = AzanNotificationMode.FULL_AZAN,
  val ishaAzanMode: AzanNotificationMode = AzanNotificationMode.FULL_AZAN,
  val selectedMuezzinId: String = MuezzinAudioOption.MAKKAH_HARAM.id,
  val prePrayerReminderMinutes: Int = 10, // 0, 5, 10, 15, 20
  // Dedicated 15-Minute Fajr Wake-Up Alarm
  val fajrWakeUpAlarmEnabled: Boolean = true,
  val fajrWakeUpOffsetMinutes: Int = 30, // 60, 45, 30, 20, 15, 10 or custom
  val tahajjudWakeUpEnabled: Boolean = false,
  val wakeUpLanguage: String = "ALL", // "ALL", "AR", "UR", "EN"
  val progressiveSequence: Boolean = true, // Gentle tone -> Fajr Azan -> Progressive Wake-Up
  val alarmVolumePercent: Int = 85,
  val boostSystemAlarmStream: Boolean = false,
  val vibrationEnabled: Boolean = true,
  val defaultSnoozeMinutes: Int = 5, // 5 or 10
  // Appearance theme
  val themeMode: AppThemeMode = AppThemeMode.LIGHT
)

object PrayerPreferencesManager {

  private const val PREFS_NAME = "bayan_prayer_and_alarm_prefs"

  fun loadConfig(context: Context): PrayerAndAlarmConfig {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return PrayerAndAlarmConfig(
      city = prefs.getString("city", "Srinagar") ?: "Srinagar",
      districtOrRegion = prefs.getString("district", "Srinagar District, Kashmir") ?: "Srinagar District, Kashmir",
      country = prefs.getString("country", "India") ?: "India",
      latitude = prefs.getString("latitude", "34.0837")?.toDoubleOrNull() ?: 34.0837,
      longitude = prefs.getString("longitude", "74.7973")?.toDoubleOrNull() ?: 74.7973,
      timeZoneId = prefs.getString("timeZoneId", "Asia/Kolkata") ?: "Asia/Kolkata",
      useAutoLocation = prefs.getBoolean("useAutoLocation", false),
      calculationMethodId = prefs.getInt("calculationMethodId", 1),
      asrMadhhab = prefs.getInt("asrMadhhab", 1),
      highLatitudeRule = prefs.getInt("highLatitudeRule", 0),
      fajrAzanMode = parseAzanMode(prefs.getString("fajrAzanMode", AzanNotificationMode.FULL_AZAN.name)),
      dhuhrAzanMode = parseAzanMode(prefs.getString("dhuhrAzanMode", AzanNotificationMode.FULL_AZAN.name)),
      asrAzanMode = parseAzanMode(prefs.getString("asrAzanMode", AzanNotificationMode.FULL_AZAN.name)),
      maghribAzanMode = parseAzanMode(prefs.getString("maghribAzanMode", AzanNotificationMode.FULL_AZAN.name)),
      ishaAzanMode = parseAzanMode(prefs.getString("ishaAzanMode", AzanNotificationMode.FULL_AZAN.name)),
      selectedMuezzinId = prefs.getString("selectedMuezzinId", MuezzinAudioOption.MAKKAH_HARAM.id)
        ?: MuezzinAudioOption.MAKKAH_HARAM.id,
      prePrayerReminderMinutes = prefs.getInt("prePrayerReminderMinutes", 10),
      fajrWakeUpAlarmEnabled = prefs.getBoolean("fajrWakeUpAlarmEnabled", true),
      fajrWakeUpOffsetMinutes = prefs.getInt("fajrWakeUpOffsetMinutes", 30),
      tahajjudWakeUpEnabled = prefs.getBoolean("tahajjudWakeUpEnabled", false),
      wakeUpLanguage = prefs.getString("wakeUpLanguage", "ALL") ?: "ALL",
      progressiveSequence = prefs.getBoolean("progressiveSequence", true),
      alarmVolumePercent = prefs.getInt("alarmVolumePercent", 85).coerceIn(20, 100),
      boostSystemAlarmStream = prefs.getBoolean("boostSystemAlarmStream", false),
      vibrationEnabled = prefs.getBoolean("vibrationEnabled", true),
      defaultSnoozeMinutes = prefs.getInt("defaultSnoozeMinutes", 5),
      themeMode = parseThemeMode(prefs.getString("themeMode", AppThemeMode.LIGHT.name))
    )
  }

  fun saveConfig(context: Context, config: PrayerAndAlarmConfig) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit()
      .putString("city", config.city)
      .putString("district", config.districtOrRegion)
      .putString("country", config.country)
      .putString("latitude", config.latitude.toString())
      .putString("longitude", config.longitude.toString())
      .putString("timeZoneId", config.timeZoneId)
      .putBoolean("useAutoLocation", config.useAutoLocation)
      .putInt("calculationMethodId", config.calculationMethodId)
      .putInt("asrMadhhab", config.asrMadhhab)
      .putInt("highLatitudeRule", config.highLatitudeRule)
      .putString("fajrAzanMode", config.fajrAzanMode.name)
      .putString("dhuhrAzanMode", config.dhuhrAzanMode.name)
      .putString("asrAzanMode", config.asrAzanMode.name)
      .putString("maghribAzanMode", config.maghribAzanMode.name)
      .putString("ishaAzanMode", config.ishaAzanMode.name)
      .putString("selectedMuezzinId", config.selectedMuezzinId)
      .putInt("prePrayerReminderMinutes", config.prePrayerReminderMinutes)
      .putBoolean("fajrWakeUpAlarmEnabled", config.fajrWakeUpAlarmEnabled)
      .putInt("fajrWakeUpOffsetMinutes", config.fajrWakeUpOffsetMinutes)
      .putBoolean("tahajjudWakeUpEnabled", config.tahajjudWakeUpEnabled)
      .putString("wakeUpLanguage", config.wakeUpLanguage)
      .putBoolean("progressiveSequence", config.progressiveSequence)
      .putInt("alarmVolumePercent", config.alarmVolumePercent)
      .putBoolean("boostSystemAlarmStream", config.boostSystemAlarmStream)
      .putBoolean("vibrationEnabled", config.vibrationEnabled)
      .putInt("defaultSnoozeMinutes", config.defaultSnoozeMinutes)
      .putString("themeMode", config.themeMode.name)
      .apply()
  }

  private fun parseAzanMode(raw: String?): AzanNotificationMode {
    return AzanNotificationMode.entries.find { it.name == raw } ?: AzanNotificationMode.FULL_AZAN
  }

  private fun parseThemeMode(raw: String?): AppThemeMode {
    return AppThemeMode.entries.find { it.name == raw } ?: AppThemeMode.LIGHT
  }
}
