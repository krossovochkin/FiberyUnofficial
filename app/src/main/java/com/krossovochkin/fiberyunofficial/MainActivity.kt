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
package com.krossovochkin.fiberyunofficial

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import com.krossovochkin.core.presentation.result.toResultBundle
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.databinding.ActivityMainBinding
import com.krossovochkin.fiberyunofficial.domain.FiberyAppData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityPickedData
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.MultiSelectPickedData
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.RESULT_KEY_ENTITY_PICKED
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.RESULT_KEY_MULTI_SELECT_PICKED
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.RESULT_KEY_SINGLE_SELECT_PICKED
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.SingleSelectPickedData
import com.krossovochkin.fiberyunofficial.entitylist.presentation.EntityCreatedData
import com.krossovochkin.fiberyunofficial.entitylist.presentation.FilterPickedData
import com.krossovochkin.fiberyunofficial.entitylist.presentation.RESULT_KEY_ENTITY_CREATED
import com.krossovochkin.fiberyunofficial.entitylist.presentation.RESULT_KEY_FILTER_PICKED
import com.krossovochkin.fiberyunofficial.entitylist.presentation.RESULT_KEY_SORT_PICKED
import com.krossovochkin.fiberyunofficial.entitylist.presentation.SortPickedData
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainActivityListener {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(binding.root)
    }

    override fun onAppSelected(fiberyAppData: FiberyAppData) {
        binding.navHostFragment.findNavController().navigate(
            NavGraphDirections.actionAppListToEntityTypeList(fiberyAppData)
        )
    }

    @Suppress("UseIfInsteadOfWhen")
    override fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema) {
        val navController = binding.navHostFragment.findNavController()
        when (val id = navController.currentDestination?.id) {
            com.krossovochkin.fiberyunofficial.entitytypelist.R.id.entityTypeList -> {
                navController.navigate(
                    NavGraphDirections.actionEntityTypeListToEntityList(
                        entityType = entityTypeSchema,
                        parentEntityData = null
                    )
                )
            }

            else -> error("Unknown current direction: $id")
        }
    }

    @Suppress("UseIfInsteadOfWhen")
    override fun onEntityTypeSelected(
        entityTypeSchema: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData
    ) {
        val navController = binding.navHostFragment.findNavController()
        when (val id = navController.currentDestination?.id) {
            com.krossovochkin.fiberyunofficial.entitydetails.R.id.entityDetails -> {
                when (entityTypeSchema.name) {
                    FiberyApiConstants.Type.FILE.value -> {
                        navController.navigate(
                            NavGraphDirections.actionEntityDetailsToFileListFragment(
                                entityType = entityTypeSchema,
                                parentEntityData = parentEntityData
                            )
                        )
                    }

                    FiberyApiConstants.Type.COMMENT.value -> {
                        navController.navigate(
                            NavGraphDirections.actionEntityDetailsToCommentListFragment(
                                entityType = entityTypeSchema,
                                parentEntityData = parentEntityData
                            )
                        )
                    }

                    else -> {
                        navController.navigate(
                            NavGraphDirections.actionEntityDetailsToEntityList(
                                entityType = entityTypeSchema,
                                parentEntityData = parentEntityData
                            )
                        )
                    }
                }
            }

            else -> error("Unknown current direction: $id")
        }
    }

    override fun onEntitySelected(entity: FiberyEntityData) {
        val navController = binding.navHostFragment.findNavController()
        val directions = when (val id = navController.currentDestination?.id) {
            com.krossovochkin.fiberyunofficial.entitylist.R.id.entityList -> {
                NavGraphDirections.actionEntityListToEntityDetails(entity)
            }

            com.krossovochkin.fiberyunofficial.entitydetails.R.id.entityDetails -> {
                NavGraphDirections.actionEntityDetailsSelf(entity)
            }

            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions)
    }

    override fun onLoginSuccess() {
        binding.navHostFragment.findNavController()
            .navigate(NavGraphDirections.actionLoginFragmentToAppList())
    }

    override fun onAddEntityRequested(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData?
    ) {
        if (parentEntityData == null) {
            binding.navHostFragment.findNavController().navigate(
                NavGraphDirections
                    .actionEntityListToEntityCreateFragment(
                        entityType = entityType
                    )
            )
        } else {
            binding.navHostFragment.findNavController().navigate(
                NavGraphDirections
                    .actionEntityListToEntityPickerFragment(
                        parentEntityData = parentEntityData,
                        currentEntity = null
                    )
            )
        }
    }

    override fun onEntityCreateSuccess(
        createdEntity: FiberyEntityData
    ) {
        setFragmentResult(
            RESULT_KEY_ENTITY_CREATED,
            EntityCreatedData(createdEntity = createdEntity)
        )
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onEntityFieldEdit(
        parentEntityData: ParentEntityData,
        entity: FiberyEntityData?
    ) {
        binding.navHostFragment.findNavController().navigate(
            NavGraphDirections
                .actionEntityDetailsToEntityPickerFragment(
                    parentEntityData = parentEntityData,
                    currentEntity = entity
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
            setFragmentResult(
                RESULT_KEY_ENTITY_CREATED,
                EntityCreatedData(createdEntity = entity)
            )
        } else {
            setFragmentResult(
                RESULT_KEY_ENTITY_PICKED,
                EntityPickedData(parentEntityData, entity)
            )
        }
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onSingleSelectFieldEdit(
        parentEntityData: ParentEntityData,
        item: FieldData.SingleSelectFieldData
    ) {
        binding.navHostFragment.findNavController().navigate(
            NavGraphDirections
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
        setFragmentResult(
            RESULT_KEY_SINGLE_SELECT_PICKED,
            SingleSelectPickedData(fieldSchema, item)
        )
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onMultiSelectFieldEdit(
        parentEntityData: ParentEntityData,
        item: FieldData.MultiSelectFieldData
    ) {
        binding.navHostFragment.findNavController().navigate(
            NavGraphDirections
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
        setFragmentResult(
            RESULT_KEY_MULTI_SELECT_PICKED,
            MultiSelectPickedData(fieldSchema, addedItems, removedItems)
        )
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onFilterEdit(
        entityTypeSchema: FiberyEntityTypeSchema,
        filter: FiberyEntityFilterData
    ) {
        binding.navHostFragment.findNavController().navigate(
            NavGraphDirections
                .actionEntityListToPickerFilterFragment(
                    entityTypeSchema = entityTypeSchema,
                    filter = filter,
                )
        )
    }

    override fun onFilterSelected(filter: FiberyEntityFilterData) {
        setFragmentResult(
            RESULT_KEY_FILTER_PICKED,
            FilterPickedData(filter = filter)
        )
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onSortEdit(
        entityTypeSchema: FiberyEntityTypeSchema,
        sort: FiberyEntitySortData
    ) {
        binding.navHostFragment.findNavController().navigate(
            NavGraphDirections
                .actionEntityListToPickerSortFragment(
                    entityTypeSchema = entityTypeSchema,
                    sort = sort
                )
        )
    }

    override fun onSortSelected(sort: FiberyEntitySortData) {
        setFragmentResult(
            RESULT_KEY_SORT_PICKED,
            SortPickedData(sort = sort)
        )
        onBackPressedDispatcher.onBackPressed()
    }

    private fun setFragmentResult(requestKey: String, result: Parcelable) {
        supportFragmentManager.findFragmentById(R.id.navHostFragment)!!
            .childFragmentManager.setFragmentResult(
                requestKey,
                result.toResultBundle()
            )
    }
}
