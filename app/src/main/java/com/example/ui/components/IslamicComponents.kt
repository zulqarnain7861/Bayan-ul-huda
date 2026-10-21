package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SahihBadgeColor
import com.example.ui.theme.ScheherazadeFontFamily

@Composable
fun BismillahHeader(
  modifier: Modifier = Modifier,
  showBorder: Boolean = true
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    shape = RoundedCornerShape(16.dp),
    border = if (showBorder) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary.copy(alpha = 0.5f))) else null
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 18.dp, horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontFamily = ScheherazadeFontFamily,
          fontSize = 28.sp,
          lineHeight = 42.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          textAlign = TextAlign.Center
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "In the name of Allah, the Entirely Merciful, the Especially Merciful",
        style = MaterialTheme.typography.bodySmall.copy(
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center
        )
      )
    }
  }
}

@Composable
fun AyahNumberBadge(
  verseNumber: Int,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(36.dp)
      .border(1.5.dp, GoldPrimary, RoundedCornerShape(8.dp))
      .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "$verseNumber",
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
    )
  }
}

@Composable
fun SahihGradeBadge(
  grade: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (grade.lowercase()) {
    "sahih" -> Pair(SahihBadgeColor.copy(alpha = 0.15f), SahihBadgeColor)
    "hasan" -> Pair(GoldPrimary.copy(alpha = 0.15f), GoldPrimary)
    else -> Pair(Color.Gray.copy(alpha = 0.15f), Color.Gray)
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(6.dp),
    color = bgColor
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(textColor)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = grade,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.SemiBold,
          color = textColor
        )
      )
    }
  }
}

@Composable
fun RevelationTypeBadge(
  type: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(6.dp),
    color = EmeraldContainer
  ) {
    Text(
      text = type,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = OnEmeraldContainer
      )
    )
  }
}
