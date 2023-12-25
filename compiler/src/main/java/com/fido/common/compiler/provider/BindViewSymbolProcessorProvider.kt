package com.fido.common.compiler.provider

import com.fido.common.annotation.view.BindView
import com.fido.common.compiler.provider.BindViewSymbolProcessorProvider.Companion.BINDVIEW_PROCESS
import com.fido.common.compiler.utils.check
import com.fido.common.compiler.utils.findAnnotationWithType
import com.fido.common.compiler.utils.isSubclassOf
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.originatingKSFiles
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

/**
@author FiDo
@description:
@date :2023/12/22 17:05
 */
class BindViewSymbolProcessorProvider : SymbolProcessorProvider {
    companion object {
        @Suppress("SpellCheckingInspection")
        val BINDVIEW_PROCESS: String = BindView::class.qualifiedName!!
    }

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return BindViewSymbolProcessor(environment.logger, environment.codeGenerator)
    }
}

// KSP https://kotlinlang.org/docs/images/ksp-class-diagram.svg
class BindViewSymbolProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {

    /* Now I can not find a good log print method, logging.warn will print log,
     @see this https://github.com/google/ksp/issues/1111 */

    // About ksp debug https://github.com/google/ksp/issues/31

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(BINDVIEW_PROCESS)
        // return can not be resolve elements in this turn
        val ret = symbols.filter { !it.validate() }.toList()

        // Filter first
        @Suppress("UNCHECKED_CAST")
        val bindViewElement: List<KSPropertyDeclaration> =
            symbols.filter { it.validate() && it is KSPropertyDeclaration && it.parent is KSClassDeclaration }
                .toList() as List<KSPropertyDeclaration>
        if (bindViewElement.isNotEmpty()) {
            CodeGen.gen(bindViewElement, codeGenerator, logger)
        }
        return ret
    }

    object CodeGen {
        @OptIn(KotlinPoetKspPreview::class)
        fun gen(
            elements: List<KSPropertyDeclaration>,
            codeGenerator: CodeGenerator,
            logger: KSPLogger
        ) {
            // Group by class those @BindView belongs, Notice nest class condition
            val map = elements.groupBy {
                val parent = it.parent as KSClassDeclaration
                /*val key = "${parent.packageName.asString()}.${parent.toClassName().simpleName}"*/
                val key = parent.qualifiedName?.asString() ?: ""
                key
            }
            logger.warn(map.toString())
            for (item in map) {
                if (item.key.isEmpty() || item.value.isEmpty()) {
                    continue
                }
                val parent = item.value[0].parent as KSClassDeclaration
                val parentClassName = parent.toClassName()

                logger.check(parent.isSubclassOf("android.app.Activity")) {
                    "@BindView can be only apply in [Activity]`s field, but you class is ${parentClassName.canonicalName}"
                }
                val packageName = parentClassName.packageName
                val fileSimpleName = parentClassName.simpleNames.joinToString("$") + "\$ViewBinding"

                val fileSpecBuilder = FileSpec.builder(
                    packageName,
                    fileSimpleName
                )
                // likes: Class MainActivity$ViewBinding
                val typeSpec = TypeSpec.classBuilder(fileSimpleName).addModifiers(KModifier.PUBLIC)
                val activityParamName = "activity"
                // constructor init(activity:MainActivity)
                val functionBuilder = FunSpec.constructorBuilder()
                    .addParameter(
                        activityParamName,
                        ClassName(packageName, parentClassName.simpleNames)
                    )

                // @BindView val textView TextView
                for (bindView in item.value) {
                    // textView
                    val symbolName = bindView.simpleName.asString()
                    val annotationValue = bindView.findAnnotationWithType<BindView>()?.value ?: 0

                    if (annotationValue == 0) {
                        logger.error("@BindView's value need assign a not-zer0 value")
                    }
                    // activity.textView = activity.findViewById(xxx)
                    functionBuilder.addStatement("${activityParamName}.$symbolName = ${activityParamName}.findViewById(${annotationValue})")
                }

                typeSpec.addFunction(functionBuilder.build())
                val file = fileSpecBuilder.addType(typeSpec.build())
                    .build()

                val dependency: List<KSFile> = if (parent.containingFile != null) {
                    listOf(parent.containingFile!!)
                } else {
                    file.originatingKSFiles()
                }

                file.writeTo(codeGenerator, false, dependency)
            }
        }
    }
}