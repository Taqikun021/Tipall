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
import kotlinx.android.synthetic.main.row_belum_dibayar.view.*
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipang.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipang.utils.SharedPreference

class BelumdibayarAdapter(private val items: List<TransaksiItem?>): RecyclerView.Adapter<BelumdibayarAdapter.MyViewHolder>() {

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
                val total = formatRupiah(item?.total_tagihan!!.toDouble())
                val imgBarang = Uri.parse(item.foto)
                val a1 = preference.getValues("lat")
                val a2 = preference.getValues("long")
                val b1 = item.lat
                val b2 = item.lng
                val distance = hitungJarak(
                    a1!!.toDouble(),
                    a2!!.toDouble(),
                    b1!!.toDouble(),
                    b2!!.toDouble()
                )

                itemView.namaBarang.text = item.nama_barang
                itemView.namaUsaha.text = item.nama_usaha
                itemView.waktu.text = "Dimulai sejak ${item.waktu_mulai}"
                itemView.tagihan.text = "$total Belum Dibayar"
                itemView.jumlahStok.text = "${item.jumlah_barang} item"
                itemView.jarak.text = "${"%.2f".format(distance)} km"
                if (item.jenis_kelamin == "Perempuan"){
                    itemView.namaPemilik.text = "Ibu ${item.username}"
                } else {
                    itemView.namaPemilik.text = "Bapak ${item.username}"
                }

                itemView.imagePenjual.visibility = View.GONE
                itemView.imagePenjualFix.visibility = View.VISIBLE
                Glide.with(context)
                    .load(imgBarang)
                    .apply(RequestOptions.centerCropTransform())
                    .into(imagePenjualFix)

                itemView.hubungi.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/62${item.no_hp}"))
                    startActivity(itemView.context, i, null)
                }
                itemView.petunjukArah.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.co.id/maps/dir/$a1,$a2/$b1,$b2"))
                    startActivity(itemView.context, i, null)
                }
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
        val view = LayoutInflater.from(parent.context).inflate(xyz.tqydn.tipang.R.layout.row_belum_dibayar, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}