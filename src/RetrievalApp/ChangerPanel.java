package RetrievalApp;


import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;

public class ChangerPanel {

	private JFrame frame;
	private JTextField textField;
	private MainWindow mainw = MainWindow.getSingletonView();

	/**
	 * Launch the application.
	 */
	public ChangerPanel() {
		frame = new JFrame();
		frame.setBounds(100, 100, 220, 110);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 40, 184, 20);
		textField.setColumns(10);
		panel.add(textField);
		
		JLabel lblNewLabel = new JLabel("Enter the result number");
		lblNewLabel.setBounds(10, 11, 119, 20);
		panel.add(lblNewLabel);
		
		JButton applyButton = new JButton("Apply");
		applyButton.setBounds(127, 10, 67, 19);
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Integer.valueOf(textField.getText()) >= 1 && Integer.valueOf(textField.getText()) <= 10 ) {
					mainw.setResultSize(Integer.valueOf(textField.getText()));
					frame.dispose();
				}
				else {
					JOptionPane.showMessageDialog(frame, "Select a number between 1 and 10");
				}
			}
		});
		panel.add(applyButton);
	}
}
