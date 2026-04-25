package com.krossovochkin.fiberyunofficial.ui

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.DialogSceneStrategy
import com.krossovochkin.commentlist.presentation.CommentListScreen
import com.krossovochkin.commentlist.presentation.CommentListViewModel
import com.krossovochkin.fiberyunofficial.applist.presentation.AppListScreen
import com.krossovochkin.fiberyunofficial.applist.presentation.AppListViewModel
import com.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateScreen
import com.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateViewModel
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsScreen
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsViewModel
import com.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListScreen
import com.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListViewModel
import com.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerScreen
import com.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerViewModel
import com.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListScreen
import com.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListViewModel
import com.krossovochkin.fiberyunofficial.login.presentation.LoginScreen
import com.krossovochkin.fiberyunofficial.login.presentation.LoginViewModel
import com.krossovochkin.fiberyunofficial.navigation.AppListNavKey
import com.krossovochkin.fiberyunofficial.navigation.CommentListNavKey
import com.krossovochkin.fiberyunofficial.navigation.EntityCreateNavKey
import com.krossovochkin.fiberyunofficial.navigation.EntityDetailsNavKey
import com.krossovochkin.fiberyunofficial.navigation.EntityListNavKey
import com.krossovochkin.fiberyunofficial.navigation.EntityPickerNavKey
import com.krossovochkin.fiberyunofficial.navigation.EntityTypeListNavKey
import com.krossovochkin.fiberyunofficial.navigation.FileListNavKey
import com.krossovochkin.fiberyunofficial.navigation.LoginNavKey
import com.krossovochkin.fiberyunofficial.navigation.NavigationViewModel
import com.krossovochkin.fiberyunofficial.navigation.PickerFilterNavKey
import com.krossovochkin.fiberyunofficial.navigation.PickerMultiSelectNavKey
import com.krossovochkin.fiberyunofficial.navigation.PickerSingleSelectNavKey
import com.krossovochkin.fiberyunofficial.navigation.PickerSortNavKey
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterScreen
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterViewModel
import com.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectScreen
import com.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectScreen
import com.krossovochkin.fiberyunofficial.pickersort.presentation.PickerSortScreen
import com.krossovochkin.fiberyunofficial.pickersort.presentation.PickerSortViewModel
import com.krossovochkin.filelist.presentation.FileListScreen
import com.krossovochkin.filelist.presentation.FileListViewModel

