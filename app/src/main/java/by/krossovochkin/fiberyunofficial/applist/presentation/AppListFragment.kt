package by.krossovochkin.fiberyunofficial.applist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.R
import by.krossovochkin.fiberyunofficial.app.App
import by.krossovochkin.fiberyunofficial.applist.AppListComponentFactory
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.fragment_app_list.*
import kotlinx.android.synthetic.main.item_app.*
import javax.inject.Inject

class AppListFragment : BaseFragment(R.layout.fragment_app_list) {

    @Inject
    lateinit var viewModel: AppListViewModel

    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateLayoutContainer<AppListItem, ListItem>(
            layout = R.layout.item_app
        ) {
            bind {
                itemView.setOnClickListener { viewModel.select(item) }
                appTitleTextView.text = item.title
            }
        }
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        AppListComponentFactory
            .create(
                fragment = this,
                appListGlobalDependencies = (context!!.applicationContext as App).applicationComponent
            )
            .inject(this)

        appListRecyclerView.layoutManager = LinearLayoutManager(context)
        appListRecyclerView.adapter = adapter
        appListRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        viewModel.appItems.observe(this, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        initToolbar(
            toolbar = appListToolbar,
            title = context!!.getString(R.string.title_app_list),
            bgColor = ContextCompat.getColor(
                context!!,
                R.color.colorPrimary
            )
        )
    }
}




