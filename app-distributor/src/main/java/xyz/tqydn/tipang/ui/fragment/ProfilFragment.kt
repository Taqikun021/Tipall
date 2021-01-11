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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.databinding.FragmentProfilBinding
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
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())
        binding.profilNama.text = preference.getValues("username")
        binding.profilHape.text = preference.getValues("no_hp")
        binding.profilEmail.text = preference.getValues("email")
        val image = Uri.parse(preference.getValues("foto_user"))
        Glide.with(requireActivity())
                .load(image)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imageProfil)
        binding.tambahUsaha.visibility = View.GONE
        binding.editusaha.visibility = View.VISIBLE
        binding.namaUsaha.text = preference.getValues("nama_usaha")
        binding.alamat.text = preference.getValues("alamat")
        val im = Uri.parse(preference.getValues("foto_usaha"))
        Glide.with(requireActivity())
                .load(im)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imageUsaha)
        binding.imageUsaha.visibility = View.VISIBLE
        binding.imageUsahaAwal.visibility = View.GONE

        binding.editprofil.setOnClickListener{
            editProfilActivity.launch(Constants.EDIT_PROFIL)
        }
        binding.tambahUsaha.setOnClickListener {
            tambahUsahaActivity.launch(Constants.TAMBAH_USAHA)
        }
        binding.editusaha.setOnClickListener {
            editUsahaActivity.launch(Constants.EDIT_USAHA)
        }
        binding.layoutRiwayat.setOnClickListener {
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
                    binding.profilNama.text = ui?.user?.username.toString()
                    binding.profilHape.text = ui?.user?.no_hp.toString()
                    binding.profilEmail.text = ui?.user?.email.toString()
                    val image = Uri.parse(ui?.user?.foto.toString())
                    Glide.with(requireActivity())
                            .load(image)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.imageProfil)
                    preference.setValues("username", ui?.user?.username.toString())
                    preference.setValues("no_hp", ui?.user?.no_hp.toString())
                    preference.setValues("email", ui?.user?.email.toString())
                    val images = ui?.user?.foto.toString()
                    preference.setValues("foto_user", images)
                    preference.setValues("id_user", ui?.user?.id_user.toString())
                    preference.setValues("id_user", ui?.user?.id_user.toString())
                    getDataUsaha(ui?.user?.id_user.toString())
                }
            }
        })
    }

    private fun getDataUsaha(id: String) {
        val call: Call<GetDistInfo> = Constants.apiInterface.getDistInfo(id)
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
        getDataUsaha(preference.getValues("id_user").toString())
    }

    private val editUsahaActivity = registerForActivityResult(EditUsahaContract()) {
        if (it != null){
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        getDataUsaha(preference.getValues("id_user").toString())
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