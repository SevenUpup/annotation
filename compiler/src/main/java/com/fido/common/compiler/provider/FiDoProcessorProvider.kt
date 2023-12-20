package com.fido.common.compiler.provider

import com.fido.common.compiler.processor.FiDoProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
@author FiDo
@description:
@date :2023/12/20 14:31
 */
class FiDoProcessorProvider:SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return FiDoProcessor(environment.codeGenerator,environment.logger)
    }
}