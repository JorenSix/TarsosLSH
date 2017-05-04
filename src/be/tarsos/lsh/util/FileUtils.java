/*
*      _______                       _        ____ _     _
*     |__   __|                     | |     / ____| |   | |
*        | | __ _ _ __ ___  ___  ___| |    | (___ | |___| |
*        | |/ _` | '__/ __|/ _ \/ __| |     \___ \|  ___  |
*        | | (_| | |  \__ \ (_) \__ \ |____ ____) | |   | |
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|   |_|
*                                                         
* -----------------------------------------------------------
*
*  TarsosLSH is developed by Joren Six.
*  
* -----------------------------------------------------------
*
*  Info    : http://0110.be/tag/TarsosLSH
*  Github  : https://github.com/JorenSix/TarsosLSH
*  Releases: http://0110.be/releases/TarsosLSH/
* 
*/

package be.tarsos.lsh.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * An utility class for file interaction.
 * 
 * @author Joren Six
 */
public final class FileUtils {
	static final Logger LOG = Logger.getLogger(FileUtils.class.getName());

	public static String temporaryDirectory() {
		final String tempDir = System.getProperty("java.io.tmpdir");
		if (tempDir.contains(" ")) {
			LOG.warning("Temporary directory (" + tempDir + ") contains whitespace");
		}
		return tempDir;
	}

	// Disable the default constructor.
	private FileUtils() {
	}

