package top.wteng.mexpress.activity


import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.*
import kotlinx.android.synthetic.main.activity_express.view.*
import org.litepal.LitePal
import org.litepal.extension.find
import top.wteng.mexpress.adapter.ExpressTraceAdpter
import top.wteng.mexpress.entity.ExpressRecorder
import top.wteng.mexpress.entity.SupportedCompany
import top.wteng.mexpress.entity.TraceResultItem
import top.wteng.mexpress.util.ExpressApiResultParser
import top.wteng.mexpress.util.OrderTraceUtil

class ExpressActivity : AppCompatActivity() {
    private lateinit var expressTraceView: RecyclerView
    private val adapterList = mutableListOf<TraceResultItem>()
    private lateinit var expressTraceAdapter: ExpressTraceAdpter
    private val expressCompany = SupportedCompany.companyMap
    private var expNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_express)

        var expCode = intent.getStringExtra("expCode")
        var expCompanyName = intent.getStringExtra("expCompanyName")
        expNo = intent.getStringExtra("expNo")!!
        var expNote = intent.getStringExtra("expNote")
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
            it.setImageResource(R.drawable.bak2)
        }

        // 设置列表
        expressTraceView = findViewById(R.id.express_trace)

        //设置工具栏
        setSupportActionBar(expressToolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //设置适配器
//        expressTraceAdapter =
        initAdaptorMap(expNo)
        expressTraceAdapter = ExpressTraceAdpter(adapterList)
        with(expressTraceView) {
            this.layoutManager =
                GridLayoutManager(this@ExpressActivity, 1)
            this.adapter = expressTraceAdapter
        }

        // 悬浮按钮添加适配器

        val floatEditButton = findViewById<FloatingActionButton>(R.id.edit_express_btn).also {
            it.setOnClickListener { v ->
//                val spinnerAdapter = ArrayAdapter(this@MainActivity, R.layout.spinner_item, expressCompany.keys.toMutableList())
                lateinit var spinner: AppCompatSpinner
                val addExpressView = layoutInflater.inflate(R.layout.add_express, null).also {
                    val noteText = it.findViewById<EditText>(R.id.express_note)
                    noteText.setText(expNote)

                    val numberText = it.findViewById<EditText>(R.id.express_number)
                    numberText.setText(expNo)

                    spinner = it.findViewById<AppCompatSpinner>(R.id.express_company).also {
                        val spinnerAdapter = ArrayAdapter(this@ExpressActivity, R.layout.spinner_item, expressCompany.keys.toMutableList())
                        it.adapter = spinnerAdapter
                        val position = expressCompany.keys.toList().indexOf(expCompanyName)
                        it.setSelection(position)
                    }
                }
                val dialogBuilder = AlertDialog.Builder(this).also {
                    it.setTitle("编辑订单")
                    it.setView(addExpressView)
                    it.setPositiveButton("确定") {dialog, _ ->
                        val etNumber = addExpressView.findViewById<EditText>(R.id.express_number)
                        val etNote = addExpressView.findViewById<EditText>(R.id.express_note)
                        val number = etNumber.text.toString()
                        val note = etNote.text.toString()
                        val expressCompanySelected: String = spinner.selectedItem.toString()
                        val newExpressRecorder = expressCompany[expressCompanySelected]?.let { expCode ->
                            ExpressRecorder(number = number, note = note, company = expressCompanySelected, companyCode = expCode)
                        }
                        val expressFound = LitePal.select("*").where("number = ?", number).find(ExpressRecorder::class.java)
//                println(expressFound)
                        if (expressFound.isEmpty()) {
                            // 添加新订单
                            newExpressRecorder!!.save()
                            Snackbar.make(v, "订单 $number 已添加", Snackbar.LENGTH_SHORT).setAction("撤销") {
                                Toast.makeText(this@ExpressActivity, "已撤销添加", Toast.LENGTH_SHORT).show()
                            }.show()
                            dialog.cancel()
//                            refresh(allExpressFlag)
                        }else {
                            expNo = number
                            expCompanyName = expressCompanySelected
                            expNote = note
                            expCode = expressCompany[expressCompanySelected]

                            expressColToolBar.title = when (expNote) {
                                null -> expCompanyName
                                "" -> expCompanyName
                                else -> expNote
                            }

                            val express = expressFound[0]
                            express.number = number
                            express.note = note
                            express.company = expressCompanySelected
                            express.companyCode = expressCompany[expressCompanySelected]
                            express.save()
                            Snackbar.make(v, "订单 $number 已更新", Snackbar.LENGTH_SHORT).setAction("撤销") {
                                Toast.makeText(this@ExpressActivity, "已撤销更新", Toast.LENGTH_SHORT).show()
                            }.show()
                            ExpressTraceTask().execute(expCode, expNo)
                            dialog.cancel()
                        }

                    }
                    it.setNegativeButton("取消") {dialog, _ ->
                        Toast.makeText(this@ExpressActivity, "已取消", Toast.LENGTH_SHORT).show()
                        dialog.cancel()
                    }
                }
                dialogBuilder.show()
            }
        }

        ExpressTraceTask().execute(expCode, expNo)
    }

    private fun initAdaptorMap(expNo: String) {
        val curRecorder = LitePal.where("number = ?", expNo).find<ExpressRecorder>()
        val traceList = curRecorder[0].toTraceResultObjList()
        adapterList.clear()
        adapterList.addAll(traceList)
    }
    
    fun updateExpressTraceInfo(trace:  MutableMap<String, Any>, number: String) {
        val expressTrace = trace["fullTrace"] as MutableList<MutableMap<String, String>>
        if (trace["success"] == true && expressTrace.size > 0) {
            val newExpressRecorder = ExpressRecorder( state = trace["state"].toString().toInt(),
                fullTrace = trace["fullTraceStr"].toString(),
                lastTrace = expressTrace.last()["acceptTime"],
                lastTime = expressTrace.last()["acceptStation"])
            println("number = $number")
            newExpressRecorder.updateAll("number = ?", number)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                setResult(RESULT_OK)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
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
            return OrderTraceUtil.getTraceMap(baseContext ,params[0], params[1])
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("task","子线程开始 ...")
        }
    }
}


