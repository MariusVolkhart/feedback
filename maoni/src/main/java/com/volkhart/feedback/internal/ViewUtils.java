package com.volkhart.feedback.internal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utilities for manipulating views
 */
public final class ViewUtils {

    public static final int COMPRESSION_QUALITY = 100;
    public static final int DEFAULT_BITMAP_WIDTH = 640;
    public static final int DEFAULT_BITMAP_HEIGHT = 480;

    private ViewUtils() {
    }

    /**
     * Convert the specified view to a drawable, if possible
     *
     * @param view the view to convert
     * @return the bitmap or {@code null} if the {@code view} is null
     */
    @Nullable
    private static Bitmap toBitmap(@Nullable final View view) {
        if (view == null) {
            return null;
        }
        final int width = view.getWidth();
        final int height = view.getHeight();
        final Bitmap bitmapToExport = Bitmap
                .createBitmap(width > 0 ? width : DEFAULT_BITMAP_WIDTH,
                        height > 0 ? height : DEFAULT_BITMAP_HEIGHT,
                        Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmapToExport);
        view.draw(canvas);
        return bitmapToExport;
    }

    /**
     * Export the given view to the specified {@code File}
     *
     * @param view    the view to export
     * @param file    the file in which to save the view
     */
    public static void exportViewToFile(@NonNull final View view, @NonNull final File file) {
        final Bitmap bitmap = toBitmap(view);
        if (bitmap == null) {
            return;
        }
        exportBitmapToFile(bitmap, file);
    }

    /**
     * Export the given bitmap to the specified {@code File}
     *
     * @param bitmap  the bitmap to export
     * @param file    the file in which to save the view
     */
    private static void exportBitmapToFile(@NonNull final Bitmap bitmap,
                                           @NonNull final File file) {
        OutputStream outputStream = null;
        try {
            // if making the directories fails we'll already handle it by not sending a screenshot
            file.getParentFile().mkdirs();
            outputStream = new BufferedOutputStream(new FileOutputStream(file, false));
            bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, outputStream);
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                //No Worries
            }
        }
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Decode a given image file with the specified dimensions
     *
     * @param filePath  the file resource path
     * @param reqWidth  the required width
     * @param reqHeight the required height
     * @return the bitmap
     */
    static Bitmap decodeSampledBitmapFromFilePath(String filePath,
                                                  int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);

    }
}
