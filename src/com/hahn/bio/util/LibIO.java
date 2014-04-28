package com.hahn.bio.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LibIO {

	public static void extract() {
		URL resource = ClassLoader.getSystemClassLoader().getResource("lib");
		
		if (resource != null) {
			try {
				File dir = new File(resource.toURI());
				
				if (dir.isDirectory()) {
					String os = System.getProperty("os.name");
					
					for (File f1: dir.listFiles()) {
						if (f1.isDirectory() && os.matches(String.format("(?i)%s.*", f1.getName()))) {
							for (File f2: f1.listFiles()) {
								extract("lib/" + f1.getName(), f2);
							}
						} else {
							extract("lib", f1);
						}
					}
				} else {
					System.err.println("Corrupt 'lib' directory");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Failed to find 'lib' directory");
		}
	}
	
	private static void extract(String path, File f) {
		InputStream is = null;
		
		File output = new File(f.getName());
		FileOutputStream fos = null;
		try {
			is = ClassLoader.getSystemClassLoader().getResourceAsStream(path + "/" + f.getName());
			
			int readBytes;
			byte[] buffer = new byte[4096];
			fos = new FileOutputStream(output);
			
			while ((readBytes = is.read(buffer)) > 0) {
	            fos.write(buffer, 0, readBytes);
	        }
		} catch (Exception e) {
			System.err.println("Failed to extract lib file '" + path + "/" + f.getName() + "'");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
