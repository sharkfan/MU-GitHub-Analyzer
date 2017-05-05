package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.OAuthService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.Git;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javancss.Javancss;

public class MainController implements Initializable {

	@FXML
	private TableView<UserDetails> tableUser;
	@FXML
	private TableColumn<UserDetails, String> columnProjectName;
	@FXML
	private TableColumn<UserDetails, String> columnContributors;
	@FXML
	private TableColumn<UserDetails, String> columnIssues;
	@FXML
	private TableColumn<UserDetails, String> columnPullRequests;
	@FXML
	private TableColumn<UserDetails, String> columnReleases;
	@FXML
	private TableColumn<UserDetails, String> columnCommits;
	@FXML
	private TableColumn<UserDetails, String> columnLastCommit;
	@FXML
	private TableColumn<UserDetails, String> columnClasses;
	@FXML
	private TableColumn<UserDetails, String> columnMethods;
	@FXML
	private TableColumn<UserDetails, String> columnNumberofLOC;
	@FXML
	private TableColumn<UserDetails, String> columnLastUpdate;
	//@FXML
	//private TableColumn<UserDetails, String> columnLanguage;
	@FXML
	private TextArea usernamefield;
	@FXML
	private TextArea feedback;
	@FXML
	private TextField loginusernamefield;
	@FXML
	private TextField loginpasswordfield;
    @FXML
    private Button btnDelete;
    @FXML
	private ChoiceBox choiceBox;
    ObservableList<String> programmingLanguageList =
    		FXCollections.observableArrayList("Java");
	//Initialize observable list to hold database data
	private ObservableList<UserDetails> data;

	private Connection connection;

	public class JacocoMetrics {
		long numberOfClasses;
		long numberOfMethods;
		long numberOfLOC;
	}

	public MainController() {
		System.out.println("Ctor maincontroller");
		createDatabase();
		createTable();
	}
	
