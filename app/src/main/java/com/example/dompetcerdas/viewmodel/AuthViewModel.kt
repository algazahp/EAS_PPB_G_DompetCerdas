package com.example.dompetcerdas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Kelas ini akan menangani status autentikasi di seluruh aplikasi.
class AuthViewModel : ViewModel() {

    // Penyimpanan pengguna sederhana di dalam memori.
    private val userStore = mutableMapOf<String, String>()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    // Fungsi untuk proses login.
    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            if (userStore.containsKey(email) && userStore[email] == pass) {
                _isLoggedIn.value = true
                _authError.value = null
            } else {
                _authError.value = "Email atau password salah."
            }
        }
    }

    // Fungsi untuk proses pendaftaran dengan validasi.
    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            // Validasi email
            if (!email.contains("@") || !email.endsWith(".com")) {
                _authError.value = "Format email tidak valid. Gunakan format @.com"
                return@launch
            }

            // Validasi password
            val passwordValidationResult = isValidPassword(pass)
            if (passwordValidationResult != null) {
                _authError.value = passwordValidationResult
                return@launch
            }

            // Periksa apakah email sudah terdaftar
            if (userStore.containsKey(email)) {
                _authError.value = "Email sudah terdaftar."
            } else {
                userStore[email] = pass
                _isLoggedIn.value = true
                _authError.value = null
            }
        }
    }

    // Fungsi validasi password
    private fun isValidPassword(password: String): String? {
        if (password.length < 8) {
            return "Password minimal 8 karakter."
        }
        if (!password.any { it.isDigit() }) {
            return "Password harus mengandung setidaknya satu angka."
        }
        if (!password.any { it.isLetter() }) {
            return "Password harus mengandung setidaknya satu huruf."
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            return "Password harus mengandung setidaknya satu simbol."
        }
        return null // Return null jika password valid
    }


    // Fungsi untuk logout.
    fun signOut() {
        viewModelScope.launch {
            _isLoggedIn.value = false
            _authError.value = null
        }
    }

    fun clearError() {
        _authError.value = null
    }
}
