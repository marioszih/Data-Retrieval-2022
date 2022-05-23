package RetrievalApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;

import javax.swing.Box;
import javax.swing.JButton;

public class ResultFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainWindow mainw = MainWindow.getSingletonView();
	private JFrame frame;
	private Box box;
	private int end;
	private int totalPagesNeeded;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	
	public void addItemsToBox() {
		Query q = null;
		String history = "";
		
		if(((String)mainw.getComboBox().getSelectedItem()).toLowerCase().equals("")){
			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "summary", "critic","rating", "genre",
																				"director", "writer","cast", "runtime",
																				"tomato rating", "audience rating"} ,new StandardAnalyzer());
			history+="No specified field, ";
			try {
				q = (BooleanQuery)queryParser.parse(mainw.getTextField().getText());
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else {
			history = history + ((String)mainw.getComboBox().getSelectedItem()).toLowerCase() + ", ";
			QueryParser parser = new QueryParser(((String)mainw.getComboBox().getSelectedItem()).toLowerCase(),new StandardAnalyzer());
			try {
				q = parser.parse(mainw.getTextField().getText());
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		history += mainw.getTextField().getText();
		if(mainw.getPage() == 1) {
			try {
	            BufferedWriter out = new BufferedWriter(new FileWriter("history.txt", true)); 
	            out.write(history);
	            out.newLine();
	            out.close(); 
			} catch (Exception e) {
				System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
			}
		}
		try {
			
			/**
			 * this method was taken from the link bellow. Use ctrl+left click to open in browser.
			 * @param https://www.bitspedia.com/2015/07/lucene-highlighter-tutorial-with-example.html
			 */
			TopDocs results = mainw.getSearcher().search(q,7600);
						
			//Uses HTML &lt;B&gt;&lt;/B&gt; tag to highlight the searched terms
			Formatter formatter = new SimpleHTMLFormatter();
			
			//It scores text fragments by the number of unique query terms found
			//Basically the matching score in layman terms
			QueryScorer scorer = new QueryScorer(q);
	         
			//used to markup highlighted terms found in the best sections of a text
			Highlighter highlighter = new Highlighter(formatter, scorer);
			highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
	        
			//It breaks text up into same-size texts but does not split up spans
			Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 200);
	        
			//set fragmenter to highlighter
			highlighter.setTextFragmenter(fragmenter);
			
			ScoreDoc[] scoreDoc=results.scoreDocs;
			int totalHits = Math.toIntExact(results.totalHits.value);
			totalPagesNeeded = (int)Math.ceil((double)totalHits/mainw.getResultSize()) ;
			end = Math.min(mainw.getPage()*mainw.getResultSize(), totalHits);
			if(scoreDoc.length!=0) {
				for(int i = (mainw.getPage()-1)*mainw.getResultSize() ; i<end;i++) {
					int docid = results.scoreDocs[i].doc;
					
					Document doc = mainw.getReader().document(scoreDoc[i].doc);
					String title = doc.get("title");
					String link = doc.get("link");
					JLabel hyper = hyperlinkMaker(title, "https://www.rottentomatoes.com" + link);
					hyper.setFont(new Font(hyper.getFont().getName(), Font.PLAIN, hyper.getFont().getSize()+4));
					box.add(hyper);
					
					String selectedField = ((String) mainw.getComboBox().getSelectedItem()).toLowerCase();
					
					String summary = doc.get("summary");
					String critics = doc.get("critic");
					String rating = doc.get("rating");
					String genre = doc.get("genre");
					String director = doc.get("director");
					String cast = doc.get("cast");
					String writer = doc.get("writer");
					String runtime = doc.get("runtime");
					String tomatoR = doc.get("tomato rating");
					String audienceR = doc.get("audience rating");
					
					//Create token stream
					@SuppressWarnings("deprecation")
					TokenStream stream1 = TokenSources.getAnyTokenStream((IndexReader)mainw.getReader(), docid, "summary", new StandardAnalyzer());
					@SuppressWarnings("deprecation")
					TokenStream stream2 = TokenSources.getAnyTokenStream((IndexReader)mainw.getReader(), docid, "critic", new StandardAnalyzer());
					@SuppressWarnings("deprecation")
					TokenStream stream3 = TokenSources.getAnyTokenStream((IndexReader)mainw.getReader(), docid, "rating", new StandardAnalyzer());
					@SuppressWarnings("deprecation")
					TokenStream stream4 = TokenSources.getAnyTokenStream((IndexReader)mainw.getReader(), docid, "genre", new StandardAnalyzer());
					@SuppressWarnings("deprecation")
					TokenStream stream5 = TokenSources.getAnyTokenStream((IndexReader)mainw.getReader(), docid, "director", new StandardAnalyzer());
					@SuppressWarnings("deprecation")
					TokenStream stream6 = TokenSources.getAnyTokenStream((IndexReader)mainw.getReader(), docid, "writer", new StandardAnalyzer());
					@SuppressWarnings("deprecation")
					TokenStream stream7 = TokenSources.getAnyTokenStream((IndexReader)mainw.getReader(), docid, "cast", new StandardAnalyzer());
					@SuppressWarnings("deprecation")
					TokenStream stream8 = TokenSources.getAnyTokenStream((IndexReader)mainw.getReader(), docid, "runtime", new StandardAnalyzer());
					@SuppressWarnings("deprecation")
					TokenStream stream9 = TokenSources.getAnyTokenStream((IndexReader)mainw.getReader(), docid, "tomato rating", new StandardAnalyzer());
					@SuppressWarnings("deprecation")
					TokenStream stream10 = TokenSources.getAnyTokenStream((IndexReader)mainw.getReader(), docid, "audience rating", new StandardAnalyzer());
					
					 
					String[] frags1 = {};
					String[] frags2 = {};
					String[] frags3 = {};
					String[] frags4 = {};
					String[] frags5 = {};
					String[] frags6 = {};
					String[] frags7 = {};
					String[] frags8 = {};
					String[] frags9 = {};
					String[] frags10 = {};
					//Get highlighted text fragments
					try {
						if(selectedField.equals("summary")) {
							frags1 = highlighter.getBestFragments(stream1, summary, 10);
						}
						else if(selectedField.equals("critic")) {
							frags2 = highlighter.getBestFragments(stream2, critics, 10);
						}
						else if(selectedField.equals("rating")) {
							frags3 = highlighter.getBestFragments(stream3, rating, 10);
						}
						else if(selectedField.equals("genre")) {
							frags4 = highlighter.getBestFragments(stream4, genre, 10);
						}
						else if(selectedField.equals("director")) {
							frags5 = highlighter.getBestFragments(stream5, director, 10);
						}
						else if(selectedField.equals("writer")) {
							frags6 = highlighter.getBestFragments(stream6, writer, 10);
						}
						else if(selectedField.equals("cast")) {
							frags7 = highlighter.getBestFragments(stream7, cast, 10);
						}
						else if(selectedField.equals("runtime")) {
							frags8 = highlighter.getBestFragments(stream8, runtime, 10);
						}
						else if(selectedField.equals("tomato rating")) {
							frags9 = highlighter.getBestFragments(stream9, tomatoR, 10);
						}
						else if(selectedField.equals("audience rating")) {
							frags10 = highlighter.getBestFragments(stream10, audienceR, 10);
						}
					} catch (InvalidTokenOffsetsException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					String finalSummary = "";
					finalSummary = filler(frags1,finalSummary, summary);
					String finalCritic = "";
					finalCritic = filler(frags2,finalCritic, critics);
					String finalRating = "";
					finalRating = filler(frags3,finalRating, rating);
					String finalGenre = "";
					finalGenre = filler(frags4,finalGenre, genre);
					String finalDirector = "";
					finalDirector = filler(frags5,finalDirector, director);
					String finalWriter = "";
					finalWriter = filler(frags6,finalWriter, writer);
					String finalCast = "";
					finalCast = filler(frags7,finalCast, cast);
					String finalRuntime = "";
					finalRuntime = filler(frags8,finalRuntime, runtime);
					String finalTomatoR = "";
					finalTomatoR = filler(frags9,finalTomatoR, tomatoR);
					String finalAudienceR = "";
					finalAudienceR = filler(frags10,finalAudienceR, audienceR);
					
					JLabel ratingLabel = new JLabel("<html>Rating:"+finalRating + " Director:" + finalDirector +"</html>\n"); //we place the <html> string so the message gets highlighted
					ratingLabel.setFont(new Font(ratingLabel.getFont().getName(), Font.PLAIN, ratingLabel.getFont().getSize()+2));
					box.add(ratingLabel);
					
					JLabel genreLabel = new JLabel("<html>Genres: "+ finalGenre + "</html>\n");
					genreLabel.setFont(new Font(genreLabel.getFont().getName(), Font.PLAIN, genreLabel.getFont().getSize()+2));
					box.add(genreLabel);
					
					JLabel castLabel = new JLabel("<html>Cast: "+ finalCast + "</html>\n");
					castLabel.setFont(new Font(castLabel.getFont().getName(), Font.PLAIN, castLabel.getFont().getSize()+2));
					box.add(castLabel);
					
					JLabel writerLabel = new JLabel("<html>Writer: "+ finalWriter + "</html>\n");
					writerLabel.setFont(new Font(writerLabel.getFont().getName(), Font.PLAIN, writerLabel.getFont().getSize()+2));
					box.add(writerLabel);
					
					JLabel runtimeLabel = new JLabel("<html>Runtime: "+ finalRuntime + "</html>\n");
					runtimeLabel.setFont(new Font(runtimeLabel.getFont().getName(), Font.PLAIN, runtimeLabel.getFont().getSize()+2));
					box.add(runtimeLabel);
					
					JLabel ratingsMeterLabel = new JLabel("<html>Tomato critics score: "+ finalTomatoR+"    " +"Audience critics score: "+ finalAudienceR +"</html>\n");
					ratingsMeterLabel.setFont(new Font(ratingsMeterLabel.getFont().getName(), Font.PLAIN, ratingsMeterLabel.getFont().getSize()+2));
					box.add(ratingsMeterLabel);
					
					JLabel summaryLabel = new JLabel("<html>Summary:\n"+finalSummary+"</html>\n"); //we place the <html> string so the message gets highlighted
					summaryLabel.setFont(new Font(summaryLabel.getFont().getName(), Font.PLAIN, summaryLabel.getFont().getSize()+2));
					box.add(summaryLabel);
					
					JLabel criticLabel = new JLabel("<html>Critics wrote:\n"+finalCritic+"</html>\n");
					criticLabel.setFont(new Font(criticLabel.getFont().getName(), Font.PLAIN, criticLabel.getFont().getSize()+2));
					box.add(criticLabel);
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "No results for that search");
				return;
			}
		}catch(IOException exc){
			exc.printStackTrace();
		}
	}
	
	public String filler(String[] frags,String finalString,String s) {
		for(String frag:frags) {
			finalString += frag;
		}
		if(finalString.equals("")) {
			finalString = s;
		}
		return finalString;
	}
	
	public void changePage(String changer) {
		if(changer == "up") {
			if(mainw.getPage() == totalPagesNeeded) {
				JOptionPane.showMessageDialog(frame, "There are no more pages to show or you reached the end point");
			}
			else {
				frame.dispose();
				mainw.setPage(mainw.getPage()+1);
				@SuppressWarnings("unused")
				ResultFrame newFrame = new ResultFrame();
			}
		}
		else if(changer == "down") {
			if(mainw.getPage()!=1) {
				frame.dispose();
				mainw.setPage(mainw.getPage()-1);
				@SuppressWarnings("unused")
				ResultFrame newFrame = new ResultFrame();
			}
			else {
				JOptionPane.showMessageDialog(frame, "Reached first page of limited results");
			}
		}
	}
	/**
	 * this method was taken from the link bellow. Use ctrl+left click to open in browser.
	 * @param https://www.codejava.net/java-se/swing/how-to-create-hyperlink-with-jlabel-in-java-swing
	 */
	public JLabel hyperlinkMaker(String alias, String link) {
		JLabel hyperlink = new JLabel(alias);
		hyperlink.setForeground(Color.BLUE.darker());
		hyperlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		hyperlink.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        // the user clicks on the label
		    	try {
		    		Desktop.getDesktop().browse(new URI(link));
		        } catch (IOException | URISyntaxException e1) {
		            e1.printStackTrace();
		        }
		    }
		    @Override
		    public void mouseEntered(MouseEvent e) {
		        // the mouse has entered the label
		    	hyperlink.setText("<html><a href=''>" + alias + "</a></html>");
		    }
		 
		    @Override
		    public void mouseExited(MouseEvent e) {
		        // the mouse has exited the label
		    	hyperlink.setText(alias);
		    }
		});
		return hyperlink;
	}
	
	public ResultFrame() {
		try {
			UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) { }
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100, 100, 1366, 768);
		frame.setVisible(true);
		frame.setResizable(false);
		
		box = Box.createVerticalBox();
		JScrollPane scrollableBox = new JScrollPane(box);  
		scrollableBox.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollableBox.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollableBox.getVerticalScrollBar().setUnitIncrement(16);
		frame.getContentPane().add(scrollableBox);
		
		addItemsToBox();
		
		JButton next = new JButton("Next Page");
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changePage("up");
			}
		});
		frame.getContentPane().add(next, BorderLayout.NORTH);
		
		JButton previous = new JButton("Previous Page");
		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changePage("down");
			}
		});
		frame.getContentPane().add(previous, BorderLayout.SOUTH);
	}
}