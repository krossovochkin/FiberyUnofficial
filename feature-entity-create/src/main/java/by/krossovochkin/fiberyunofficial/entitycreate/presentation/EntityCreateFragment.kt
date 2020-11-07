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

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.delayTransitions
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformEnterTransition
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitycreate.DaggerEntityCreateComponent
import by.krossovochkin.fiberyunofficial.entitycreate.EntityCreateParentComponent
import by.krossovochkin.fiberyunofficial.entitycreate.R
import by.krossovochkin.fiberyunofficial.entitycreate.databinding.FragmentEntityCreateBinding
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class EntityCreateFragment(
    private val entityCreateParentComponent: EntityCreateParentComponent
) : Fragment(R.layout.fragment_entity_create) {

    @Inject
    lateinit var viewModel: EntityCreateViewModel

    private val binding by viewBinding(FragmentEntityCreateBinding::bind)

    private var parentListener: ParentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        delayTransitions()

        DaggerEntityCreateComponent.factory()
            .create(
                entityCreateParentComponent = entityCreateParentComponent,
                fragment = this
            )
            .inject(this)

        view.transitionName = requireContext()
            .getString(R.string.entity_create_root_transition_name)

        viewModel.navigation.observe(viewLifecycleOwner) { event ->
            when (val navEvent = event.getContentIfNotHandled()) {
                is EntityCreateNavEvent.OnEntityCreateSuccessEvent -> {
                    parentListener?.onEntityCreateSuccess(
                        createdEntity = navEvent.createdEntity
                    )
                }
            }
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

        binding.entityCreateButton.setOnClickListener {
            viewModel.createEntity(binding.entityCreateNameEditText.text.toString())
        }

        binding.entityCreateToolbar.initToolbar(
            activity = requireActivity(),
            state = viewModel.toolbarViewState,
            onBackPressed = { parentListener?.onBackPressed() }
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
        val entityTypeSchema: FiberyEntityTypeSchema
    )

    interface ArgsProvider {

        fun getEntityCreateArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onEntityCreateSuccess(
            createdEntity: FiberyEntityData
        )

        fun onBackPressed()
    }
}
