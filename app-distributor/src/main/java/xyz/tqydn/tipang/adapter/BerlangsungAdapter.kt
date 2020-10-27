package xyz.tqydn.tipang.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_berlangsung.view.*
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipang.utils.SharedPreference

class BerlangsungAdapter(private val items: List<TransaksiItem?>): RecyclerView.Adapter<BerlangsungAdapter.MyViewHolder>() {

    private lateinit var preference: SharedPreference
    private var onItemClickCallback: BerlangsungAdapter.OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: BerlangsungAdapter.OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: TransaksiItem?){
            with(itemView){
                preference = SharedPreference(context)
                val imgBarang = Uri.parse(item?.foto_barang)
                val distance = hitungJarak(
                    preference.getValues("lat")!!.toDouble(),
                    preference.getValues("long")!!.toDouble(),
                    item?.lat!!.toDouble(),
                    item.lng!!.toDouble()
                )

                itemView.namaBarang.text = item.nama_barang
                itemView.namaUsaha.text = item.nama_usaha
                itemView.jumlahStok.text = "${item.jumlah_barang} item"
                itemView.jarak.text = "${"%.2f".format(distance)} km"
                itemView.waktu.text = "Dimulai Sejak ${item.waktu_mulai}"

                Glide.with(context)
                    .load(imgBarang)
                    .apply(RequestOptions.centerCropTransform())
                    .into(imageBarang)

                itemView.setOnClickListener {
                    onItemClickCallback?.onItemClicked(item)
                    preference.setValues("trans_click", item.id_transaksi.toString())
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: TransaksiItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(xyz.tqydn.tipang.R.layout.row_berlangsung, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}