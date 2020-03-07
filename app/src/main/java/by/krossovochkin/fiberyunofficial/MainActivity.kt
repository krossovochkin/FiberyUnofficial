package by.krossovochkin.fiberyunofficial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.navigation.findNavController
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragmentDirections
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.databinding.ActivityMainBinding
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentDirections
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentDirections
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentDirections
import by.krossovochkin.fiberyunofficial.login.presentation.LoginFragmentDirections

class MainActivity : AppCompatActivity(), MainActivityListener {

    private val mainActivityComponent: MainActivityComponent by lazy {
        DaggerMainActivityComponent.builder()
            .applicationComponent((applicationContext as App).applicationComponent)
            .mainActivityArgsProvider(argsProvider)
            .build()
    }
    private val argsProvider: MainActivityArgsProvider = MainActivityArgsProvider()

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        supportFragmentManager.fragmentFactory = MainActivityFragmentFactory(mainActivityComponent)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onAppSelected(fiberyAppData: FiberyAppData) {
        binding.navHostFragment.findNavController().navigate(
            AppListFragmentDirections.actionAppListToEntityTypeList(fiberyAppData)
        )
    }

    @Suppress("UseIfInsteadOfWhen")
    override fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema) {
        val navController = binding.navHostFragment.findNavController()
        val directions = when (val id = navController.currentDestination?.id) {
            R.id.entityTypeList -> {
                EntityTypeListFragmentDirections.actionEntityTypeListToEntityList(
                    entityType = entityTypeSchema,
                    entity = null,
                    field = null
                )
            }
            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions)
    }

    @Suppress("UseIfInsteadOfWhen")
    override fun onEntityTypeSelected(
        entityTypeSchema: FiberyEntityTypeSchema,
        entity: FiberyEntityData,
        fieldSchema: FiberyFieldSchema
    ) {
        val navController = binding.navHostFragment.findNavController()
        val directions = when (val id = navController.currentDestination?.id) {
            R.id.entityDetails -> {
                EntityDetailsFragmentDirections.actionEntityDetailsToEntityList(
                    entityType = entityTypeSchema,
                    entity = entity,
                    field = fieldSchema
                )
            }
            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions)
    }

    override fun onEntitySelected(entity: FiberyEntityData) {
        val navController = binding.navHostFragment.findNavController()
        val directions = when (val id = navController.currentDestination?.id) {
            R.id.entityList -> {
                EntityListFragmentDirections.actionEntityListToEntityDetails(entity)
            }
            R.id.entityDetails -> {
                EntityDetailsFragmentDirections.actionEntityDetailsSelf(entity)
            }
            else -> error("Unknown current direction: $id")
        }
        navController.navigate(directions)
    }

    override fun onLoginSuccess() {
        binding.navHostFragment.findNavController()
            .navigate(LoginFragmentDirections.actionLoginFragmentToAppList())
    }
}
