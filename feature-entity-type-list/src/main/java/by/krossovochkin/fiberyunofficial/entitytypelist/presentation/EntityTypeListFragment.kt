package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitytypelist.DaggerEntityTypeListComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListParentComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.R
import by.krossovochkin.fiberyunofficial.entitytypelist.databinding.FragmentEntityTypeListBinding
import by.krossovochkin.fiberyunofficial.entitytypelist.databinding.ItemEntityTypeBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import javax.inject.Inject

class EntityTypeListFragment(
    private val entityTypeListParentComponent: EntityTypeListParentComponent
) : Fragment(R.layout.fragment_entity_type_list) {

    @Inject
    lateinit var viewModel: EntityTypeListViewModel

    private val binding by viewBinding(FragmentEntityTypeListBinding::bind)

    private var parentListener: ParentListener? = null

    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateLayoutContainer<EntityTypeListItem, ListItem>(
            layout = R.layout.item_entity_type
        ) {
            bind {
                val binding = ItemEntityTypeBinding.bind(this.itemView)
                itemView.setOnClickListener { viewModel.select(item) }
                binding.entityTypeTitleTextView.text = item.title
                binding.entityTypeBadgeView.setBackgroundColor(
                    ColorUtils.getDesaturatedColorIfNeeded(requireContext(), item.badgeBgColor)
                )
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

        viewModel.navigation.observe(viewLifecycleOwner, Observer { event ->
            when (val navEvent = event.getContentIfNotHandled()) {
                is EntityTypeListNavEvent.OnEntityTypeSelectedEvent -> {
                    parentListener?.onEntityTypeSelected(navEvent.entityTypeSchema)
                }
                is EntityTypeListNavEvent.BackEvent -> {
                    parentListener?.onBackPressed()
                }
            }
        })

        binding.entityTypeListToolbar.initToolbar(
            activity = requireActivity(),
            title = requireContext().getString(R.string.title_entity_type_list),
            bgColorInt = ColorUtils.getColor(requireContext(), R.attr.colorPrimary),
            hasBackButton = true,
            onBackPressed = { viewModel.onBackPressed() }
        )
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
        val fiberyAppData: FiberyAppData
    )

    interface ArgsProvider {

        fun getEntityTypeListArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema)

        fun onBackPressed()
    }
}
