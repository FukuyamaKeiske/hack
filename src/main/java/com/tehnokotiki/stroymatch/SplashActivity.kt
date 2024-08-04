package com.tehnokotiki.stroymatch

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences = getSharedPreferences("job_matching_prefs", MODE_PRIVATE)
            val userRole = sharedPreferences.getString("user_role", null)

            if (userRole == null) {
                startActivity(Intent(this, RoleSelectionActivity::class.java))
            } else {
                // Redirect to the main activity or another appropriate activity
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 2000) // 2 seconds delay
    }
}
