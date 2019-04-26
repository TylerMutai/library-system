package librarysystem.models;

import java.security.SecureRandom;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * 
 * This class represents a blueprint of all employees/admins in our library
 * system
 * 
 **/

public abstract class User extends Model {

	protected User(String modelName, ModelFunctionality model) {
		super(modelName, model);
		ID = generateID();
	}
	public final String ID_FIELD = "ID";
	public final String USERNAME_FIELD = "username";
	public final String FIRSTNAME_FIELD = "firstName";
	public final String LASTNAME_FIELD = "lastName";
	public final String EMAIL_FIELD = "email";
	public final String PASSWORD_FIELD = "password";
	public final String REMEMBER_TOKEN_FIELD = "rememberToken";
	
	public String ID = "";
	public String username = "";
	public String firstName = "";
	public String lastName = "";
	public String email = "";
	public String password = "";
	public String rememberToken = "";
	public String generatedPassword = "";
	/* Set of symbols we allow as passwords */
	private static final String symbols = "ABCDEFGJKLMNPRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

	private final Random random = new SecureRandom();
	

	// Passwords can be auto generated or defined.
	public void hashPassword(String password) {
		String hashedPassword = "";
		//Hashing passwords for extra security.
		
		password = hashedPassword;
	}

	public void getPassword() {
		String generatedPassword = "";
		// automatically generated passwords will be a length of 10 characters.
		generatedPassword += generateString();
		this.generatedPassword = generatedPassword;
		hashPassword(generatedPassword);
	}
	
	public String generateString() {
		char[] buf = new char[10];
		for (int idx = 0; idx < buf.length; ++idx)
			buf[idx] = symbols.charAt(random.nextInt(symbols.length()));
		return new String(buf);
	}
	

	protected String returnGeneratedPassword() {
		return this.generatedPassword;
	}
	
	//This method saves records to the database
	@Override
	public boolean save() {
		//Perform checks to ensure fields have been set correctly
		if(username=="" || firstName=="" || lastName=="" || email=="" ||password=="") {
			throw new NullPointerException("One or more field values have not been set. If you're trying to update fields, use the update method instead.");
		}
		JsonObject fieldsAndValues = null;
		JsonObjectBuilder builder = Json.createObjectBuilder();
		ID = generateID();
		builder.add(ID_FIELD, ID);
		builder.add(USERNAME_FIELD, username);
		builder.add(FIRSTNAME_FIELD, firstName);
		builder.add(LASTNAME_FIELD, lastName);
		builder.add(EMAIL_FIELD, email);
		builder.add(PASSWORD_FIELD, password);
		fieldsAndValues = builder.build();
		
		return super.saveModelData(fieldsAndValues);
	}

}
