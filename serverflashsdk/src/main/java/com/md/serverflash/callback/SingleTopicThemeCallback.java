package com.md.serverflash.callback;

import com.md.serverflash.beans.Theme;

import java.util.List;

/**
 * Created by ChenR on 2018/6/14.
 */

public interface SingleTopicThemeCallback extends OnFailureCallback {
    void onSuccess(int code, List<Theme> data);
}
