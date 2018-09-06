package com.md.flashset.download;

public class DownloadState {
    public static final int STATE_NOT_DOWNLOAD = 0;
    public static final int STATE_DOWNLOADING = 1;
    public static final int STATE_DOWNLOAD_FAIL = 2;
    public static final int STATE_DOWNLOAD_SUCCESS = 3;
    public static final int STATE_DOWNLOAD_CONNECTING = 4;
}