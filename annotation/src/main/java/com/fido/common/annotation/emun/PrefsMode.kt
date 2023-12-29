package com.fido.common.annotation.emun

/**
@author FiDo
@description:
@date :2023/12/27 10:20
 */
enum class PrefsMode(val value: Int) {

    MODE_PRIVATE(0x0000),
    MODE_WORLD_READABLE(0x0001),
    MODE_WORLD_WRITEABLE(0x0002),
    MODE_MULTI_PROCESS(0x0004),

}