package per.wei.stringsearch;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.swing.JCheckBox;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WindowUI {

	private JFrame frame;
	private JTextField textFieldRootPath;
	private JTextField textFieldTargetString;
	private JTextField textFieldSuffix;
	private JProgressBar progressBar;
	private JScrollPane scrollPane;
	private JPanel panelDisplayArea;
	private ThreadShare threadShare = new ThreadShare();
	private JTextField textFieldLog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WindowUI window = new WindowUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WindowUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panelInputArea = new JPanel();
		frame.getContentPane().add(panelInputArea, BorderLayout.NORTH);
		panelInputArea.setLayout(new BoxLayout(panelInputArea, BoxLayout.Y_AXIS));

		JPanel panelInputArea_rootPath = new JPanel();
		panelInputArea.add(panelInputArea_rootPath);
		panelInputArea_rootPath.setLayout(new BoxLayout(panelInputArea_rootPath, BoxLayout.X_AXIS));

		JLabel labelRootPath = new JLabel("搜索根路径:");
		panelInputArea_rootPath.add(labelRootPath);

		textFieldRootPath = new JTextField();
		textFieldRootPath.setColumns(10);
		panelInputArea_rootPath.add(textFieldRootPath);

		JPanel panelInputArea_targetString = new JPanel();
		panelInputArea.add(panelInputArea_targetString);
		panelInputArea_targetString.setLayout(new BoxLayout(panelInputArea_targetString, BoxLayout.X_AXIS));

		JLabel labelTargetString = new JLabel("目标字符串:");
		panelInputArea_targetString.add(labelTargetString);

		textFieldTargetString = new JTextField();
		panelInputArea_targetString.add(textFieldTargetString);
		textFieldTargetString.setColumns(10);

		JPanel panelInputArea_suffixCheckBox = new JPanel();
		panelInputArea.add(panelInputArea_suffixCheckBox);
		panelInputArea_suffixCheckBox.setLayout(new BoxLayout(panelInputArea_suffixCheckBox, BoxLayout.X_AXIS));

		JCheckBox checkBoxSuffix = new JCheckBox("指定文件后缀:");
		checkBoxSuffix.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				boolean checkBoxSelected = checkBoxSuffix.isSelected();
				textFieldSuffix.setEditable(checkBoxSelected);
			}
		});
		panelInputArea_suffixCheckBox.add(checkBoxSuffix);

		textFieldSuffix = new JTextField();
		textFieldSuffix.setEditable(false);
		panelInputArea_suffixCheckBox.add(textFieldSuffix);
		textFieldSuffix.setColumns(10);

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setForeground(new Color(34, 139, 34));
		panelInputArea.add(progressBar);

		JPanel panelBtnTextArea = new JPanel();
		frame.getContentPane().add(panelBtnTextArea, BorderLayout.SOUTH);
		panelBtnTextArea.setLayout(new BorderLayout(0, 0));

		JButton btnStart = new JButton("开始");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removePreResult();
				String searchRootPath = textFieldRootPath.getText();
				String toBeSearchedString = textFieldTargetString.getText();
				String[] suffixes = null;
				String str = textFieldSuffix.getText();
				if (!str.isEmpty()) {
					suffixes = str.split("\\s+", -1);
				}
				startSearchThread(searchRootPath, toBeSearchedString, suffixes);
			}
		});
		panelBtnTextArea.add(btnStart, BorderLayout.EAST);
		
		textFieldLog = new JTextField();
		textFieldLog.setEditable(false);
		panelBtnTextArea.add(textFieldLog, BorderLayout.CENTER);
		textFieldLog.setColumns(10);
		panelDisplayArea = new JPanel();
		panelDisplayArea.setBorder(null);
		panelDisplayArea.setLayout(new BoxLayout(panelDisplayArea, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(panelDisplayArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	public void startSearchThread(String searchRootPath, String toBeSearchedString, String[] suffixes) {
		new ProgressBarRealized(searchRootPath, toBeSearchedString, suffixes).execute();
	}

	private void removePreResult() {
		panelDisplayArea.removeAll();
	}

	private void displayResult(Map<String, Integer> mapFilePath2Line) {
		if (mapFilePath2Line == null || mapFilePath2Line.size() == 0) {
			return;
		}
		for (Entry<String, Integer> entry : mapFilePath2Line.entrySet()) {
			JButton jButton = new JButton();
			jButton.setText(String.format("%s line:%d", entry.getKey(), entry.getValue()));
			panelDisplayArea.add(jButton);
		}
		panelDisplayArea.updateUI();
	}

	class ProgressBarRealized extends SwingWorker<Void, Integer> {
		private String searchRootPath;
		private String toBeSearchedString;
		private String[] suffixes;
		private Map<String, Integer> mapFilePath2Line = new HashMap<String, Integer>();

		public ProgressBarRealized(String searchRootPath, String toBeSearchedString, String[] suffixes) {
			this.searchRootPath = searchRootPath;
			this.toBeSearchedString = toBeSearchedString;
			this.suffixes = suffixes;
		}

		@Override
		// 后台任务在此方法中实现
		protected Void doInBackground() throws Exception {
			ContentFinderThread contentFinderThread = new ContentFinderThread(searchRootPath, toBeSearchedString,
					suffixes, mapFilePath2Line, threadShare);
			FutureTask<Error> future = new FutureTask<>(contentFinderThread);
			new Thread(future).start();
			try {
				Error result = Error.Unknown;
				while (true) {
					boolean isDone = future.isDone();
					Integer progressObj = new Integer(0);
					if (threadShare.getShareMemory() instanceof Integer) {
						progressObj = (Integer) (threadShare.getShareMemory());
						System.out.println("UI:" + progressObj.intValue());
					}
					publish(progressObj.intValue());// 将当前进度信息加入chunks中
					if (isDone) {
						result = future.get();
						break;
					}
					Thread.sleep(1000);
				}
				if (result == Error.Success) {
					displayResult(mapFilePath2Line);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		// 每次更新进度条的信息
		protected void process(List<Integer> chunks) {
			progressBar.setValue(chunks.get(chunks.size() - 1));
		}

		@Override
		// 任务完成后返回一个信息
		protected void done() {
			
		}
	}
}