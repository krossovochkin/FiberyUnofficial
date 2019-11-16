package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import kotlinx.coroutines.launch
import java.lang.Exception

class EntityDetailsViewModel(
    private val getEntityDetailsInteractor: GetEntityDetailsInteractor,
    private val entityDetailsParentListener: ParentListener,
    private val entityDetailsArgs: EntityDetailsFragment.Args
) : ViewModel() {

    init {
        viewModelScope.launch {
            try {
                val entityData = getEntityDetailsInteractor.execute(entityDetailsArgs.entityData)

                Log.e(EntityDetailsViewModel::class.java.simpleName, entityData.toString())
            } catch (e: Exception) {
                Log.e("", "")
            }
        }
    }

    interface ParentListener
}