package com.example.ui.quran

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.data.local.entities.AyahEntity
import com.example.data.local.entities.SurahEntity
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TajweedPracticeDialog(
  ayah: AyahEntity,
  surah: SurahEntity,
  cleanArabicText: String,
  currentReciterName: String,
  onPlayAuthenticReciter: () -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val recorder = remember { TajweedVoiceRecorder(context) }
  val recordingState by recorder.state.collectAsState()

  DisposableEffect(Unit) {
    onDispose {
      recorder.stopListening()
    }
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      recorder.startListening(
        surah = ayah.surahNumber,
        ayah = ayah.verseNumber,
        canonicalVerseText = cleanArabicText
      )
    } else {
      Toast.makeText(
        context,
        "Microphone permission is required to analyze your recitation.",
        Toast.LENGTH_LONG
      ).show()
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 16.dp)
        .testTag("tajweed_practice_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = EmeraldPrimary.copy(alpha = 0.14f),
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Tajweed Recitation Practice",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "${surah.nameEnglish} • Ayah ${ayah.verseNumber} (${ayah.surahNumber}:${ayah.verseNumber})",
                style = MaterialTheme.typography.labelSmall.copy(color = GoldPrimary, fontWeight = FontWeight.SemiBold)
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sacred Canonical Ayah Text & Word Evaluation Display
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
          border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            val eval = recordingState.evaluationResult

            // Always render word-by-word using the canonical text as the base
            // to ensure layout stability and zero jumping during recitation analysis.
            val canonicalWords = remember(cleanArabicText) {
              cleanArabicText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
            }
            
            val displayWordStatuses = if (eval != null) {
              eval.wordStatuses
            } else {
              canonicalWords.mapIndexed { index, word ->
                TajweedWordStatus(index = index, canonicalWord = word, grade = TajweedWordGrade.PENDING)
              }
            }

            FlowRow(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("tajweed_word_display_flow"),
              horizontalArrangement = Arrangement.End,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              displayWordStatuses.forEach { wordStatus ->
                val bgColor = when (wordStatus.grade) {
                  TajweedWordGrade.CORRECT -> Color(0xFFE8F5E9)
                  TajweedWordGrade.SMALL_MISTAKE -> Color(0xFFFFF9C4)
                  TajweedWordGrade.WRONG_OR_MISSED -> Color(0xFFFFEBEE)
                  TajweedWordGrade.PENDING -> MaterialTheme.colorScheme.surface
                }
                val borderColor = when (wordStatus.grade) {
                  TajweedWordGrade.CORRECT -> Color(0xFF2E7D32)
                  TajweedWordGrade.SMALL_MISTAKE -> Color(0xFFFBC02D)
                  TajweedWordGrade.WRONG_OR_MISSED -> Color(0xFFD32F2F)
                  TajweedWordGrade.PENDING -> MaterialTheme.colorScheme.outlineVariant
                }
                val textColor = when (wordStatus.grade) {
                  TajweedWordGrade.CORRECT -> Color(0xFF1B5E20)
                  TajweedWordGrade.SMALL_MISTAKE -> Color(0xFFE65100)
                  TajweedWordGrade.WRONG_OR_MISSED -> Color(0xFFD32F2F)
                  TajweedWordGrade.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                val label = when (wordStatus.grade) {
                  TajweedWordGrade.CORRECT -> "Correct"
                  TajweedWordGrade.SMALL_MISTAKE -> "Small Mistake"
                  TajweedWordGrade.WRONG_OR_MISSED -> "Wrong / Missed"
                  TajweedWordGrade.PENDING -> "Waiting..."
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = bgColor,
                  border = BorderStroke(1.5.dp, borderColor),
                  modifier = Modifier
                    .padding(horizontal = 3.dp, vertical = 2.dp)
                    .testTag("tajweed_word_${wordStatus.index}_${wordStatus.grade.name.lowercase()}")
                ) {
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text(
                      text = wordStatus.canonicalWord,
                      fontFamily = AmiriFontFamily,
                      fontSize = 22.sp,
                      fontWeight = if (wordStatus.grade == TajweedWordGrade.CORRECT) FontWeight.Normal else FontWeight.Bold,
                      color = textColor,
                      textAlign = TextAlign.Center
                    )
                    Text(
                      text = label,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = textColor
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Canonical Uthmani Arabic • Zero alteration to sacred text",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Evaluation Summary & Accuracy Badge (if available)
        recordingState.evaluationResult?.let { eval ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
              eval.wrongOrMissedCount == 0 && eval.smallMistakeCount == 0 -> EmeraldPrimary.copy(alpha = 0.12f)
              eval.wrongOrMissedCount == 0 -> Color(0xFFFFF9C4).copy(alpha = 0.5f)
              else -> Color(0xFFFFEBEE)
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("tajweed_evaluation_summary_card")
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
            ) {
              Text(
                text = "Recitation Accuracy: ${eval.accuracyPercentage}% (${eval.matchedCount}/${eval.totalCount} Correct)",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = when {
                    eval.accuracyPercentage >= 80 -> EmeraldPrimary
                    eval.accuracyPercentage >= 50 -> Color(0xFFE65100)
                    else -> Color(0xFFD32F2F)
                  }
                )
              )
              Text(
                text = eval.feedbackSummary,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurface)
              )

              Spacer(modifier = Modifier.height(8.dp))

              // 3-Color Legend: Green (Correct), Yellow (Small Mistake), Red (Wrong of text)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFE8F5E9),
                  border = BorderStroke(1.dp, Color(0xFF2E7D32))
                ) {
                  Text(
                    text = "● ${eval.matchedCount} Correct",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFFFF9C4),
                  border = BorderStroke(1.dp, Color(0xFFFBC02D))
                ) {
                  Text(
                    text = "● ${eval.smallMistakeCount} Small Mistake",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFFFEBEE),
                  border = BorderStroke(1.dp, Color(0xFFD32F2F))
                ) {
                  Text(
                    text = "● ${eval.wrongOrMissedCount} Wrong",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        // Active Listening or Error Status Banner
        if (recordingState.isListening) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GoldPrimary.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              CircularProgressIndicator(modifier = Modifier.size(18.dp), color = GoldPrimary, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Listening... Recite the Ayah aloud in Arabic now.",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
              )
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
        } else if (recordingState.errorMessage != null) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = recordingState.errorMessage ?: "",
              modifier = Modifier.padding(10.dp),
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error)
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        // Recording & Playback Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (recordingState.isListening) {
            Button(
              onClick = { recorder.stopListening() },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("tajweed_stop_recording_button")
            ) {
              Icon(Icons.Default.MicOff, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Stop & Check", fontWeight = FontWeight.Bold)
            }
          } else {
            Button(
              onClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                  context,
                  Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                  recorder.startListening(
                    surah = ayah.surahNumber,
                    ayah = ayah.verseNumber,
                    canonicalVerseText = cleanArabicText
                  )
                } else {
                  permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("tajweed_record_button")
            ) {
              Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (recordingState.evaluationResult != null) "Re-Record Recitation" else "Record Recitation",
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Listen to Authentic Human Reciter Button (Strictly No AI voice)
          OutlinedButton(
            onClick = {
              onPlayAuthenticReciter()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("tajweed_listen_reciter_button")
          ) {
            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Listen: $currentReciterName",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Strict Authenticity & Privacy Notice
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "• Zero AI-Generated Voice: Recitation is played strictly by verified human reciters ($currentReciterName).\n" +
              "• Words missed or mispronounced are highlighted in RED.\n" +
              "• Zero alteration to original Quranic Arabic text.",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 9.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }
  }
}
