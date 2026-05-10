package com.ava.splashnt.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val id: String,
    val slug: String,
    val title: String,
)