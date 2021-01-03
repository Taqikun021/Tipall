package xyz.tqydn.tipall.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import xyz.tqydn.tipall.databinding.RowBarangBinding
import xyz.tqydn.tipall.model.DataBarang
import xyz.tqydn.tipall.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipall.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipall.utils.SharedPreference

class BarangAdapter(private val barangs: List<DataBarang>): RecyclerView.Adapter<BarangAdapter.MyViewHolder>() {

    lateinit var preference: SharedPreference
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: RowBarangBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(barang: DataBarang){
            preference = SharedPreference(itemView.context)
            val imgBarang = Uri.parse(barang.foto_barang)
            val imgUsaha = Uri.parse(barang.foto_usaha)
            val hargaAwal = formatRupiah(barang.harga_awal.toDouble())
            val jarak = hitungJarak(
                preference.getValues("lat")!!.toDouble(),
                preference.getValues("long")!!.toDouble(),
                barang.lat.toDouble(),
                barang.lng.toDouble())
            binding.namaBarang.text = barang.nama_barang
            binding.namaPemilik.text = barang.nama_usaha
            binding.HargaBarang.text = hargaAwal
            binding.jarak.text = "${"%.2f".format(jarak)} km"
            if (barang.jumlah_stok.toInt() > 0){
                binding.stok.text = "Tersedia"
            } else {
                binding.stok.text = "Stok Kosong"
            }
            Glide.with(itemView.context)
                .load(imgBarang)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imageBarang)
            Glide.with(itemView.context)
                .load(imgUsaha)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imagePenjual)
            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(barang)
                preference.setValues("barang_click", barang.id_barang)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(barang: DataBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(barangs[position])
    }

    override fun getItemCount(): Int = barangs.size
}