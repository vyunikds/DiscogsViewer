package com.example.feature.details.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.core.basepresentation.ui.FavoriteRed
import com.example.feature.details.R
import com.example.feature.details.state.ReleaseDetailsScreenState
import com.example.feature.details.state.ReleaseDetailsState

@Composable
fun ReleaseDetailsScreen(
    modifier: Modifier = Modifier,
    state: ReleaseDetailsScreenState,
    onToggleFavorite: (String, Boolean) -> Unit,
    onShare: () -> Unit,
    onRetry: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.hasError) {
            ErrorStateView(
                state.errorProvider,
                onRetry,
            )
        } else {
            DetailsContent(
                details = state.detailsState,
                onToggleFavorite = onToggleFavorite,
                onShare = onShare,
            )
        }
    }
}

@Composable
private fun ErrorStateView(
    errorProvider: (android.content.Context) -> String,
    onRetry: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = errorProvider(LocalContext.current),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.try_again))
        }
    }
}

@Composable
private fun DetailsContent(
    details: ReleaseDetailsState,
    onToggleFavorite: (String, Boolean) -> Unit,
    onShare: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        CoverImageSection(
            details = details,
            onToggleFavorite = onToggleFavorite,
            onShare = onShare,
        )
        DetailsInfoSection(details = details)
    }
}

@Composable
private fun CoverImageSection(
    details: ReleaseDetailsState,
    onToggleFavorite: (String, Boolean) -> Unit,
    onShare: () -> Unit,
) {
    Box {
        SubcomposeAsyncImage(
            model = details.coverImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(300.dp),
            loading = { CoverLoadingContent() },
            error = { CoverErrorContent() },
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.90f to MaterialTheme.colorScheme.background.copy(alpha = 0.99f),
                            0.95f to MaterialTheme.colorScheme.background,
                        ),
                    ),
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.BottomStart),
        ) {
            ReleaseReleaseTitleText(details.releaseTitle)
            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                ReleaseCommunityInfoText(details.have)
            }
        }
        Row(modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)) {
            IconButton(
                modifier =
                    Modifier
                        .padding(6.dp)
                        .background(color = Color.White.copy(alpha = 0.5f), shape = CircleShape),
                onClick = { onShare() },
            ) {
                Icon(
                    modifier = Modifier.size(48.dp).padding(6.dp),
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            FavoriteIconButton(
                isFavorite = details.isFavorite,
                onToggle = { onToggleFavorite(details.id, !details.isFavorite) },
            )
        }
    }
}

@Composable
private fun CoverLoadingContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.discogs),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.1f,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
        )
    }
}

@Composable
private fun CoverErrorContent() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.LightGray),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(R.string.image_not_available), color = Color.Gray, fontSize = 16.sp)
    }
}

@Composable
private fun ReleaseReleaseTitleText(title: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = title,
        fontSize = 32.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        style =
            MaterialTheme.typography.titleLarge.copy(
                shadow =
                    Shadow(
                        color = MaterialTheme.colorScheme.background,
                        offset = Offset.Zero,
                        blurRadius = 1.0f,
                    ),
            ),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
private fun ReleaseCommunityInfoText(count: Int) {
    Text(
        modifier = Modifier.padding(horizontal = 6.dp),
        text = stringResource(R.string.already_have, count.toString()),
        fontSize = 16.sp,
        fontWeight = FontWeight.Light,
        overflow = TextOverflow.Ellipsis,
        style =
            MaterialTheme.typography.titleSmall.copy(
                shadow =
                    Shadow(
                        color = MaterialTheme.colorScheme.background,
                        offset = Offset.Zero,
                        blurRadius = 1.0f,
                    ),
            ),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7F),
    )
}

@Composable
private fun FavoriteIconButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
) {
    IconButton(
        onClick = onToggle,
        modifier =
            Modifier
                .padding(6.dp)
                .background(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = CircleShape,
                ),
    ) {
        AnimatedContent(
            targetState = isFavorite,
            transitionSpec = {
                (fadeIn() + scaleIn(initialScale = 0.2f))
                    .togetherWith(fadeOut() + scaleOut(targetScale = 0.2f))
            },
            label = "favorite_icon_animation",
        ) { favorite ->
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector =
                    if (favorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                contentDescription = null,
                tint =
                    if (favorite) {
                        FavoriteRed
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
            )
        }
    }
}

@Composable
private fun DetailsInfoSection(details: ReleaseDetailsState) {
    Box {
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart),
        ) {
            InfoRow(
                label = R.string.label_artist_name,
                value = details.artistTitle,
            )
            GenresInfoRow(genres = details.genres)
            InfoRow(
                label = R.string.label_country,
                value = details.country,
            )
            InfoRow(
                label = R.string.label_release_id,
                value = details.id,
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: Int,
    value: String,
) {
    Row(
        modifier =
            Modifier
                .padding(
                    vertical = 8.dp,
                    horizontal = 16.dp,
                ),
    ) {
        Text(
            modifier = Modifier.width(120.dp),
            text = stringResource(label),
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun GenresInfoRow(genres: List<String>) {
    Row(
        modifier =
            Modifier
                .padding(
                    vertical = 8.dp,
                    horizontal = 16.dp,
                ),
    ) {
        Text(
            modifier = Modifier.width(120.dp),
            text = stringResource(R.string.label_genres),
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(modifier = Modifier.width(16.dp))
        genres.forEach { genre ->
            Text(
                text = genre,
                modifier =
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(8.dp),
                        ).padding(horizontal = 6.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondary,
            )
            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReleaseDetailsScreenPreview() {
    ReleaseDetailsScreen(
        state =
            ReleaseDetailsScreenState(
                isLoading = false,
                detailsState =
                    ReleaseDetailsState(
                        releaseTitle = "Abbey Roadewrwerwerfwerewr3223323232r",
                        artistTitle = "artistTitle saddffdsdfasdfasafsdafssfdfasadsfsasadfsadfsa333",
                        coverImage =
                            "https://i.discogs.com/5m6_bBtu4gBfLQSmEr80zoVNXvZG" +
                                "FB8Ld3ajU7_Vkoo/rs:fit/g:sm/q:40/h:150/w:150/" +
                                "czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt" +
                                "/YWdlcy9SLTE1OTk3/NjMyLTE2MDE2MTA1" +
                                "/NTYtMzQwNC5qcGVn.jpeg",
                        isFavorite = true,
                        country = "Russia",
                        genres = listOf("Rock", "pop", "rock"),
                    ),
            ),
        onToggleFavorite = { _, _ -> },
        onShare = { },
    )
}
