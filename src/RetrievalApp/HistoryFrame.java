package RetrievalApp;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;

public class HistoryFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainWindow mainw = MainWindow.getSingletonView();

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public HistoryFrame() {
		try {
			UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) { }
		JFrame historyFrame = new JFrame("Search History");
		historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		historyFrame.setBounds(200, 200, 400, 400);
		
		JScrollPane scrollableBox = new JScrollPane();  
		scrollableBox.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollableBox.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel panel = new JPanel();
		scrollableBox.setViewportView(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		Scanner scannerOfFile = null;
		try {
			File historyFile = new File("history.txt");
			scannerOfFile = new Scanner(historyFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(mainw.getFrame(), "Nothing has been searched yet");
			return;
		}
		while(scannerOfFile.hasNextLine()) {
			String lastSearch = scannerOfFile.nextLine();
			JButton searchAgain = new JButton(lastSearch);
			searchAgain.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainw.setPage(1);
					@SuppressWarnings("unused")
					ResultFrame res = new ResultFrame();
				}
			});
			panel.add(searchAgain);					
			JLabel line = new JLabel(lastSearch);
			line.setFont(new Font(line.getFont().getName(), Font.PLAIN, line.getFont().getSize()+4));
		}
		historyFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		historyFrame.getContentPane().add(scrollableBox);
		historyFrame.setVisible(true);
	}

}
