package xyz.tqydn.tipang.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import xyz.tqydn.tipang.databinding.RowBarangBinding
import xyz.tqydn.tipang.model.DataBarang
import xyz.tqydn.tipang.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipang.utils.SharedPreference

class BarangAdapter(private val stoks: List<DataBarang>): RecyclerView.Adapter<BarangAdapter.MyViewHolder>() {

    lateinit var preference: SharedPreference
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: RowBarangBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(stok: DataBarang){
            preference = SharedPreference(itemView.context)
            val img = Uri.parse(stok.foto_barang)
            val hargaAwal = formatRupiah(stok.harga_awal.toDouble())
            val hargaJual = formatRupiah(stok.harga_jual.toDouble())
            binding.judulBarang.text = stok.nama_barang
            binding.descBarang.text = stok.deskripsi_produk
            binding.jumlahStok.text = stok.jumlah_stok
            binding.HargaAwal.text = hargaAwal
            binding.HargaJual.text = hargaJual
            Glide.with(itemView.context)
                .load(img)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imageBarang)
            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(stok)
                preference.setValues("barang_click", stok.id_barang)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(stok: DataBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RowBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stok = stoks[position]
        holder.bind(stok)
    }

    override fun getItemCount(): Int = stoks.size
}