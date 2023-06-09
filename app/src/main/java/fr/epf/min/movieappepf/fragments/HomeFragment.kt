package fr.epf.min.movieappepf.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.min.movieappepf.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment(private val context: MainActivity) : Fragment() {
    private val trendingMoviesList: MutableList<Model> = mutableListOf()
    private lateinit var verticalRecyclerView: RecyclerView
    private val movieList: MutableList<Model> = mutableListOf()
    private val removedMoviesList = mutableListOf<Model>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        getTrendingMovies("en")
        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view)
        verticalRecyclerView.adapter = MovieAdapter(inflater,context, trendingMoviesList, R.layout.item_vertical_movie)
        return view
    }

    private fun mapMovieToMovieModel(movie: Movie): Model {
        val partialPosterPath = movie.poster_path
        val fullPosterPath = "https://image.tmdb.org/t/p/original$partialPosterPath"

        return Model(
            name = movie.title,
            overview = movie.overview,
            Url = fullPosterPath,
            releaseDate = movie.release_date,
            vote_average = movie.vote_average
        )
    }

    fun updateMovieList(newMovieList: List<Model>) {
        for (newMovie in newMovieList) {
            val existingMovie = movieList.find { it.name == newMovie.name }
            if (existingMovie == null && !removedMoviesList.contains(newMovie)) {
                movieList.add(newMovie)
            }
        }
        verticalRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun updateTrendingMoviesList(newMoviesList: List<Model>) {
        trendingMoviesList.clear()
        trendingMoviesList.addAll(newMoviesList)
        verticalRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun getTrendingMovies(langue: String) {
        val apiKey = "3726296f8b608d7e06a42bb263bca61f"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(Service::class.java)
        val call = service.getTrendingMovies(apiKey,langue)
        call.enqueue(object : Callback<ModelSearch> {
            override fun onResponse(call: Call<ModelSearch>, response: Response<ModelSearch>) {
                if (response.isSuccessful) {
                    val searchResult = response.body()
                    val trendingMovies = searchResult?.results ?: emptyList()
                    val trendingMovieModels = trendingMovies.map { mapMovieToMovieModel(it) }
                    updateTrendingMoviesList(trendingMovieModels)
                } else {
                }
            }
            override fun onFailure(call: Call<ModelSearch>, t: Throwable) {
            }
        })
    }

    class MovieAdapter (
        private val inflater: LayoutInflater,
        val context:MainActivity,

        private val movieList:MutableList<Model>,
        private val layoutId: Int,
    ):RecyclerView.Adapter<MovieAdapter.ViewHolder>(){

        class ViewHolder(view: View):RecyclerView.ViewHolder(view){
            val movieImage=view.findViewById<ImageView>(R.id.image_item)
            val movieName: TextView?=view.findViewById(R.id.name_item)
            val movieReleaseDate: TextView?=view.findViewById(R.id.movie_releasedate)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(layoutId, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int =movieList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentMovie=movieList[position]
            Glide.with(context).load(Uri.parse(currentMovie.Url)).into(holder.movieImage)
            holder.movieName?.text=currentMovie.name
            holder.movieReleaseDate?.text= currentMovie.releaseDate

            holder.itemView.setOnClickListener {
                Popup(this,currentMovie).show()
            }
        }
    }
}