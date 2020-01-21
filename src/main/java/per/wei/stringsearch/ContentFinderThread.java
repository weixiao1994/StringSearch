package per.wei.stringsearch;

import java.util.Map;
import java.util.concurrent.Callable;

public class ContentFinderThread implements Callable<Error>{
	
	private String searchRootPath;
	private String toBeSearchedString;
	private String[] suffixes;
	private Map<String, Integer> mapFilePath2Line;
	private ThreadShare threadShare;
	
	public ContentFinderThread(String searchRootPath, String toBeSearchedString, String[] suffixes,Map<String, Integer> mapFilePath2Line,ThreadShare threadShare) {
		this.searchRootPath=searchRootPath;
		this.toBeSearchedString=toBeSearchedString;
		this.suffixes = suffixes;
		this.mapFilePath2Line = mapFilePath2Line;
		this.threadShare = threadShare;
	}
	@Override
	public Error call() {
		ContentFinder contentFinder = new ContentFinder(threadShare);
		return contentFinder.getFindResult(searchRootPath, toBeSearchedString, suffixes, mapFilePath2Line);
	}
}
