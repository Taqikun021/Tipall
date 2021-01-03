package xyz.tqydn.tipang.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import xyz.tqydn.tipang.databinding.ViewpagerBarangBinding
import xyz.tqydn.tipang.model.DataBarang
import xyz.tqydn.tipang.utils.Constants.Companion.formatRupiah

class BarangPagerAdapter(private val items: List<DataBarang>?): RecyclerView.Adapter<BarangPagerAdapter.MyViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ViewpagerBarangBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DataBarang){
            val img = Uri.parse(item.foto_barang)
            val harga = formatRupiah(item.harga_awal.toDouble())
            binding.namaBarang.text = item.nama_barang
            binding.descBarang.text = item.deskripsi_produk
            binding.stok.text = "Tersisa ${item.jumlah_stok} item"
            binding.HargaBarang.text = harga
            Glide.with(itemView.context)
                .load(img)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.barang)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: DataBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ViewpagerBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items!![position])
    }

    override fun getItemCount(): Int = items!!.size
}