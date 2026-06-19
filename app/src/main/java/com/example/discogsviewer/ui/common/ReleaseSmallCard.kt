package com.example.discogsviewer.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.discogsviewer.R
import com.example.discogsviewer.presentation.theme.FavoriteRed

enum class ReleaseSmallCardMode {
    FAVORITES,
    TOGGLE_FAVORITE,
}

@Composable
fun ReleaseSmallCard(
    modifier: Modifier = Modifier,
    release: ReleaseCardState,
    mode: ReleaseSmallCardMode,
    onRemoveFavorite: ((String) -> Unit)? = null,
    onToggleFavorite: ((String, Boolean) -> Unit)? = null,
    onItemClicked: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onItemClicked(release.id) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = release.thumb.ifBlank { release.coverImage.ifBlank { null } },
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = release.releaseTitle,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
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
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }

            when (mode) {
                ReleaseSmallCardMode.FAVORITES -> {
                    if (onRemoveFavorite != null) {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            onClick = { onRemoveFavorite(release.id) }
                        ) {
                            Icon(
                                modifier = Modifier.size(32.dp),
                                imageVector = Icons.Default.Favorite,
                                contentDescription = stringResource(R.string.remove_favorite_desc),
                                tint = FavoriteRed
                            )
                        }
                    }
                }

                ReleaseSmallCardMode.TOGGLE_FAVORITE -> {
                    if (onToggleFavorite != null) {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            onClick = { onToggleFavorite(release.id, !release.isFavorite) }
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
            }
        }
    }
}
