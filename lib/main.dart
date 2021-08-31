
import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData.light(),
      title: 'Channel',
      home: _HomePage(),
    );
  }
}


class _HomePage extends StatefulWidget {

  @override
  __HomePageState createState() => __HomePageState();
}

class __HomePageState extends State<_HomePage> {
  String text = "";
  static final platform = MethodChannel('samples.flutter.dev/battery');
  static final eventChannel = EventChannel('samples.flutter.dev/batteryEvent');
  late StreamSubscription _streamSubscription;

  @override
  void initState() {
    /// 注册native eventChannel监听
    _streamSubscription = eventChannel.receiveBroadcastStream().listen((event) {
      setState(() {
        text = "from eventChannel: $event";
      });
    }, onError: (e){
      print(e);
    }, cancelOnError: true);

    /// native call flutter 回调
    platform.setMethodCallHandler((call) async {
      switch(call.method){
        case "call":
          return "from flutter callback";
      }
    });
    super.initState();
  }

  @override
  void dispose() {
    /// 退出时取消监听
    _streamSubscription.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(text),
              TextButton(onPressed: () async {
                final map = <String, dynamic>{};
                map['jump'] = true;
                map['from'] = "method";
                /// 传递参数map类型，flutter call native，await 等待返回结果
                final ret = await platform.invokeMethod("getBatteryLevel", map);
                setState(() {
                  text = "from methodChannel $ret";
                });
              }, child: Text('Method')),
              TextButton(onPressed: (){
                if(_streamSubscription.isPaused){
                  print('resume');
                _streamSubscription.resume();
                } else {
                  print('pause');
                  _streamSubscription.pause();
                }
              }, child: Text('Event'))
            ]
          )
      ),
    );
  }
}
