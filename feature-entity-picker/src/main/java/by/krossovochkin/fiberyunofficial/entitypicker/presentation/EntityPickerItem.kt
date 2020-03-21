package by.krossovochkin.fiberyunofficial.entitypicker.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem

data class EntityPickerItem(
    val entityData: FiberyEntityData,
    val title: String
) : ListItem
