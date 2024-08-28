import java.io.File;
import java.io.IOException;

public class astensEncoder {

	public static void main(String args[]) throws IOException {
		String currentPath = "test/";
		Boolean exportNodeValue = true;
		Boolean exportDot = true;
		Boolean coloredNodeFlag = true;

		String srcPath = currentPath + "/source/";
		String writePath = currentPath + "/astens/";
		String dotPath = currentPath + "/dot/";
		String donePath = currentPath + "/done/";
		String errorPath = currentPath + "/errors/";

		File file = new File(srcPath);
		if (!file.exists())
			System.out.println("No source files!");
		else {
			file = new File(writePath);
			if (!file.exists())
				file.mkdir();
			file = new File(dotPath);
			if (!file.exists())
				file.mkdir();
			file = new File(donePath);
			if (!file.exists())
				file.mkdir();
			file = new File(errorPath);
			if (!file.exists())
				file.mkdir();

			astensEncoderC astensC = new astensEncoderC();
			astensC.currentPath = currentPath;
			astensC.exportNodeValue = exportNodeValue;
			astensC.exportDot = exportDot;
			astensC.EncodingASTENS();

//			astensEncoderCPP14 astensCPP14 = new astensEncoderCPP14();
//			astensCPP14.currentPath = currentPath;
//			astensCPP14.exportNodeValue = exportNodeValue;
//			astensCPP14.exportDot = exportDot;
//			astensCPP14.EncodingASTENS();

			astensEncoderJava astensJava = new astensEncoderJava();
			astensJava.currentPath = currentPath;
			astensJava.exportNodeValue = exportNodeValue;
			astensJava.exportDot = exportDot;
			astensJava.coloredNodeFlag = coloredNodeFlag;
			astensJava.EncodingASTENS();

			astensEncoderPython3 astensPython3 = new astensEncoderPython3();
			astensPython3.currentPath = currentPath;
			astensPython3.exportNodeValue = exportNodeValue;
			astensPython3.exportDot = exportDot;
			astensPython3.EncodingASTENS();

			astensEncoderECMAScript astensECMAScript = new astensEncoderECMAScript();
			astensECMAScript.currentPath = currentPath;
			astensECMAScript.exportNodeValue = exportNodeValue;
			astensECMAScript.exportDot = exportDot;
			astensECMAScript.EncodingASTENS();

			astensEncoderGolang astensGolang = new astensEncoderGolang();
			astensGolang.currentPath = currentPath;
			astensGolang.exportNodeValue = exportNodeValue;
			astensGolang.exportDot = exportDot;
			astensGolang.EncodingASTENS();
		}
	}
}
