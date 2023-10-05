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
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "orderID")
    var orderID: Int = 0,

    @ColumnInfo(name = "orderDate")
    var orderDate: String?,

    @ColumnInfo(name = "orderAmount")
    var orderAmount: Double,

    @ColumnInfo(name = "orderStatus")
    var orderStatus: String?,

    @ColumnInfo(name = "orderDeal")
    var deal: Boolean,

    @ColumnInfo(name = "userID")
    var userID: String?,

    @ColumnInfo(name = "paymentID")
    var paymentID: Int

) :Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readInt()
    )
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(orderID)
        parcel.writeString(orderDate)
        parcel.writeDouble(orderAmount)
        parcel.writeString(orderStatus)
        parcel.writeByte(if (deal) 1 else 0)
        parcel.writeString(userID)
        parcel.writeInt(paymentID)
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


    constructor() : this(0, "", 0.0, "", false, "", 0)
}
