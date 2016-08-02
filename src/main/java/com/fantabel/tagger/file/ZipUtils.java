package com.fantabel.tagger.file;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;

public class ZipUtils {
	public static String compressToCbz(File dir) {
		String newFilename = null;
		try {
			File taggedFolder = new File("." + File.separator + "tagged");

			if (!taggedFolder.exists()) {
				taggedFolder.mkdir();
			}

			String zipFile = dir.getName() + ".cbz";

			// create byte buffer
			byte[] buffer = new byte[1024];

			// create object of FileOutputStream
			newFilename = taggedFolder + File.separator + zipFile;
			FileOutputStream fout = new FileOutputStream(newFilename);

			// create object of ZipOutputStream from FileOutputStream
			ZipOutputStream zout = new ZipOutputStream(fout);

			// check to see if this directory exists
			if (!dir.isDirectory()) {
				System.out.println(dir.getName() + " is not a directory");
			} else {
				File[] files = dir.listFiles();

				for (int i = 0; i < files.length; i++) {

					// create object of FileInputStream for source file
					FileInputStream fin = new FileInputStream(files[i]);

					zout.putNextEntry(new ZipEntry(files[i].getName()));

					int length;

					while ((length = fin.read(buffer)) > 0) {
						zout.write(buffer, 0, length);
					}
					zout.closeEntry();

					fin.close();
				}
			}

			// close the ZipOutputStream
			zout.close();

		} catch (IOException ioe) {
			System.out.println("IOException :" + ioe);
		}
		return newFilename;

	}

	public static File createTempFolder(File f) {
		File tempDir = new File(FileUtils.removeExtension(f.getName()));
		if (tempDir.exists()) {
			System.out.println("temporary file already exists");
			FileUtils.deleteFolder(tempDir);
		}

		tempDir.mkdir();
		tempDir.deleteOnExit();

		return tempDir;
	}

	public static File extractCbrTemp(File f) {
		File tempDir = createTempFolder(f);

		Archive a = null;
		try {
			a = new Archive(new FileVolumeManager(f));
		} catch (RarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (a != null) {
			a.getMainHeader().print();
			FileHeader fh = a.nextFileHeader();
			while (fh != null) {
				try {
					if (FileUtils.filenameContainsExcludedPatterns(fh.getFileNameString())) {
						fh = a.nextFileHeader();
						continue;
					}
					if (!fh.isDirectory()) {
						System.out.println(fh.getFileNameString());
						File out = new File(tempDir.getName() + File.separator + fh.getFileNameString().trim());
						FileOutputStream os = new FileOutputStream(out);
						a.extractFile(fh, os);
						os.close();
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RarException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fh = a.nextFileHeader();
			}
		}

		return tempDir;

	}

	public static File extractCbzTemp(File f) {
		File tempDir = createTempFolder(f);

		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
			ZipEntry ze = zis.getNextEntry();
			byte[] buffer = new byte[1024];

			while (ze != null) {
				String fileName = ze.getName();
				if (FileUtils.filenameContainsExcludedPatterns(fileName)) {
					ze = zis.getNextEntry();
					continue;
				}
				System.out.println(fileName);
				if (fileName.contains(File.separator)) {
					fileName = fileName.substring(fileName.lastIndexOf(File.separator));
				}

				File newFile = new File(tempDir.getAbsolutePath() + File.separator + fileName);

				if (!ze.isDirectory()) {
					newFile.createNewFile();

					FileOutputStream fos = new FileOutputStream(newFile);

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();
				}
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return tempDir;
	}

	private static byte[] getBytesFromZipEntry(ZipInputStream zis) throws IOException {
		int data = 0;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while ((data = zis.read()) != -1) {
			output.write(data);
		}
		return output.toByteArray();
	}

	public static Image getLastImageFromCbz(File archive) {
		ZipInputStream zis;
		byte[] b = null;
		try {
			zis = new ZipInputStream(new FileInputStream(archive));

			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				b = getBytesFromZipEntry(zis);
				ze = zis.getNextEntry();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InputStream in = new ByteArrayInputStream(b);
		BufferedImage bImageFromConvert = null;
		try {
			bImageFromConvert = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bImageFromConvert;
	}
}
