package librarysystem.models;

import java.sql.ResultSet;
import java.util.ArrayList;

public interface ModelFunctionality {
	public ArrayList<Model> process(ResultSet set);

}
