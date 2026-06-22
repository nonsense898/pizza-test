package com.cpunks.pizzacatalog.feature.catalog

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.crossfade
import com.cpunks.pizzacatalog.core.ui.theme.BackgroundBeige
import com.cpunks.pizzacatalog.core.ui.theme.DividerBeige
import com.cpunks.pizzacatalog.core.ui.theme.PizzaOrange
import com.cpunks.pizzacatalog.core.ui.theme.PizzaTheme
import com.cpunks.pizzacatalog.core.ui.theme.SurfaceWhite
import com.cpunks.pizzacatalog.core.ui.theme.TextDark
import com.cpunks.pizzacatalog.core.ui.theme.TextLight
import com.cpunks.pizzacatalog.core.ui.theme.TextMedium
import com.cpunks.pizzacatalog.domain.model.Pizza
import com.cpunks.pizzacatalog.domain.model.PizzaVariant

private fun pizzaDiameter(size: String): Dp = when (size) {
    "S" -> 140.dp
    "M" -> 220.dp
    "L" -> 340.dp
    else -> 220.dp
}

@Composable
fun CatalogScreen(
    onBack: () -> Unit = {},
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.error != null && state.pizzas.isEmpty()) {
        ErrorScreen(message = state.error ?: "", onRetry = viewModel::retry)
        return
    }

    CatalogContent(
        pizzas = state.pizzas,
        selectedSizes = state.selectedSizes,
        quantities = state.quantities,
        imagesReady = state.imagesReady,
        onBack = onBack,
        onSelectSize = viewModel::selectSize,
        onIncrement = viewModel::increment,
        onDecrement = viewModel::decrement,
    )
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(PizzaOrange)
                    .clickable { onRetry() }
                    .padding(horizontal = 32.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Retry",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SurfaceWhite
                )
            }
        }
    }
}

