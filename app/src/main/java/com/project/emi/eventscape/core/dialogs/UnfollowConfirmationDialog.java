
package com.project.emi.eventscape.core.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.project.emi.eventscape.R;
import com.project.emi.eventscape.models.User;
import com.project.emi.eventscape.util.GlideApp;
import com.project.emi.eventscape.util.ImageUtil;


public class UnfollowConfirmationDialog extends DialogFragment {
    public static final String TAG = UnfollowConfirmationDialog.class.getSimpleName();
    public static final String PROFILE = "EditCommentDialog.PROFILE";

    private Callback callback;
    private User profile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getActivity() instanceof Callback) {
            callback = (Callback) getActivity();
        } else {
            throw new RuntimeException(getActivity().getTitle() + " should implements Callback");
        }

        profile = (User) getArguments().get(PROFILE);

        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_confirmation_unfollow, null);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView confirmationMessageTextView = view.findViewById(R.id.confirmationMessageTextView);

        confirmationMessageTextView.setText(getString(R.string.unfollow_user_message, profile.getUsername()));

        ImageUtil.loadImage(GlideApp.with(this), profile.getPhotoUrl(), imageView);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setNegativeButton(R.string.button_title_cancel, null)
                .setPositiveButton(R.string.button_title_unfollow, (dialog, which) -> {
                    callback.onUnfollowButtonClicked();
                    dialog.cancel();
                });

        return builder.create();
    }

    public interface Callback {
        void onUnfollowButtonClicked();
    }
}
