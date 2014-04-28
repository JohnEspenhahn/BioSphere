package com.hahn.bio.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

public class ConfigIO {
	public static <T> void read(T config) {		
		Scanner s = null;
		
		try {
			Class<?> clazz = config.getClass();
			File file = new File(config.getClass().getSimpleName().toLowerCase() + ".properties");
			if (file.exists()) {
				s = new Scanner(file);
				
				while (s.hasNextLine()) {
					String line = s.nextLine().trim();
					if (!line.startsWith("#")) {
						String[] def = Util.trim(line.split("="));
						
						if (def.length == 2) {
							try {
								Field f = clazz.getField(def[0].toUpperCase());
								Class<?> type = f.getType();
								if (type == Integer.class || type == int.class) {
									f.setInt(null, Integer.valueOf(def[1]));
								} else if (type == Float.class || type == float.class) {
									f.setFloat(null, Float.valueOf(def[1]));
								} else if (type == Double.class || type == double.class) {
									f.setDouble(null, Double.valueOf(def[1]));
								} else if (type == Boolean.class || type == boolean.class) {
									f.setBoolean(null, Boolean.valueOf(def[1]));
								} else if (type == String.class) {
									f.set(null, def[1]);
								} else {
									System.err.println("Unknown type " + type);
								}
								
								System.out.printf("Set %s = %s\n", f.getName(), f.get(null));
							} catch (NumberFormatException e) {
								System.err.println("Invalid number format '" + def[1] + "' for '" + def[0].toUpperCase() + "'");
							} catch (NoSuchFieldException e) {
								System.err.println("Unknown property '" + def[0].toUpperCase() + "'");
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							System.err.println("Invalid definition '" + line + "'");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (s != null) {
				s.close();
			}
		}
		
		write(config);
	}
	
	public static <T> void write(T config) {
		FileOutputStream fos = null;
		
		try {
			Class<?> clazz = config.getClass();
			File file = new File(config.getClass().getSimpleName().toLowerCase() + ".properties");
			if (!file.exists()) {
				file.createNewFile();
			}
			
			fos = new FileOutputStream(file);
			fos.write(String.format("# Created %s\r\n", new Timestamp(new Date().getTime()).toString()).getBytes());
			
			for (Field f: clazz.getFields()) {
				if (Modifier.isStatic(f.getModifiers())) {
					fos.write(String.format("%s=%s\r\n", f.getName(), f.get(null).toString()).getBytes());
				}
			}
			
			fos.close();
		}  catch (Exception e) {
			e.printStackTrace();
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
