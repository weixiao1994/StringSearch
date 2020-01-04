package per.wei.stringsearch;

import java.util.EventListener;

public interface ProgressListener extends EventListener {
	public void progressEvent(ProgressValueEvent event);
}
