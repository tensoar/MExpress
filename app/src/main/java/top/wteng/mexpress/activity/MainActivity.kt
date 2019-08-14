package top.wteng.mexpress.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.*
import org.litepal.LitePal
import top.wteng.mexpress.R
import top.wteng.mexpress.adapter.ExpressAdapter
import top.wteng.mexpress.entity.ExpressRecorder

class MainActivity : AppCompatActivity() {
    private val expressCompany = mapOf("京东快递" to "JD", "百世快递" to "HTKY", "申通快递" to "STO", "安能快递" to "ANE",
        "承诺达" to "CND", "中通快递" to "ZTO", "韵达快递" to "YD", "圆通速递" to "YTO", "天天快递" to "HHTT",
        "顺丰速运" to "SF", "EMS" to "EMS", "京东快运" to "JDKY")
    private lateinit var recyclerView: RecyclerView
    private var allExpress = mutableListOf<ExpressRecorder>()
    private lateinit var expressAdapter: ExpressAdapter
    private lateinit var refreshView: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 设置悬浮按钮
        val fab = findViewById<FloatingActionButton>(R.id.fab).also {
            it.setOnClickListener { view ->
                addExpress(view)
            }
        }

        //设置列表
        recyclerView =  findViewById(R.id.recycle_view)
        initExpress()
        expressAdapter = ExpressAdapter(allExpress)
        with(recyclerView) {
            this.layoutManager = GridLayoutManager(this@MainActivity, 1)
            this.adapter = expressAdapter
        }

        //下拉刷新
        refreshView = findViewById(R.id.swipe_refresh)
        with(refreshView) {
            this.setColorSchemeResources(R.color.colorPrimary)
            this.setOnRefreshListener {
                refresh()
            }
        }
    }

    private fun refresh() {
        initExpress()
        if (!refreshView.isRefreshing){
            refreshView.isRefreshing = true
        }
        expressAdapter.notifyDataSetChanged()
        refreshView.isRefreshing = false
    }

    private fun initExpress(){
        allExpress.clear()
        allExpress.addAll(LitePal.findAll(ExpressRecorder::class.java))
    }

    @SuppressLint("InflateParams")
    private fun addExpress(v: View) {
        val addExpressView = layoutInflater.inflate(R.layout.add_express, null)

        val spinnerAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, expressCompany.keys.toMutableList())
        val spinner = addExpressView.findViewById<AppCompatSpinner>(R.id.express_company).also {
            it.adapter = spinnerAdapter
        }

        val dialogBuilder = AlertDialog.Builder(this).also {
            it.setTitle("添加单号")
            it.setView(addExpressView)
            it.setPositiveButton("添加") {dialog, _ ->
                val etNumber = addExpressView.findViewById<EditText>(R.id.express_number)
                val etNote = addExpressView.findViewById<EditText>(R.id.express_note)
                val number = etNumber.text.toString()
                val note = etNote.text.toString()
                val expressCompanySelected: String = spinner.selectedItem.toString()
                val newExpressRecorder = expressCompany[expressCompanySelected]?.let { expCode ->
                    ExpressRecorder(number = number, note = note, company = expressCompanySelected,
                        companyCode = expCode
                    )
                }
                val expressFound = LitePal.select("*").where("number = ?", number).find(ExpressRecorder::class.java)
                println(expressFound)
                if (expressFound.isEmpty()) {
                    // 添加新订单
                    newExpressRecorder!!.save()
                    Snackbar.make(v, "订单 $number 已添加", Snackbar.LENGTH_SHORT).setAction("撤销") {
                        Toast.makeText(this@MainActivity, "已撤销添加", Toast.LENGTH_SHORT).show()
                    }.show()
                    dialog.cancel()
                    refresh()
                }else {
                    Snackbar.make(v, "订单号 $number 已存在，无需重复添加", Snackbar.LENGTH_SHORT).setAction("") {
                        Toast.makeText(this@MainActivity, "已撤销添加", Toast.LENGTH_SHORT).show()
                    }.show()
                    dialog.cancel()
                }

            }
            it.setNegativeButton("取消") {dialog, _ ->
                Toast.makeText(this@MainActivity, "已取消", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }
        }
        dialogBuilder.show()
    }
}
