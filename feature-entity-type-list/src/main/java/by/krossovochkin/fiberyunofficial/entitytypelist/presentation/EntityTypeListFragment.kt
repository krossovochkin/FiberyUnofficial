package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListParentComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.R
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitytypelist.DaggerEntityTypeListComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.databinding.FragmentEntityTypeListBinding
import by.krossovochkin.fiberyunofficial.entitytypelist.databinding.ItemEntityTypeBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import javax.inject.Inject

class EntityTypeListFragment(
    private val entityTypeListParentComponent: EntityTypeListParentComponent
) : BaseFragment(R.layout.fragment_entity_type_list) {

    @Inject
    lateinit var viewModel: EntityTypeListViewModel

    private val binding by viewBinding(FragmentEntityTypeListBinding::bind)

    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateLayoutContainer<EntityTypeListItem, ListItem>(
            layout = R.layout.item_entity_type
        ) {
            bind {
                val binding = ItemEntityTypeBinding.bind(this.itemView)
                itemView.setOnClickListener { viewModel.select(item) }
                binding.entityTypeTitleTextView.text = item.title
                binding.entityTypeBadgeView.setBackgroundColor(item.badgeBgColor)
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

        binding.entityTypeListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.entityTypeListRecyclerView.adapter = adapter
        binding.entityTypeListRecyclerView
            .addItemDecoration(
                DividerItemDecoration(context, LinearLayout.VERTICAL)
            )

        viewModel.entityTypeItems.observe(viewLifecycleOwner, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        viewModel.progress.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = it
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

        initToolbar(
            toolbar = binding.entityTypeListToolbar,
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





