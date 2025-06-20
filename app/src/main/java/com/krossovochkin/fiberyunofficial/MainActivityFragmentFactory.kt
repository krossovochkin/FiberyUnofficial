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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.krossovochkin.commentlist.presentation.CommentListFragment
import com.krossovochkin.commentlist.presentation.CommentListFragmentArgs
import com.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import com.krossovochkin.fiberyunofficial.di.applist.DaggerAppListComponent
import com.krossovochkin.fiberyunofficial.di.commentlist.DaggerCommentListComponent
import com.krossovochkin.fiberyunofficial.di.entitycreate.DaggerEntityCreateComponent
import com.krossovochkin.fiberyunofficial.di.entitydetails.DaggerEntityDetailsComponent
import com.krossovochkin.fiberyunofficial.di.entitylist.DaggerEntityListComponent
import com.krossovochkin.fiberyunofficial.di.entitytypelist.DaggerEntityTypeListComponent
import com.krossovochkin.fiberyunofficial.di.filelist.DaggerFileListComponent
import com.krossovochkin.fiberyunofficial.di.login.DaggerLoginComponent
import com.krossovochkin.fiberyunofficial.di.pickerentity.DaggerEntityPickerComponent
import com.krossovochkin.fiberyunofficial.di.pickerfilter.DaggerPickerFilterComponent
import com.krossovochkin.fiberyunofficial.di.pickermultiselect.DaggerPickerMultiSelectComponent
import com.krossovochkin.fiberyunofficial.di.pickersingleselect.DaggerPickerSingleSelectComponent
import com.krossovochkin.fiberyunofficial.di.pickersort.DaggerPickerSortComponent
import com.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import com.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragmentArgs
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentArgs
import com.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import com.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentArgs
import com.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import com.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragmentArgs
import com.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import com.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentArgs
import com.krossovochkin.fiberyunofficial.login.presentation.LoginFragment
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterFragment
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterFragmentArgs
import com.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectDialogFragment
import com.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectDialogFragmentArgs
import com.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectDialogFragment
import com.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectDialogFragmentArgs
import com.krossovochkin.fiberyunofficial.pickersort.presentation.PickerSortFragment
import com.krossovochkin.fiberyunofficial.pickersort.presentation.PickerSortFragmentArgs
import com.krossovochkin.filelist.presentation.FileListFragment
import com.krossovochkin.filelist.presentation.FileListFragmentArgs

