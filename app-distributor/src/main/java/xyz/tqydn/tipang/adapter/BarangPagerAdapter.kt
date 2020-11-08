package xyz.tqydn.tipang.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.viewpager_barang.view.*
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.DataBarang
import xyz.tqydn.tipang.utils.Constants.Companion.formatRupiah

class BarangPagerAdapter(private val items: List<DataBarang>?): RecyclerView.Adapter<BarangPagerAdapter.MyViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DataBarang){
            with(itemView){
                val img = Uri.parse(item.foto_barang)
                val harga = formatRupiah(item.harga_awal.toDouble())

                itemView.namaBarang.text = item.nama_barang
                itemView.descBarang.text = item.deskripsi_produk
                itemView.stok.text = "Tersisa ${item.jumlah_stok} item"
                itemView.HargaBarang.text = harga

                Glide.with(context)
                    .load(img)
                    .apply(RequestOptions.centerCropTransform())
                    .into(barang)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: DataBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewpager_barang, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items!![position])
    }

    override fun getItemCount(): Int = items!!.size
}