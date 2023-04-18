package com.example.unsplash.adapters

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplash.models.Photo
import retrofit2.HttpException
import java.io.IOException

class GetListPhotosInCollectionPagingSource(
    private val idCollection: String,
    private val getData: suspend (idCollection: String, page: Int) -> List<Photo>
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val page = params.key ?: 1
        return try {
            val response = getData(idCollection, page)
            LoadResult.Page(
                response, prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            return PagingSource.LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return PagingSource.LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}