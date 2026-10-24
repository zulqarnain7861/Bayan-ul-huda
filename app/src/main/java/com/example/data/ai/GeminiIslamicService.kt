package com.example.data.ai

import android.util.Log
import com.example.data.local.ExtendedHadithDataProvider
import com.example.data.local.entities.AyahEntity
import com.example.data.local.entities.HadithEntity
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiIslamicService"

sealed interface GeminiUiState {
  data object Idle : GeminiUiState
  data object Loading : GeminiUiState
  data class Streaming(val text: String) : GeminiUiState
  data class Success(val text: String) : GeminiUiState
  data class Error(val message: String) : GeminiUiState
}

class GeminiIslamicService {

  private val model by lazy {
    Firebase.ai.generativeModel("gemini-3.8-flash")
  }

  private val _uiState = MutableStateFlow<GeminiUiState>(GeminiUiState.Idle)
  val uiState: StateFlow<GeminiUiState> = _uiState.asStateFlow()

  fun resetState() {
    _uiState.value = GeminiUiState.Idle
  }

  suspend fun askScholarlyQuestionStream(
    userQuestion: String,
    traditionScope: String = "BOTH",
    matchedAyahs: List<AyahEntity> = emptyList(),
    matchedHadiths: List<HadithEntity> = emptyList()
  ) {
    val trimmed = userQuestion.trim()
    if (trimmed.isEmpty()) {
      _uiState.value = GeminiUiState.Error("Please enter a question or topic to ask Gemini.")
      return
    }

    _uiState.value = GeminiUiState.Loading
    val accumulatedText = StringBuilder()

    val contextSnippet = buildString {
      if (matchedAyahs.isNotEmpty()) {
        appendLine("Verified Local Quran Matches:")
        matchedAyahs.take(3).forEach { ayah ->
          appendLine("- Quran ${ayah.surahNumber}:${ayah.verseNumber}: ${ayah.textArabic} | EN: ${ayah.translationEnglish}")
        }
      }
      if (matchedHadiths.isNotEmpty()) {
        appendLine("Verified Local Hadith Matches:")
        matchedHadiths.take(4).forEach { h ->
          val tradition = if (ExtendedHadithDataProvider.isShiaCollection(h.collectionKey)) "Shia" else "Sunni"
          val grading = ExtendedHadithDataProvider.formatGradingMethodologyLabel(h)
          appendLine("- [$tradition] ${h.reference} ($grading): ${h.englishText.take(220)}")
        }
      }
    }

    val prompt = buildString {
      appendLine("You are Bayan al-Huda's respectful, evidence-based Islamic scholarly research assistant powered by Gemini.")
      appendLine("Tradition Scope Requested: $traditionScope (Wait on verified primary sources; clearly distinguish Sunni and Shia collections and grading methodologies; never use Darussalam labels for Shia sources).")
      if (contextSnippet.isNotBlank()) {
        appendLine("\nGrounded Reference Evidence from Installed App Database:")
        appendLine(contextSnippet)
      }
      appendLine("\nUser Question / Topic: $trimmed")
      appendLine("\nProvide a clear, well-structured response citing exact Quran Surah:Verse references and authentic Hadith references (in English, with key Arabic/Urdu terms where helpful).")
    }

    try {
      withTimeout(60_000L) {
        model.generateContentStream(prompt).collect { chunk ->
          val chunkText = chunk.text.orEmpty()
          accumulatedText.append(chunkText)
          _uiState.value = GeminiUiState.Streaming(accumulatedText.toString())
        }
      }
      if (accumulatedText.isNotBlank()) {
        _uiState.value = GeminiUiState.Success(accumulatedText.toString())
      } else {
        _uiState.value = GeminiUiState.Error("Model returned an empty response.")
      }
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "Gemini streaming timed out", e)
      _uiState.value = GeminiUiState.Error("Generation timed out after 60 seconds. Please try again.")
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Firebase AI streaming generation failed", e)
      val userFriendly = if (e.message?.contains("network", ignoreCase = true) == true ||
        e.message?.contains("unable to resolve", ignoreCase = true) == true ||
        e.message?.contains("offline", ignoreCase = true) == true
      ) {
        "Device appears offline. Local search across verified Quran (6,236 verses) and Sunni/Shia Hadith libraries remains fully active below."
      } else {
        "AI response unavailable: ${e.localizedMessage ?: "Unknown error"}. Local Quran & Hadith verified matches below remain fully accessible."
      }
      _uiState.value = GeminiUiState.Error(userFriendly)
    }
  }
}
