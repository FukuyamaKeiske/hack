package com.tehnokotiki.stroymatch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RoleSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        val btnWorker: MaterialButton = findViewById(R.id.btn_worker)
        val btnEmployer: MaterialButton = findViewById(R.id.btn_employer)

        btnWorker.setOnClickListener {
            saveRoleAndProceed("worker")
        }

        btnEmployer.setOnClickListener {
            saveRoleAndProceed("employer")
        }
    }

    private fun saveRoleAndProceed(role: String) {
        val sharedPreferences = getSharedPreferences("job_matching_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("selected_role", role)
            apply()
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
