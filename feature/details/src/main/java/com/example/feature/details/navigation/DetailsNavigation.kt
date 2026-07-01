package com.example.feature.details.navigation

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feature.details.R
import com.example.feature.details.state.ReleaseDetailsScreenState
import com.example.feature.details.state.ReleaseDetailsViewModel
import com.example.feature.details.ui.ReleaseDetailsScreen

@Composable
fun DetailsScreenRoute(
    onBack: () -> Unit,
) {
    val viewModel: ReleaseDetailsViewModel = hiltViewModel()
    val state: ReleaseDetailsScreenState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    ) { innerPadding ->
        ReleaseDetailsScreen(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onToggleFavorite = viewModel::onToggleFavorite,
            onRetry = viewModel::retry,
            onShare = {
                val details = state.detailsState
                val shareText = "https://discogs.com/release/${details.id}"
                val shareTitle = context.getString(R.string.share_via)
                val messengerIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        addCategory(Intent.CATEGORY_APP_MESSAGING)
                    }
                } else {
                    null
                }
                val intent = if (messengerIntent != null
                    && context.packageManager.queryIntentActivities(messengerIntent, 0).isNotEmpty()
                ) {
                    Intent.createChooser(messengerIntent, shareTitle)
                } else {
                    val generalIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    Intent.createChooser(generalIntent, shareTitle)
                }
                context.startActivity(intent)
            },
        )
    }
}
