package librarysystem.gui;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import librarysystem.models.Admins;
import librarysystem.models.Employees;
import librarysystem.models.Model;
import librarysystem.resources.Resources;
import librarysystem.utils.Log;

public class Login extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception {
		handle(primaryStage);
	}
	
	private static void handle(Stage primaryStage) {
		primaryStage.setTitle(Resources.getString("library_system"));
		//Error Message if username or password was incorrect:
				String message = Resources.getString("username_or_password_was_wrong");
				Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
				alert.setHeaderText(null);
				alert.setTitle(Resources.getString("error"));
				   
		        HBox horizontalLayoutForUsernameOrEmail = new HBox();
		        HBox horizontalLayoutForPassword = new HBox();
		        HBox horizontalLayoutForTitle = new HBox();
		        HBox horizontalLayoutForLoginButton = new HBox();
		        
		        //This represents the root node
		        VBox parentVerticalLayout = new VBox();
		        
		        Label loginTitle = new Label(Resources.getString("login"));
		        TextField usernameOrEmailTextField = new TextField();
		        Label usernameOrEmailLabel = new Label(Resources.getString("username") + "/" + Resources.getString("email"));
		        PasswordField passwordTextField = new PasswordField();
		        TextField plainPasswordTextField = new TextField();
		        Label passwordLabel = new Label(Resources.getString("password"));
		        CheckBox unmaskPasswordCheckBox = new CheckBox(Resources.getString("show") + "/" + Resources.getString("hide") + " " + Resources.getString("password"));
		        CheckBox keepMeLoggedInCheckBox = new CheckBox(Resources.getString("keep_me_logged_in"));
		        Button loginButton = new Button(Resources.getString("login"));
		        
		        parentVerticalLayout.setSpacing(30);
		        horizontalLayoutForUsernameOrEmail.setSpacing(10);
		        horizontalLayoutForPassword.setSpacing(45);
		        horizontalLayoutForLoginButton.setSpacing(45);
		        
		        //Align center vertically and horizontally:
		        parentVerticalLayout.setAlignment(Pos.CENTER);
		        
		        horizontalLayoutForUsernameOrEmail.setAlignment(Pos.CENTER); 
		        horizontalLayoutForPassword.setAlignment(Pos.CENTER); 
		        horizontalLayoutForTitle.setAlignment(Pos.CENTER); 
		        horizontalLayoutForLoginButton.setAlignment(Pos.CENTER);
		        
		        ObservableList<Node> horizontalLayoutListForUsernameOrEmail = horizontalLayoutForUsernameOrEmail.getChildren();
		        ObservableList<Node> horizontalLayoutListForPassword = horizontalLayoutForPassword.getChildren();
		        ObservableList<Node> horizontalLayoutListForTitle = horizontalLayoutForTitle.getChildren();
		        ObservableList<Node> horizontalLayoutListForLoginButton = horizontalLayoutForLoginButton.getChildren();
		        
		        ObservableList<Node> parentVerticalLayoutList = parentVerticalLayout.getChildren();
		         
		        horizontalLayoutListForTitle.add(loginTitle);
		        horizontalLayoutListForUsernameOrEmail.addAll(usernameOrEmailLabel,usernameOrEmailTextField);
		        horizontalLayoutListForPassword.addAll(passwordLabel,passwordTextField,plainPasswordTextField);
		        horizontalLayoutListForLoginButton.addAll(loginButton);
		        parentVerticalLayoutList.addAll(horizontalLayoutForTitle);
		        parentVerticalLayoutList.addAll(horizontalLayoutForUsernameOrEmail);
		        parentVerticalLayoutList.addAll(horizontalLayoutForPassword);
		        parentVerticalLayoutList.addAll(unmaskPasswordCheckBox);
		        parentVerticalLayoutList.addAll(keepMeLoggedInCheckBox);
		        parentVerticalLayoutList.addAll(horizontalLayoutForLoginButton);
		        
		        
		        // Bind properties. Toggle textField and passwordField
		        // visibility and managability properties mutually when checkbox's state is changed.
		        // Because we want to display only one component (textField or passwordField)
		        // on the scene at a time.
		        plainPasswordTextField.managedProperty().bind(unmaskPasswordCheckBox.selectedProperty());
		        plainPasswordTextField.visibleProperty().bind(unmaskPasswordCheckBox.selectedProperty());

		        passwordTextField.managedProperty().bind(unmaskPasswordCheckBox.selectedProperty().not());
		        passwordTextField.visibleProperty().bind(unmaskPasswordCheckBox.selectedProperty().not());

		        // Bind the textField and passwordField text values bidirectionally.
		        plainPasswordTextField.textProperty().bindBidirectional(passwordTextField.textProperty());
		        
		        parentVerticalLayout.setPadding(new Insets(20,20,20,20));
		        Scene scene = new Scene(parentVerticalLayout);
				primaryStage.setTitle(Resources.getString("login"));
				primaryStage.setScene(scene);
				primaryStage.show();
				EventHandler<ActionEvent> loginButtonEventHandler
				= event -> {
					
					//Check user credentials.
					String usernameOrEmail = usernameOrEmailTextField.getText();
					String password = passwordTextField.getText();
					
					//Check if the person Logging in is an Admin
					Admins admins = new Admins();
					ArrayList<Model> results = admins.whereAnd(admins.EMAIL_FIELD, "=", usernameOrEmail,admins.PASSWORD_FIELD,"=",password);
					if(results.size() >= 1) {
						//Successful Login via email
						Log.log("Admin Loggin via email: " + usernameOrEmail);
						admins = (Admins) results.get(0);
						
						//Keep User Logged In if he's selected the checkbox
						if(keepMeLoggedInCheckBox.isSelected()) {
							admins.rememberToken = admins.generateString();
							admins.update();
						}
						//Do sumn'
						Dashboard.launchDashboard(primaryStage, admins);
						return;
					} else {
						results = admins.whereAnd(admins.USERNAME_FIELD, "=", usernameOrEmail,admins.PASSWORD_FIELD,"=",password);
						if(results.size() >= 1) {
							//Successful Login via username
							Log.log("Admin Loggin via username: " + usernameOrEmail);
							admins = (Admins) results.get(0);
							
							//Keep User Logged In if he's selected the checkbox
							if(keepMeLoggedInCheckBox.isSelected()) {
								admins.rememberToken = admins.generateString();
								admins.update();
							} 
							Dashboard.launchDashboard(primaryStage, admins);
							
							return;
						}else {
							
							//Check if the person Loggin in is an Employee
							Employees employees = new Employees();
							results = employees.whereAnd(employees.EMAIL_FIELD, "=", usernameOrEmail,employees.PASSWORD_FIELD,"=",password);
							if(results.size() >= 1) {
								//Successful Login via email
								Log.log("Employee Loggin via email: " + usernameOrEmail);
								employees = (Employees) results.get(0);
								
								//Keep User Logged In if he's selected the checkbox
								if(keepMeLoggedInCheckBox.isSelected()) {
									employees.rememberToken = admins.generateString();
									employees.update();
								}
								
								//Do sumn'
								Dashboard.launchDashboard(primaryStage, admins);
								
								return;
							} else {
								results = employees.whereAnd(employees.USERNAME_FIELD, "=", usernameOrEmail,employees.PASSWORD_FIELD,"=",password);
								if(results.size() >= 1) {
									//Successful Login via username
									Log.log("Employee Loggin via username: " + usernameOrEmail);
									employees = (Employees) results.get(0);
									
									//Keep User Logged In if he's selected the checkbox
									if(keepMeLoggedInCheckBox.isSelected()) {
										employees.rememberToken = admins.generateString();
										employees.update();
									}
									
									//Do sumn'
									Dashboard.launchDashboard(primaryStage, admins);
									
									return;
								}else {
									alert.showAndWait();
									
									if(alert.getResult() == ButtonType.OK) {
										alert.hide();	
										return;
									}
								}
							}
						}
					}
					
				};
				
				//Handle on Login button Click
				loginButton.setOnAction(loginButtonEventHandler);
				}
	
	public static void main(String args[]){   
		Resources.setLanguage(Resources.ENGLISH_LANGUAGE_FILE);
		Admins admins = new Admins();
		Employees employees = new Employees();
		//Check if user was already logged in.
		ArrayList<Model> data = admins.where(admins.REMEMBER_TOKEN_FIELD, "<>", "NULL");
		if(data.size()!=0) {
			Log.log("Logged in User: " + ((Admins) data.get(0)).username);
			admins = (Admins)data.get(0);
			Dashboard.handleFromMain(admins);
			return;
		} else {
			data = employees.where(employees.REMEMBER_TOKEN_FIELD, "<>", "NULL");
			if(data.size()!=0) {
				Log.log("Logged in User: " + ((Employees) data.get(0)).username);
				employees = (Employees) data.get(0);
				Dashboard.handleFromMain(employees);
			return;
			}
		}
		launch(args);  
		} 
	public static void instantiate(Stage stage) {
		handle(stage);
		
	}

}
