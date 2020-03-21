package by.krossovochkin.fiberyunofficial.entitypicker.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitypicker.DaggerEntityPickerComponent
import by.krossovochkin.fiberyunofficial.entitypicker.EntityPickerParentComponent
import by.krossovochkin.fiberyunofficial.entitypicker.R
import by.krossovochkin.fiberyunofficial.entitypicker.databinding.FragmentEntityPickerBinding
import by.krossovochkin.fiberyunofficial.entitypicker.databinding.ItemEntityBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.hannesdorfmann.adapterdelegates4.paging.PagedListDelegationAdapter
import javax.inject.Inject

class EntityPickerFragment(
    private val entityPickerParentComponent: EntityPickerParentComponent
) : Fragment(R.layout.fragment_entity_picker) {

    @Inject
    lateinit var viewModel: EntityPickerViewModel

    private val binding by viewBinding(FragmentEntityPickerBinding::bind)

    private var parentListener: ParentListener? = null

    private val adapter = PagedListDelegationAdapter(
        object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return if (oldItem is EntityPickerItem && newItem is EntityPickerItem) {
                    oldItem.entityData.id == newItem.entityData.id
                } else {
                    oldItem === newItem
                }
            }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return oldItem.equals(newItem)
            }
        },
        adapterDelegateLayoutContainer<EntityPickerItem, ListItem>(
            layout = R.layout.item_entity
        ) {
            val binding = ItemEntityBinding.bind(this.itemView)
            bind {
                itemView.setOnClickListener { viewModel.select(item) }
                binding.entityTitleTextView.text = item.title
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DaggerEntityPickerComponent.builder()
            .fragment(this)
            .entityPickerParentDependencies(entityPickerParentComponent)
            .build()
            .inject(this)

        initList()
        initNavigation()
        initToolbar()
    }

    private fun initList() {
        binding.entityPickerRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.entityPickerRecyclerView.adapter = adapter
        binding.entityPickerRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        viewModel.entityItems.observe(viewLifecycleOwner, Observer {
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
                is EntityPickerNavEvent.OnEntityPickedEvent -> {
                    parentListener?.onEntityPicked(navEvent.fieldSchema, navEvent.entity)
                }
                is EntityPickerNavEvent.BackEvent -> {
                    parentListener?.onBackPressed()
                }
            }
        })
    }

    private fun initToolbar() {
        viewModel.toolbarViewState.observe(viewLifecycleOwner, Observer {
            with(it) {
                binding.entityPickerToolbar.initToolbar(
                    activity = requireActivity(),
                    title = title,
                    bgColorInt = bgColorInt,
                    hasBackButton = true,
                    onBackPressed = { viewModel.onBackPressed() }
                )
            }
        })
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
        val fieldSchema: FiberyFieldSchema,
        val entity: FiberyEntityData?
    )

    interface ArgsProvider {

        fun getEntityPickerArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onEntityPicked(fieldSchema: FiberyFieldSchema, entity: FiberyEntityData?)

        fun onBackPressed()
    }
}