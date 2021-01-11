package xyz.tqydn.tipall.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.model.GetInfoPenjual
import xyz.tqydn.tipall.model.GetUserInfo
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.SharedPreference

@SuppressLint("SetTextI18n")
class CadanganActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadangan)
        preference = SharedPreference(this)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        if (preference.getValues("id_user") == ""){
            getDataUser()
        }
    }

    private fun getDataUser() {
        val call: Call<GetUserInfo> = Constants.apiInterface.getUserInfo("Bearer ${preference.getValues("token")}")
        call.enqueue(object : Callback<GetUserInfo> {
            override fun onFailure(call: Call<GetUserInfo>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@CadanganActivity).create()
                val inflater = LayoutInflater.from(this@CadanganActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }

            override fun onResponse(call: Call<GetUserInfo>, response: Response<GetUserInfo>) {
                val ui: GetUserInfo? = response.body()
                if (ui?.status.toString() != "200") {
                    val photoDialog = MaterialAlertDialogBuilder(this@CadanganActivity).create()
                    val inflater = LayoutInflater.from(this@CadanganActivity)
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    tv.text = "${ui?.message}. Cek koneksi anda!"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                } else {
                    preference.setValues("username", ui?.user?.username.toString())
                    preference.setValues("email", ui?.user?.email.toString())
                    val image = ui?.user?.foto.toString()
                    preference.setValues("foto_user", image)
                    preference.setValues("no_hp", ui?.user?.no_hp.toString())
                    preference.setValues("id_user", ui?.user?.id_user.toString())
                    if (image == ""){
                        val photoDialog = MaterialAlertDialogBuilder(this@CadanganActivity).create()
                        val inflater = LayoutInflater.from(this@CadanganActivity)
                        val dialogView = inflater.inflate(R.layout.alert_error, null)
                        photoDialog.setCancelable(true)
                        val iv = dialogView.findViewById(R.id.iv) as ImageView
                        val tv = dialogView.findViewById(R.id.tv) as TextView
                        iv.setImageResource(R.drawable.ic_illustrasi_bertanya)
                        tv.text = "Harap lengkapi profil anda!"
                        photoDialog.setView(dialogView)
                        photoDialog.show()
                    }
                    getDataUsaha(ui?.user?.id_user.toString())
                }
            }
        })
    }

    private fun getDataUsaha(id: String) {
        val call: Call<GetInfoPenjual> = Constants.apiInterface.getInfoPenjual(id)
        call.enqueue(object: Callback<GetInfoPenjual> {
            override fun onResponse(call: Call<GetInfoPenjual>, response: Response<GetInfoPenjual>) {
                val ui: GetInfoPenjual? = response.body()
                if (response.code() == 404) {
                    val photoDialog = MaterialAlertDialogBuilder(this@CadanganActivity).create()
                    val inflater = LayoutInflater.from(this@CadanganActivity)
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val iv = dialogView.findViewById(R.id.iv) as ImageView
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    iv.setImageResource(R.drawable.ic_illustrasi_bertanya)
                    tv.text = "Profil anda belum lengkap. Data usaha anda kosong"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                } else {
                    preference.setValues("nama_usaha", ui?.dist_data?.nama_usaha.toString())
                    preference.setValues("alamat", ui?.dist_data?.alamat.toString())
                    val im = ui?.dist_data?.foto_usaha.toString()
                    preference.setValues("foto_usaha", im)
                    preference.setValues("id_penjual", ui?.dist_data!!.id_penjual)
                }
            }

            override fun onFailure(call: Call<GetInfoPenjual>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@CadanganActivity).create()
                val inflater = LayoutInflater.from(this@CadanganActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }
}