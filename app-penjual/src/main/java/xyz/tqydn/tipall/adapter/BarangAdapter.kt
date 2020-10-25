package xyz.tqydn.tipall.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_barang.view.*
import xyz.tqydn.tipall.R
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

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(barang: DataBarang){
            with(itemView){
                preference = SharedPreference(context)
                val imgBarang = Uri.parse(barang.foto_barang)
                val imgUsaha = Uri.parse(barang.foto_usaha)
                val hargaAwal = formatRupiah(barang.harga_awal.toDouble())
                val hargaJual = formatRupiah(barang.harga_jual.toDouble())
                val jarak = hitungJarak(
                    preference.getValues("lat")!!.toDouble(),
                    preference.getValues("long")!!.toDouble(),
                    barang.lat.toDouble(),
                    barang.lng.toDouble())

                itemView.namaBarang.text = barang.nama_barang
                itemView.namaPemilik.text = barang.nama_usaha
                itemView.HargaBarang.text = hargaAwal
                itemView.HargaJualBarang.text = hargaJual
                itemView.jarak.text = "${"%.2f".format(jarak)} km"
                if (barang.jumlah_stok.toInt() > 0){
                    itemView.stok.text = "Tersedia"
                } else {
                    itemView.stok.text = "Stok Kosong"
                }

                Glide.with(itemView.context)
                    .load(imgBarang)
                    .apply(RequestOptions.centerCropTransform())
                    .into(itemView.imageBarang)

                Glide.with(itemView.context)
                    .load(imgUsaha)
                    .apply(RequestOptions.circleCropTransform())
                    .into(itemView.imagePenjual)

                itemView.setOnClickListener {
                    onItemClickCallback?.onItemClicked(barang)
                    preference.setValues("barang_click", barang.id_barang)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(barang: DataBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_barang, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(barangs[position])
    }

    override fun getItemCount(): Int = barangs.size
}