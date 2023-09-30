package com.example.resell.database
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.util.Date
import com.google.firebase.database.PropertyName


@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = false)
    var productID: Int = 0,

    var productName: String? = null,
    var productPrice: Double? = null,
    var productDesc: String? = null,
    var productCondition: String? = null,
    var productImage: String? = null,
    var dateUpload: Long? = null,
    var productAvailability: Boolean? = null
  
    @PropertyName("productName")
    var productName: String? = null,
    @PropertyName("productPrice")
    var productPrice: Double? = null,
    @PropertyName("productDesc")
    var productDesc: String? = null,
    @PropertyName("productCondition")
    var productCondition: String? = null,
    @PropertyName("productImage")
    var productImage: String? = null,
    @PropertyName("dateUpload")
    var dateUpload: Long? = null,
    @PropertyName("productAvailability")
    var productAvailability: Boolean? = null,
   

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(productID)
        parcel.writeString(productName)
        parcel.writeValue(productPrice)
        parcel.writeString(productDesc)
        parcel.writeString(productCondition)
        parcel.writeString(productImage)
        dateUpload?.let { parcel.writeLong(it) }
        parcel.writeValue(productAvailability)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }

    
}{

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "productID" to productID,
            "productName" to productName,
            "productPrice" to productPrice,
            "productDesc" to productDesc,
            "productCondition" to productCondition,
            "productImage" to productImage,
            "dateUpload" to dateUpload,
            "productAvailability" to productAvailability
        )
    }
}