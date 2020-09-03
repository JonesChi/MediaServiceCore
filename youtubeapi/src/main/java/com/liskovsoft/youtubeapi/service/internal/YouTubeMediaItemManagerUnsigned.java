package com.liskovsoft.youtubeapi.service.internal;

import com.liskovsoft.mediaserviceinterfaces.data.MediaItem;
import com.liskovsoft.youtubeapi.next.WatchNextServiceUnsigned;
import com.liskovsoft.youtubeapi.next.models.WatchNextResult;
import com.liskovsoft.youtubeapi.service.data.YouTubeMediaItem;
import com.liskovsoft.youtubeapi.service.data.YouTubeMediaItemFormatInfo;
import com.liskovsoft.youtubeapi.service.data.YouTubeMediaItemMetadata;
import com.liskovsoft.youtubeapi.videoinfo.VideoInfoServiceUnsigned;
import com.liskovsoft.youtubeapi.videoinfo.models.VideoInfoResult;

public class YouTubeMediaItemManagerUnsigned implements MediaItemManagerInt {
    private static YouTubeMediaItemManagerUnsigned sInstance;
    private final WatchNextServiceUnsigned mWatchNextServiceUnsigned;
    private final VideoInfoServiceUnsigned mVideoInfoServiceUnsigned;

    private YouTubeMediaItemManagerUnsigned() {
       mWatchNextServiceUnsigned = WatchNextServiceUnsigned.instance();
       mVideoInfoServiceUnsigned = VideoInfoServiceUnsigned.instance();
    }

    public static YouTubeMediaItemManagerUnsigned instance() {
        if (sInstance == null) {
            sInstance = new YouTubeMediaItemManagerUnsigned();
        }

        return sInstance;
    }

    public static void unhold() {
        sInstance = null;
        WatchNextServiceUnsigned.unhold();
    }

    //@Override
    //public YouTubeMediaItemMetadata getMetadata(MediaItem item) {
    //    YouTubeMediaItem ytMediaItem = (YouTubeMediaItem) item;
    //
    //    YouTubeMediaItemMetadata metadata = ytMediaItem.getMetadata();
    //
    //    if (metadata == null) {
    //        metadata = getMetadata(item.getMediaId());
    //
    //        ytMediaItem.setMetadata(metadata);
    //    }
    //
    //    return metadata;
    //}

    @Override
    public YouTubeMediaItemMetadata getMetadata(String videoId) {
        WatchNextResult watchNextResult = mWatchNextServiceUnsigned.getWatchNextResult(videoId);

        return YouTubeMediaItemMetadata.from(watchNextResult);
    }

    @Override
    public YouTubeMediaItemFormatInfo getFormatInfo(MediaItem item) {
        YouTubeMediaItem ytMediaItem = (YouTubeMediaItem) item;

        YouTubeMediaItemFormatInfo formatInfo = ytMediaItem.getFormatInfo();

        if (formatInfo == null) {
            VideoInfoResult videoInfo = mVideoInfoServiceUnsigned.getVideoInfo(item.getMediaId());

            formatInfo = YouTubeMediaItemFormatInfo.from(videoInfo);

            ytMediaItem.setFormatInfo(formatInfo);
        }

        return formatInfo;
    }

    @Override
    public YouTubeMediaItemFormatInfo getFormatInfo(String videoId) {
        VideoInfoResult videoInfo = mVideoInfoServiceUnsigned.getVideoInfo(videoId);

        return YouTubeMediaItemFormatInfo.from(videoInfo);
    }

    @Override
    public void updateHistoryPosition(MediaItem item, float positionSec) {
        // Do nothing, user is unsigned
    }

    @Override
    public void updateHistoryPosition(String videoId, float positionSec) {
        // Do nothing, user is unsigned
    }

    @Override
    public void setLike(MediaItem item) {
        // Do nothing, user is unsigned
    }

    @Override
    public void removeLike(MediaItem item) {
        // Do nothing, user is unsigned
    }

    @Override
    public void setDislike(MediaItem item) {
        // Do nothing, user is unsigned
    }

    @Override
    public void removeDislike(MediaItem item) {
        // Do nothing, user is unsigned
    }

    @Override
    public void subscribe(MediaItem item) {
        // Do nothing, user is unsigned
    }

    @Override
    public void unsubscribe(MediaItem item) {
        // Do nothing, user is unsigned
    }
}