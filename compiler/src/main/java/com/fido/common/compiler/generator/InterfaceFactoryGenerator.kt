package com.fido.common.compiler.generator

import com.fido.common.annotation.test.InterfaceFactory
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
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
@description: 通过注解 InterfaceFactory 实现生成子类的工厂方法
@date :2023/12/29 9:51
 */
class InterfaceFactoryGenerator(
    codeGenerator: CodeGenerator,
    logger: KSPLogger
) : BaseGenerator(codeGenerator, logger) {

    data class FactoryData(
        var packageName: String = "",
        var className: String = "",
        var childClass: MutableList<ClassName> = mutableListOf()
    )

    override fun process(resolver: Resolver) {

        val symbols = resolver.getSymbolsWithAnnotation(InterfaceFactory::class.qualifiedName!!)

        symbols.filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }
            .forEach { ksClassDeclaration ->
                ksClassDeclaration.containingFile?.let {
                    val data = FactoryData()
                    data.packageName = ksClassDeclaration.packageName.asString()
                    data.className = ksClassDeclaration.simpleName.asString()
                    //访问所有文件，查找出父类是InterfaceFactory的子类
                    resolver.getAllFiles().forEach {
                        it.accept(FileVisitor(data), Unit)
                    }
                    generateFactoryCode(data,it)
                }
            }
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun generateFactoryCode(data: FactoryData, ksFile: KSFile) {
        val interfaceType = "${data.className}Type"
        val interfaceTypeEnum = TypeSpec.enumBuilder(interfaceType).apply {
            data.childClass.forEach {
                addEnumConstant(it.simpleName.uppercase())
            }
        }.build()

        val interfaceTypeName = ClassName(data.packageName,interfaceType)
        val funType = FunSpec.builder("from")
            .addParameter(ParameterSpec.builder("type",interfaceTypeName).build())
            .returns(ClassName(data.packageName,data.className))
            .beginControlFlow("return when(type)")
            .apply {
                data.childClass.forEach {
                    addStatement("${interfaceType}.${it.simpleName.uppercase()} -> %T()",it)
                }
            }
            .endControlFlow()
            .build()

        val interfaceTypeFactory = "${data.className}Factory"
        val fileSpec = FileSpec.builder(data.packageName,interfaceTypeFactory)
            .addType(interfaceTypeEnum)  // 在该类生成 InterfaceType 的枚举类
            .addType(
                TypeSpec.interfaceBuilder(ClassName(data.packageName,interfaceTypeFactory))
                    .addType(
                        TypeSpec.companionObjectBuilder()
                            .addFunction(funType)
                            .build()
                    )
                    .build()
            )
            .build()
        fileSpec.writeTo(codeGenerator, Dependencies(true,ksFile))
    }

    inner class FileVisitor(val factoryData: FactoryData) : KSVisitorVoid() {
        private val visited = HashSet<Any>()

        private fun checkVisited(symbol:Any):Boolean{
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
                    val className = ClassName(
                        ksFile.packageName.asString(),
                        classDeclaration.simpleName.asString()
                    )
                    factoryData.childClass.add(className)
                }
            }
        }

    }

}