package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitydetails.DaggerEntityDetailsComponent
import by.krossovochkin.fiberyunofficial.entitydetails.EntityDetailsParentComponent
import by.krossovochkin.fiberyunofficial.entitydetails.R
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.FragmentEntityDetailsBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldCollectionBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldHeaderBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldRelationBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldRichTextBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldSingleSelectBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldTextBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import io.noties.markwon.Markwon
import javax.inject.Inject

class EntityDetailsFragment(
    private val entityDetailsParentComponent: EntityDetailsParentComponent
) : BaseFragment(R.layout.fragment_entity_details) {

    @Inject
    lateinit var viewModel: EntityDetailsViewModel

    private val binding by viewBinding(FragmentEntityDetailsBinding::bind)

    private var parentListener: ParentListener? = null

    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateLayoutContainer<FieldHeaderItem, ListItem>(
            layout = R.layout.item_field_header
        ) {
            val binding = ItemFieldHeaderBinding.bind(this.itemView)
            bind {
                binding.fieldHeaderTitleTextView.text = item.title
            }
        },
        adapterDelegateLayoutContainer<FieldTextItem, ListItem>(
            layout = R.layout.item_field_text
        ) {
            val binding = ItemFieldTextBinding.bind(this.itemView)
            bind {
                binding.fieldTextTitleView.text = item.title
                binding.fieldTextView.text = item.text
            }
        },
        adapterDelegateLayoutContainer<FieldSingleSelect, ListItem>(
            layout = R.layout.item_field_single_select
        ) {
            val binding = ItemFieldSingleSelectBinding.bind(this.itemView)
            bind {
                binding.fieldSingleSelectTitleView.text = item.title
                binding.fieldSingleSelectView.text = item.text
                binding.root.setOnClickListener {
                    var selectedIndex: Int = item.values.map { it.title }.indexOf(item.text)
                    AlertDialog.Builder(requireContext())
                        .setSingleChoiceItems(
                            item.values.map { it.title }.toTypedArray(),
                            selectedIndex
                        ) { _, index -> selectedIndex = index }
                        .setTitle(item.title)
                        .setNegativeButton(android.R.string.cancel) { _, _ -> }
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            viewModel.updateSingleSelectValue(
                                currentTitle = item.text,
                                fieldSchema = item.fieldSchema,
                                selectedValue = item.values[selectedIndex]
                            )
                        }
                        .create()
                        .show()
                }
            }
            onViewRecycled {
                binding.root.setOnClickListener(null)
            }
        },
        adapterDelegateLayoutContainer<FieldRichTextItem, ListItem>(
            layout = R.layout.item_field_rich_text
        ) {
            val binding = ItemFieldRichTextBinding.bind(this.itemView)
            bind {
                binding.richTextTitleView.text = item.title

                Markwon.create(context).setMarkdown(binding.richTextView, item.value)
            }
        },
        adapterDelegateLayoutContainer<FieldRelationItem, ListItem>(
            layout = R.layout.item_field_relation
        ) {
            val binding = ItemFieldRelationBinding.bind(this.itemView)
            bind {
                binding.fieldRelationTitleView.text = item.title
                binding.fieldRelationEntityNameTextView.text = item.entityName

                itemView.setOnClickListener { viewModel.selectEntity(item.entityData) }
            }
        },
        adapterDelegateLayoutContainer<FieldCollectionItem, ListItem>(
            layout = R.layout.item_field_collection
        ) {
            bind {
                val binding = ItemFieldCollectionBinding.bind(this.itemView)
                binding.fieldCollectionTitleView.text = item.title
                binding.fieldCollectionCountTextView.text = item.countText

                itemView.setOnClickListener {
                    viewModel.selectCollection(
                        entityTypeSchema = item.entityTypeSchema,
                        entityData = item.entityData,
                        fieldSchema = item.fieldSchema
                    )
                }
            }
        }
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        DaggerEntityDetailsComponent.builder()
            .fragment(this)
            .entityDetailsParentComponent(entityDetailsParentComponent)
            .build()
            .inject(this)

        binding.entityDetailsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.entityDetailsRecyclerView.adapter = adapter

        viewModel.items.observe(viewLifecycleOwner, Observer {
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
                is EntityDetailsNavEvent.OnEntitySelectedEvent -> {
                    parentListener?.onEntitySelected(navEvent.entity)
                }
                is EntityDetailsNavEvent.OnEntityTypeSelectedEvent -> {
                    parentListener?.onEntityTypeSelected(
                        entityTypeSchema = navEvent.entityTypeSchema,
                        entity = navEvent.entity,
                        fieldSchema = navEvent.fieldSchema
                    )
                }
                is EntityDetailsNavEvent.BackEvent -> {
                    parentListener?.onBackPressed()
                }
            }
        })

        with(viewModel.toolbarViewState) {
            initToolbar(
                toolbar = binding.entityDetailsToolbar,
                title = title,
                bgColorInt = bgColorInt,
                hasBackButton = true,
                onBackPressed = { viewModel.onBackPressed() }
            )
        }
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
        val entityData: FiberyEntityData
    )

    interface ArgsProvider {

        fun getEntityDetailsArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onEntitySelected(entity: FiberyEntityData)

        fun onEntityTypeSelected(
            entityTypeSchema: FiberyEntityTypeSchema,
            entity: FiberyEntityData,
            fieldSchema: FiberyFieldSchema
        )

        fun onBackPressed()
    }
}
