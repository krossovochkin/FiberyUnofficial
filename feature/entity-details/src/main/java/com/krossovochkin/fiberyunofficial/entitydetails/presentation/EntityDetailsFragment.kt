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
package com.krossovochkin.fiberyunofficial.entitydetails.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.core.text.util.LinkifyCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.core.presentation.animation.setupTransformEnterTransition
import com.krossovochkin.core.presentation.color.ColorUtils
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.list.OffsetItemDecoration
import com.krossovochkin.core.presentation.list.initRecyclerView
import com.krossovochkin.core.presentation.navigation.initNavigation
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.core.presentation.result.toResultParcelable
import com.krossovochkin.core.presentation.ui.error.initErrorHandler
import com.krossovochkin.core.presentation.ui.progress.initProgressBar
import com.krossovochkin.core.presentation.ui.toolbar.initToolbar
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitydetails.R
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsFragmentBinding
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsItemFieldCheckboxBinding
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsItemFieldCollectionBinding
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsItemFieldEmailBinding
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsItemFieldHeaderBinding
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsItemFieldMultiSelectBinding
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsItemFieldRelationBinding
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsItemFieldRichTextBinding
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsItemFieldSingleSelectBinding
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsItemFieldTextBinding
import com.krossovochkin.fiberyunofficial.entitydetails.databinding.EntityDetailsItemFieldUrlBinding
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.core.net.toUri

