import 'package:lmplayer/generated/json/base/json_convert_content.dart';

class CloudMediaEntity with JsonConvert<CloudMediaEntity> {
	CloudMediaData data;
	int what;
	String note;
	bool success;
}

class CloudMediaData with JsonConvert<CloudMediaData> {
	int length;
	int from;
	int limit;
	List<CloudMediaDataData> data;
}

class CloudMediaDataData with JsonConvert<CloudMediaDataData> {
	int type;
	int id;
	dynamic note;
	String url;
	String logo;
	dynamic time;
	String name;
	dynamic duration;
	dynamic favorite;
}
