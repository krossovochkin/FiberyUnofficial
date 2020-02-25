package by.krossovochkin.fiberyunofficial.entitylist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitylist.DaggerEntityListComponent
import by.krossovochkin.fiberyunofficial.entitylist.EntityListParentComponent
import by.krossovochkin.fiberyunofficial.entitylist.R
import by.krossovochkin.fiberyunofficial.entitylist.databinding.FragmentEntityListBinding
import by.krossovochkin.fiberyunofficial.entitylist.databinding.ItemEntityBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.hannesdorfmann.adapterdelegates4.paging.PagedListDelegationAdapter
import javax.inject.Inject

class EntityListFragment(
    private val entityListParentComponent: EntityListParentComponent
) : BaseFragment(R.layout.fragment_entity_list) {

    @Inject
    lateinit var viewModel: EntityListViewModel

    private val binding by viewBinding(FragmentEntityListBinding::bind)

    private val adapter = PagedListDelegationAdapter(
        object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return if (oldItem is EntityListItem && newItem is EntityListItem) {
                    oldItem.entityData.id == newItem.entityData.id
                } else {
                    oldItem === newItem
                }
            }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return oldItem.equals(newItem)
            }

        },
        adapterDelegateLayoutContainer<EntityListItem, ListItem>(
            layout = R.layout.item_entity
        ) {
            val binding = ItemEntityBinding.bind(this.itemView)
            bind {
                itemView.setOnClickListener { viewModel.select(item) }
                binding.entityTitleTextView.text = item.title
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

        binding.entityListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.entityListRecyclerView.adapter = adapter
        binding.entityListRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        viewModel.entityTypeItems.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { error ->
                Snackbar
                    .make(
                        requireView(),
                        error.message ?: getString(R.string.unknown_error),
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            }
        })

        with(viewModel.toolbarViewState) {
            initToolbar(
                toolbar = binding.entityListToolbar,
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
