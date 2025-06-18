package com.krossovochkin.fiberyunofficial.di.filelist

import com.krossovochkin.auth.AuthStorage
import com.krossovochkin.fiberyunofficial.GlobalDependencies
import com.krossovochkin.filelist.presentation.FileListFragment
import com.krossovochkin.filelist.presentation.FileListViewModel
import dagger.BindsInstance
import dagger.Component
import dagger.Lazy
import javax.inject.Scope

interface FileListParentComponent : GlobalDependencies {

    fun authStorage(): AuthStorage
}

@FileList
@Component(
    dependencies = [
        FileListParentComponent::class
    ]
)
interface FileListComponent {

    fun viewModelFactory(): FileListViewModel.Factory

    @Component.Factory
    interface Factory {

        fun create(
            fileListParentComponent: FileListParentComponent
        ): FileListComponent
    }
}

@Scope
@Retention
annotation class FileList
