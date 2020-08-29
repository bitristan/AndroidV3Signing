package com.android.example.v3.signing

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signatureTv = findViewById<TextView>(R.id.signature)
        signatureTv.text = querySignatureInfo()
    }

    private fun querySignatureInfo(): String {
        val builder = StringBuilder()
        val packageInfo = packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        )
        val signingInfo = packageInfo.signingInfo
        if (signingInfo.hasMultipleSigners()) {
            signingInfo.apkContentsSigners.forEach {
                appendSigInfo(it.toByteArray(), builder)
            }
        } else {
            signingInfo.signingCertificateHistory.forEach {
                appendSigInfo(it.toByteArray(), builder)
            }
        }
        return builder.toString()
    }

    private fun appendSigInfo(data: ByteArray, builder: StringBuilder) {
        builder.append("MD5: ")
        builder.append(data.md5())
        builder.append("\n\n")
        builder.append("SHA256: ")
        builder.append(data.sha256())
        builder.append("\n")
    }
}