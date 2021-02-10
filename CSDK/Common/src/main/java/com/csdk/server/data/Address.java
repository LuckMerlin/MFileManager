package com.csdk.server.data;

/**
 * Create LuckMerlin
 * Date 14:44 2020/9/8
 * TODO
 */
public final class Address {
    private final double mLongitude;
    private final double mLatitude;
    private final String mCityCode;
    private final String mCity;
    private final String mDistrict;

    public Address(double longitude, double latitude, String cityCode, String city, String district){
        mCityCode=cityCode;
        mLongitude=longitude;
        mLatitude=latitude;
        mCity=city;
        mDistrict=district;
    }

    public final String getCityCode(){
        return mCityCode;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public String getCity() {
        return mCity;
    }

    public String getDistrict() {
        return mDistrict;
    }
}
