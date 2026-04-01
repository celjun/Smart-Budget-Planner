package com.example.budgetplanner

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetplanner.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar

/**
 * SCREEN: Register
 *
 * Validates email, password, and password confirmation fields,
 * then navigates to LoginActivity (or directly to HomeActivity on success).
 *
 * In production, replace the stub with your actual registration logic
 * (Firebase Auth, REST API, etc.).
 *
 * Navigation:
 *  - Register button → LoginActivity (after successful registration)
 *  - Login link      → LoginActivity (back)
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { attemptRegister() }

        binding.tvGoToLogin.setOnClickListener {
            finish()   // pop back to LoginActivity
        }
    }

    // ── Register logic ────────────────────────────────────────────────────────

    private fun attemptRegister() {
        val email    = binding.etEmail.text?.toString()?.trim() ?: ""
        val password = binding.etPassword.text?.toString() ?: ""
        val confirm  = binding.etConfirmPassword.text?.toString() ?: ""

        // Reset errors
        binding.tilEmail.error           = null
        binding.tilPassword.error        = null
        binding.tilConfirmPassword.error = null

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

        // Confirm password validation
        if (confirm.isEmpty()) {
            binding.tilConfirmPassword.error = "Please confirm your password"
            valid = false
        } else if (password != confirm) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            valid = false
        }

        if (!valid) return

        // ── TODO: Replace with your real registration call ────────────────────
        // Example with Firebase:
        //   FirebaseAuth.getInstance()
        //       .createUserWithEmailAndPassword(email, password)
        //       .addOnCompleteListener { task ->
        //           if (task.isSuccessful) navigateToLogin()
        //           else showError(task.exception?.message)
        //       }
        // ─────────────────────────────────────────────────────────────────────

        // Stub: show success and go back to login
        Snackbar.make(binding.root, "Account created! Please log in.", Snackbar.LENGTH_SHORT).show()
        binding.root.postDelayed({ navigateToLogin() }, 1200)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun showError(message: String?) {
        Snackbar.make(
            binding.root,
            message ?: "Registration failed. Please try again.",
            Snackbar.LENGTH_LONG
        ).show()
    }
}
