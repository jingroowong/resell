package com.example.resell.models

data class Orders(
    var orderId: Long? = null,
    val orderAmount: Long? = null,
    val orderDate: String? = null,
    val orderStatus: String? = null,
    val deal: Boolean? = null,
    val paymentID: Long? = null,
    val userID: String? = null
) {
    // Add a no-argument constructor
    constructor() : this(-0, 0L, "", "", false,0L,"")
}
