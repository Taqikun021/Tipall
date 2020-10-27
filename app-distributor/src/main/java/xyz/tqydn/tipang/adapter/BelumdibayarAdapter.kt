package xyz.tqydn.tipang.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_belum_dibayar.view.*
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipang.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipang.utils.SharedPreference

class BelumdibayarAdapter(private val items: List<TransaksiItem?>): RecyclerView.Adapter<BelumdibayarAdapter.MyViewHolder>() {

    private lateinit var preference: SharedPreference
    private var onItemClickCallback: BelumdibayarAdapter.OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: BelumdibayarAdapter.OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: TransaksiItem?){
            with(itemView){
                preference = SharedPreference(context)
                val total = formatRupiah(item?.total_tagihan!!.toDouble())
                val imgBarang = Uri.parse(item.foto_barang)
                val distance = hitungJarak(
                    preference.getValues("lat")!!.toDouble(),
                    preference.getValues("long")!!.toDouble(),
                    item.lat!!.toDouble(),
                    item.lng!!.toDouble()
                )

                itemView.namaBarang.text = item.nama_barang
                itemView.namaUsaha.text = item.nama_usaha
                itemView.waktu.text = item.waktu_mulai
                itemView.tagihan.text = "${total} Belum Dibayar"
                itemView.jumlahStok.text = "${item.jumlah_barang} item"
                itemView.jarak.text = "${"%.2f".format(distance)} km"

                itemView.imageBarang.visibility = View.GONE
                itemView.imageBarangFix.visibility = View.VISIBLE
                Glide.with(context)
                    .load(imgBarang)
                    .apply(RequestOptions.centerCropTransform())
                    .into(imageBarangFix)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: TransaksiItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(xyz.tqydn.tipang.R.layout.row_belum_dibayar, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}