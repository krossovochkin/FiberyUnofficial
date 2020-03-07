package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.annotation.ColorInt
import androidx.annotation.MenuRes

data class EntityListToolbarViewState(
    val title: String,
    @ColorInt
    val bgColorInt: Int,
    @MenuRes
    val menuResId: Int?
)
