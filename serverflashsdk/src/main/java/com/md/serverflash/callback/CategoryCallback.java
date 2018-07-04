package com.md.serverflash.callback;


import com.md.serverflash.beans.Category;

import java.util.List;

/**
 * Created by ChenR on 2018/5/9.
 */

public interface CategoryCallback extends OnFailureCallback {
    void onSuccess(int code, List<Category> data);
}