	/**
	 * Calling loadDataFromDatabase() here is necessary for the UI to show the
	 * mySQL database.
	 * 
	 * @param url
	 *            The location used to resolve relative paths for the root
	 *            object, or null if the location is not known.
	 * 
	 * @param rb
	 *            The resources used to localize the root object, or null if the
	 *            root object was not localized.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		loadDataFromDatabase();
		choiceBox.setMaxWidth(Control.USE_COMPUTED_SIZE);
		choiceBox.setItems(programmingLanguageList);
	}

	/**
	 * Allows access to the GitHub API.
	 * 
	 * @param login
	 *            GitHub user name or e-mail address.
	 * 
	 * @param password
	 *            The password for that GitHub user name or e-mail address.
	 * 
	 * @return Connection to GitHub.
	 */
	public GitHub initializeGitHubApi(String login, String password) {
		try {
			return GitHub.connectUsingPassword(login, password);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Not used. Deletes folders during the static analysis part of
	 * generateProjectMetrics().
	 * 
	 * @param folder
	 *            The directory to be deleted.
	 */
	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	/**
	 * Imports all of GitHub user's (Java) repositories to the user's machine,
	 * uses Javancss to find the number of Classes, Methods, and LOC.
	 * 
	 * @param user
	 *            The GitHub User.
	 * 
	 * @param generatedMetrics
	 *            A HashMap that holds the number of Classes, Methods, and LOC.
	 */
	public void generateProjectMetrics(String user, HashMap<String, JacocoMetrics> generatedMetrics) {
		int count = 1;
		OAuthService oauthService = new OAuthService();

		// Connect to github
		if (loginusernamefield.getText().isEmpty() || loginpasswordfield.getText().isEmpty()) {
			// TODO add alert dialog or attention-grabbing effect for the text fields here
			return;
		}
		String login = loginusernamefield.getText();
		String password = loginpasswordfield.getText();
		oauthService.getClient().setCredentials(login, password);

		// Print repository
		RepositoryService service = new RepositoryService();
		try {
			for (Repository repo : service.getRepositories(user)) {
				try {
					if (repo != null && repo.getLanguage() != null && repo.getLanguage().equalsIgnoreCase("Java")) {
						System.out.println(count++ + " URL " + repo.getCloneUrl() + "   " + repo.getLanguage());

						String filename = "GitLandingDir";
						String workingDirectory = System.getProperty("user.dir");
						String absoluteFilePath = "";

						absoluteFilePath = workingDirectory + File.separator + filename;

						System.out.println("Final filepath : " + absoluteFilePath);

						File landingDir = new File(absoluteFilePath);

						if (!landingDir.exists()) {
							if (landingDir.mkdirs()) {
								System.out.println("Directory is created!");
							} else {
								System.out.println("Failed to create directory!");
							}
						}

						try {
							FileUtils.forceDelete(landingDir);
						} catch (IOException e) {
							System.out.println("force delete failed for " + landingDir);
							continue;
						}
						System.out.println("landing dir deleted");

						Git git = Git.cloneRepository().setURI(repo.getCloneUrl()).setDirectory(landingDir).call();

						System.out.println("\nfetch completed\n");

						// metricsGenerator
						String repoName = repo.getCloneUrl();
						repoName = repoName.replace("https://github.com/", "");
						repoName = repoName.replace(".git", "");

						String newRepoName = repoName;
						System.out.println("new repo name " + newRepoName);

						String[] dirs = new String[1];

						dirs[0] = absoluteFilePath;

						git.getRepository().close();

						JacocoMetrics metrics = new JacocoMetrics();
						try {
							Javancss metricsGenerator = new Javancss(dirs);

							metrics.numberOfClasses = metricsGenerator.getObjectMetrics().size();
							metrics.numberOfMethods = metricsGenerator.getFunctionMetrics().size();
							metrics.numberOfLOC = metricsGenerator.getNcss();

							generatedMetrics.put(newRepoName, metrics);
						} catch (Exception e) {
							continue;
						}

					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getNumberOfContributors(GitHub github, String repoName) {
		try {

			GHRepository repository = github.getRepository(repoName);

			PagedIterable<GHRepository.Contributor> contributors = repository.listContributors();
			return contributors.asList().size();
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("Exception in GH api ");
		}
		return 0;
	}

	public int getNumberOfIssues(GitHub github, String repoName) {
		try {

			GHRepository repository = github.getRepository(repoName);

			return repository.getIssues(GHIssueState.OPEN).size() + repository.getIssues(GHIssueState.CLOSED).size();
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("Exception in GH api ");
		}
		return 0;
	}

	public int getNumberOfPullRequests(GitHub github, String repoName) {
		try {

			GHRepository repository = github.getRepository(repoName);

			return (repository.getPullRequests(GHIssueState.CLOSED).size()
					+ repository.getPullRequests(GHIssueState.OPEN).size());
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("Exception in GH api ");
		}
		return 0;
	}

	public int getNumberOfReleases(GitHub github, String repoName) {
		try {

			GHRepository repository = github.getRepository(repoName);

			PagedIterable<GHTag> tags = repository.listTags();
			return tags.asList().size();

		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("Exception in GH api ");
		}
		return 0;
	}

	public int getNumberOfCommits(GitHub github, String repoName) {
		try {

			GHRepository repository = github.getRepository(repoName);

			PagedIterable<GHCommit> commits = repository.listCommits();
			return commits.asList().size();
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("Exception in GH api ");
		}
		return 0;
	}

	public java.util.Date getLatestCommit(GitHub github, String repoName) {
		try {

			GHRepository repository = github.getRepository(repoName);

			PagedIterable<GHCommit> commits = repository.listCommits();

			return commits.asList().get(0).getCommitDate();
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("Exception in GH api ");
		}
		return null;
	}
	
	/**
	 * Finds data about every repository of every user provided via UI
	 * and enters it in the database.
	 * 
	 * @param event
	 *            Clicking the button that says "Search".
	 */
	public void getMetrics(ActionEvent event) throws IOException, SQLException {

		// connect to github using koshuke api
		if (loginusernamefield.getText().isEmpty() || loginpasswordfield.getText().isEmpty()) {
			// to do: add alert dialog or attention-grabbing effect for the text
			// fields here
			return;
		}
		String login = loginusernamefield.getText();
		String password = loginpasswordfield.getText();

		//get user name list from the UI, parse it, and add it to a string array
		String usernames = usernamefield.getText();
		String[] arr = usernames.split("[^a-zA-Z0-9_-]+");

		// Run a loop to do the following for each user in the array of string
		for (int i = 0; i < arr.length; i++) {
			String user = arr[i];
			System.out.println("Getting repositories from " + arr[i]);

			long numberOfClasses = 0;
			long numberOfMethods = 0;
			long numberOfLOC = 0;

			HashMap<String, JacocoMetrics> generatedMetrics = new HashMap<String, JacocoMetrics>();

			generateProjectMetrics(user, generatedMetrics);

			System.out.println(" Number of Classes: " + numberOfClasses);
			System.out.println(" Number of Methods: " + numberOfMethods);
			System.out.println(" Number of Lines: " + numberOfLOC);

			GitHub github = initializeGitHubApi(login, password);

			RepositoryService service = new RepositoryService();

			for (Repository repo : service.getRepositories(user)) {
				if (repo.getLanguage() != null && repo.getLanguage().equalsIgnoreCase("Java")) {

					String repoURL = repo.getCloneUrl();

					// Adjust repoURL to query git using github api
					String repoName = repoURL;
					repoName = repoName.replace("https://github.com/", "");
					repoName = repoName.replace(".git", "");

					String newRepoName = repoName;
					System.out.println("new repo name " + newRepoName);
					feedback.appendText(user + "'s project: " + newRepoName + " added to candidate pool\n");

					try {
						int numberOfContributors = getNumberOfContributors(github, newRepoName);
						int numberOfIssues = getNumberOfIssues(github, newRepoName);
						int numberOfPullRequests = getNumberOfPullRequests(github, newRepoName);
						int numberOfReleases = getNumberOfReleases(github, newRepoName);
						int numberOfCommits = getNumberOfCommits(github, newRepoName);

						java.util.Date lastCommitDate = getLatestCommit(github, newRepoName);

						System.out.println("# of contributors " + numberOfContributors + " \n");
						System.out.println("# of issues " + numberOfIssues + " \n");
						System.out.println("# of pull requests " + numberOfPullRequests + " \n");
						System.out.println("# of releases " + numberOfReleases + " \n");
						System.out.println("# of Commits " + numberOfCommits + " \n");

						System.out.println("last commit " + lastCommitDate);

						JacocoMetrics metrics = generatedMetrics.get(newRepoName);

						if (metrics == null) {
							System.out.println(" No generated metrics for " + repoURL);
							continue;
						}
						System.out.println("# of classes " + metrics.numberOfClasses);
						System.out.println("# of methods " + metrics.numberOfMethods);
						System.out.println("# of LOC " + metrics.numberOfLOC);

						insertProjectRecords(repoURL, numberOfContributors, numberOfIssues, numberOfPullRequests,
								numberOfReleases, numberOfCommits, lastCommitDate, metrics.numberOfClasses,
								metrics.numberOfMethods, metrics.numberOfLOC);
					} catch (Exception e) {
						System.out.println(" exception111 " + e.getStackTrace());
					}
				}
			}
			//refresh the UI table
			loadDataFromDatabase();
		}
	}

	/**
	 * Allows access to the mySQL database.
	 * 
	 * @param dbname
	 *            The name of the database (mugha).
	 * 
	 * @return Connection to the database.
	 */
	public Connection connectToDB(String dbname) {
		// JDBC driver name and database URL
		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://localhost/";

		// Database credentials
		// TODO resolve potential for these credentials to not match up with other users.
		final String USER = "root";
		final String PASS = "";
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			connection = (Connection) DriverManager.getConnection(DB_URL + dbname, USER, PASS);
			return connection;
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * Creates a mySQL database called mugha if none exists.
	 */
	public void createDatabase() {
		Connection conn = connectToDB("");

		Statement stmt = null;
		try {
			System.out.println("Creating database...");
			stmt = (Statement) conn.createStatement();
			String sql = "CREATE DATABASE IF NOT EXISTS mugha";
			stmt.executeUpdate(sql);
			System.out.println("Database created successfully...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		}

	}

	/**
	 * Creates a table within the mugha database if none exists.
	 */
	public void createTable() {
		Connection conn = connectToDB("mugha");
		PreparedStatement create;
		try {
			create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS project"
					+ "(ProjectURL VARCHAR(200),NumberOfContributors INT,"
					+ "NumberOfIssues INT, NumberOfPullRequests INT, " + "NumberOfReleases INT, NumberOfCommits INT, "
					+ "LastCommitDate datetime, NumberOfClasses long, NumberOfMethods long, "
					+ "NumberOfLOC long, LastUpdate datetime default now(), PRIMARY KEY (ProjectURL) )");
			create.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(" PROJECT Table created...");
	}

	/**
	 * Puts data about the GitHub repositories in the database.
	 */
	public void insertProjectRecords(String projectURL, int numberOfContributors, int issues, int numberOfPullRequests,
			int numberOfReleases, int numberOfCommits, java.util.Date latestCommitDate, long numberOfClasses,
			long numberOfMethods, long numberOfLOC) {
		try {

			String sql = "REPLACE INTO project (ProjectURL, NumberOfContributors, "
					+ "NumberOfIssues, NumberOfPullRequests, " + "NumberOfReleases, NumberOfCommits, "
					+ "LastCommitDate, NumberOfClasses, NumberOfMethods, NumberOfLOC) " + "VALUES"
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, projectURL);
			pstmt.setInt(2, numberOfContributors);
			pstmt.setInt(3, issues);
			pstmt.setInt(4, numberOfPullRequests);
			pstmt.setInt(5, numberOfReleases);
			pstmt.setInt(6, numberOfCommits);
			pstmt.setTimestamp(7, new java.sql.Timestamp(latestCommitDate.getTime()));
			pstmt.setLong(8, numberOfClasses);
			pstmt.setLong(9, numberOfMethods);
			pstmt.setLong(10, numberOfLOC);

			pstmt.executeUpdate();
			pstmt.close();

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes data from the mugha database and shows it in the UI table
	 */
	@FXML
	private void loadDataFromDatabase() {
		try {
			Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/mugha", "root", "");
			data = FXCollections.observableArrayList();
			// Execute query and store result in a resultset
			ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM mugha.project");
			while (rs.next()) {
				// get string from db,whichever way
				data.add(new UserDetails(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9),
						rs.getString(10), rs.getString(11)));
			}

		} catch (SQLException ex) {
			System.err.println("Error" + ex);
		}

		// Set cell value factory to tableview.
		// NB.PropertyValue Factory must be the same with the one set in model class.
		columnProjectName.setCellValueFactory(new PropertyValueFactory<>("ProjectURL"));
		columnContributors.setCellValueFactory(new PropertyValueFactory<>("Contributors"));
		columnIssues.setCellValueFactory(new PropertyValueFactory<>("Issues"));
		columnPullRequests.setCellValueFactory(new PropertyValueFactory<>("PullRequests"));
		columnReleases.setCellValueFactory(new PropertyValueFactory<>("Releases"));
		columnCommits.setCellValueFactory(new PropertyValueFactory<>("Commits"));
		columnLastCommit.setCellValueFactory(new PropertyValueFactory<>("LastCommitDate"));
		columnClasses.setCellValueFactory(new PropertyValueFactory<>("Classes"));
		columnMethods.setCellValueFactory(new PropertyValueFactory<>("Methods"));
		columnNumberofLOC.setCellValueFactory(new PropertyValueFactory<>("NumberofLOC"));
		columnLastUpdate.setCellValueFactory(new PropertyValueFactory<>("LastUpdate"));
		// columnLanguage.setCellValueFactory(newPropertyValueFactory<>("Language"));

		tableUser.setItems(null);
		tableUser.setItems(data);
	}

	/**
	 * Deletes an item from the database if its associated row is selected in the UI.
	 * 
	 * @param event
	 *            Clicking the Remove button.
	 */
	public void onDeleteItem(ActionEvent event) throws SQLException {
		if (tableUser.getSelectionModel().selectedItemProperty().getValue().getProjectURL() != null) {

			// Declare a DELETE statement
			String updateStmt = "DELETE FROM mugha.project where ProjectURL = \""
					+ tableUser.getSelectionModel().selectedItemProperty().getValue().getProjectURL() + "\"";
			// Execute UPDATE operation
			try {
				PreparedStatement dstmt = connection.prepareStatement(updateStmt);
				dstmt.executeUpdate();
				dstmt.close();
				loadDataFromDatabase();
			} catch (SQLException e) {
				System.out.print("Error occurred while DELETE Operation: " + e);
				throw e;
			}
		} else {return;}
	}
}
