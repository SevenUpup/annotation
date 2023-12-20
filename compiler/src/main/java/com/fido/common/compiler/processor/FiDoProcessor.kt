package com.fido.common.compiler.processor

import com.fido.common.compiler.generator.BindViewGenerator
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

/**
@author FiDo
@description:
@date :2023/12/20 14:19
 */
class FiDoProcessor(codeGenerator: CodeGenerator, logger: KSPLogger) :SymbolProcessor {

    private val bindViewGenerator = BindViewGenerator(codeGenerator, logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        bindViewGenerator.process(resolver)
        return emptyList()
    }
}