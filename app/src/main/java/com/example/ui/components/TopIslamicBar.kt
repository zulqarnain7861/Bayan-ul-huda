package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.prayer.AppThemeMode
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ScheherazadeFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopIslamicBar(
  title: String = "Bayan al-Huda",
  arabicSubtitle: String = "بَيَانُ الهُدَى",
  themeMode: AppThemeMode = AppThemeMode.LIGHT,
  onCycleThemeMode: () -> Unit = {},
  onSearchClick: () -> Unit,
  onAboutClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  TopAppBar(
    modifier = modifier,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .background(EmeraldPrimary, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = MaterialTheme.colorScheme.primary
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = arabicSubtitle,
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = ScheherazadeFontFamily,
              color = GoldPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
          )
        }
      }
    },
    actions = {
      IconButton(
        onClick = onCycleThemeMode,
        modifier = Modifier.testTag("top_bar_theme_action")
      ) {
        val icon = when (themeMode) {
          AppThemeMode.LIGHT -> Icons.Default.LightMode
          AppThemeMode.EYE_COMFORT -> Icons.Default.RemoveRedEye
          AppThemeMode.DARK -> Icons.Default.DarkMode
          AppThemeMode.AUTO -> Icons.Default.BrightnessAuto
        }
        Icon(
          imageVector = icon,
          contentDescription = "Appearance: ${themeMode.label}",
          tint = GoldPrimary
        )
      }
      IconButton(
        onClick = onSearchClick,
        modifier = Modifier.testTag("top_bar_search_action")
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }
      IconButton(
        onClick = onAboutClick,
        modifier = Modifier.testTag("top_bar_about_action")
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "About & Diagnostics",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  )
}
