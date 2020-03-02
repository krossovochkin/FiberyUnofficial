package by.krossovochkin.fiberyunofficial

import android.os.Bundle
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentArgs
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentArgs
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentArgs

class MainActivityArgsProvider :
    EntityTypeListFragment.ArgsProvider,
    EntityListFragment.ArgsProvider,
    EntityDetailsFragment.ArgsProvider {

    override fun getEntityTypeListArgs(arguments: Bundle): EntityTypeListFragment.Args {
        val args = EntityTypeListFragmentArgs.fromBundle(arguments)
        return EntityTypeListFragment.Args(
            fiberyAppData = args.fiberyApp
        )
    }

    override fun getEntityListArgs(arguments: Bundle): EntityListFragment.Args {
        val args = EntityListFragmentArgs.fromBundle(arguments)
        return EntityListFragment.Args(
            entityTypeSchema = args.entityType,
            entityParams = if (args.field != null && args.entity != null) {
                args.field to args.entity
            } else {
                null
            }
        )
    }

    override fun getEntityDetailsArgs(arguments: Bundle): EntityDetailsFragment.Args {
        val args = EntityDetailsFragmentArgs.fromBundle(arguments)
        return EntityDetailsFragment.Args(
            entityData = args.entity
        )
    }
}
