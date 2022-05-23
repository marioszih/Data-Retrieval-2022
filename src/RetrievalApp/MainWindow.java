package RetrievalApp;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import com.opencsv.CSVReader;
import javax.swing.JButton;

public class MainWindow {

	private JFrame frame;
	private JTextField textField;
	private JComboBox<String> comboBox;
	private static MainWindow mainw;
	private String indexPath;
	private Directory dir;
	private DirectoryReader reader;
	private IndexSearcher searcher;
	private File oldHistoryFile = new File("history.txt");
	private int page = 1;
	private int resultSize = 10;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainw = new MainWindow();
					mainw.deleteHistoryFile();
					mainw.initialize();
					mainw.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static MainWindow getSingletonView()
	{
		if(mainw == null)
			mainw = new MainWindow();
		return mainw;
	}
	
	public JComboBox<String> getComboBox(){
		return comboBox;
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public JTextField getTextField() {
		return textField;
	}
	
	public DirectoryReader getReader() {
		return reader;
	}
	
	public IndexSearcher getSearcher() {
		return searcher;
	}
	
	public int getPage() {
		return page;
	}
	
	public int getResultSize() {
		return resultSize;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public void setResultSize(int resultSize) {
		this.resultSize = resultSize;
	}
	
	public void deleteHistoryFile() {
		oldHistoryFile.delete();
	}
	public void Indexer(){
		try{
			indexPath = ".\\indexPath";
			dir =FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(dir,iwc);
			//add documents
			try {
				CSVReader reader = new CSVReader(new FileReader(new File(getClass().getResource("edited_file.csv").getFile())));
				String line[];
				reader.readNext(); // First line contains the fields
				while((line = reader.readNext())!=null) {
					Document doc = new Document();
					doc.add(new TextField("link",line[0],Field.Store.YES));
					doc.add(new TextField("title",line[1],Field.Store.YES));
                    doc.add(new TextField("summary",line[2],Field.Store.YES));
                    doc.add(new TextField("critic",line[3],Field.Store.YES));
                    doc.add(new TextField("rating",line[4],Field.Store.YES));
                    doc.add(new TextField("genre",line[5],Field.Store.YES));
                    doc.add(new TextField("director",line[6],Field.Store.YES));
                    doc.add(new TextField("writer",line[7],Field.Store.YES));
                    doc.add(new TextField("cast",line[8],Field.Store.YES));
                    doc.add(new TextField("runtime",line[9].substring(0,line[9].length() - 2),Field.Store.YES)); //remove the .0 from the int so it can search
                    doc.add(new TextField("tomato rating",line[10],Field.Store.YES));
                    doc.add(new TextField("audience rating",line[11].substring(0,line[11].length() - 2),Field.Store.YES)); //same as line[9]
                    writer.addDocument(doc);
				}
			}catch(IOException ex) {
				ex.printStackTrace();
			}
			writer.forceMerge(1);
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			indexPath = ".\\indexPath";
			dir =FSDirectory.open(Paths.get(indexPath));
			reader = DirectoryReader.open(dir);
			searcher = new IndexSearcher(reader);
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) { }
		Indexer();
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 150);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		comboBox = new JComboBox<String>();
		comboBox.addItem("");
		comboBox.addItem("Title");
		comboBox.addItem("Summary");
		comboBox.addItem("Critic");
		comboBox.addItem("Rating");
		comboBox.addItem("Genre");
		comboBox.addItem("Director");
		comboBox.addItem("Writer");
		comboBox.addItem("Cast");
		comboBox.addItem("Runtime");
		comboBox.addItem("Tomato Rating");
		comboBox.addItem("Audience Rating");
		comboBox.setBounds(10, 11, 464, 22);
		panel.add(comboBox);
		
		textField = new JTextField();
		textField.setBounds(10, 50, 464, 20);
		panel.add(textField);
		textField.setColumns(10);
		
		JButton historyButton = new JButton("History");
		historyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				HistoryFrame result = new HistoryFrame();
				}
		});
		historyButton.setBounds(10, 77, 126, 23);
		panel.add(historyButton);
		
		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainw.setPage(1);
				@SuppressWarnings("unused")
				ResultFrame result = new ResultFrame();
				}
		});
		searchButton.setBounds(348, 77, 126, 23);
		panel.add(searchButton);
		
		JButton changeResultSizeButton = new JButton("Edit result number");
		changeResultSizeButton.setBounds(180, 77, 126, 23);
		changeResultSizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				ChangerPanel changer = new ChangerPanel();
				}
		});
		panel.add(changeResultSizeButton);
	}
}
