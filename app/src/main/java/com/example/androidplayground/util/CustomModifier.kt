package com.example.androidplayground.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role

/**
 * Created by JeckOnly on 2023/6/14
 * Describe:
 */

fun Modifier.noIndicationClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = composed {
    this.clickable(
        interactionSource = remember {
            MutableInteractionSource()
        },
        indication = null,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick
    )
}