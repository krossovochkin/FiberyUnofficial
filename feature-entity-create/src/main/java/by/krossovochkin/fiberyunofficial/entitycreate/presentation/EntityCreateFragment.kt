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
package by.krossovochkin.fiberyunofficial.entitycreate.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.initErrorHandler
import by.krossovochkin.fiberyunofficial.core.presentation.initNavigation
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.parentListener
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformEnterTransition
import by.krossovochkin.fiberyunofficial.core.presentation.updateInsetMargins
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitycreate.R
import by.krossovochkin.fiberyunofficial.entitycreate.databinding.EntityCreateFragmentBinding

class EntityCreateFragment(
    factoryProducer: () -> EntityCreateViewModelFactory
) : Fragment(R.layout.entity_create_fragment) {

    private val viewModel: EntityCreateViewModel by viewModels { factoryProducer() }

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
            toolbarData = MutableLiveData(viewModel.toolbarViewState),
            onBackPressed = { parentListener.onBackPressed() }
        )

        initErrorHandler(viewModel.error)

        binding.entityCreateButton.setOnClickListener {
            viewModel.createEntity(binding.entityCreateNameEditText.text.toString())
        }
        binding.entityCreateButton.updateInsetMargins(requireActivity(), bottom = true)
    }

    data class Args(
        val entityTypeSchema: FiberyEntityTypeSchema
    )

    fun interface ArgsProvider {

        fun getEntityCreateArgs(): Args
    }

    interface ParentListener {

        fun onEntityCreateSuccess(
            createdEntity: FiberyEntityData
        )

        fun onBackPressed()
    }
}
