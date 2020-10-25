package xyz.tqydn.tipall.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_tawaran.view.*
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.SharedPreference

class TawaranAdapter(private val items: List<TransaksiItem?>): RecyclerView.Adapter<TawaranAdapter.MyViewHolder>() {

    private lateinit var preference: SharedPreference
    private var onItemClickCallback: TawaranAdapter.OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: TawaranAdapter.OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: TransaksiItem?){
            with(itemView) {
                preference = SharedPreference(context)
                val imgBarang = Uri.parse(item?.foto_barang)
                val imgUsaha = Uri.parse(item?.foto_usaha)
                val distance = Constants.hitungJarak(
                        preference.getValues("lat")!!.toDouble(),
                        preference.getValues("long")!!.toDouble(),
                        item?.lat!!.toDouble(),
                        item.lng!!.toDouble()
                )

                itemView.jumlahStok.text = "${item.jumlah_barang} item"
                itemView.namaBarang.text = item.nama_barang
                itemView.namaUsaha.text = item.nama_usaha
                itemView.jarak.text = "${"%.2f".format(distance)} km"

                Glide.with(context)
                        .load(imgBarang)
                        .apply(RequestOptions.centerCropTransform())
                        .into(imageBarang)

                Glide.with(context)
                        .load(imgUsaha)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imagePenjual)

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
        val view = LayoutInflater.from(parent.context).inflate(xyz.tqydn.tipall.R.layout.row_tawaran, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}