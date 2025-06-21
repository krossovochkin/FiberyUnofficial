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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
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
import com.krossovochkin.commentlist.R as CommentListR
import com.krossovochkin.fiberyunofficial.entitycreate.R as EntityCreateR
import com.krossovochkin.fiberyunofficial.entitydetails.R as EntityDetailsR
import com.krossovochkin.fiberyunofficial.entitylist.R as EntityListR
import com.krossovochkin.fiberyunofficial.entitypicker.R as PickerEntityR
import com.krossovochkin.fiberyunofficial.entitytypelist.R as EntityTypeListR
import com.krossovochkin.fiberyunofficial.pickerfilter.R as PickerFilterR
import com.krossovochkin.fiberyunofficial.pickersort.R as PickerSortR
import com.krossovochkin.filelist.R as FileListR

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainActivityListener {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(binding.root)
    }

    override fun onAppSelected(fiberyAppData: FiberyAppData, itemView: View) {
        val key = getString(EntityTypeListR.string.entity_type_list_root_transition_name)
        binding.navHostFragment.findNavController().navigate(
            NavGraphDirections.actionAppListToEntityTypeList(fiberyAppData),
            FragmentNavigatorExtras(itemView to key)
        )
    }

    @Suppress("UseIfInsteadOfWhen")
    override fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema, itemView: View) {
        val navController = binding.navHostFragment.findNavController()
        val (directions, extras) = when (val id = navController.currentDestination?.id) {
            com.krossovochkin.fiberyunofficial.entitytypelist.R.id.entityTypeList -> {
                val key =
                    getString(EntityListR.string.entity_list_root_transition_name)
                NavGraphDirections.actionEntityTypeListToEntityList(
                    entityType = entityTypeSchema,
                    parentEntityData = null
                ) to FragmentNavigatorExtras(itemView to key)
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
            com.krossovochkin.fiberyunofficial.entitydetails.R.id.entityDetails -> {
                when (entityTypeSchema.name) {
                    FiberyApiConstants.Type.FILE.value -> {
                        val key = getString(FileListR.string.file_list_root_transition_name)
                        NavGraphDirections.actionEntityDetailsToFileListFragment(
                            entityType = entityTypeSchema,
                            parentEntityData = parentEntityData
                        ) to FragmentNavigatorExtras(itemView to key)
                    }

                    FiberyApiConstants.Type.COMMENT.value -> {
                        val key = getString(CommentListR.string.comment_list_root_transition_name)
                        NavGraphDirections.actionEntityDetailsToCommentListFragment(
                            entityType = entityTypeSchema,
                            parentEntityData = parentEntityData
                        ) to FragmentNavigatorExtras(itemView to key)
                    }

                    else -> {
                        val key = getString(EntityListR.string.entity_list_root_transition_name)
                        NavGraphDirections.actionEntityDetailsToEntityList(
                            entityType = entityTypeSchema,
                            parentEntityData = parentEntityData
                        ) to FragmentNavigatorExtras(itemView to key)
                    }
                }
            }

            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions, extras)
    }

    override fun onEntitySelected(entity: FiberyEntityData, itemView: View) {
        val navController = binding.navHostFragment.findNavController()
        val (directions, extras) = when (val id = navController.currentDestination?.id) {
            com.krossovochkin.fiberyunofficial.entitylist.R.id.entityList -> {
                val key = getString(
                    EntityDetailsR.string.entity_details_root_transition_name,
                    entity.id
                )
                NavGraphDirections.actionEntityListToEntityDetails(entity) to
                    FragmentNavigatorExtras(itemView to key)
            }

            com.krossovochkin.fiberyunofficial.entitydetails.R.id.entityDetails -> {
                val key = getString(
                    EntityDetailsR.string.entity_details_root_transition_name,
                    entity.id
                )
                NavGraphDirections.actionEntityDetailsSelf(entity) to
                    FragmentNavigatorExtras(itemView to key)
            }

            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions, extras)
    }

    override fun onLoginSuccess() {
        binding.navHostFragment.findNavController()
            .navigate(NavGraphDirections.actionLoginFragmentToAppList())
    }

    override fun onAddEntityRequested(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData?,
        view: View
    ) {
        if (parentEntityData == null) {
            val key =
                getString(EntityCreateR.string.entity_create_root_transition_name)
            binding.navHostFragment.findNavController().navigate(
                NavGraphDirections
                    .actionEntityListToEntityCreateFragment(
                        entityType = entityType
                    ),
                FragmentNavigatorExtras(view to key)
            )
        } else {
            val key = getString(PickerEntityR.string.picker_entity_root_transition_name)
            binding.navHostFragment.findNavController().navigate(
                NavGraphDirections
                    .actionEntityListToEntityPickerFragment(
                        parentEntityData = parentEntityData,
                        currentEntity = null
                    ),
                FragmentNavigatorExtras(view to key)
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
        entity: FiberyEntityData?,
        itemView: View
    ) {
        val key = getString(PickerEntityR.string.picker_entity_root_transition_name)
        binding.navHostFragment.findNavController().navigate(
            NavGraphDirections
                .actionEntityDetailsToEntityPickerFragment(
                    parentEntityData = parentEntityData,
                    currentEntity = entity
                ),
            FragmentNavigatorExtras(itemView to key)
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
        filter: FiberyEntityFilterData,
        view: View
    ) {
        val key =
            getString(PickerFilterR.string.picker_filter_root_transition_name)
        binding.navHostFragment.findNavController().navigate(
            NavGraphDirections
                .actionEntityListToPickerFilterFragment(
                    entityTypeSchema = entityTypeSchema,
                    filter = filter,
                ),
            FragmentNavigatorExtras(view to key)
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
        sort: FiberyEntitySortData,
        view: View
    ) {
        val key =
            getString(PickerSortR.string.picker_sort_root_transition_name)
        binding.navHostFragment.findNavController().navigate(
            NavGraphDirections
                .actionEntityListToPickerSortFragment(
                    entityTypeSchema = entityTypeSchema,
                    sort = sort
                ),
            FragmentNavigatorExtras(view to key)
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
