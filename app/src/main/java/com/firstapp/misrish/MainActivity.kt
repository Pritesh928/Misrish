package com.firstapp.misrish

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.cardview.widget.CardView
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // Views
    private lateinit var tilPersonName: TextInputLayout
    private lateinit var etPersonName: TextInputEditText
    private lateinit var btnScanId: MaterialButton
    private lateinit var btnCallPolice: MaterialButton
    private lateinit var btnPhotoClick: MaterialButton
    private lateinit var cardPersonInfo: CardView
    private lateinit var cardAppInfo: CardView
    private lateinit var cardOtpWarning: CardView
    private lateinit var tvPersonName: TextView
    private lateinit var tvScanId: TextView
    private lateinit var tvStatus: TextView
    private lateinit var ivPersonPhoto: ImageView
    private lateinit var btnElicitsApp: MaterialButton
    private lateinit var btnYesOtp: MaterialButton
    private lateinit var btnNoOtp: MaterialButton
    private lateinit var btnEmergencyPolice: MaterialButton
    private lateinit var btnCyberHelpline: MaterialButton

    companion object {
        const val REQUEST_CAMERA = 1001
        const val REQUEST_CAMERA_PERMISSION = 2001
        const val REQUEST_CALL_PERMISSION = 2002
        const val POLICE_NUMBER = "100"
        const val CYBER_HELPLINE_NUMBER = "1930"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        tilPersonName = findViewById(R.id.tilPersonName)
        etPersonName = findViewById(R.id.etPersonName)
        btnScanId = findViewById(R.id.btnScanId)
        btnCallPolice = findViewById(R.id.btnCallPolice)
        btnPhotoClick = findViewById(R.id.btnPhotoClick)
        cardPersonInfo = findViewById(R.id.cardPersonInfo)
        cardAppInfo = findViewById(R.id.cardAppInfo)
        cardOtpWarning = findViewById(R.id.cardOtpWarning)
        tvPersonName = findViewById(R.id.tvPersonName)
        tvScanId = findViewById(R.id.tvScanId)
        tvStatus = findViewById(R.id.tvStatus)
        ivPersonPhoto = findViewById(R.id.ivPersonPhoto)
        btnElicitsApp = findViewById(R.id.btnElicitsApp)
        btnYesOtp = findViewById(R.id.btnYesOtp)
        btnNoOtp = findViewById(R.id.btnNoOtp)
        btnEmergencyPolice = findViewById(R.id.btnEmergencyPolice)
        btnCyberHelpline = findViewById(R.id.btnCyberHelpline)
    }

    private fun setupClickListeners() {

        // Scan ID - opens camera for QR/document scan
        btnScanId.setOnClickListener {
            openCamera()
        }

        // Call Police from search section
        btnCallPolice.setOnClickListener {
            makePhoneCall(POLICE_NUMBER)
        }

        // Photo Click - take photo of suspicious person
        btnPhotoClick.setOnClickListener {
            val name = etPersonName.text.toString().trim()
            if (name.isEmpty()) {
                tilPersonName.error = "Please enter a person name first"
                return@setOnClickListener
            }
            tilPersonName.error = null
            openCamera()
            showPersonInfo(name)
        }

        // Elicits / warns about dangerous app
        btnElicitsApp.setOnClickListener {
            Toast.makeText(
                this,
                "⚠ Do NOT install or open apps sent by unknown people!",
                Toast.LENGTH_LONG
            ).show()
        }

        // OTP - Yes, gave OTP (DANGER)
        btnYesOtp.setOnClickListener {
            showOtpDangerDialog()
        }

        // OTP - No, did not give OTP (safe)
        btnNoOtp.setOnClickListener {
            Toast.makeText(
                this,
                "✅ Good! Never share OTP with anyone, not even bank staff.",
                Toast.LENGTH_LONG
            ).show()
        }

        // Emergency Police
        btnEmergencyPolice.setOnClickListener {
            makePhoneCall(POLICE_NUMBER)
        }

        // Cyber Helpline
        btnCyberHelpline.setOnClickListener {
            makePhoneCall(CYBER_HELPLINE_NUMBER)
        }
    }

    /**
     * Show person info card and related warning sections
     */
    private fun showPersonInfo(name: String) {
        tvPersonName.text = name
        tvScanId.text = "SC-${System.currentTimeMillis().toString().takeLast(6)}"

        // Set status color based on a simple check (in real app, this would be a DB lookup)
        tvStatus.text = "⚠ Verify Identity"
        tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))

        cardPersonInfo.visibility = View.VISIBLE
        cardAppInfo.visibility = View.VISIBLE
        cardOtpWarning.visibility = View.VISIBLE

        // Scroll hint
        Toast.makeText(this, "Scroll down to see safety warnings", Toast.LENGTH_SHORT).show()
    }

    /**
     * Show OTP danger warning dialog
     */
    private fun showOtpDangerDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_otp_warning)
        dialog.window?.setLayout(
            android.view.WindowManager.LayoutParams.MATCH_PARENT,
            android.view.WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        val btnCallNow = dialog.findViewById<MaterialButton>(R.id.btnCallHelplineNow)
        val btnDismiss = dialog.findViewById<MaterialButton>(R.id.btnDismissDialog)

        btnCallNow.setOnClickListener {
            dialog.dismiss()
            makePhoneCall(CYBER_HELPLINE_NUMBER)
        }

        btnDismiss.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Opens device camera
     */
    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(cameraIntent, REQUEST_CAMERA)
            } else {
                Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Makes a phone call to the given number
     */
    private fun makePhoneCall(number: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL_PERMISSION
            )
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$number")
            startActivity(callIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            val photo = data?.extras?.get("data") as? android.graphics.Bitmap
            if (photo != null) {
                ivPersonPhoto.setImageBitmap(photo)
                // Show person info card if not already visible
                if (cardPersonInfo.visibility != View.VISIBLE) {
                    val name = etPersonName.text.toString().trim().ifEmpty { "Unknown Person" }
                    showPersonInfo(name)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CALL_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted. Please tap the call button again.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Call permission denied. Use your dialer manually.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}