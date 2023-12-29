package com.fido.common.compiler.provider

import com.fido.common.annotation.test.InterfaceFactory
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo

/**
@author FiDo
@description:
@date :2023/12/28 17:36
 */
class TestFactoryProcessorProvider:SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return TestFactoryProcessor(environment.codeGenerator,environment.logger)
    }
}

class TestFactoryProcessor(val codeGenerator: CodeGenerator,val logger: KSPLogger) :SymbolProcessor{

    data class FactoryData(
        var packageName: String = "",
        var className: String = "",
        var childClass: MutableList<ClassName> = mutableListOf()
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val symbolsWithAnnotation =
            resolver.getSymbolsWithAnnotation(InterfaceFactory::class.qualifiedName!!)
        val ret = symbolsWithAnnotation.filter { !it.validate() }.toList()

        symbolsWithAnnotation
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }
            .forEach {ksClassDeclaration ->
                ksClassDeclaration.containingFile?.let {
                    val dataClass = FactoryData()
                    dataClass.packageName = ksClassDeclaration.packageName.asString()
                    dataClass.className = ksClassDeclaration.simpleName.asString()
                    // 访问所有文件，在每个文件中查找父类是Animal的Class，并收集
                    resolver.getAllFiles().forEach {
                        it.accept(FileVisitor(dataClass),Unit)
                    }
                    generateFactoryCode(dataClass,it)
                }
            }

        return ret
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun generateFactoryCode(data: FactoryData, containingFile: KSFile) {
        val animalType = "${data.className}Type"
        val animalTypeEnum = TypeSpec.enumBuilder(animalType).apply {
            data.childClass.forEach {
                addEnumConstant(it.simpleName.uppercase())
            }
        }.build()

        val animalTypeName = ClassName(data.packageName, animalType)
        val fromFun =  FunSpec.builder("from")
            .addParameter(ParameterSpec.builder("type", animalTypeName).build())
            .returns(ClassName(data.packageName, data.className))
            .beginControlFlow("return when (type)")
            .apply {
                data.childClass.forEach {
                    addStatement("${animalType}.${it.simpleName.uppercase()} -> %T()", it)
                }
            }
            .endControlFlow()
            .build()
        val animalFactory = "${data.className}Factory"
        val fileSpec = FileSpec.builder(data.packageName, animalFactory)
            .addType(animalTypeEnum)
            .addType(
                TypeSpec.interfaceBuilder(ClassName(data.packageName, animalFactory))
                    .addType(
                        TypeSpec.companionObjectBuilder()
                            .addFunction(fromFun)
                            .build()
                    )
                    .build()
            )
            .build()
        fileSpec.writeTo(codeGenerator, Dependencies(true, containingFile))
    }

    inner class FileVisitor(val factoryData: FactoryData): KSVisitorVoid() {
        private val visited = HashSet<Any>()
        private fun checkVisited(symbol: Any): Boolean {
            if (visited.contains(symbol)) return true
            visited.add(symbol)
            return false
        }

        override fun visitFile(file: KSFile, data: Unit) {
            if (checkVisited(file)) return
            file.declarations
                .filterIsInstance<KSClassDeclaration>()
                .filter { it.validate() }
                .forEach {
                    it.accept(this,data)
                }
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if (checkVisited(classDeclaration)) return
            val ksFile = classDeclaration.containingFile?:return
            classDeclaration.getAllSuperTypes().forEach {
                if (it.declaration.simpleName.asString() == factoryData.className) {
                    val className = ClassName(ksFile.packageName.asString(),
                        classDeclaration.simpleName.asString())
                    factoryData.childClass.add(className)
                }
            }
        }

    }

}