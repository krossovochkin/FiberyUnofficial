package com.krossovochkin.fiberyunofficial.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

private const val MOVE_DURATION_MS = 300
private const val FADE_DURATION_MS = 200

/**
 * Default drill-down transitions (master -> detail).
 *
 * Horizontal slide + fade, following Material shared X-axis motion:
 * - forward: new screen slides in from the right, old slides out to the left
 * - back: new screen slides in from the left, old slides out to the right
 */
fun AnimatedContentTransitionScope<*>.fiberyForwardTransition(): ContentTransform =
    (
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(MOVE_DURATION_MS)
        ) + fadeIn(animationSpec = tween(FADE_DURATION_MS))
        ) togetherWith
        (
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(MOVE_DURATION_MS)
            ) + fadeOut(animationSpec = tween(FADE_DURATION_MS))
            )

fun AnimatedContentTransitionScope<*>.fiberyPopTransition(): ContentTransform =
    (
        slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(MOVE_DURATION_MS)
        ) + fadeIn(animationSpec = tween(FADE_DURATION_MS))
        ) togetherWith
        (
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(MOVE_DURATION_MS)
            ) + fadeOut(animationSpec = tween(FADE_DURATION_MS))
            )

fun AnimatedContentTransitionScope<*>.fiberyPredictivePopTransition(): ContentTransform =
    fiberyPopTransition()

/**
 * Modal transitions (create form, entity/filter/sort pickers).
 *
 * Vertical slide, following Material shared Y-axis motion:
 * - forward: new screen slides up over the old one, old stays underneath
 * - back: top screen slides down, revealing the one underneath
 */
fun AnimatedContentTransitionScope<*>.fiberyModalForwardTransition(): ContentTransform =
    (
        slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(MOVE_DURATION_MS)
        ) + fadeIn(animationSpec = tween(FADE_DURATION_MS))
        ) togetherWith
        ExitTransition.KeepUntilTransitionsFinished

fun AnimatedContentTransitionScope<*>.fiberyModalPopTransition(): ContentTransform =
    EnterTransition.None togetherWith
        (
            slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(MOVE_DURATION_MS)
            ) + fadeOut(animationSpec = tween(FADE_DURATION_MS))
            )

fun AnimatedContentTransitionScope<*>.fiberyModalPredictivePopTransition(): ContentTransform =
    fiberyModalPopTransition()

/**
 * Crossfade for root replacements (e.g. Login <-> AppList) where
 * a directional slide would imply a false hierarchy.
 */
fun AnimatedContentTransitionScope<*>.fiberyFadeTransition(): ContentTransform =
    fadeIn(animationSpec = tween(FADE_DURATION_MS)) togetherWith
        fadeOut(animationSpec = tween(FADE_DURATION_MS))
