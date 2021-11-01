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
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.krossovochkin.core.presentation.result.toResultBundle
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.applist.presentation.AppListFragmentDirections
import com.krossovochkin.fiberyunofficial.databinding.ActivityMainBinding
import com.krossovochkin.fiberyunofficial.domain.FiberyAppData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentDirections
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityPickedData
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.MultiSelectPickedData
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.RESULT_KEY_ENTITY_PICKED
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.RESULT_KEY_MULTI_SELECT_PICKED
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.RESULT_KEY_SINGLE_SELECT_PICKED
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.SingleSelectPickedData
import com.krossovochkin.fiberyunofficial.entitylist.presentation.EntityCreatedData
import com.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentDirections
import com.krossovochkin.fiberyunofficial.entitylist.presentation.FilterPickedData
import com.krossovochkin.fiberyunofficial.entitylist.presentation.RESULT_KEY_ENTITY_CREATED
import com.krossovochkin.fiberyunofficial.entitylist.presentation.RESULT_KEY_FILTER_PICKED
import com.krossovochkin.fiberyunofficial.entitylist.presentation.RESULT_KEY_SORT_PICKED
import com.krossovochkin.fiberyunofficial.entitylist.presentation.SortPickedData
import com.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentDirections
import com.krossovochkin.fiberyunofficial.login.presentation.LoginFragmentDirections

class MainActivity : AppCompatActivity(), MainActivityListener {

    private val mainActivityComponent: MainActivityComponent by lazy {
        DaggerMainActivityComponent.factory()
            .create(
                applicationComponent = (applicationContext as App).applicationComponent
            )
    }

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
                when (entityTypeSchema.name) {
                    FiberyApiConstants.Type.FILE.value -> {
                        EntityDetailsFragmentDirections.actionEntityDetailsToFileListFragment(
                            entityType = entityTypeSchema,
                            parentEntityData = parentEntityData
                        ) to FragmentNavigatorExtras(
                            itemView to getString(R.string.file_list_root_transition_name)
                        )
                    }
                    FiberyApiConstants.Type.COMMENT.value -> {
                        EntityDetailsFragmentDirections.actionEntityDetailsToCommentListFragment(
                            entityType = entityTypeSchema,
                            parentEntityData = parentEntityData
                        ) to FragmentNavigatorExtras(
                            itemView to getString(R.string.comment_list_root_transition_name)
                        )
                    }
                    else -> {
                        EntityDetailsFragmentDirections.actionEntityDetailsToEntityList(
                            entityType = entityTypeSchema,
                            parentEntityData = parentEntityData
                        ) to FragmentNavigatorExtras(
                            itemView to getString(R.string.entity_list_root_transition_name)
                        )
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
                    view to getString(R.string.picker_entity_root_transition_name)
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
                itemView to getString(R.string.picker_entity_root_transition_name)
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
        setFragmentResult(
            RESULT_KEY_SINGLE_SELECT_PICKED,
            SingleSelectPickedData(fieldSchema, item)
        )
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
        setFragmentResult(
            RESULT_KEY_MULTI_SELECT_PICKED,
            MultiSelectPickedData(fieldSchema, addedItems, removedItems)
        )
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
        setFragmentResult(
            RESULT_KEY_FILTER_PICKED,
            FilterPickedData(filter = filter, params = params)
        )
        onBackPressed()
    }

    override fun onSortEdit(
        entityTypeSchema: FiberyEntityTypeSchema,
        sort: String,
        view: View
    ) {
        binding.navHostFragment.findNavController().navigate(
            EntityListFragmentDirections
                .actionEntityListToPickerSortFragment(
                    entityTypeSchema = entityTypeSchema,
                    sort = sort
                ),
            FragmentNavigatorExtras(
                view to getString(R.string.picker_sort_root_transition_name)
            )
        )
    }

    override fun onSortSelected(sort: String) {
        setFragmentResult(
            RESULT_KEY_SORT_PICKED,
            SortPickedData(sort = sort)
        )
        onBackPressed()
    }

    private fun setFragmentResult(requestKey: String, result: Parcelable) {
        supportFragmentManager.findFragmentById(R.id.navHostFragment)!!
            .childFragmentManager.setFragmentResult(
                requestKey,
                result.toResultBundle()
            )
    }
}
