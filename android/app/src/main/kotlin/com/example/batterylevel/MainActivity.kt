package com.example.batterylevel

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val channel = "samples.flutter.dev/battery"
    private val channelEvent = "samples.flutter.dev/batteryEvent"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannelUtil.methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger,channel)
        MethodChannelUtil.methodChannel!!.setMethodCallHandler{
            call, result ->
            if (call.method == "getBatteryLevel") {
                val hashMap = call.arguments as HashMap<*, *>
                println("**************")
                println(hashMap)
                println(hashMap["jump"])
                println(hashMap["from"])
                /// 跳转到第二个页面，不影响返回结果
                startActivity(Intent(this,SecondActivity::class.java))
                val batteryLevel = getBatteryLevel()

                if (batteryLevel != -1){
                    result.success(batteryLevel)
                } else {
                    result.error("UNAVAILABLE","Battery level not available",null)
                }
            } else {
                result.notImplemented()
            }
        }

        var count = 0

        EventChannel(flutterEngine.dartExecutor.binaryMessenger,channelEvent).setStreamHandler(
            object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                    /// 创建Handler 循环发送事件到flutter
                    val handler: Handler = object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            events?.success(count++)
                            sendEmptyMessageDelayed(0,500)
                            super.handleMessage(msg)
                        }
                    }
                    handler.sendEmptyMessage(0)
                }

                override fun onCancel(arguments: Any?) {
                    Log.v("事件取消：", "Event Channel")
                }

            }
        )
    }

    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(
                Intent.ACTION_BATTERY_CHANGED)
            )
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }
        return batteryLevel
    }
}
