package com.fido.common.fido_annotation

import android.app.Application
import android.content.Context

/**
@author FiDo
@description:
@date :2023/12/27 18:03
 */
class MyApp: Application() {

    val app:Context by lazy { this }

    override fun onCreate() {
        super.onCreate()
    }

}