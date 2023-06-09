package fr.epf.min.movieappepf

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import fr.epf.min.movieappepf.fragments.HomeFragment

class Popup(
    private val adapter: HomeFragment.MovieAdapter,
    private val currentMovie:Model
) :Dialog(adapter.context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_movie_details)
        setupComponents()
        setupClosebutton()
    }

    private fun setupClosebutton(){
        findViewById<ImageView>(R.id.popup_close_button).setOnClickListener {
            dismiss()
        }
    }


    private fun setupComponents() {
        val movieImage=findViewById<ImageView>(R.id.popup_image)
        Glide.with(adapter.context).load(Uri.parse(currentMovie.Url)).into(movieImage)
        findViewById<TextView>(R.id.popup_movie_name).text=currentMovie.name
        findViewById<TextView>(R.id.popup_movie_subtitle).text=currentMovie.overview
        findViewById<TextView>(R.id.popup_movie_release_date).text=currentMovie.releaseDate
        findViewById<TextView>(R.id.popup_movie_vote_average).text= currentMovie.vote_average.toString()
    }
}