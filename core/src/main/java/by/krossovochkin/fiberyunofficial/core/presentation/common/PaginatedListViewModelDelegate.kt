package by.krossovochkin.fiberyunofficial.core.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.paging.map
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val DEFAULT_PAGE_SIZE = 20

class PaginatedListViewModelDelegate<ValueT : Any>(
    private val viewModel: ViewModel,
    pageSize: Int = DEFAULT_PAGE_SIZE,
    loadPage: suspend (offset: Int, pageSize: Int) -> List<ValueT>,
    private val mapper: (ValueT) -> ListItem
) {

    private var dataSource: DataSource<ValueT>? = null
    private val pager = Pager(
        PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        )
    ) {
        DataSource(loadPage)
            .also { dataSource = it }
    }

    val items: Flow<PagingData<ListItem>>
        get() = pager
            .flow
            .map { it.map { data -> mapper(data) } }
            .cachedIn(viewModel.viewModelScope)

    fun invalidate() {
        dataSource?.invalidate()
    }
}

private class DataSource<ValueT : Any>(
    private val loadPageList: suspend (offset: Int, pageSize: Int) -> List<ValueT>
) : PagingSource<Int, ValueT>() {

    private suspend fun loadPage(offset: Int, pageSize: Int): LoadResult<Int, ValueT> {
        return try {
            val pageData = loadPageList(offset, pageSize)
            LoadResult.Page(
                data = pageData,
                prevKey = null,
                nextKey = if (pageData.size == pageSize) offset + pageSize else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, ValueT> {
        return when (params) {
            is LoadParams.Refresh -> loadPage(0, params.loadSize)
            is LoadParams.Append -> loadPage(params.key, params.loadSize)
            is LoadParams.Prepend -> LoadResult.Error(UnsupportedOperationException())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ValueT>): Int {
        return 0
    }
}
