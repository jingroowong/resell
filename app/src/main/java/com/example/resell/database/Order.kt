package com.example.resell.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude

@Entity(
    tableName = "order_table", foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userID"],
        childColumns = ["userID"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Payment::class,
        parentColumns = ["paymentID"],
        childColumns = ["paymentID"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class Order(
    //var deal: Boolean? = null,
    var orderID: Int? = null,
    var orderDate: String? = null,
    var orderAmount: Double? = null,
    var orderStatus: String? = null,
    var userID: Int? = null,
    var paymentID: Int? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
        //parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
       // parcel.writeValue(deal)
        parcel.writeValue(orderID)
        parcel.writeString(orderDate)
        parcel.writeValue(orderAmount)
        parcel.writeString(orderStatus)
        parcel.writeValue(userID)
        parcel.writeValue(paymentID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "orderID" to orderID,
            "orderStatus" to orderStatus,
            "orderDate" to orderDate,
            "orderAmount" to orderAmount,
            "orderStatus" to orderStatus,
            "userID" to userID,
            "paymentID" to paymentID
        )
    }
}
