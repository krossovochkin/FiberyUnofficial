package by.krossovochkin.fiberyunofficial.entitylist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.R
import by.krossovochkin.fiberyunofficial.app.App
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitylist.EntityListComponentFactory
import by.krossovochkin.fiberyunofficial.utils.presentation.ColorUtils
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.fragment_entity_list.*
import kotlinx.android.synthetic.main.item_entity.*
import javax.inject.Inject

class EntityListFragment : BaseFragment(R.layout.fragment_entity_list) {

    @Inject
    lateinit var viewModel: EntityListViewModel
    private val args: EntityListFragmentArgs by navArgs()
    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateLayoutContainer<EntityListItem, ListItem>(
            layout = R.layout.item_entity
        ) {
            bind {
                itemView.setOnClickListener { viewModel.select(item) }
                entityTitleTextView.text = item.title
            }
        }
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        EntityListComponentFactory
            .create(
                fragment = this,
                fiberyEntityTypeSchema = args.entityType,
                entityListGlobalDependencies = (context!!.applicationContext as App).applicationComponent
            )
            .inject(this)

        entityListRecyclerView.layoutManager = LinearLayoutManager(context)
        entityListRecyclerView.adapter = adapter
        entityListRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        viewModel.entityTypeItems.observe(this, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        initToolbar(
            toolbar = entityListToolbar,
            title = args.entityType.displayName,
            bgColor = ColorUtils.getColor(args.entityType.uiColorHex)
        )
    }
}
