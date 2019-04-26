package librarysystem.models;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import javax.json.JsonObject;

import librarysystem.utils.DatabaseConnection;


/** 
 * 
 * Super class for all Models (tables) created. Contains all methods necessary for 
 * performing database operations
 */
public abstract class Model {
	private String sqlFieldString="";
	private Connection con;
	private final String modelName;
	private final ModelFunctionality model;
	private static final String symbols = "ABCDEFGJKLMNPRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

	private final Random random = new SecureRandom();
	
	
	protected Model(String modelName,ModelFunctionality model) {
		this.modelName = modelName;
		this.model = model;
	}
	/*
	 * Subclasses (Models) need to implement these abstract methods.
	 * 
	 */
	
	public abstract boolean create();
	public abstract boolean update();
	public abstract boolean save();
	public abstract boolean delete();
	
	public ArrayList<Model> all(){
	ArrayList<Model> data = new ArrayList<>();
	// Get result set:
	
	data = model.process(getAll());
	try {
		con.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return data;
}
	public ArrayList<Model> where(String firstOperandField, String operator, String lastOperandField){
		ArrayList<Model> data = new ArrayList<>();
		// Get result set:
		ResultSet mySet = getWhere(firstOperandField, operator, lastOperandField);
		if(!Objects.isNull(mySet))
		data = model.process(mySet);
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	public ArrayList<Model> whereAnd(Object... vals){
		ArrayList<Model> data = new ArrayList<>();
		// Get result set:
		ResultSet mySet = getWhereAnd(vals);
		if(!Objects.isNull(mySet))
			data = model.process(mySet);
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	public boolean deleteFromModel(String... ID) {
		
		String sqlString = "Delete From " + modelName + " Where ";
		if(ID.length > 1) {
			for(int i=0; i< ID.length; i++) {
				sqlString += ID[i];
				i++;
				sqlString += ID[i];
				i++;
				sqlString += "'" + ID[i] +"' " + blankOrAnd(i,ID.length);
			}
			
		} else {
		sqlString += "ID = '" + ID[0] + "'";
		}
		try {
			if(con==null || con.isClosed()) {
				con = DatabaseConnection.getConnection();
				}
			con.prepareStatement(sqlString).executeUpdate();
			con.close();
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	protected String generateID() {
		String ID = "";
		//Our integer ids are 15 digits long and are only numerical values.
		while(ID.length()<15) ID += random.nextInt(symbols.length());
		return ID;
	}
	
	/**
	 * Creates a new table depending on the model.
	 * @return TRUE when the process of creating a table is successful.
	 */
	protected boolean createModel() {
		try {
			if(con==null || con.isClosed()) {
			con = DatabaseConnection.getConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//Bail, Since we can't possibly do anything without a database connection.
			return false;
		}
		//Using java prepared Statements:
		if(sqlFieldString != "") {
			String sqlString = "create table " + modelName +
					"(" + sqlFieldString.substring(0, sqlFieldString.length()) +  ")";
			try {
				con.prepareStatement(sqlString).executeUpdate();
				con.close();
			} catch(SQLException e) {
				e.printStackTrace();
				return false;
			}
			System.out.println("Successfully created table: " + modelName);
			return true;
		} else {
			throw new NullPointerException("You need to set field properties before creating a model");
		}
		
	}

	
	/**
	 * 
	 * @return true if data is successfully saved to table
	 */
	protected boolean saveModelData(JsonObject fieldsAndValues) {
		try {
			if(con==null || con.isClosed()) {
			con = DatabaseConnection.getConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//Bail, Since we can't possibly do anything without a database connection.
			return false;
		}
		String sqlString = "INSERT INTO " + modelName + " (";
		String sqlStringPart = " VALUES(";
		ArrayList<String> keys = new ArrayList<>();
		for(Object data:fieldsAndValues.keySet()) {
			keys.add((String) data);
		}
		Integer objectSize = fieldsAndValues.size();
		for (int i=0;i<objectSize;i++) {
			sqlString += keys.get(i) + comaOrBlank(i,objectSize);
			sqlStringPart += fieldsAndValues.get(keys.get(i)) + comaOrBlank(i,objectSize);
		}
		sqlString += ")";
		sqlStringPart += ")";
		
		try {
			con.prepareStatement(sqlString + sqlStringPart).executeUpdate();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			
			return false;
		}
		return true;
	}
	
	
	protected ResultSet getAll(){
			try {
				if(con==null || con.isClosed()) {
				con = DatabaseConnection.getConnection();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				//Bail, Since we can't possibly do anything without a database connection.
				return null;
			}
		ResultSet data = null;
		String sql = "Select * from " + modelName;
		try {
			data = con.prepareStatement(sql).executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	protected boolean updateModel(JsonObject fieldsAndValues, String firstOperandField, String operator, String lastOperandField){
		try {
			if(con==null || con.isClosed()) {
			con = DatabaseConnection.getConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//Bail, Since we can't possibly do anything without a database connection.
			return false;
		}
		String sql = "Update " + modelName + " Set ";
		ArrayList<String> keys = new ArrayList<>();
		for(Object data:fieldsAndValues.keySet()) {
			keys.add((String) data);
		}
		Integer objectSize = fieldsAndValues.size();
		for (int i=0;i<objectSize;i++) {
			sql += keys.get(i) + "=" + fieldsAndValues.get(keys.get(i)) + comaOrBlank(i,objectSize);
		}
		sql += " Where " + firstOperandField + operator + "'" + lastOperandField + "'";
		
		try {
		 con.prepareStatement(sql).executeUpdate();
		 con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	protected ResultSet getWhere(String firstOperandField, String operator, Object lastOperandField){
		try {
			if(con==null || con.isClosed()) {
			con = DatabaseConnection.getConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//Bail, Since we can't possibly do anything without a database connection.
			return null;
		}
		ResultSet data = null;
		String sql = "Select * from " + modelName + " Where " + firstOperandField + operator;
		sql += lastOperandField instanceof Integer ? lastOperandField :  "'" 
				+ lastOperandField + "'" ;
		try {
			data = con.prepareStatement(sql).executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * 
	 * @param fieldsAndOperands Strings should be passed in the order:
	 * field name, the operator, the value to compare with and so on.
	 * @return
	 */
	protected ResultSet getWhereAnd(Object... fieldsAndOperands){
		try {
			if(con==null || con.isClosed()) {
			con = DatabaseConnection.getConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//Bail, Since we can't possibly do anything without a database connection.
			return null;
		}
		ResultSet data = null;
		String sql = "Select * from " + modelName + " Where ";
		
		for(int i=0; i<fieldsAndOperands.length;i++) {
			sql += (String) fieldsAndOperands[i] + fieldsAndOperands[i+1];
			i=i+2;
			sql += fieldsAndOperands[i] instanceof Integer ? fieldsAndOperands[i] : "'" 
					+ fieldsAndOperands[i] + "'" + blankOrAnd(i,fieldsAndOperands.length);
		}
		try {
			data = con.prepareStatement(sql).executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	protected String comaOrBlank(int position, int length) {
		if(position == length -1) {
			return "";
		} else {
			return ",";
		}
	}
	private String blankOrAnd(int position, int length) {
		if(position == length -1) {
			return "";
		} else {
			return " AND ";
		}
	}
	
	
	
	protected void appendComa() {
		if(sqlFieldString != "") {
			sqlFieldString += ",";
		}
	}
	
	protected Model string(String fieldName) {
	appendComa();
	sqlFieldString += fieldName + " VARCHAR(155)";
	return this;
	}
	
	protected Model integer(String fieldName) {
	appendComa();
	sqlFieldString += fieldName + " BIGINT";
	return this;
	}

	protected Model date(String fieldName) {
	appendComa();
	sqlFieldString += fieldName + " DATE";
	return this;
	}

	protected Model timestamp(String fieldName) {
	appendComa();
	sqlFieldString += fieldName + " TIMESTAMP";
	return this;
	}
	
	protected Model nullable() {
		sqlFieldString += " NULL";
		return this;
	}
	
	protected Model notNullable() {
		sqlFieldString += " NOT NULL";
		return this;
	}
	
	protected Model primary() {
		sqlFieldString += " PRIMARY KEY";
		return this;
	}
	
	protected Model unique() {
		sqlFieldString += " UNIQUE";
		return this;
	}
}
