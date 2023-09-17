package com.example.resell.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.resell.models.Item
import java.util.Date

class ItemDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "resell.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ItemContract.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${ItemContract.TABLE_NAME}")
        onCreate(db)
    }

    fun insertItem(item: Item) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ItemContract.COLUMN_NAME, item.name)
            put(ItemContract.COLUMN_PRICE, item.price)
            put(ItemContract.COLUMN_IMAGE, item.image)
            put(ItemContract.COLUMN_DATE_UPLOAD, item.dateUpload.time)
            put(ItemContract.COLUMN_STATUS, item.status)
        }

        db.insert(ItemContract.TABLE_NAME, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllItems(): List<Item> {
        val items = mutableListOf<Item>()
        val db = readableDatabase

        val cursor = db.query(
            ItemContract.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(ItemContract.COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndex(ItemContract.COLUMN_NAME))
            val price = cursor.getDouble(cursor.getColumnIndex(ItemContract.COLUMN_PRICE))
            val image = cursor.getString(cursor.getColumnIndex(ItemContract.COLUMN_IMAGE))
            val dateUploadInMillis =
                cursor.getLong(cursor.getColumnIndex(ItemContract.COLUMN_DATE_UPLOAD))
            val dateUpload = Date(dateUploadInMillis)
            val status = cursor.getString(cursor.getColumnIndex(ItemContract.COLUMN_STATUS))

            val item = Item(id, name, price, image, dateUpload, status)
            items.add(item)
        }

        cursor.close()
        db.close()

        return items
    }

    @SuppressLint("Range")
    fun getItemById(itemId: Long): Item? {
        val db = readableDatabase
        val selection = "${ItemContract.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(itemId.toString())

        val cursor = db.query(
            ItemContract.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var item: Item? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndex(ItemContract.COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndex(ItemContract.COLUMN_NAME))
            val price = cursor.getDouble(cursor.getColumnIndex(ItemContract.COLUMN_PRICE))
            val image = cursor.getString(cursor.getColumnIndex(ItemContract.COLUMN_IMAGE))
            val dateUploadInMillis =
                cursor.getLong(cursor.getColumnIndex(ItemContract.COLUMN_DATE_UPLOAD))
            val dateUpload = Date(dateUploadInMillis)
            val status = cursor.getString(cursor.getColumnIndex(ItemContract.COLUMN_STATUS))

            item = Item(id, name, price, image, dateUpload, status)
        }

        cursor.close()
        db.close()

        return item
    }


}
