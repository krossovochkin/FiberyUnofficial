package com.krossovochkin.fiberyunofficial.ui

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.noties.markwon.Markwon
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
import com.krossovochkin.core.presentation.resources.toComposeColor

class FiberyEntryProvider(
    private val navigationViewModel: NavigationViewModel
) {
    val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider<NavKey> {
        entry<LoginNavKey>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) { fiberyFadeTransition() }
                put(NavDisplay.PopTransitionKey) { fiberyFadeTransition() }
                put(NavDisplay.PredictivePopTransitionKey) { fiberyFadeTransition() }
            }
        ) {
            StatusBarContrastEffect(MaterialTheme.colorScheme.surface)
            LoginScreen(
                viewModel = hiltViewModel<LoginViewModel, LoginViewModel.Factory> { factory ->
                    factory.create()
                },
                onLoginSuccess = { navigationViewModel.onLoginSuccess() }
            )
        }
        entry<AppListNavKey>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) { fiberyFadeTransition() }
                put(NavDisplay.PopTransitionKey) { fiberyFadeTransition() }
                put(NavDisplay.PredictivePopTransitionKey) { fiberyFadeTransition() }
            }
        ) {
            val appListViewModel =
                hiltViewModel<AppListViewModel, AppListViewModel.Factory> { factory ->
                    factory.create()
                }
            StatusBarContrastEffect(appListViewModel.getToolbarViewState().bgColor.toComposeColor())
            AppListScreen(
                viewModel = appListViewModel,
                onAppSelected = { navigationViewModel.onAppSelected(it) }
            )
        }
        entry<EntityTypeListNavKey> { key ->
            val entityTypeListViewModel =
                hiltViewModel<EntityTypeListViewModel, EntityTypeListViewModel.Factory> { factory ->
                    factory.create(key)
                }
            StatusBarContrastEffect(entityTypeListViewModel.getToolbarViewState().bgColor.toComposeColor())
            EntityTypeListScreen(
                viewModel = entityTypeListViewModel,
                onBack = { navigationViewModel.pop() },
                onEntityTypeSelected = { navigationViewModel.onEntityTypeSelected(it) }
            )
        }
        entry<EntityListNavKey> { key ->
            val entityListViewModel =
                hiltViewModel<EntityListViewModel, EntityListViewModel.Factory> { factory ->
                    factory.create(key)
                }
            StatusBarContrastEffect(entityListViewModel.toolbarViewState.bgColor.toComposeColor())
            EntityListScreen(
                viewModel = entityListViewModel,
                onBack = { navigationViewModel.pop() },
                onEntitySelected = { navigationViewModel.onEntitySelected(it) },
                onFilterEdit = { type, filter -> navigationViewModel.onFilterEdit(type, filter) },
                onSortEdit = { type, sort -> navigationViewModel.onSortEdit(type, sort) },
                onCreateEntity = { type, parent -> navigationViewModel.onAddEntityRequested(type, parent) }
            )
        }
        entry<EntityDetailsNavKey> { key ->
            val entityDetailsViewModel =
                hiltViewModel<EntityDetailsViewModel, EntityDetailsViewModel.Factory> { factory ->
                    factory.create(key)
                }
            StatusBarContrastEffect(entityDetailsViewModel.toolbarViewState.bgColor.toComposeColor())
            EntityDetailsScreen(
                viewModel = entityDetailsViewModel,
                onBack = { navigationViewModel.pop() },
                onEntitySelected = { navigationViewModel.onEntitySelected(it) },
                onEntityFieldEdit = { parent, entity -> navigationViewModel.onEntityFieldEdit(parent, entity) },
                onEntityTypeSelected = { type, parent -> navigationViewModel.onEntityTypeSelected(type, parent) },
                onSingleSelectFieldEdit = { parent, item ->
                    navigationViewModel.onSingleSelectFieldEdit(parent, item)
                },
                onMultiSelectFieldEdit = { parent, item ->
                    navigationViewModel.onMultiSelectFieldEdit(parent, item)
                },
                onEntityFieldClear = { parent ->
                    navigationViewModel.onEntityFieldCleared(parent)
                }
            )
        }
        entry<EntityCreateNavKey>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) { fiberyModalForwardTransition() }
                put(NavDisplay.PopTransitionKey) { fiberyModalPopTransition() }
                put(NavDisplay.PredictivePopTransitionKey) { fiberyModalPredictivePopTransition() }
            }
        ) { key ->
            val entityCreateViewModel =
                hiltViewModel<EntityCreateViewModel, EntityCreateViewModel.Factory> { factory ->
                    factory.create(key)
                }
            StatusBarContrastEffect(entityCreateViewModel.toolbarViewState.bgColor.toComposeColor())
            EntityCreateScreen(
                viewModel = entityCreateViewModel,
                onBack = { navigationViewModel.pop() },
                onEntityCreateSuccess = { navigationViewModel.onEntityCreateSuccess() }
            )
        }
        entry<FileListNavKey> { key ->
            val fileListViewModel =
                hiltViewModel<FileListViewModel, FileListViewModel.Factory> { factory ->
                    factory.create(key)
                }
            StatusBarContrastEffect(fileListViewModel.toolbarViewState.bgColor.toComposeColor())
            FileListScreen(
                viewModel = fileListViewModel,
                onBack = { navigationViewModel.pop() }
            )
        }
        entry<CommentListNavKey> { key ->
            val context = LocalContext.current
            val markwon = remember(context) { Markwon.create(context) }
            val commentListViewModel =
                hiltViewModel<CommentListViewModel, CommentListViewModel.Factory> { factory ->
                    factory.create(key)
                }
            StatusBarContrastEffect(commentListViewModel.toolbarViewState.bgColor.toComposeColor())
            CommentListScreen(
                viewModel = commentListViewModel,
                markwon = markwon,
                onBack = { navigationViewModel.pop() }
            )
        }
        entry<PickerFilterNavKey>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) { fiberyModalForwardTransition() }
                put(NavDisplay.PopTransitionKey) { fiberyModalPopTransition() }
                put(NavDisplay.PredictivePopTransitionKey) { fiberyModalPredictivePopTransition() }
            }
        ) { key ->
            val pickerFilterViewModel =
                hiltViewModel<PickerFilterViewModel, PickerFilterViewModel.Factory> { factory ->
                    factory.create(key)
                }
            StatusBarContrastEffect(pickerFilterViewModel.toolbarViewState.bgColor.toComposeColor())
            PickerFilterScreen(
                viewModel = pickerFilterViewModel,
                onBack = { navigationViewModel.pop() },
                onFilterApply = { type, filter -> navigationViewModel.onFilterSelected(type, filter) }
            )
        }
        entry<PickerSortNavKey>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) { fiberyModalForwardTransition() }
                put(NavDisplay.PopTransitionKey) { fiberyModalPopTransition() }
                put(NavDisplay.PredictivePopTransitionKey) { fiberyModalPredictivePopTransition() }
            }
        ) { key ->
            val pickerSortViewModel =
                hiltViewModel<PickerSortViewModel, PickerSortViewModel.Factory> { factory ->
                    factory.create(key)
                }
            StatusBarContrastEffect(pickerSortViewModel.toolbarViewState.bgColor.toComposeColor())
            PickerSortScreen(
                viewModel = pickerSortViewModel,
                onBack = { navigationViewModel.pop() },
                onSortApply = { type, sort -> navigationViewModel.onSortSelected(type, sort) }
            )
        }
        entry<EntityPickerNavKey>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) { fiberyModalForwardTransition() }
                put(NavDisplay.PopTransitionKey) { fiberyModalPopTransition() }
                put(NavDisplay.PredictivePopTransitionKey) { fiberyModalPredictivePopTransition() }
            }
        ) { key ->
            EntityPickerScreen(
                viewModel = hiltViewModel<EntityPickerViewModel, EntityPickerViewModel.Factory> { factory ->
                    factory.create(key)
                },
                onBack = { navigationViewModel.pop() },
                onEntityPicked = { parentEntityData, entity ->
                    navigationViewModel.onEntityPicked(parentEntityData, entity)
                }
            )
        }
        entry<PickerSingleSelectNavKey>(
            metadata = DialogSceneStrategy.dialog()
        ) { key ->
            PickerSingleSelectScreen(
                item = key.item,
                onConfirm = { selectedValue ->
                    navigationViewModel.onSingleSelectPicked(key.parentEntityData, selectedValue)
                },
                onDismiss = { navigationViewModel.pop() }
            )
        }
        entry<PickerMultiSelectNavKey>(
            metadata = DialogSceneStrategy.dialog()
        ) { key ->
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

/**
 * Matches status bar icons to the toolbar behind them: dark icons on light
 * backgrounds and vice versa, same contrast rule as the toolbar content.
 */
private const val LIGHT_BACKGROUND_LUMINANCE_THRESHOLD = 0.5f

@Composable
private fun StatusBarContrastEffect(backgroundColor: Color) {
    val context = LocalContext.current
    SideEffect {
        val window = (context as? Activity)?.window ?: return@SideEffect
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            backgroundColor.luminance() > LIGHT_BACKGROUND_LUMINANCE_THRESHOLD
    }
}
