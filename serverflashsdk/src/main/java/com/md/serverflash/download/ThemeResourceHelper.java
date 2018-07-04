package com.md.serverflash.download;

import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.local.ThemeSyncLocal;
import com.md.serverflash.util.Async;
import com.md.serverflash.util.HttpUtil;
import com.md.serverflash.util.LogUtil;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ThemeResourceHelper {
    private static final String THEME_REQUEST_URL = "http://material.lionmobi.com/api.php";
    private static final String KEY_HTTP = "*2od2S!#%s";
    private boolean mIsCanWrite;

    private ThemeResourceHelper() {
    }

    private static ThemeResourceHelper instance = null;

    public static ThemeResourceHelper getInstance() {
        if (instance == null) {
            synchronized (ThemeResourceHelper.class) {
                if (instance == null) {
                    instance = new ThemeResourceHelper();
                }
            }
        }
        return instance;
    }

    private List<String> mDownloadUrl = new ArrayList<>();
    private Map<String, Call> mResourcesUrlCache = new HashMap<>();
    private Map<String, OnDownloadListener> mListenerMap = new HashMap<>();
    private List<WeakReference<OnDownloadListener>> mGeneralListenerCache = new ArrayList<>();

    public void isCanWriteInStorage(boolean isCanWrite) {
        mIsCanWrite = isCanWrite;
    }

    private void unregisterDownloadListener(String url) {
        mListenerMap.remove(url);
    }

    public void addGeneralListener(OnDownloadListener listener) {
        mGeneralListenerCache.add(new WeakReference<OnDownloadListener>(listener));
    }

    public void removeGeneralListener(OnDownloadListener listener) {
        Iterator<WeakReference<OnDownloadListener>> iterator = mGeneralListenerCache.iterator();
        while (iterator.hasNext()) {
            WeakReference<OnDownloadListener> reference = iterator.next();
            if (reference != null && reference.get() != null && reference.get() == listener) {
                mGeneralListenerCache.remove(reference);
                break;
            }
        }
    }

    public void downloadThemeResources(final String objId, final String url, final OnDownloadListener listener) {
        if (mDownloadUrl.contains(url)) {
            // downloading already
            if (listener != null) {
                mListenerMap.put(url, listener);
            }
            return;
        } else {
            final File file = ThemeSyncManager.getInstance().getFileByUrl(ThemeSyncManager.getInstance().getContext(), url);
            if (file != null && file.exists()) {
                if (listener != null)
                    listener.onSuccess(url, file);
                return;
            }
        }

        // download from server
        final File file = ThemeSyncManager.getInstance().getFileByUrl(ThemeSyncManager.getInstance().getContext(), url);
        final File tempFile = new File(file.getAbsoluteFile() + ".temp");
        LogUtil.d("adadada", "downloadThemeResources file path:" + file.getAbsolutePath());
        mDownloadUrl.add(url);
        if (listener != null) mListenerMap.put(url, listener);
        Request request = new Request.Builder().url(url).build();
        Call call = new OkHttpClient().newCall(request); // TODO: one ok http client?
        mResourcesUrlCache.put(url, call);

        reportDownloadEvent(url, objId, "download_request");

        if (!isCanWriteInStorage(tempFile)) {
            LogUtil.d("dadadada", "downloadThemeResources not can write in storage need sdcard permission");
            Async.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OnDownloadListener mCurrentListener = mListenerMap.get(url);
                    if (mCurrentListener != null) {
                        mCurrentListener.onFailureForIOException(url);
                    }
                    handleFailed(url);
                }
            });
            return;
        }

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Async.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        OnDownloadListener mCurrentListener = mListenerMap.get(url);
                        if (mCurrentListener != null) {
                            mCurrentListener.onFailure(url);
                        }
                        for (int i = mGeneralListenerCache.size() - 1; i >= 0; i--) {
                            WeakReference<OnDownloadListener> reference = mGeneralListenerCache.get(i);
                            if (reference != null && reference.get() != null) {
                                reference.get().onFailure(url);
                            }
                        }
                        handleFailed(url);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] buf = new byte[1024];
                int len;
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    if (!tempFile.createNewFile()) {
                        throw new IOException("create " + url + " failed !");
                    }
                    fos = new FileOutputStream(tempFile);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        final int progress = (int) (sum * 1.0f / total * 100);
                        Async.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                OnDownloadListener mCurrentListener = mListenerMap.get(url);
                                if (mCurrentListener != null) {
                                    mCurrentListener.onProgress(url, progress);
                                }

                                for (int i = mGeneralListenerCache.size() - 1; i >= 0; i--) {
                                    WeakReference<OnDownloadListener> reference = mGeneralListenerCache.get(i);
                                    if (reference != null && reference.get() != null) {
                                        reference.get().onProgress(url, progress);
                                    }
                                }
                            }
                        });
                    }
                    fos.flush();

                    if (total != -1 && sum != 0 && total == sum) {
                        Async.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tempFile.renameTo(file);
                                OnDownloadListener mCurrentListener = mListenerMap.get(url);
                                if (mCurrentListener != null) {
                                    mCurrentListener.onSuccess(url, file);
                                }
                                for (int i = 0; i < mGeneralListenerCache.size(); i++) {
                                    WeakReference<OnDownloadListener> reference = mGeneralListenerCache.get(i);
                                    if (reference != null && reference.get() != null) {
                                        reference.get().onSuccess(url, file);
                                    }
                                }
                                reportDownloadEvent(url, objId, "download_success");
                                ThemeSyncLocal.getInstance().markDownloadedTheme(url);
                                mDownloadUrl.remove(url);
                                mListenerMap.remove(url);
                                mResourcesUrlCache.remove(url);
                            }
                        });
                    } else {
                        Async.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                OnDownloadListener mCurrentListener = mListenerMap.get(url);
                                if (mCurrentListener != null) {
                                    mCurrentListener.onFailure(url);
                                }

                                for (int i = mGeneralListenerCache.size() - 1; i >= 0; i--) {
                                    WeakReference<OnDownloadListener> reference = mGeneralListenerCache.get(i);
                                    if (reference != null && reference.get() != null) {
                                        reference.get().onFailure(url);
                                    }
                                }
                                handleFailed(url);
                            }
                        });
                    }

                } catch (Exception e) {
                    LogUtil.e("dadadada", "downloadThemeResources Exception e :" + e.getClass().getName() + ",message:" + e.getMessage());
                    e.printStackTrace();
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            OnDownloadListener mCurrentListener = mListenerMap.get(url);
                            if (mCurrentListener != null) {
                                mCurrentListener.onFailure(url);
                            }

                            for (int i = mGeneralListenerCache.size() - 1; i >= 0; i--) {
                                WeakReference<OnDownloadListener> reference = mGeneralListenerCache.get(i);
                                if (reference != null && reference.get() != null) {
                                    reference.get().onFailure(url);
                                }
                            }
                            handleFailed(url);
                        }
                    });
                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (fos != null)
                            fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean isCanWriteInStorage(File tempFile) {
        boolean is = false;
        if (mIsCanWrite) {
            return true;
        }
        BufferedOutputStream os = null;
        try {
            if (!tempFile.createNewFile()) {
                throw new IOException("isCanWriteInStorage create file failed!");
            }
            if (tempFile.canWrite()) {
                os = new BufferedOutputStream(new FileOutputStream(tempFile));
                byte[] msg = "isCanWriteInStorage test".getBytes();
                os.write(msg, 0, msg.length);
                os.flush();
                mIsCanWrite = true;
                is = true;
            } else {
                is = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            is = false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(tempFile!=null && tempFile.exists()) {
                tempFile.delete();
            }
        }
        return is;
    }

    private void reportDownloadEvent(String url, String objId, String eventName) {
        if (!url.endsWith("jpg") && !url.endsWith("JGP")
                && !url.endsWith("JPEG") && !url.endsWith("jpeg")
                && !url.endsWith("PNG") && !url.endsWith("png")) {
            try {
                long id = Long.parseLong(objId);
                HttpUtil.requestEventStatistics(eventName, id);
            } catch (Exception e) {
            }
        }
    }

    private void handleFailed(String url) {
        try {
            File f = ThemeSyncManager.getInstance().getFileByUrl(ThemeSyncManager.getInstance().getContext(), url);
            if (f.exists()) f.delete();
            mDownloadUrl.remove(url);
            unregisterDownloadListener(url);
            Call remove = mResourcesUrlCache.remove(url);
            if (!remove.isCanceled()) {
                remove.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void uploadFile(final Map params, final File file, final OnDownloadListener listener) {
        if (file == null || !file.exists()) {
            return;
        }

        JSONObject jsonObject = new JSONObject(params);
        String jsonData = jsonObject.toString();
        MediaType mediaType = MediaType.parse("application/octet-stream");
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("data", jsonData);
        builder.addFormDataPart("sig", HttpUtil.MD5Encode(KEY_HTTP.replace("%s", jsonData)));
        builder.addFormDataPart("file", file.getName(), RequestBody.create(mediaType, file));

        final Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.url(THEME_REQUEST_URL).post(builder.build()).build();

        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onFailure(THEME_REQUEST_URL);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int responseCode = response.code();
                if (responseCode == 200) {

                } else {

                }
            }
        });
    }

    public void cancelDownloadByUrl(String url) {
        if (mDownloadUrl.contains(url)) {
            mDownloadUrl.remove(url);
        }

        if (mListenerMap.containsKey(url)) {
            mListenerMap.remove(url);
        }

        if (mResourcesUrlCache.containsKey(url)) {
            Call call = mResourcesUrlCache.remove(url);
            if (call != null && call.isExecuted() && !call.isCanceled()) {
                call.cancel();
            }
        }
    }

    public void cancelAllDownload() {
        Collection<Call> calls = mResourcesUrlCache.values();
        if (calls != null && calls.size() > 0) {
            for (Call call : calls) {
                if (call != null && call.isExecuted() && !call.isCanceled()) {
                    call.cancel();
                }
            }
        }

        mDownloadUrl.clear();
        mResourcesUrlCache.clear();
        mGeneralListenerCache.clear();
    }

    public void destroyAllCache() {
        mListenerMap.clear();
        mDownloadUrl.clear();
        mResourcesUrlCache.clear();
        mGeneralListenerCache.clear();

        mListenerMap = null;
        mDownloadUrl = null;
        mResourcesUrlCache = null;
        mGeneralListenerCache = null;
    }

}
