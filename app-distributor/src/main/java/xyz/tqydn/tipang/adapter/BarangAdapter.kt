package xyz.tqydn.tipang.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_barang.view.*
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.DataBarang
import xyz.tqydn.tipang.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipang.utils.SharedPreference

class BarangAdapter(private val stoks: List<DataBarang>): RecyclerView.Adapter<BarangAdapter.MyViewHolder>() {

    lateinit var preference: SharedPreference
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(stok: DataBarang){
            with(itemView){
                preference = SharedPreference(context)
                val img = Uri.parse(stok.foto_barang)
                val hargaAwal = formatRupiah(stok.harga_awal.toDouble())
                val hargaJual = formatRupiah(stok.harga_jual.toDouble())
                itemView.judulBarang.text = stok.nama_barang
                itemView.descBarang.text = stok.deskripsi_produk
                itemView.jumlahStok.text = stok.jumlah_stok
                itemView.HargaAwal.text = hargaAwal
                itemView.HargaJual.text = hargaJual
                Glide.with(itemView.context)
                    .load(img)
                    .apply(RequestOptions.centerCropTransform())
                    .into(itemView.imageBarang)
                itemView.setOnClickListener {
                    onItemClickCallback?.onItemClicked(stok)
                    preference.setValues("barang_click", stok.id_barang)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(stok: DataBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_barang, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stok = stoks[position]
        holder.bind(stok)
    }

    override fun getItemCount(): Int = stoks.size
}