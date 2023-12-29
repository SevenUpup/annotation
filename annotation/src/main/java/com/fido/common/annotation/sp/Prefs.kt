package com.fido.common.annotation.sp

import com.fido.common.annotation.emun.PrefsMode

/**
@author FiDo
@description: SharedPreferences文件注解,可生成对应的文件
@date :2023/12/27 10:18
 */
annotation class Prefs(
    val value:String = "",
    val mode : PrefsMode = PrefsMode.MODE_PRIVATE
)
