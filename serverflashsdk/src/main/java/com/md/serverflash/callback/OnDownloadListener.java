package com.md.serverflash.callback;

import java.io.File;

/**
 * Created by ChenR on 2018/5/10.
 */

public interface OnDownloadListener {
    void onConnecting(String url);
    void onFailure(String url);
    void onFailureForIOException(String url);
    void onProgress(String url, int progress);
    void onSuccess(String url, File file);
}
