package librarysystem.gui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import librarysystem.models.Admins;
import librarysystem.models.Books;
import librarysystem.models.BorrowedBooks;
import librarysystem.models.Categories;
import librarysystem.models.Employees;
import librarysystem.models.Members;
import librarysystem.models.Model;
import librarysystem.models.User;
import librarysystem.resources.Resources;
import librarysystem.utils.Log;

public class Dashboard extends Application {

	private static Scene scene;
	private static Model theModel;
	private static ArrayList<String> ID = new ArrayList<>();
	private static String bookID = "";
	// stores the id after the user meets minimum requirements.
	private static String userID = "";
	private final static double SPACING = 10;
	private final static Insets LABELS_BUTTONS = new Insets(10, 20, 10, 20);
	private final static Insets COLUMNS = new Insets(10, 0, 10, 0);
	private final static int ROW_HEIGHT = 24;
	private static Stage myStage;

	@Override
	public void start(Stage stage) throws Exception {
		handle();
		stage.setTitle(Resources.getString("library_system"));
		myStage = stage;
		stage.setScene(scene);
		stage.show();
	}

	private static void handle() {
		// Parent root
		VBox parentVerticalLayout = new VBox();
		HBox parentHorizontalLayout = new HBox();
		HBox mainLabelHorizontalLayout = new HBox();
		Label mainLabel = new Label(Resources.getString("welcome") + ": " + ((User) theModel).username);
		Label userIDLabel = new Label(((User) theModel).ID);
		userIDLabel.setVisible(false);
		Button logoutButton = new Button(Resources.getString("logout"));
		// Columns:
		VBox registerUserColumn = new VBox();
		registerUserColumn.setSpacing(SPACING);
		registerUserColumn.setPadding(COLUMNS);
		registerUserColumn.setStyle("-fx-border-color: black");
		registerUserColumn.setPadding(LABELS_BUTTONS);
		Label registerUserLabel = new Label(Resources.getString("register_users"));
		registerUserLabel.setPadding(LABELS_BUTTONS);
		Button addUserButton = new Button(Resources.getString("add_user"));
		addUserButton.setPadding(LABELS_BUTTONS);
		addUserButton.prefWidthProperty().bind(registerUserColumn.widthProperty());
		Button listUsersButton = new Button(Resources.getString("list_users"));
		listUsersButton.setPadding(LABELS_BUTTONS);
		listUsersButton.prefWidthProperty().bind(registerUserColumn.widthProperty());
		ObservableList<Node> registerUserColumnChildren = registerUserColumn.getChildren();
		registerUserColumnChildren.add(registerUserLabel);
		registerUserColumnChildren.add(addUserButton);
		registerUserColumnChildren.add(listUsersButton);
		// Autoresize children according to parent
		for (Node n : registerUserColumnChildren) {
			VBox.setVgrow(n, Priority.ALWAYS);
		}

		VBox borrowBookColumn = new VBox();
		borrowBookColumn.setSpacing(SPACING);
		borrowBookColumn.setStyle("-fx-border-color: black");
		borrowBookColumn.setPadding(LABELS_BUTTONS);
		Label borrowBookLabel = new Label(Resources.getString("books"));
		borrowBookLabel.setPadding(LABELS_BUTTONS);
		Button borrowBookButton = new Button(Resources.getString("borrow_book"));
		borrowBookButton.setPadding(LABELS_BUTTONS);
		borrowBookButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());
		Button returnButton = new Button(Resources.getString("return_book"));
		returnButton.setPadding(LABELS_BUTTONS);
		returnButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());
		Button addBookButton = new Button(Resources.getString("add_book"));
		addBookButton.setPadding(LABELS_BUTTONS);
		addBookButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());
		ObservableList<Node> borrowBookColumnChildren = borrowBookColumn.getChildren();
		borrowBookColumnChildren.add(borrowBookLabel);
		borrowBookColumnChildren.add(borrowBookButton);
		borrowBookColumnChildren.add(returnButton);
		borrowBookColumnChildren.add(addBookButton);

		// Autoresize children according to parent
		for (Node n : borrowBookColumnChildren) {
			VBox.setVgrow(n, Priority.ALWAYS);
		}

		VBox editUserDetailsColumn = new VBox();
		editUserDetailsColumn.setSpacing(SPACING);
		editUserDetailsColumn.setStyle("-fx-border-color: black");
		editUserDetailsColumn.setPadding(LABELS_BUTTONS);
		Label editUserDetailsLabel = new Label(Resources.getString("edit_your_details"));
		editUserDetailsLabel.setPadding(new Insets(10, 20, 10, 20));
		Button editUsernameButton = new Button(Resources.getString("edit_username"));
		editUsernameButton.setPadding(LABELS_BUTTONS);
		editUsernameButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());
		Button editPasswordButton = new Button(Resources.getString("edit_password"));
		editPasswordButton.setPadding(LABELS_BUTTONS);
		editPasswordButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());
		Button listEmployeesButton = new Button(Resources.getString("list_employees"));
		listEmployeesButton.setPadding(LABELS_BUTTONS);
		listEmployeesButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());
		Button addEmployeesButton = new Button(Resources.getString("add_employees"));
		addEmployeesButton.setPadding(LABELS_BUTTONS);
		addEmployeesButton.prefWidthProperty().bind(borrowBookColumn.widthProperty());

		ObservableList<Node> editUserDetilsColumnChildren = editUserDetailsColumn.getChildren();
		editUserDetilsColumnChildren.add(editUserDetailsLabel);
		editUserDetilsColumnChildren.add(editUsernameButton);
		editUserDetilsColumnChildren.add(editPasswordButton);

		// Display the list employees button if the logged in user is an admin:
		Admins theAdmins = new Admins();
		if (theAdmins.where(theAdmins.ID_FIELD, "=", userIDLabel.getText()).size() >= 1) {
			editUserDetilsColumnChildren.add(listEmployeesButton);
			editUserDetilsColumnChildren.add(addEmployeesButton);
		}

		// Autoresize children according to parent
		for (Node n : editUserDetilsColumnChildren) {
			VBox.setVgrow(n, Priority.ALWAYS);
		}

		registerUserColumn.setAlignment(Pos.CENTER);
		borrowBookColumn.setAlignment(Pos.CENTER);
		editUserDetailsColumn.setAlignment(Pos.CENTER);

		//Get an observable list of the children and add more nodes to this list.
		ObservableList<Node> parentHorizontalLayoutChildren = parentHorizontalLayout.getChildren();
		parentHorizontalLayoutChildren.add(registerUserColumn);
		parentHorizontalLayoutChildren.add(borrowBookColumn);
		parentHorizontalLayoutChildren.add(editUserDetailsColumn);

		// Autoresize children according to parent (Main Parent)
		for (Node n : parentHorizontalLayoutChildren) {
			HBox.setHgrow(n, Priority.ALWAYS);
		}

		parentHorizontalLayout.setSpacing(SPACING);
		ObservableList<Node> mainLabelHorizontalLayoutChildren = mainLabelHorizontalLayout.getChildren();
		mainLabelHorizontalLayoutChildren.addAll(mainLabel, userIDLabel, logoutButton);
		mainLabelHorizontalLayout.setStyle("-fx-border-color: black");
		mainLabelHorizontalLayout.setAlignment(Pos.CENTER);

		ObservableList<Node> parentVerticalLayoutChildren = parentVerticalLayout.getChildren();
		parentVerticalLayoutChildren.add(mainLabelHorizontalLayout);
		parentVerticalLayoutChildren.add(parentHorizontalLayout);
		parentVerticalLayout.setSpacing(SPACING);
		parentVerticalLayout.setPadding(new Insets(30, 30, 30, 30));

		// Set OnClick listeners:
		addUserButton.setOnAction(event -> {
			addUserDialog();
		});
		listUsersButton.setOnAction(event -> {
			listUsers();
		});
		borrowBookButton.setOnAction(event -> {
			borrowBook();
		});
		addBookButton.setOnAction(event -> {
			addBook();
		});
		returnButton.setOnAction(event -> {
			returnBook();
		});
		addEmployeesButton.setOnAction(event -> {
			addEmployee();
		});
		listEmployeesButton.setOnAction(event -> {
			listEmployees();
		});
		editUsernameButton.setOnAction(event -> {
			Employees employees = new Employees();
			ArrayList<Model> loggedInEmployee = employees.where(employees.ID_FIELD, "=", userIDLabel.getText());
			if (loggedInEmployee.size() >= 1) {
				Employees employee = ((Employees)loggedInEmployee.get(0));
					Optional<String> input = inputDialog(Resources.getString("change_username")
							,Resources.getString("change_username"),
							"(" + Resources.getString("previous_username") + ": " 
					+ employee.username + ")");
					if(input.isPresent()) {
					if(input.get().trim().isEmpty()) {
						alertUser(Resources.getString("did_not_fill_in_fields"),
								AlertType.ERROR,Resources.getString("error"));
						return;
					}
					employee.username = input.get();
					if(employee.update()) {
						alertUser(Resources.getString("username_saved_successfully"),
								AlertType.INFORMATION,Resources.getString("success"));
					} else {
						alertUser(Resources.getString("username_could_not_be_saved"),
								AlertType.ERROR,Resources.getString("error"));
					}
					}
				} else {
				Admins admins = new Admins();
				ArrayList<Model> loggedInAdmin = admins.where(admins.ID_FIELD, "=", userIDLabel.getText());
				if (loggedInAdmin.size() >= 1) {
					Admins admin = ((Admins)loggedInAdmin.get(0));
					Optional<String> input = inputDialog(Resources.getString("change_username")
							,Resources.getString("change_username"),
							"(" + Resources.getString("previous_username") + ": " + admin.username + ")");
					if(input.isPresent()) {
					if(input.get().trim().isEmpty()) {
						alertUser(Resources.getString("did_not_fill_in_fields"),
								AlertType.ERROR,Resources.getString("error"));
						return;
					}
					admin.username = input.get();
					if(admin.update()){
						alertUser(Resources.getString("username_saved_successfully"),
								AlertType.INFORMATION,Resources.getString("success"));
					} else {
						alertUser(Resources.getString("username_could_not_be_saved"),
								AlertType.ERROR,Resources.getString("error"));
					}
				}
				}
			}
		});
		
		editPasswordButton.setOnAction(event -> {
			Employees employees = new Employees();
			ArrayList<Model> loggedInEmployee = employees.where(employees.ID_FIELD, "=", userIDLabel.getText());
			if (loggedInEmployee.size() >= 1) {
				Employees employee = ((Employees)loggedInEmployee.get(0));
					Optional<String> input = inputDialog(Resources.getString("change_password")
							,Resources.getString("change_password"),
							"(" + Resources.getString("previous_password") + ":" 
					+ employee.password + ")");
					if(input.isPresent()) {
					if(input.get().trim().isEmpty()) {
						alertUser(Resources.getString("did_not_fill_in_fields"),
								AlertType.ERROR,Resources.getString("error"));
						return;
					}
					employee.password = input.get();
					if(employee.update()) {
						alertUser(Resources.getString("password_saved_successfully"),
								AlertType.INFORMATION,Resources.getString("success"));
					} else {
						alertUser(Resources.getString("password_could_not_be_saved"),
								AlertType.ERROR,Resources.getString("error"));
					}
					}
				} else {
				Admins admins = new Admins();
				ArrayList<Model> loggedInAdmin = admins.where(admins.ID_FIELD, "=", userIDLabel.getText());
				if (loggedInAdmin.size() >= 1) {
					Admins admin = ((Admins)loggedInAdmin.get(0));
					Optional<String> input = inputDialog(Resources.getString("change_password")
							,Resources.getString("change_password"),
							"(" + Resources.getString("previous_password") + ":" + admin.password + ")");
					if(input.isPresent()) {
					if(input.get().trim().isEmpty()) {
						alertUser(Resources.getString("did_not_fill_in_fields"),
								AlertType.ERROR,Resources.getString("error"));
						return;
					}
					admin.password = input.get();
					if(admin.update()){
						alertUser(Resources.getString("password_saved_successfully"),
								AlertType.INFORMATION,Resources.getString("success"));
					} else {
						alertUser(Resources.getString("password_could_not_be_saved"),
								AlertType.ERROR,Resources.getString("error"));
					}
				}
				}
			}
		});
		logoutButton.setOnAction(event -> {
			Employees employees = new Employees();

			ArrayList<Model> loggedInEmployee = employees.where(employees.ID_FIELD, "=", userIDLabel.getText());
			if (loggedInEmployee.size() >= 1) {
				// Log the employee out:
				Employees theEmployee = (Employees) loggedInEmployee.get(0);
				theEmployee.rememberToken = "NULL";
				theEmployee.update();
				Log.log("Logged out: " + theEmployee.email + ":" + theEmployee.firstName);
			} else {
				Admins admins = new Admins();
				ArrayList<Model> loggedInAdmin = admins.where(admins.ID_FIELD, "=", userIDLabel.getText());
				if (loggedInAdmin.size() >= 1) {
					// Log the admin out:
					Admins theEmployee = (Admins) loggedInAdmin.get(0);
					theEmployee.rememberToken = "NULL";
					theEmployee.update();
					Log.log("Logged out: " + theEmployee.email + ":" + theEmployee.firstName);

				}
			}

			if (myStage != null) {
				Login.instantiate(myStage);
			}

			return;
		});

		scene = new Scene(parentVerticalLayout);
	}
	
	private static Optional<String> inputDialog(String title, String message, String content){
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(title);
		dialog.setHeaderText(message);
		dialog.setContentText(content);
		return dialog.showAndWait();
	}
	public static void launchDashboard(Stage stage, Model model) {
		stage.setTitle(Resources.getString("library_system"));
		theModel = model;
		handle();
		myStage = stage;
		stage.setScene(scene);
	}

	public static void handleFromMain(Model model) {
		theModel = model;
		launch();
	}

	private static void addEmployee() {
		Dialog<Employees> addUserDialog = new Dialog<>();
		addUserDialog.setTitle(Resources.getString("add_employees"));
		addUserDialog.setHeaderText(Resources.getString("add_employees"));
		URL url = null;
		try {
			url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/add_user.png").toURI()
					.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addUserDialog.setGraphic(new ImageView(url.toString()));
		ButtonType saveUserDataButton = new ButtonType(Resources.getString("save"), ButtonData.OK_DONE);

		TextField firstNameTextField = new TextField();
		TextField lastNameTextField = new TextField();
		TextField emailTextField = new TextField();
		TextField usernameTextField = new TextField();
		PasswordField passwordTextField = new PasswordField();
		TextField plainPasswordTextField = new TextField();
		CheckBox unmaskPasswordCheckBox = new CheckBox(Resources.getString("show") + "/" + Resources.getString("hide")
				+ " " + Resources.getString("password"));

		// Bind properties. Toggle textField and passwordField
		// visibility and managability properties mutually when checkbox's state is
		// changed.
		// Because we want to display only one component (textField or passwordField)
		// on the scene at a time.
		plainPasswordTextField.managedProperty().bind(unmaskPasswordCheckBox.selectedProperty());
		plainPasswordTextField.visibleProperty().bind(unmaskPasswordCheckBox.selectedProperty());

		passwordTextField.managedProperty().bind(unmaskPasswordCheckBox.selectedProperty().not());
		passwordTextField.visibleProperty().bind(unmaskPasswordCheckBox.selectedProperty().not());

		// Bind the textField and passwordField text values bidirectionally.
		plainPasswordTextField.textProperty().bindBidirectional(passwordTextField.textProperty());

		Label firstNameLabel = new Label(Resources.getString("first_name"));
		Label lastNameLabel = new Label(Resources.getString("last_name"));
		Label emailLabel = new Label(Resources.getString("email"));
		Label usernameLabel = new Label(Resources.getString("username"));
		Label passwordLabel = new Label(Resources.getString("password"));

		GridPane parentGridLayout = new GridPane();
		parentGridLayout.setHgap(SPACING);
		parentGridLayout.setVgap(SPACING);
		parentGridLayout.setPadding(LABELS_BUTTONS);

		parentGridLayout.add(firstNameLabel, 0, 0);
		parentGridLayout.add(firstNameTextField, 1, 0);

		parentGridLayout.add(lastNameLabel, 0, 1);
		parentGridLayout.add(lastNameTextField, 1, 1);

		parentGridLayout.add(emailLabel, 0, 2);
		parentGridLayout.add(emailTextField, 1, 2);

		parentGridLayout.add(usernameLabel, 0, 3);
		parentGridLayout.add(usernameTextField, 1, 3);

		parentGridLayout.add(passwordLabel, 0, 4);
		parentGridLayout.add(passwordTextField, 1, 4);
		parentGridLayout.add(plainPasswordTextField, 1, 4);

		parentGridLayout.add(unmaskPasswordCheckBox, 1, 5);

		addUserDialog.getDialogPane().getButtonTypes().addAll(saveUserDataButton, ButtonType.CANCEL);

		// Disable Login Button until the user fills in all necessary fields
		Node saveButton = (Node) addUserDialog.getDialogPane().lookupButton(saveUserDataButton);
		saveButton.setDisable(true);

		firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
		lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
		emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
		usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
		passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});

		// Display dialog box as long as email is invalid
		((Button) saveButton).addEventFilter(ActionEvent.ACTION, event -> {
			if (!isEmailValid(emailTextField.getText())) {
				alertUser(Resources.getString("email") + " " + Resources.getString("is_invalid"), AlertType.ERROR,
						Resources.getString("error"));
				event.consume();
			} else if(firstNameTextField.getText().trim().isEmpty() || 
					lastNameTextField.getText().trim().isEmpty() 
					|| emailTextField.getText().trim().isEmpty() ||
					usernameTextField.getText().trim().isEmpty() || passwordTextField.getText().trim().isEmpty()) {
				alertUser(Resources.getString("did_not_fill_in_fields"), AlertType.ERROR,
						Resources.getString("error"));
				event.consume();
			}

		});

		addUserDialog.getDialogPane().setContent(parentGridLayout);

		addUserDialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveUserDataButton) {

				Employees members = new Employees();
				members.email = emailTextField.getText().trim();
				members.firstName = firstNameTextField.getText().trim();
				members.lastName = lastNameTextField.getText().trim();
				members.username = usernameTextField.getText().trim();
				members.password = passwordTextField.getText().trim();
				return members;
			}
			return null;
		});

		Optional<Employees> result = addUserDialog.showAndWait();

		result.ifPresent(members -> {

			if (members.save()) {
				Log.log("Saved new Employee: " + members.email + ": " + members.firstName);
				Alert alert = new Alert(AlertType.INFORMATION, Resources.getString("user_saved_successfully"),
						ButtonType.OK);
				alert.setHeaderText(null);
				alert.setTitle(Resources.getString("success"));
				alert.showAndWait();

				if (alert.getResult() == ButtonType.OK) {
					alert.hide();
					return;
				}

			} else {
				alertUser(Resources.getString("user_could_not_be_added"), AlertType.ERROR,
						Resources.getString("error"));
			}
		});
	}

	private static void addUserDialog() {
		Dialog<Members> addUserDialog = new Dialog<>();
		addUserDialog.setTitle(Resources.getString("add_user"));
		addUserDialog.setHeaderText(Resources.getString("add_user"));
		URL url = null;
		try {
			url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/add_user.png").toURI()
					.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addUserDialog.setGraphic(new ImageView(url.toString()));
		ButtonType saveUserDataButton = new ButtonType(Resources.getString("save"), ButtonData.OK_DONE);

		TextField firstNameTextField = new TextField();
		TextField lastNameTextField = new TextField();
		TextField emailTextField = new TextField();

		ObservableList<String> paymentMethods = FXCollections.observableArrayList("MPESA", "CASH");
		ListView<String> paymentMethodListView = new ListView<String>(paymentMethods);

		// This sets the initial height of the ListView:
		paymentMethodListView.setPrefHeight(paymentMethods.size() * ROW_HEIGHT + 2);

		Label firstNameLabel = new Label(Resources.getString("first_name"));
		Label lastNameLabel = new Label(Resources.getString("last_name"));
		Label emailLabel = new Label(Resources.getString("email"));
		Label paymentMethodLabel = new Label(Resources.getString("payment_method"));

		GridPane parentGridLayout = new GridPane();
		parentGridLayout.setHgap(SPACING);
		parentGridLayout.setVgap(SPACING);
		parentGridLayout.setPadding(LABELS_BUTTONS);

		parentGridLayout.add(firstNameLabel, 0, 0);
		parentGridLayout.add(firstNameTextField, 1, 0);

		parentGridLayout.add(lastNameLabel, 0, 1);
		parentGridLayout.add(lastNameTextField, 1, 1);

		parentGridLayout.add(emailLabel, 0, 2);
		parentGridLayout.add(emailTextField, 1, 2);

		parentGridLayout.add(paymentMethodLabel, 0, 3);
		parentGridLayout.add(paymentMethodListView, 1, 3);

		addUserDialog.getDialogPane().getButtonTypes().addAll(saveUserDataButton, ButtonType.CANCEL);

		// Disable Login Button until the user fills in all necessary fields
		Node saveButton = (Node) addUserDialog.getDialogPane().lookupButton(saveUserDataButton);
		saveButton.setDisable(true);

		firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
		lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
		emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});

		// Display dialog box as long as email is invalid
		((Button) saveButton).addEventFilter(ActionEvent.ACTION, event -> {
			if (!isEmailValid(emailTextField.getText())) {
				alertUser(Resources.getString("email") + " " + Resources.getString("is_invalid"), AlertType.ERROR,
						Resources.getString("error"));
				event.consume();
			}else if(firstNameTextField.getText().trim().isEmpty() || 
					lastNameTextField.getText().trim().isEmpty() || emailTextField.getText().trim().isEmpty()) {
				alertUser(Resources.getString("did_not_fill_in_fields"), AlertType.ERROR,
						Resources.getString("error"));
				event.consume();
			}

		});

		addUserDialog.getDialogPane().setContent(parentGridLayout);

		addUserDialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveUserDataButton) {

				Members members = new Members();
				members.email = emailTextField.getText().trim();
				members.firstName = firstNameTextField.getText().trim();
				members.lastName = lastNameTextField.getText().trim();
				members.paymentMethod = paymentMethodListView.getSelectionModel().getSelectedItem();
				return members;
			}
			return null;
		});

		Optional<Members> result = addUserDialog.showAndWait();

		result.ifPresent(members -> {

			if (members.save()) {
				Log.log("Saved new member: " + members.email + ": " + members.firstName);
				Alert alert = new Alert(AlertType.INFORMATION, Resources.getString("user_saved_successfully"),
						ButtonType.OK);
				alert.setHeaderText(null);
				alert.setTitle(Resources.getString("success"));
				alert.showAndWait();

				if (alert.getResult() == ButtonType.OK) {
					alert.hide();
					return;
				}

			} else {
				alertUser(Resources.getString("user_could_not_be_added"), AlertType.ERROR,
						Resources.getString("error"));
			}
		});
	}

	private static void addBook() {
		Dialog<Books> addBookDialog = new Dialog<>();
		addBookDialog.setTitle(Resources.getString("add_book"));
		addBookDialog.setHeaderText(Resources.getString("multiple_authors"));
		URL url = null;
		try {
			url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/borrow_book.png").toURI()
					.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addBookDialog.setGraphic(new ImageView(url.toString()));
		ButtonType saveBookButton = new ButtonType(Resources.getString("save"), ButtonData.OK_DONE);

		TextField bookTitleTextField = new TextField();
		TextField bookAuthorsTextField = new TextField();
		ListView<String> bookCategoryListView = new ListView<>();

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setPrefHeight(5 * ROW_HEIGHT + 2);
		scrollPane.setContent(bookCategoryListView);

		Categories categories = new Categories();
		ArrayList<Model> tempCategories = categories.all();
		ArrayList<String> parsedCategories = new ArrayList<>();
		for (Model category : tempCategories) {
			Categories tempCategory = (Categories) category;
			parsedCategories.add(tempCategory.ID + ":" + tempCategory.categoryName);
		}
		ObservableList<String> bookCategories = FXCollections.observableArrayList(parsedCategories);
		bookCategoryListView.setItems(bookCategories);

		Label bookTitleLabel = new Label(Resources.getString("book_title"));
		Label bookCategoryLabel = new Label(Resources.getString("book_category"));
		Label bookAuthorsLabel = new Label(Resources.getString("book_author"));

		GridPane parentGridLayout = new GridPane();
		parentGridLayout.setHgap(SPACING);
		parentGridLayout.setVgap(SPACING);
		parentGridLayout.setPadding(LABELS_BUTTONS);

		parentGridLayout.add(bookTitleLabel, 0, 0);
		parentGridLayout.add(bookTitleTextField, 1, 0);

		parentGridLayout.add(bookAuthorsLabel, 0, 1);
		parentGridLayout.add(bookAuthorsTextField, 1, 1);

		parentGridLayout.add(bookCategoryLabel, 0, 2);
		parentGridLayout.add(scrollPane, 1, 2);

		addBookDialog.getDialogPane().getButtonTypes().addAll(saveBookButton, ButtonType.CANCEL);

		// Disable Login Button until the user fills in all necessary fields
		Node saveButton = (Node) addBookDialog.getDialogPane().lookupButton(saveBookButton);
		saveButton.setDisable(true);

		bookTitleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
		bookAuthorsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});

		// Display dialog box as long as category isn't selected
		((Button) saveButton).addEventFilter(ActionEvent.ACTION, event -> {
			if (bookCategoryListView.getSelectionModel().getSelectedItem() == null) {
				alertUser(Resources.getString("book_category_not_selected"), AlertType.ERROR,
						Resources.getString("error"));
				event.consume();
			} else if(bookTitleTextField.getText().trim().isEmpty() || bookAuthorsTextField.getText().trim().isEmpty()) {
				alertUser(Resources.getString("did_not_fill_in_fields"), AlertType.ERROR,
						Resources.getString("error"));
				event.consume();
			}

		});

		addBookDialog.getDialogPane().setContent(parentGridLayout);

		addBookDialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveBookButton) {

				Books books = new Books();
				String category = bookCategoryListView.getSelectionModel().getSelectedItem();
				books.bookCategory = category.substring(0, category.indexOf(":"));
				books.bookTitle = bookTitleTextField.getText();
				books.bookAuthors = bookAuthorsTextField.getText();
				return books;
			}
			return null;
		});

		Optional<Books> result = addBookDialog.showAndWait();

		result.ifPresent(books -> {

			if (books.save()) {
				Log.log("Saved new Book: " + books.ID + ": " + books.bookTitle);
				Alert alert = new Alert(AlertType.INFORMATION, Resources.getString("book_saved_successfully"),
						ButtonType.OK);
				alert.setHeaderText(null);
				alert.setTitle(Resources.getString("success"));
				alert.showAndWait();

				if (alert.getResult() == ButtonType.OK) {
					alert.hide();
					return;
				}

			} else {
				alertUser(Resources.getString("book_could_not_be_saved"), AlertType.ERROR,
						Resources.getString("error"));
			}
		});
	}

	private static void listEmployees() {
		ID = new ArrayList<>();
		ScrollPane scrollbar = new ScrollPane();
		GridPane usersGridLayout = new GridPane();

		VBox IDsListViewAndLabel = new VBox();
		VBox firstNamesListViewAndLabel = new VBox();
		VBox lastNamesListViewAndLabel = new VBox();
		VBox emailsListViewAndLabel = new VBox();
		VBox usernamesListViewAndLabel = new VBox();

		IDsListViewAndLabel.setSpacing(SPACING);
		firstNamesListViewAndLabel.setSpacing(SPACING);
		lastNamesListViewAndLabel.setSpacing(SPACING);
		emailsListViewAndLabel.setSpacing(SPACING);
		usernamesListViewAndLabel.setSpacing(SPACING);

		ListView<String> IDsListView = new ListView<>();
		ListView<String> firstNamesListView = new ListView<>();
		ListView<String> lastNamesListView = new ListView<>();
		ListView<String> emailsListView = new ListView<>();
		ListView<String> usernamesListView = new ListView<>();

		Label IDsListViewLabel = new Label(Resources.getString("user_id"));
		Label firstNamesListViewLabel = new Label(Resources.getString("first_name"));
		Label lastNamesListViewLabel = new Label(Resources.getString("last_name"));
		Label emailsListViewLabel = new Label(Resources.getString("email"));
		Label usernamesListViewLabel = new Label(Resources.getString("username"));

		IDsListView.setOrientation(Orientation.VERTICAL);
		firstNamesListView.setOrientation(Orientation.VERTICAL);
		lastNamesListView.setOrientation(Orientation.VERTICAL);
		emailsListView.setOrientation(Orientation.VERTICAL);
		usernamesListView.setOrientation(Orientation.VERTICAL);

		Employees members = new Employees();
		ArrayList<Model> allUsers = members.all();

		ArrayList<String> IDsList = new ArrayList<>();
		ArrayList<String> firstNamesList = new ArrayList<>();
		ArrayList<String> lastNamesList = new ArrayList<>();
		ArrayList<String> emailsList = new ArrayList<>();
		ArrayList<String> usernamesList = new ArrayList<>();

		for (Model user : allUsers) {
			Employees member = (Employees) user;
			IDsList.add(member.ID);
			firstNamesList.add(member.firstName);
			lastNamesList.add(member.lastName);
			emailsList.add(member.email);
			usernamesList.add(member.username);
		}

		ObservableList<String> IDsListViewItems = FXCollections.observableArrayList(IDsList);
		ObservableList<String> firstNamesListViewItems = FXCollections.observableArrayList(firstNamesList);
		ObservableList<String> lastNamesListViewItems = FXCollections.observableArrayList(lastNamesList);
		ObservableList<String> emailsListViewItems = FXCollections.observableArrayList(emailsList);
		ObservableList<String> usernamesListViewItems = FXCollections.observableArrayList(usernamesList);

		IDsListView.setItems(IDsListViewItems);
		firstNamesListView.setItems(firstNamesListViewItems);
		lastNamesListView.setItems(lastNamesListViewItems);
		emailsListView.setItems(emailsListViewItems);
		usernamesListView.setItems(usernamesListViewItems);

		// This sets the initial height of the ListView:
		IDsListView.setPrefHeight(IDsListViewItems.size() * ROW_HEIGHT + 2);
		firstNamesListView.setPrefHeight(firstNamesListViewItems.size() * ROW_HEIGHT + 2);
		lastNamesListView.setPrefHeight(lastNamesListViewItems.size() * ROW_HEIGHT + 2);
		emailsListView.setPrefHeight(emailsListViewItems.size() * ROW_HEIGHT + 2);
		usernamesListView.setPrefHeight(usernamesListViewItems.size() * ROW_HEIGHT + 2);

		IDsListViewAndLabel.getChildren().addAll(IDsListViewLabel, IDsListView);
		firstNamesListViewAndLabel.getChildren().addAll(firstNamesListViewLabel, firstNamesListView);
		lastNamesListViewAndLabel.getChildren().addAll(lastNamesListViewLabel, lastNamesListView);
		emailsListViewAndLabel.getChildren().addAll(emailsListViewLabel, emailsListView);
		usernamesListViewAndLabel.getChildren().addAll(usernamesListViewLabel, usernamesListView);

		VBox parentLayout = new VBox();
		usersGridLayout.addColumn(0, IDsListViewAndLabel);
		usersGridLayout.addColumn(1, firstNamesListViewAndLabel);
		usersGridLayout.addColumn(2, lastNamesListViewAndLabel);
		usersGridLayout.addColumn(3, emailsListViewAndLabel);
		usersGridLayout.addColumn(4, usernamesListViewAndLabel);
		scrollbar.setPrefHeight(10 * ROW_HEIGHT + 2);
		scrollbar.setContent(usersGridLayout);
		parentLayout.getChildren().addAll(scrollbar);

		// Display the created GUI inside a dialog box
		Dialog<ListView<String>> listUserDialog = new Dialog<>();
		listUserDialog.setTitle(Resources.getString("list_employees"));
		listUserDialog.setHeaderText(Resources.getString("list_employees"));
		URL url = null;
		try {
			url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/list_user.jpg").toURI()
					.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listUserDialog.setGraphic(new ImageView(url.toString()));

		// Multiple select in a list view:
		IDsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		ButtonType deleteUserButton = new ButtonType(Resources.getString("delete_user"), ButtonData.OK_DONE);

		// Dialog functionality
		listUserDialog.getDialogPane().getButtonTypes().addAll(deleteUserButton, ButtonType.CANCEL);

		// Store corresponding IDs to an ID variable
		firstNamesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			ID.add(IDsListView.getItems().get(newIndex.intValue()));
		});
		lastNamesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			ID.add(IDsListView.getItems().get(newIndex.intValue()));
		});
		emailsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			ID.add(IDsListView.getItems().get(newIndex.intValue()));
		});
		usernamesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			ID.add(IDsListView.getItems().get(newIndex.intValue()));
		});

		listUserDialog.getDialogPane().setContent(parentLayout);

		listUserDialog.setResultConverter(dialogButton -> {
			if (dialogButton == deleteUserButton) {
				return IDsListView;
			}
			return null;
		});

		// Prevent the delete button from executing if a user has no fields clicked:
		Button button = (Button) listUserDialog.getDialogPane().lookupButton(deleteUserButton);
		button.addEventFilter(ActionEvent.ACTION, event -> {
			if (ID.size() == 0 && IDsListView.getSelectionModel().getSelectedItems().size() <= 0) {
				// Tell the user to select at least a single cell.
				alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), AlertType.ERROR,
						Resources.getString("error"));
				event.consume();
			}
		});

		Optional<ListView<String>> result = listUserDialog.showAndWait();

		result.ifPresent(clickedList -> {
			Employees membersAgain = new Employees();
			membersAgain.ID = "";
			ObservableList<String> listOfIDs = clickedList.getSelectionModel().getSelectedItems();
			boolean errorOccured = false;
			System.out.println(ID.size());
			if (listOfIDs.size() != 0) {

				for (String theID : listOfIDs) {
					membersAgain.ID = theID;
					if (membersAgain.delete()) {
						Employees theMember = (Employees) membersAgain.where(membersAgain.ID, "=", theID).get(0);
						Log.log("Deleted Employee " + theMember.email + ": " + theMember.firstName);
					} else {
						errorOccured = true;
					}
				}
			} else if (ID.size() != 0) {
				for (String theID : ID) {
					membersAgain.ID = theID;
					ArrayList<Model> membersTemp = membersAgain.where(membersAgain.ID_FIELD, "=", theID);
					if (membersAgain.delete()) {
						Employees theMember = ((Employees) membersTemp.get(0));
						Log.log("Deleted Employee " + theMember.email + ": " + theMember.firstName);
					} else {
						errorOccured = true;
					}

				}
			} else {
				// Do nothing
				errorOccured = true;
			}
			
			Alert alert;

			if (errorOccured) {
				alert = new Alert(AlertType.ERROR, Resources.getString("user_could_not_be_deleted"), ButtonType.OK);
				alert.setTitle(Resources.getString("error"));
			} else {
				alert = new Alert(AlertType.INFORMATION, Resources.getString("user_deleted_successfully"),
						ButtonType.OK);
				alert.setTitle(Resources.getString("success"));
			}
			alert.setHeaderText(null);

			alert.showAndWait();

			if (alert.getResult() == ButtonType.OK) {
				alert.hide();
				return;
			}
		});
		

	}

	private static void listUsers() {
		ID = new ArrayList<>();
		ScrollPane scrollbar = new ScrollPane();
		GridPane usersGridLayout = new GridPane();

		VBox IDsListViewAndLabel = new VBox();
		VBox firstNamesListViewAndLabel = new VBox();
		VBox lastNamesListViewAndLabel = new VBox();
		VBox emailsListViewAndLabel = new VBox();
		VBox paymentMethodsListViewAndLabel = new VBox();

		IDsListViewAndLabel.setSpacing(SPACING);
		firstNamesListViewAndLabel.setSpacing(SPACING);
		lastNamesListViewAndLabel.setSpacing(SPACING);
		emailsListViewAndLabel.setSpacing(SPACING);
		paymentMethodsListViewAndLabel.setSpacing(SPACING);

		ListView<String> IDsListView = new ListView<>();
		ListView<String> firstNamesListView = new ListView<>();
		ListView<String> lastNamesListView = new ListView<>();
		ListView<String> emailsListView = new ListView<>();
		ListView<String> paymentMethodsListView = new ListView<>();

		Label IDsListViewLabel = new Label(Resources.getString("user_id"));
		Label firstNamesListViewLabel = new Label(Resources.getString("first_name"));
		Label lastNamesListViewLabel = new Label(Resources.getString("last_name"));
		Label emailsListViewLabel = new Label(Resources.getString("email"));
		Label paymentMethodsListViewLabel = new Label(Resources.getString("payment_method"));

		IDsListView.setOrientation(Orientation.VERTICAL);
		firstNamesListView.setOrientation(Orientation.VERTICAL);
		lastNamesListView.setOrientation(Orientation.VERTICAL);
		emailsListView.setOrientation(Orientation.VERTICAL);
		paymentMethodsListView.setOrientation(Orientation.VERTICAL);

		Members members = new Members();
		ArrayList<Model> allUsers = members.all();

		ArrayList<String> IDsList = new ArrayList<>();
		ArrayList<String> firstNamesList = new ArrayList<>();
		ArrayList<String> lastNamesList = new ArrayList<>();
		ArrayList<String> emailsList = new ArrayList<>();
		ArrayList<String> paymentMethodsList = new ArrayList<>();

		for (Model user : allUsers) {
			Members member = (Members) user;
			IDsList.add(member.ID);
			firstNamesList.add(member.firstName);
			lastNamesList.add(member.lastName);
			emailsList.add(member.email);
			paymentMethodsList.add(member.paymentMethod);
		}

		ObservableList<String> IDsListViewItems = FXCollections.observableArrayList(IDsList);
		ObservableList<String> firstNamesListViewItems = FXCollections.observableArrayList(firstNamesList);
		ObservableList<String> lastNamesListViewItems = FXCollections.observableArrayList(lastNamesList);
		ObservableList<String> emailsListViewItems = FXCollections.observableArrayList(emailsList);
		ObservableList<String> paymentMethodsListViewItems = FXCollections.observableArrayList(paymentMethodsList);

		IDsListView.setItems(IDsListViewItems);
		firstNamesListView.setItems(firstNamesListViewItems);
		lastNamesListView.setItems(lastNamesListViewItems);
		emailsListView.setItems(emailsListViewItems);
		paymentMethodsListView.setItems(paymentMethodsListViewItems);

		// This sets the initial height of the ListView:
		IDsListView.setPrefHeight(IDsListViewItems.size() * ROW_HEIGHT + 2);
		firstNamesListView.setPrefHeight(firstNamesListViewItems.size() * ROW_HEIGHT + 2);
		lastNamesListView.setPrefHeight(lastNamesListViewItems.size() * ROW_HEIGHT + 2);
		emailsListView.setPrefHeight(emailsListViewItems.size() * ROW_HEIGHT + 2);
		paymentMethodsListView.setPrefHeight(paymentMethodsListViewItems.size() * ROW_HEIGHT + 2);

		IDsListViewAndLabel.getChildren().addAll(IDsListViewLabel, IDsListView);
		firstNamesListViewAndLabel.getChildren().addAll(firstNamesListViewLabel, firstNamesListView);
		lastNamesListViewAndLabel.getChildren().addAll(lastNamesListViewLabel, lastNamesListView);
		emailsListViewAndLabel.getChildren().addAll(emailsListViewLabel, emailsListView);
		paymentMethodsListViewAndLabel.getChildren().addAll(paymentMethodsListViewLabel, paymentMethodsListView);

		VBox parentLayout = new VBox();
		usersGridLayout.addColumn(0, IDsListViewAndLabel);
		usersGridLayout.addColumn(1, firstNamesListViewAndLabel);
		usersGridLayout.addColumn(2, lastNamesListViewAndLabel);
		usersGridLayout.addColumn(3, emailsListViewAndLabel);
		usersGridLayout.addColumn(4, paymentMethodsListViewAndLabel);
		scrollbar.setPrefHeight(10 * ROW_HEIGHT + 2);
		scrollbar.setContent(usersGridLayout);
		parentLayout.getChildren().addAll(scrollbar);

		// Display the created GUI inside a dialog box
		Dialog<ListView<String>> listUserDialog = new Dialog<>();
		listUserDialog.setTitle(Resources.getString("list_users"));
		listUserDialog.setHeaderText(Resources.getString("list_users"));
		URL url = null;
		try {
			url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/list_user.jpg").toURI()
					.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listUserDialog.setGraphic(new ImageView(url.toString()));

		// Multiple select in a list view:
		IDsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		ButtonType deleteUserButton = new ButtonType(Resources.getString("delete_user"), ButtonData.OK_DONE);

		// Dialog functionality
		listUserDialog.getDialogPane().getButtonTypes().addAll(deleteUserButton, ButtonType.CANCEL);

		// Store corresponding IDs to an ID variable
		firstNamesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			ID.add(IDsListView.getItems().get(newIndex.intValue()));
		});
		lastNamesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			ID.add(IDsListView.getItems().get(newIndex.intValue()));
		});
		emailsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			ID.add(IDsListView.getItems().get(newIndex.intValue()));
		});
		paymentMethodsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			ID.add(IDsListView.getItems().get(newIndex.intValue()));
		});

		listUserDialog.getDialogPane().setContent(parentLayout);

		listUserDialog.setResultConverter(dialogButton -> {
			if (dialogButton == deleteUserButton) {
				return IDsListView;
			}
			return null;
		});

		// Prevent the delete button from executing if a user has no fields clicked:
		Button button = (Button) listUserDialog.getDialogPane().lookupButton(deleteUserButton);
		button.addEventFilter(ActionEvent.ACTION, event -> {
			if (ID.size() == 0 && IDsListView.getSelectionModel().getSelectedItems().size() <= 0) {
				// Tell the user to select at least a single cell.
				alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), AlertType.ERROR,
						Resources.getString("error"));
				event.consume();
			}
		});

		Optional<ListView<String>> result = listUserDialog.showAndWait();

		result.ifPresent(clickedList -> {
			Members membersAgain = new Members();
			membersAgain.ID = "";
			ObservableList<String> listOfIDs = clickedList.getSelectionModel().getSelectedItems();
			boolean errorOccured = false;
			if (listOfIDs.size() != 0) {

				for (String theID : listOfIDs) {
					membersAgain.ID = theID;
					if (membersAgain.delete()) {
						Members theMember = (Members) membersAgain.where(membersAgain.ID, "=", theID).get(0);
						Log.log("Deleted Member " + theMember.email + ": " + theMember.firstName);
					} else {
						errorOccured = true;
					}
				}
			} else if (ID.size() != 0) {
				for (String theID : ID) {
					membersAgain.ID = theID;
					ArrayList<Model> membersTemp = membersAgain.where(membersAgain.ID_FIELD, "=", theID);
					if (membersAgain.delete()) {
						Members theMember = ((Members) membersTemp.get(0));
						Log.log("Deleted Member " + theMember.email + ": " + theMember.firstName);
					} else {
						errorOccured = true;
					}

				}
			} else {
				// Do nothing
				errorOccured = true;
			}
			Alert alert;

			if (errorOccured) {
				alert = new Alert(AlertType.ERROR, Resources.getString("user_could_not_be_deleted"), ButtonType.OK);
				alert.setTitle(Resources.getString("error"));
			} else {
				alert = new Alert(AlertType.INFORMATION, Resources.getString("user_deleted_successfully"),
						ButtonType.OK);
				alert.setTitle(Resources.getString("success"));
			}
			alert.setHeaderText(null);

			alert.showAndWait();

			if (alert.getResult() == ButtonType.OK) {
				alert.hide();
				return;
			}
		});

	}

	private static boolean displayUserEmailInput() {
		TextInputDialog userEmailInput = new TextInputDialog();
		userEmailInput.setTitle(Resources.getString("enter_email"));
		userEmailInput.setHeaderText(Resources.getString("enter_email"));
		Optional<String> userInput = userEmailInput.showAndWait();

		if (userInput.isPresent()) {
			Members members = new Members();
			ArrayList<Model> member = members.where(members.EMAIL_FIELD, "=", userInput.get());
			if (member.size() <= 0) {
				alertUser(
						Resources.getString("email_does_not_exist") + ". "
								+ Resources.getString("try_registering_user"),
						AlertType.ERROR, Resources.getString("error"));
				return false;
			}
			String userIDTemp = ((Members) member.get(0)).ID;
			// Check if the user meets minimum requirements (Can only borrow up to 3 books)
			BorrowedBooks books = new BorrowedBooks();
			ArrayList<Model> totalBooks = books.where(books.USER_ID_FIELD, "=", userIDTemp);
			if (totalBooks.size() >= 3) {
				alertUser(Resources.getString("user_has_exceeded_book_borrowing_limit"), AlertType.ERROR,
						Resources.getString("error"));
				return false;
			}
			userID = ((Members) member.get(0)).ID;
		} else {
			return false;
		}
		return true;
	}

	private static boolean displayUserEmailInputForBookReturn() {
		TextInputDialog userEmailInput = new TextInputDialog();
		userEmailInput.setTitle(Resources.getString("enter_email"));
		userEmailInput.setHeaderText(Resources.getString("enter_email"));
		Optional<String> userInput = userEmailInput.showAndWait();

		if (userInput.isPresent()) {
			Members members = new Members();
			ArrayList<Model> member = members.where(members.EMAIL_FIELD, "=", userInput.get());
			if (member.size() <= 0) {
				alertUser(
						Resources.getString("email_does_not_exist") + ". "
								+ Resources.getString("try_registering_user"),
						AlertType.ERROR, Resources.getString("error"));
				return false;
			}
			userID = ((Members) member.get(0)).ID;
		} else {
			return false;
		}
		return true;
	}

	private static void returnBook() {
		if (!displayUserEmailInputForBookReturn()) {
			return;
		}

		GridPane booksListGrid = new GridPane();

		// VBoxes
		VBox bookIDAndLabel = new VBox();
		VBox bookTitleAndLabel = new VBox();
		VBox bookCategoryAndLabel = new VBox();

		// Labels
		Label bookIDLabel = new Label(Resources.getString("book_id"));
		Label bookTitleLabel = new Label(Resources.getString("book_title"));
		Label bookCategoryLabel = new Label(Resources.getString("book_category"));

		// ListViews
		ListView<String> bookIDsListView = new ListView<>();
		ListView<String> bookTitlesListView = new ListView<>();
		ListView<String> bookCategoriesListView = new ListView<>();

		// ArrayLists of data:
		ArrayList<String> bookIDsArrayList = new ArrayList<>();
		ArrayList<String> bookTitlesArrayList = new ArrayList<>();
		ArrayList<String> bookCategoriesArrayList = new ArrayList<>();

		// Populate ArrayLists:
		// Get user borrowed books:
		BorrowedBooks borrowedBooks = new BorrowedBooks();
		ArrayList<Model> userBooks = borrowedBooks.where(borrowedBooks.USER_ID_FIELD, "=", userID);
		if (userBooks.size() <= 0) {
			alertUser(Resources.getString("user_has_no_borrowed_books"), AlertType.WARNING,
					Resources.getString("warning"));
			return;
		}
		Books books = new Books();
		Categories categories = new Categories();
		for (Model userBook : userBooks) {
			BorrowedBooks aBook = (BorrowedBooks) userBook;
			ArrayList<Model> allBooks = books.where(books.ID_FIELD, "=", aBook.BookID);
			for (Model book : allBooks) {
				Books theBook = (Books) book;
				bookIDsArrayList.add(theBook.ID);
				bookTitlesArrayList.add(theBook.bookTitle);
				bookCategoriesArrayList.add(((Categories) categories
						.where(categories.ID_FIELD, "=", theBook.bookCategory).get(0)).categoryName);
			}
		}

		// Observable lists of ListViews:
		ObservableList<String> bookIDsObservableList = FXCollections.observableArrayList(bookIDsArrayList);
		ObservableList<String> bookTitlesObservableList = FXCollections.observableArrayList(bookTitlesArrayList);
		ObservableList<String> bookCategoriesObservableList = FXCollections
				.observableArrayList(bookCategoriesArrayList);

		bookIDsListView.setItems(bookIDsObservableList);
		bookTitlesListView.setItems(bookTitlesObservableList);
		bookCategoriesListView.setItems(bookCategoriesObservableList);

		bookIDAndLabel.getChildren().addAll(bookIDLabel, bookIDsListView);
		bookTitleAndLabel.getChildren().addAll(bookTitleLabel, bookTitlesListView);
		bookCategoryAndLabel.getChildren().addAll(bookCategoryLabel, bookCategoriesListView);

		booksListGrid.addColumn(0, bookIDAndLabel);
		booksListGrid.addColumn(1, bookTitleAndLabel);
		booksListGrid.addColumn(2, bookCategoryAndLabel);

		// This sets the initial height of the ListView:
		bookIDsListView.setPrefHeight(bookIDsObservableList.size() * ROW_HEIGHT + 2);
		bookTitlesListView.setPrefHeight(bookTitlesObservableList.size() * ROW_HEIGHT + 2);
		bookCategoriesListView.setPrefHeight(bookCategoriesObservableList.size() * ROW_HEIGHT + 2);

		// Listeners for the listviews clicks:
		// Store corresponding IDs to an ID variable
		bookTitlesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			bookID = bookIDsListView.getItems().get(newIndex.intValue());
		});
		bookCategoriesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			bookID = bookIDsListView.getItems().get(newIndex.intValue());
		});
		bookIDsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			bookID = bookIDsListView.getItems().get(newIndex.intValue());
		});

		// Dialog buttons:
		ButtonType borrowButton = new ButtonType(Resources.getString("return"), ButtonData.OK_DONE);

		// The Dialog in which we display our list
		Dialog<ListView<String>> dialog = new Dialog<>();
		dialog.setTitle(Resources.getString("return_book"));
		dialog.setHeaderText(Resources.getString("return_book"));
		URL url = null;
		try {
			url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/borrow_book.jpg").toURI()
					.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dialog.setGraphic(new ImageView(url.toString()));
		dialog.getDialogPane().getButtonTypes().add(borrowButton);
		dialog.getDialogPane().setContent(booksListGrid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == borrowButton) {
				return bookIDsListView;
			}
			return null;
		});

		// Prevent the borrow button from executing if a user has no fields clicked:
		Button button = (Button) dialog.getDialogPane().lookupButton(borrowButton);
		button.addEventFilter(ActionEvent.ACTION, event -> {
			if (bookID.equals("") && bookIDsListView.getSelectionModel().getSelectedItems().size() <= 0) {
				// Tell the user to select at least a single cell.
				alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), AlertType.ERROR,
						Resources.getString("error"));
				event.consume();
			}
		});

		Optional<ListView<String>> result = dialog.showAndWait();

		result.ifPresent(clickedList -> {
			BorrowedBooks borrowedBookss = new BorrowedBooks();
			boolean errorOccured = false;

			ArrayList<Model> theBooks = borrowedBookss.whereAnd(borrowedBookss.BOOK_ID_FIELD, "=", bookID,
					borrowedBookss.USER_ID_FIELD, "=", userID);

			// Check if borrowed book has surpassed deadline:
			Date currentDate = new Date(System.currentTimeMillis());
			BorrowedBooks theBorrowedBook = (BorrowedBooks) theBooks.get(0);
			if (theBorrowedBook.bookDeadline.compareTo(currentDate) > 0
					|| theBorrowedBook.bookDeadline.compareTo(currentDate) == 0) {
				// Deadline is on or after the current date, so no fine

			} else {
				// User incurs fine
				alertUser(Resources.getString("user_should_pay_fine"), AlertType.INFORMATION,
						Resources.getString("warning"));
			}
			if (!theBorrowedBook.delete()) {
				errorOccured = true;
			}
			Alert alert;

			if (errorOccured) {
				alert = new Alert(AlertType.ERROR, Resources.getString("book_could_not_be_returned"), ButtonType.OK);
				alert.setTitle(Resources.getString("error"));
			} else {
				alert = new Alert(AlertType.INFORMATION, Resources.getString("book_returned_successfully"),
						ButtonType.OK);
				alert.setTitle(Resources.getString("success"));
			}
			alert.setHeaderText(null);

			alert.showAndWait();

			if (alert.getResult() == ButtonType.OK) {
				alert.hide();
				return;
			}
		});
	}

	private static void borrowBook() {
		if (!displayUserEmailInput()) {
			return;
		}
		ScrollPane scrollPane = new ScrollPane();
		VBox parentLayout = new VBox();
		GridPane booksListGrid = new GridPane();

		// Search
		HBox search = new HBox();
		search.prefWidthProperty().bind(parentLayout.prefWidthProperty());
		search.setAlignment(Pos.CENTER);
		search.setPadding(COLUMNS);
		Button searchButton = new Button(Resources.getString("search"));
		TextField searchInput = new TextField();
		search.getChildren().addAll(searchInput, searchButton);

		// VBoxes
		VBox bookIDAndLabel = new VBox();
		VBox bookTitleAndLabel = new VBox();
		VBox bookCategoryAndLabel = new VBox();
		VBox bookTotalAndLabel = new VBox();

		// Labels
		Label bookIDLabel = new Label(Resources.getString("book_id"));
		Label bookTitleLabel = new Label(Resources.getString("book_title"));
		Label bookCategoryLabel = new Label(Resources.getString("book_category"));
		Label bookTotalLabel = new Label(Resources.getString("book_total"));

		// ListViews
		ListView<String> bookIDsListView = new ListView<>();
		ListView<String> bookTitlesListView = new ListView<>();
		ListView<String> bookCategoriesListView = new ListView<>();
		ListView<String> bookTotalsListView = new ListView<>();

		// ArrayLists of data:
		ArrayList<String> bookIDsArrayList = new ArrayList<>();
		ArrayList<String> bookTitlesArrayList = new ArrayList<>();
		ArrayList<String> bookCategoriesArrayList = new ArrayList<>();
		ArrayList<String> bookTotalsArrayList = new ArrayList<>();

		// Populate ArrayLists:
		Books books = new Books();
		ArrayList<Model> allBooks = books.all();
		Categories categories = new Categories();
		for (Model book : allBooks) {
			Books theBook = (Books) book;
			bookIDsArrayList.add(theBook.ID);
			bookTitlesArrayList.add(theBook.bookTitle);
			bookCategoriesArrayList.add(((Categories) categories.where(categories.ID_FIELD, "=", theBook.bookCategory)
					.get(0)).categoryName);
			bookTotalsArrayList.add("" + theBook.bookTotal);
		}
		// Observable lists of ListViews:
		ObservableList<String> bookIDsObservableList = FXCollections.observableArrayList(bookIDsArrayList);
		ObservableList<String> bookTitlesObservableList = FXCollections.observableArrayList(bookTitlesArrayList);
		ObservableList<String> bookCategoriesObservableList = FXCollections
				.observableArrayList(bookCategoriesArrayList);
		ObservableList<String> bookTotalsObservableList = FXCollections.observableArrayList(bookTotalsArrayList);

		bookIDsListView.setItems(bookIDsObservableList);
		bookTitlesListView.setItems(bookTitlesObservableList);
		bookCategoriesListView.setItems(bookCategoriesObservableList);
		bookTotalsListView.setItems(bookTotalsObservableList);

		bookIDAndLabel.getChildren().addAll(bookIDLabel, bookIDsListView);
		bookTitleAndLabel.getChildren().addAll(bookTitleLabel, bookTitlesListView);
		bookCategoryAndLabel.getChildren().addAll(bookCategoryLabel, bookCategoriesListView);
		bookTotalAndLabel.getChildren().addAll(bookTotalLabel, bookTotalsListView);

		booksListGrid.addColumn(0, bookIDAndLabel);
		booksListGrid.addColumn(1, bookTitleAndLabel);
		booksListGrid.addColumn(2, bookCategoryAndLabel);
		booksListGrid.addColumn(3, bookTotalAndLabel);

		scrollPane.setPrefHeight(10 * ROW_HEIGHT + 2);
		scrollPane.setContent(booksListGrid);

		parentLayout.getChildren().addAll(search, scrollPane);

		// This sets the initial height of the ListView:
		bookIDsListView.setPrefHeight(bookIDsObservableList.size() * ROW_HEIGHT + 2);
		bookTitlesListView.setPrefHeight(bookTitlesObservableList.size() * ROW_HEIGHT + 2);
		bookCategoriesListView.setPrefHeight(bookCategoriesObservableList.size() * ROW_HEIGHT + 2);
		bookTotalsListView.setPrefHeight(bookTotalsObservableList.size() * ROW_HEIGHT + 2);

		// Listeners for the listviews clicks:
		// Store corresponding IDs to an ID variable
		bookTitlesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			bookID = bookIDsListView.getItems().get(newIndex.intValue());
		});
		bookCategoriesListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			bookID = bookIDsListView.getItems().get(newIndex.intValue());
		});
		bookTotalsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			bookID = bookIDsListView.getItems().get(newIndex.intValue());
		});
		bookIDsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
			bookID = bookIDsListView.getItems().get(newIndex.intValue());
		});

		// Dialog buttons:
		ButtonType borrowButton = new ButtonType(Resources.getString("borrow"), ButtonData.OK_DONE);

		// The Dialog in which we display our list
		Dialog<ListView<String>> dialog = new Dialog<>();
		dialog.setTitle(Resources.getString("borrow_book"));
		dialog.setHeaderText(Resources.getString("borrow_book"));
		URL url = null;
		try {
			url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/borrow_book.jpg").toURI()
					.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dialog.setGraphic(new ImageView(url.toString()));
		dialog.getDialogPane().setContent(parentLayout);
		dialog.getDialogPane().getButtonTypes().add(borrowButton);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == borrowButton) {
				return bookIDsListView;
			}
			return null;
		});

		// Prevent the borrow button from executing if a user has no fields clicked:
		Button button = (Button) dialog.getDialogPane().lookupButton(borrowButton);
		button.addEventFilter(ActionEvent.ACTION, event -> {
			if (bookID.equals("") && bookIDsListView.getSelectionModel().getSelectedItems().size() <= 0) {
				// Tell the user to select at least a single cell.
				alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), AlertType.ERROR,
						Resources.getString("error"));
				event.consume();
			}
		});

		Optional<ListView<String>> result = dialog.showAndWait();

		result.ifPresent(clickedList -> {
			BorrowedBooks borrowedBooks = new BorrowedBooks();

			// Check if the user already has this book:
			if (borrowedBooks
					.whereAnd(borrowedBooks.BOOK_ID_FIELD, "=", bookID, borrowedBooks.USER_ID_FIELD, "=", userID)
					.size() >= 1) {
				alertUser(Resources.getString("user_has_this_book"), AlertType.ERROR, Resources.getString("error"));
				return;
			}
			boolean errorOccured = false;
			borrowedBooks.BookID = bookID;
			borrowedBooks.UserID = userID;
			// 2 weeks in milliseconds: 1209600000
			borrowedBooks.bookDeadline = new Date(System.currentTimeMillis() + 1209600000);
			if (!borrowedBooks.save()) {
				errorOccured = true;
			}
			Alert alert;

			if (errorOccured) {
				alert = new Alert(AlertType.ERROR, Resources.getString("book_could_not_be_borrowed"), ButtonType.OK);
				alert.setTitle(Resources.getString("error"));
			} else {
				alert = new Alert(AlertType.INFORMATION, Resources.getString("book_borrowed_successfully") + 
						"\r\n" + Resources.getString("deadline") + ": " + borrowedBooks.bookDeadline,
						ButtonType.OK);
				alert.setTitle(Resources.getString("success"));
			}
			alert.setHeaderText(null);

			alert.showAndWait();

			if (alert.getResult() == ButtonType.OK) {
				alert.hide();
				return;
			}
		});

	}

	public static void alertUser(String message, AlertType alertType, String title) {
		Alert alert = new Alert(alertType, message, ButtonType.OK);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.OK) {
			alert.hide();
			return;
		}
	}

	public static boolean isEmailValid(String email) {
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

}
