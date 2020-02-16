package by.krossovochkin.fiberyunofficial.applist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.applist.AppListParentComponent
import by.krossovochkin.fiberyunofficial.applist.DaggerAppListComponent
import by.krossovochkin.fiberyunofficial.applist.R
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.fragment_app_list.*
import kotlinx.android.synthetic.main.item_app.*
import javax.inject.Inject

class AppListFragment(
    private val appListParentComponent: AppListParentComponent
) : BaseFragment(R.layout.fragment_app_list) {

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

        DaggerAppListComponent.builder()
            .fragment(this)
            .appListGlobalDependencies(appListParentComponent)
            .build()
            .inject(this)

        appListRecyclerView.layoutManager = LinearLayoutManager(context)
        appListRecyclerView.adapter = adapter
        appListRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        viewModel.appItems.observe(viewLifecycleOwner, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        viewModel.progress.observe(viewLifecycleOwner, Observer {
            progressBar.isVisible = it
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { error ->
                Snackbar
                    .make(
                        requireView(),
                        error.message ?: getString(R.string.unknown_error),
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            }
        })

        initToolbar(
            toolbar = appListToolbar,
            title = context!!.getString(R.string.title_app_list),
            bgColorInt = ColorUtils.getColor(context!!, R.attr.colorPrimary)
        )
    }
}
