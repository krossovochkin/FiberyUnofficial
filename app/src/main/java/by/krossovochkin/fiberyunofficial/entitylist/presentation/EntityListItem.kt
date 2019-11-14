package by.krossovochkin.fiberyunofficial.entitylist.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem

data class EntityListItem(
    val entityData: FiberyEntityData,
    val title: String
) : ListItem
