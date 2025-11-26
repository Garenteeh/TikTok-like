package com.example.tiktokapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun VideoActionButton(
    icon: ImageVector,
    text: String? = null,
    isActive: Boolean = false,
    activeColor: Color = Color.Red,
    inactiveColor: Color = Color.White,
    onClick: () -> Unit
) {
    var animationTrigger by remember { mutableStateOf(false) }

    // Animation de scale (rebond)
    val scale by animateFloatAsState(
        targetValue = if (animationTrigger) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = {
            if (animationTrigger) {
                animationTrigger = false
            }
        },
        label = "scale"
    )

    // Animation de la couleur
    val color by animateColorAsState(
        targetValue = if (isActive) activeColor else inactiveColor,
        animationSpec = tween(durationMillis = 200),
        label = "color"
    )

    Column(
        modifier = Modifier
            .clickable {
                animationTrigger = true
                onClick()
            }
            .padding(4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
        )
        if (!text.isNullOrEmpty()) {
            Text(
                text = text,
                color = Color.White,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
