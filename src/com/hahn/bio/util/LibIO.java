package com.hahn.bio.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LibIO {
	private static final Pattern FILE = Pattern.compile("lib/(.+/)?(.+)", Pattern.CASE_INSENSITIVE);
	
	public static void extract() {
		String path = LibIO.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		
		if (path != null) {
			String os = System.getProperty("os.name");
			
			ZipFile zip = null;			
			InputStream is = null;
			try {				
				zip = new ZipFile(path.toString());
				Enumeration<? extends ZipEntry> entries = zip.entries();
				while (entries.hasMoreElements()) {
					ZipEntry e = entries.nextElement();
					
					if (!e.isDirectory()) {
						Matcher m = FILE.matcher(e.getName());
						if (m.matches()) {
							String g1 = m.group(1);
							if (g1 == null || os.matches(String.format("(?i)%s.*", g1.substring(0, g1.length() - 1)))) {
								is = zip.getInputStream(e);

								extract(e.getName(), is);
								
								is.close();
							}
						}
						
					}
				}
			} catch (FileNotFoundException e) {
				System.err.println("Failed to find jar");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (zip != null) {
					try {
						zip.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			System.err.println("Failed to find jar");
		}
	}
	
	private static void extract(String file, InputStream is) {
		String[] fileParts = file.split("/");
		File output = new File(fileParts[fileParts.length - 1]);
		FileOutputStream fos = null;
		try {			
			int readBytes;
			byte[] buffer = new byte[4096];
			fos = new FileOutputStream(output);
			
			while ((readBytes = is.read(buffer)) > 0) {
	            fos.write(buffer, 0, readBytes);
	        }
		} catch (Exception e) {
			System.err.println("Failed to extract lib file '" + file + "'");
		} finally {			
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
