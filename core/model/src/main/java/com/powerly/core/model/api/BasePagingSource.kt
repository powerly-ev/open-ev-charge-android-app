package com.powerly.core.model.api

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * Base class for implementing a paging source in Android's Paging 3 library.
 * This class abstracts the logic for loading pages of data from a remote data source.
 *
 * @param T The type of items being loaded by this paging source.
 * @property apiCall A suspend function that loads a page of data from the remote data source.
 * @property onPageLoaded A function that is invoked when items are loaded.
 *                        It provides the current page index and total number of items.
 */
class BasePagingSource<T : Any>(
    private val apiCall: suspend (page: Int) -> BaseResponsePaginated<T>,
    private val onPageLoaded: ((pageIndex: Int, totalItems: Int) -> Unit)? = null,
    private val reversed: Boolean = false
) : PagingSource<Int, T>() {

    /**
     * Loads a page of data from the remote data source.
     *
     * @param params The parameters for loading data, including the key of the page to load.
     * @return A LoadResult object representing the result of loading the page.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 1
            val response = apiCall(page)
            val data: List<T> = if (reversed) response.data.orEmpty().reversed()
            else response.data.orEmpty()

            val currentPage = response.meta?.currentPage ?: 0
            val lastPage = response.meta?.lastPage ?: 0
            val total = response.meta?.total ?: 0

            // Invoke onLoadItems callback if provided
            onPageLoaded?.invoke(currentPage, total)

            LoadResult.Page(
                data = data,
                prevKey = if (page <= 1) null else page.minus(1),
                nextKey = if (data.isEmpty() || currentPage == lastPage) null
                else page.plus(1),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            onPageLoaded?.invoke(-1, 0)
            LoadResult.Error(e)
        }
    }

    /**
     * Determines the key for refreshing the list after a data change.
     *
     * @param state The current paging state.
     * @return The key for refreshing the list, based on the anchor position.
     */
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}
