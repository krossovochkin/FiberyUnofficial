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
package com.krossovochkin.fiberyunofficial.entitycreate.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.krossovochkin.core.presentation.animation.setupTransformEnterTransition
import com.krossovochkin.core.presentation.navigation.initNavigation
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.core.presentation.system.updateInsetMargins
import com.krossovochkin.core.presentation.ui.error.initErrorHandler
import com.krossovochkin.core.presentation.ui.toolbar.initToolbar
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.entitycreate.R
import com.krossovochkin.fiberyunofficial.entitycreate.databinding.EntityCreateFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class EntityCreateFragment : Fragment(R.layout.entity_create_fragment) {

    private val viewModel: EntityCreateViewModel by viewModels()

    private val binding by viewBinding(EntityCreateFragmentBinding::bind)

    private val parentListener: ParentListener by parentListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation(
            navigationData = viewModel.navigation,
            transitionName = requireContext()
                .getString(R.string.entity_create_root_transition_name)
        ) { event ->
            when (event) {
                is EntityCreateNavEvent.OnEntityCreateSuccessEvent -> {
                    parentListener.onEntityCreateSuccess(
                        createdEntity = event.createdEntity
                    )
                }
            }
        }

        initToolbar(
            toolbar = binding.entityCreateToolbar,
            toolbarData = MutableStateFlow(viewModel.toolbarViewState),
            onBackPressed = { parentListener.onBackPressed() }
        )

        initErrorHandler(viewModel.error)

        binding.entityCreateButton.setOnClickListener {
            viewModel.createEntity(binding.entityCreateNameEditText.text.toString())
        }
        binding.entityCreateButton.updateInsetMargins(bottom = true)
    }

    interface ParentListener {

        fun onEntityCreateSuccess(
            createdEntity: FiberyEntityData
        )

        fun onBackPressed()
    }
}
