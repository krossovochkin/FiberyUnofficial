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
package by.krossovochkin.fiberyunofficial

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragmentDirections
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.databinding.ActivityMainBinding
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentDirections
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityPickedViewModel
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.MultiSelectPickedViewModel
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.SingleSelectPickedViewModel
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityCreatedData
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityCreatedViewModel
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentDirections
import by.krossovochkin.fiberyunofficial.entitylist.presentation.FilterPickedViewModel
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentDirections
import by.krossovochkin.fiberyunofficial.login.presentation.LoginFragmentDirections

class MainActivity : AppCompatActivity(), MainActivityListener {

    private val mainActivityComponent: MainActivityComponent by lazy {
        DaggerMainActivityComponent.factory()
            .create(
                applicationComponent = (applicationContext as App).applicationComponent,
                mainActivityArgsProvider = argsProvider
            )
    }
    private val argsProvider: MainActivityArgsProvider = MainActivityArgsProvider()

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        supportFragmentManager.fragmentFactory = MainActivityFragmentFactory(mainActivityComponent)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onAppSelected(fiberyAppData: FiberyAppData, itemView: View) {
        binding.navHostFragment.findNavController().navigate(
            AppListFragmentDirections.actionAppListToEntityTypeList(fiberyAppData),
            FragmentNavigatorExtras(
                itemView to getString(R.string.entity_type_list_root_transition_name)
            )
        )
    }

