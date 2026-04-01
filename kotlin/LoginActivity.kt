package com.example.budgetplanner

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetplanner.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar

/**
 * SCREEN: Login
 *
 * Validates email + password fields, then navigates to HomeActivity.
 *
 * In production, replace the stub validation with your actual auth logic
 * (Firebase Auth, JWT API, etc.).
 *
 * Navigation:
 *  - Login button  → HomeActivity (on success)
 *  - Register link → RegisterActivity
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { attemptLogin() }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // ── Login logic ───────────────────────────────────────────────────────────

    private fun attemptLogin() {
        val email    = binding.etEmail.text?.toString()?.trim() ?: ""
        val password = binding.etPassword.text?.toString() ?: ""

        // Reset field errors
        binding.tilEmail.error    = null
        binding.tilPassword.error = null

        var valid = true

        // Email validation
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email"
            valid = false
        }

        // Password validation
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            valid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            valid = false
        }

        if (!valid) return

        // ── TODO: Replace with your real authentication call ──────────────────
        // Example with Firebase:
        //   FirebaseAuth.getInstance()
        //       .signInWithEmailAndPassword(email, password)
        //       .addOnCompleteListener { task ->
        //           if (task.isSuccessful) navigateToHome()
        //           else showError(task.exception?.message)
        //       }
        // ─────────────────────────────────────────────────────────────────────

        // Stub: accept any valid-format credentials
        navigateToHome()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showError(message: String?) {
        Snackbar.make(
            binding.root,
            message ?: "Login failed. Please try again.",
            Snackbar.LENGTH_LONG
        ).show()
    }
}
