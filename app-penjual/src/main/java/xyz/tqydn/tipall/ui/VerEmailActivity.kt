package xyz.tqydn.tipall.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.tqydn.tipall.databinding.ActivityVerEmailBinding

class VerEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonMasuk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}