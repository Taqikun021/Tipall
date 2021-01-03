package xyz.tqydn.tipall.ui.fragment

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.databinding.FragmentProfilBinding
import xyz.tqydn.tipall.model.GetInfoPenjual
import xyz.tqydn.tipall.model.GetUserInfo
import xyz.tqydn.tipall.ui.ListRiwayatActivity
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.Constants.Companion.EDIT_PROFIL
import xyz.tqydn.tipall.utils.Constants.Companion.EDIT_USAHA
import xyz.tqydn.tipall.utils.Constants.Companion.TAMBAH_USAHA
import xyz.tqydn.tipall.utils.SharedPreference
import xyz.tqydn.tipall.utils.contracts.EditProfilContract
import xyz.tqydn.tipall.utils.contracts.EditUsahaContract
import xyz.tqydn.tipall.utils.contracts.TambahUsahaContract

class ProfilFragment : Fragment() {

    private lateinit var preference: SharedPreference
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        getDataUser()
        getDataUsaha()
        binding.editprofil.setOnClickListener{
            editProfilActivity.launch(EDIT_PROFIL)
        }
        binding.tambahUsaha.setOnClickListener {
            tambahUsahaActivity.launch(TAMBAH_USAHA)
        }
        binding.editusaha.setOnClickListener {
            editUsahaActivity.launch(EDIT_USAHA)
        }
        binding.layoutRiwayat.setOnClickListener {
            startActivity(Intent(requireContext(), ListRiwayatActivity::class.java))
        }
    }

    private fun getDataUsaha() {
        val call: Call<GetInfoPenjual> = Constants.apiInterface.getInfoPenjual(preference.getValues("id_user"))
        call.enqueue(object: Callback<GetInfoPenjual> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<GetInfoPenjual>, response: Response<GetInfoPenjual>) {
                val ui: GetInfoPenjual? = response.body()
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
                    binding.tambahUsaha.visibility = View.GONE
                    binding.editusaha.visibility = View.VISIBLE
                    binding.namaUsaha.text = ui?.dist_data?.nama_usaha.toString()
                    binding.alamat.text = ui?.dist_data?.alamat.toString()
                    val im = Uri.parse(ui?.dist_data?.foto_usaha.toString())
                    Glide.with(requireActivity())
                        .load(im)
                        .apply(RequestOptions.centerCropTransform())
                        .into(binding.imageUsaha)
                    binding.imageUsaha.visibility = View.VISIBLE
                    binding.imageUsahaAwal.visibility = View.GONE
                    preference.setValues("id_penjual", ui?.dist_data?.id_penjual.toString())
                }
            }

            override fun onFailure(call: Call<GetInfoPenjual>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(requireContext()).create()
                val inflater = LayoutInflater.from(requireContext())
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun getDataUser() {
        val call: Call<GetUserInfo> = Constants.apiInterface.getUserInfo("Bearer ${preference.getValues("token")}")
        call.enqueue(object: Callback<GetUserInfo> {
            override fun onFailure(call: Call<GetUserInfo>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(requireContext()).create()
                val inflater = LayoutInflater.from(requireContext())
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }

            @SuppressLint("SetTextI18n")
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
                    binding.profilNama.text = ui?.user?.username.toString()
                    binding.profilHape.text = ui?.user?.no_hp.toString()
                    binding.profilEmail.text = ui?.user?.email.toString()
                    val image = Uri.parse(ui?.user?.foto.toString())
                    Glide.with(requireActivity())
                        .load(image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imageProfil)
                    preference.setValues("id_user", ui?.user?.id_user.toString())
                }
            }
        })
    }

    private val editProfilActivity = registerForActivityResult(EditProfilContract()){
        if (it != null){
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}