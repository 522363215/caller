package com.hiblock.caller.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiblock.caller.R;
import com.hiblock.caller.utils.LanguageSettingUtil;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by Sandy on 16/7/5.
 */
public class ActionBar extends RelativeLayout {

    FontIconView iconFontBack;
    TextView tvTitle;
    String fontIcon;
    String fontIconLdrtl;
    private RoundedImageView ivContactPhoto;
    private LinearLayout layoutContactSearch;
    private EditText etSearch;
    private FontIconView fivCloseEditText;
    private LinearLayout layoutContactSearchAndAdd;
    private FontIconView fivContactSearch;
    private FontIconView fivContactAdd;
    private FontIconView fivContactPhoto;

    public ActionBar(Context context) {
        super(context);
        init(context, null);
    }

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {


        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ActionBar, 0, 0);
            try {
                boolean layoutFullLine = attributes.getBoolean(R.styleable.ActionBar_layoutFullLine, false);
                if (layoutFullLine) {
                    LayoutInflater.from(context).inflate(R.layout.layout_action_bar_full_line, this);
                } else {
                    LayoutInflater.from(context).inflate(R.layout.layout_action_bar, this);
                }


                iconFontBack = (FontIconView) findViewById(R.id.imgReturn);
                tvTitle = (TextView) findViewById(R.id.txtTitle);
                ivContactPhoto = (RoundedImageView) findViewById(R.id.iv_contact_photo);
                fivContactPhoto = (FontIconView) findViewById(R.id.fiv_contact_icon);

                String font = attributes.getString(R.styleable.ActionBar_title);
                tvTitle.setText(font);

                int txtColor = attributes.getColor(R.styleable.ActionBar_txtColor, getResources().getColor(R.color.whitesmoke));
                tvTitle.setTextColor(txtColor);

                int backColor = attributes.getColor(R.styleable.ActionBar_backColor, getResources().getColor(R.color.whitesmoke));
                iconFontBack.setTextColor(backColor);

                fontIcon = attributes.getString(R.styleable.ActionBar_fontIcon);
                fontIconLdrtl = attributes.getString(R.styleable.ActionBar_fontIconLdrtl);

                if (fontIcon != null && fontIconLdrtl == null) {
                    fontIconLdrtl = fontIcon;
                } else if (fontIcon == null) {
                    fontIcon = getResources().getString(R.string.icon_arrow);
                    fontIconLdrtl = getResources().getString(R.string.icon_arrow);
                }
                if (LanguageSettingUtil.isLayoutReverse(context)) {
                    iconFontBack.setText(fontIconLdrtl);
                    iconFontBack.setRotation(180);
                } else {
                    iconFontBack.setText(fontIcon);
                }

            } finally {
                attributes.recycle();
            }
        }
    }

    public void setBackImg(String originLang) {
        if ("ar".equals(originLang) || "fa".equals(originLang)) {
            iconFontBack.setText(R.string.icon_arrow);
        } else {
            iconFontBack.setText(R.string.icon_arrow);
        }
    }

    public void setBackImg(int resId){
        iconFontBack.setText(resId);
    }

    public void setTitle(String title) {
        tvTitle.setVisibility(VISIBLE);
        tvTitle.setText(title);
    }

    public void setTitle(int resId) {
        tvTitle.setVisibility(VISIBLE);
        tvTitle.setText(resId);
    }

    public void setOnBackClickListener(OnClickListener l) {
        iconFontBack.setOnClickListener(l);
    }

    public void setContactPhoto(boolean isHavePhoto,Bitmap bitmap){
        if (isHavePhoto){
            if (bitmap!=null){
                ivContactPhoto.setVisibility(VISIBLE);
                fivContactPhoto.setVisibility(GONE);
                ivContactPhoto.setImageBitmap(bitmap);
            }else {
                ivContactPhoto.setVisibility(GONE);
                fivContactPhoto.setVisibility(VISIBLE);
            }
        }else {
            ivContactPhoto.setVisibility(GONE);
            fivContactPhoto.setVisibility(VISIBLE);
        }

    }
}
