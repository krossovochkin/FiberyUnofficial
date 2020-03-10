package by.krossovochkin.fiberyunofficial.entitycreate.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitycreate.DaggerEntityCreateComponent
import by.krossovochkin.fiberyunofficial.entitycreate.EntityCreateParentComponent
import by.krossovochkin.fiberyunofficial.entitycreate.R
import by.krossovochkin.fiberyunofficial.entitycreate.databinding.FragmentEntityCreateBinding
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class EntityCreateFragment(
    private val entityCreateComponent: EntityCreateParentComponent
) : Fragment(R.layout.fragment_entity_create) {

    @Inject
    lateinit var viewModel: EntityCreateViewModel

    private val binding by viewBinding(FragmentEntityCreateBinding::bind)

    private var parentListener: ParentListener? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        DaggerEntityCreateComponent.builder()
            .fragment(this)
            .entityCreateGlobalDependencies(entityCreateComponent)
            .build()
            .inject(this)

        viewModel.navigation.observe(viewLifecycleOwner, Observer { event ->
            when (event.getContentIfNotHandled()) {
                is EntityCreateNavEvent.OnEntityCreateSuccessEvent -> {
                    parentListener?.onEntityCreateSuccess()
                }
            }
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

        binding.entityCreateButton.setOnClickListener {
            viewModel.createEntity(binding.entityCreateNameEditText.text.toString())
        }

        with(viewModel.toolbarViewState) {
            binding.entityCreateToolbar.initToolbar(
                activity = requireActivity(),
                title = getString(R.string.toolbar_title_create, title),
                bgColorInt = bgColorInt,
                hasBackButton = true,
                onBackPressed = { parentListener?.onBackPressed() }
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
        val entityTypeSchema: FiberyEntityTypeSchema
    )

    interface ArgsProvider {

        fun getEntityCreateArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onEntityCreateSuccess()

        fun onBackPressed()
    }
}
