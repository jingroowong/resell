package com.example.resell.data

object ItemContract {
    // Define the table name
    const val TABLE_NAME = "items"

    // Define column names
    const val COLUMN_ID = "_id"
    const val COLUMN_NAME = "name"
    const val COLUMN_PRICE = "price"
    const val COLUMN_IMAGE = "image"
    const val COLUMN_DATE_UPLOAD = "date_upload"
    const val COLUMN_STATUS = "status"

    // SQL statement to create the table
    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_PRICE REAL NOT NULL,
            $COLUMN_IMAGE TEXT,
            $COLUMN_DATE_UPLOAD INTEGER,
            $COLUMN_STATUS TEXT
        );
    """
}