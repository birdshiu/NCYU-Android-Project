package JavaServer;

import java.sql.*;

public class JavaServer {

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");     //載入MYSQL JDBC驅動程式   
			//Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("Success loading Mysql Driver!");
			} catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
			}
	}

}
