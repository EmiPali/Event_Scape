package com.project.emi.eventscape.domain.chat;


import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greentoad.turtlebody.mediapicker.MediaPicker;
import com.greentoad.turtlebody.mediapicker.core.MediaPickerConfig;
import com.project.emi.eventscape.R;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class ChatFragment extends Fragment{
    private static final String TAG = "ChatFragment";

    private static final int REQUEST_CODE_CHOOSE = 23;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        return view;
    }




}
