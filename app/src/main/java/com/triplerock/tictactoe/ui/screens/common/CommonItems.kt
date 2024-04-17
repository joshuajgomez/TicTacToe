package com.triplerock.tictactoe.ui.screens.common

import android.content.res.Configuration
import android.graphics.BlurMaskFilter
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.triplerock.tictactoe.R
import com.triplerock.tictactoe.data.sampleNames
import com.triplerock.tictactoe.data.sampleRoomNames
import com.triplerock.tictactoe.ui.theme.Red10
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme


@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.ArrowBack,
    onClick: () -> Unit = {},
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.padding(all = 5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = colorScheme.onSurface
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewCustomTextButtonDark() {
    TicBackground {
        CustomTextButton(modifier = Modifier.padding(20.dp))
    }
}

@Preview
@Composable
fun PreviewCustomTextButtonLight() {
    TicBackground {
        Row(modifier = Modifier.background(Red10)) {
        CustomTextButton(modifier = Modifier.padding(20.dp))
        }
    }
}

@Composable
fun CustomTextButton(
    modifier: Modifier = Modifier,
    text: String = "Click me",
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier

    ) {
        TextButton(
            onClick = onClick,
            modifier = modifier
                .padding(horizontal = 20.dp)
                .solidShadow2(offset = 2.dp, color = colorScheme.background)
                .height(50.dp)
                .border(2.dp, colorScheme.onBackground, shape = RoundedCornerShape(30.dp))
                .clip(RoundedCornerShape(30.dp))
                .background(colorScheme.onBackground)
        ) {
            Text(
                text = text,
                fontSize = 22.sp,
                color = colorScheme.background,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
fun Modifier.solidShadow(
    color: Color = colorScheme.onBackground,
    strokeColor: Color = colorScheme.background,
    offset: Dp = 2.dp,
    radius: Float = 75f,
) = then(
    drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            val paint2 = Paint()
            val frameworkPaint2 = paint2.asFrameworkPaint()
            frameworkPaint2.color = strokeColor.toArgb()

            frameworkPaint.color = color.toArgb()

            val leftPixel = (-offset).toPx()
            val topPixel = offset.toPx()
            val rightPixel = size.width + leftPixel
            val bottomPixel = size.height + topPixel

            canvas.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                paint = paint,
                radiusX = radius,
                radiusY = radius
            )
            canvas.drawRoundRect(
                left = leftPixel - 1,
                top = topPixel - 1,
                right = rightPixel + 1,
                bottom = bottomPixel + 1,
                paint = paint2,
                radiusX = radius,
                radiusY = radius
            )
        }
    }
)

@Composable
fun Modifier.solidShadow2(
    color: Color = colorScheme.background,
    strokeWidth: Float = 2f,
    strokeColor: Color = colorScheme.onBackground,
    offset: Dp = 2.dp,
    radius: Float = 75f,
) = then(
    drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = color.toArgb()

            val paint2 = Paint()
            val frameworkPaint2 = paint2.asFrameworkPaint()
            frameworkPaint2.color = strokeColor.toArgb()

            val leftPixel = (-offset).toPx()
            val topPixel = offset.toPx()
            val rightPixel = size.width + leftPixel
            val bottomPixel = size.height + topPixel



            canvas.drawRoundRect(
                left = leftPixel - strokeWidth,
                top = topPixel - strokeWidth,
                right = rightPixel + strokeWidth,
                bottom = bottomPixel + strokeWidth,
                paint = paint2,
                radiusX = radius,
                radiusY = radius
            )

            canvas.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                paint = paint,
                radiusX = radius,
                radiusY = radius
            )
        }
    }
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewLoadingDark() {
    TicBackground {
        Loading()
    }
}

@Preview
@Composable
fun PreviewLoadingLight() {
    TicBackground {
        Loading()
    }
}

