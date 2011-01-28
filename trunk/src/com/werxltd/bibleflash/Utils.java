package com.werxltd.bibleflash;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.res.AssetManager;
import android.util.Log;

abstract public class Utils {
	private static final String TAG = "Utils";
	
	static public void startUnzipAsset(final String moduleAsset,
			final String dstDirName, final String fileInZip, AssetManager am) {
		try {
			Utils.unzipAsset(moduleAsset, dstDirName, fileInZip, am);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static private void unzipAsset(final String moduleAsset, final String dstDirName, final String fileInZip, AssetManager am)
			throws IOException {
		InputStream in = new BufferedInputStream(am.open(moduleAsset));

		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry e;

		// loading_pb.setMax(zin.available());

		while ((e = zin.getNextEntry()) != null) {
			if(!e.getName().matches(fileInZip)) continue;
			
			// loading_text.setText("Unzipping " + e.getName() + "..");
			if (e.isDirectory()) {
				if (Preferences.D)
					Log.d(TAG, "extracting directory: " + e.getName());
				(new File(e.getName())).mkdir();
				continue;
			}

			Utils.unzip(zin, dstDirName + e.getName());
			// loading_pb.incrementSecondaryProgressBy(1);
		}
		zin.close();

	}

	static private void unzip(final ZipInputStream zin, final String fileName) throws IOException {
		if (Preferences.D)
			Log.d(TAG, "unzipping: " + fileName);

		File lockFile = new File(fileName);
		File lockParentDir = new File(lockFile.getParent());

		if (!lockParentDir.exists()) {
			if (Preferences.D)
				Log.d(TAG, "mkdirs(): " + lockFile.getParent());

			lockParentDir.mkdirs();
		}

		FileOutputStream out = new FileOutputStream(fileName);
		byte[] b = new byte[512];
		int len = 0;
		while ((len = zin.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.close();
	}
}