class MainActivityFragmentFactory(
    private val mainActivityComponent: MainActivityComponent
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            LoginFragment::class.java.canonicalName -> {
                instantiateLoginFragment()
            }
            AppListFragment::class.java.canonicalName -> {
                instantiateAppListFragment()
            }
            EntityTypeListFragment::class.java.canonicalName -> {
                instantiateEntityTypeListFragment()
            }
            EntityListFragment::class.java.canonicalName -> {
                instantiateEntityListFragment()
            }
            EntityDetailsFragment::class.java.canonicalName -> {
                instantiateEntityDetailsFragment()
            }
            EntityCreateFragment::class.java.canonicalName -> {
                instantiateEntityCreateFragment()
            }
            EntityPickerFragment::class.java.canonicalName -> {
                instantiateEntityPickerFragment()
            }
            PickerSingleSelectDialogFragment::class.java.canonicalName -> {
                instantiatePickerSingleSelectFragment()
            }
            PickerMultiSelectDialogFragment::class.java.canonicalName -> {
                instantiatePickerMultiSelectFragment()
            }
            PickerFilterFragment::class.java.canonicalName -> {
                instantiatePickerFilterFragment()
            }
            PickerSortFragment::class.java.canonicalName -> {
                instantiatePickerSortFragment()
            }
            FileListFragment::class.java.canonicalName -> {
                instantiateFileListFragment()
            }
            CommentListFragment::class.java.canonicalName -> {
                instantiateCommentListFragment()
            }
            else -> return super.instantiate(classLoader, className)
        }
    }

    private fun instantiatePickerFilterFragment(): Fragment {
        return instantiate { argsExtractor ->
            PickerFilterFragment(
                factoryProvider = DaggerPickerFilterComponent.factory()
                    .create(
                        pickerFilterParentComponent = mainActivityComponent,
                        argsProvider = {
                            val args = PickerFilterFragmentArgs
                                .fromBundle(argsExtractor.extract())
                            PickerFilterFragment.Args(
                                entityTypeSchema = args.entityTypeSchema,
                                filter = args.filter
                            )
                        }
                    )
                    .viewModelFactoryProducer()
            )
        }
    }

    private fun instantiatePickerSortFragment(): Fragment {
        return instantiate { argsExtractor ->
            PickerSortFragment(
                factoryProvider = DaggerPickerSortComponent.factory()
                    .create(
                        pickerSortParentComponent = mainActivityComponent,
                        argsProvider = {
                            val args = PickerSortFragmentArgs
                                .fromBundle(argsExtractor.extract())
                            PickerSortFragment.Args(
                                entityTypeSchema = args.entityTypeSchema,
                                sort = args.sort
                            )
                        }
                    )
                    .viewModelFactoryProducer()
            )
        }
    }

    private fun instantiatePickerMultiSelectFragment(): Fragment {
        return instantiate { argsExtractor ->
            PickerMultiSelectDialogFragment(
                factoryProducer = DaggerPickerMultiSelectComponent.factory()
                    .create(
                        pickerMultiSelectParentComponent = mainActivityComponent,
                        argsProvider = {
                            val args = PickerMultiSelectDialogFragmentArgs
                                .fromBundle(argsExtractor.extract())
                            PickerMultiSelectDialogFragment.Args(
                                item = args.item,
                                parentEntityData = args.parentEntityData
                            )
                        }
                    )
                    .viewModelFactoryProducer()
            )
        }
    }

    private fun instantiatePickerSingleSelectFragment(): Fragment {
        return instantiate { argsExtractor ->
            PickerSingleSelectDialogFragment(
                factoryProducer = DaggerPickerSingleSelectComponent.factory()
                    .create(
                        pickerSingleSelectParentComponent = mainActivityComponent,
                        argsProvider = {
                            val args = PickerSingleSelectDialogFragmentArgs
                                .fromBundle(argsExtractor.extract())
                            PickerSingleSelectDialogFragment.Args(
                                item = args.item,
                                parentEntityData = args.parentEntityData
                            )
                        }
                    )
                    .viewModelFactoryProducer()
            )
        }
    }

    private fun instantiateEntityPickerFragment(): Fragment {
        return instantiate { argsExtractor ->
            EntityPickerFragment(
                factoryProducer = DaggerEntityPickerComponent.factory()
                    .create(
                        entityPickerParentComponent = mainActivityComponent,
                        argsProvider = {
                            val args = EntityPickerFragmentArgs
                                .fromBundle(argsExtractor.extract())
                            EntityPickerFragment.Args(
                                parentEntityData = args.parentEntityData,
                                entity = args.currentEntity
                            )
                        }
                    )
                    .viewModelFactoryProducer()
            )
        }
    }

    private fun instantiateEntityCreateFragment(): Fragment {
        return instantiate { argsExtractor ->
            EntityCreateFragment(
                factoryProducer = DaggerEntityCreateComponent.factory()
                    .create(
                        entityCreateParentComponent = mainActivityComponent,
                        argsProvider = {
                            val args = EntityCreateFragmentArgs
                                .fromBundle(argsExtractor.extract())
                            EntityCreateFragment.Args(
                                entityTypeSchema = args.entityType
                            )
                        }
                    )
                    .viewModelFactoryProducer()
            )
        }
    }

    private fun instantiateEntityDetailsFragment(): Fragment {
        return instantiate { argsExtractor ->
            val argsProvider = EntityDetailsFragment.ArgsProvider {
                val args = EntityDetailsFragmentArgs.fromBundle(argsExtractor.extract())
                EntityDetailsFragment.Args(
                    entityData = args.entity
                )
            }

            EntityDetailsFragment(
                factoryProducer = DaggerEntityDetailsComponent.factory()
                    .create(
                        entityDetailsParentComponent = mainActivityComponent,
                        argsProvider = argsProvider
                    )
                    .viewModelFactoryProducer(),
                argsProvider = argsProvider
            )
        }
    }

    private fun instantiateEntityListFragment(): Fragment {
        return instantiate { argsExtractor ->
            EntityListFragment(
                DaggerEntityListComponent.factory()
                    .create(
                        entityListParentComponent = mainActivityComponent,
                        argsProvider = {
                            val args = EntityListFragmentArgs
                                .fromBundle(argsExtractor.extract())
                            EntityListFragment.Args(
                                entityTypeSchema = args.entityType,
                                parentEntityData = args.parentEntityData
                            )
                        }
                    )
                    .viewModelFactoryProducer()
            )
        }
    }

    private fun instantiateEntityTypeListFragment(): Fragment {
        return instantiate { argsExtractor ->
            EntityTypeListFragment(
                factoryProducer = DaggerEntityTypeListComponent.factory()
                    .create(
                        entityTypeListParentComponent = mainActivityComponent,
                        argsProvider = {
                            val args = EntityTypeListFragmentArgs
                                .fromBundle(argsExtractor.extract())
                            EntityTypeListFragment.Args(
                                fiberyAppData = args.fiberyApp
                            )
                        }
                    )
                    .viewModelFactoryProducer()
            )
        }
    }

    private fun instantiateAppListFragment(): AppListFragment {
        return AppListFragment(
            factoryProducer = DaggerAppListComponent.factory()
                .create(appListParentComponent = mainActivityComponent)
                .viewModelFactory()
        )
    }

    private fun instantiateLoginFragment(): LoginFragment {
        return LoginFragment(
            factoryProducer = DaggerLoginComponent.factory()
                .create(loginParentComponent = mainActivityComponent)
                .viewModelFactoryProducer()
        )
    }

    private fun instantiateFileListFragment(): Fragment {
        return instantiate { argsExtractor ->
            FileListFragment(
                factoryProducer = DaggerFileListComponent.factory()
                    .create(
                        fileListParentComponent = mainActivityComponent,
                        argsProvider = {
                            val args = FileListFragmentArgs
                                .fromBundle(argsExtractor.extract())
                            FileListFragment.Args(
                                entityTypeSchema = args.entityType,
                                parentEntityData = args.parentEntityData
                            )
                        }
                    )
                    .viewModelFactoryProducer()
            )
        }
    }

    private fun instantiateCommentListFragment(): Fragment {
        return instantiate { argsExtractor ->
            CommentListFragment(
                factoryProducer = DaggerCommentListComponent.factory()
                    .create(
                        commentListParentComponent = mainActivityComponent,
                        argsProvider = {
                            val args = CommentListFragmentArgs
                                .fromBundle(argsExtractor.extract())
                            CommentListFragment.Args(
                                entityTypeSchema = args.entityType,
                                parentEntityData = args.parentEntityData
                            )
                        }
                    )
                    .viewModelFactoryProducer()
            )
        }
    }

    private inline fun instantiate(create: (ArgsExtractor) -> Fragment): Fragment {
        val argsExtractor = ArgsExtractor()
        val fragment = create(argsExtractor)
        argsExtractor.fragment = fragment
        return fragment
    }
}

private class ArgsExtractor {

    var fragment: Fragment? = null

    fun extract(): Bundle {
        return fragment!!.requireArguments()
    }
}
