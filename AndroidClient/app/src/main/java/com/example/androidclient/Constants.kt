package com.example.androidclient

import android.Manifest

val permissions = listOf(
    Manifest.permission.READ_CONTACTS,
    Manifest.permission.WRITE_CONTACTS
)

const val SHOW_MODE_LOCAL = "local"
const val SHOW_MODE_CLOUD = "cloud"