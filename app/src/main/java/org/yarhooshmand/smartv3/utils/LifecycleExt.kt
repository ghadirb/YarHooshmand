package org.yarhooshmand.smartv3.utils

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope

/**
 * دسترسی امن به LifecycleOwner در Compose
 */
@Composable
fun currentLifecycleOwner(): LifecycleOwner = LocalLifecycleOwner.current

/**
 * گرفتن lifecycleScope بدون try/catch دور Composable (که باعث خطای Compose می‌شد)
 */
@Composable
fun lifecycleScopeOrNull(): CoroutineScope? =
    LocalLifecycleOwner.current.lifecycleScope
