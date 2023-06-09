package fr.epf.min.movieappepf.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import fr.epf.min.movieappepf.Service
import fr.epf.min.movieappepf.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ResultFragment : Fragment() {

    private lateinit var titleTextView: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var releaseDateTextView: TextView
    private lateinit var idTextView: TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_results, container, false)
        titleTextView = view.findViewById(R.id.name_item)
        posterImageView = view.findViewById(R.id.image_item)
        releaseDateTextView = view.findViewById(R.id.movie_releasedate)
        idTextView = view.findViewById(R.id.idTextView)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getString("title")
        var posterPath = arguments?.getString("poster_path")
        val releaseDate = arguments?.getString("release_date")
        val id = arguments?.getString("id")
        titleTextView.text = title
        releaseDateTextView.text = releaseDate
        idTextView.text = id

        if (!posterPath.isNullOrEmpty()) {
            val fullPosterPath = "https://image.tmdb.org/t/p/original$posterPath"
            Glide.with(requireContext())
                .load(fullPosterPath)
                .override(500, 500)
                .centerInside()
                .into(posterImageView)
        }

        val suggestButton = view.findViewById<Button>(R.id.suggestionButton)
        suggestButton.setOnClickListener {
            val movieId = idTextView.text.toString().toInt()
            getMovieRecommendations(movieId)
        }

    }

    private fun getMovieRecommendations(movieId: Int) {
        val apiKey = "3726296f8b608d7e06a42bb263bca61f"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(Service::class.java)
        val call = service.getRecommendations(movieId, apiKey)
        call.enqueue(object : Callback<ModelSearch> {
            override fun onResponse(call: Call<ModelSearch>, response: Response<ModelSearch>) {
                if (response.isSuccessful) {
                    val searchResult = response.body()
                    val recommendedMovies = searchResult?.results ?: emptyList()
                    showSuggestionsMovieDetails(recommendedMovies)
                } else {
                }
            }
            override fun onFailure(call: Call<ModelSearch>, t: Throwable) {
            }
        })
    }

    private fun showSuggestionsMovieDetails(movieList: List<Movie>) {
        val stackSuggestionFragment = StackSuggestionFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackSuggestionFragment)
            .commit()
        for (movie in movieList) {
            val newResultFragment = ResultFragment()
            val bundle = createMovieBundle(movie)
            newResultFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .add(R.id.stackSuggestionContainer, newResultFragment)
                .addToBackStack(null)
                .commit()
        }
    }
    private fun createMovieBundle(movie: Movie): Bundle {
        val bundle = Bundle()
        bundle.putString("title", movie.title)
        bundle.putString("poster_path", movie.poster_path)
        bundle.putString("release_date", movie.release_date)
        bundle.putString("id", movie.id.toString())
        return bundle
    }
}
