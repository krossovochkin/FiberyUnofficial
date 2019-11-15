package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListComponentFactory
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListGlobalDependencies
import by.krossovochkin.fiberyunofficial.entitytypelist.R
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.fragment_entity_type_list.*
import kotlinx.android.synthetic.main.item_entity_type.*
import javax.inject.Inject

class EntityTypeListFragment(
    private val entityTypeListGlobalDependencies: EntityTypeListGlobalDependencies
) : BaseFragment(R.layout.fragment_entity_type_list) {

    @Inject
    lateinit var viewModel: EntityTypeListViewModel
//    private val args: EntityTypeListFragmentArgs by navArgs()
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

        EntityTypeListComponentFactory
            .create(
                fragment = this,
                entityTypeListGlobalDependencies = entityTypeListGlobalDependencies
            )
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
}





