package per.wei.stringsearch;

import javax.swing.JProgressBar;

public class ProgressListenerImpl implements ProgressListener{

	private JProgressBar progressBar;
	
	public ProgressListenerImpl(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	
	@Override
	public void progressEvent(ProgressValueEvent event) {
		// TODO Auto-generated method stub
		progressBar.setValue(event.getValue());
		progressBar.updateUI();
	}
}
