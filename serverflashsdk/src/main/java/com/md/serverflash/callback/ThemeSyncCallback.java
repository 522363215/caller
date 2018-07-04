package com.md.serverflash.callback;


import com.md.serverflash.beans.Theme;

import java.util.List;

/**
 * Created by ChenR on 2018/5/9.
 */

public interface ThemeSyncCallback extends OnFailureCallback {
    void onSuccess(List<Theme> data);
}
