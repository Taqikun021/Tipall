package xyz.tqydn.tipall.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.databinding.ActivitySignUpBinding
import xyz.tqydn.tipall.model.DefaultResponse
import xyz.tqydn.tipall.utils.Constants.Companion.apiInterface

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val nama = binding.FieldNamaLengkap.text.toString().trim()
        val email = binding.FieldDaftarEmail.text.toString().trim()
        val pw = binding.FieldPwDaftar.text.toString().trim()
        val pwUlang = binding.FieldPwUlang.text.toString().trim()
        if (pw != pwUlang){
            binding.FieldPwUlang.error = "Password Harus Sama"
            binding.FieldPwUlang.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.FieldDaftarEmail.error = "Masukan Email dengan benar"
            binding.FieldDaftarEmail.requestFocus()
        } else if (nama.isEmpty()){
            binding.FieldNamaLengkap.error = "Nama Tidak Boleh Kosong"
            binding.FieldNamaLengkap.requestFocus()
        } else if (email.isEmpty()){
            binding.FieldDaftarEmail.error = "Email Tidak Boleh Kosong"
            binding.FieldDaftarEmail.requestFocus()
        } else if (pw.isEmpty()){
            binding.FieldPwDaftar.error = "Silahkan isi Password"
            binding.FieldPwDaftar.requestFocus()
        } else if (pw.length < 8){
            binding.FieldPwDaftar.error = "Password Minimal memiliki 8 karakter"
            binding.FieldPwDaftar.requestFocus()
        } else {
            binding.buttonDaftar.setOnClickListener{
                daftar(email, nama, pw)
            }
        }
        binding.buttonMasuklagi.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }
    }

    private fun daftar(email: String, nama: String, pw: String) {
        val call: Call<DefaultResponse> = apiInterface.createUser("", email, nama, pw, "2")
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@SignUpActivity).create()
                val inflater = LayoutInflater.from(this@SignUpActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val defaultResponse: DefaultResponse? = response.body()
                Toast.makeText(this@SignUpActivity, defaultResponse?.message, Toast.LENGTH_LONG).show()
                if (defaultResponse?.status.toString() == "201") return
                startActivity(Intent(this@SignUpActivity, VerEmailActivity::class.java))
            }
        })
    }
}