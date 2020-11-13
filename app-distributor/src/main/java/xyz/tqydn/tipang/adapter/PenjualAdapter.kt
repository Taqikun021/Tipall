package xyz.tqydn.tipang.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_penjual.view.*
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.DataPenjual
import xyz.tqydn.tipang.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipang.utils.SharedPreference

class PenjualAdapter(private val penjuals: List<DataPenjual>): RecyclerView.Adapter<PenjualAdapter.MyViewHolder>() {

    lateinit var preference: SharedPreference
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(penjual: DataPenjual){
            with(itemView){
                preference = SharedPreference(context)
                val imgUsaha = Uri.parse(penjual.foto_usaha)
                val imgProfil = Uri.parse(penjual.foto)
                val jarak = hitungJarak(
                    preference.getValues("lat")!!.toDouble(),
                    preference.getValues("long")!!.toDouble(),
                    penjual.lat.toDouble(),
                    penjual.lng.toDouble())
                Glide.with(itemView.context)
                    .load(imgUsaha)
                    .apply(RequestOptions.centerCropTransform())
                    .into(itemView.imageUsaha)
                Glide.with(itemView.context)
                    .load(imgProfil)
                    .apply(RequestOptions.circleCropTransform())
                    .into(itemView.imagePenjual)
                itemView.namaUsaha.text = penjual.nama_usaha
                itemView.jarak.text = "${"%.2f".format(jarak)} km dari lokasi Anda."
                if (penjual.jenis_kelamin != "Perempuan") {
                    itemView.namaPemilik.text = "Bapak ${penjual.username}"
                } else {
                    itemView.namaPemilik.text = "Ibu ${penjual.username}"
                }
                itemView.setOnClickListener {
                    onItemClickCallback?.onItemClicked(penjual)
                    preference.setValues("penjual_click", penjual.id_penjual)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(penjual: DataPenjual)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_penjual, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(penjuals[position])
    }

    override fun getItemCount(): Int = penjuals.size
}