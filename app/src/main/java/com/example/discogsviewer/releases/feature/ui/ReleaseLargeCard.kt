package com.example.discogsviewer.releases.feature.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.discogsviewer.R
import com.example.discogsviewer.presentation.theme.DiscogsViewerTheme
import com.example.discogsviewer.presentation.theme.FavoriteRed
import com.example.discogsviewer.ui.common.ReleaseCardState

@Composable
fun ReleaseLargeCard(
    modifier: Modifier = Modifier,
    release: ReleaseCardState,
    onToggleFavorite: ((String, Boolean) -> Unit)? = null,
    onItemClicked: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onItemClicked(release.id) }
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(120.dp)
        ) {
            SubcomposeAsyncImage(
                model = release.thumb.ifBlank { release.coverImage.ifBlank { null } },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onPrimary),
                loading = {
                    Image(
                        painter = painterResource(R.drawable.discogs),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    )
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.discogs),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.padding(16.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                            )
                            Text(
                                text = stringResource(R.string.image_not_available),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = release.releaseTitle,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = release.artistTitle,
                fontWeight = FontWeight.Light,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                release.genre.forEach { genre ->
                    Text(
                        text = genre,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(8.dp),
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }

        IconButton(
            onClick = { onToggleFavorite?.invoke(release.id, !release.isFavorite) }
        ) {
            AnimatedContent(
                targetState = release.isFavorite,
                transitionSpec = {
                    (fadeIn() + scaleIn(initialScale = 0.2f))
                        .togetherWith(fadeOut() + scaleOut(targetScale = 0.2f))
                },
                label = "favorite_icon_animation"
            ) { isFavorite ->
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.add_favorite_desc),
                    tint = if (isFavorite) FavoriteRed else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewReleaseLargeCard() {
    DiscogsViewerTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Карточка с избранным
            ReleaseLargeCard(
                release = ReleaseCardState(
                    id = "1",
                    releaseTitle = "Random Access Memories",
                    artistTitle = "Daft Punk",
                    thumb = "https://img.discogs.com/...",
                    genre = listOf("Electronic", "House", "Disco"),
                    isFavorite = true,
                    country = "country",
                ),
                onToggleFavorite = { _, _ -> },
                onItemClicked = {}
            )

            // Карточка без избранного
            ReleaseLargeCard(
                release = ReleaseCardState(
                    id = "2",
                    releaseTitle = "OK Computer",
                    artistTitle = "Radiohead",
                    thumb = "https://img.discogs.com/...",
                    genre = listOf("Alternative Rock", "Art Rock"),
                    isFavorite = false,
                    country = "country",
                ),
                onToggleFavorite = { _, _ -> },
                onItemClicked = {}
            )

            // Карточка с длинным названием и множеством жанров
            ReleaseLargeCard(
                release = ReleaseCardState(
                    id = "3",
                    releaseTitle = "The Dark Side of the Moon (2011 Remastered Edition)",
                    artistTitle = "Pink Floyd",
                    thumb = "https://img.discogs.com/...",
                    genre = listOf(
                        "Progressive Rock",
                        "Psychedelic Rock",
                        "Experimental",
                        "Classic Rock"
                    ),
                    isFavorite = false,
                    country = "country",
                ),
                onToggleFavorite = { _, _ -> },
                onItemClicked = {}
            )
        }
    }
}