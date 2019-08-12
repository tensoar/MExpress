package top.wteng.wtexpress.activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import top.wteng.wtexpress.R
import top.wteng.wtexpress.util.OrderTraceUtil
import kotlin.math.log

class ExpressActivity : AppCompatActivity() {
    lateinit var expressContentText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_express)

        val expCode = intent.getStringExtra("expCode")
        val expCompanyName = intent.getStringExtra("expCompanyName")
        val expNo = intent.getStringExtra("expNo")
        val expNote = intent.getStringExtra("expNote")

        val expressToolbar = findViewById<Toolbar>(R.id.express_toolbar)
        val expressColToolBar = findViewById<CollapsingToolbarLayout>(R.id.express_col_toolbar).also {
            it.title = when(expNote) {
                null -> expCompanyName
                "" -> expCompanyName
                else -> expNote
            }
        }
        var expressImage = findViewById<ImageView>(R.id.express_toolbar_image).also {
            it.setImageResource(R.drawable.plan)
        }
        expressContentText = findViewById(R.id.express_content)

        setSupportActionBar(expressToolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        ExpressTraceTask().execute(expCode, expNo)

//        val expressTrace = OrderTraceUtil.getTraceMap(expCode, expNo)
//        getExpressTraceTask().execute(expCode, expNo)
//        Thread{
//            val expressTrace = OrderTraceUtil.getTraceMap("STO", "3720510705418")
//            println("---------------------------------")
//            println(expressTrace)
//            val str = "fqeuireroueoiashfjdakslfjndkl;asfdakfjadskl;fjdalks;jfkd" +
//                    "fdsakl;fjdsak;fjdaks;lfjkd;sajfkdsaf;jdsakl;fjdkl;sajfkdsa;" +
//                    "fdaskljfjhd;kasjfkl;dasjfvkocd;sahjfkojfkjdask" +
//                    "fdakl;sjfd;askjfkldsapjflpdsj'a'fvpdjkasfpolkdopsa" +
//                    "fdsaklfjkdsaljvdsafjdsplafjpodajfopedoapfikopeasikfpodikdpoas" +
//                    "fdajslkfjdpolaskjfopdsajkfopdsajkpofkjdopsajfopdsajkopfgvdsajop'" +
//                    "fjkldajfpdosjfopdsajfd[askfopdsjapoifjdpoajfldasjfg;ldsajvp" +
//                    "fdaslkfjpdsajfopdajfl;vdjspokajpofasjkglkdasjfpdskafldsjapvgodas" +
//                    "fjdaslkfjdpaslkfcdasjfvopd'[asipofkdsjfgdasofopdas'ujgodaskfdsajfokd[a" +
//                    "fdolas;fjpoasjfkopdsajfdas[pkfdp[safjkopdas'ikfp[dasfpkjdasfkdsa" +
//                    "fjapofjkdasp[fjkdaposjopjopdasfpdsaifpadsifpdiasp[fpda[fd[pasfodp[as"
//            showExpressContent(expressTrace["fullTrace"].toString() + str)
//        }
    }

//    fun showExpressContent(content: String) {
//        println(content)
//        runOnUiThread { expressContentText.text = content }
//    }

    inner class ExpressTraceTask: AsyncTask<String, Int, MutableMap<String, Any>>() {
        var progressBar: ProgressBar? = null
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            progressBar?.progress = values[0] ?: 0
        }

        override fun onPostExecute(result: MutableMap<String, Any>?) {
            super.onPostExecute(result)
            expressContentText.text = result!!["fullTrace"].toString()
            Log.d("task", "线程结束")
        }

        override fun doInBackground(vararg params: String): MutableMap<String, Any> {
            Log.d("task", "expCode = ${params[0]}, expNo = ${params[1]}")
            for(i in (0 until 1000)) {
                publishProgress(i)
            }
//        OrderTraceUtil
            val expressTrace = OrderTraceUtil.getTraceMap(params[0], params[1])
            println("---------------------------------")
            println(expressTrace)
            val str = "fqeuireroueoiashfjdakslfjndkl;asfdakfjadskl;fjdalks;jfkd" +
                    "fdsakl;fjdsak;fjdaks;lfjkd;sajfkdsaf;jdsakl;fjdkl;sajfkdsa;" +
                    "fdaskljfjhd;kasjfkl;dasjfvkocd;sahjfkojfkjdask" +
                    "fdakl;sjfd;askjfkldsapjflpdsj'a'fvpdjkasfpolkdopsa" +
                    "fdsaklfjkdsaljvdsafjdsplafjpodajfopedoapfikopeasikfpodikdpoas" +
                    "fdajslkfjdpolaskjfopdsajkfopdsajkpofkjdopsajfopdsajkopfgvdsajop'" +
                    "fjkldajfpdosjfopdsajfd[askfopdsjapoifjdpoajfldasjfg;ldsajvp" +
                    "fdaslkfjpdsajfopdajfl;vdjspokajpofasjkglkdasjfpdskafldsjapvgodas" +
                    "fjdaslkfjdpaslkfcdasjfvopd'[asipofkdsjfgdasofopdas'ujgodaskfdsajfokd[a" +
                    "fdolas;fjpoasjfkopdsajfdas[pkfdp[safjkopdas'ikfp[dasfpkjdasfkdsa" +
                    "fjapofjkdasp[fjkdaposjopjopdasfpdsaifpadsifpdiasp[fpda[fd[pasfodp[as"
//        MainActivity.s
            return expressTrace
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("task","子线程开始 ...")
        }
    }
}


