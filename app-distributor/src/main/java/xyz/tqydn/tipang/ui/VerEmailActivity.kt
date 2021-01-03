package xyz.tqydn.tipang.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.databinding.ActivityVerEmailBinding

class VerEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_email)
        binding.buttonMasuk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}