package org.yarhooshmand.smartv3.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope

@Composable
fun lifecycleScopeOrNull() = try {
    LocalLifecycleOwner.current.lifecycleScope
} catch (e: Exception) {
    null
}
