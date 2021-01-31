package com.example.developerslife

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class GifProperty(
    //@SerializedName("description")
    var description: String,
   // @SerializedName("gifURL")
    var gifUrlSource: String
)