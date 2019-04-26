package librarysystem.models;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class BorrowedBooks extends Model {
	private final static String TABLE_NAME = "BorrowedBooks";
	public String BookID ="";
	public String UserID = "";
	public Date bookDeadline = null;
	
	public final String BOOK_ID_FIELD="BookID";
	public final String USER_ID_FIELD = "UserID";
	public final String BOOK_DEADLINE_FIELD = "bookDeadline";
	
	private final static ModelFunctionality model = set -> {

		ArrayList<Model> data = new ArrayList<>();
			try {
				while (set.next()) {
					BorrowedBooks admins = new BorrowedBooks();
					admins.BookID = set.getString(admins.BOOK_ID_FIELD);
					admins.UserID = set.getString(admins.USER_ID_FIELD);
					admins.bookDeadline = Date.valueOf(set.getString(admins.BOOK_DEADLINE_FIELD));
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
	
	public BorrowedBooks() {
		super(TABLE_NAME, model);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create() {
		// Setting attributes:
		string(BOOK_ID_FIELD).notNullable();
		string(USER_ID_FIELD).notNullable();
		timestamp(BOOK_DEADLINE_FIELD).notNullable();
		return this.createModel();
	}

	@Override
	public boolean update() {
		//This Model can not be updated. You can only insert values into it.
		throw new UnsupportedOperationException("You cannot update this model.");
	}

	@Override
	public boolean save() {
		//Perform checks to ensure fields have been set correctly
				if(BookID == "" || UserID == "" || Objects.isNull(bookDeadline)) {
					throw new NullPointerException("One or more field values have not been set. If you're trying to update fields, use the update method instead.");
				}
				JsonObject fieldsAndValues = null;
				JsonObjectBuilder builder = Json.createObjectBuilder();
				builder.add(BOOK_ID_FIELD, BookID);
				builder.add(USER_ID_FIELD, UserID);
				builder.add(BOOK_DEADLINE_FIELD, bookDeadline.toString());
				fieldsAndValues = builder.build();
				
				return super.saveModelData(fieldsAndValues);
	}

	@Override
	public boolean delete() {
		if(BookID =="" && UserID=="") {
			return false;
		}
		return super.deleteFromModel(this.BOOK_ID_FIELD,"=",BookID,this.USER_ID_FIELD,"=",UserID);
	}

}
