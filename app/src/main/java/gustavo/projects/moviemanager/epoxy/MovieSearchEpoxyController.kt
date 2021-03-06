package gustavo.projects.moviemanager.epoxy

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging3.PagingDataEpoxyController
import com.squareup.picasso.Picasso
import gustavo.projects.moviemanager.R
import gustavo.projects.moviemanager.databinding.ModelSearchExceptionMsgBinding
import gustavo.projects.moviemanager.databinding.ModelSearchMovieListItemBinding
import gustavo.projects.moviemanager.domain.models.Movie
import gustavo.projects.moviemanager.movies.search.MovieSearchPagingSource
import gustavo.projects.moviemanager.util.Constants

class MovieSearchEpoxyController(
    private val onMovieSelected: (Int) -> Unit
): PagingDataEpoxyController<Movie>() {

    var searchException: MovieSearchPagingSource.SearchException? = null
        set(value) {
            field = value
            if(searchException != null) {
                requestForcedModelBuild()
            }
        }

    override fun buildItemModel(currentPosition: Int,
                                item: Movie?
    ): EpoxyModel<*> {
        return SearchedMoviesItemEpoxyModel(
            item!!.id!!,
            item!!.poster_path,
            item!!.title!!,
            item!!.vote_average!!,
            onMovieSelected
        ).id(item.id)
    }

    override fun addModels(models: List<EpoxyModel<*>>) {

        searchException?.let {
            SearchedMoviesExceptionItemEpoxyModel(it).id("exception_msg").addTo(this)
            return
        }

        if(models.isEmpty()) {
            LoadingEpoxyModel().id("loading").addTo(this)
            return
        }

        super.addModels(models)
    }

    data class SearchedMoviesItemEpoxyModel(
        val movieId: Int,
        val posterPath: String?,
        val movieTitle: String,
        val movieRating: Double,
        val onMovieSelected: (Int) -> Unit
    ): ViewBindingKotlinModel<ModelSearchMovieListItemBinding>(R.layout.model_search_movie_list_item) {

        override fun ModelSearchMovieListItemBinding.bind() {
            var fullPosterPath = ""
            if(posterPath != null)
            {
                fullPosterPath = Constants.BASE_IMAGE_URL + posterPath
            }else
            {
                fullPosterPath = Constants.MISSING_PROFILE_PICTURE_URL
            }

            Picasso.get().load(fullPosterPath).into(movieImageView)
            movieTitleTextView.text = movieTitle
            movieRatingTextView.text = movieRating.toString()

            root.setOnClickListener {
                onMovieSelected(movieId)
            }
        }
    }

    data class SearchedMoviesExceptionItemEpoxyModel(
       val searchException: MovieSearchPagingSource.SearchException
    ): ViewBindingKotlinModel<ModelSearchExceptionMsgBinding>(R.layout.model_search_exception_msg) {

        override fun ModelSearchExceptionMsgBinding.bind() {
            exceptionTitleTextView.text = searchException.title
            exceptionDescriptionTextView.text = searchException.description
        }
    }
}