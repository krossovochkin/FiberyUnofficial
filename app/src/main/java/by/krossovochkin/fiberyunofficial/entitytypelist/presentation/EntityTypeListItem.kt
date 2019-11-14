package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import androidx.annotation.ColorInt
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem

data class EntityTypeListItem(
    val entityTypeData: FiberyEntityTypeSchema,
    val title: String,
    @ColorInt
    val badgeBgColor: Int
) : ListItem
