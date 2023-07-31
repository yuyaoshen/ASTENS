import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class cleanComments {
	static Boolean strLiteralReplace = true;
	private static Pattern MultiLinesComment = Pattern.compile("/\\*.+?\\*/", Pattern.DOTALL);
	private static Pattern StringLiteral = Pattern.compile("\".*?\"");
	private static Pattern CommentLabel = Pattern.compile("//");
	private static Pattern InLineComment = Pattern.compile("//.+");
	private static Pattern MultiSentenceInOneLine = Pattern.compile(".*?;");

	public static String cleanComment(String content) throws IOException {
		content = MultiLinesComment.matcher(content).replaceAll("");
		String lines[] = content.split("\\r?\\n");
		ArrayList<String> codeLines = new ArrayList<String>();
		for (int i = 0; i < lines.length; i++) {
			if (strLiteralReplace)
				// replace the string literals with "string_literal"
				lines[i] = StringLiteral.matcher(lines[i]).replaceAll("\"string_literal\"");
			else {
				// replace the "//" symbols in string literals with "/#"
				Matcher m = StringLiteral.matcher(lines[i]);
				while (m.find()) {
					String tmp = m.group();
					tmp = CommentLabel.matcher(tmp).replaceAll("/#");
					lines[i] = m.replaceAll(tmp);
				}
			}
			lines[i] = InLineComment.matcher(lines[i]).replaceAll("");
			if (!lines[i].isBlank()) {
				Matcher m = MultiSentenceInOneLine.matcher(lines[i]);
				boolean multiSentence = false;
				while (m.find()) {
					multiSentence = true;
					codeLines.add(m.group());
				}
				if (!multiSentence)
					codeLines.add(lines[i]);
			}
		}
		content = String.join("\n", codeLines);
		return content;
	}
}
