package org.yarhooshmand.smartv3.utils

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope

@Composable fun currentLifecycleOwner(): LifecycleOwner = LocalLifecycleOwner.current
@Composable fun lifecycleScopeOrNull(): CoroutineScope? = LocalLifecycleOwner.current.lifecycleScope
