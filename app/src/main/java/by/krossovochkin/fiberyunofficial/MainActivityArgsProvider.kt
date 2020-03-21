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
package by.krossovochkin.fiberyunofficial

import android.os.Bundle
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragmentArgs
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentArgs
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentArgs
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragmentArgs
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentArgs

class MainActivityArgsProvider :
    EntityTypeListFragment.ArgsProvider,
    EntityListFragment.ArgsProvider,
    EntityDetailsFragment.ArgsProvider,
    EntityCreateFragment.ArgsProvider,
    EntityPickerFragment.ArgsProvider {

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

    override fun getEntityCreateArgs(arguments: Bundle): EntityCreateFragment.Args {
        val args = EntityCreateFragmentArgs.fromBundle(arguments)
        return EntityCreateFragment.Args(
            entityTypeSchema = args.entityType,
            entityParams = if (args.entity != null && args.fieldSchema != null) {
                args.fieldSchema to args.entity
            } else {
                null
            }
        )
    }

    override fun getEntityPickerArgs(arguments: Bundle): EntityPickerFragment.Args {
        val args = EntityPickerFragmentArgs.fromBundle(arguments)
        return EntityPickerFragment.Args(
            fieldSchema = args.fieldSchema,
            entity = args.entity
        )
    }
}
