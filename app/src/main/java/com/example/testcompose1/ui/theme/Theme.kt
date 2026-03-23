package com.example.testcompose1.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 定义自定义颜色方案 .创建两个颜色方案：浅色主题和深色主题。

// 浅色主题的颜色方案
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),          // App主色调。用于关键交互元素（选中状态、按钮、下划线）
    secondary = Color(0xFF03DAC6),        // 次色调。次要强调色，用于次要按钮、图标等
    tertiary = Color(0xFF3700B3),         // 第三强调色（补充色），用于特殊强调的元素
    background = Color(0xFFFFFBFE),       // App 页面的整体背景色
    surface = Color(0xFFFFFBFE),          // 控件 / 卡片的背景色（比如 TextField、Card、按钮的背景）
    onPrimary = Color.White,                     // 显示在 primary 颜色上的文字 / 图标色（保证对比度）
    onSecondary = Color.Black,                   // 显示在 secondary 颜色上的文字 / 图标色
    onBackground = Color(0xFF1C1B1F),     // 显示在 background 上的文字 / 图标色
    onSurface = Color(0xFF1C1B1F),        // 显示在 surface 上的文字 / 图标色
    // 可以继续自定义其他颜色
)

// 深色主题的颜色方案
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF3700B3),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF2C2B2F),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

// 字体样式
val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // 可以继续自定义其他样式
)

// 形状
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// 创建自定义主题包装函数
// 这个函数会判断当前是否为深色模式（通过系统设置或用户手动选择），然后应用对应的颜色方案、字体和形状。
// 我们还会加入一个状态来控制用户的手动深色模式选择。
@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // 默认跟随系统
    dynamicColor: Boolean = false,               // 是否启用动态颜色（Android 12+）
    content: @Composable () -> Unit
) {
    // 根据暗色标志选择颜色方案
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // 动态颜色（基于壁纸）
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

// 自动生成的。
@Composable
fun TestCompose1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(), // 是否深色主题
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true, //动态取色
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // 统一应用所有 Material Design 组件的样式
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // 排版配置.管理App中文字样式
        content = content
    )
}