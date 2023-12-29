package com.fido.common.fido_annotation.test.decorate

/**
@author FiDo
@description:
@date :2023/12/28 16:07
 */
class TestMain {

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            var coffee :Beverage = Coffee()
            coffee = SugarDecorator(coffee)
            coffee = MilkeDecorator(coffee)
            coffee = LemonDecorator(coffee)
            println("您点的是："+coffee.description + " 价格=" + coffee.price)
        }
    }

}