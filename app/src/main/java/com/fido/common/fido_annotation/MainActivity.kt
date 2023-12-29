package com.fido.common.fido_annotation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.fido.common.annotation.view.BindView

class MainActivity : AppCompatActivity() {

    @BindView(R.id.mBt)
    lateinit var mBt:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        BindViewUtils.bind(this)
//        bindView()
        mBt.setOnClickListener {
            Toast.makeText(this,"clike me by BindView",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,JavaActivity::class.java))
        }
    }
}