package xyz.tqydn.tipang.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.model.LoginResponse
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipang.utils.SharedPreference

class LoginActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        preferences = SharedPreference(this)
        if (preferences.getValues("isLogin").equals("1")){
            val i = Intent(this@LoginActivity, CadanganActivity::class.java)
            startActivity(i)
            finishAffinity()
        } else {
            buttonMasuk.setOnClickListener {
                val email = editTextEmail.text.toString()
                val pw = editTextPaw.text.toString()
                when {
                    email.isEmpty() -> {
                        editTextEmail.error = "Silahkan tulis Username Anda"
                        editTextEmail.requestFocus()
                    } pw.isEmpty() -> {
                    editTextPaw.error = "Silahkan tulis Password Anda"
                    editTextPaw.requestFocus()
                } else -> {
                    pushLogin(email, pw)
                }
                }
            }
            buttonBuatAkun.setOnClickListener {
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