package com.genedev.ink_socialmedia.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.genedev.ink_socialmedia.R;

import android.app.Fragment;

public class FragmentPages extends Fragment {


        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";
        private String mParam1;
        private String mParam2;

        public static FragmentPages newInstance(String param1, String param2) {
            FragmentPages fragment1 = new FragmentPages();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment1.setArguments(args);
            return fragment1;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.home_fragment_pages, container, false);

            return rootView;
        }
    }