package com.alan.unityvrservice.utils;

import android.R;
import android.content.Context;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppItem {
	public ResolveInfo resolveInfo;
	public CharSequence label;
	public Bitmap appIcon;
	public String packageName;
	public String className;
	public Bundle extras;
	// public int position;
	public int container;
	public boolean visible = true;

	private static final float ICON_SIZE_DEFINED_IN_APP_DP = 48;

	public AppItem(String componentName) {
		String name = componentName.substring("ComponentInfo{".length(),
				componentName.length() - 1);
		int sep = name.indexOf('/');
		if (sep < 0 || (sep + 1) >= name.length()) {
			return;
		}
		packageName = name.substring(0, sep);
		className = name.substring(sep + 1);
	}

	public AppItem(PackageManager pm, ResolveInfo info, IconResizer resizer,
			Context context) {
		resolveInfo = info;
		label = resolveInfo.loadLabel(pm);
		Resources resources = null;
		ComponentInfo ci = resolveInfo.activityInfo;
		if (ci == null) {
			ci = resolveInfo.serviceInfo;
		}

		if (label == null && ci != null) {
			label = resolveInfo.activityInfo.name;
		}

		packageName = ci.applicationInfo.packageName;
		className = ci.name;

		if (resizer != null) {
			try {
				resources = pm.getResourcesForApplication(packageName);
			} catch (NameNotFoundException e) {   
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (resources != null) {
				appIcon = resizer.createIconThumbnail(resources
						.getDrawableForDensity(resolveInfo.getIconResource(),
								getLauncherIconDensity(context.getResources()
										.getDimension(R.dimen.app_icon_size))));
				//saveThumbnail(appIcon, VRApplication.VRAppIconPath, label.toString());
			}
		}
	}
	
	private void saveThumbnail(Bitmap tempBitMap, String savePath, String picName) {
		File dirFile = new File(savePath);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		String name[] = picName.split("\\.");
		String path = savePath ;
		FileOutputStream fos = null;
		try {
			File file = new File(path);
			if (file.exists()) {
				return;
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			// Bitmap.CompressFormat.PNG
			tempBitMap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static int getLauncherIconDensity(float f) {
		// Densities typically defined by an app.
		int[] densityBuckets = new int[] { DisplayMetrics.DENSITY_LOW,
				DisplayMetrics.DENSITY_MEDIUM, DisplayMetrics.DENSITY_TV,
				DisplayMetrics.DENSITY_HIGH, DisplayMetrics.DENSITY_XHIGH,
				DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_XXXHIGH };

		int density = DisplayMetrics.DENSITY_XXXHIGH;
		for (int i = densityBuckets.length - 1; i >= 0; i--) {
			float expectedSize = ICON_SIZE_DEFINED_IN_APP_DP
					* densityBuckets[i] / DisplayMetrics.DENSITY_DEFAULT;
			if (expectedSize >= f) {
				density = densityBuckets[i];
			}
		}
		return density;
	}

	public String toString() {
		return "FloatAppItem{" + packageName + "/" + className + ":vis = "
				+ visible + "}";
	}

	@Override
	public boolean equals(Object o) {
		AppItem other = (AppItem) o;
		if (other == null) {
			return false;
		}
		if (other.packageName == null && other.className == null) {
			return packageName == null && className == null;
		}
		return other.packageName.equals(packageName)
				&& other.className.equals(className);
	}
}
