package by.krossovochkin.fiberyunofficial.entitydetails.presentation


import android.os.Bundle
import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.entitydetails.DaggerEntityDetailsComponent
import by.krossovochkin.fiberyunofficial.entitydetails.EntityDetailsParentComponent
import by.krossovochkin.fiberyunofficial.entitydetails.R
import javax.inject.Inject

class EntityDetailsFragment(
    private val entityDetailsParentComponent: EntityDetailsParentComponent
) : Fragment(R.layout.fragment_entity_details) {

    @Inject
    lateinit var viewModel: EntityDetailsViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        DaggerEntityDetailsComponent.builder()
            .fragment(this)
            .entityDetailsParentComponent(entityDetailsParentComponent)
            .build()
            .inject(this)
    }

    interface ArgsProvider {

        fun getEntityDetailsArgs(arguments: Bundle): Args
    }

    data class Args(
        val entityData: FiberyEntityData)

}
