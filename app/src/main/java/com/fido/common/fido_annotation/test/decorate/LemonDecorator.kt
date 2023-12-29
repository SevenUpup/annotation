package com.fido.common.fido_annotation.test.decorate

/**
@author FiDo
@description:
@date :2023/12/28 16:06
 */
class LemonDecorator(val beverage: Beverage):Beverage {
    override val description: String
        get() =  "${beverage.description},加柠檬"
    override val price: Float
        get() = beverage.price + 4.0f
}