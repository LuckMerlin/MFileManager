cmake_minimum_required(VERSION 3.4.1)
set(FFROOT ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI})
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${FFROOT}")

add_library( lmplayer
    SHARED

    ../../ios/Classes/Player.cpp )
