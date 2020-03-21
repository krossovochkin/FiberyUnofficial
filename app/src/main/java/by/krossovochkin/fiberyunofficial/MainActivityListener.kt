package by.krossovochkin.fiberyunofficial

import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.login.presentation.LoginFragment

interface MainActivityListener :
    LoginFragment.ParentListener,
    AppListFragment.ParentListener,
    EntityTypeListFragment.ParentListener,
    EntityListFragment.ParentListener,
    EntityDetailsFragment.ParentListener,
    EntityCreateFragment.ParentListener,
    EntityPickerFragment.ParentListener
