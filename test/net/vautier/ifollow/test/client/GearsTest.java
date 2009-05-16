package net.vautier.ifollow.test.client;

import java.util.Date;

import net.vautier.ifollow.test.client.util.RandomText;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Label;

public class GearsTest implements EntryPoint {
	
	private final String db_name = "ifollow_test_fts";
	
	private Database db;
	private TextBox countField;
	private TextBox searchField;
	private Label progressLabel;

	@Override
	public void onModuleLoad() {
		initializeGui();
		initializeStore();
	}

	/**
	 * Initializes GUI elements.
	 */
	protected void initializeGui() {
		final Button populateButton = new Button("Populate");
		final Button searchButton = new Button("Search");
		final Button resetButton = new Button("Reset (empty store)");

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

		RootPanel.get().add(resetButton);

		populateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				long start = new Date().getTime();
				long milliseconds;
				populateStore();
				milliseconds = new Date().getTime() - start;
				progressLabel.setText( new Double( milliseconds ).toString() + " ms" );
			}
		});
		
		searchButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				long start = new Date().getTime();
				long milliseconds;
				int count = searchStore();
				milliseconds = new Date().getTime() - start;
				progressLabel.setText( new Integer(count).toString() + " rows found in " + new Double( milliseconds ).toString() + " ms" );
			}
		});

		resetButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if ( resetStore() ) {
					progressLabel.setText( "Store emptied." );
				} else {
					progressLabel.setText( "Could not empty store." );
				}
			}
		});
	}

	/**
	 * Initializes Gears data store.
	 */
	protected void initializeStore() {
		db = Factory.getInstance().createDatabase();
		db.open("gears-stress-test");
	
		try {
			// reset store if it exists
			db.execute("DROP TABLE IF EXISTS " + db_name);
			db.execute(
					"CREATE VIRTUAL TABLE " + db_name + " USING fts2(" +
					"	id		INTEGER PRIMARY KEY AUTOINCREMENT" +
					",	data	TEXT" +
					")");
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets Gears data store.
	 */
	protected boolean resetStore() {
		try {
			db.execute("DELETE FROM " + db_name );
			return true;
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Searches database.
	 */
	protected int searchStore() {
		// Retrieve string to search for in database
		String searchString = searchField.getText();
		try {
			ResultSet rs = db.execute("SELECT data FROM " + db_name + " WHERE " + db_name + " MATCH ?", searchString);
			int i = 0;
			while (rs.isValidRow()) {
				i++;
				rs.next();
			}
			rs.close();
			return i;
		} catch (DatabaseException e) {
			e.printStackTrace();
			return 0;
		}		
	}
	
	/**
	 * Populates database.
	 */
	protected void populateStore() {
		// Retrieve number of entries to add (user-defined)
		int count = Integer.parseInt( countField.getText() );
		String sentences[] = RandomText.randomSentences(count);
		// Insert random entries into database
		try {
			db.execute("BEGIN");
			for (int i=0; i<count; i++) {
				db.execute("INSERT INTO " + db_name + " (data) VALUES (?)", sentences[i]);
			}
			db.execute("COMMIT");
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

}