@Composable
fun Loading(
    message: String = "Loading",
    color: Color = colorScheme.onBackground,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    ) {
        CircularProgressIndicator(color = color)
        Text(text = message, fontSize = 25.sp, color = color)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTitleBarDark() {
    TicSurface {
        TitleBar()
    }
}

@Preview
@Composable
fun PreviewTitleBarLight() {
    TicSurface {
        TitleBar()
    }
}

@Composable
fun TitleBar(
    title: String = "tic.tac.toe",
    onBackClick: () -> Unit = {},
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
    ) {
        val (iconRef, titleRef) = createRefs()
        CustomButton(
            onClick = onBackClick,
            modifier = Modifier.constrainAs(iconRef) {
                start.linkTo(parent.start, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )
        Text(
            text = title,
            fontSize = 35.sp,
            color = colorScheme.onSurface,
            modifier = Modifier.constrainAs(titleRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewNameTagsDark(onNameSelect: (name: String) -> Unit = {}) {
    TicSurface {
        Box(modifier = Modifier.padding(20.dp)) {
            NameTags()
        }
    }
}

@Preview
@Composable
fun PreviewNameTagsLight(onNameSelect: (name: String) -> Unit = {}) {
    TicSurface {
        Box(modifier = Modifier.padding(20.dp)) {
            NameTags()
        }
    }
}

@Preview
@Composable
fun PreviewRoomNameTags(onNameSelect: (name: String) -> Unit = {}) {
    TicSurface {
        RoomNameTags()
    }
}

@Composable
fun NameTags(
    names: List<String> = sampleNames,
    onNameSelect: (name: String) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(text = "choose another name", color = colorScheme.onBackground)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items = names) {
                NameTag(name = it) {
                    onNameSelect(it)
                }
            }
        }
    }
}

@Composable
fun RoomNameTags(
    names: List<String> = sampleRoomNames,
    onNameSelect: (name: String) -> Unit = {},
) {
    LazyColumn {
        items(items = names) {
            NameTag(name = it) {
                onNameSelect(it)
            }
        }
    }
}

@Composable
private fun NameTag(name: String, onClick: () -> Unit) {
    SuggestionChip(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = colorScheme.onBackground,
            labelColor = colorScheme.background,
        ),
        label = {
            Text(
                text = name,
                fontSize = 20.sp,
            )
        }
    )
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewCustomTextFieldDark() {
    TicTacToeTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colorScheme.background
        ) {
            CustomTextField(Modifier.padding(all = 20.dp))
        }
    }
}

@Preview()
@Composable
fun PreviewCustomTextFieldLight() {
    TicTacToeTheme {
        Surface(color = colorScheme.background) {
            CustomTextField(modifier = Modifier.padding(all = 20.dp))
        }
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String = "Heyy",
    onTextChanged: (text: String) -> Unit = {},
) {
    BasicTextField(
        modifier = modifier
            .background(
                color = colorScheme.background,
                shape = MaterialTheme.shapes.extraLarge,
            )
            .border(1.dp, color = colorScheme.onBackground, shape = RoundedCornerShape(30.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth(),
        value = text,
        onValueChange = {
            if ("\n" !in it) onTextChanged(it)
        },
        cursorBrush = SolidColor(colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = colorScheme.onBackground,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
    )
}

@Composable
fun TicSurface(content: @Composable () -> Unit) {
    TicTacToeTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = colorScheme.background,
            content = content
        )
    }
}

@Composable
fun TicBackground(content: @Composable () -> Unit) {
    TicTacToeTheme {
        Surface(
            color = colorScheme.background,
            content = content
        )
    }
}

@Composable
fun gradientBrush(): Brush {
    return Brush.verticalGradient(
        listOf(
            colorScheme.primaryContainer,
            colorScheme.surface,
        )
    )
}

@Composable
fun rainbowBrush(): Brush {
    return Brush.verticalGradient(
        listOf(
            Color.Red,
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Blue,
        )
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppLogoContainerDark() {
    TicSurface {
        XoMarqueeContainer()
    }
}

@Preview
@Composable
fun PreviewAppLogoContainerLight() {
    TicSurface {
        XoMarqueeContainer()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun XoMarqueeContainer() {
    Column {
        val count = 6
        val spacing = MarqueeSpacing.fractionOfContainer(0f)
        Row(modifier = Modifier.basicMarquee(spacing = spacing)) {
            for (i in 1..count) {
                XoIcon()
                XoIcon(Icons.Outlined.Circle)
            }
        }
        Row(modifier = Modifier.basicMarquee(spacing = spacing)) {
            for (i in 1..count) {
                XoIcon(Icons.Outlined.Circle)
                XoIcon()
            }
        }
        Row(modifier = Modifier.basicMarquee(spacing = spacing)) {
            for (i in 1..count) {
                XoIcon()
                XoIcon(Icons.Outlined.Circle)
            }
        }
    }
}

@Composable
fun XoIcon(icon: ImageVector = Icons.Default.Close) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(40.dp),
    )
}


fun Modifier.advancedShadow(
    color: Color = Color.Red,
    alpha: Float = 1f,
    cornersRadius: Dp = 0.dp,
    shadowBlurRadius: Dp = 1.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
) = drawBehind {

    val shadowColor = color.copy().toArgb()
    val transparentColor = color.copy(alpha = 0f).toArgb()

    drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowBlurRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
    }
}