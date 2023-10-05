package com.example.resell.models

data class AdminFeedback(
    var orderId: Long? = null,
    val feedback : String ?=null,
    val rating : Long? = null,

)