class EntityDetailsFragment(
    viewModelFactory: EntityDetailsViewModel.Factory,
    private val argsProvider: ArgsProvider
) : Fragment(R.layout.entity_details_fragment) {

    private val viewModel: EntityDetailsViewModel by viewModels {
        EntityDetailsViewModel.provideFactory(
            viewModelFactory,
            argsProvider.getEntityDetailsArgs()
        )
    }

    private val binding by viewBinding(EntityDetailsFragmentBinding::bind)

    private val parentListener: ParentListener by parentListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()

        setFragmentResultListener(RESULT_KEY_ENTITY_PICKED) { _, bundle ->
            val (parentEntityData, entity) = bundle.toResultParcelable<EntityPickedData>()
            viewModel.updateEntityField(parentEntityData.fieldSchema, entity)
        }
        setFragmentResultListener(RESULT_KEY_SINGLE_SELECT_PICKED) { _, bundle ->
            val (fieldSchema, item) = bundle.toResultParcelable<SingleSelectPickedData>()
            viewModel.updateSingleSelectField(fieldSchema, item)
        }
        setFragmentResultListener(RESULT_KEY_MULTI_SELECT_PICKED) { _, bundle ->
            val data = bundle.toResultParcelable<MultiSelectPickedData>()
            viewModel.updateMultiSelectField(data)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation(
            navigationData = viewModel.navigation,
            transitionName = argsProvider.getEntityDetailsArgs().entityData.id
                .let {
                    requireContext().getString(R.string.entity_details_root_transition_name, it)
                }
        ) { event ->
            when (event) {
                is EntityDetailsNavEvent.OnEntitySelectedEvent -> {
                    parentListener.onEntitySelected(event.entity, event.itemView)
                }
                is EntityDetailsNavEvent.OnEntityTypeSelectedEvent -> {
                    parentListener.onEntityTypeSelected(
                        entityTypeSchema = event.entityTypeSchema,
                        parentEntityData = event.parentEntityData,
                        itemView = event.itemView
                    )
                }
                is EntityDetailsNavEvent.BackEvent -> {
                    parentListener.onBackPressed()
                }
                is EntityDetailsNavEvent.OnSingleSelectSelectedEvent -> {
                    parentListener.onSingleSelectFieldEdit(
                        parentEntityData = event.parentEntityData,
                        item = event.singleSelectItem
                    )
                }
                is EntityDetailsNavEvent.OnMultiSelectSelectedEvent -> {
                    parentListener.onMultiSelectFieldEdit(
                        parentEntityData = event.parentEntityData,
                        item = event.multiSelectItem
                    )
                }
                is EntityDetailsNavEvent.OnEntityFieldEditEvent -> {
                    parentListener.onEntityFieldEdit(
                        parentEntityData = event.parentEntityData,
                        entity = event.currentEntity,
                        itemView = event.itemView
                    )
                }
                is EntityDetailsNavEvent.OpenUrlEvent -> {
                    Intent(Intent.ACTION_VIEW).setData(event.url.toUri()).let { intent ->
                        intent.resolveActivity(requireContext().packageManager)?.let {
                            startActivity(intent)
                        }
                    }
                }
                is EntityDetailsNavEvent.SendEmailEvent -> {
                    Intent(Intent.ACTION_SENDTO).setData("mailto://".toUri())
                        .apply { putExtra(Intent.EXTRA_EMAIL, event.email) }
                        .let { intent ->
                            intent.resolveActivity(requireContext().packageManager)?.let {
                                startActivity(intent)
                            }
                        }
                }
            }
        }

        initToolbar(
            toolbar = binding.entityDetailsToolbar,
            toolbarData = MutableStateFlow(viewModel.toolbarViewState),
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

        initRecyclerView(
            recyclerView = binding.entityDetailsRecyclerView,
            itemsFlow = viewModel.items,
            adapterDelegateViewBinding<FieldHeaderItem, ListItem, EntityDetailsItemFieldHeaderBinding>(
                viewBinding = { inflater, parent ->
                    EntityDetailsItemFieldHeaderBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fieldHeaderTitleTextView.text = item.title
                }
            },
            adapterDelegateViewBinding<FieldTextItem, ListItem, EntityDetailsItemFieldTextBinding>(
                viewBinding = { inflater, parent ->
                    EntityDetailsItemFieldTextBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fieldTextTitleView.text = item.title
                    binding.fieldTextView.text = item.text
                }
            },
            adapterDelegateViewBinding<FieldUrlItem, ListItem, EntityDetailsItemFieldUrlBinding>(
                viewBinding = { inflater, parent ->
                    EntityDetailsItemFieldUrlBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fieldUrlTitleView.text = item.title
                    binding.fieldUrlView.text = item.url

                    if (item.isOpenAvailable) {
                        LinkifyCompat.addLinks(binding.fieldUrlView, Linkify.WEB_URLS)
                        itemView.setOnClickListener { viewModel.selectUrl(item) }
                    }
                }
            },
            adapterDelegateViewBinding<FieldEmailItem, ListItem, EntityDetailsItemFieldEmailBinding>(
                viewBinding = { inflater, parent ->
                    EntityDetailsItemFieldEmailBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fieldEmailTitleView.text = item.title
                    binding.fieldEmailView.text = item.email

                    if (item.isOpenAvailable) {
                        LinkifyCompat.addLinks(binding.fieldEmailView, Linkify.EMAIL_ADDRESSES)
                        itemView.setOnClickListener { viewModel.selectEmail(item) }
                    }
                }
            },
            adapterDelegateViewBinding<FieldSingleSelectItem, ListItem, EntityDetailsItemFieldSingleSelectBinding>(
                viewBinding = { inflater, parent ->
                    EntityDetailsItemFieldSingleSelectBinding.inflate(inflater, parent, false)
                }
            ) {
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
            adapterDelegateViewBinding<FieldMultiSelectItem, ListItem, EntityDetailsItemFieldMultiSelectBinding>(
                viewBinding = { inflater, parent ->
                    EntityDetailsItemFieldMultiSelectBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fieldMultiSelectTitleView.text = item.title
                    binding.fieldMultiSelectView.text = item.text
                    binding.root.setOnClickListener {
                        viewModel.selectMultiSelectField(item)
                    }
                }
            },
            adapterDelegateViewBinding<FieldRichTextItem, ListItem, EntityDetailsItemFieldRichTextBinding>(
                viewBinding = { inflater, parent ->
                    EntityDetailsItemFieldRichTextBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.richTextTitleView.text = item.title

                    Markwon.create(context).setMarkdown(binding.richTextView, item.value)
                }
            },
            adapterDelegateViewBinding<FieldRelationItem, ListItem, EntityDetailsItemFieldRelationBinding>(
                viewBinding = { inflater, parent ->
                    EntityDetailsItemFieldRelationBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fieldRelationTitleView.text = item.title
                    binding.fieldRelationEntityNameTextView.text = item.entityName

                    itemView.setOnClickListener {
                        viewModel.selectEntityField(item.fieldSchema, item.entityData, itemView)
                    }

                    binding.fieldRelationOpenAction.imageTintList =
                        ColorStateList.valueOf(ColorUtils.getDefaultContrastColor(requireContext()))
                    binding.fieldRelationOpenAction.setOnClickListener {
                        item.entityData?.let { viewModel.openEntity(it, itemView) }
                    }
                    binding.fieldRelationOpenAction.isVisible = item.isOpenAvailable

                    binding.fieldRelationDeleteAction.imageTintList =
                        ColorStateList.valueOf(ColorUtils.getDefaultContrastColor(requireContext()))
                    binding.fieldRelationDeleteAction.setOnClickListener {
                        item.entityData?.let { viewModel.updateEntityField(item.fieldSchema, null) }
                    }
                    binding.fieldRelationDeleteAction.isVisible = item.isDeleteAvailable
                    itemView.transitionName = requireContext()
                        .getString(
                            R.string.entity_details_list_transition_name,
                            absoluteAdapterPosition
                        )
                }
            },
            adapterDelegateViewBinding<FieldCollectionItem, ListItem, EntityDetailsItemFieldCollectionBinding>(
                viewBinding = { inflater, parent ->
                    EntityDetailsItemFieldCollectionBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fieldCollectionTitleView.text = item.title
                    binding.fieldCollectionCountTextView.text = item.countText

                    itemView.setOnClickListener {
                        viewModel.selectCollectionField(
                            entityTypeSchema = item.entityTypeSchema,
                            fieldSchema = item.fieldSchema,
                            itemView = itemView
                        )
                    }
                    itemView.transitionName = requireContext()
                        .getString(
                            R.string.entity_details_list_transition_name,
                            absoluteAdapterPosition
                        )
                }
            },
            adapterDelegateViewBinding<FieldCheckboxItem, ListItem, EntityDetailsItemFieldCheckboxBinding>(
                viewBinding = { inflater, parent ->
                    EntityDetailsItemFieldCheckboxBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fieldCheckboxTitleView.text = item.title
                    binding.fieldCheckBox.isChecked = item.value
                    binding.fieldCheckBox.isEnabled = false
                }
            },
            itemDecoration = OffsetItemDecoration(R.dimen.entity_details_field_offset),
        )

        initProgressBar(
            progressBar = binding.progressBar,
            progressVisibleData = viewModel.progress
        )

        initErrorHandler(viewModel.error)
    }

    data class Args(
        val entityData: FiberyEntityData
    )

    fun interface ArgsProvider {

        fun getEntityDetailsArgs(): Args
    }

    interface ParentListener {

        fun onEntitySelected(
            entity: FiberyEntityData,
            itemView: View
        )

        fun onEntityTypeSelected(
            entityTypeSchema: FiberyEntityTypeSchema,
            parentEntityData: ParentEntityData,
            itemView: View
        )

        fun onEntityFieldEdit(
            parentEntityData: ParentEntityData,
            entity: FiberyEntityData?,
            itemView: View
        )

        fun onSingleSelectFieldEdit(
            parentEntityData: ParentEntityData,
            item: FieldData.SingleSelectFieldData
        )

        fun onMultiSelectFieldEdit(
            parentEntityData: ParentEntityData,
            item: FieldData.MultiSelectFieldData
        )

        fun onBackPressed()
    }
}
