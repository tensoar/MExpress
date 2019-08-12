package top.wteng.mexpress.activity

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import top.wteng.mexpress.R
import top.wteng.mexpress.adapter.ExpressTraceAdpter
import top.wteng.mexpress.util.OrderTraceUtil

class ExpressActivity : AppCompatActivity() {
    lateinit var expressTraceView: RecyclerView
    private lateinit var expressTraceAdapter: ExpressTraceAdpter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_express)

        val expCode = intent.getStringExtra("expCode")
        val expCompanyName = intent.getStringExtra("expCompanyName")
        val expNo = intent.getStringExtra("expNo")
        val expNote = intent.getStringExtra("expNote")

        val expressToolbar = findViewById<Toolbar>(R.id.express_toolbar)
        val expressColToolBar = findViewById<CollapsingToolbarLayout>(R.id.express_col_toolbar).also {
            it.title = when (expNote) {
                null -> expCompanyName
                "" -> expCompanyName
                else -> expNote
            }
        }
        var expressImage = findViewById<ImageView>(R.id.express_toolbar_image).also {
            it.setImageResource(R.drawable.plan)
        }

        // 设置列表
        expressTraceView = findViewById(R.id.express_trace)

        setSupportActionBar(expressToolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        ExpressTraceTask().execute(expCode, expNo)
    }

    inner class ExpressTraceTask: AsyncTask<String, Int, MutableMap<String, Any>>() {
        var progressBar: ProgressBar? = null
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            progressBar?.progress = values[0] ?: 0
        }

        override fun onPostExecute(result: MutableMap<String, Any>?) {
            super.onPostExecute(result)
            val expressTrace = result!!["fullTrace"] as MutableList<MutableMap<String, String>>
            expressTraceAdapter = ExpressTraceAdpter(expressTrace)
            with(expressTraceView) {
                this.layoutManager = GridLayoutManager(this@ExpressActivity, 1)
                this.adapter = expressTraceAdapter
            }
            Log.d("task", "线程结束")
        }

        override fun doInBackground(vararg params: String): MutableMap<String, Any> {
            Log.d("task", "expCode = ${params[0]}, expNo = ${params[1]}")
            for(i in (0 until 1000)) {
                publishProgress(i)
            }
            val expressTrace = OrderTraceUtil.getTraceMap(params[0], params[1])
            return expressTrace
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("task","子线程开始 ...")
        }
    }
}


