package com.dicoding.sub1_appsstory.Login

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.sub1_appsstory.Data.LoginUser
import com.dicoding.sub1_appsstory.MainActivity
import com.dicoding.sub1_appsstory.Regis.RegisterActivity
import com.dicoding.sub1_appsstory.RemoteData.Result
import com.dicoding.sub1_appsstory.ViewModel.MainViewModel
import com.dicoding.sub1_appsstory.ViewModel.ViewModelFactory
import com.dicoding.sub1_appsstory.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val tv_register = binding.tvRegister
        val btnLogin = binding.btnLogin
        val edEmail = binding.edLoginEmail
        val edPassword = binding.edLoginPassword

        // Cek Login
        isLoginBefore(this)

        //Masuk ke halaman register
        tv_register.setOnClickListener {
            val register = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(register)
            finish()
        }


        if (Build.VERSION.SDK_INT >= 33) {
            val data = intent.getParcelableExtra("extra_email_username", LoginUser::class.java)
            if (data != null) {
                userLogin(data.email.toString(), data.password.toString())
            }
        } else {
            @Suppress("DEPRECATION")
            val data = intent.getParcelableExtra<LoginUser>("extra_email_username")
            if (data != null) {
                userLogin(data.email.toString(), data.password.toString())
            }
        }

        btnLogin.setOnClickListener {
            if (edPassword.text?.isEmpty() == true) {
                edPassword.error = "Mohon Untuk Di isi, Tidak Boleh Kosong"
            }

            if (edEmail.text?.isEmpty() == true) {
                edEmail.error = "Mohon isi dengan Format @gmail dan Tidak boleh kosong"
            }
            if (edPassword.error == null && edEmail.error == null) {
                userLogin(edEmail.text.toString(), edPassword.text.toString())
            }
        }
    }

    private fun isLoginBefore(context: Context) {
        mainViewModel.getPreference(context).observe(this) { token ->
            if (token?.isEmpty() == false) {
                val mainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(mainActivity)
                finish()
            }
        }
    }

    private fun userLogin(email: String, password: String) {
        mainViewModel.loginNewUser(email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        loadingProcess()
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val data = result.data
                        Toast.makeText(this@LoginActivity, data.message, Toast.LENGTH_SHORT).show()
                        if (data.loginResult?.token != null) {
                            mainViewModel.setPreference(data.loginResult.token, this)
                        }
                        val mainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(mainActivity)
                        finish()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadingProcess() {
        binding.progressBar.visibility = View.VISIBLE
        binding.edLoginEmail.isCursorVisible = false
        binding.edLoginPassword.isCursorVisible = false
    }
}