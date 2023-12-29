package com.fido.common.fido_annotation.test.factory;

import androidx.annotation.NonNull;

/**
 * @author FiDo
 * @description:
 * @date :2023/12/29 10:07
 */
public class Woman implements Human{
    @NonNull
    @Override
    public String say() {
        return "i am queen of the world!";
    }
}
