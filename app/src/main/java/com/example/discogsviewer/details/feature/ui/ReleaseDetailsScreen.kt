package com.example.discogsviewer.details.feature.ui

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.discogsviewer.R
import com.example.discogsviewer.details.feature.ReleaseDetailsScreenState
import com.example.discogsviewer.details.feature.ReleaseDetailsState
import com.example.discogsviewer.presentation.theme.FavoriteRed

@Composable
fun ReleaseDetailsScreen(
    modifier: Modifier = Modifier,
    state: ReleaseDetailsScreenState,
    onToggleFavorite: (String, Boolean) -> Unit,
    onShare: () -> Unit,
    onRetry: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.hasError) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.errorProvider(LocalContext.current),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text(stringResource(R.string.try_again))
                }
            }
        } else {
            val details = state.detailsState

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box {
                    SubcomposeAsyncImage(
                        model = details.coverImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        loading = {
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
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.image_not_available),
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        },
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    0f to Color.Transparent,
//                                    0.7f to MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                    0.90f to MaterialTheme.colorScheme.background.copy(alpha = 0.99f),
                                    0.95f to MaterialTheme.colorScheme.background,
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomStart),
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = details.releaseTitle,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(
                                shadow = Shadow(
                                    color = MaterialTheme.colorScheme.background,
                                    offset = Offset.Zero,
                                    blurRadius = 1.0f
                                )
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(
                            modifier = Modifier
//                                .align(Alignment.Start)
                                .padding(vertical = 12.dp),
                        ) {
                            Icon(
//                                modifier = Modifier.padding( 6.dp),
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                text = stringResource(R.string.already_have, details.have.toString()),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Light,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    shadow = Shadow(
                                        color = MaterialTheme.colorScheme.background,
                                        offset = Offset.Zero,
                                        blurRadius = 1.0f
                                    )
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7F),
                            )
                        }

                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                    ) {
                        IconButton(
                            modifier = Modifier
                                .padding(6.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.5f),
                                    shape = CircleShape
                                ),
                            onClick = { onShare() }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(6.dp),
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = {
                                onToggleFavorite(
                                    details.id.toString(),
                                    !details.isFavorite
                                )
                            },
                            modifier = Modifier
//                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                        ) {
                            AnimatedContent(
                                targetState = details.isFavorite,
                                transitionSpec = {
                                    (fadeIn() + scaleIn(initialScale = 0.2f))
                                        .togetherWith(fadeOut() + scaleOut(targetScale = 0.2f))
                                },
                                label = "favorite_icon_animation"
                            ) { isFavorite ->
                                Icon(
                                    modifier = Modifier.size(48.dp),
                                    imageVector = if (isFavorite) {
                                        Icons.Default.Favorite
                                    } else {
                                        Icons.Default.FavoriteBorder
                                    },
                                    contentDescription = null,
                                    tint = if (isFavorite) {
                                        FavoriteRed
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
                Box {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp,
                                )
                        ) {
                            Text(
                                modifier = Modifier.width(120.dp),
                                text = stringResource(R.string.label_artist_name),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Light,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = details.artistTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp,
                                )
                        ) {
                            Text(
                                modifier = Modifier.width(120.dp),
                                text = stringResource(R.string.label_genres),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Light,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            details.genres.forEach { genre ->
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
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }

                        Row(
                            modifier = Modifier
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp,
                                )
                        ) {
                            Text(
                                modifier = Modifier.width(120.dp),
                                text = stringResource(R.string.label_country),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Light,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = details.country,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp,
                                )
                        ) {
                            Text(
                                modifier = Modifier.width(120.dp),
                                text = stringResource(R.string.label_release_id),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Light,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = details.id.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    horizontalAlignment = Alignment.End
//                ) {
//                    Text(
//                        text = details.title,
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .align(Alignment.Start),
//                        style = MaterialTheme.typography.titleMedium,
//                        color = MaterialTheme.colorScheme.secondary
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Text(
//                        text = details.country,
//                        fontSize = 18.sp,
//                        modifier = Modifier.padding(vertical = 14.dp),
//                        style = MaterialTheme.typography.titleSmall,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReleaseDetailsScreenPreview() {
    ReleaseDetailsScreen(
        state = ReleaseDetailsScreenState(
            isLoading = false,
            detailsState = ReleaseDetailsState(
                releaseTitle = "Abbey Roadewrwerwerfwerewr3223323232r",
                artistTitle = "artistTitle saddffdsdfasdfasafsdafssfdfasadsfsasadfsadfsa333",
                coverImage = "https://i.discogs.com/5m6_bBtu4gBfLQSmEr80zoVNXvZGFB8Ld3ajU7_Vkoo/rs:fit/g:sm/q:40/h:150/w:150/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE1OTk3/NjMyLTE2MDE2MTA1/NTYtMzQwNC5qcGVn.jpeg",
                isFavorite = true,
                country = "Russia",
                genres = listOf("Rock", "pop", "rock"),
            )
        ),
        onToggleFavorite = { _, _ -> },
        onShare = { },
    )
}
