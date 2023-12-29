package com.fido.common.fido_annotation.test.decorate

/**
@author FiDo
@description:
@date :2023/12/28 16:04
 */
class MilkeDecorator(val beverage: Beverage):Beverage {
    override val description: String
        get() =  "${beverage.description}，加奶"
    override val price: Float
        get() = beverage.price + 3.0f
}