    @Suppress("UseIfInsteadOfWhen")
    override fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema, itemView: View) {
        val navController = binding.navHostFragment.findNavController()
        val (directions, extras) = when (val id = navController.currentDestination?.id) {
            R.id.entityTypeList -> {
                EntityTypeListFragmentDirections.actionEntityTypeListToEntityList(
                    entityType = entityTypeSchema,
                    parentEntityData = null
                ) to FragmentNavigatorExtras(
                    itemView to getString(R.string.entity_list_root_transition_name)
                )
            }
            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions, extras)
    }

    @Suppress("UseIfInsteadOfWhen")
    override fun onEntityTypeSelected(
        entityTypeSchema: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        itemView: View
    ) {
        val navController = binding.navHostFragment.findNavController()
        val (directions, extras) = when (val id = navController.currentDestination?.id) {
            R.id.entityDetails -> {
                EntityDetailsFragmentDirections.actionEntityDetailsToEntityList(
                    entityType = entityTypeSchema,
                    parentEntityData = parentEntityData
                ) to FragmentNavigatorExtras(
                    itemView to getString(R.string.entity_list_root_transition_name)
                )
            }
            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions, extras)
    }

    override fun onEntitySelected(entity: FiberyEntityData, itemView: View) {
        val navController = binding.navHostFragment.findNavController()
        val (directions, extras) = when (val id = navController.currentDestination?.id) {
            R.id.entityList -> {
                EntityListFragmentDirections.actionEntityListToEntityDetails(entity) to
                    FragmentNavigatorExtras(
                        itemView to
                            getString(R.string.entity_details_root_transition_name, entity.id)
                    )
            }
            R.id.entityDetails -> {
                EntityDetailsFragmentDirections.actionEntityDetailsSelf(entity) to
                    FragmentNavigatorExtras(
                        itemView to
                            getString(R.string.entity_details_root_transition_name, entity.id)
                    )
            }
            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions, extras)
    }

    override fun onLoginSuccess() {
        binding.navHostFragment.findNavController()
            .navigate(LoginFragmentDirections.actionLoginFragmentToAppList())
    }

    override fun onAddEntityRequested(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData?,
        view: View
    ) {
        if (parentEntityData == null) {
            binding.navHostFragment.findNavController().navigate(
                EntityListFragmentDirections
                    .actionEntityListToEntityCreateFragment(
                        entityType = entityType
                    ),
                FragmentNavigatorExtras(
                    view to getString(R.string.entity_create_root_transition_name)
                )
            )
        } else {
            binding.navHostFragment.findNavController().navigate(
                EntityListFragmentDirections
                    .actionEntityListToEntityPickerFragment(
                        parentEntityData = parentEntityData,
                        currentEntity = null
                    ),
                FragmentNavigatorExtras(
                    view to getString(R.string.entity_picker_root_transition_name)
                )
            )
        }
    }

    override fun onEntityCreateSuccess(
        createdEntity: FiberyEntityData
    ) {
        ViewModelProvider(this@MainActivity).get<EntityCreatedViewModel>()
            .createEntity(EntityCreatedData(createdEntity = createdEntity))
        onBackPressed()
    }

    override fun onEntityFieldEdit(
        parentEntityData: ParentEntityData,
        entity: FiberyEntityData?,
        itemView: View
    ) {
        binding.navHostFragment.findNavController().navigate(
            EntityDetailsFragmentDirections
                .actionEntityDetailsToEntityPickerFragment(
                    parentEntityData = parentEntityData,
                    currentEntity = entity
                ),
            FragmentNavigatorExtras(
                itemView to getString(R.string.entity_picker_root_transition_name)
            )
        )
    }

    override fun onEntityPicked(
        parentEntityData: ParentEntityData,
        entity: FiberyEntityData?
    ) {
        if (parentEntityData.fieldSchema.meta.isCollection) {
            if (entity == null) {
                error("Can't add null entity to collection")
            }
            ViewModelProvider(this@MainActivity).get<EntityCreatedViewModel>()
                .createEntity(EntityCreatedData(createdEntity = entity))
        } else {
            ViewModelProvider(this@MainActivity).get<EntityPickedViewModel>()
                .pickEntity(parentEntityData, entity)
        }
        onBackPressed()
    }

    override fun onSingleSelectFieldEdit(
        parentEntityData: ParentEntityData,
        item: FieldData.SingleSelectFieldData
    ) {
        binding.navHostFragment.findNavController().navigate(
            EntityDetailsFragmentDirections
                .actionEntityDetailsToPickerSingleSelectDialogFragment(
                    item = item,
                    parentEntityData = parentEntityData
                )
        )
    }

    override fun onSingleSelectPicked(
        fieldSchema: FiberyFieldSchema,
        item: FieldData.EnumItemData
    ) {
        ViewModelProvider(this@MainActivity).get<SingleSelectPickedViewModel>()
            .pickSingleSelect(fieldSchema, item)
        onBackPressed()
    }

    override fun onMultiSelectFieldEdit(
        parentEntityData: ParentEntityData,
        item: FieldData.MultiSelectFieldData
    ) {
        binding.navHostFragment.findNavController().navigate(
            EntityDetailsFragmentDirections
                .actionEntityDetailsToPickerMultiSelectDialogFragment(
                    parentEntityData = parentEntityData,
                    item = item
                )
        )
    }

    override fun onMultiSelectPicked(
        fieldSchema: FiberyFieldSchema,
        addedItems: List<FieldData.EnumItemData>,
        removedItems: List<FieldData.EnumItemData>
    ) {
        ViewModelProvider(this@MainActivity).get<MultiSelectPickedViewModel>()
            .pickMultiSelect(fieldSchema, addedItems, removedItems)
        onBackPressed()
    }

    override fun onFilterEdit(
        entityTypeSchema: FiberyEntityTypeSchema,
        filter: String,
        params: String,
        view: View
    ) {
        binding.navHostFragment.findNavController().navigate(
            EntityListFragmentDirections
                .actionEntityListToPickerFilterFragment(
                    entityTypeSchema = entityTypeSchema,
                    filter = filter,
                    params = params
                ),
            FragmentNavigatorExtras(
                view to getString(R.string.picker_filter_root_transition_name)
            )
        )
    }

    override fun onFilterSelected(filter: String, params: String) {
        ViewModelProvider(this@MainActivity).get<FilterPickedViewModel>()
            .pickFilter(filter = filter, params = params)
        onBackPressed()
    }
}
