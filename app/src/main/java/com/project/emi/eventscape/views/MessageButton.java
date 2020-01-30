package com.project.emi.eventscape.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.project.emi.eventscape.R;
import com.project.emi.eventscape.enums.FollowState;
import com.project.emi.eventscape.util.LogUtil;


public class MessageButton extends AppCompatButton {
    public static final String TAG = MessageButton.class.getSimpleName();

    public static final int FOLLOW_STATE = 1;
    public static final int FOLLOW_BACK_STATE = 2;
    public static final int FOLLOWING_STATE = 3;
    public static final int INVISIBLE_STATE = -1;

    private int state;


    public MessageButton(Context context) {
        super(context);
        init();
    }

    public MessageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MessageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setState(FollowState followState) {
        switch (followState) {
            case I_FOLLOW_USER:
            case FOLLOW_EACH_OTHER:
                state = FOLLOWING_STATE;
                break;
            case USER_FOLLOW_ME:
                state = FOLLOW_BACK_STATE;
                break;
            case NO_ONE_FOLLOW:
                state = FOLLOW_STATE;
                break;
            case MY_PROFILE:
                state = INVISIBLE_STATE;

        }

        updateButtonState();
        LogUtil.logDebug(TAG, "new state code: " + state);
    }

    private void init() {
        state = INVISIBLE_STATE;
        updateButtonState();
    }

    public int getState() {
        return state;
    }

    public void updateButtonState() {
        setClickable(true);

        switch (state) {
            case FOLLOW_STATE: {
                setVisibility(VISIBLE);
                setText("Message");
                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.follow_button_dark_bg));
                setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                break;
            }


            case FOLLOWING_STATE: {
                setVisibility(VISIBLE);
                setText("Message");
                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.follow_button_light_bg));
                setTextColor(ContextCompat.getColor(getContext(), R.color.primary_dark_text));
                break;
            }

            case INVISIBLE_STATE: {
                setVisibility(INVISIBLE);
                setClickable(false);
                break;
            }
        }
    }
}
