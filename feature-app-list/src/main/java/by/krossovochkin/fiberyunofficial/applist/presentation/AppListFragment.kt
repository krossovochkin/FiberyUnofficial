package by.krossovochkin.fiberyunofficial.applist.presentation

import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.applist.AppListParentComponent
import by.krossovochkin.fiberyunofficial.applist.DaggerAppListComponent
import by.krossovochkin.fiberyunofficial.applist.R
import by.krossovochkin.fiberyunofficial.applist.databinding.FragmentAppListBinding
import by.krossovochkin.fiberyunofficial.applist.databinding.ItemAppBinding
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import javax.inject.Inject

class AppListFragment(
    private val appListParentComponent: AppListParentComponent
) : BaseFragment(R.layout.fragment_app_list) {

    @Inject
    lateinit var viewModel: AppListViewModel

    private var parentListener: ParentListener? = null

    private val binding by viewBinding(FragmentAppListBinding::bind)

    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateLayoutContainer<AppListItem, ListItem>(
            layout = R.layout.item_app
        ) {
            val binding = ItemAppBinding.bind(this.itemView)
            bind {
                itemView.setOnClickListener { viewModel.select(item) }
                binding.appTitleTextView.text = item.title
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

        binding.appListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.appListRecyclerView.adapter = adapter
        binding.appListRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayout.VERTICAL
            )
        )

        viewModel.appItems.observe(viewLifecycleOwner, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        viewModel.progress.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = it
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

        viewModel.navigation.observe(viewLifecycleOwner, Observer { event: Event<AppListNavEvent> ->
            when (val navEvent = event.getContentIfNotHandled()) {
                is AppListNavEvent.OnAppSelectedEvent -> {
                    parentListener?.onAppSelected(navEvent.fiberyAppData)
                }
            }
        })

        initToolbar(
            toolbar = binding.appListToolbar,
            title = context!!.getString(R.string.title_app_list),
            bgColorInt = ColorUtils.getColor(context!!, R.attr.colorPrimary)
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentListener = context as ParentListener
    }

    override fun onDetach() {
        super.onDetach()
        parentListener = null
    }

    interface ParentListener {

        fun onAppSelected(fiberyAppData: FiberyAppData)
    }
}
