import 'package:lmplayer/bean/cloud_media_entity.dart';

cloudMediaEntityFromJson(CloudMediaEntity data, Map<String, dynamic> json) {
	if (json['data'] != null) {
		data.data = new CloudMediaData().fromJson(json['data']);
	}
	if (json['what'] != null) {
		data.what = json['what']?.toInt();
	}
	if (json['note'] != null) {
		data.note = json['note']?.toString();
	}
	if (json['success'] != null) {
		data.success = json['success'];
	}
	return data;
}

Map<String, dynamic> cloudMediaEntityToJson(CloudMediaEntity entity) {
	final Map<String, dynamic> data = new Map<String, dynamic>();
	if (entity.data != null) {
		data['data'] = entity.data.toJson();
	}
	data['what'] = entity.what;
	data['note'] = entity.note;
	data['success'] = entity.success;
	return data;
}

cloudMediaDataFromJson(CloudMediaData data, Map<String, dynamic> json) {
	if (json['length'] != null) {
		data.length = json['length']?.toInt();
	}
	if (json['from'] != null) {
		data.from = json['from']?.toInt();
	}
	if (json['limit'] != null) {
		data.limit = json['limit']?.toInt();
	}
	if (json['data'] != null) {
		data.data = new List<CloudMediaDataData>();
		(json['data'] as List).forEach((v) {
			data.data.add(new CloudMediaDataData().fromJson(v));
		});
	}
	return data;
}

Map<String, dynamic> cloudMediaDataToJson(CloudMediaData entity) {
	final Map<String, dynamic> data = new Map<String, dynamic>();
	data['length'] = entity.length;
	data['from'] = entity.from;
	data['limit'] = entity.limit;
	if (entity.data != null) {
		data['data'] =  entity.data.map((v) => v.toJson()).toList();
	}
	return data;
}

cloudMediaDataDataFromJson(CloudMediaDataData data, Map<String, dynamic> json) {
	if (json['type'] != null) {
		data.type = json['type']?.toInt();
	}
	if (json['id'] != null) {
		data.id = json['id']?.toInt();
	}
	if (json['note'] != null) {
		data.note = json['note'];
	}
	if (json['url'] != null) {
		data.url = json['url']?.toString();
	}
	if (json['logo'] != null) {
		data.logo = json['logo']?.toString();
	}
	if (json['time'] != null) {
		data.time = json['time'];
	}
	if (json['name'] != null) {
		data.name = json['name']?.toString();
	}
	if (json['duration'] != null) {
		data.duration = json['duration'];
	}
	if (json['favorite'] != null) {
		data.favorite = json['favorite'];
	}
	return data;
}

Map<String, dynamic> cloudMediaDataDataToJson(CloudMediaDataData entity) {
	final Map<String, dynamic> data = new Map<String, dynamic>();
	data['type'] = entity.type;
	data['id'] = entity.id;
	data['note'] = entity.note;
	data['url'] = entity.url;
	data['logo'] = entity.logo;
	data['time'] = entity.time;
	data['name'] = entity.name;
	data['duration'] = entity.duration;
	data['favorite'] = entity.favorite;
	return data;
}