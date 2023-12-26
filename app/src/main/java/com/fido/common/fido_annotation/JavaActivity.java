package com.fido.common.fido_annotation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fido.common.annotation.view.BindView;

/**
 * @author FiDo
 * @description:
 * @date :2023/12/26 8:48
 */
public class JavaActivity extends AppCompatActivity {

    @BindView(R.id.mBt)
    Button mBt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);

        JavaActivityViewBindKt.bindView(this);
        mBt.setOnClickListener(v -> Toast.makeText(JavaActivity.this,"java ksp success",Toast.LENGTH_SHORT).show());
    }
}
