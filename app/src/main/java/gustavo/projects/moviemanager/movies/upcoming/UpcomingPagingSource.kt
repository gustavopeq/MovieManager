package gustavo.projects.moviemanager.movies.upcoming

import androidx.paging.PagingSource
import androidx.paging.PagingState
import gustavo.projects.moviemanager.domain.mappers.MovieMapper
import gustavo.projects.moviemanager.domain.models.Movie
import gustavo.projects.moviemanager.network.NetworkLayer

class UpcomingPagingSource : PagingSource<Int, Movie>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {

                val pageNumber = params.key ?: 1
                var previousKey: Int?
                if(pageNumber == 1) {
                        previousKey = null
                }else {
                        previousKey = pageNumber - 1
                }

                val request = NetworkLayer.apiClient.getUpcomingMoviesPage(pageNumber)

                request.exception?.let {
                        return LoadResult.Error(it)
                }

                return LoadResult.Page(
                        data = request.body.results.map { movieResponse ->
                                MovieMapper.buildFrom(movieResponse)
                        },
                        prevKey = previousKey,
                        nextKey = pageNumber + 1
                )
        }


        override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {

                return state.anchorPosition?.let { anchorPosition ->
                        val anchorPage = state.closestPageToPosition(anchorPosition)
                        anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
                }
        }
}