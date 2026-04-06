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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.krossovochkin.core.presentation.animation.setupTransformEnterTransition
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.core.presentation.result.toResultParcelable
import com.krossovochkin.core.presentation.ui.error.initErrorHandler
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.core.net.toUri

@AndroidEntryPoint
class EntityDetailsFragment : Fragment() {

    private val viewModel: EntityDetailsViewModel by viewModels()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): android.view.View {
        val dummyView = android.view.View(requireContext())
        return ComposeView(requireContext()).apply {
            setBackgroundColor(android.graphics.Color.WHITE)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                EntityDetailsScreen(
                    viewModel = viewModel,
                    onBackPressed = { viewModel.onBackPressed() },
                    onDeleteClicked = { viewModel.deleteEntity() },
                    onFieldHeaderClicked = { /* no-op */ },
                    onTextFieldClicked = { /* no-op */ },
                    onUrlFieldClicked = { viewModel.selectUrl(it) },
                    onEmailFieldClicked = { viewModel.selectEmail(it) },
                    onSingleSelectClicked = { viewModel.selectSingleSelectField(it) },
                    onMultiSelectClicked = { viewModel.selectMultiSelectField(it) },
                    onRelationFieldClicked = { fieldSchema, entityData, _ ->
                        viewModel.selectEntityField(fieldSchema, entityData, dummyView)
                    },
                    onRelationOpenClicked = { entityData, _ ->
                        viewModel.openEntity(entityData, dummyView)
                    },
                    onRelationDeleteClicked = { fieldSchema ->
                        viewModel.updateEntityField(fieldSchema, null)
                    },
                    onCollectionFieldClicked = { entityTypeSchema, fieldSchema, _ ->
                        viewModel.selectCollectionField(entityTypeSchema, fieldSchema, dummyView)
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initErrorHandler(viewModel.error)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigation.collect { event ->
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
            }
        }
    }

    interface ParentListener {

        fun onEntitySelected(
            entity: FiberyEntityData,
            itemView: android.view.View
        )

        fun onEntityTypeSelected(
            entityTypeSchema: FiberyEntityTypeSchema,
            parentEntityData: ParentEntityData,
            itemView: android.view.View
        )

        fun onEntityFieldEdit(
            parentEntityData: ParentEntityData,
            entity: FiberyEntityData?,
            itemView: android.view.View
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
