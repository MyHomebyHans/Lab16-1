package com.androidhans.lab16

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

class MyContentProvider : ContentProvider() {
    private lateinit var dbrw: SQLiteDatabase //初始化資料庫

    //Step1:當Resolver要求操作資料時。先開啟資料庫
    override fun onCreate(): Boolean {
        val context = context ?: return false
        //取得資料庫實體
        dbrw = MyDBHelper(context).writableDatabase
        return true
    }
    //Step2:Resolver要求新資料時，則取得它給予的資料並新增於資料庫
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        //取出其它應用程式給予的book資料
        val book = values ?: return null
        //將資料新增於資料庫並回傳此筆記錄的Id
        val rowId = dbrw.insert("myTable", null, book)
        //回傳此筆記錄的Uri
        return Uri.parse("content://com.androidhans.lab16/$rowId")
    }
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    )
       : Int {
        //取出其他應用程式要搜尋的書名及要更新的價格
        val name = selection ?: return 0
        val price = values ?: return 0
        //更新特定書名的價格，並回傳被更新的紀錄筆數
        return dbrw.update("myTable", price, "book='${name}'", null)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?):
            Int {
        //取出其他應用程式要刪除的書名
        val name = selection ?: return 0
        //刪除特定書名，並回傳被刪除的紀錄筆數
        return dbrw.delete("myTable", "book='${name}'", null)
    }
    override fun getType(uri: Uri): String? = null

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        //取出其他應用程式要查詢的書名，若沒有書名則搜尋全部書籍
        val queryString = if (selection == null) null else
            "book='${selection}'"
        //將搜尋完成的 Cursor 回傳
        return dbrw.query(
            "myTable", null, queryString, null, null,
            null, null
        )
    }
}