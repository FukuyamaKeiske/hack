package com.tehnokotiki.stroymatch

import android.content.Intent
import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tehnokotiki.stroymatch.network.ApiService
import com.tehnokotiki.stroymatch.network.EmployerInformation
import com.tehnokotiki.stroymatch.network.WorkerInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InfoActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val tilName: TextInputLayout = findViewById(R.id.til_name)
        val etName: TextInputEditText = findViewById(R.id.et_name)
        val tilBirthDate: TextInputLayout = findViewById(R.id.til_birth_date)
        val etBirthDate: TextInputEditText = findViewById(R.id.et_birth_date)
        val tilCity: TextInputLayout = findViewById(R.id.til_city)
        val etCity: TextInputEditText = findViewById(R.id.et_city)
        val tilCompanyName: TextInputLayout = findViewById(R.id.til_company_name)
        val etCompanyName: TextInputEditText = findViewById(R.id.et_company_name)
        val tilIndustry: TextInputLayout = findViewById(R.id.til_industry)
        val spinnerIndustry: Spinner = findViewById(R.id.spinner_industry)
        val tilCompanySize: TextInputLayout = findViewById(R.id.til_company_size)
        val etCompanySize: TextInputEditText = findViewById(R.id.et_company_size)
        val btnNext: MaterialButton = findViewById(R.id.btn_next)

        apiService = Retrofit.Builder()
            .baseUrl("http://10.2.0.125:8000") // Убедитесь, что URL правильный
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val sharedPreferences = getSharedPreferences("job_matching_prefs", MODE_PRIVATE)
        val role = sharedPreferences.getString("selected_role", null)

        if (role == "worker") {
            tilName.visibility = TextInputLayout.VISIBLE
            tilBirthDate.visibility = TextInputLayout.VISIBLE
            tilCity.visibility = TextInputLayout.VISIBLE
        } else if (role == "employer") {
            tilCompanyName.visibility = TextInputLayout.VISIBLE
            tilIndustry.visibility = TextInputLayout.VISIBLE
            tilCompanySize.visibility = TextInputLayout.VISIBLE
        }

        btnNext.setOnClickListener {
            if (role == "worker") {
                val name = etName.text.toString()
                val birthDate = etBirthDate.text.toString()
                val city = etCity.text.toString()

                if (name.isNotEmpty() && birthDate.isNotEmpty() && city.isNotEmpty()) {
                    saveWorkerInfo(name, birthDate, city)
                } else {
                    Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                }
            } else if (role == "employer") {
                val companyName = etCompanyName.text.toString()
                val industry = spinnerIndustry.selectedItem.toString()
                val companySize = etCompanySize.text.toString()

                if (companyName.isNotEmpty() && industry.isNotEmpty() && companySize.isNotEmpty()) {
                    saveEmployerInfo(companyName, industry, companySize)
                } else {
                    Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveWorkerInfo(name: String, birthDate: String, city: String) {
        val sharedPreferences = getSharedPreferences("job_matching_prefs", MODE_PRIVATE)
        val phone = sharedPreferences.getString("phone", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateWorkerInfo(phone, WorkerInformation(name, birthDate, city))
                if (response.isSuccessful) {
                    val user = response.body()
                    with(sharedPreferences.edit()) {
                        putString("worker_name", name)
                        putString("worker_birth_date", birthDate)
                        putString("worker_city", city)
                        putString("user_data", user.toString()) // Сохраняем данные пользователя
                        apply()
                    }
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@InfoActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@InfoActivity, "Ошибка сохранения информации", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InfoActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveEmployerInfo(companyName: String, industry: String, companySize: String) {
        val sharedPreferences = getSharedPreferences("job_matching_prefs", MODE_PRIVATE)
        val phone = sharedPreferences.getString("phone", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateEmployerInfo(phone, EmployerInformation(companyName, industry, companySize))
                if (response.isSuccessful) {
                    val user = response.body()
                    with(sharedPreferences.edit()) {
                        putString("employer_company_name", companyName)
                        putString("employer_industry", industry)
                        putString("employer_company_size", companySize)
                        putString("user_data", user.toString()) // Сохраняем данные пользователя
                        apply()
                    }
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@InfoActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@InfoActivity, "Ошибка сохранения информации", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InfoActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
