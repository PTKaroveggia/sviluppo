package com.pharmathek.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.pharmathek.db.bean.MagoOrdine;

public class JdbcMsSql {

	private static Connection connObj = null;
	
	private static String dbUser = "sa";
	private static String dbPasswrd = "cpn053K1";

	private static String JDBC_URL = "jdbc:sqlserver://10.0.0.17:1433;" + "databaseName=PHARMATHEK_4;"
			+ "user="+dbUser+";" + "password="+dbPasswrd+";"
	;

	public static Connection getDbConnection() {
		if (connObj == null) {
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				connObj = DriverManager.getConnection(JDBC_URL);
				if (connObj != null) {
					DatabaseMetaData metaObj = (DatabaseMetaData) connObj.getMetaData();
					System.out.println("Driver Name?= " + metaObj.getDriverName() + ", Driver Version?= "
							+ metaObj.getDriverVersion() + ", Product Name?= " + metaObj.getDatabaseProductName()
							+ ", Product Version?= " + metaObj.getDatabaseProductVersion());
				}
			} catch (Exception sqlException) {
				sqlException.printStackTrace();
			}
		}
		return connObj;
	}

	public static ResultSet query() throws SQLException {
		Statement stmt = getDbConnection().createStatement();
		return stmt.executeQuery("select * from ptk_schedecosto");
	}
	
	
	public static MagoOrdine readOrdine(String nrOrdine) throws SQLException {
		MagoOrdine ord = new MagoOrdine();
		ord.setNrOrdine(nrOrdine);
		
		String qry = "Select * from MA_SaleOrd where InternalOrdNo = ?";  
		
		PreparedStatement stmt = getDbConnection().prepareStatement(qry); 
		stmt.setString(1, nrOrdine);
		ResultSet rs = stmt.executeQuery();
		 while (rs.next()) {
			 ord.setSaleOrdId(rs.getInt("SaleOrdId"));
             ord.setPriorita(rs.getInt("Priority"));
             ord.setData(rs.getDate("ConfirmedDeliveryDate"));
             ord.setRecordExist(true); 
         }
		
		return ord; 
		
	}
	
	
	public static boolean updateDataOrdine(MagoOrdine ord, java.util.Date dataConferma) throws SQLException {
		
		
		String qry = "update MA_SaleOrd set ConfirmedDeliveryDate = ? where SaleOrdId = ?";  
		Connection con = getDbConnection(); 
		
		con.setAutoCommit(false);
		
		try {
			PreparedStatement stmt = getDbConnection().prepareStatement(qry);
			
			java.sql.Date datalocale = null;
			
			if (dataConferma != null) {
				datalocale = new Date(dataConferma.getTime());
			} 
			
			stmt.setDate(1, (Date) datalocale);
			stmt.setInt(2, ord.getSaleOrdId());
			
			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			con.rollback();
			throw e; 
		}
		
		return true;
		
	}
	

}
