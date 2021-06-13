<?php
	//下面的狀態碼是從 RegisterActivity.java 搬來的
	$REGISTER_RESULT_OK="0";
	$REGISTER_RESULT_DUPLICATED="1";
	$REGISTER_RESULT_SERVER_ERROR="2";

	$android_whoscall=NULL;
	$database_host="127.0.0.1";
	$database_account="root";
	$database_password="qaz1231044";
	$database="android_whoscall";
	
	$post_user=NULL;
	$post_password=NULL;
	
	//連接資料庫:https://www.w3schools.com/php/php_mysql_select.asp
	$android_whoscall=mysqli_connect($database_host, $database_account, $database_password, $database);
	//如果資料庫沒開的話，android端會收到 "</ br>"
	
	if(isset($_POST["user"])){
		$post_user=$_POST["user"];
	}
	
	if(isset($_POST["password"])){
		$post_password=$_POST["password"];
	}
	
	if(isset($post_user) && isset($post_password)){ //正常狀況下，使用者應該都會送這兩個資料來
		$sql_command="SELECT * FROM User WHERE Account='$post_user'";
		$result=mysqli_query($android_whoscall, $sql_command); //送出查詢
		$nums=mysqli_num_rows($result); //算算有幾筆資料
		
		if($nums != 0){ //有重覆資料
			echo $REGISTER_RESULT_DUPLICATED;
		}else{
			//插入資料:https://www.w3schools.com/php/func_mysqli_prepare.asp
			$stmt=mysqli_prepare($android_whoscall, "INSERT INTO User (Account, Password) VALUES(?, ?)");
			//mysqli_stmt_bind_param : https://www.php.net/manual/en/mysqli-stmt.bind-param.php
			mysqli_stmt_bind_param($stmt, "ss", $post_user, $post_password);
			$stmt_result=mysqli_stmt_execute($stmt);
			
			if($stmt_result == 1){ //如果 SQL 指令執行成功
				echo $REGISTER_RESULT_OK;
			}else{
				echo $REGISTER_RESULT_SERVER_ERROR;
			}
			
		}
	}else{
		echo $REGISTER_RESULT_SERVER_ERROR;
	}
	
	$android_whoscall=NULL; //關掉資料庫
?>