package com.alan.unityvrservice.utils;

//import com.hoperun.vrservices.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.Log;

/**
 * Utility class to resize icons to match default icon size.
 */
public class IconResizer {
	private Context mContext;
	private String TAG = "IconResizer";

	//default value
	private int mIconWidth = 50;
	private int mIconHeight = 50;

	private final Rect mOldBounds = new Rect();
	private Canvas mCanvas = new Canvas();

	public IconResizer(Context context) {
		mContext = context;
		mCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
				Paint.FILTER_BITMAP_FLAG));
	}

	public void setIconSize(int iconSize) {
		mIconHeight = mIconWidth = iconSize;
	}

	/**
	 * Returns a Drawable representing the thumbnail of the specified Drawable.
	 * The size of the thumbnail is defined by the dimension
	 * android.R.dimen.launcher_application_icon_size. This method is not
	 * thread-safe and should be invoked on the UI thread only.
	 * 
	 * @param icon
	 *            The icon to get a thumbnail of.
	 * @return A thumbnail for the specified icon or the icon itself if the
	 *         thumbnail could not be created.
	 */
	public Bitmap createIconThumbnail(Drawable icon) {
		int width = mIconWidth;
		int height = mIconHeight;

		final int iconWidth = icon.getIntrinsicWidth();
		final int iconHeight = icon.getIntrinsicHeight();
		Bitmap thumb = null;

		if (icon instanceof PaintDrawable) {
			PaintDrawable painter = (PaintDrawable) icon;
			painter.setIntrinsicWidth(width);
			painter.setIntrinsicHeight(height);
		}
		Log.d(TAG, "createIconThumbnail: iconWidth = "
				+ iconWidth + ", iconHeight = " + iconHeight
				+ ", mIconWidth = " + mIconWidth + ", mIconHeight = "
				+ mIconHeight + ", icon = " + icon + ",icon.getOpacity() = "
				+ icon.getOpacity());

		if (width > 0 && height > 0) {
			if (width < iconWidth || height < iconHeight) {
				final float ratio = (float) iconWidth / iconHeight;

				if (iconWidth > iconHeight) {
					height = (int) (width / ratio);
				} else if (iconHeight > iconWidth) {
					width = (int) (height * ratio);
				}

				final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565;
				thumb = Bitmap.createBitmap(mIconWidth,
						mIconHeight, c);
				final Canvas canvas = mCanvas;
				canvas.setBitmap(thumb);
				// Copy the old bounds to restore them later
				// If we were to do oldBounds = icon.getBounds(),
				// the call to setBounds() that follows would
				// change the same instance and we would lose the
				// old bounds
				mOldBounds.set(icon.getBounds());
				final int x = (mIconWidth - width) / 2;
				final int y = (mIconHeight - height) / 2;
				icon.setBounds(x, y, x + width, y + height);
				icon.draw(canvas);
				icon.setBounds(mOldBounds);
				//icon = new BitmapDrawable(mContext.getResources(), thumb);
				canvas.setBitmap(null);
			} else if (iconWidth < width && iconHeight < height) {
				final Bitmap.Config c = Bitmap.Config.ARGB_8888;
				thumb = Bitmap.createBitmap(mIconWidth,
						mIconHeight, c);
				final Canvas canvas = mCanvas;
				canvas.setBitmap(thumb);
				mOldBounds.set(icon.getBounds());
				final int x = (width - iconWidth) / 2;
				final int y = (height - iconHeight) / 2;
				icon.setBounds(0, 0, width, height);
				icon.draw(canvas);
				icon.setBounds(mOldBounds);
				//icon = new BitmapDrawable(mContext.getResources(), thumb);
				canvas.setBitmap(null);
			}
		}
		return thumb;
	}
}