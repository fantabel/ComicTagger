package com.fantabel.tagger.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

public class FileUtils {
	public static String getExtension(String s) {
		int index = s.lastIndexOf(".");
		if (index == -1)
			return "";

		return s.substring(index);
	}

	public static String removeExtension(String s) {
		int index = s.lastIndexOf(".");
		if (index == -1)
			return s;

		return s.substring(0, index);
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	public static File[] getCbxFromFolder(File temp) {
		// TODO Auto-generated method stub
		return temp.listFiles();
	}

	public static boolean filenameContainsExcludedPatterns(final String fileName) {
		String[] excluded = { "MACOSX", ".nfo", ".DS_Store", ".db", ".xml", ".SFV", ".txt", "#0001", ".par", ".pdf",
				".ini" };
		List<String> list = Arrays.asList(excluded);
		return list.stream().anyMatch(s -> test(fileName, s));
		// return fileName.contains("MACOSX") || fileName.contains(".nfo") ||
		// fileName.contains(".DS_Store");
	}

	public static boolean test(String filename, String pattern) {
		return filename.toLowerCase().contains(pattern.toLowerCase());
	}

	public static int getRegularWidth(File tempDir) {
		File[] list = tempDir.listFiles();
		int[] widths = new int[list.length];
		int i = 0;

		for (File f : list) {
			widths[i++] = getImageWidth(f);
		}

		HashMap<Integer, Integer> h = new HashMap<Integer, Integer>();
		for (int width : widths) {
			if (h.containsKey(width)) {
				h.put(width, h.get(width) + 1);
			} else {
				h.put(width, 1);
			}

		}

		int mostOccurence = h.entrySet().stream()
				.max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();

		return mostOccurence;
	}

	public static int getImageWidth(File f) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(f);
		} catch (IOException e) {
			return 0;
		}

		return img.getWidth();
	}

	public static boolean imageIsDoubleTheSizeOf(File f, int regularWidth) {
		int epsilon = 100;

		return Math.abs(getImageWidth(f) - (regularWidth * 2)) < epsilon;
	}

}
