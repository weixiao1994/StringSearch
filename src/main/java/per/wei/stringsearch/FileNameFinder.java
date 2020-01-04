package per.wei.stringsearch;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class FileNameFinder {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("请输入要搜索的根目录：");
		String searchRootPath = scanner.nextLine();
		System.out.println("请输入要搜索的文件名：");
		String toBeSearchedString = scanner.nextLine();
		List<String> listMatchedFiles = findMatchedPath(toBeSearchedString, searchRootPath);
		if (listMatchedFiles.size() == 0) {
			System.out.println("未找到符合条件的文件！");
		} else {
			System.out.println("找到以下文件");
			System.out.println("-----------------------------------------------------");
			for (String file : listMatchedFiles) {
				System.out.println(file);
			}
			System.out.println("-----------------------------------------------------");
		}
		scanner.close();
	}

	public static List<String> findMatchedPath(String toBeSearchedString, String searchRootPath) {
		File directory = new File(searchRootPath);
		if (!directory.exists()) {
			System.out.println(String.format("[error]路径：%s不存在！", searchRootPath));
			return null;
		}
		if (!directory.isDirectory()) {
			System.out.println(String.format("[error]路径：%s不是目录！", searchRootPath));
			return null;
		}
		List<String> matchedPaths = new LinkedList<String>();
		String regex = ".*?" + toBeSearchedString + ".*?";
		try {
			listFilesPath(directory, regex, matchedPaths);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return matchedPaths;
	}

	private static void listFilesPath(File parent, String regex, List<String> matchedPaths) throws IOException {
		if (!parent.isFile()) {
			File[] files = parent.listFiles();
			for (File file : files) {
				listFilesPath(file, regex, matchedPaths);
			}
		} else {
			if (parent.getName().matches(regex)) {
				matchedPaths.add(parent.getCanonicalPath());
			}
		}
	}
}
