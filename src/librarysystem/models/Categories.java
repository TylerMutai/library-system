package librarysystem.models;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


public class Categories extends Model {
	private final static String TABLE_NAME = "Categories";
	public String ID ="";
	public String categoryName = "";
	
	public final String ID_FIELD="ID";
	public final String CATEGORY_NAME_FIELD = "category";
	
	private final static ModelFunctionality model = set -> {

		ArrayList<Model> data = new ArrayList<>();
			try {
				while (set.next()) {
					Categories admins = new Categories();
					admins.categoryName = set.getString(admins.CATEGORY_NAME_FIELD);
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

	public Categories() {
		super(TABLE_NAME, model);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create() {
		// Setting attributes:
				string(ID_FIELD).primary().notNullable().unique();
				string(CATEGORY_NAME_FIELD).notNullable();
				return this.createModel();
	}

	@Override
	public boolean update() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObject fieldsAndValues = null;
		if(this.categoryName == "") {
			throw new NullPointerException("You need to set at least the value of one field");
		}
		if(this.ID == "") {
			throw new NullPointerException("You need to at least call: where, whereAnd, all, to specify which values you'd like to update");
		}
		
		builder.add(this.CATEGORY_NAME_FIELD,this.categoryName);
		fieldsAndValues = builder.build();

		// TODO Auto-generated method stub
		return updateModel(fieldsAndValues, this.ID_FIELD, "=", this.ID);
	}

	@Override
	public boolean save() {
		//Perform checks to ensure fields have been set correctly
		if(categoryName == "") {
			throw new NullPointerException("One or more field values have not been set. If you're trying to update fields, use the update method instead.");
		}
		JsonObject fieldsAndValues = null;
		JsonObjectBuilder builder = Json.createObjectBuilder();
		ID = generateID();
		builder.add(ID_FIELD, ID);
		builder.add(CATEGORY_NAME_FIELD, categoryName);
		fieldsAndValues = builder.build();
		
		return super.saveModelData(fieldsAndValues);
	}

	@Override
	public boolean delete() {
		if(ID =="") {
			return false;
		}
		return super.deleteFromModel(ID);
	}

}
