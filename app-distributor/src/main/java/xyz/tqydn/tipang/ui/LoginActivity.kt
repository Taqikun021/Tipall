package xyz.tqydn.tipang.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.model.LoginResponse
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.databinding.ActivityLoginBinding
import xyz.tqydn.tipang.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipang.utils.SharedPreference

class LoginActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreference
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = SharedPreference(this)
        if (preferences.getValues("isLogin").equals("1")){
            val i = Intent(this@LoginActivity, CadanganActivity::class.java)
            startActivity(i)
            finishAffinity()
        } else {
            binding.buttonMasuk.setOnClickListener {
                val email = binding.editTextEmail.text.toString()
                val pw = binding.editTextPaw.text.toString()
                when {
                    email.isEmpty() -> {
                        binding.editTextEmail.error = "Silahkan tulis Username Anda"
                        binding.editTextEmail.requestFocus()
                    } pw.isEmpty() -> {
                        binding.editTextPaw.error = "Silahkan tulis Password Anda"
                        binding.editTextPaw.requestFocus()
                    } else -> {
                        pushLogin(email, pw)
                    }
                }
            }
            binding.buttonBuatAkun.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }
        }
    }

    private fun pushLogin(email: String, pw: String) {
        val call: Call<LoginResponse> = apiInterface.login(email, pw)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@LoginActivity).create()
                val inflater = LayoutInflater.from(this@LoginActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val loginResponse: LoginResponse? = response.body()
                Toast.makeText(this@LoginActivity, loginResponse?.message, Toast.LENGTH_LONG).show()
                if (loginResponse?.status.toString() != "422") {
                    preferences.setValues("token", loginResponse?.token.toString())
                    preferences.setValues("isLogin", "1")
                    startActivity(Intent(this@LoginActivity, CadanganActivity::class.java))
                }
            }
        })
    }
}