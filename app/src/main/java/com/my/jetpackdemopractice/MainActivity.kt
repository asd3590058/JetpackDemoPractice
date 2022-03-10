package com.my.jetpackdemopractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.my.jetpackdemopractice.biometric.BiometricActivity
import com.my.jetpackdemopractice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.biometricBtn.setOnClickListener{startActivity(Intent(this,BiometricActivity::class.java))}
    }
}