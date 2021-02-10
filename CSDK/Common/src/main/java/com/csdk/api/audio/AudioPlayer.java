package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 17:12 2021/1/22
 * TODO
 */
public interface AudioPlayer {
   boolean playOrStop(final AudioObject audioObject);
   boolean stop(final AudioObject audioObject);
   boolean add(OnAudioStatusChange callback);
   boolean remove(OnAudioStatusChange callback);
   AudioObject getPlaying();
}
