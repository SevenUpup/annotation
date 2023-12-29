package com.fido.common.compiler.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver

/**
@author FiDo
@description:
@date :2023/12/27 10:25
 */
class PrefsGenerator(codeGenerator: CodeGenerator, logger: KSPLogger) : BaseGenerator(
    codeGenerator,
    logger
) {
    override fun process(resolver: Resolver) {

    }
}