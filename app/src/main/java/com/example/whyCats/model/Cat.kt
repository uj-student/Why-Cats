package com.example.whyCats.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Cat(
    var breeds: List<Breed?>?,
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
) : Serializable

data class Breed(
    var id: String,
    var name: String,
    var origin: String,
    var description: String,
    var socialNeeds: String,
    var temperament: String,
    @SerializedName("wikipedia_url")
    var wikiUrl: String
    ): Serializable