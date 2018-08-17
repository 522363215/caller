package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

public class UsedPictureFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_used_picture,container,false);
        return view;
    }
}
