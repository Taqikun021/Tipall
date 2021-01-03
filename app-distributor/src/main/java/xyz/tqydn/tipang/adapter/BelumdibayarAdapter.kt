package xyz.tqydn.tipang.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import xyz.tqydn.tipang.databinding.RowBelumDibayarBinding
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference

class BelumdibayarAdapter(private val items: List<TransaksiItem?>): RecyclerView.Adapter<BelumdibayarAdapter.MyViewHolder>() {

    private lateinit var preference: SharedPreference
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: RowBelumDibayarBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TransaksiItem?){
            preference = SharedPreference(itemView.context)
            val total = Constants.formatRupiah(item?.total_tagihan!!.toDouble())
            val imgBarang = Uri.parse(item.foto)
            val a1 = preference.getValues("lat")
            val a2 = preference.getValues("long")
            val b1 = item.lat
            val b2 = item.lng
            val distance = Constants.hitungJarak(a1!!.toDouble(), a2!!.toDouble(), b1!!.toDouble(), b2!!.toDouble())
            binding.namaBarang.text = item.nama_barang
            binding.namaUsaha.text = item.nama_usaha
            binding.waktu.text = "Dimulai sejak ${item.waktu_mulai}"
            binding.tagihan.text = "$total Belum Dibayar"
            binding.jumlahStok.text = "${item.jumlah_barang} item"
            binding.jarak.text = "${"%.2f".format(distance)} km"
            if (item.jenis_kelamin == "Perempuan"){
                binding.namaPemilik.text = "Ibu ${item.username}"
            } else {
                binding.namaPemilik.text = "Bapak ${item.username}"
            }
            binding.imagePenjual.visibility = View.GONE
            binding.imagePenjualFix.visibility = View.VISIBLE
            Glide.with(itemView.context)
                .load(imgBarang)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imagePenjualFix)
            binding.hubungi.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/62${item.no_hp}"))
                startActivity(itemView.context, i, null)
            }
            binding.petunjukArah.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.co.id/maps/dir/$a1,$a2/$b1,$b2"))
                startActivity(itemView.context, i, null)
            }
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
        val view = RowBelumDibayarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}