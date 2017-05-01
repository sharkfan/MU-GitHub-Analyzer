package application;

//This class acts as a model class,holding getters,setters and properties

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author admin
 */
public class UserDetails {

	private final StringProperty ProjectURL;
	private final StringProperty Contributors;
	private final StringProperty Issues;
	private final StringProperty PullRequests;
	private final StringProperty Releases;
	private final StringProperty Commits;
	private final StringProperty LastCommitDate;
	private final StringProperty Classes;
	private final StringProperty Methods;
	private final StringProperty NumberofLOC;
	private final StringProperty LastUpdate;
	// private final StringProperty Language;

	// Default constructor
	public UserDetails(String ProjectURL, String Contributors, String Issues, String PullRequests, String Releases, String Commits, String LastCommitDate, String Classes, String Methods, String NumberofLOC, String LastUpdate/*String Language*/) {
		this.ProjectURL = new SimpleStringProperty(ProjectURL);
		this.Contributors = new SimpleStringProperty(Contributors);
		this.Issues = new SimpleStringProperty(Issues);
		this.PullRequests = new SimpleStringProperty(PullRequests);
		this.Releases = new SimpleStringProperty(Releases);
		this.Commits = new SimpleStringProperty(Commits);
		this.LastCommitDate = new SimpleStringProperty(LastCommitDate);
		this.Classes = new SimpleStringProperty(Classes);
		this.Methods = new SimpleStringProperty(Methods);
		this.NumberofLOC = new SimpleStringProperty(NumberofLOC);
		this.LastUpdate = new SimpleStringProperty(LastUpdate);
		// this.Language = new SimpleStringProperty(Language);
	}

	// Getters
	public String getProjectURL() {
		return ProjectURL.get();
	}

	public String getContributors() {
		return Contributors.get();
	}
	
	public String getIssues() {
		return Issues.get();
	}
	
	public String getPullRequests() {
		return PullRequests.get();
	}
	
	public String getReleases() {
		return Releases.get();
	}
	
	public String getCommits() {
		return Commits.get();
	}
	
	public String getLastCommitDate() {
		return LastCommitDate.get();
	}
	public String getClasses() {
		return Classes.get();
	}
	public String getMethods() {
		return Methods.get();
	}
	public String getNumberofLOC() {
		return NumberofLOC.get();
	}
	public String getLastUpdate() {
		return LastUpdate.get();
	}

/*
 * public String getLanguage() { 
 * 		return Language.get(); 
 * }
 */

	// Setters
	public void setProjectURL(String value) {
		ProjectURL.set(value);
	}

	public void setContributors(String value) {
		Contributors.set(value);
	}
	public void setIssues(String value) {
		Issues.set(value);
	}
	public void setPullRequests(String value) {
		PullRequests.set(value);
	}
	public void setReleases(String value) {
		Releases.set(value);
	}
	public void setCommits(String value) {
		Commits.set(value);
	}
	public void setLastCommitDate(String value) {
		LastCommitDate.set(value);
	}
	public void setClasses(String value) {
		Classes.set(value);
	}
	public void setMethods(String value) {
		Methods.set(value);
	}
	public void setNumberofLOC(String value) {
		NumberofLOC.set(value);
	}
	public void setLastUpdate(String value) {
		LastUpdate.set(value);
	}

/*
 * public void setLanguage(String value) {
 * 		Language.set(value);
 * }
 */

	// Property values
	public StringProperty ProjectURLProperty() {
		return ProjectURL;
	}

	public StringProperty ContributorsProperty() {
		return Contributors;
	}
	public StringProperty IssuesProperty() {
		return Issues;
	}
	public StringProperty PullRequestsProperty() {
		return PullRequests;
	}
	public StringProperty ReleasesProperty() {
		return Releases;
	}
	public StringProperty CommitsProperty() {
		return Commits;
	}
	public StringProperty LastCommitDateProperty() {
		return LastCommitDate;
	}
	public StringProperty ClassesProperty() {
		return Classes;
	}
	public StringProperty MethodsProperty() {
		return Methods;
	}
	public StringProperty NumberofLOCProperty() {
		return NumberofLOC;
	}
	public StringProperty LastUpdateProperty() {
		return LastUpdate;
	}

/*
 * public StringProperty LanguageProperty() { 
 * 		return Language; 
 * }
 */
}
