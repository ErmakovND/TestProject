package com.testproject.service.audio;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.AudioAttributesCompat;
import androidx.media.AudioFocusRequestCompat;
import androidx.media.AudioManagerCompat;
import androidx.media.MediaBrowserServiceCompat;

import java.util.List;

public class AudioPlaybackService extends MediaBrowserServiceCompat {

    private static final String MEDIA_ROOT = "MediaRoot";

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    private MediaMetadataCompat.Builder mediaMetadataBuilder;
    private AudioFocusRequestCompat audioFocusRequest;
    private AudioPlayer audioPlayer;

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MEDIA_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, "AudioService");

        playbackStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaMetadataBuilder = new MediaMetadataCompat.Builder();

        mediaSession.setPlaybackState(playbackStateBuilder.build());
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        setSessionToken(mediaSession.getSessionToken());
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        private final AudioManager audioManager;
        public MediaSessionCallback(Context context) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public void onPlay() {
            AudioAttributesCompat audioAttributes = new AudioAttributesCompat.Builder()
                    .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                    .build();
            audioFocusRequest = new AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .build();
            if (AudioManagerCompat.requestAudioFocus(audioManager, audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                startService(new Intent(getApplicationContext(), AudioPlaybackService.class));
                mediaSession.setActive(true);
                audioPlayer.play();
            }
        }
        @Override
        public void onPause() {
            audioPlayer.pause();
        }
        @Override
        public void onStop() {
            AudioManagerCompat.abandonAudioFocusRequest(audioManager, audioFocusRequest);
            stopService(new Intent(getApplicationContext(), AudioPlaybackService.class));
            mediaSession.setActive(false);
            audioPlayer.stop();
        }
    }
}
