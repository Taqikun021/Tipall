package xyz.tqydn.tipall.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_permintaan.view.*
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipall.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipall.utils.SharedPreference

class PermintaanAdapter(private val items: List<TransaksiItem?>): RecyclerView.Adapter<PermintaanAdapter.MyViewHolder>() {

    lateinit var preference: SharedPreference
    private var onItemClickCallback: PermintaanAdapter.OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: PermintaanAdapter.OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: TransaksiItem?){
            with(itemView){
                preference = SharedPreference(context)
                val imgBarang = Uri.parse(item?.foto_barang)
                val imgUsaha = Uri.parse(item?.foto_usaha)
                val harga = formatRupiah(item?.total_tagihan!!.toDouble())
                val distance = hitungJarak(
                        preference.getValues("lat")!!.toDouble(),
                        preference.getValues("long")!!.toDouble(),
                        item.lat!!.toDouble(),
                        item.lng!!.toDouble()
                )

                itemView.namaBarang.text = item.nama_barang
                itemView.namaUsaha.text = item.nama_usaha
                itemView.HargaBarang.text = harga
                itemView.jarak.text = "${"%.2f".format(distance)} km"
                itemView.stok.text = item.jumlah_barang

                Glide.with(itemView.context)
                        .load(imgBarang)
                        .apply(RequestOptions.centerCropTransform())
                        .into(imageBarang)

                Glide.with(itemView.context)
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
        fun onItemClicked(transaksi: TransaksiItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(xyz.tqydn.tipall.R.layout.row_permintaan, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}