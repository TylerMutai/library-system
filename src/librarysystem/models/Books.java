package librarysystem.models;

import java.sql.SQLException;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Books extends Model {
	private static final String TABLE_NAME = "Books";
	
	public String ID ="";
	public String bookTitle = "";
	public String bookCategory = "";
	public String bookAuthors = "";
	public int bookTotal = 0;
	
	public final String ID_FIELD="ID";
	public final String BOOK_TITLE_FIELD = "bookTitle";
	public final String BOOK_CATEGORY_FIELD = "bookCategory"; //actually stores the category ID
	public final String BOOK_AUTHORS_FIELD = "bookAuthors";
	public final String BOOK_TOTAL_FIELD ="bookTotal";
	
	private final static ModelFunctionality model = set -> {

		ArrayList<Model> data = new ArrayList<>();
			try {
				while (set.next()) {
					Books admins = new Books();
					admins.bookTitle = set.getString(admins.BOOK_TITLE_FIELD);
					admins.bookCategory = set.getString(admins.BOOK_CATEGORY_FIELD);
					admins.bookAuthors = set.getString(admins.BOOK_AUTHORS_FIELD);
					admins.bookTotal = Integer.parseInt(set.getString(admins.BOOK_TOTAL_FIELD));
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
		
		public Books() {
		super(TABLE_NAME, model);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create() {
		// Setting attributes:
		string(ID_FIELD).primary().notNullable().unique();
		string(BOOK_TITLE_FIELD).notNullable();
		string(BOOK_CATEGORY_FIELD).notNullable();
		string(BOOK_AUTHORS_FIELD).notNullable();
		string(BOOK_TOTAL_FIELD).notNullable();
		return this.createModel();
	}

	@Override
	public boolean update() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObject fieldsAndValues = null;
		if(this.bookAuthors.equals("") && this.bookCategory.equals("") && this.bookTitle.equals("") && this.bookTotal == 0) {
			throw new NullPointerException("You need to set at least the value of one field");
		}
		if(this.ID == "") {
			throw new NullPointerException("You need to at least call: where, whereAnd, all, to specify which values you'd like to update");
		}

		if (!this.bookAuthors.equals("")) {
			builder.add(this.BOOK_AUTHORS_FIELD,this.bookAuthors);
		}
		if (!this.bookCategory.equals("")) {
			builder.add(this.BOOK_CATEGORY_FIELD,this.bookCategory);
		}
		if (!this.bookTitle.equals("")) {
			builder.add(this.BOOK_TITLE_FIELD,this.bookTitle);
		}
		if (!(this.bookTotal == 0)) {
			builder.add(this.BOOK_TOTAL_FIELD,this.bookTotal);
		}
		fieldsAndValues = builder.build();

		// TODO Auto-generated method stub
		return updateModel(fieldsAndValues, this.ID_FIELD, "=", this.ID);
	}

	@Override
	public boolean save() {
		//Perform checks to ensure fields have been set correctly
		if(bookCategory=="" || bookTitle=="" || bookAuthors.equals("")) {
			throw new NullPointerException("One or more field values have not been set. If you're trying to update fields, use the update method instead.");
		}
		JsonObject fieldsAndValues = null;
		JsonObjectBuilder builder = Json.createObjectBuilder();
		ID = generateID();
		builder.add(ID_FIELD, ID);
		builder.add(BOOK_CATEGORY_FIELD, bookCategory);
		builder.add(BOOK_TITLE_FIELD, bookTitle);
		builder.add(BOOK_TOTAL_FIELD, 0 + "");
		builder.add(BOOK_AUTHORS_FIELD, bookAuthors);
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
