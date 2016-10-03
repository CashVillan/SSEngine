package com.swingsword.ssengine.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileUtils {

	public static void pasteFile(File clipboard, File target, ArrayList<String> ignore) {
		try {
			if (!ignore.contains(clipboard.getName())) {
				if (clipboard.isDirectory()) {
					if (!target.exists())
						target.mkdirs();
					String files[] = clipboard.list();
					for (String file : files) {
						File srcFile = new File(clipboard, file);
						File destFile = new File(target, file);
						pasteFile(srcFile, destFile, ignore);
					}
				} else {
					InputStream in = new FileInputStream(clipboard);
					OutputStream out = new FileOutputStream(target);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = in.read(buffer)) > 0)
						out.write(buffer, 0, length);
					in.close();
					out.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
