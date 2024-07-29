package com.sunilmishra.androidzcgassignment.data.model

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String
)

data class Section(
    @SerializedName("sectionType")
    val sectionType: String,
    @SerializedName("items")
    val items: List<Item>
)
