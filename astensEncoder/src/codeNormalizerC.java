import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class codeNormalizerC {
	static String currentPath;
	static Boolean commentsDeletion;
	static Boolean codeNormalization;

	private static Pattern functionNamePattern = Pattern.compile("\\b([_A-Za-z]\\w*)\\b(?=\\s*\\()");
	private static Pattern variableNamePattern = Pattern
			.compile("\\b([_A-Za-z]\\w*)\\b(?:(?=\\s*\\w+\\()|(?!\\s*\\w+))(?!\\s*\\()");

	public void normalizing() throws IOException {
		String srcPath = currentPath + "/source/";
		String writePath = currentPath + "/normalization/";
		String donePath = currentPath + "/done/";
		String errorPath = currentPath + "/errors/";

		keywordsList keywordslist = new keywordsList();

		File dir = new File(srcPath);
		File[] files = dir.listFiles();
		int count = files.length;
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				// reading source code file
				String filename = files[i].getName();
				String language = filename.split("\\.")[filename.split("\\.").length - 1];
				if (language.equals("c") | language.equals("cpp")) {
					ArrayList<String> functionNames = new ArrayList<String>();
					ArrayList<String> variableNames = new ArrayList<String>();
					System.out.println(filename);
					File file = new File(srcPath + filename);
					byte[] encoded = Files.readAllBytes(file.toPath());
					String content = new String(encoded, Charset.forName("UTF-8"));

					// cleaning all comments in source code
					if (commentsDeletion)
						content = cleanComments.cleanComment(content);

					if (codeNormalization) {
						// detecting the names of functions and variables
						String lines[] = content.split("\\r?\\n");
						String name = "";
						for (int j = 0; j < lines.length; j++) {
							Matcher m = functionNamePattern.matcher(lines[j]);
							while (m.find()) {
								name = m.group();
								if (!keywordsList.mainSet.contains(name) && !keywordsList.keywordsC11.contains(name)
										&& !functionNames.contains(name))
									functionNames.add(name);
							}
							m = variableNamePattern.matcher(lines[j]);
							while (m.find()) {
								name = m.group();
								if (!keywordsList.mainArgs.contains(name) && !keywordsList.keywordsC11.contains(name)
										&& !variableNames.contains(name))
									variableNames.add(name);
							}
						}
						// System.out.println(functionNames);
						// System.out.println(variableNames);

						// blinding the names of functions and variables
						int functionNum = functionNames.size();
						int variableNum = variableNames.size();
						content = String.join("\n", lines);
						for (int j = 0; j < functionNum; j++)
							content = content.replaceAll("\\b(" + functionNames.get(j) + ")\\b(?=\\s*\\()", "FUN_" + j);
						for (int j = 0; j < variableNum; j++)
							content = content.replaceAll(
									"\\b(" + variableNames.get(j) + ")\\b(?:(?=\\s*\\w+\\()|(?!\\s*\\w+))(?!\\s*\\()",
									"VAR_" + j);

						File writefile = new File(writePath + filename + ".new");
						if (writefile.exists())
							writefile.delete();
						writefile.createNewFile();// create output file
						FileOutputStream fos = null;
						PrintStream ps = null;
						fos = new FileOutputStream(writefile, true);
						ps = new PrintStream(fos);
						ps.print(content);
						ps.close();

						Files.move(Paths.get(files[i].getPath()), Paths.get(donePath + filename),
								StandardCopyOption.REPLACE_EXISTING);
					}
				}
			}
		}
	}
}
