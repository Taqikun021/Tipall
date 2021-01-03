package xyz.tqydn.tipall.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import xyz.tqydn.tipall.databinding.RowPermintaanBinding
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipall.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipall.utils.SharedPreference

class PermintaanAdapter(private val items: List<TransaksiItem?>): RecyclerView.Adapter<PermintaanAdapter.MyViewHolder>() {

    lateinit var preference: SharedPreference
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: RowPermintaanBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TransaksiItem?){
            preference = SharedPreference(itemView.context)
            val imgBarang = Uri.parse(item?.foto_barang)
            val imgUsaha = Uri.parse(item?.foto_usaha)
            val harga = formatRupiah(item?.total_tagihan!!.toDouble())
            val distance = hitungJarak(
                preference.getValues("lat")!!.toDouble(),
                preference.getValues("long")!!.toDouble(),
                item.lat!!.toDouble(),
                item.lng!!.toDouble())
            binding.namaBarang.text = item.nama_barang
            binding.namaUsaha.text = item.nama_usaha
            binding.HargaBarang.text = harga
            binding.jarak.text = "${"%.2f".format(distance)} km"
            binding.stok.text = item.jumlah_barang
            Glide.with(itemView.context)
                .load(imgBarang)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imageBarang)
            Glide.with(itemView.context)
                .load(imgUsaha)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imagePenjual)
            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(item)
                preference.setValues("trans_click", item.id_transaksi.toString())
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(transaksi: TransaksiItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowPermintaanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}