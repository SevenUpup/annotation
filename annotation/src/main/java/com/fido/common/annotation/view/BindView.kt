package com.fido.common.annotation.view

import androidx.annotation.Keep

/**
@author FiDo
@description:
@date :2023/12/20 11:14
 */
@Keep
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class BindView(val value:Int)
