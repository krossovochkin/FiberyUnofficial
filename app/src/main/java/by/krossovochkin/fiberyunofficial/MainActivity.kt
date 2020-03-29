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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.findNavController
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragmentDirections
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.databinding.ActivityMainBinding
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentDirections
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityPickedViewModel
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.MultiSelectPickedViewModel
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.SingleSelectPickedViewModel
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityCreatedData
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityCreatedViewModel
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentDirections
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentDirections
import by.krossovochkin.fiberyunofficial.login.presentation.LoginFragmentDirections

class MainActivity : AppCompatActivity(), MainActivityListener {

    private val mainActivityComponent: MainActivityComponent by lazy {
        DaggerMainActivityComponent.builder()
            .applicationComponent((applicationContext as App).applicationComponent)
            .mainActivityArgsProvider(argsProvider)
            .build()
    }
    private val argsProvider: MainActivityArgsProvider = MainActivityArgsProvider()

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        supportFragmentManager.fragmentFactory = MainActivityFragmentFactory(mainActivityComponent)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onAppSelected(fiberyAppData: FiberyAppData) {
        binding.navHostFragment.findNavController().navigate(
            AppListFragmentDirections.actionAppListToEntityTypeList(fiberyAppData)
        )
    }

    @Suppress("UseIfInsteadOfWhen")
    override fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema) {
        val navController = binding.navHostFragment.findNavController()
        val directions = when (val id = navController.currentDestination?.id) {
            R.id.entityTypeList -> {
                EntityTypeListFragmentDirections.actionEntityTypeListToEntityList(
                    entityType = entityTypeSchema,
                    entity = null,
                    field = null
                )
            }
            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions)
    }

    @Suppress("UseIfInsteadOfWhen")
    override fun onEntityTypeSelected(
        entityTypeSchema: FiberyEntityTypeSchema,
        entity: FiberyEntityData,
        fieldSchema: FiberyFieldSchema
    ) {
        val navController = binding.navHostFragment.findNavController()
        val directions = when (val id = navController.currentDestination?.id) {
            R.id.entityDetails -> {
                EntityDetailsFragmentDirections.actionEntityDetailsToEntityList(
                    entityType = entityTypeSchema,
                    entity = entity,
                    field = fieldSchema
                )
            }
            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions)
    }

    override fun onEntitySelected(entity: FiberyEntityData) {
        val navController = binding.navHostFragment.findNavController()
        val directions = when (val id = navController.currentDestination?.id) {
            R.id.entityList -> {
                EntityListFragmentDirections.actionEntityListToEntityDetails(entity)
            }
            R.id.entityDetails -> {
                EntityDetailsFragmentDirections.actionEntityDetailsSelf(entity)
            }
            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions)
    }

    override fun onLoginSuccess() {
        binding.navHostFragment.findNavController()
            .navigate(LoginFragmentDirections.actionLoginFragmentToAppList())
    }

    override fun onAddEntityRequested(
        entityType: FiberyEntityTypeSchema,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ) {
        if (entityParams == null) {
            binding.navHostFragment.findNavController().navigate(
                EntityListFragmentDirections
                    .actionEntityListToEntityCreateFragment(
                        entityType,
                        entityParams?.second,
                        entityParams?.first
                    )
            )
        } else {
            binding.navHostFragment.findNavController().navigate(
                EntityListFragmentDirections
                    .actionEntityListToEntityPickerFragment(
                        fieldSchema = entityParams.first,
                        entity = entityParams.second
                    )
            )
        }
    }

    override fun onEntityCreateSuccess(
        createdEntityId: String,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ) {
        ViewModelProvider(this@MainActivity).get<EntityCreatedViewModel>()
            .createEntity(
                EntityCreatedData(
                    entityParams = entityParams,
                    createdEntityId = createdEntityId
                )
            )
        onBackPressed()
    }

    override fun onEntityFieldEdit(fieldSchema: FiberyFieldSchema, entity: FiberyEntityData?) {
        binding.navHostFragment.findNavController().navigate(
            EntityDetailsFragmentDirections
                .actionEntityDetailsToEntityPickerFragment(
                    fieldSchema = fieldSchema,
                    entity = entity
                )
        )
    }

    override fun onEntityPicked(
        fieldSchema: FiberyFieldSchema,
        entity: FiberyEntityData?,
        parentEntity: FiberyEntityData?
    ) {
        if (fieldSchema.meta.isCollection) {
            if (entity == null) {
                error("Can't add null entity to collection")
            }
            ViewModelProvider(this@MainActivity).get<EntityCreatedViewModel>()
                .createEntity(
                    EntityCreatedData(
                        createdEntityId = entity.id,
                        entityParams = parentEntity?.let { fieldSchema to it }
                    )
                )
        } else {
            ViewModelProvider(this@MainActivity).get<EntityPickedViewModel>()
                .pickEntity(fieldSchema, entity)
        }
        onBackPressed()
    }

    override fun onSingleSelectFieldEdit(
        fieldSchema: FiberyFieldSchema,
        item: FieldData.SingleSelectFieldData
    ) {
        binding.navHostFragment.findNavController().navigate(
            EntityDetailsFragmentDirections
                .actionEntityDetailsToPickerSingleSelectDialogFragment(item, fieldSchema)
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
        fieldSchema: FiberyFieldSchema,
        item: FieldData.MultiSelectFieldData
    ) {
        binding.navHostFragment.findNavController().navigate(
            EntityDetailsFragmentDirections
                .actionEntityDetailsToPickerMultiSelectDialogFragment(fieldSchema, item)
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
}
