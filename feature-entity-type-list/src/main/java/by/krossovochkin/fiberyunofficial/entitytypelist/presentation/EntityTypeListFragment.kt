package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListParentComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.R
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitytypelist.DaggerEntityTypeListComponent
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.fragment_entity_type_list.*
import kotlinx.android.synthetic.main.item_entity_type.*
import javax.inject.Inject

class EntityTypeListFragment(
    private val entityTypeListParentComponent: EntityTypeListParentComponent
) : BaseFragment(R.layout.fragment_entity_type_list) {

    @Inject
    lateinit var viewModel: EntityTypeListViewModel
    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateLayoutContainer<EntityTypeListItem, ListItem>(
            layout = R.layout.item_entity_type
        ) {
            bind {
                itemView.setOnClickListener { viewModel.select(item) }
                entityTypeTitleTextView.text = item.title
                entityTypeBadgeView.setBackgroundColor(item.badgeBgColor)
            }
        }
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        DaggerEntityTypeListComponent.builder()
            .fragment(this)
            .entityTypeListGlobalDependencies(entityTypeListParentComponent)
            .build()
            .inject(this)

        entityTypeListRecyclerView.layoutManager = LinearLayoutManager(context)
        entityTypeListRecyclerView.adapter = adapter
        entityTypeListRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL)
        )

        viewModel.entityTypeItems.observe(this, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        initToolbar(
            toolbar = entityTypeListToolbar,
            title = context!!.getString(R.string.title_entity_type_list),
            bgColorInt = ColorUtils.getColor(context!!, R.attr.colorPrimary)
        )
    }

    interface ArgsProvider {

        fun getEntityTypeListArgs(arguments: Bundle): Args
    }

    data class Args(
        val fiberyAppData: FiberyAppData
    )

}





