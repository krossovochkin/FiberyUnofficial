package by.krossovochkin.fiberyunofficial.entitylist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitylist.EntityListComponentFactory
import by.krossovochkin.fiberyunofficial.entitylist.EntityListGlobalDependencies
import by.krossovochkin.fiberyunofficial.entitylist.R
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.fragment_entity_list.*
import kotlinx.android.synthetic.main.item_entity.*
import javax.inject.Inject

class EntityListFragment(
    private val entityListGlobalDependencies: EntityListGlobalDependencies
) : BaseFragment(R.layout.fragment_entity_list) {

    @Inject
    lateinit var viewModel: EntityListViewModel
    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateLayoutContainer<EntityListItem, ListItem>(
            layout = R.layout.item_entity
        ) {
            bind {
                itemView.setOnClickListener { viewModel.select(item) }
                entityTitleTextView.text = item.title
            }
        }
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        EntityListComponentFactory
            .create(
                fragment = this,
                entityListGlobalDependencies = entityListGlobalDependencies
            )
            .inject(this)

        entityListRecyclerView.layoutManager = LinearLayoutManager(context)
        entityListRecyclerView.adapter = adapter
        entityListRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        viewModel.entityTypeItems.observe(this, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        with(viewModel.toolbarViewState) {
            initToolbar(
                toolbar = entityListToolbar,
                title = title,
                bgColorInt = bgColorInt
            )
        }
    }
}
