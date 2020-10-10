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

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.OffsetItemDecoration
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
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
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
        adapterDelegateViewBinding<FieldHeaderItem, ListItem, ItemFieldHeaderBinding>(
            viewBinding = { inflater, parent ->
                ItemFieldHeaderBinding.inflate(inflater, parent, false)
            }
        ) {
            bind {
                binding.fieldHeaderTitleTextView.text = item.title
            }
        },
        adapterDelegateViewBinding<FieldTextItem, ListItem, ItemFieldTextBinding>(
            viewBinding = { inflater, parent ->
                ItemFieldTextBinding.inflate(inflater, parent, false)
            }
        ) {
            bind {
                binding.fieldTextTitleView.text = item.title
                binding.fieldTextView.text = item.text
            }
        },
        adapterDelegateViewBinding<FieldUrlItem, ListItem, ItemFieldUrlBinding>(
            viewBinding = { inflater, parent ->
                ItemFieldUrlBinding.inflate(inflater, parent, false)
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
        adapterDelegateViewBinding<FieldEmailItem, ListItem, ItemFieldEmailBinding>(
            viewBinding = { inflater, parent ->
                ItemFieldEmailBinding.inflate(inflater, parent, false)
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
        adapterDelegateViewBinding<FieldSingleSelectItem, ListItem, ItemFieldSingleSelectBinding>(
            viewBinding = { inflater, parent ->
                ItemFieldSingleSelectBinding.inflate(inflater, parent, false)
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
        adapterDelegateViewBinding<FieldMultiSelectItem, ListItem, ItemFieldMultiSelectBinding>(
            viewBinding = { inflater, parent ->
                ItemFieldMultiSelectBinding.inflate(inflater, parent, false)
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
        adapterDelegateViewBinding<FieldRichTextItem, ListItem, ItemFieldRichTextBinding>(
            viewBinding = { inflater, parent ->
                ItemFieldRichTextBinding.inflate(inflater, parent, false)
            }
        ) {
            bind {
                binding.richTextTitleView.text = item.title

                Markwon.create(context).setMarkdown(binding.richTextView, item.value)
            }
        },
        adapterDelegateViewBinding<FieldRelationItem, ListItem, ItemFieldRelationBinding>(
            viewBinding = { inflater, parent ->
                ItemFieldRelationBinding.inflate(inflater, parent, false)
            }
        ) {
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
        adapterDelegateViewBinding<FieldCollectionItem, ListItem, ItemFieldCollectionBinding>(
            viewBinding = { inflater, parent ->
                ItemFieldCollectionBinding.inflate(inflater, parent, false)
            }
        ) {
            bind {
                binding.fieldCollectionTitleView.text = item.title
                binding.fieldCollectionCountTextView.text = item.countText

                itemView.setOnClickListener {
                    viewModel.selectCollectionField(
                        entityTypeSchema = item.entityTypeSchema,
                        fieldSchema = item.fieldSchema
                    )
                }
            }
        },
        adapterDelegateViewBinding<FieldCheckboxItem, ListItem, ItemFieldCheckboxBinding>(
            viewBinding = { inflater, parent ->
                ItemFieldCheckboxBinding.inflate(inflater, parent, false)
            }
        ) {
            bind {
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

        DaggerEntityDetailsComponent.factory()
            .create(
                entityDetailsParentComponent = entityDetailsParentComponent,
                fragment = this
            )
            .inject(this)

        initList()
        initNavigation()
        initToolbar()

        entityPickedViewModel.pickedEntity.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { (parentEntityData, entity) ->
                viewModel.updateEntityField(parentEntityData.fieldSchema, entity)
            }
        }
        singleSelectPickedViewModel.pickedSingleSelect.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { (fieldSchema, item) ->
                viewModel.updateSingleSelectField(fieldSchema, item)
            }
        }
        multiSelectPickedViewModel.pickedMultiSelect.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { data ->
                viewModel.updateMultiSelectField(data)
            }
        }
    }

    private fun initList() {
        binding.entityDetailsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.entityDetailsRecyclerView.adapter = adapter
        binding.entityDetailsRecyclerView.addItemDecoration(OffsetItemDecoration(R.dimen.entityDetails_fieldOffset))

        viewModel.items.observe(viewLifecycleOwner) {
            adapter.items = it
            adapter.notifyDataSetChanged()
        }

        viewModel.progress.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }

        viewModel.error.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { error ->
                Snackbar
                    .make(
                        requireView(),
                        error.message ?: getString(R.string.unknown_error),
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun initNavigation() {
        viewModel.navigation.observe(viewLifecycleOwner) { event ->
            when (val navEvent = event.getContentIfNotHandled()) {
                is EntityDetailsNavEvent.OnEntitySelectedEvent -> {
                    parentListener?.onEntitySelected(navEvent.entity)
                }
                is EntityDetailsNavEvent.OnEntityTypeSelectedEvent -> {
                    parentListener?.onEntityTypeSelected(
                        entityTypeSchema = navEvent.entityTypeSchema,
                        parentEntityData = navEvent.parentEntityData
                    )
                }
                is EntityDetailsNavEvent.BackEvent -> {
                    parentListener?.onBackPressed()
                }
                is EntityDetailsNavEvent.OnSingleSelectSelectedEvent -> {
                    parentListener?.onSingleSelectFieldEdit(
                        parentEntityData = navEvent.parentEntityData,
                        item = navEvent.singleSelectItem
                    )
                }
                is EntityDetailsNavEvent.OnMultiSelectSelectedEvent -> {
                    parentListener?.onMultiSelectFieldEdit(
                        parentEntityData = navEvent.parentEntityData,
                        item = navEvent.multiSelectItem
                    )
                }
                is EntityDetailsNavEvent.OnEntityFieldEditEvent -> {
                    parentListener?.onEntityFieldEdit(
                        parentEntityData = navEvent.parentEntityData,
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
        }
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
            parentEntityData: ParentEntityData
        )

        fun onEntityFieldEdit(
            parentEntityData: ParentEntityData,
            entity: FiberyEntityData?
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
