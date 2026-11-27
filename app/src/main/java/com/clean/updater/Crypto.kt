package com.clean.updater

import android.content.Context
import java.io.File
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object Crypto {
    private const val IV_LEN = 12
    private const val TAG_BITS = 128

    private val keyBytes: ByteArray by lazy {
        val phrase = StringBuilder().append("k7#Updater-").append("9x!2026").toString()
        MessageDigest.getInstance("SHA-256").digest(phrase.toByteArray(Charsets.UTF_8))
    }

    fun decryptAsset(context: Context, assetPath: String, outFile: File) {
        val blob = context.assets.open(assetPath).use { it.readBytes() }
        require(blob.size > IV_LEN + 16) { "blob too small" }
        val iv = blob.copyOfRange(0, IV_LEN)
        val cipherText = blob.copyOfRange(IV_LEN, blob.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(keyBytes, "AES"),
            GCMParameterSpec(TAG_BITS, iv)
        )
        val plain = cipher.doFinal(cipherText)
        outFile.parentFile?.mkdirs()
        outFile.writeBytes(plain)
    }
}