package top.wteng.mexpress.activity

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.litepal.LitePal
//import top.wteng.mexpress.R
import top.wteng.mexpress.adapter.ExpressAdapter
import top.wteng.mexpress.entity.ExpressRecorder
import top.wteng.mexpress.entity.SupportedCompany

class MainActivity : AppCompatActivity() {
    private val expressCompany = SupportedCompany.companyMap
    private lateinit var recyclerView: RecyclerView
    private var allExpress = mutableListOf<ExpressRecorder>()
    private lateinit var expressAdapter: ExpressAdapter
    private lateinit var refreshView: SwipeRefreshLayout
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private var allExpressFlag = true

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //申请权限
        checkPermission()
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        //设置toolbar菜单
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 设置悬浮按钮
        val fab = findViewById<FloatingActionButton>(R.id.fab).also {
            it.setOnClickListener { view ->
                addExpress(view)
            }
        }

        //设置列表
        recyclerView =  findViewById(R.id.recycle_view)
        initExpress(allExpressFlag = allExpressFlag)
        expressAdapter = ExpressAdapter(allExpress)
        with(recyclerView) {
            this.layoutManager =
                GridLayoutManager(this@MainActivity, 1)
            this.adapter = expressAdapter
        }

        //下拉刷新
        refreshView = findViewById(R.id.swipe_refresh)
        with(refreshView) {
            this.setColorSchemeResources(R.color.colorPrimary)
            this.setOnRefreshListener {
                refresh(allExpressFlag)
            }
        }

        // 抽屉
        drawerLayout = findViewById(R.id.activity_main)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
//            actionBar.setHomeButtonEnabled(true)
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu)
        }

        //侧边栏

        //读取配置
        val pref = getSharedPreferences("setting", Context.MODE_PRIVATE)
        val customizeName = pref.getString("name", "MExpress")
        val customizeSignature = pref.getString("signature", "package tracker demo with kuaidiniao api")

        navView = findViewById(R.id.nav_view)
        navView.getHeaderView(0).also {
            it.findViewById<TextView>(R.id.username).text = customizeName
            it.findViewById<TextView>(R.id.signature).text = customizeSignature
        }
        navView.setCheckedItem(R.id.all_express)
        navView.setNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.all_express -> {
                    allExpressFlag = true
                    initExpress(allExpressFlag = allExpressFlag)
                    expressAdapter.notifyDataSetChanged()
                }
                R.id.on_the_way -> {
                    allExpressFlag = false
                    initExpress(allExpressFlag = allExpressFlag)
                    expressAdapter.notifyDataSetChanged()
                }
            }
            drawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_menu, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    refresh(allExpressFlag)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun refresh(allExpressFlag: Boolean) {
        initExpress(allExpressFlag)
        if (!refreshView.isRefreshing){
            refreshView.isRefreshing = true
        }
        expressAdapter.notifyDataSetChanged()
        refreshView.isRefreshing = false
    }


    private fun initExpress(allExpressFlag: Boolean){
        allExpress.clear()
        val expresses = when(allExpressFlag){
            true -> LitePal.findAll(ExpressRecorder::class.java)
            else -> LitePal.where("state <= ?", "2").find(ExpressRecorder::class.java)
        }.also { it.reverse() }
        allExpress.addAll(expresses)
    }

    @SuppressLint("InflateParams")
    private fun addExpress(v: View) {
        val addExpressView = layoutInflater.inflate(R.layout.add_express, null)

//        val spinnerAdapter = ArrayAdapter.createFromResource(this@MainActivity, )
        val spinnerAdapter = ArrayAdapter(this@MainActivity, R.layout.spinner_item, expressCompany.keys.toMutableList())
        val spinner = addExpressView.findViewById<AppCompatSpinner>(R.id.express_company).also {
            it.adapter = spinnerAdapter
            it.setPadding(5, 5,5,5)
            it.dropDownHorizontalOffset = -10
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
                        companyCode = expCode, state = 0, lastTime = "", lastTrace = "暂无轨迹",
                        fullTrace = """
                            [{"acceptTime": "", "acceptStation": "暂无轨迹"}]
                        """.trimIndent()
                    )
                }
                val expressFound = LitePal.select("*").where("number = ?", number).find(ExpressRecorder::class.java)
//                println(expressFound)
                if (expressFound.isEmpty()) {
                    // 添加新订单
                    newExpressRecorder!!.save()
                    Snackbar.make(v, "订单 $number 已添加", Snackbar.LENGTH_SHORT).setAction("撤销") {
                        Toast.makeText(this@MainActivity, "已撤销添加", Toast.LENGTH_SHORT).show()
                    }.show()
                    dialog.cancel()
                    refresh(allExpressFlag)
                }else {
                    Snackbar.make(v, "订单号 $number 已存在，无需重复添加", Snackbar.LENGTH_SHORT).setAction("") {
                        Toast.makeText(this@MainActivity, "已撤销添加", Toast.LENGTH_SHORT).show()
                    }.show()
                    dialog.cancel()
                }

            }
            it.setNegativeButton("取消") {dialog, _ ->
                Toast.makeText(this, "已取消", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }
        }
        dialogBuilder.show()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun checkPermission() {
        val neededPermission = mutableListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        )
        val noGrantedPermission = mutableListOf<String>()
        neededPermission.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this@MainActivity, permission)
                != PackageManager.PERMISSION_GRANTED) {
                noGrantedPermission.add(permission)
            }
        }
        if (noGrantedPermission.size > 0) {
            ActivityCompat.requestPermissions(this, noGrantedPermission.toTypedArray(), 1)
        }
    }
}
