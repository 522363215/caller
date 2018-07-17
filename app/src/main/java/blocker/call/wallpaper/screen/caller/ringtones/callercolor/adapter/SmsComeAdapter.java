package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.Message;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.NumberInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ContactManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DateUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LinkifyUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.NumberUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.AvatarView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.OKCancelDialog;


/**
 * Created by admin on 2017/5/8.
 */

public class SmsComeAdapter extends BaseAdapter {
    private Context mContext;
    private List<Message> mMessages;
    private OKCancelDialog mDeletedialog;

    public SmsComeAdapter(Context context, List<Message> messages) {
        this.mContext = context;
        this.mMessages = messages;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Message getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_sms_come_listview_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        NumberInfo contact = ContactManager.getInstance().getContact(getItem(position).address);
        String number = NumberUtil.getLocalizationNumber(getItem(position).address);
        if (contact == null) {
            viewHolder.tvNumber.setVisibility(View.GONE);
            viewHolder.tvName.setText(number);
        } else {
            viewHolder.tvNumber.setVisibility(View.VISIBLE);
            viewHolder.tvNumber.setText(number);
            viewHolder.tvName.setText(contact.name);
            String photoId = ContactManager.getInstance().getContactPhotoId(getItem(position).address);
            viewHolder.avatarView.setPhotoId(photoId);
        }
        viewHolder.tvDate.setText(DateUtils.getHmsForTime(getItem(position).date, LanguageSettingUtil.getLocale(mContext)));
        setBody(getItem(position), viewHolder);

        //从第二个开始隐藏
        if (mMessages.size() > 1 && position >= 1) {
            String lastAddress = getItem(position - 1).address;
            if (getItem(position).address.equals(lastAddress)) {
                viewHolder.layoutMessageInfo.setVisibility(View.GONE);
            } else {
                viewHolder.layoutMessageInfo.setVisibility(View.VISIBLE);
            }
            LogUtil.d("smsAdapter", "lastAddress:" + getItem(position - 1).address + ",currentAddress:" + getItem(position).address
                    + ",layoutMessageInfo:" + viewHolder.layoutMessageInfo.getVisibility());
        }

        //从第一个开始隐藏
        if (mMessages.size() > 1 && position + 1 <= mMessages.size() - 1) {
            String nextAddress = getItem(position + 1).address;
            if (getItem(position).address.equals(nextAddress)) {
                viewHolder.layoutButtons.setVisibility(View.GONE);
            } else {
                viewHolder.layoutButtons.setVisibility(View.VISIBLE);
            }
            LogUtil.d("smsAdapter", "nextAddress:" + getItem(position + 1).address + ",currentAddress:" + getItem(position).address
                    + ",layoutButtons:" + viewHolder.layoutButtons.getVisibility());
        }

        if (position == mMessages.size() - 1) {
            LogUtil.d("smsApapter", "last position:" + position);
            viewHolder.layoutButtons.setVisibility(View.VISIBLE);
        }

        if (position == 0) {
            viewHolder.layoutMessageInfo.setVisibility(View.VISIBLE);
        }


        viewHolder.avatarView.setTag(position);

        return convertView;
    }

    private void setBody(final Message message, final ViewHolder viewHolder) {
        viewHolder.tvContent.setAutoLinkMask(0);
        SpannableStringBuilder buf = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(message.body)) {
//            String body = EmojiRegistry.parseEmojis(message.body);
            buf.append(message.body);
        }

        if (!TextUtils.isEmpty(buf)) {
            viewHolder.tvContent.setText(buf);
            LinkifyUtils.setLinksColor(mContext.getResources().getColor(R.color.background));
            LinkifyUtils.addLinks(viewHolder.tvContent);
        }
        viewHolder.tvContent.setVisibility(TextUtils.isEmpty(buf) ? View.GONE : View.VISIBLE);
        viewHolder.tvContent.callOnClick();
    }

    public class ViewHolder {
        public TextView tvBlock;
        public AvatarView avatarView;
        private TextView tvName;
        private TextView tvNumber;
        private TextView tvDate;
        private TextView tvContent;
        private LinearLayout layoutdetail;
        private LinearLayout layoutDail;
        private LinearLayout layoutReply;
        private LinearLayout layoutButtons;
        private RelativeLayout layoutMessageInfo;

        private ViewHolder(View view) {
            avatarView = (AvatarView) view.findViewById(R.id.av_sms);
            tvName = (TextView) view.findViewById(R.id.tv_address);
            tvNumber = (TextView) view.findViewById(R.id.tv_number);
            tvDate = (TextView) view.findViewById(R.id.tv_date);
            tvContent = (TextView) view.findViewById(R.id.tv_sms_content);
            layoutdetail = (LinearLayout) view.findViewById(R.id.layout_detail);
            tvBlock = (TextView) view.findViewById(R.id.tv_block);
            layoutDail = (LinearLayout) view.findViewById(R.id.layout_dail);
            layoutReply = (LinearLayout) view.findViewById(R.id.layout_reply);
            layoutButtons = (LinearLayout) view.findViewById(R.id.layout_buttons);
            layoutMessageInfo = (RelativeLayout) view.findViewById(R.id.layout_sms_info);

            layoutDail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlurryAgent.logEvent("SmsComeActivity--smsAdaptrr--clickDial");
                    int position = -1;
                    if (avatarView.getTag() != null) {
                        position = (Integer) avatarView.getTag();
                    }
                    Message message = getItem(position);
                    if (message != null) {
                        DeviceUtil.callOut(mContext, message.address);
                    }
                }
            });

            layoutReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlurryAgent.logEvent("SmsComeActivity--smsAdaptrr--clickReply");
                    int position = -1;
                    if (avatarView.getTag() != null) {
                        position = (Integer) avatarView.getTag();
                    }
                    Message message = getItem(position);
                    if (message != null) {
                        DeviceUtil.toSystemSms(mContext, message.address);
                    }
                }
            });
        }
    }
}
