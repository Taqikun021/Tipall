package xyz.tqydn.tipall.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.model.DefaultResponse
import xyz.tqydn.tipall.utils.Constants.Companion.apiInterface

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val nama = FieldNamaLengkap.text.toString().trim()
        val email = FieldDaftarEmail.text.toString().trim()
        val pw = FieldPwDaftar.text.toString().trim()
        val pwUlang = FieldPwUlang.text.toString().trim()

        if (pw != pwUlang){
            FieldPwUlang.error = "Password Harus Sama"
            FieldPwUlang.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            FieldDaftarEmail.error = "Masukan Email dengan benar"
            FieldDaftarEmail.requestFocus()
        } else if (nama.isEmpty()){
            FieldNamaLengkap.error = "Nama Tidak Boleh Kosong"
            FieldNamaLengkap.requestFocus()
        } else if (email.isEmpty()){
            FieldDaftarEmail.error = "Email Tidak Boleh Kosong"
            FieldDaftarEmail.requestFocus()
        } else if (pw.isEmpty()){
            FieldPwDaftar.error = "Silahkan isi Password"
            FieldPwDaftar.requestFocus()
        } else if (pw.length < 8){
            FieldPwDaftar.error = "Password Minimal memiliki 8 karakter"
            FieldPwDaftar.requestFocus()
        }

        buttonDaftar.setOnClickListener{
            daftar(email, nama, pw)
        }
        buttonMasuklagi.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }
    }

    private fun daftar(email: String, nama: String, pw: String) {
        val call: Call<DefaultResponse> = apiInterface.createUser("", email, nama, pw, "2")
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, t.message, Toast.LENGTH_LONG).show()
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