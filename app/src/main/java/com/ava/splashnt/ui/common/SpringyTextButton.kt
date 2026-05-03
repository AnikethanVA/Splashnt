package com.ava.splashnt.ui.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SpringyTextButton(
    buttonText: String,
    containerColor: Color,
    textColor: Color,
    trailingIcon: ImageVector? = null,
    onClick: () -> Unit
) {

    val buttonInteractionSource = remember { MutableInteractionSource() }
    val isPressed by buttonInteractionSource.collectIsPressedAsState()

    val roundedCornerPercentage by animateIntAsState(
        targetValue = if (isPressed) 30 else 100
    )

    val buttonScale by animateFloatAsState(
        targetValue = if(isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
        )
    )

    TextButton(
        modifier = Modifier
            .scale(buttonScale),
        shape = RoundedCornerShape(percent = roundedCornerPercentage),
        colors = ButtonDefaults.textButtonColors(containerColor = containerColor),
        onClick = onClick,
        interactionSource = buttonInteractionSource,
        content = {
            Text(text = buttonText, color = textColor)
            if(trailingIcon != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = trailingIcon, contentDescription = buttonText, tint = textColor)
            }
        },
    )
}

@Preview(showSystemUi = true)
@Composable
private fun SpringyTextButtonPreview() {
    SpringyTextButton(
        buttonText = "Download",
        containerColor = MaterialTheme.colorScheme.background,
        textColor = MaterialTheme.colorScheme.onBackground,
        onClick = { }
    )
}