
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'MediasList.dart';

class PlayerWidget extends StatelessWidget{
  int mCurrentIndex=0;

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: Scaffold(
          appBar: AppBar(title: Text("LMPlayer")),
          body: new MediasList(),
          bottomNavigationBar:BottomNavigationBar(
            showUnselectedLabels: true,
            showSelectedLabels: true,
            currentIndex: mCurrentIndex,
            items: [
              BottomNavigationBarItem(
                backgroundColor: Colors.blue,
                icon: Icon(Icons.folder),
                label: "Local",
              ),
              BottomNavigationBarItem(
                backgroundColor: Colors.green,
                icon: Icon(Icons.cloud_done_sharp),
                label: "Online",
              ),
              BottomNavigationBarItem(
                backgroundColor: Colors.amber,
                icon: Icon(Icons.list_alt_rounded),
                label: "Sheet",
              ),
              BottomNavigationBarItem(
                backgroundColor: Colors.red,
                icon: Icon(Icons.settings),
                label: "Settings",
              ),
            ],
          ),
      ),
    );
  }
}