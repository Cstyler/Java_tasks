import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by khan on 23.03.16. Sync
 */

public class Sync {
	private static final ArrayList<String> deleteFiles = new ArrayList<>(), copyFiles = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		printAnswer(args[0], args[1]);
	}

	private static void printAnswer(String S, String D) throws IOException {
		analyseDirectories(D, S, "");
		if (deleteFiles.isEmpty() && copyFiles.isEmpty()) {
			System.out.println("IDENTICAL");
			return;
		}
		printFiles(deleteFiles, "DELETE ");
		printFiles(copyFiles, "COPY ");
	}

	private static void printFiles(ArrayList<String> stringArrayList, String cmd) {
		stringArrayList.sort(String::compareTo);
		for (String s :
				stringArrayList) {
			System.out.println(cmd + s);
		}
	}

	private static boolean compareFiles(String path1, String path2) throws IOException {
		return Arrays.equals(Files.readAllBytes(Paths.get(path1)), Files.readAllBytes(Paths.get(path2)));
	}

	private static void deleteOrCopyDir(String path, String baseDir, ArrayList<String> a) {
		for (String s :
				new File(path).list()) {
			String tempPath = path + "/" + s;
			if (new File(tempPath).isDirectory()) {
				deleteOrCopyDir(tempPath, baseDir + s, a);
			} else {
				a.add(baseDir + s);
			}
		}
	}

	private static HashSet<String> initHashSet(String[] a) {
		return new HashSet<String>() {{
			addAll(Arrays.asList(a));
		}};
	}

	private static void analyseDirectories(String path1, String path2, String baseDir) throws IOException {
		String[] listD = new File(path1).list(), listS = new File(path2).list();
		HashSet<String> DHashSet = initHashSet(listD), SHashSet = initHashSet(listS);
		for (String s :
				listS) {
			String path1String = path1 + "/" + s, path2String = path2 + "/" + s;
			if (DHashSet.contains(s)) {
				File tempFile = new File(path1String);
				if (tempFile.isFile() && !compareFiles(path1String, path2String)) {
					String tempString = baseDir + s;
					deleteFiles.add(tempString);
					copyFiles.add(tempString);
				} else if (tempFile.isDirectory()) {
					analyseDirectories(path1String, path2String, baseDir + s + "/");
				}
			} else {
				if (new File(path2String).isDirectory()) {
					deleteOrCopyDir(path2String, s + "/", copyFiles);
				} else {
					copyFiles.add(baseDir + s);
				}
			}
		}
		for (String s :
				listD) {
			if (!SHashSet.contains(s)) {
				String path1String = path1 + "/" + s;
				if (new File(path1String).isDirectory()) {
					deleteOrCopyDir(path1String, s + "/", deleteFiles);
				} else {
					deleteFiles.add(baseDir + s);
				}
			}
		}
	}
}
