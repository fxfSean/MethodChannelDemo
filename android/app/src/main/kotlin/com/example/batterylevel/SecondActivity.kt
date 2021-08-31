package com.example.batterylevel

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import io.flutter.plugin.common.MethodChannel

class SecondActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

    }

    fun onClick(view: View) {
        // native 调用 flutter
        MethodChannelUtil.methodChannel?.invokeMethod("call",null, object : MethodChannel.Result {
            override fun success(result: Any?) {
                (view as TextView).text = result.toString()
            }

            override fun error(errorCode: String?, errorMessage: String?, errorDetails: Any?) {
                Log.v("methodChannel: ", "errorCode: $errorCode errorMsg: $errorMessage")
            }

            override fun notImplemented() {
                Log.v("methodChannel: ", "notImplemented")
            }

        })
    }
}