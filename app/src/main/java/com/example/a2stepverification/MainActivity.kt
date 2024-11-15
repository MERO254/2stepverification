package com.example.a2stepverification

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var edtContact: EditText
    private lateinit var edtOtp: EditText
    private lateinit var btnSendContact: Button
    private lateinit var btnSignIn: Button

    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Setting system bars insets for full-screen support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        edtContact = findViewById(R.id.edtcontact)
        edtOtp = findViewById(R.id.edtotp)
        btnSendContact = findViewById(R.id.btnsendcontact)
        btnSignIn = findViewById(R.id.btnsignin)

        edtOtp.visibility = View.GONE
        btnSignIn.visibility = View.GONE

        btnSendContact.setOnClickListener {
            val phoneNumber = edtContact.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendOtp(phoneNumber)
                edtOtp.visibility = View.VISIBLE
                btnSignIn.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            }
        }

        btnSignIn.setOnClickListener {
            val otpCode = edtOtp.text.toString().trim()
            if (otpCode.isNotEmpty() && verificationId != null) {
                val credential = PhoneAuthProvider.getCredential(verificationId!!, otpCode)
                signInWithAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Automatically verify and sign in the user
                    signInWithAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@MainActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Assign the verification ID
                    this@MainActivity.verificationId = verificationId
                    Toast.makeText(this@MainActivity, "OTP code sent", Toast.LENGTH_LONG).show()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Verification successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Verification failed", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
