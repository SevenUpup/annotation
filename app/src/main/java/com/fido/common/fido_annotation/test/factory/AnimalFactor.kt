package com.fido.common.fido_annotation.test.factory

/**
@author FiDo
@description:
@date :2023/12/28 16:42
 */

class TestMain(){
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
//            val animalSound = Animal.from(AnimalType.CAT).makeSound()
//            println(animalSound)
            val animal = AnimalFactory.from(AnimalType.DRAGON)
            println(animal.makeSound())
            val human = HumanFactory.from(HumanType.MAN)
            println(human.say())
        }
    }

}

//enum class AnimalType{DOG,CAT,PIG,DRAGON}
//fun Animal.Factory.from(animalType: AnimalType):Animal{
//    return when (animalType) {
//        AnimalType.DOG -> Dog()
//        AnimalType.CAT -> Cat()
//        AnimalType.PIG -> Pig()
//        AnimalType.DRAGON -> Dragon()
//    }
//}
