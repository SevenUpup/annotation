package com.fido.common.fido_annotation.test.factory

import com.fido.common.annotation.test.InterfaceFactory

/**
@author FiDo
@description: @InterfaceFactory 生成接口工厂模型
@date :2023/12/28 16:38
 */
@InterfaceFactory
interface Animal {

    fun makeSound():String

    companion object Factory

}