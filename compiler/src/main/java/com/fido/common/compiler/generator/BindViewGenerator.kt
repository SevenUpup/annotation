package com.fido.common.compiler.generator

import com.fido.common.annotation.view.BindView
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

/**
@author FiDo
@description:
@date :2023/12/20 14:38
 */
class BindViewGenerator(
    codeGenerator: CodeGenerator,
    logger: KSPLogger
) : BaseGenerator(codeGenerator, logger) {
    override fun process(resolver: Resolver) {
        val symbols = resolver.getSymbolsWithAnnotation(BindView::class.qualifiedName!!)
        val bindList = symbols.filter {
            it is KSPropertyDeclaration && it.validate()
        }.map {
            it as KSPropertyDeclaration
        }.toList()

        val map = bindList.groupBy {
            val parent = it.parent as KSClassDeclaration
            val key = "${parent.toClassName().simpleName},${parent.packageName.asString()}"
            key
        }

        map.forEach {
            val classItem = it.value[0].parent as KSClassDeclaration
            val fileSpecBuilder = FileSpec.builder(
                classItem.packageName.asString(),
                "${classItem.toClassName().simpleName}ViewBind"
            )

            val functionBuilder = FunSpec.builder("bindView")
                .receiver(classItem.toClassName())

            it.value.forEach { item ->
                val symbolName = item.simpleName.asString()
                val annotationValue =
                    (item.annotations.firstOrNull()?.arguments?.firstOrNull()?.value as? Int) ?: 0
                functionBuilder.addStatement("$symbolName = findViewById(${annotationValue})")
            }

            fileSpecBuilder.addFunction(functionBuilder.build())
                .build()
                .writeTo(codeGenerator, false)
        }
    }


}