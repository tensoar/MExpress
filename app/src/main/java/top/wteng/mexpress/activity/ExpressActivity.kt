package top.wteng.mexpress.activity

import android.graphics.Color
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
import org.litepal.LitePal
import org.litepal.extension.find
//import top.wteng.mexpress.R
import top.wteng.mexpress.adapter.ExpressTraceAdpter
import top.wteng.mexpress.entity.ExpressRecorder
import top.wteng.mexpress.entity.TraceResultItem
import top.wteng.mexpress.util.ExpressApiResultParser
import top.wteng.mexpress.util.OrderTraceUtil

class ExpressActivity : AppCompatActivity() {
    lateinit var expressTraceView: RecyclerView
    private val adapterList = mutableListOf<TraceResultItem>()
    private lateinit var expressTraceAdapter: ExpressTraceAdpter
    private var expNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_express)

        val expCode = intent.getStringExtra("expCode")
        val expCompanyName = intent.getStringExtra("expCompanyName")
        expNo = intent.getStringExtra("expNo")
        val expNote = intent.getStringExtra("expNote")
//        expId = intent.getStringExtra("id").toLong()

        val expressToolbar = findViewById<Toolbar>(R.id.express_toolbar)
        val expressColToolBar = findViewById<CollapsingToolbarLayout>(R.id.express_col_toolbar).also {
            it.title = when (expNote) {
                null -> expCompanyName
                "" -> expCompanyName
                else -> expNote
            }
            it.setExpandedTitleColor(Color.RED)
        }
        var expressImage = findViewById<ImageView>(R.id.express_toolbar_image).also {
            it.setImageResource(R.drawable.plan)
        }

        // 设置列表
        expressTraceView = findViewById(R.id.express_trace)

        //设置工具栏
        setSupportActionBar(expressToolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //设置适配器
//        expressTraceAdapter =
        initAdaptorMap(expNo)
        expressTraceAdapter = ExpressTraceAdpter(adapterList)
        with(expressTraceView) {
            this.layoutManager = GridLayoutManager(this@ExpressActivity, 1)
            this.adapter = expressTraceAdapter
        }

        ExpressTraceTask().execute(expCode, expNo)
    }

    private fun initAdaptorMap(expNo: String) {
        val curRecorder = LitePal.where("number = ?", expNo).find<ExpressRecorder>()
        val traceList = curRecorder[0].toTraceResultObjList()
        adapterList.clear()
        println(traceList)
        adapterList.addAll(traceList)
    }
    
    fun updateExpressTraceInfo(trace:  MutableMap<String, Any>, number: String) {
        val expressTrace = trace["fullTrace"] as MutableList<MutableMap<String, String>>
        println("-----------")
        println(trace)
        println(expressTrace)
        if (trace["success"] == true && expressTrace.size > 0) {
            val newExpressRecorder = ExpressRecorder( state = trace["state"].toString().toInt(),
                fullTrace = trace["fullTraceStr"].toString(),
                lastTrace = expressTrace.last()["acceptTime"],
                lastTime = expressTrace.last()["acceptStation"])
            println("number = $number")
            newExpressRecorder.updateAll("number = ?", number)
        }
    }

    inner class ExpressTraceTask: AsyncTask<String, Int, MutableMap<String, Any>>() {
        private var progressBar: ProgressBar? = null
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            progressBar?.progress = values[0] ?: 0
        }

        override fun onPostExecute(result: MutableMap<String, Any>?) {
            super.onPostExecute(result)
            val traceList = ExpressApiResultParser.changeTraceMapToObj(result!!)
            if (traceList.size > 0) {
                adapterList.clear()
                adapterList.addAll(traceList)
                expressTraceAdapter.notifyDataSetChanged()
                updateExpressTraceInfo(result, expNo)
                Log.d("task", "线程结束")
            }
//            result?.let { expressTraceAdapter = ExpressTraceAdpter(result) }
        }

        override fun doInBackground(vararg params: String): MutableMap<String, Any> {
            Log.d("task", "expCode = ${params[0]}, expNo = ${params[1]}")
            return OrderTraceUtil.getTraceMap(params[0], params[1])
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("task","子线程开始 ...")
        }
    }
}


