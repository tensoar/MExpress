package top.wteng.mexpress.db

import android.annotation.TargetApi
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build

class ExpressDatabaseHelper @TargetApi(Build.VERSION_CODES.P) constructor(
    context: Context?,
    name: String?,
    version: Int,
    openParams: SQLiteDatabase.OpenParams
) : SQLiteOpenHelper(context, name, version, openParams) {
    private val mContext: Context? = context
    private val createExpressRecorderTableStr = """
        create table express (
            id integer primary key autoincrement,
            number text,
            note text,
            company text,
            status integer,
            lastTime text,
            lastTrace text,
            fullTrace text
        )
    """

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createExpressRecorderTableStr)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}