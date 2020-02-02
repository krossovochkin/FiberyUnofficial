package by.krossovochkin.fiberyunofficial.entitylist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitylist.DaggerEntityListComponent
import by.krossovochkin.fiberyunofficial.entitylist.EntityListParentComponent
import by.krossovochkin.fiberyunofficial.entitylist.R
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.fragment_entity_list.*
import kotlinx.android.synthetic.main.item_entity.*
import javax.inject.Inject

class EntityListFragment(
    private val entityListParentComponent: EntityListParentComponent
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

        DaggerEntityListComponent.builder()
            .fragment(this)
            .entityListGlobalDependencies(entityListParentComponent)
            .build()
            .inject(this)

        entityListRecyclerView.layoutManager = LinearLayoutManager(context)
        entityListRecyclerView.adapter = adapter
        entityListRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        viewModel.entityTypeItems.observe(viewLifecycleOwner, Observer {
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

    interface ArgsProvider {

        fun getEntityListArgs(arguments: Bundle): Args
    }

    data class Args(
        val entityTypeSchema: FiberyEntityTypeSchema,
        val entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    )
}
