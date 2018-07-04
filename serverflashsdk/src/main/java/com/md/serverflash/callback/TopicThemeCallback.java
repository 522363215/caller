package com.md.serverflash.callback;


import com.md.serverflash.beans.Theme;

import java.util.List;
import java.util.Map;

/**
 * Created by ChenR on 2018/5/9.
 */

public interface TopicThemeCallback extends OnFailureCallback {
    void onSuccess(int code, Map<String, List<Theme>> data);
}
