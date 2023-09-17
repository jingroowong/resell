package com.example.resell

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.resell.data.ItemDbHelper
import com.example.resell.models.Item
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = ItemDbHelper(this)
        val db = dbHelper.writableDatabase

        val date = Date()

        val test = Item("item", 22.11, "image1", date, "new")

        dbHelper.insertItem(test)


        db.close()


        val item = dbHelper.getAllItems()

        if (item != null) {

            val itemDetailsTextView = findViewById<TextView>(R.id.itemDetailsTextView)
//            val formattedDate =
//                SimpleDateFormat(
//                    "yyyy-MM-dd HH:mm:ss",
//                    Locale.getDefault()
//                ).format(item.dateUpload)
//            val itemDetailsText =
//                "Item ID: ${item.id}\nName: ${item.name}\nPrice: ${item.price}\nImage: ${item.image}\nDate Upload: $formattedDate\nStatus: ${item.status}"

            val itemDetailsText =item.toString()

            itemDetailsTextView.text = itemDetailsText
        }

    }
}