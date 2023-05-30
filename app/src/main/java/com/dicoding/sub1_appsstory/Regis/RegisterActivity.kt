package com.dicoding.sub1_appsstory.Regis

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.sub1_appsstory.Data.LoginUser
import com.dicoding.sub1_appsstory.Login.LoginActivity
import com.dicoding.sub1_appsstory.ViewModel.MainViewModel
import com.dicoding.sub1_appsstory.ViewModel.ViewModelFactory
import com.dicoding.sub1_appsstory.databinding.ActivityRegisterBinding
import com.dicoding.sub1_appsstory.RemoteData.Result

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get value from edit text
        val nameVal = binding.edRegisterName.text
        val emailVal = binding.edRegisterEmail.text
        val passwordVal = binding.edRegisterPassword.text

        binding.btnRegister.setOnClickListener {
            val edRegisterPasswordError = binding.edRegisterPassword.text
            // Digunakan
            // Jika Password yang dimasukan kurang dari 8 maka Button Register akan error
            if (edRegisterPasswordError?.length!! < 8) {
                binding.btnRegister.error =
                    "Password yang anda inputkan harus lebih dari 8 Karakter"
            } else {
                loadingProcess()
                mainViewModel.registerNewUser(
                    nameVal.toString(),
                    passwordVal.toString(),
                    emailVal.toString()
                ).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val response = result.data
                            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                            sendDataToLoginActivity(
                                LoginUser(
                                    emailVal.toString(),
                                    passwordVal.toString()
                                )
                            )
                        }
                        is Result.Error -> {
                            val errorMessage = result.error
                            wrongDataGiven()
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

//        binding.edRegisterPassword.addTextChangedListener(object: TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                binding.btnRegister.isEnabled = true
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
//            }
//
//        })

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Do something before going back to the previous activity
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    private fun loadingProcess() {
        binding.progressBar.visibility = View.VISIBLE
        binding.edRegisterName.isCursorVisible = false
        binding.edRegisterEmail.isCursorVisible = false
        binding.edRegisterPassword.isCursorVisible = false
    }

    private fun wrongDataGiven() {
        binding.progressBar.visibility = View.GONE
        binding.edRegisterName.isCursorVisible = true
        binding.edRegisterEmail.isCursorVisible = true
        binding.edRegisterPassword.isCursorVisible = true
    }

    private fun sendDataToLoginActivity(data: LoginUser) {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.putExtra("extra_email_username", data)
        startActivity(intent)
        finish()
    }
}