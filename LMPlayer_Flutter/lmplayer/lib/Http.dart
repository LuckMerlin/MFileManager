import 'package:dio/dio.dart';
import 'Debug.dart';

class Http{

   Future<String> post(String url,String json) async{
    if(url==null||url.length<=0){
      Debug.W("Fail post http while url is invalid.");
      return null;
    }
    Response response=await Dio().post("/test",data:{"id":12,"name":"wendu"});

  }
}