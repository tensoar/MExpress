package top.wteng.mexpress.adapter

import de.hdodenhof.circleimageview.CircleImageView
import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import top.wteng.mexpress.activity.R
import top.wteng.mexpress.entity.TraceResultItem

//import top.wteng.mexpress.R

class ExpressTraceAdpter(private val expressTrace: List<TraceResultItem>): RecyclerView.Adapter<ExpressTraceViewHolder>() {
    private var mContext: Context? = null
//    private val fullTrace = expressTrace["fullTrace"] as MutableList<MutableMap<String, String>>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpressTraceViewHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.express_trace_item, parent, false)
        return ExpressTraceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return expressTrace.size
    }

    override fun onBindViewHolder(holder: ExpressTraceViewHolder, position: Int) {
        val oneExpressTrace = expressTrace[position]
        holder.expressContentView.text = oneExpressTrace.acceptStation
        holder.expressTimeView.text = oneExpressTrace.acceptTime
        if (position == 0){
            when (oneExpressTrace.state) {
                3 -> holder.smallImgView.setImageResource(R.drawable.right)  // 已签收
                4 -> holder.smallImgView.setImageResource(R.drawable.cha)  // 问题件
                else -> holder.smallImgView.setImageResource(R.drawable.plan_small)  //在途中
            }
            holder.expressContentView.setTextColor(Color.parseColor("#FF0000"))
            holder.topLineView.visibility = View.INVISIBLE
        }
    }

}


class ExpressTraceViewHolder(view: View): RecyclerView.ViewHolder(view) {
//    var expressItemTitleView: TextView = view.findViewById(R.id.express_item_title)
//    var expressItemStatusView: TextView = view.findViewById(R.id.express_item_status)
//    var expressItemInfoViw: TextView = view.findViewById(R.id.express_item_info)
    val expressTimeView: TextView = view.findViewById(R.id.express_time)
    val expressContentView: TextView = view.findViewById(R.id.express_content)
    val smallImgView: CircleImageView = view.findViewById(R.id.express_info_small_img)
    val topLineView: View = view.findViewById(R.id.top_line)

}