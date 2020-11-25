
import 'package:flutter/material.dart';
import 'package:lmplayer/Http.dart';

class MediasList extends StatefulWidget{

  @override
  State<StatefulWidget> createState() {
    return _MediaPageState();
  }

}


class _MediaPageState extends State<MediasList> {
  ScrollController _mScrollController = ScrollController(); //listview的控制器
  List mList = new List(); //列表要展示的数据
  bool mLoading = false;

  @override
  void initState() {
    super.initState();
    _mScrollController.addListener(() {
      if (_mScrollController.position.pixels == _mScrollController.position.maxScrollExtent) {
        _getMore();
      }
    });
    _getMore();

  }

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemBuilder: _renderRow,
      itemCount: mList.length,
      controller: _mScrollController,
    );
  }

  Widget _renderRow(BuildContext context, int index) {
    return ListTile(
      title: Text(""+index.toString()+" "+mList[index]),
    );
  }

  Future _getMore() async {
    if (!mLoading) {
      setState(() {
        mLoading = true;
      });
      Http().post("https://xxxx.com.cn/search", json);
      
      await Future.delayed(Duration(seconds: 1), () {
        setState(() {
          mList.addAll(List.generate(20, (i) => '第次上拉来的数据'));
          print(mList.length);
          mLoading = false;
        });
      });
    }
  }

  @override
  void dispose() {
    super.dispose();
    _mScrollController.dispose();
  }
}