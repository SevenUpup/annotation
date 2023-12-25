@file:OptIn(KspExperimental::class)

package com.fido.common.compiler.utils

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Origin
import com.squareup.kotlinpoet.ClassName

/**
@author FiDo
@description:
@date :2023/12/22 17:07
 */
internal fun KSClassDeclaration.isKotlinClass(): Boolean {
    return origin == Origin.KOTLIN ||
            origin == Origin.KOTLIN_LIB ||
            isAnnotationPresent(Metadata::class)
}

internal inline fun <reified T : Annotation> KSAnnotated.findAnnotationWithType(): T? {
    return getAnnotationsByType(T::class).firstOrNull()
}

internal inline fun KSPLogger.check(condition: Boolean, message: () -> String) {
    check(condition, null, message)
}

internal inline fun KSPLogger.check(condition: Boolean, element: KSNode?, message: () -> String) {
    if (!condition) {
        error(message(), element)
    }
}

// https://www.raywenderlich.com/33148161-write-a-symbol-processor-with-kotlin-symbol-processing
internal fun KSClassDeclaration.isSubclassOf(
    superClassName: String,
): Boolean {
    val superClasses = superTypes.toMutableList()
    while (superClasses.isNotEmpty()) {
        val current = superClasses.first() // KSTypeReference
        // KSTypeReference->KSType->KSDeclaration
        val declaration = current.resolve().declaration
        when {
            declaration is KSClassDeclaration && declaration.qualifiedName?.asString() == superClassName -> {
                return true
            }
            declaration is KSClassDeclaration -> {
                superClasses.removeAt(0)
                superClasses.addAll(0, declaration.superTypes.toList())
            }
            else -> {
                superClasses.removeAt(0)
            }
        }
    }
    return false
}

internal fun KSPropertyDeclaration.isSubclassOf(superClassName: String): Boolean {
    val propertyType = type.resolve().declaration
    return if (propertyType is KSClassDeclaration) {
        propertyType.isSubclassOf(superClassName)
    } else {
        false
    }
}

/* Split simple full class name to package and simple class name */
private fun packageSplit(str: String): Pair<String, String> {
    var index = 0
    for (i in str.indices.reversed()) {
        if (str[i] == '.') {
            index = i
            break
        }
    }
    return Pair(str.substring(0, index), str.substring(index + 1, str.length))
}

internal fun String.quantifyNameToClassName(): ClassName {
    val pair = packageSplit(this)
    return ClassName(pair.first, pair.second)
}