package com.project.emi.eventscape.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.emi.eventscape.R;


public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    public CameraFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup  container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera,container,false);
        return  view;
    }

}
