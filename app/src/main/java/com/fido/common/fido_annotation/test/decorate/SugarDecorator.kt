package com.fido.common.fido_annotation.test.decorate

/**
@author FiDo
@description:
@date :2023/12/28 15:56
 */
class SugarDecorator(val beverage: Beverage):Beverage {
    override val description: String
        get() =   "${beverage.description},加糖"
    override val price: Float
        get() = beverage.price + 2.0f
}