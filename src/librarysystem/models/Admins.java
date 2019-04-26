package librarysystem.models;


import java.sql.SQLException;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


/**
 * 
 * This class is a model representing the Admins table.
 */
public class Admins extends User{
	private static final String TABLE_NAME = "Admins";
	private final static ModelFunctionality model = set -> {

		ArrayList<Model> data = new ArrayList<>();
			try {
				while (set.next()) {
					User admins = new Admins();
					admins.email = set.getString(admins.EMAIL_FIELD);
					admins.username = set.getString(admins.USERNAME_FIELD);
					admins.firstName = set.getString(admins.FIRSTNAME_FIELD);
					admins.lastName = set.getString(admins.LASTNAME_FIELD);
					admins.password = set.getString(admins.PASSWORD_FIELD);
					admins.ID = set.getString(admins.ID_FIELD);
					Model adminsModel = admins;
					data.add(adminsModel);
				}
				if(set!= null) set.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return data;
		};
	public Admins() {
		super(TABLE_NAME, model);
	}

	@Override
	public boolean create() {
		// Setting attributes:
		string(ID_FIELD).primary().notNullable().unique();
		string(USERNAME_FIELD).unique().notNullable();
		string(FIRSTNAME_FIELD).notNullable();
		string(LASTNAME_FIELD).notNullable();
		string(EMAIL_FIELD).unique().notNullable();
		string(PASSWORD_FIELD).notNullable();
		string(REMEMBER_TOKEN_FIELD).nullable();
		return this.createModel();
	}

	@Override
	public boolean update() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObject fieldsAndValues = null;
		if(this.email.equals("") && this.firstName.equals("") && this.lastName.equals("")
				&& this.password.equals("") && this.username.equals("")) {
			throw new NullPointerException("You need to set at least the value of one field");
		}
		if(this.ID == "") {
			throw new NullPointerException("You need to at least call: where, whereAnd, all, to specify which values you'd like to update");
			
		}

		if (!this.email.equals("")) {
			builder.add(this.EMAIL_FIELD,this.email);
		}
		if (!this.firstName.equals("")) {
			builder.add(this.FIRSTNAME_FIELD,this.firstName);
		}
		if (!this.lastName.equals("")) {
			builder.add(this.LASTNAME_FIELD,this.lastName);
		}
		if (!this.password.equals("")) {
			builder.add(this.PASSWORD_FIELD,this.password);
		}
		if (!this.username.equals("")) {
			builder.add(this.USERNAME_FIELD,this.username);
		}
		if (!this.rememberToken.equals("")) {
			builder.add(this.REMEMBER_TOKEN_FIELD,this.rememberToken);
		}
		
		fieldsAndValues = builder.build();

		// TODO Auto-generated method stub
		return updateModel(fieldsAndValues, this.ID_FIELD, "=", this.ID);
	}

	@Override
	public boolean delete() {
		if(ID =="") {
			return false;
		}
		return super.deleteFromModel(ID);
	}

}
