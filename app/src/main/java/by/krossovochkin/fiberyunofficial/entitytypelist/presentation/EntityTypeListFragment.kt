package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.R
import by.krossovochkin.fiberyunofficial.app.App
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListComponentFactory
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.fragment_entity_type_list.*
import kotlinx.android.synthetic.main.item_entity_type.*
import javax.inject.Inject

class EntityTypeListFragment : BaseFragment(R.layout.fragment_entity_type_list) {

    @Inject
    lateinit var viewModel: EntityTypeListViewModel
    private val args: EntityTypeListFragmentArgs by navArgs()
    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateLayoutContainer<EntityTypeListItem, ListItem>(
            layout = R.layout.item_entity_type
        ) {
            bind {
                itemView.setOnClickListener { viewModel.select(item) }
                entityTypeTitleTextView.text = item.title
                entityTypeBadgeView.setBackgroundColor(item.badgeBgColor)
            }
        }
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        EntityTypeListComponentFactory
            .create(
                fragment = this,
                fiberyAppData = args.fiberyApp,
                entityTypeListGlobalDependencies = (context!!.applicationContext as App).applicationComponent
            )
            .inject(this)

        entityTypeListRecyclerView.layoutManager = LinearLayoutManager(context)
        entityTypeListRecyclerView.adapter = adapter
        entityTypeListRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL)
        )

        viewModel.entityTypeItems.observe(this, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        initToolbar(
            toolbar = entityTypeListToolbar,
            title = context!!.getString(R.string.title_entity_type_list),
            bgColor = ContextCompat.getColor(
                context!!,
                R.color.colorPrimary
            )
        )
    }
}





