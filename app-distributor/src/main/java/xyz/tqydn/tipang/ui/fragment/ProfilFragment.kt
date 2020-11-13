package xyz.tqydn.tipang.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_profil.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.GetDistInfo
import xyz.tqydn.tipang.model.GetUserInfo
import xyz.tqydn.tipang.ui.ListRiwayatActivity
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.EditProfilContract
import xyz.tqydn.tipang.utils.contracts.EditUsahaContract
import xyz.tqydn.tipang.utils.contracts.TambahUsahaContract

@SuppressLint("SetTextI18n")
class ProfilFragment : Fragment() {

    private lateinit var preference: SharedPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        getDataUser()
        getDataUsaha()
        editprofil.setOnClickListener{
            editProfilActivity.launch(Constants.EDIT_PROFIL)
        }
        tambahUsaha.setOnClickListener {
            tambahUsahaActivity.launch(Constants.TAMBAH_USAHA)
        }
        editusaha.setOnClickListener {
            editUsahaActivity.launch(Constants.EDIT_USAHA)
        }
        layoutRiwayat.setOnClickListener {
            startActivity(Intent(requireContext(), ListRiwayatActivity::class.java))
        }
    }

    private fun getDataUser() {
        val call: Call<GetUserInfo> = Constants.apiInterface.getUserInfo("Bearer ${preference.getValues("token")}")
        call.enqueue(object : Callback<GetUserInfo> {
            override fun onFailure(call: Call<GetUserInfo>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(requireContext()).create()
                val inflater = LayoutInflater.from(requireContext())
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }

            override fun onResponse(call: Call<GetUserInfo>, response: Response<GetUserInfo>) {
                val ui: GetUserInfo? = response.body()
                if (ui?.status.toString() != "200") {
                    val photoDialog = MaterialAlertDialogBuilder(requireContext()).create()
                    val inflater = LayoutInflater.from(requireContext())
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    tv.text = "${ui?.message}. Cek koneksi anda!"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                } else {
                    profil_nama.text = ui?.user?.username.toString()
                    profil_hape.text = ui?.user?.no_hp.toString()
                    profil_email.text = ui?.user?.email.toString()
                    val image = Uri.parse(ui?.user?.foto.toString())
                    Glide.with(requireActivity())
                            .load(image)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageProfil)
                    preference.setValues("id_user", ui?.user?.id_user.toString())
                }
            }
        })
    }

    private fun getDataUsaha() {
        val call: Call<GetDistInfo> = Constants.apiInterface.getDistInfo(preference.getValues("id_user"))
        call.enqueue(object: Callback<GetDistInfo> {
            override fun onResponse(call: Call<GetDistInfo>, response: Response<GetDistInfo>) {
                val ui: GetDistInfo? = response.body()
                if (ui?.status.toString() != "200") {
                    val photoDialog = MaterialAlertDialogBuilder(requireContext()).create()
                    val inflater = LayoutInflater.from(requireContext())
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    tv.text = "${ui?.message}. Cek koneksi anda!"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                } else {
                    tambahUsaha.visibility = View.GONE
                    editusaha.visibility = View.VISIBLE
                    namaUsaha.text = ui?.dist_data?.nama_usaha.toString()
                    alamat.text = ui?.dist_data?.alamat.toString()
                    val im = Uri.parse(ui?.dist_data?.foto_usaha.toString())
                    Glide.with(requireActivity())
                        .load(im)
                        .apply(RequestOptions.centerCropTransform())
                        .into(imageUsaha)
                    imageUsaha.visibility = View.VISIBLE
                    imageUsahaAwal.visibility = View.GONE
                    preference.setValues("id_distributor", ui?.dist_data!!.id_distributor)
                }
            }

            override fun onFailure(call: Call<GetDistInfo>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(requireContext()).create()
                val inflater = LayoutInflater.from(requireContext())
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private val editProfilActivity = registerForActivityResult(EditProfilContract()) {
        if (it != null) {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        getDataUser()
    }

    private val tambahUsahaActivity = registerForActivityResult(TambahUsahaContract()) {
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        getDataUsaha()
    }

    private val editUsahaActivity = registerForActivityResult(EditUsahaContract()) {
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        getDataUsaha()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }
}