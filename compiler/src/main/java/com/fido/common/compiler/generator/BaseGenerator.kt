package com.fido.common.compiler.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver

/**
@author FiDo
@description:
@date :2023/12/20 14:39
 */
abstract class BaseGenerator(
    val codeGenerator: CodeGenerator,
    val logger:KSPLogger
) {

    abstract fun process(resolver: Resolver)

    fun warning(tag: String, msg: String) {
        logger.warn("$tag:$msg")
    }

    fun error(tag: String, msg: String) {
        logger.error("$tag:$msg")
    }
}