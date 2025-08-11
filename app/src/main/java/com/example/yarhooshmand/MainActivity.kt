package com.example.yarhooshmand

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yarhooshmand.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.simpleBtn.setOnClickListener {
            startActivity(Intent(this, SimpleReminderActivity::class.java))
        }
        b.smartBtn.setOnClickListener {
            startActivity(Intent(this, SmartActivity::class.java))
        }
    }
}
