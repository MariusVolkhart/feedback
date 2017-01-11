package com.volkhart.feedback.ui;

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
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.volkhart.feedback.utils.ViewUtils;

import org.rm3l.maoni.R;

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
        final int highlightColor = ContextCompat.getColor(getActivity(), R.color.highlight_transparent_semi);
        final int blackoutColor = ContextCompat.getColor(getActivity(), R.color.black);
        final Uri fileUri = getArguments().getParcelable(ARG_SCREENSHOT_URI);
        final View view = inflater.inflate(R.layout.maoni_screenshot_preview, container, false);
        final Listener presenter = (Listener) getActivity();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDoneWithScreenshotEditing();
            }
        });
        toolbar.inflateMenu(R.menu.menu_screenshot_editor);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_save) {
                    ViewUtils.exportViewToFile(getActivity(), view.findViewById(R.id.maoni_screenshot_preview_image_view_updated), new File(fileUri.getPath()));
                    presenter.onDoneWithScreenshotEditing();
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
        config.setMaxZoom(1.0f);
        config.setStrokeColor(highlightColor);
        imageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                config.setCanvasWidth(v.getWidth());
                config.setCanvasHeight(v.getHeight());
                drawableView.setConfig(config);
            }
        });

        RadioGroup colorChooser = (RadioGroup) view.findViewById(R.id.color_chooser);
        colorChooser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maoni_screenshot_preview_pick_highlight_color) {
                    config.setStrokeColor(highlightColor);
                } else {
                    config.setStrokeColor(blackoutColor);
                }
            }
        });
        view.findViewById(R.id.maoni_screenshot_preview_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawableView.undo();
            }
        });

        return view;
    }

    public interface Listener {
        void onDoneWithScreenshotEditing();
    }
}
