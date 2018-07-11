package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.callflash;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashInfo;

import java.io.File;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.CallFlashAlbumactivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide.GlideHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;

/**
 * Created by ChenR on 2017/12/21.
 */

public class CallFlashGifFragment extends Fragment {
    private static final String KEY_GIF_RESOURCE = "bundle_key_with_gif_resource_path";

    private View root = null;
    private ImageView mGifImageView;
    private CallFlashInfo mCallFlashInfo;
    private View mAddCustomContainer;
    private List<String> mNumbersForCallFlash;
    private View mIvCallHang;
    private View mIvCallAnswer;
    private boolean mIsHideHangAndAnswerButton;

    public static CallFlashGifFragment newInstance(CallFlashInfo info) {
        CallFlashGifFragment fragment = new CallFlashGifFragment();

        Bundle data = new Bundle();
        data.putSerializable(KEY_GIF_RESOURCE, info);

        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            CallFlashInfo info = (CallFlashInfo) bundle.getSerializable(KEY_GIF_RESOURCE);
            if (info != null) {
                this.mCallFlashInfo = info;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_call_flash_gif, container, false);
            mGifImageView = (ImageView) root.findViewById(R.id.flash_gif_view);
            mAddCustomContainer = root.findViewById(R.id.layout_call_flash_custom_container);
            mIvCallAnswer = root.findViewById(R.id.iv_call_answer);
            mIvCallHang = root.findViewById(R.id.iv_call_hang);

            if (mCallFlashInfo != null) {
                setGifImage();
            }

            if (mIsHideHangAndAnswerButton) {
                mIvCallAnswer.setVisibility(View.GONE);
                mIvCallHang.setVisibility(View.GONE);
            } else {
                mIvCallAnswer.setVisibility(View.VISIBLE);
                mIvCallHang.setVisibility(View.VISIBLE);
            }

            listener();
            startAnswerAnim();
        }
        return root;
    }

    private void listener() {
        mAddCustomContainer.findViewById(R.id.fiv_add_custom_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity act = getActivity();

                if (act != null) {
                    Intent intent = new Intent();
                    intent.setClass(act, CallFlashAlbumactivity.class);
                    intent.putExtra(ConstantUtils.COME_FROM_CALLAFTER, act.getIntent()
                            .getBooleanExtra(ConstantUtils.COME_FROM_CALLAFTER, false));
                    intent.putExtra(ConstantUtils.COME_FROM_PHONEDETAIL, act.getIntent()
                            .getBooleanExtra(ConstantUtils.COME_FROM_PHONEDETAIL, false));
//                    intent.putStringArrayListExtra(ConstantUtils.NUMBER_FOR_CALL_FLASH, mNumbersForCallFlash);
                    startActivity(intent);
                }
            }
        });
    }

    private void startAnswerAnim() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.answer_button_anim);
        root.findViewById(R.id.iv_call_answer).startAnimation(animation);
    }

    public void setShowOrHideAnswerAndHangButton(Boolean isHide) {
        mIsHideHangAndAnswerButton = isHide;
        if (mIvCallAnswer != null && mIvCallHang != null) {
            if (isHide) {
                mIvCallAnswer.setVisibility(View.GONE);
                mIvCallHang.setVisibility(View.GONE);
            } else {
                mIvCallAnswer.setVisibility(View.VISIBLE);
                mIvCallHang.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setGifImage();
        }
    }

    public void setCallFlashInfo(CallFlashInfo info) {
        this.mCallFlashInfo = info;
    }

    public void setGifImage() {
        if (mCallFlashInfo != null && mGifImageView != null) {
            if (mCallFlashInfo.isOnlionCallFlash) {
                mAddCustomContainer.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(mCallFlashInfo.path) && new File(mCallFlashInfo.path).exists()) {
                    if (mCallFlashInfo.path.endsWith(".gif") || mCallFlashInfo.path.endsWith(".GIF")) {
                        GlideHelper.with(getActivity()).loadGif(mCallFlashInfo.path).listener(new RequestListener<String, GifDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                                if (mCallFlashInfo.imgResId > 0) {
                                    GlideHelper.with(getActivity()).loadGif(mCallFlashInfo.imgResId).into(mGifImageView);
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        }).into(mGifImageView);
                    } else if (mCallFlashInfo.path.endsWith(".png") || mCallFlashInfo.path.endsWith(".PNG")
                            || mCallFlashInfo.path.endsWith(".jpg") || mCallFlashInfo.path.endsWith(".JPG")
                            || mCallFlashInfo.path.endsWith(".jpeg") || mCallFlashInfo.path.endsWith(".JPEG")) {
                        if (mAddCustomContainer != null) {
                            mAddCustomContainer.setVisibility(View.GONE);
                        }
                        GlideHelper.with(getActivity()).load("file://" + mCallFlashInfo.path).into(mGifImageView);
                    }
                } else {
                    GlideHelper.with(getActivity()).load(mCallFlashInfo.img_vUrl).into(mGifImageView);
                }
            } else {
                // 本地来电秀, 自定义来电秀
                if (mCallFlashInfo.flashType == FlashLed.FLASH_TYPE_CUSTOM) {
                    if (!TextUtils.isEmpty(mCallFlashInfo.path)) {
                        if (mCallFlashInfo.path.endsWith(".GIF") || mCallFlashInfo.path.endsWith(".gif")) {
                            GlideHelper.with(getActivity()).loadGif(mCallFlashInfo.path).listener(new RequestListener<String, GifDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                                    if (mCallFlashInfo.imgResId > 0) {
                                        GlideHelper.with(getActivity()).loadGif(mCallFlashInfo.imgResId).into(mGifImageView);
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(mGifImageView);
                        } else {
                            GlideHelper.with(getActivity()).load("file://" + mCallFlashInfo.path).into(mGifImageView);
                        }
                    } else {
                        if (mAddCustomContainer != null) {
                            mAddCustomContainer.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (mAddCustomContainer != null) {
                        mAddCustomContainer.setVisibility(View.GONE);
                    }
                    if (mCallFlashInfo.imgResId > 0) {
                        GlideHelper.with(getActivity()).loadGif(mCallFlashInfo.imgResId).into(mGifImageView);
                    } else {
                        GlideHelper.with(getActivity()).load(new ColorDrawable(getResources().getColor(R.color.black))).into(mGifImageView);
                    }
                }
            }

            /*if (!TextUtils.isEmpty(mCallFlashInfo.path) && new File(mCallFlashInfo.path).exists()) {
                if (mCallFlashInfo.path.endsWith(".gif") || mCallFlashInfo.path.endsWith(".GIF")) {
                    mAddCustomContainer.setVisibility(View.GONE);

                    Drawable drawable = mGifImageView.getDrawable();
                    try {
                        if (drawable != null && drawable instanceof GifDrawable) {
                            GifDrawable gifDrawable = (GifDrawable) drawable;
                            gifDrawable.recycle();
                        }
                        drawable = new GifDrawable(mCallFlashInfo.path);
                        mGifImageView.setImageDrawable(drawable);
                    } catch (Throwable e) {
                        if (mCallFlashInfo.imgResId > 0) {
                            mGifImageView.setImageResource(mCallFlashInfo.imgResId);
                        } else {
                            mGifImageView.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
                        }
                    }
                } else if (mCallFlashInfo.path.endsWith(".png") || mCallFlashInfo.path.endsWith(".PNG")
                        || mCallFlashInfo.path.endsWith(".jpg") || mCallFlashInfo.path.endsWith(".JPG")
                        || mCallFlashInfo.path.endsWith(".jpeg") || mCallFlashInfo.path.endsWith(".JPEG")) {
                    if (mAddCustomContainer != null)
                        mAddCustomContainer.setVisibility(View.GONE);

                    try {
//                        mGifImageView.setImageBitmap(BitmapFactory.decodeFile(mCallFlashInfo.path));
                        mGifImageView.setImageBitmap(ImageLoaderHelper.getInstance().loadImageSync("file://" + mCallFlashInfo.path));
                    } catch (Exception e) {
                        mGifImageView.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
                    }

                } else if (mCallFlashInfo.flashType == FlashLed.FLASH_TYPE_CUSTOM) {
                    if (mAddCustomContainer != null) {
                        mAddCustomContainer.setVisibility(View.VISIBLE);
                    }
                } else if (mCallFlashInfo.imgResId > 0) {
                    mGifImageView.setImageResource(mCallFlashInfo.imgResId);
                } else {
                    mGifImageView.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
                }
            } else if (mCallFlashInfo.flashType == FlashLed.FLASH_TYPE_CUSTOM) {
                if (mAddCustomContainer != null) {
                    mAddCustomContainer.setVisibility(View.VISIBLE);
                }
            } else if (mCallFlashInfo.imgResId > 0) {
                mGifImageView.setImageResource(mCallFlashInfo.imgResId);
            } else {
//                mGifImageView.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
            }*/
        }
    }

    public void setNumbersForCallFlash(List<String> numbersForCallFlash) {
        mNumbersForCallFlash = numbersForCallFlash;
    }
}
