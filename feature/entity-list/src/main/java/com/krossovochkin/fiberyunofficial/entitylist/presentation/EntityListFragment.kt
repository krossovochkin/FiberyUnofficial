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
package com.krossovochkin.fiberyunofficial.entitylist.presentation

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
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EntityListFragment : Fragment() {

    private val viewModel: EntityListViewModel by viewModels()

    private val parentListener: ParentListener by parentListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()

        setFragmentResultListener(RESULT_KEY_FILTER_PICKED) { _, bundle ->
            val data = bundle.toResultParcelable<FilterPickedData>()
            viewModel.onFilterSelected(filter = data.filter)
        }
        setFragmentResultListener(RESULT_KEY_ENTITY_CREATED) { _, bundle ->
            val data = bundle.toResultParcelable<EntityCreatedData>()
            viewModel.onEntityCreated(createdEntity = data.createdEntity)
        }
        setFragmentResultListener(RESULT_KEY_SORT_PICKED) { _, bundle ->
            val data = bundle.toResultParcelable<SortPickedData>()
            viewModel.onSortSelected(sort = data.sort)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setBackgroundColor(android.graphics.Color.WHITE)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                EntityListScreen(
                    viewModel = viewModel,
                    onBackPressed = { viewModel.onBackPressed() },
                    onEntitySelected = { item ->
                        viewModel.select(item)
                    },
                    onRemoveRelation = { item ->
                        viewModel.removeRelation(item)
                    },
                    onCreateEntityClicked = {
                        viewModel.onCreateEntityClicked()
                    },
                    onFilterClicked = {
                        viewModel.onFilterClicked()
                    },
                    onSortClicked = {
                        viewModel.onSortClicked()
                    },
                    onError = { error ->
                        viewModel.onError(error)
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
                        is EntityListNavEvent.OnEntitySelectedEvent -> {
                            parentListener.onEntitySelected(event.entity)
                        }
                        is EntityListNavEvent.BackEvent -> {
                            parentListener.onBackPressed()
                        }
                        is EntityListNavEvent.OnFilterSelectedEvent -> {
                            parentListener.onFilterEdit(
                                entityTypeSchema = event.entityTypeSchema,
                                filter = event.filter
                            )
                        }
                        is EntityListNavEvent.OnSortSelectedEvent -> {
                            parentListener.onSortEdit(
                                entityTypeSchema = event.entityTypeSchema,
                                sort = event.sort
                            )
                        }
                        is EntityListNavEvent.OnCreateEntityEvent -> {
                            parentListener.onAddEntityRequested(event.entityType, event.parentEntityData)
                        }
                    }
                }
            }
        }
    }

    interface ParentListener {

        fun onEntitySelected(entity: FiberyEntityData)

        fun onAddEntityRequested(
            entityType: FiberyEntityTypeSchema,
            parentEntityData: ParentEntityData?
        )

        fun onFilterEdit(
            entityTypeSchema: FiberyEntityTypeSchema,
            filter: FiberyEntityFilterData
        )

        fun onSortEdit(
            entityTypeSchema: FiberyEntityTypeSchema,
            sort: FiberyEntitySortData
        )

        fun onBackPressed()
    }
}