class FiberyEntryProvider(
    private val navigationViewModel: NavigationViewModel
) {
    val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider<NavKey> {
        entry<LoginNavKey> { key ->
            NavEntry(key) {
                LoginScreen(
                    viewModel = hiltViewModel<LoginViewModel, LoginViewModel.Factory> {
                            factory ->
                        factory.create()
                    },
                    onLoginSuccess = { navigationViewModel.onLoginSuccess() }
                )
            }
        }
        entry<AppListNavKey> { key ->
            NavEntry(key) {
                AppListScreen(
                    viewModel = hiltViewModel<AppListViewModel, AppListViewModel.Factory> {
                            factory ->
                        factory.create()
                    },
                    onAppSelected = { navigationViewModel.onAppSelected(it) }
                )
            }
        }
        entry<EntityTypeListNavKey> { key ->
            NavEntry(key) {
                EntityTypeListScreen(
                    viewModel = hiltViewModel<EntityTypeListViewModel, EntityTypeListViewModel.Factory> {
                            factory ->
                        factory.create(key)
                    },
                    onBack = { navigationViewModel.pop() },
                    onEntityTypeSelected = { navigationViewModel.onEntityTypeSelected(it) }
                )
            }
        }
        entry<EntityListNavKey> { key ->
            NavEntry(key) {
                EntityListScreen(
                    viewModel = hiltViewModel<EntityListViewModel, EntityListViewModel.Factory> {
                            factory ->
                        factory.create(key)
                    },
                    onBack = { navigationViewModel.pop() },
                    onEntitySelected = { navigationViewModel.onEntitySelected(it) },
                    onFilterEdit = { type, filter -> navigationViewModel.onFilterEdit(type, filter) },
                    onSortEdit = { type, sort -> navigationViewModel.onSortEdit(type, sort) },
                    onCreateEntity = { type, parent -> navigationViewModel.onAddEntityRequested(type, parent) }
                )
            }
        }
        entry<EntityDetailsNavKey> { key ->
            NavEntry(key) {
                EntityDetailsScreen(
                    viewModel = hiltViewModel<EntityDetailsViewModel, EntityDetailsViewModel.Factory> {
                            factory ->
                        factory.create(key)
                    },
                    onBack = { navigationViewModel.pop() },
                    onEntitySelected = { navigationViewModel.onEntitySelected(it) },
                    onEntityFieldEdit = { parent, entity -> navigationViewModel.onEntityFieldEdit(parent, entity) },
                    onEntityTypeSelected = { type, parent -> navigationViewModel.onEntityTypeSelected(type, parent) },
                    onSingleSelectFieldEdit = { parent, item -> navigationViewModel.onSingleSelectFieldEdit(parent, item) },
                    onMultiSelectFieldEdit = { parent, item -> navigationViewModel.onMultiSelectFieldEdit(parent, item) }
                )
            }
        }
        entry<EntityCreateNavKey> { key ->
            NavEntry(key) {
                EntityCreateScreen(
                    viewModel = hiltViewModel<EntityCreateViewModel, EntityCreateViewModel.Factory> {
                            factory ->
                        factory.create(key)
                    },
                    onBack = { navigationViewModel.pop() },
                    onEntityCreateSuccess = { navigationViewModel.onEntityCreateSuccess() }
                )
            }
        }
        entry<FileListNavKey> { key ->
            NavEntry(key) {
                FileListScreen(
                    viewModel = hiltViewModel<FileListViewModel, FileListViewModel.Factory> {
                            factory ->
                        factory.create(key)
                    },
                    onBack = { navigationViewModel.pop() }
                )
            }
        }
        entry<CommentListNavKey> { key ->
            NavEntry(key) {
                CommentListScreen(
                    viewModel = hiltViewModel<CommentListViewModel, CommentListViewModel.Factory> {
                            factory ->
                        factory.create(key)
                    },
                    markwon = null,
                    onBack = { navigationViewModel.pop() }
                )
            }
        }
        entry<PickerFilterNavKey>(
            metadata = DialogSceneStrategy.dialog()
        ) { key ->
            NavEntry(key) {
                PickerFilterScreen(
                    viewModel = hiltViewModel<PickerFilterViewModel, PickerFilterViewModel.Factory> {
                            factory ->
                        factory.create(key)
                    },
                    onBack = { navigationViewModel.pop() },
                    onFilterApply = { type, filter -> navigationViewModel.onFilterSelected(type, filter) }
                )
            }
        }
        entry<PickerSortNavKey>(
            metadata = DialogSceneStrategy.dialog()
        ) { key ->
            NavEntry(key) {
                PickerSortScreen(
                    viewModel = hiltViewModel<PickerSortViewModel, PickerSortViewModel.Factory> {
                            factory ->
                        factory.create(key)
                    },
                    onBack = { navigationViewModel.pop() },
                    onSortApply = { type, sort -> navigationViewModel.onSortSelected(type, sort) }
                )
            }
        }
        entry<EntityPickerNavKey>(
            metadata = DialogSceneStrategy.dialog()
        ) { key ->
            NavEntry(key) {
                EntityPickerScreen(
                    viewModel = hiltViewModel<EntityPickerViewModel, EntityPickerViewModel.Factory> {
                            factory ->
                        factory.create(key)
                    },
                    onBack = { navigationViewModel.pop() },
                    onEntityPicked = { parentEntityData, entity ->
                        navigationViewModel.onEntityPicked(parentEntityData, entity)
                    }
                )
            }
        }
        entry<PickerSingleSelectNavKey>(
            metadata = DialogSceneStrategy.dialog()
        ) { key ->
            NavEntry(key) {
                PickerSingleSelectScreen(
                    item = key.item,
                    onConfirm = { selectedValue ->
                        navigationViewModel.onSingleSelectPicked(key.parentEntityData, selectedValue)
                    },
                    onDismiss = { navigationViewModel.pop() }
                )
            }
        }
        entry<PickerMultiSelectNavKey>(
            metadata = DialogSceneStrategy.dialog()
        ) { key ->
            NavEntry(key) {
                PickerMultiSelectScreen(
                    item = key.item,
                    onConfirm = { addedItems, removedItems ->
                        navigationViewModel.onMultiSelectPicked(
                            key.parentEntityData,
                            addedItems,
                            removedItems
                        )
                    },
                    onDismiss = { navigationViewModel.pop() }
                )
            }
        }
    }
}
