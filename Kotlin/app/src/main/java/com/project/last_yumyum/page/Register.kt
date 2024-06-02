package com.project.last_yumyum.page

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.last_yumyum.Dataclass.Register
import com.project.last_yumyum.Dataclass.SignUpResponse
import com.project.last_yumyum.R
import com.project.last_yumyum.retrofitClient.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {
    private lateinit var btnRegister: Button
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPhone: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 초기화
        val backButton: Button = findViewById(R.id.backbutton)
        btnRegister = findViewById(R.id.btn_register)
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etPhone = findViewById(R.id.et_phone)


        backButton.setOnClickListener {
            // 이전 화면으로 이동하는 코드 작성
            onBackPressed()
        }
        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val phoneNumber = etPhone.text.toString()

            val registerData = Register(name, email, password, phoneNumber)
            RetrofitClient.api.register(registerData).enqueue(object :
                Callback<SignUpResponse> {
                override fun onResponse(
                    call: Call<SignUpResponse>,
                    response: Response<SignUpResponse>
                ) {
                    if (response.isSuccessful) {
                        val signUpResponse = response.body()
                        val message = signUpResponse?.message

                        if (message == "Signup successful") {
                            // 회원가입 성공 메시지 표시
                            Toast.makeText(this@Register, "회원가입 성공!", Toast.LENGTH_SHORT).show()

                            // 회원가입 성공 시 다음 액티비티로 이동
                            val intent = Intent(this@Register, MainActivity::class.java)
                            startActivity(intent)
                            finish() // 현재 액티비티 종료
                        } else {
                            // 회원가입 실패 메시지 표시
                            Toast.makeText(
                                this@Register,
                                "회원가입 실패: $message",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        // 응답이 성공적이지 않을 때의 처리
                        Toast.makeText(this@Register, "회원가입 실패: 서버 오류", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    Toast.makeText(this@Register, "회원가입 오류: ${t.message}", Toast.LENGTH_LONG)
                        .show()
                }
            })
        }
    }
}