@Composable
private fun CatalogContent(
    pizzas: List<Pizza>,
    selectedSizes: Map<String, String>,
    quantities: Map<String, Int>,
    imagesReady: Boolean = true,
    onBack: () -> Unit,
    onSelectSize: (String, String) -> Unit,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    pizzaImage: @Composable (pizza: Pizza, isCurrent: Boolean, size: Dp) -> Unit = { pizza, _, sz ->
        val szVal = sz.value
        val visualScale = if (szVal < 220f) {
            0.75f + (szVal - 140f) / (220f - 140f) * (1f - 0.75f)
        } else {
            1f + (szVal - 220f) / (340f - 220f) * (1.35f - 1f)
        }
        val context = androidx.compose.ui.platform.LocalContext.current
        val imageRequest = androidx.compose.runtime.remember(pizza.imageUrl) {
            coil3.request.ImageRequest.Builder(context)
                .data(pizza.imageUrl)
                .size(coil3.size.Size.ORIGINAL)
                .crossfade(true)
                .build()
        }
        AsyncImage(
            model = imageRequest,
            contentDescription = pizza.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(sz)
                .graphicsLayer {
                    scaleX = visualScale
                    scaleY = visualScale
                }
                .clip(CircleShape)
        )
    },
) {

    val waveProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        waveProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    }

    val hasData = pizzas.isNotEmpty()

    val isWaveFinished = waveProgress.value >= 0.99f
    val revealed = imagesReady && isWaveFinished
    val contentAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "contentAlpha"
    )

    val imageReveal by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 200f),
        label = "imageReveal"
    )

    val pagerState = rememberPagerState { pizzas.size }
    val currentPizza = pizzas.getOrNull(pagerState.currentPage)
    val selectedSize = currentPizza?.let { selectedSizes[it.id] ?: it.defaultSize } ?: "M"
    val quantity = currentPizza?.let { quantities[it.id] } ?: 1
    val price = currentPizza?.let { cp ->
        cp.variants.firstOrNull { it.size == selectedSize }?.price
            ?: cp.variants.firstOrNull()?.price
    } ?: 0.0

    val targetDiameter = pizzaDiameter(selectedSize)
    val animDiameter by animateDpAsState(
        targetValue = targetDiameter,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "pizzaDiameter"
    )

    var liked by remember { mutableStateOf(false) }
    var isZoomed by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundBeige)
        ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(76.dp)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .graphicsLayer { alpha = contentAlpha },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentPizza != null) {
                CircleIconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.i_back),
                        contentDescription = stringResource(id = R.string.back),
                        tint = Color.Unspecified
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.pizzas),
                        fontSize = 11.sp,
                        color = Color(0x000000).copy(.7f),
                        letterSpacing = 0.5.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-16).dp)
                    )
                    AnimatedContent(
                        targetState = currentPizza.name,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                        },
                        label = "pizzaNameAnimation"
                    ) { targetName ->
                        Text(
                            targetName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }
                CircleIconButton(onClick = { liked = !liked }) {
                    Icon(
                        painter = painterResource(
                            id = if (liked) R.drawable.ic_liked_heart else R.drawable.ic_default_heart
                        ),
                        contentDescription = stringResource(id = R.string.like),
                        tint = if (liked) PizzaOrange else Color.Unspecified
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .graphicsLayer { alpha = contentAlpha },
            contentAlignment = Alignment.Center
        ) {
            if (hasData) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 100.dp),
                    pageSpacing = 0.dp,
                    verticalAlignment = Alignment.CenterVertically
                ) { page ->
                    val pizza = pizzas[page]
                    val isCurrent = page == pagerState.currentPage

                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                    val pageOffsetSign = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                    val fraction = pageOffset.coerceIn(0f, 1f)
                    val pageSelectedSize = selectedSizes[pizza.id] ?: pizza.defaultSize
                    val pageTargetDiameter = pizzaDiameter(pageSelectedSize)
                    val centerSize = if (page == pagerState.currentPage) {
                        lerp(animDiameter, pageTargetDiameter, fraction)
                    } else {
                        pageTargetDiameter
                    }
                    val sizeDp = lerp(centerSize, 90.dp, fraction)
                    val rotation = pageOffsetSign * -120f

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.graphicsLayer {
                                rotationZ = rotation

                                val s = 0.6f + 0.4f * imageReveal
                                scaleX = s
                                scaleY = s
                                alpha = imageReveal
                            }
                        ) {
                            pizzaImage(pizza, isCurrent, sizeDp)
                        }
                        if (isCurrent) {
                            val zoomAlpha = (1f - fraction * 2f).coerceIn(0f, 1f)
                            if (zoomAlpha > 0f) {
                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .graphicsLayer { alpha = zoomAlpha }
                                        .clickable { isZoomed = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_zoom),
                                        contentDescription = stringResource(id = R.string.zoom),
                                        modifier = Modifier.size(88.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (currentPizza != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val w = size.width
                            val h = size.height
                            val offsetY = h * (1f - waveProgress.value)

                            val buttonDiameter = 56.dp.toPx()
                            val buttonRadius = buttonDiameter / 2f
                            val topPadding = 16.dp.toPx()
                            val horizontalPadding = 36.dp.toPx()
                            val maxDrop = 30.dp.toPx()

                            val ySideCenter = topPadding + buttonRadius
                            val yMidCenter = ySideCenter + maxDrop
                            val xSideCenter = horizontalPadding + buttonRadius

                            val A = (4f * xSideCenter * (w - xSideCenter)) / (w * w)
                            val yEdge = (ySideCenter - A * yMidCenter) / (1f - A)
                            val yControl = 2f * yMidCenter - yEdge

                            val path = Path().apply {
                                moveTo(0f, yEdge + offsetY)
                                quadraticTo(
                                    x1 = w / 2f, y1 = yControl + offsetY,
                                    x2 = w, y2 = yEdge + offsetY
                                )
                                lineTo(w, h)
                                lineTo(0f, h)
                                close()
                            }
                            drawPath(path, androidx.compose.ui.graphics.Color.White)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = contentAlpha },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        SizeSelector(
                            sizes = currentPizza.variants.map { it.size },
                            selectedSize = selectedSize,
                            onSelect = { onSelectSize(currentPizza.id, it) }
                        )

                        Spacer(Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            AnimatedContent(
                                targetState = currentPizza.description,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                                },
                                modifier = Modifier.fillMaxSize(),
                                label = "pizzaDescriptionAnimation"
                            ) { targetDesc ->
                                Text(
                                    text = targetDesc,
                                    fontSize = 14.sp,
                                    color = TextMedium,
                                    lineHeight = 20.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        BottomBar(
                            quantity = quantity,
                            price = price,
                            onIncrement = { onIncrement(currentPizza.id) },
                            onDecrement = { onDecrement(currentPizza.id) },
                            onAdd = {}
                        )

                        Spacer(Modifier.navigationBarsPadding().height(16.dp))
                    }
                }
            }
        }
    }

        AnimatedVisibility(
            visible = isZoomed && currentPizza != null,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            if (currentPizza != null) {
                var scale by remember { mutableStateOf(3f) }
                var offset by remember { mutableStateOf(Offset.Zero) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .clipToBounds(),
                    contentAlignment = Alignment.Center
                ) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val imageRequest = androidx.compose.runtime.remember(currentPizza.imageUrl) {
                        coil3.request.ImageRequest.Builder(context)
                            .data(currentPizza.imageUrl)
                            .size(coil3.size.Size.ORIGINAL)
                            .build()
                    }
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = currentPizza.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { isZoomed = false },
                                    onDoubleTap = {
                                        if (scale > 1f) {
                                            scale = 1f
                                            offset = Offset.Zero
                                        } else {
                                            scale = 3f
                                        }
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    val newScale = (scale * zoom).coerceIn(1f, 5f)
                                    scale = newScale
                                    if (newScale > 1f) {
                                        val maxOffsetX = (size.width * (newScale - 1f)) / 2f
                                        val maxOffsetY = (size.height * (newScale - 1f)) / 2f
                                        offset = Offset(
                                            x = (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                                            y = (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                                        )
                                    } else {
                                        offset = Offset.Zero
                                    }
                                }
                            }
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun SizeSelector(
    sizes: List<String>,
    selectedSize: String,
    onSelect: (String) -> Unit
) {

    val maxDrop = 30.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 36.dp, end = 36.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            sizes.forEachIndexed { i, size ->

                val t = if (sizes.size > 1) i.toFloat() / (sizes.size - 1) else 0.5f
                val drop = maxDrop * (1f - (2f * t - 1f) * (2f * t - 1f))
                SizeButton(
                    size = size,
                    selected = size == selectedSize,
                    modifier = Modifier.padding(top = drop)
                ) { onSelect(size) }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_banana),
                contentDescription = stringResource(id = R.string.banana_for_scale),
                modifier = Modifier
                    .size(88.dp)
            )
        }
    }
}

@Composable
private fun SizeButton(
    size: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val diameter = 56.dp
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) Color.Black else Color.White,
        animationSpec = tween(durationMillis = 250),
        label = "sizeButtonBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color.Black,
        animationSpec = tween(durationMillis = 250),
        label = "sizeButtonText"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (selected) 3.dp else 0.dp,
        animationSpec = tween(durationMillis = 250),
        label = "sizeButtonBorder"
    )

    Box(
        modifier = modifier
            .size(diameter)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .background(backgroundColor, CircleShape)
            .border(borderWidth, Color.White, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = size,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun CircleIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .background(SurfaceWhite)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun BottomBar(
    quantity: Int,
    price: Double,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(colorResource(id = R.color.bottom_bar_qty_bg), RoundedCornerShape(50))
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .background(Color.White)
                    .clickable { onDecrement() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.minus),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Text(
                text = "$quantity",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 24.dp)
            )

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .background(Color.White)
                    .clickable { onIncrement() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.plus),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        AnimatedContent(
            targetState = price * quantity,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
            },
            label = "priceAnimation",
            modifier = Modifier.weight(1f)
        ) { targetPrice ->
            Text(
                text = stringResource(id = R.string.price_format, targetPrice),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Box(
            modifier = Modifier
                .height(42.dp)
                .clip(RoundedCornerShape(50))
                .background(colorResource(id = R.color.teal_button))
                .clickable { onAdd() }
                .padding(horizontal = 22.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.add),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SurfaceWhite,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
