package com.project.last_yumyum.page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.last_yumyum.Dataclass.LogIn
import com.project.last_yumyum.Dataclass.LoginResponse
import com.project.last_yumyum.R
import com.project.last_yumyum.retrofitClient.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var btnLogin: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 초기화
        btnLogin = findViewById(R.id.btn_login)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnRegister = findViewById(R.id.btn_register)

/*
        // 임시테스트 이후 지울것 //
        btnLogin.setOnClickListener {
            val intent2 = Intent(this@MainActivity, HomePage::class.java)
            startActivity(intent2)
        }
        */



        // 로그인 버튼 클릭 리스너 설정
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            val loginData = LogIn(email, password)
            RetrofitClient.api.login(loginData).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val message = loginResponse?.message

                        if (message == "Login successful") {
                            // 로그인 성공 메시지 표시
                            Toast.makeText(
                                this@MainActivity,
                                "로그인 성공: $message",
                                Toast.LENGTH_SHORT
                            ).show()
                            // 로그인 성공 시 다음 액티비티로 이동
                            val intent1 = Intent(this@MainActivity, HomePage::class.java)
                            startActivity(intent1)
                        } else {
                            // 로그인 실패 메시지 표시
                            Toast.makeText(this@MainActivity, "로그인 실패: $message", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // 응답이 성공적이지 않을 때의 처리
                        Toast.makeText(this@MainActivity, "로그인 실패: 서버 오류", Toast.LENGTH_SHORT).show()
                    }

                }


                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // 실패한 이유를 로그로 출력
                    Log.e("LoginError", "로그인 요청 실패: ${t.message}", t)
                    // 실패한 이유를 사용자에게 토스트 메시지로 표시
                    Toast.makeText(this@MainActivity, "로그인 오류: ${t.message}", Toast.LENGTH_LONG)
                        .show()
                }

            })
        }

    }

    override fun onStart() {
        super.onStart()

        // 회원가입 버튼 클릭 리스너 설정
        btnRegister.setOnClickListener {
            val intent = Intent(this@MainActivity, Register::class.java)
            startActivity(intent)
        }
    }
}