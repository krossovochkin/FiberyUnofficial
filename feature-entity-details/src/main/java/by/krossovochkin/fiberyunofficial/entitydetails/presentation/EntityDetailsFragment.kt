/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.core.text.util.LinkifyCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitydetails.DaggerEntityDetailsComponent
import by.krossovochkin.fiberyunofficial.entitydetails.EntityDetailsParentComponent
import by.krossovochkin.fiberyunofficial.entitydetails.R
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.FragmentEntityDetailsBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldCheckboxBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldCollectionBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldEmailBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldHeaderBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldMultiSelectBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldRelationBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldRichTextBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldSingleSelectBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldTextBinding
import by.krossovochkin.fiberyunofficial.entitydetails.databinding.ItemFieldUrlBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import io.noties.markwon.Markwon
import javax.inject.Inject

class EntityDetailsFragment(
    private val entityDetailsParentComponent: EntityDetailsParentComponent
) : Fragment(R.layout.fragment_entity_details) {

    @Inject
    lateinit var viewModel: EntityDetailsViewModel

    private val binding by viewBinding(FragmentEntityDetailsBinding::bind)

    private var parentListener: ParentListener? = null

    private val adapter = ListDelegationAdapter(
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
        adapterDelegateLayoutContainer<FieldUrlItem, ListItem>(
            layout = R.layout.item_field_url
        ) {
            val binding = ItemFieldUrlBinding.bind(this.itemView)
            bind {
                binding.fieldUrlTitleView.text = item.title
                binding.fieldUrlView.text = item.url

                if (item.isOpenAvailable) {
                    LinkifyCompat.addLinks(binding.fieldUrlView, Linkify.WEB_URLS)
                    itemView.setOnClickListener { viewModel.selectUrl(item) }
                }
            }
        },
        adapterDelegateLayoutContainer<FieldEmailItem, ListItem>(
            layout = R.layout.item_field_email
        ) {
            val binding = ItemFieldEmailBinding.bind(this.itemView)
            bind {
                binding.fieldEmailTitleView.text = item.title
                binding.fieldEmailView.text = item.email

                if (item.isOpenAvailable) {
                    LinkifyCompat.addLinks(binding.fieldEmailView, Linkify.EMAIL_ADDRESSES)
                    itemView.setOnClickListener { viewModel.selectEmail(item) }
                }
            }
        },
        adapterDelegateLayoutContainer<FieldSingleSelectItem, ListItem>(
            layout = R.layout.item_field_single_select
        ) {
            val binding = ItemFieldSingleSelectBinding.bind(this.itemView)
            bind {
                binding.fieldSingleSelectTitleView.text = item.title
                binding.fieldSingleSelectView.text = item.text
                binding.root.setOnClickListener {
                    viewModel.selectSingleSelectField(item)
                }
            }
            onViewRecycled {
                binding.root.setOnClickListener(null)
            }
        },
        adapterDelegateLayoutContainer<FieldMultiSelectItem, ListItem>(
            layout = R.layout.item_field_multi_select
        ) {
            val binding = ItemFieldMultiSelectBinding.bind(this.itemView)
            bind {
                binding.fieldMultiSelectTitleView.text = item.title
                binding.fieldMultiSelectView.text = item.text
                binding.root.setOnClickListener {
                    viewModel.selectMultiSelectField(item)
                }
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

                itemView.setOnClickListener {
                    viewModel.selectEntityField(item.fieldSchema, item.entityData)
                }

                binding.fieldRelationOpenAction.imageTintList =
                    ColorStateList.valueOf(ColorUtils.getDefaultContrastColor(requireContext()))
                binding.fieldRelationOpenAction.setOnClickListener {
                    item.entityData?.let(viewModel::openEntity)
                }
                binding.fieldRelationOpenAction.isVisible = item.isOpenAvailable

                binding.fieldRelationDeleteAction.imageTintList =
                    ColorStateList.valueOf(ColorUtils.getDefaultContrastColor(requireContext()))
                binding.fieldRelationDeleteAction.setOnClickListener {
                    item.entityData?.let { viewModel.updateEntityField(item.fieldSchema, null) }
                }
                binding.fieldRelationDeleteAction.isVisible = item.isDeleteAvailable
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
                    viewModel.selectCollectionField(
                        entityTypeSchema = item.entityTypeSchema,
                        entityData = item.entityData,
                        fieldSchema = item.fieldSchema
                    )
                }
            }
        },
        adapterDelegateLayoutContainer<FieldCheckboxItem, ListItem>(
            layout = R.layout.item_field_checkbox
        ) {
            bind {
                val binding = ItemFieldCheckboxBinding.bind(this.itemView)
                binding.fieldCheckboxTitleView.text = item.title
                binding.fieldCheckBox.isChecked = item.value
                binding.fieldCheckBox.isEnabled = false
            }
        }
    )

    private val entityPickedViewModel: EntityPickedViewModel by activityViewModels()
    private val singleSelectPickedViewModel: SingleSelectPickedViewModel by activityViewModels()
    private val multiSelectPickedViewModel: MultiSelectPickedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DaggerEntityDetailsComponent.builder()
            .fragment(this)
            .entityDetailsParentComponent(entityDetailsParentComponent)
            .build()
            .inject(this)

        initList()
        initNavigation()
        initToolbar()

        entityPickedViewModel.pickedEntity.observe(
            viewLifecycleOwner,
            Observer { event ->
                event.getContentIfNotHandled()?.let { (fieldSchema, entity) ->
                    viewModel.updateEntityField(fieldSchema, entity)
                }
            }
        )
        singleSelectPickedViewModel.pickedSingleSelect.observe(
            viewLifecycleOwner,
            Observer { event ->
                event.getContentIfNotHandled()?.let { (fieldSchema, item) ->
                    viewModel.updateSingleSelectField(fieldSchema, item)
                }
            }
        )
        multiSelectPickedViewModel.pickedMultiSelect.observe(
            viewLifecycleOwner,
            Observer { event ->
                event.getContentIfNotHandled()?.let { data ->
                    viewModel.updateMultiSelectField(data)
                }
            }
        )
    }

    private fun initList() {
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
    }

    private fun initNavigation() {
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
                is EntityDetailsNavEvent.OnSingleSelectSelectedEvent -> {
                    parentListener?.onSingleSelectFieldEdit(
                        navEvent.fieldSchema,
                        navEvent.singleSelectItem
                    )
                }
                is EntityDetailsNavEvent.OnMultiSelectSelectedEvent -> {
                    parentListener?.onMultiSelectFieldEdit(
                        navEvent.fieldSchema,
                        navEvent.multiSelectItem
                    )
                }
                is EntityDetailsNavEvent.OnEntityFieldEditEvent -> {
                    parentListener?.onEntityFieldEdit(
                        fieldSchema = navEvent.fieldSchema,
                        entity = navEvent.currentEntity
                    )
                }
                is EntityDetailsNavEvent.OpenUrlEvent -> {
                    Intent(Intent.ACTION_VIEW).setData(Uri.parse(navEvent.url)).let { intent ->
                        intent.resolveActivity(requireContext().packageManager)?.let {
                            startActivity(intent)
                        }
                    }
                }
                is EntityDetailsNavEvent.SendEmailEvent -> {
                    Intent(Intent.ACTION_SENDTO).setData(Uri.parse("mailto://"))
                        .apply {
                            putExtra(Intent.EXTRA_EMAIL, navEvent.email)
                        }
                        .let { intent ->
                            intent.resolveActivity(requireContext().packageManager)?.let {
                                startActivity(intent)
                            }
                        }
                }
            }
        })
    }

    private fun initToolbar() {
        binding.entityDetailsToolbar.initToolbar(
            activity = requireActivity(),
            state = viewModel.toolbarViewState,
            onBackPressed = { viewModel.onBackPressed() },
            onMenuItemClicked = { menuId ->
                if (menuId.itemId == R.id.action_delete) {
                    viewModel.deleteEntity()
                    true
                } else {
                    error("Unknown menu item")
                }
            }
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

        fun onEntityFieldEdit(
            fieldSchema: FiberyFieldSchema,
            entity: FiberyEntityData?
        )

        fun onSingleSelectFieldEdit(
            fieldSchema: FiberyFieldSchema,
            item: FieldData.SingleSelectFieldData
        )

        fun onMultiSelectFieldEdit(
            fieldSchema: FiberyFieldSchema,
            item: FieldData.MultiSelectFieldData
        )

        fun onBackPressed()
    }
}
