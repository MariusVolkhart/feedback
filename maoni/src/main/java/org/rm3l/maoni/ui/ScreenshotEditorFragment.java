package org.rm3l.maoni.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import org.rm3l.maoni.R;
import org.rm3l.maoni.utils.ViewUtils;

import java.io.File;

import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

public final class ScreenshotEditorFragment extends Fragment {

    public static final String TAG = ScreenshotEditorFragment.class.getSimpleName();
    static final String ARG_SCREENSHOT_URI = "screenshot_uri";

    public static ScreenshotEditorFragment newInstance(Uri screenshot) {
        ScreenshotEditorFragment fragment = new ScreenshotEditorFragment();
        Bundle args = new Bundle(1);
        args.putParcelable(ARG_SCREENSHOT_URI, screenshot);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final int highlightColor = ContextCompat.getColor(getActivity(), R.color.maoni_highlight_transparent_semi);
        final int blackoutColor = ContextCompat.getColor(getActivity(), R.color.maoni_black);
        final Uri fileUri = getArguments().getParcelable(ARG_SCREENSHOT_URI);
        final View view = inflater.inflate(R.layout.maoni_screenshot_preview, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FIXME
            }
        });
        toolbar.inflateMenu(R.menu.menu_screenshot_editor);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_save) {
                    ViewUtils.exportViewToFile(getActivity(), view.findViewById(R.id.maoni_screenshot_preview_image_view_updated), new File(fileUri.getPath()));
                    // FIXME
//                    initScreenCaptureView(intent);
//                    dismiss();
                    return true;
                } else {
                    return false;
                }
            }
        });

        ImageView imageView = (ImageView) view.findViewById(R.id.maoni_screenshot_preview_image);
        imageView.setImageURI(fileUri);

        final DrawableView drawableView = (DrawableView) view.findViewById(R.id.maoni_screenshot_preview_image_drawable_view);
        final DrawableViewConfig config = new DrawableViewConfig();
        config.setStrokeWidth(57.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setStrokeColor(highlightColor);
        imageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                config.setCanvasWidth(v.getWidth());
                config.setCanvasHeight(v.getHeight());
                drawableView.setConfig(config);
            }
        });

        final CompoundButton highlightColorButton = (CompoundButton) view.findViewById(R.id.maoni_screenshot_preview_pick_highlight_color);
        final CompoundButton blackoutColorButton = (CompoundButton) view.findViewById(R.id.maoni_screenshot_preview_pick_blackout_color);
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (buttonView == highlightColorButton) {
                        config.setStrokeColor(highlightColor);
                        blackoutColorButton.setChecked(false);
                    } else {
                        config.setStrokeColor(blackoutColor);
                        highlightColorButton.setChecked(false);
                    }
                }
            }
        };
        highlightColorButton.setOnCheckedChangeListener(checkedChangeListener);
        blackoutColorButton.setOnCheckedChangeListener(checkedChangeListener);
        view.findViewById(R.id.maoni_screenshot_preview_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawableView.undo();
            }
        });

        return view;
    }
}
