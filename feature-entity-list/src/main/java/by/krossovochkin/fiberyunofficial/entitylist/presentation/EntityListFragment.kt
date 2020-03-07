package by.krossovochkin.fiberyunofficial.entitylist.presentation

import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
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
import by.krossovochkin.fiberyunofficial.entitylist.databinding.DialogFilterBinding
import by.krossovochkin.fiberyunofficial.entitylist.databinding.DialogSortBinding
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

    private var parentListener: ParentListener? = null

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

        initList()
        initNavigation()
        initToolbar()
    }

    private fun initList() {
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
    }

    private fun initNavigation() {
        viewModel.navigation.observe(viewLifecycleOwner, Observer { event ->
            when (val navEvent = event.getContentIfNotHandled()) {
                is EntityListNavEvent.OnEntitySelectedEvent -> {
                    parentListener?.onEntitySelected(navEvent.entity)
                }
                is EntityListNavEvent.BackEvent -> {
                    parentListener?.onBackPressed()
                }
                is EntityListNavEvent.OnFilterSelectedEvent -> {
                    showUpdateFilterDialog()
                }
                is EntityListNavEvent.OnSortSelectedEvent -> {
                    showUpdateSortDialog()
                }
            }
        })
    }

    private fun initToolbar() {
        with(viewModel.toolbarViewState) {
            initToolbar(
                toolbar = binding.entityListToolbar,
                title = title,
                bgColorInt = bgColorInt,
                hasBackButton = true,
                onBackPressed = { viewModel.onBackPressed() },
                menuResId = menuResId,
                onMenuItemClicked = { item ->
                    when (item.itemId) {
                        R.id.action_filter -> {
                            viewModel.onFilterClicked()
                            true
                        }
                        R.id.action_sort -> {
                            viewModel.onSortClicked()
                            true
                        }
                        else -> error("Unknown menu item: $item")
                    }
                }
            )
        }
    }

    private fun showUpdateFilterDialog() {
        val binding = DialogFilterBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle(getString(R.string.dialog_filter_title))
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.onFilterSelected(
                    filter = binding.filterTextInput.text.toString(),
                    params = binding.paramsTextInput.text.toString()
                )
            }
            .create()
            .show()
    }

    private fun showUpdateSortDialog() {
        val binding = DialogSortBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle(getString(R.string.dialog_sort_title))
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.onSortSelected(
                    sort = binding.sortTextInput.text.toString()
                )
            }
            .create()
            .show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentListener = context as ParentListener
    }

    override fun onDetach() {
        super.onDetach()
        parentListener = null
    }

    data class Args(
        val entityTypeSchema: FiberyEntityTypeSchema,
        val entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    )

    interface ArgsProvider {

        fun getEntityListArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onEntitySelected(entity: FiberyEntityData)

        fun onBackPressed()
    }
}
