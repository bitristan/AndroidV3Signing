package com.android.example.v3.signing

import java.math.BigInteger
import java.security.MessageDigest

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

fun ByteArray.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(this)).toString(16).padStart(32, '0')
}

fun ByteArray.sha1(): String {
    val md = MessageDigest.getInstance("SHA-1")
    return BigInteger(1, md.digest(this)).toString(16).padStart(32, '0')
}

fun ByteArray.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(this)).toString(16).padStart(32, '0')
}
