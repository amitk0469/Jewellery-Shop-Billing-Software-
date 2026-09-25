package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

fun Modifier.liquidGlassBackground(): Modifier = this.drawBehind {
    val width = size.width
    val height = size.height

    // Base background linear gradient 145deg
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(MeshBaseStart, MeshBaseEnd),
            start = Offset(0f, 0f),
            end = Offset(width, height)
        )
    )

    // Top-left gold radial glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(MeshGold, Color.Transparent),
            center = Offset(width * 0.05f, height * 0.05f),
            radius = width * 0.55f
        ),
        center = Offset(width * 0.05f, height * 0.05f),
        radius = width * 0.55f
    )

    // Top-right soft blue glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(MeshBlue, Color.Transparent),
            center = Offset(width * 0.95f, height * 0.18f),
            radius = width * 0.55f
        ),
        center = Offset(width * 0.95f, height * 0.18f),
        radius = width * 0.55f
    )

    // Bottom pink glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(MeshPink, Color.Transparent),
            center = Offset(width * 0.50f, height * 0.95f),
            radius = width * 0.65f
        ),
        center = Offset(width * 0.50f, height * 0.95f),
        radius = width * 0.65f
    )
}

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    padding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val glassBrush = Brush.linearGradient(
        colors = listOf(GlassCardBgStart, GlassCardBgEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = Color(0x1A1E283C),
                spotColor = Color(0x1F1E283C)
            )
            .clip(shape)
            .background(glassBrush)
            .border(width = 1.dp, color = GlassBorder, shape = shape)
            .padding(padding)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
fun GoldBadge(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 35.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = Color(0x40956B20))
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(GoldGradientStart, GoldGradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (text.length > 2) 13.sp else 16.sp
        )
    }
}

@Composable
fun GlassInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Decimal,
    isLargeFont: Boolean = false,
    textColor: Color = CharcoalDark,
    trailingText: String? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4E4E4E),
            letterSpacing = 0.4.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        val shape = RoundedCornerShape(14.dp)
        val borderColor = if (isFocused) GoldPrimary else Color(0x1A000000)
        val borderWidth = if (isFocused) 1.5.dp else 1.dp
        val bgColor = if (isFocused) Color(0xB8FFFFFF) else Color(0x8CFFFFFF)

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = textColor,
                fontSize = if (isLargeFont) 20.sp else 15.sp,
                fontWeight = if (isLargeFont) FontWeight.Bold else FontWeight.SemiBold
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            singleLine = true,
            cursorBrush = SolidColor(GoldDeep),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = if (isLargeFont) 54.dp else 48.dp)
                .onFocusChanged { isFocused = it.isFocused }
                .clip(shape)
                .background(bgColor)
                .border(borderWidth, borderColor, shape)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = GrayLight,
                                fontSize = if (isLargeFont) 18.sp else 14.sp
                            )
                        }
                        innerTextField()
                    }
                    if (trailingText != null) {
                        Text(
                            text = trailingText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrayText,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun GoldGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .shadow(
                elevation = 6.dp,
                shape = shape,
                spotColor = Color(0x40956B20),
                ambientColor = Color(0x26956B20)
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = if (enabled) listOf(Color(0xFFC9A052), Color(0xFF956C24))
                    else listOf(Color(0xFFD4C2A1), Color(0xFFB5A182))
                )
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun DarkActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 54.dp)
            .shadow(8.dp, shape, spotColor = Color(0x4D000000))
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF161616), Color(0xFF383838))
                )
            )
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            letterSpacing = 0.6.sp
        )
    }
}
