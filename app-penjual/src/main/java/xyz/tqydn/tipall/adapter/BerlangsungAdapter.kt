package xyz.tqydn.tipall.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import xyz.tqydn.tipall.databinding.RowBerlangsungBinding
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipall.utils.SharedPreference

class BerlangsungAdapter(private val items: List<TransaksiItem?>): RecyclerView.Adapter<BerlangsungAdapter.MyViewHolder>() {

    private lateinit var preference: SharedPreference
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: RowBerlangsungBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TransaksiItem?){
            preference = SharedPreference(itemView.context)
            val imgBarang = Uri.parse(item?.foto)
            binding.namaUsaha.text = item?.nama_usaha
            binding.jumlahStok.text = "${item?.jumlah_barang} item"
            binding.waktu.text = "Dimulai Sejak ${item?.waktu_mulai}"
            binding.tagihan.text = formatRupiah(item?.total_tagihan!!.toDouble())
            if (item.jenis_kelamin == "Perempuan"){
                binding.namaPemilik.text = "Ibu ${item.username}"
            } else {
                binding.namaPemilik.text = "Bapak ${item.username}"
            }
            Glide.with(itemView.context)
                .load(imgBarang)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imagePenjual)
            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(item)
                preference.setValues("trans_click", item.id_transaksi.toString())
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: TransaksiItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowBerlangsungBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}