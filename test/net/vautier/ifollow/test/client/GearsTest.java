package net.vautier.ifollow.test.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Label;

public class GearsTest implements EntryPoint {
	
	private final String db_name = "ifollow_test_fts";
	private final String[] keywords = new String[] { "test-driven", "vautier", "unit-test", "mock" };
	
	private Database db;
	private TextBox countField;
	private TextBox searchField;
	private Label progressLabel;

	@Override
	public void onModuleLoad() {
		final Button populateButton = new Button("Populate");
		final Button searchButton = new Button("Search");

		countField = new TextBox();
		countField.setText("100");
		RootPanel.get().add(countField);
		RootPanel.get().add(populateButton);
		
		progressLabel = new Label("Give a number of entries to insert into db.");
		RootPanel.get().add(progressLabel);

		searchField = new TextBox();
		searchField.setText("");
		RootPanel.get().add(searchField);
		RootPanel.get().add(searchButton);
		
		db = Factory.getInstance().createDatabase();
		db.open("gears-stress-test");
	
		try {
			// create virtual table
			// TODO - Check for existence of table ("if not exists" not valid with virtual tables).
			db.execute(
					"create virtual table " + db_name + " using fts2(" +
					"	id		integer primary key autoincrement" +
					",	data	text" +
					")");
			db.execute("delete from " + db_name);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		populateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				long start = new Date().getTime();
				long milliseconds;
				populate();
				milliseconds = new Date().getTime() - start;
				progressLabel.setText( new Double( milliseconds ).toString() + " ms" );
			}
		});
		searchButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				long start = new Date().getTime();
				long milliseconds;
				int count = search();
				milliseconds = new Date().getTime() - start;
				progressLabel.setText( new Integer(count).toString() + " rows found in " + new Double( milliseconds ).toString() + " ms" );
			}
		});

	}
	
	/**
	 * Searches database.
	 */
	public int search() {
		// Retrieve string to search for in database
		String searchString = searchField.getText();
		try {
			ResultSet rs = db.execute("SELECT * FROM " + db_name + " WHERE " + db_name + " MATCH ?", searchString);
			return rs.getFieldCount();
		} catch (DatabaseException e) {
			e.printStackTrace();
			return 0;
		}		
	}
	
	/**
	 * Populates database.
	 */
	public void populate() {
		// Retrieve number of entries to add (user-defined)
		int count = Integer.parseInt( countField.getText() );
		String sentences[] = new String[ count ];
		for (int i=0; i<count; i++) {
			sentences[i] = randomSentence();
		}
		// Insert random entries into database
		try {
			db.execute("begin");
			for (int i=0; i<count; i++) {
				db.execute("insert into " + db_name + " (data) values (?)", sentences[i]);
//				db.execute("insert into ifollow_test (data) values (?)", randomSentence());
			}
			db.execute("commit");
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a random word
	 * @return	A random word.
	 */
	private String randomWord() {
		if ( Random.nextInt( 100 ) > 95 ) {
			return keywords[ Random.nextInt(keywords.length) ];
		} else {
			String s = "";
			for ( int i=0; i<Random.nextInt(14); i++) {
				Character c = new Character( (char) (Random.nextInt(26) + 97) );
				s += c;
			}
			return s;
		}		
	}
	
	/**
	 * Generates a random sentence.
	 * @return	A random sentence of 100 words or less.
	 */
	private String randomSentence() {
		String s = "";
		for ( int i=0; i<Random.nextInt(100); i++) {
			s += randomWord() + " ";
		}
		return s;
	}

}
