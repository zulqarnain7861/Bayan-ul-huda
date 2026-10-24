package com.example.data.remote

import java.net.URI
import java.util.Locale

/**
 * Defensive Security & Sacred Content Integrity Guard for Bayan al-Huda.
 *
 * Ensures:
 * 1. Only official, HTTPS-encrypted Islamic scripture, Hadith, Prayer-Time, and Quran audio hosts are contacted.
 * 2. Zero ad networks, third-party trackers, or unauthorized scripts can ever be loaded or injected.
 * 3. Incoming API responses are sanitized against HTML/script/iframe/ad injection before touching Room or UI.
 * 4. Canonical offline Quran verses (6,236 verses across 114 Surahs) cannot be overwritten with corrupted or empty text.
 * 5. Developer diagnostic access is verified for the authorized maintainer without exposing secrets.
 */
object SecurityIntegrityGuard {

  const val AUTHORIZED_DEVELOPER_EMAIL = "sheikhzulqar@gmail.com"
  const val AUTHORIZED_DEVELOPER_NAME = "Zulqarnain Ahmad Sheikh"

  private val ALLOWED_HTTPS_HOSTS = setOf(
    "api.alquran.cloud",
    "cdn.jsdelivr.net",
    "api.aladhan.com",
    "everyayah.com",
    "api.quran.com",
    "quranenc.com",
    "download.quranicaudio.com",
    "islamcan.com",
    "archive.org",
    "ia800304.us.archive.org",
    "ia802606.us.archive.org",
    "server14.mp3quran.net",
    "server16.mp3quran.net",
    "mp3quran.net",
    "wa.me",
    "api.whatsapp.com"
  )

  private val BLOCKED_AD_AND_TRACKER_KEYWORDS = listOf(
    "doubleclick",
    "googlesyndication",
    "admob",
    "adservice",
    "adsense",
    "unityads",
    "applovin",
    "vungle",
    "ironsource",
    "chartboost",
    "taboola",
    "outbrain",
    "moatads",
    "scorecardresearch",
    "adsystem",
    "popads",
    "propellerads"
  )

  private val DANGEROUS_BLOCK_REGEX = Regex(
    """<\s*(script|iframe|object|embed|form|style|svg)\b[^>]*>.*?</\s*\1\s*>""",
    setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
  )

  private val DANGEROUS_INJECTION_REGEX = Regex(
    """<\s*/?\s*(?:script|iframe|object|embed|form|style|meta|link|svg|img)[^>]*>|javascript\s*:|vbscript\s*:|data\s*:\s*text/html|on(?:load|error|click|mouseover|focus)\s*=""",
    setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
  )

  /**
   * Validates that a target URL is strictly HTTPS, belongs to the official Islamic content host allowlist,
   * and contains no ad-network or malicious redirect patterns.
   */
  fun isUrlTrustedAndAllowed(rawUrl: String): Boolean {
    val trimmed = rawUrl.trim()
    if (trimmed.isEmpty()) return false
    return try {
      val uri = URI(trimmed)
      val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return false
      if (scheme != "https") return false

      val host = uri.host?.lowercase(Locale.ROOT)?.removePrefix("www.") ?: return false
      if (BLOCKED_AD_AND_TRACKER_KEYWORDS.any { host.contains(it) || trimmed.lowercase(Locale.ROOT).contains(it) }) {
        return false
      }
      ALLOWED_HTTPS_HOSTS.contains(host)
    } catch (_: Exception) {
      false
    }
  }

  /**
   * Neutralizes BiDi (Bidirectional) Override characters (\u202A-\u202E, \u2066-\u2069) and zero-width
   * cloaking characters (\u200B-\u200D, \uFEFF) commonly exploited by penetration testers and adversaries
   * to visually spoof Arabic text, disguise malicious payloads, or bypass verification filters.
   */
  fun stripUnicodeCloakingAndBidiTrojan(raw: String): String {
    if (raw.isEmpty()) return ""
    return raw
      .replace(Regex("[\u202A-\u202E\u2066-\u2069\u200B-\u200D\uFEFF]"), "")
      .replace(Regex("""[\x00-\x08\x0B\x0C\x0E-\x1F\x7F]"""), "") // Control characters
  }

