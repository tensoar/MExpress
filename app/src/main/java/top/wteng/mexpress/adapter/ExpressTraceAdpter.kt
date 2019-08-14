package top.wteng.mexpress.adapter

import de.hdodenhof.circleimageview.CircleImageView
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import top.wteng.mexpress.R

class ExpressTraceAdpter(private val expressTraceList: MutableList<MutableMap<String, String>>): RecyclerView.Adapter<ExpressTraceViewHolder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpressTraceViewHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.express_info_item, parent, false)
        return ExpressTraceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return expressTraceList.size
    }

    override fun onBindViewHolder(holder: ExpressTraceViewHolder, position: Int) {
        val oneExpressTrace = expressTraceList.get(position)
        holder.expressContentView.text = oneExpressTrace["acceptStation"]
        holder.expressTimeView.text = oneExpressTrace["acceptTime"]
        if (position == 0){
            holder.smallImgView.setImageResource(R.drawable.plan_small)
            holder.expressContentView.setTextColor(Color.parseColor("#FF0000"))
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

}