package fr.epf.min.movieappepf.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import fr.epf.min.movieappepf.Movie
import fr.epf.min.movieappepf.R
import fr.epf.min.movieappepf.ModelSearch
import fr.epf.min.movieappepf.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var service: Service
    private var movies: List<Movie> = emptyList()
    private var onCreateViewCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        onCreateViewCount++
        val view = inflater.inflate(R.layout.fragment_research, container, false)
        searchView = view.findViewById(R.id.searchView)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(Service::class.java)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val apiKey = "3726296f8b608d7e06a42bb263bca61f"
                val call = service.searchMovies(apiKey, query)
                call.enqueue(object : Callback<ModelSearch> {
                    override fun onResponse(call: Call<ModelSearch>, response: Response<ModelSearch>) {
                        if (response.isSuccessful) {
                            val searchResult = response.body()
                            movies = searchResult?.results ?: emptyList()
                            if (movies.isNotEmpty()) {
                                showMovieDetails(movies)
                            }
                        } else {
                            Log.e("SearchFragment", "Erreur: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<ModelSearch>, t: Throwable) {
                        Log.e("SearchFragment", "Erreur: ${t.message}")
                    }
                })
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })

        return view
    }
    private fun showMovieDetails(movieList: List<Movie>) {
        val stackFragment = StackFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackFragment)
            .commit()
        for (movie in movieList) {
            val newResultFragment = ResultFragment()
            val bundle = createMovieBundle(movie)
            newResultFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .add(R.id.stackContainer, newResultFragment)
                .addToBackStack(null)
                .commit()
            stackFragment.incrementFragmentCount()
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