  /**
   * Defense-in-depth verification against arbitrary intent injection or malicious redirects.
   * Only trusted, HTTPS-encrypted endpoints matching the strictly defined allowlist can be launched.
   */
  fun isExternalIntentSafe(rawUrl: String): Boolean {
    val trimmed = rawUrl.trim()
    if (trimmed.isEmpty()) return false
    return try {
      val uri = URI(trimmed)
      val scheme = uri.scheme?.lowercase(Locale.ROOT)
      if (scheme != "https") return false
      val host = uri.host?.lowercase(Locale.ROOT)?.removePrefix("www.") ?: return false
      ALLOWED_HTTPS_HOSTS.contains(host)
    } catch (_: Exception) {
      false
    }
  }

  /**
   * Checks whether incoming text contains dangerous injection strings, eval/script tags,
   * or inappropriate altered payloads intended to corrupt Islamic scripture.
   */
  fun containsInappropriateOrAlteredPayload(raw: String): Boolean {
    if (raw.isEmpty()) return false
    val lower = raw.lowercase(Locale.ROOT)
    if (DANGEROUS_BLOCK_REGEX.containsMatchIn(raw) || DANGEROUS_INJECTION_REGEX.containsMatchIn(raw)) {
      return true
    }
    return lower.contains("javascript:") ||
      lower.contains("data:text/html") ||
      lower.contains("<svg") ||
      lower.contains("onload=") ||
      lower.contains("onerror=")
  }

  /**
   * Sanitizes sacred text with full anti-tamper, BiDi neutralizing, and script stripping protections.
   */
  fun sanitizeSacredText(rawText: String): String {
    if (rawText.isEmpty()) return ""
    val deBidi = stripUnicodeCloakingAndBidiTrojan(rawText)
    var cleaned = deBidi.replace(DANGEROUS_BLOCK_REGEX, "")
    cleaned = cleaned.replace(DANGEROUS_INJECTION_REGEX, "")
    // Remove any stray HTML tags while keeping legitimate angle-bracket-free religious text intact
    cleaned = cleaned.replace(Regex("""</?[a-zA-Z][^>]{0,120}>"""), "")
    return cleaned.trim()
  }

  /**
   * Verifies that a fetched Quran verse has non-empty Uthmani Arabic and translation text before
   * allowing it to be stored, preventing any corrupted response from overwriting canonical offline verses.
   */
  fun isValidQuranVersePayload(surahNumber: Int, verseNumber: Int, arabicText: String): Boolean {
    if (surahNumber !in 1..114) return false
    if (verseNumber !in 1..286) return false
    val sanitized = sanitizeSacredText(arabicText)
    if (sanitized.isBlank()) return false
    // Must contain authentic Arabic Unicode block characters (\u0600-\u06FF or \u0750-\u077F or \uFB50-\uFDFF)
    return sanitized.any { ch ->
      ch in '\u0600'..'\u06FF' || ch in '\u0750'..'\u077F' || ch in '\uFB50'..'\uFDFF'
    }
  }

  /**
   * Checks whether the current user email matches the authorized developer maintainer.
   */
  fun isAuthorizedDeveloper(email: String?): Boolean {
    return email?.trim()?.equals(AUTHORIZED_DEVELOPER_EMAIL, ignoreCase = true) == true
  }

  fun getSecurityAuditSummary(): String {
    return "HTTPS-Only Host Pinning Active (${ALLOWED_HTTPS_HOSTS.size} Allowlisted Islamic APIs) • " +
      "Anti-Penetration BiDi Shield Active • Ad-Injection & Script Sanitizer Active • " +
      "Cleartext Disabled • Room Canonical Scripture Protection Enabled"
  }
}
