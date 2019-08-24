package top.wteng.mexpress.adapter

//import android.support.v7.widget.RecyclerView
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import de.hdodenhof.circleimageview.CircleImageView
import org.litepal.LitePal
//import top.wteng.mexpress.R
import top.wteng.mexpress.activity.ExpressActivity
import top.wteng.mexpress.activity.R
import top.wteng.mexpress.entity.ExpressRecorder
//import android.R



class ExpressAdapter(private val expressList: MutableList<ExpressRecorder>): RecyclerView.Adapter<ExpressViewHolder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpressViewHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.express_recorder_item, parent, false)
        val holder =  ExpressViewHolder(view)

        holder.itemView.setOnClickListener { v ->
            val position = holder.adapterPosition
            val curExpress = expressList[position]
            val intent = Intent(mContext, ExpressActivity::class.java).also {
                it.putExtra("expCode", curExpress.companyCode)
                it.putExtra("expCompanyName", curExpress.company)
                it.putExtra("expNo", curExpress.number)
                it.putExtra("expNote", curExpress.note)
                it.putExtra("expId", curExpress.id)
            }
            mContext?.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener { v ->
            val position = holder.adapterPosition
            val curExpress = expressList[position]
            val alertDialog = mContext?.let { content ->
                AlertDialog.Builder(content).also {dialog ->
                    dialog.setMessage("删除此订单?")
                    dialog.setPositiveButton("删除") { d, _ ->
                        LitePal.delete(ExpressRecorder::class.java, curExpress.id)
                        expressList.removeAt(position)
                        d.cancel()
                        notifyDataSetChanged()
                        Toast.makeText(content, "订单 ${curExpress.number} 已删除", Toast.LENGTH_SHORT).show()
                    }
                    dialog.setNegativeButton("取消") { d, _ ->
                        d.cancel()
                        Toast.makeText(content, "已取消", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            alertDialog?.show()
            return@setOnLongClickListener true
        }

        return holder
    }

    override fun getItemCount(): Int {
        return expressList.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ExpressViewHolder, position: Int) {
        val expressRecorder = expressList[position]
        holder.expressItemTitleView.text = when(expressRecorder.note) {
            "" -> expressRecorder.company
            else -> expressRecorder.note
        }
        holder.expressItemContentView.text = expressRecorder.company
        with(holder.expressItemStateView){
            this.text = when(expressRecorder.state) {
                0 -> "无轨迹"
                1 -> "已揽收"
                2 -> {
                    this.setTextColor(Color.parseColor("#7FFF00"))
                    "在途中"
                }
                3 -> "已签收"
                4 -> {
                    this.setTextColor(Color.parseColor("#FF0000"))
                    "问题件"
                }
                else -> "无轨迹"
            }
        }
        val logoName = expressRecorder.companyCode?.toLowerCase() + "_logo"
        val picId = mContext?.resources?.getIdentifier(logoName,"drawable", mContext?.packageName)
        if (picId!! > 0) {
            holder.expressLogoView.setImageResource(picId)
        }

    }

}


class ExpressViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var expressItemTitleView: TextView = view.findViewById(R.id.express_item_title)
    var expressItemContentView: TextView = view.findViewById(R.id.express_item_content)
//    var expressItemInfoViw: TextView = view.findViewById(R.id.express_recoder_item)
    var expressItemStateView: TextView = view.findViewById(R.id.express_item_status)
    var expressLogoView: CircleImageView = view.findViewById(R.id.express_img)

}