	/**
	 * Joins path elements using the systems path separator. e.g. "/tmp" and
	 * "be.hogent.tarsos.lsh.test.wav" combined together should yield /tmp/be.hogent.tarsos.lsh.test.wav on UNIX.
	 * 
	 * @param path
	 *            The path parts part.
	 * @return Each element from path joined by the systems path separator.
	 */
	public static String combine(final String... path) {
		File file = new File(path[0]);
		for (int i = 1; i < path.length; i++) {
			file = new File(file, path[i]);
		}
		return file.getPath();
	}
	
	
	/**
	 * @return The path where the program is executed.
	 */
	public static String runtimeDirectory() {
		String runtimePath = "";
		try {
			runtimePath = new File(".").getCanonicalPath();
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "Could not find runtime path, strange.", e);
		}
		return runtimePath;
	}

	/**
	 * Writes a file to disk. Uses the string contents as content. Failures are
	 * logged.
	 * 
	 * @param contents
	 *            The contents of the file.
	 * @param name
	 *            The name of the file to create.
	 */
	public static void writeFile(final String contents, final String name) {
		writeFile(contents, name, false);
	}

	private static void writeFile(final String contents, final String name, final boolean append) {
		BufferedWriter outputStream = null;
		PrintWriter output = null;
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(name, append);
			outputStream = new BufferedWriter(fileWriter);
			output = new PrintWriter(outputStream);
			output.print(contents);
			outputStream.flush();
			output.close();
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "Could not write file " + name, e);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (final IOException e) {
				LOG.log(Level.SEVERE, "Failed to close file: " + name, e);
			}
		}
	}

	/**
	 * Appends a string to a file on disk. Fails silently.
	 * 
	 * @param contents
	 *            The contents of the file.
	 * @param name
	 *            The name of the file to create.
	 */
	public static void appendFile(final String contents, final String name) {
		writeFile(contents, name, true);
	}

	/**
	 * Reads the contents of a file.
	 * 
	 * @param name
	 *            the name of the file to read
	 * @return the contents of the file if successful, an empty string
	 *         otherwise.
	 */
	public static String readFile(final String name) {
		FileReader fileReader = null;
		final StringBuilder contents = new StringBuilder();
		try {
			final File file = new File(name);
			if (!file.exists()) {
				throw new IllegalArgumentException("File " + name + " does not exist");
			}
			fileReader = new FileReader(file);
			final BufferedReader reader = new BufferedReader(fileReader);
			String inputLine = reader.readLine();
			while (inputLine != null) {
				contents.append(inputLine).append("\n");
				inputLine = reader.readLine();
			}
			reader.close();
		} catch (final IOException i1) {
			LOG.severe("Can't open file:" + name);
		}
		return contents.toString();
	}

	/**
	 * Reads the contents of a file in a jar.
	 * 
	 * @param path
	 *            the path to read e.g. /package/name/here/help.html
	 * @return the contents of the file when successful, an empty string
	 *         otherwise.
	 */
	public static String readFileFromJar(final String path) {
		final StringBuilder contents = new StringBuilder();
		final URL url = FileUtils.class.getResource(path);
		URLConnection connection;
		try {
			connection = url.openConnection();
			final InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
			final BufferedReader reader = new BufferedReader(inputStreamReader);
			String inputLine;
			inputLine = reader.readLine();
			while (inputLine != null) {
				contents.append(new String(inputLine.getBytes(), "UTF-8")).append("\n");
				inputLine = reader.readLine();
			}
			reader.close();
		} catch (final IOException e) {
			LOG.severe("Error while reading file " + path + " from jar: " + e.getMessage());
		} catch (final NullPointerException e) {
			LOG.severe("Error while reading file " + path + " from jar: " + e.getMessage());
		}
		return contents.toString();
	}

	/**
	 * Copy a file from a jar.
	 * 
	 * @param source
	 *            The path to read e.g. /package/name/here/help.html
	 * @param target
	 *            The target to save the file to.
	 */
	public static void copyFileFromJar(final String source, final String target) {
		try {
			final InputStream inputStream = new FileUtils().getClass().getResourceAsStream(source);
			OutputStream out;
			out = new FileOutputStream(target);
			final byte[] buffer = new byte[4096];
			int len = inputStream.read(buffer);
			while (len != -1) {
				out.write(buffer, 0, len);
				len = inputStream.read(buffer);
			}
			out.close();
			inputStream.close();
		} catch (final FileNotFoundException e) {
			LOG.log(Level.SEVERE, "File not found: " + e.getMessage(), e);
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "Exception while copying file from jar" + e.getMessage(), e);
		}
	}

	/**
	 * Reads a CSV-file from disk. The separator can be chosen.
	 * 
	 * @param fileName
	 *            the filename, an exception if thrown if the file does not
	 *            exist
	 * @param separator
	 *            the separator, e.g. ";" or ","
	 * @param expectedColumns
	 *            The expected number of columns, user -1 if the number is
	 *            unknown. An exception is thrown if there is a row with an
	 *            unexpected row length.
	 * @return a List of string arrays. The data of the CSV-file can be found in
	 *         the arrays. Each row corresponds with an array.
	 */
	public static List<String[]> readCSVFile(final String fileName, final String separator,
			final int expectedColumns) {
		final List<String[]> data = new ArrayList<String[]>();
		FileReader fileReader = null;
		BufferedReader in = null;
		try {
			final File file = new File(fileName);
			if (!file.exists()) {
				throw new IllegalArgumentException("File '" + fileName + "' does not exist");
			}
			fileReader = new FileReader(file);
			in = new BufferedReader(fileReader);
			String inputLine;
			int lineNumber = 0;
			inputLine = in.readLine();
			while (inputLine != null) {
				lineNumber++;
				final String[] row = inputLine.split(separator);
				if (expectedColumns == -1 || expectedColumns == row.length) {
					data.add(row);
				} else {
					throw new AssertionError("Unexpected row length (line " + lineNumber + " ). "
							+ "Expected:" + expectedColumns + " real " + row.length
							+ ". CVS-file incorrectly formatted?");
				}
				inputLine = in.readLine();
			}
			
		} catch (final IOException i1) {
			LOG.severe("Can't open file:" + fileName);
		} finally {
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					//ignore
				}
			}
		}
		return data;
	}

	public interface RowFilter {
		boolean acceptRow(String[] row);
	}

	public static final RowFilter ACCEPT_ALL_ROWFILTER = new RowFilter() {
		public boolean acceptRow(final String[] row) {
			return true;
		}
	};

	public static List<String> readColumnFromCSVData(final List<String[]> data, final int columnIndex,
			final RowFilter filter) {
		final RowFilter actualFilter = filter == null ? ACCEPT_ALL_ROWFILTER : filter;
		final List<String> columnData = new ArrayList<String>();
		for (final String[] row : data) {
			if (actualFilter.acceptRow(row)) {
				columnData.add(row[columnIndex]);
			}
		}
		return columnData;
	}

	/**
	 * <p>
	 * Return a list of files in directory that satisfy pattern. Pattern should
	 * be a valid regular expression not a 'unix glob pattern' so in stead of
	 * <code>*.wav</code> you could use <code>.*\.wav</code>
	 * </p>
	 * <p>
	 * E.g. in a directory <code>home</code> with the files
	 * <code>be.hogent.tarsos.lsh.test.txt</code>, <code>blaat.wav</code> and <code>foobar.wav</code>
	 * the pattern <code>.*\.wav</code> matches <code>blaat.wav</code> and
	 * <code>foobar.wav</code>
	 * </p>
	 * 
	 * @param directory
	 *            A readable directory.
	 * @param pattern
	 *            A valid regular expression.
	 * @param recursive
	 *            A boolean defining if directories should be traversed
	 *            recursively.
	 * @return a list of filenames matching the pattern for directory.
	 * @exception Error
	 *                an error is thrown if the directory is not ... a
	 *                directory.
	 * @exception java.util.regex.PatternSyntaxException
	 *                Unchecked exception thrown to indicate a syntax error in a
	 *                regular-expression pattern.
	 */
	public static List<String> glob(final String directory, final String pattern, final boolean recursive) {
		final List<String> matchingFiles = new ArrayList<String>();
		final Pattern p = Pattern.compile(pattern);
		final File dir = new File(new File(directory).getAbsolutePath());
		glob(dir, p, recursive, matchingFiles);
		// sort alphabetically
		Collections.sort(matchingFiles);
		return matchingFiles;
	}

	private static void glob(final File directory, final Pattern pattern, final boolean recursive,
			List<String> matchingFiles) {

		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(directory + " is not a directory");
		}
		for (final String file : directory.list()) {
			File filePath = new File(FileUtils.combine(directory.getAbsolutePath(), file));
			if (recursive && filePath.isDirectory()) {
				glob(filePath, pattern, recursive, matchingFiles);
			} else {
				if (pattern.matcher(file).matches() && file != null) {
					matchingFiles.add(filePath.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Return the extension of a file.
	 * 
	 * @param fileName
	 *            the file to get the extension for
	 * @return the extension. E.g. TXT or JPEG.
	 */
	public static String extension(final String fileName) {
		final int dot = fileName.lastIndexOf('.');
		return dot == -1 ? "" : fileName.substring(dot + 1);
	}

	/**
	 * Returns the filename without path and without extension.
	 * 
	 * @param fileName The file name to find the basename for.
	 * @return the file name without extension and path
	 */
	public static String basename(final String fileName) {
		int dot = fileName.lastIndexOf('.');
		int sep = fileName.lastIndexOf(File.separatorChar);
		if (sep == -1) {
			sep = fileName.lastIndexOf('\\');
		}
		if (dot == -1) {
			dot = fileName.length();
		}
		return fileName.substring(sep + 1, dot);
	}

	/**
	 * Returns the path for a file.<br>
	 * <code>path("/home/user/be.hogent.tarsos.lsh.test.jpg") == "/home/user"</code><br>
	 * Uses the correct pathSeparator depending on the operating system. On
	 * windows c:/be.hogent.tarsos.lsh.test/ is not c:\be.hogent.tarsos.lsh.test\
	 * 
	 * @param fileName
	 *            the name of the file using correct path separators.
	 * @return the path of the file.
	 */
	public static String path(final String fileName) {
		final int sep = fileName.lastIndexOf(File.separatorChar);
		return fileName.substring(0, sep);
	}

	/**
	 * Checks if a file exists.
	 * 
	 * @param fileName
	 *            the name of the file to check.
	 * @return true if and only if the file or directory denoted by this
	 *         abstract pathname exists; false otherwise
	 */
	public static boolean exists(final String fileName) {
		return new File(fileName).exists();
	}

	/**
	 * Creates a directory and parent directories if needed.
	 * 
	 * @param path
	 *            the path of the directory to create
	 * @return true if the directory was created (possibly with parent
	 *         directories) , false otherwise
	 */
	public static boolean mkdirs(final String path) {
		return new File(path).mkdirs();
	}

	/**
	 * Copy from source to target.
	 * 
	 * @param source
	 *            the source file.
	 * @param target
	 *            the target file.
	 */
	public static void cp(final String source, final String target) {
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			inChannel = new FileInputStream(new File(source)).getChannel();
			outChannel = new FileOutputStream(new File(target)).getChannel();
			// JavaVM does its best to do this as native I/O operations.
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (final FileNotFoundException e) {
			LOG.severe("File " + source + " not found! " + e.getMessage());
		} catch (final IOException e) {
			LOG.severe("Error while copying " + source + " to " + target + " : " + e.getMessage());
		} finally {
			try {
				if (inChannel != null) {
					inChannel.close();
				}
				if (outChannel != null) {
					outChannel.close();
				}
			} catch (final IOException e) {
				// ignore
				LOG.log(Level.INFO, "Ignored exception while copying files", e);
			}
		}
	}

	/**
	 * Removes a file from disk.
	 * 
	 * @param fileName
	 *            the file to remove
	 * @return true if and only if the file or directory is successfully
	 *         deleted; false otherwise
	 */
	public static boolean rm(final String fileName) {
		return new File(fileName).delete();
	}

	/**
	 * Tests whether the file denoted by this abstract pathname is a directory.
	 * 
	 * @param inputFile
	 *            A pathname string.
	 * @return true if and only if the file denoted by this abstract pathname
	 *         exists and is a directory; false otherwise.
	 */
	public static boolean isDirectory(final String inputFile) {
		return new File(inputFile).isDirectory();
	}

}
