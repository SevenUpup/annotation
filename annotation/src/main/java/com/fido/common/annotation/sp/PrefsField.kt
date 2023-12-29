package com.fido.common.annotation.sp

/**
@author FiDo
@description: Shared的参数注解 只能修饰在Double,Float,Int,Long,String,Boolean类型上的注解
@date :2023/12/27 10:22
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class PrefsField(
    /**
     * 字段名字,如果为空就取参数名称
     */
    val value:String = ""
)
