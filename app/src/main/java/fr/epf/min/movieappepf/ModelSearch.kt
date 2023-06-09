package fr.epf.min.movieappepf

data class ModelSearch(
    val results: List<Movie>
)

data class Movie(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val overview: String,
    val release_date: String,
    val original_language: String,
    val vote_average: Double
)


