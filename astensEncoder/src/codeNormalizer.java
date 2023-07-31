import java.io.File;
import java.io.IOException;

public class codeNormalizer {

	public static void main(String args[]) throws IOException {
		String currentPath = "./";
		Boolean commentsDeletion = true;
		Boolean codeNormalization = true;

		String srcPath = currentPath + "/source/";
		String writePath = currentPath + "/normalization/";
		String donePath = currentPath + "/done/";
		String errorPath = currentPath + "/errors/";

		File file = new File(srcPath);
		if (!file.exists())
			System.out.println("No source files!");
		else {
			file = new File(writePath);
			if (!file.exists())
				file.mkdir();
			file = new File(donePath);
			if (!file.exists())
				file.mkdir();
			file = new File(errorPath);
			if (!file.exists())
				file.mkdir();

			codeNormalizerC normalizationC = new codeNormalizerC();
			normalizationC.currentPath = currentPath;
			normalizationC.commentsDeletion = commentsDeletion;
			normalizationC.codeNormalization = codeNormalization;
			normalizationC.normalizing();

			codeNormalizerJava normalizationJava = new codeNormalizerJava();
			normalizationJava.currentPath = currentPath;
			normalizationJava.commentsDeletion = commentsDeletion;
			normalizationJava.codeNormalization = codeNormalization;
			normalizationJava.normalizing();
		}
	}
}
