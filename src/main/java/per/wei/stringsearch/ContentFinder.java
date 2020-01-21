package per.wei.stringsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class ContentFinder {
	
	private ThreadShare threadShare;
	
	public ContentFinder(ThreadShare threadShare) {
		this.threadShare = threadShare;
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			System.out.println("请输入要搜索的根目录：");
			String searchRootPath = scanner.nextLine();
			System.out.println("请输入要搜索的字符串：");
			String toBeSearchedString = scanner.nextLine();
			System.out.println("请输入要搜索的文件后缀（如txt,doc 多个后缀以空格区分）：");
			String inputStr = scanner.nextLine();
			String[] suffixes = null;
			if (!inputStr.isEmpty()) {
				suffixes = inputStr.split("\\s+", -1);
			}

			ContentFinder contentFinder = new ContentFinder(new ThreadShare());
			List<String> allPaths = new LinkedList<String>();
			if (Error.Success != contentFinder.listAllFiles(toBeSearchedString, searchRootPath, suffixes, allPaths)) {
				return;
			}
			Map<String, Integer> mapFilePath2Line = new HashMap<String, Integer>();
			if (Error.Success != contentFinder.findMatchedFile(toBeSearchedString, searchRootPath, allPaths,
					mapFilePath2Line)) {
				return;
			}
			if (mapFilePath2Line == null || mapFilePath2Line.size() == 0) {
				System.out.println("未找到目标字符串！");
				scanner.close();
				return;
			}
			for (Entry<String, Integer> entry : mapFilePath2Line.entrySet()) {
				System.out.println(entry.getKey() + " line:" + entry.getValue());
			}
		} finally {
			scanner.close();
		}
	}

	public Error getFindResult(String searchRootPath, String toBeSearchedString, String[] suffixes,
			Map<String, Integer> mapFilePath2Line) {
		List<String> allPaths = new LinkedList<String>();
		Error tmpResult = listAllFiles(toBeSearchedString, searchRootPath, suffixes, allPaths);
		if (Error.Success != tmpResult) {
			return tmpResult;
		}
		tmpResult = findMatchedFile(toBeSearchedString, searchRootPath, allPaths, mapFilePath2Line);
		if (Error.Success != tmpResult) {
			return tmpResult;
		}
		return Error.Success;
	}

	public Error listAllFiles(String toBeSearchedString, String searchRootPath, String[] suffixes,
			List<String> allPaths) {
		File directory = new File(searchRootPath);
		if (!directory.exists()) {
			System.out.println(String.format("[error]路径：%s不存在！", searchRootPath));
			return Error.DirNotExsit;
		}
		if (!directory.isDirectory()) {
			System.out.println(String.format("[error]路径：%s不是目录！", searchRootPath));
			return Error.StringIsNotDir;
		}
		try {
			// System.out.println("正在获取目录集合...");
			listFilesPath(directory, suffixes, allPaths);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Error.Unknown;
		}
		return Error.Success;
	}

	public Error findMatchedFile(String toBeSearchedString, String searchRootPath, List<String> allPaths,
			Map<String, Integer> mapFilePath2Line) {
		int preValue = -1;
		for (int i = 0; i < allPaths.size(); i++) {
			int progressValue = (int) (100.0 * ((i + 1) / allPaths.size()));
			if(preValue!=progressValue) {
				threadShare.setShareMemory(new Integer(progressValue));
				preValue = progressValue;
			}
			
			int lineIndex = lineFound(allPaths.get(i), toBeSearchedString);
			if (lineIndex != -1) {
				mapFilePath2Line.put(allPaths.get(i), lineIndex);
			}
		}
		return Error.Success;
	}

	private void listFilesPath(File parent, String[] suffixes, List<String> allPaths) throws IOException {
		if (!parent.isFile()) {
			File[] files = parent.listFiles();
			for (File file : files) {
				listFilesPath(file, suffixes, allPaths);
			}
		} else {
			String fileName = parent.getName();
			String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (suffixes == null || suffixes.length == 0) {
				allPaths.add(parent.getCanonicalPath());
			} else {
				for (String suffix : suffixes) {
					if (fileSuffix.equals(suffix)) {
						allPaths.add(parent.getCanonicalPath());
					}
				}
			}
		}
	}

	/**
	 * 在文本文件中寻找指定字符串
	 * 
	 * @param file
	 * @param toBeSearchedString
	 * @return 第一个匹配的字符串所在行数，没有找到返回-1
	 */
	private int lineFound(String filePath, String toBeSearchedString) {

		File file = new File(filePath);

		String line = "";
		int lineIndex = 0;
		try (FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr)) {
			try {
				while ((line = br.readLine()) != null) {
					lineIndex++;
					if (line.contains(toBeSearchedString)) {
						return lineIndex;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return -1;
	}

}
