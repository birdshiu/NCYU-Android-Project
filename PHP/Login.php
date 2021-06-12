<?php
	//下面的狀態碼是從 LoginActivity.java 搬來的
	$LOGIN_RESULT_OK="0";
	$LOGIN_RESULT_NO_SUCH_ACCOUNT="1";
	$LOGIN_RESULT_INCORRECT_PASSWORD="2";

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
		$sql_command="SELECT * FROM user WHERE Account='$post_user'";
		$result=mysqli_query($android_whoscall, $sql_command); //送出查詢
		$nums=mysqli_num_rows($result); //算算有幾筆資料(最多一筆)
		
		if($nums == 0){ //沒有這個帳號
			echo $LOGIN_RESULT_NO_SUCH_ACCOUNT;
		}else{
			$rows=mysqli_fetch_assoc($result); //抓一個 rows
			$database_password=$rows["Password"];
			
			if($database_password == $post_password){ //登入成功
				echo $LOGIN_RESULT_OK;
			}else{ //登入失敗
				echo $LOGIN_RESULT_INCORRECT_PASSWORD;
			}
			
		}
	}else{
		echo $REGISTER_RESULT_SERVER_ERROR;
	}
	
	$android_whoscall=NULL; //關掉資料庫
?>