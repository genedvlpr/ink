package com.genedev.ink_socialmedia.TimeFrame.TimeFrameTabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.genedev.ink_socialmedia.R;

import androidx.fragment.app.Fragment;

public class TabVideos extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.time_fragment_videos, container, false);
    }
}