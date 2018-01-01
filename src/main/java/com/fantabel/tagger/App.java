package com.fantabel.tagger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import com.fantabel.tagger.controller.Controller;
import com.fantabel.tagger.model.exception.TaggerException;
import com.fantabel.tagger.model.util.FileUtils;
import com.fantabel.tagger.model.util.ZipUtils;

/**
 * Hello world!
 *
 */
public class App {

	public static void go(String[] args) {
		// TODO Filter arguments. Treat whole folder or single files
		// TODO do not extract nfo files
		System.out.println("Start of program");
		Arguments arg = new Arguments(args);
		File[] files = null;
		BufferedImage img = null;
		files = arg.getFiles();

		for (File f : files) {
			if (StringUtils.equals(FileUtils.getExtension(f.getName()), ".cbz")
					|| StringUtils.equals(FileUtils.getExtension(f.getName()), ".cbr")) {
				System.out.println("Processing : " + f.getName());
				File tempDir = null;
				if (StringUtils.equals(FileUtils.getExtension(f.getName()), ".cbz")) {
					tempDir = ZipUtils.extractCbzTemp(f);
				} else {
					tempDir = ZipUtils.extractCbrTemp(f);
				}

				File renamedDir = new File(
						tempDir.getName().replace("v1x", "v01x").replace("..", "").replace(" ", "."));
				tempDir.renameTo(renamedDir);
				tempDir = renamedDir;

				String filename = tempDir.getName();
				Pattern patternVolume = Pattern.compile("v\\p{Digit}{1,2}");
				Pattern patternIssue = Pattern.compile("x\\p{Digit}{1,3}(-?\\p{Digit}{1,3})");

				Matcher matcherVolume = patternVolume.matcher(filename);
				Matcher matcherIssue = patternIssue.matcher(filename);
				String pagename = null;

				String volume = "";
				String issue = "";

				if (matcherVolume.find()) {
					volume = matcherVolume.group();
				}
				if (matcherIssue.find()) {
					issue = matcherIssue.group();
				}

				System.out.println(issue);

				int index = filename.indexOf(volume + issue);
				if (index < 0 && filename.indexOf(volume + "." + issue) >= 0) {
					throw new RuntimeException("Volume + issue malformed. Is there a dot?");

				} else if (filename.indexOf(volume) > filename.indexOf(issue)) {
					volume = "";
					index = filename.indexOf(volume + issue);
				}

				String pre = filename.substring(0, index);
				String post = filename.substring(pre.length() + volume.length() + issue.length(), filename.length());
				System.out.println(pre + " " + post + " " + volume + " " + issue);
				if (!volume.equals("") || !issue.equals("")) {
					pagename = pre + volume + issue + "$1$2" + post;
				} else {
					pagename = filename + "$1$2";
				}

				int i = 0;
				boolean cover = true;
				int regularWidth = FileUtils.getRegularWidth(tempDir);
				System.out.println("RegularWidth : " + regularWidth);
				File[] orderedFiles = tempDir.listFiles();
				Arrays.sort(orderedFiles, Comparator.comparing(File::getName));
				for (File page : tempDir.listFiles()) {
					System.out.println(page.getName());
					if (cover) {
						try {
							img = ImageIO.read(page);
							try {
								if (!Controller.showCoverDialog(filename, img)) {
									cover = false;
									i = 1;
								}
							} catch (TaggerException e) {
								page.delete();
								continue;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					String format = tempDir.listFiles().length > 99 ? "%03d" : "%02d";
					String pageNumber = String.format(format, i++);
					String pageNumber2 = "";
					String currentPageName = pagename;
					if (FileUtils.imageIsDoubleTheSizeOf(page, regularWidth)) {
						currentPageName = pagename.replace("$2", "$2-$3");
						pageNumber2 = String.format(format, i++);
					}

					String newFilename = currentPageName.replace("$1", cover ? "c" : "p").replace("$2", pageNumber)
							.replace("$3", pageNumber2) + FileUtils.getExtension(page.getName().replace("JPG", "jpg"));
					System.out.println(newFilename);
					page.renameTo(new File(tempDir.getAbsolutePath() + File.separator + newFilename));

				}

				// Check for team tag
				try {
					File[] content = tempDir.listFiles();

					File lastFile = content[content.length - 1];
					img = ImageIO.read(lastFile);
					// Image img = ZipUtils.getLastImageFromCbz(new
					// File(newFile));
					while (!Controller.showDeleteDialog(f.getName(), img)) {
						lastFile.delete();
						content = tempDir.listFiles();
						lastFile = content[content.length - 1];
						img = ImageIO.read(lastFile);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
				String newFile = ZipUtils.compressToCbz(tempDir);
				FileUtils.deleteFolder(tempDir);

				File done = new File(".//toTrash");
				if (!done.exists()) {
					done.mkdir();
				}

				f.renameTo(new File("toTrash//" + f.getName()));

			}
		}
	}

	private static class Arguments {
		CommandLine commandLine;
		Options options;
		Option oConvert;
		Option oFile;
		Option oTrim;
		Option oHelp;

		File[] files;

		Arguments(String[] args) {
			oConvert = Option.builder("c").longOpt("convert")
					.desc("This option converts the content of a cbz file with the name of the archive").build();
			oFile = Option.builder("f").longOpt("file").hasArg().hasArgs().desc("The file or directory to process")
					.build();
			oTrim = Option.builder("t").longOpt("trim").desc("Ask to trim the last page of a comic (group tag)")
					.build();
			oHelp = Option.builder("h").longOpt("help").desc("Print this help message").build();
			oHelp = Option.builder("C").longOpt("covers").desc("Check if there are multiple covers").build();

			options = new Options();

			options.addOption(oConvert);
			options.addOption(oFile);
			options.addOption(oTrim);
			options.addOption(oHelp);

			CommandLineParser parser = new DefaultParser();

			try {
				commandLine = parser.parse(options, args);

				if (commandLine.hasOption('h')) {
					printHelpMessage();
					System.exit(0);
				}

				if (commandLine.hasOption('c')) {

				}

				String[] params = null;
				if (commandLine.hasOption('f')) {
					params = commandLine.getOptionValues('f');
				} else {
					params = commandLine.getArgs();

				}

				if (params.length == 0) {
					System.out.println("No file specified, using current dir");
					params = new String[] { "." };

				}

				ArrayList<File> listTemp = new ArrayList<>();
				for (String s : params) {
					File temp = new File(s);
					if (temp.exists()) {
						if (temp.isDirectory()) {
							File[] fTemp = FileUtils.getCbxFromFolder(temp);
							for (File f : fTemp) {
								listTemp.add(f);
							}
						} else {
							listTemp.add(temp);
						}

					}

				}
				files = listTemp.toArray(new File[0]);

			} catch (ParseException exception) {
				System.out.print("Parse error: ");
				System.out.println(exception.getMessage());
				System.exit(-1);
			}
		}

		public boolean shouldConvert() {
			return commandLine.hasOption('c');
		}

		public boolean shouldPrintHelp() {
			return commandLine.hasOption('h');
		}

		public boolean shouldTrimLastPage() {
			return commandLine.hasOption('t');
		}

		public boolean shouldCheckMultipleCovers() {
			return commandLine.hasOption('C');
		}

		public File[] getFiles() {
			return files;
		}

		public void printHelpMessage() {
			String header = "Do something useful with an input file\n\n";
			String footer = "\nPlease report issues at http://example.com/issues";

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("myapp", header, options, footer, true);
		}
	}
}
