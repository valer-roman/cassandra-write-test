/**
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package ro.softgress.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ro.softgress.client.Data;

/**
 * Data acces to a RDBMS system. It is used to test the metrics writing to a rdbms.
 *  
 * @author val
 */
public class RdbmsDAO implements DAO {

	private static final String RDBMS_URL = "jdbc:mysql://localhost:3306/";
	private static final String RDBMS_DB = "demo";
	
	private Connection con = null;
	private boolean batchInsert = true;
	
	public RdbmsDAO() {
		String url = RDBMS_URL;
		String db = RDBMS_DB;
		String driver = "com.mysql.jdbc.Driver";
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url+db,"root","123456");
		}
		catch (Exception e){
			e.printStackTrace();
		}		
	}
	
	public void insertData(Data[] datas) {
		if (batchInsert) {
			insertDataBatch(datas);
		} else {
			insertDataNotBatch(datas);
		}
	}
	
	private void insertDataNotBatch(Data[] datas) {
		try{
			for (Data data : datas) {
				Statement st = con.createStatement();
				int val = st.executeUpdate(
						"INSERT metrics VALUES (" + data.getTimestamp() +"," 
						+ data.getApplicationId() + ","
						+ data.getSystemId() + ","
						+ "'" + data.getValue() + "')");
			}
		}
		catch (SQLException s){
			s.printStackTrace();
			System.out.println("SQL statement is not executed!");
		}		
	}
	
	private void insertDataBatch(Data[] datas) {
		try{
			StringBuilder sql = new StringBuilder("INSERT metrics VALUES ");
			boolean firstData = true;
			for (Data data : datas) {
				if (!firstData) {
					sql.append(",");
				} else {
					firstData = false;
				}
				sql.append("(" + data.getTimestamp() +"," 
						+ data.getApplicationId() + ","
						+ data.getSystemId() + ","
						+ "'" + data.getValue() + "')");
			}
			Statement st = con.createStatement();
			int val = st.executeUpdate(sql.toString());
		}
		catch (SQLException s){
			s.printStackTrace();
			System.out.println("SQL statement is not executed!");
		}		
	}
}
