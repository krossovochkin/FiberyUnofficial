package com.krossovochkin.fiberyunofficial.di.commentlist

import com.krossovochkin.commentlist.presentation.CommentListViewModel
import com.krossovochkin.fiberyunofficial.GlobalDependencies
import dagger.Component
import javax.inject.Scope

interface CommentListParentComponent : GlobalDependencies

@CommentList
@Component(
    dependencies = [
        CommentListParentComponent::class
    ]
)
interface CommentListComponent {

    fun viewModelFactory(): CommentListViewModel.Factory

    @Component.Factory
    interface Factory {

        fun create(
            commentListParentComponent: CommentListParentComponent
        ): CommentListComponent
    }
}

@Scope
@Retention
annotation class CommentList
