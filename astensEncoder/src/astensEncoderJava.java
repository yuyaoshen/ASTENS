
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

public class astensEncoderJava {
	static String currentPath;
	static Boolean exportNodeValue;
	static Boolean exportDot;
	static Boolean coloredNodeFlag;
	static ArrayList<String> level = new ArrayList<String>();
	static ArrayList<String> structureCoding = new ArrayList<String>();
	static ArrayList<String> nodeType = new ArrayList<String>();
	static ArrayList<String> nodeToken = new ArrayList<String>();
	static ArrayList<String> typeCoding = new ArrayList<String>();
	static ArrayList<String> lineNumber = new ArrayList<String>();
	static ArrayList<String> seqPosNumber = new ArrayList<String>();
	static ArrayList<String> colorMap = new ArrayList<String>();
	static ArrayList<String> nodeTypeStatList = new ArrayList<String>();

	public void EncodingASTENS() throws IOException {
		String srcPath = currentPath + "/source/";
		String writePath = currentPath + "/astens/";
		String dotPath = currentPath + "/dot/";
		String donePath = currentPath + "/done/";
		String errorPath = currentPath + "/errors/";

		File dir = new File(srcPath);
		File[] files = dir.listFiles();
		int count = files.length;
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				// reading source code file
				String filename = files[i].getName();
				String language = filename.split("\\.")[filename.split("\\.").length - 1];
				if (language.equals("java")) {
					System.out.println(filename);
					File file = new File(srcPath + filename);
					byte[] encoded = Files.readAllBytes(file.toPath());
					String inputString = new String(encoded, Charset.forName("UTF-8"));

					level = new ArrayList<String>();
					structureCoding = new ArrayList<String>();
					nodeType = new ArrayList<String>();
					nodeToken = new ArrayList<String>();
					typeCoding = new ArrayList<String>();
					lineNumber = new ArrayList<String>();
					seqPosNumber = new ArrayList<String>();
					// parsing AST and encoding sequence
					ANTLRInputStream input = new ANTLRInputStream(inputString);
					JavaLexer lexer = new JavaLexer(input);
					CommonTokenStream tokens = new CommonTokenStream(lexer);
					JavaParser parser = new JavaParser(tokens);
//					ParserRuleContext ctx = parser.compilationUnit();
					ParserRuleContext ctx = parser.classBodyDeclaration();
					if (parser.getNumberOfSyntaxErrors() > 0) {
						Files.move(Paths.get(files[i].getPath()), Paths.get(errorPath + filename),
								StandardCopyOption.REPLACE_EXISTING);
						continue;
					}

					try {
						generateAST(ctx, false, 0);
					} catch (NullPointerException e) {
						Files.move(Paths.get(files[i].getPath()), Paths.get(errorPath + filename),
								StandardCopyOption.REPLACE_EXISTING);
						continue;
					}
					// get the structure coding of all nodes
					getStructureCoding();

					File writefile = new File(writePath + filename + ".txt");
					if (writefile.exists())
						writefile.delete();
					writefile.createNewFile();// create output file

					for (int j = 0; j < level.size(); j++) {
						FileOutputStream fos = null;
						PrintStream ps = null;
						try {
							fos = new FileOutputStream(writefile, true);
							ps = new PrintStream(fos);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						String string = null;
						if (exportNodeValue)
							string = (structureCoding.get(j) + typeCoding.get(j) + " " + nodeType.get(j) + " "
									+ lineNumber.get(j) + " " + seqPosNumber.get(j) + " " + nodeToken.get(j) + "\n");
						else
							string = (structureCoding.get(j) + typeCoding.get(j) + " " + nodeType.get(j) + " "
									+ lineNumber.get(j) + "\n");
						ps.print(string);
						ps.close();
					}

					if (exportDot) {
						if (coloredNodeFlag)
							getColorMap();
						File writeDOTfile = new File(dotPath + filename + ".dot");
						if (writeDOTfile.exists())
							writeDOTfile.delete();
						writeDOTfile.createNewFile();// create Dot file
						PrintStream console = System.out;
						PrintStream printDot = new PrintStream(writeDOTfile);
						System.setOut(printDot);
						System.out.println("digraph G {");
						printDOT();
						System.out.println("}");
						System.setOut(console);
					}

					Files.move(Paths.get(files[i].getPath()), Paths.get(donePath + filename),
							StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	private static void generateAST(RuleContext ctx, boolean verbose, int indentation) {
		boolean toBeIgnored = !verbose && ctx.getChildCount() == 1 && ctx.getChild(0) instanceof ParserRuleContext;

		if (!toBeIgnored) {
			// get the level of each node
			level.add(Integer.toString(indentation));

			// get the node type of each node and its type coding
			String ruleName = JavaParser.ruleNames[ctx.getRuleIndex()];
			String ruleIndex = "unknown";
			String ruleToken = " ";
			Token startToken = ((ParserRuleContext) ctx).start;
			Token stopToken = ((ParserRuleContext) ctx).stop;
			if (startToken.getTokenIndex() == stopToken.getTokenIndex())
				ruleToken = startToken.getText();
			int nodeID = ctx.getRuleIndex();
			if (nodeID < 10)
				ruleIndex = "00" + Integer.toString(nodeID);
			else if (nodeID < 100)
				ruleIndex = "0" + Integer.toString(nodeID);
			else
				ruleIndex = Integer.toString(nodeID);
			nodeType.add(ruleName);
			nodeToken.add(ruleToken);
			typeCoding.add(ruleIndex);
			lineNumber.add(Integer.toString(((ParserRuleContext) ctx).getStart().getLine()));
			seqPosNumber.add(Integer.toString(((ParserRuleContext) ctx).getStart().getStartIndex()));
		}
		for (int i = 0; i < ctx.getChildCount(); i++) {
			ParseTree element = ctx.getChild(i);
			if (element instanceof RuleContext) {
				generateAST((RuleContext) element, verbose, indentation + (toBeIgnored ? 0 : 1));
			}
		}
	}

	private static void getStructureCoding() {
		int preNodeLevel = 0;
		int levelDiff = 0;
		String strLevelDiff = "****";
		for (int i = 0; i < level.size(); i++) {
			levelDiff = Integer.parseInt(level.get(i)) - preNodeLevel;
			if (levelDiff > 99)
				strLevelDiff = "+" + levelDiff;
			else if (levelDiff > 9)
				strLevelDiff = "+0" + levelDiff;
			else if (levelDiff > -1)
				strLevelDiff = "+00" + levelDiff;
			else if (levelDiff > -10)
				strLevelDiff = "-00" + (-levelDiff);
			else if (levelDiff > -100)
				strLevelDiff = "-0" + (-levelDiff);
			else if (levelDiff < -99)
				strLevelDiff = "-" + (-levelDiff);
			preNodeLevel = Integer.parseInt(level.get(i));
			structureCoding.add(strLevelDiff);
		}
	}

	private static void printDOT() {
		printLabel();
		int pos = 0;
		for (int i = 1; i < level.size(); i++) {
			pos = getPos(Integer.parseInt(level.get(i)) - 1, i);
			System.out.println((Integer.parseInt(level.get(i)) - 1) + Integer.toString(pos) + "->" + level.get(i) + i);
			System.out.println(level.get(i - 1) + (i - 1) + "->" + level.get(i) + i
					+ "[color=red,arrowhead=vee,style=dotted,constraint=false]");
		}
	}

	private static void getColorMap() {
		String colorFileName = currentPath + "../libs/ColorMap_81.txt";
		Path colorMapPath = Paths.get(colorFileName);
		try {
			@SuppressWarnings("resource")
			Scanner scannerColor = new Scanner(colorMapPath);
			String colorMapLine;
			String[] RGB;
			while (scannerColor.hasNextLine()) {
				colorMapLine = scannerColor.nextLine();
				RGB = colorMapLine.split(" ");
				int r = Integer.parseInt(RGB[0]);
				int g = Integer.parseInt(RGB[1]);
				int b = Integer.parseInt(RGB[2]);
				String hex = Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
				colorMap.add(hex);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		String nodeTypeList = currentPath + "../libs/JAVA-nodeTypes-wo_sc.uniq.txt";
		Path nodeTypeListPath = Paths.get(nodeTypeList);
		try {
			@SuppressWarnings("resource")
			Scanner scannerNode = new Scanner(nodeTypeListPath);
			String nodeTypeLine;
			String[] nodeList;
			while (scannerNode.hasNextLine()) {
				nodeTypeLine = scannerNode.nextLine();
				nodeList = nodeTypeLine.split(" ");
				String SC = nodeList[1];
				nodeTypeStatList.add(SC);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printLabel() {
		for (int i = 0; i < level.size(); i++) {
			if (coloredNodeFlag) {
				int idx = nodeTypeStatList.indexOf(typeCoding.get(i));
				if (exportNodeValue)
					System.out
							.println(level.get(i) + i + "[label=\"" + nodeType.get(i) + "\\n(" + structureCoding.get(i)
									+ "," + typeCoding.get(i) + ")\\n" + nodeToken.get(i).replaceAll("\"", "\'")
									+ "\",shape=box,style=filled,color=\"#" + colorMap.get(idx) + "\"]");
				else
					System.out.println(level.get(i) + i + "[label=\"" + nodeType.get(i) + "\\n("
							+ structureCoding.get(i) + "," + typeCoding.get(i) + ")\",shape=box,style=filled,color=\"#"
							+ colorMap.get(idx) + "\"]");
			} else {
				if (exportNodeValue)
					System.out.println(level.get(i) + i + "[label=\"" + nodeType.get(i) + "\\n("
							+ structureCoding.get(i) + "," + typeCoding.get(i) + ")\\n"
							+ nodeToken.get(i).replaceAll("\"", "\'") + "\",shape=box]");
				else
					System.out.println(level.get(i) + i + "[label=\"" + nodeType.get(i) + "\\n("
							+ structureCoding.get(i) + "," + typeCoding.get(i) + ")\",shape=box]");
			}
		}
	}

	private static int getPos(int n, int limit) {
		int pos = 0;
		for (int i = 0; i < limit; i++) {
			if (Integer.parseInt(level.get(i)) == n) {
				pos = i;
			}
		}
		return pos;
	}
}
