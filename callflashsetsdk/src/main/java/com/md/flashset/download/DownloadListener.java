package com.md.flashset.download;

/**
 * 下载监听
 *
 */
public interface DownloadListener {
    void onFinished(String gifPath);

    void onProgress(int progress);

    void onPause();

    void onCancel();

    void onFailed();
}
