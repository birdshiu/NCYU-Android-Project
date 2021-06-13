<?php
	//有錯誤才會回東西
	$android_whoscall=NULL;
	$database_host="127.0.0.1";
	$database_account="root";
	$database_password="qaz1231044";
	$database="android_whoscall";
	
	$post_user=$_POST["user"];
	$post_number=$_POST["number"];
	$post_description=$_POST["description"];
	$post_join_date=$_POST["joinDate"];
	
	$android_whoscall=mysqli_connect($database_host, $database_account, $database_password, $database);
	
	if($post_description == ""){//刪除這筆資料:https://www.w3schools.com/php/php_mysql_delete.asp
		$sql_command="DELETE FROM user_advice_phone_number WHERE Number='".$post_number."' AND Account='".$post_user."'";
		mysqli_query($android_whoscall, $sql_command);
	}else{//看看要更改還是新增資料
		$sql_command="SELECT * FROM user_advice_phone_number WHERE Number='".$post_number."' AND Account='".$post_user."'";
		$result=mysqli_query($android_whoscall, $sql_command); //送出查詢
		$nums=mysqli_num_rows($result); //算算有幾筆資料(最多一筆)
		
		if($nums != 0){//表示要做的是更新資料:https://www.w3schools.com/php/php_mysql_update.asp
			$sql_command="UPDATE user_advice_phone_number SET Description='".$post_description."', JoinDate='".$post_join_date."' WHERE Account='".$post_user."' AND Number='".$post_number."'";
			mysqli_query($android_whoscall, $sql_command);
		}
		else{
			$stmt=mysqli_prepare($android_whoscall, "INSERT INTO user_advice_phone_number (Account, Number, Description, JoinDate) VALUES(?, ?, ?, ?)");
			//mysqli_stmt_bind_param : https://www.php.net/manual/en/mysqli-stmt.bind-param.php
			mysqli_stmt_bind_param($stmt, "ssss", $post_user, $post_number, $post_description, $post_join_date);
			$stmt_result=mysqli_stmt_execute($stmt);
			
			if($stmt_result != 1){ //如果 SQL 指令執行失敗
				echo "error";
			}
		}
		
	}
	
	$android_whoscall=NULL;//關資料庫
?>