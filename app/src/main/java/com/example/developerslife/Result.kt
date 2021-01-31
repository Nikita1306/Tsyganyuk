package com.example.developerslife

import com.google.gson.annotations.SerializedName

class Result {
    @SerializedName("result")
    var result: List<GifProperty>? = null
}