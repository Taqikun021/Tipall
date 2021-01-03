package xyz.tqydn.tipall.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import xyz.tqydn.tipall.databinding.RowTawaranBinding
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.SharedPreference

class TawaranAdapter(private val items: List<TransaksiItem?>): RecyclerView.Adapter<TawaranAdapter.MyViewHolder>() {

    private lateinit var preference: SharedPreference
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: RowTawaranBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TransaksiItem?){
            preference = SharedPreference(itemView.context)
            val imgBarang = Uri.parse(item?.foto_barang)
            val imgUsaha = Uri.parse(item?.foto_usaha)
            val distance = Constants.hitungJarak(
                preference.getValues("lat")!!.toDouble(),
                preference.getValues("long")!!.toDouble(),
                item?.lat!!.toDouble(),
                item.lng!!.toDouble())
            binding.jumlahStok.text = "${item.jumlah_barang} item"
            binding.namaBarang.text = item.nama_barang
            binding.namaUsaha.text = item.nama_usaha
            binding.jarak.text = "${"%.2f".format(distance)} km"
            Glide.with(itemView.context)
                .load(imgBarang)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imageBarang)
            Glide.with(itemView.context)
                .load(imgUsaha)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imagePenjual)
            binding.terima.setOnClickListener {
                onItemClickCallback?.onItemClicked(item)
                preference.setValues("trans_click", item.id_transaksi.toString())
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: TransaksiItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowTawaranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
