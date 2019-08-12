package top.wteng.mexpress.adapter

//import android.support.v7.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import top.wteng.mexpress.R
import top.wteng.mexpress.activity.ExpressActivity
import top.wteng.mexpress.entity.ExpressRecorder

class ExpressAdapter(private val expressList: MutableList<ExpressRecorder>): RecyclerView.Adapter<ExpressViewHolder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpressViewHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.express_item, parent, false)
        val holder =  ExpressViewHolder(view)
        holder.itemView.setOnClickListener { v ->
            val position = holder.adapterPosition
            val curExpress = expressList[position]
            val intent = Intent(mContext, ExpressActivity::class.java).also {
                it.putExtra("expCode", curExpress.companyCode)
                it.putExtra("expCompanyName", curExpress.company)
                it.putExtra("expNo", curExpress.number)
                it.putExtra("expNote", curExpress.note)
            }
            mContext?.startActivity(intent)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return expressList.size
    }

    override fun onBindViewHolder(holder: ExpressViewHolder, position: Int) {
        val expressRecorder = expressList.get(position)
        holder.expressItemTitleView.text = expressRecorder.company
        holder.expressItemStatusView.text = expressRecorder.number
        holder.expressItemInfoViw.text = "详情 >"
    }

}


class ExpressViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var expressItemTitleView: TextView = view.findViewById(R.id.express_item_title)
    var expressItemStatusView: TextView = view.findViewById(R.id.express_item_status)
    var expressItemInfoViw: TextView = view.findViewById(R.id.express_item_info)

}