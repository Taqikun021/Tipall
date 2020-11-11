package xyz.tqydn.tipang.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_riwayat.view.*
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference

class RiwayatAdapter(private val items: List<TransaksiItem?>): RecyclerView.Adapter<RiwayatAdapter.MyViewHolder>() {

    private lateinit var preference: SharedPreference
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TransaksiItem?){
            with(itemView){
                preference = SharedPreference(context)
                val imgBarang = Uri.parse(item?.foto)

                itemView.namaUsaha.text = item?.nama_usaha
                itemView.jumlahStok.text = "${item?.jumlah_barang} item"
                itemView.waktu.text = "Dimulai Sejak ${item?.waktu_mulai}"
                itemView.tagihan.text = Constants.formatRupiah(item?.total_tagihan!!.toDouble())
                if (item.jenis_kelamin == "Perempuan"){
                    itemView.namaPemilik.text = "Ibu ${item.username}"
                } else {
                    itemView.namaPemilik.text = "Bapak ${item.username}"
                }

                Glide.with(context)
                    .load(imgBarang)
                    .apply(RequestOptions.centerCropTransform())
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
        val view = LayoutInflater.from(parent.context).inflate(xyz.tqydn.tipang.R.layout.row_riwayat, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}