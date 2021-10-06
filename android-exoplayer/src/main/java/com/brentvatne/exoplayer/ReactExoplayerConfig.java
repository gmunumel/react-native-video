package com.brentvatne.exoplayer;

// Added import
import com.brentvatne.exoplayer.bitrate.BitrateAdaptionPreset;
// End import
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;

/**
 * Extension points to configure the Exoplayer instance
 */
public interface ReactExoplayerConfig {
    LoadErrorHandlingPolicy buildLoadErrorHandlingPolicy(int minLoadRetryCount);

    DefaultBandwidthMeter getBandwidthMeter();

    // Added Bitrate
    BitrateAdaptionPreset getBitrateAdaptionPreset();
}
