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
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.HashMap;
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
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JProgressBar progressBar;
	private JPanel panel_5;
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

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		JLabel label = new JLabel("搜索根路径:");
		panel_1.add(label);

		textField = new JTextField();
		textField.setColumns(10);
		panel_1.add(textField);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));

		JLabel lblNewLabel = new JLabel("目标字符串:");
		panel_2.add(lblNewLabel);

		textField_1 = new JTextField();
		panel_2.add(textField_1);
		textField_1.setColumns(10);

		JPanel panel_3 = new JPanel();
		panel.add(panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));

		JCheckBox chckbxNewCheckBox = new JCheckBox("指定文件后缀:");
		chckbxNewCheckBox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				boolean checkBoxSelected = chckbxNewCheckBox.isSelected();
				textField_2.setEditable(checkBoxSelected);
			}
		});
		panel_3.add(chckbxNewCheckBox);

		textField_2 = new JTextField();
		textField_2.setEditable(false);
		panel_3.add(textField_2);
		textField_2.setColumns(10);

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setForeground(new Color(34, 139, 34));
		panel.add(progressBar);
		
		JPanel panel_4 = new JPanel();
		frame.getContentPane().add(panel_4, BorderLayout.SOUTH);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JButton btnNewButton = new JButton("开始");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String searchRootPath = textField.getText();
				String toBeSearchedString = textField_1.getText();
				String[] suffixes = null;
				String str = textField_2.getText();
				if (!str.isEmpty()) {
					suffixes = str.split("\\s+", -1);
				}
				startSearchThread(searchRootPath,toBeSearchedString,suffixes);
			}
		});
		panel_4.add(btnNewButton);
		
		panel_5 = new JPanel();
		frame.getContentPane().add(panel_5, BorderLayout.WEST);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.Y_AXIS));
	}

	public void startSearchThread(String searchRootPath, String toBeSearchedString, String[] suffixes) {
		Map<String, Integer> mapFilePath2Line = new HashMap<String, Integer>();
		ProgressListener listener = new ProgressListenerImpl(progressBar);
		ContentFinderThread contentFinderThread = new ContentFinderThread(searchRootPath, toBeSearchedString, suffixes,
				mapFilePath2Line, listener);
		FutureTask<Error> future = new FutureTask<>(contentFinderThread);
		new Thread(future).start();
//		try {
//			Error result = future.get();
//			if(result == Error.Success) {
//				displayResult(mapFilePath2Line);
//			}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private void displayResult(Map<String, Integer> mapFilePath2Line) {
		if(mapFilePath2Line==null||mapFilePath2Line.size()==0) {
			return;
		}
		for(Entry<String, Integer> entry:mapFilePath2Line.entrySet()) {
			JButton jButton = new JButton();
			jButton.setText(String.format("%s line:%d", entry.getKey(),entry.getValue()));
			panel_5.add(jButton);
		}
		panel_5.updateUI();
	}
}
