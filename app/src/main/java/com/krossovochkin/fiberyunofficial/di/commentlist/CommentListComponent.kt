package com.krossovochkin.fiberyunofficial.di.commentlist

import com.krossovochkin.commentlist.presentation.CommentListFragment
import com.krossovochkin.commentlist.presentation.CommentListViewModelFactory
import com.krossovochkin.fiberyunofficial.GlobalDependencies
import dagger.BindsInstance
import dagger.Component
import dagger.Lazy
import javax.inject.Scope

interface CommentListParentComponent : GlobalDependencies

@CommentList
@Component(
    dependencies = [
        CommentListParentComponent::class
    ]
)
interface CommentListComponent {

    fun viewModelFactory(): Lazy<CommentListViewModelFactory>

    @Component.Factory
    interface Factory {

        fun create(
            commentListParentComponent: CommentListParentComponent,
            @BindsInstance argsProvider: CommentListFragment.ArgsProvider
        ): CommentListComponent
    }
}

@Scope
@Retention
annotation class CommentList
