package by.krossovochkin.fiberyunofficial.entitylist.presentation

import android.view.View
import by.krossovochkin.fiberyunofficial.entitylist.databinding.ItemEntityBinding
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class EntityListItemViewHolder(
    view: View
) : RecyclerViewHolder<EntityListItem>(view) {
    val binding = ItemEntityBinding.bind(view)
}