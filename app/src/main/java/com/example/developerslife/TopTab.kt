package com.example.developerslife

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class TopTab : Fragment() {

    companion object {
        fun newInstance() = TopTab()
    }

    private lateinit var listOfGifs: ArrayList<GifProperty>
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    lateinit var progressBar: ProgressBar
    lateinit var errorMessage: TextView
    lateinit var buttonRetry: Button
    lateinit var cl: ConstraintLayout
    var currentPage = 0
    var currentGif = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.top_tab_fragment, container, false)
        imageView = root.findViewById(R.id.top_gif)
        textView = root.findViewById(R.id.top_description)
        val buttonNext: ImageButton = root.findViewById(R.id.button_next_top)
        val buttonPrevious: ImageButton = root.findViewById(R.id.button_previous_top)
        errorMessage = root.findViewById(R.id.error_text_top)
        buttonRetry = root.findViewById(R.id.button_retry_top)
        var flagDone = false
        var isError = false
        listOfGifs = ArrayList()
        currentPage = 0
        val url = "https://developerslife.ru/top/$currentPage?json=true"
        cl = root.findViewById(R.id.gifLayoutTop)
        progressBar = root.findViewById(R.id.progressBar3)
        progressBar.visibility = View.VISIBLE
        // Log.d("TSG", "${listOfGifs.add(loadGif(url))} + ${listOfGifs.last()}")

        loadGif(url, currentGif)
        currentGif = 0
        if (!isOnline(context!!)) {
            textView.visibility = View.GONE
            imageView.visibility = View.GONE
            buttonNext.visibility = View.GONE
            buttonRetry.visibility = View.VISIBLE
            errorMessage.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
        }

        buttonRetry.setOnClickListener{
            if (isOnline(context!!)) {
                //currentGif++
                textView.visibility = View.VISIBLE
                imageView.visibility = View.VISIBLE
                buttonNext.visibility = View.VISIBLE
                buttonRetry.visibility = View.GONE
                errorMessage.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                isError = false
                loadGif("https://developerslife.ru/top/$currentPage?json=true", currentGif)
                //currentGif++
            }
        }

        buttonNext.setOnClickListener {
            if (!isOnline(context!!) && currentGif >= listOfGifs.size - 1) {
                textView.visibility = View.GONE
                imageView.visibility = View.GONE
                buttonNext.visibility = View.GONE
                buttonRetry.visibility = View.VISIBLE
                errorMessage.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                isError = true
            } else {
                textView.visibility = View.VISIBLE
                imageView.visibility = View.VISIBLE
                buttonNext.visibility = View.VISIBLE
                buttonRetry.visibility = View.GONE
                errorMessage.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                isError = false
            }

            if (currentGif < listOfGifs.size - 1) {
                currentGif++
                var anim = AnimationUtils.loadAnimation(context, R.anim.slidetoleft)
                cl.startAnimation(anim)
                Glide.with(this)
                    .load(listOfGifs[currentGif].gifUrlSource)
                    .onlyRetrieveFromCache(true)
                    .into(imageView)
                textView.setText(listOfGifs[currentGif].description)
            } else {
                currentGif++
                var anim = AnimationUtils.loadAnimation(context, R.anim.slidetoleft)
                cl.startAnimation(anim)
                loadGif("https://developerslife.ru/top/$currentPage?json=true", currentGif)
            }
            if (currentGif % 5 == 4 && currentGif != 0) {
                currentPage++
            }

            if (currentGif > 0) {
                buttonPrevious.visibility = View.VISIBLE
            }


        }

        buttonPrevious.setOnClickListener{
            if (isError) {
                textView.visibility = View.VISIBLE
                imageView.visibility = View.VISIBLE
                buttonNext.visibility = View.VISIBLE
                buttonRetry.visibility = View.GONE
                errorMessage.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
            currentGif--
            var anim = AnimationUtils.loadAnimation(context, R.anim.slidetoright)
            cl.startAnimation(anim)
            Glide.with(this)
                .load(listOfGifs[currentGif].gifUrlSource)
                .onlyRetrieveFromCache(true)
                .into(imageView)
            textView.setText(listOfGifs[currentGif].description)
            if (currentGif == 0) {
                buttonPrevious.visibility = View.GONE
            }
        }
        return root
    }

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val n = cm.activeNetwork
            if (n != null) {
                val nc = cm.getNetworkCapabilities(n)
                //It will check for both wifi and cellular network
                return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI)
            }
            return false
        } else {
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }


    fun loadGif(url: String, ind: Int) {

        textView.setText("")
        var gif: GifProperty = GifProperty("","")
        if (isOnline(context!!))
        progressBar.visibility = View.VISIBLE
        else progressBar.visibility = View.INVISIBLE
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, Response.Listener { response ->
                if (response.getJSONArray("result").length() == 0) {
                    Glide.with(this)
                        .load(R.drawable.failed_loading)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
//                        currentGif--
//                        isError = true
//                        textView.visibility = View.GONE
//                        imageView.visibility = View.GONE
//                        buttonNext.visibility = View.GONE
//                        buttonRetry.visibility = View.VISIBLE
//                        errorMessage.visibility = View.VISIBLE
//                        errorMessage.setText("Отсутствует подключение к интернету!\n Пожалуйста, повторите позже")
                                progressBar.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                textView.setText(R.string.no_gif_message)
                                progressBar.visibility = View.GONE
                                return false
                            }

                        })
                        .into(imageView)
                        //imageView.foreground = resources.getDrawable(R.drawable.gradient)
                } else {
                    var arr = response.getJSONArray("result")
                    gif.description = arr.getJSONObject(currentGif % 5).getString("description")
                    gif.gifUrlSource = arr.getJSONObject(currentGif % 5).getString("gifURL")
                    listOfGifs.add(gif)
                    Glide.with(this)
                        .load(gif!!.gifUrlSource)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                textView.setText(gif!!.description)
                                progressBar.visibility = View.GONE
                                return false
                            }

                        })
                        .into(imageView)
                    imageView.foreground = resources.getDrawable(R.drawable.gradient)
                }
            },
            Response.ErrorListener {
            })
        MySingleton.getInstance(this.context!!).addToRequestQueue(jsonObjectRequest)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


}
