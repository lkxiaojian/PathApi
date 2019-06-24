package com.example.administrator.pathapi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        // 常用api
//        setContentView(PathApi(this))
        //多阶贝塞尔曲线
        setContentView(BezierView(this))

    }
}
