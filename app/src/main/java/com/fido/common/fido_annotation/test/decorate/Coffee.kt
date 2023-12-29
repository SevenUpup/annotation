package com.fido.common.fido_annotation.test.decorate

/**
@author FiDo
@description: 咖啡饮料
@date :2023/12/28 15:53
 */
class Coffee :Beverage{
    override val description: String
        get() = "咖啡"
    override val price: Float
        get() = 10f
}