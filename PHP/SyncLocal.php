<?php
	$android_whoscall=NULL;
	$database_host="127.0.0.1";
	$database_account="root";
	$database_password="qaz1231044";
	$database="android_whoscall";
	
	$post_user=$_POST["user"];
	$post_update_date=$_POST["updatedate"];
	$post_join_date=$_POST["joindate"];
	
	//連接資料庫:https://www.w3schools.com/php/php_mysql_select.asp
	$android_whoscall=mysqli_connect($database_host, $database_account, $database_password, $database);
	
	//先傳 user_advice_phone_number 的資訊
	//要比較時間:https://www.tutorialspoint.com/compare-date-string-with-string-from-mysql-datetime-field
	$sql_command="SELECT * FROM user_advice_phone_number WHERE Account='".$post_user."' AND date(JoinDate) > date('".$post_join_date."')";
	$result=mysqli_query($android_whoscall, $sql_command); //送出查詢
	$nums=mysqli_num_rows($result);
	echo $nums."\n";//讓 client 端知道有幾筆資料
	if($nums != 0){
		while($row=mysqli_fetch_assoc($result)){
			echo $row["Number"]."/".$row["Description"]."/".$row["JoinDate"]."\n";
		}
	}
	
	//再來傳 phone_number_information 的東西
	//一樣要比較時間
	$sql_command="SELECT * FROM phone_number_information WHERE date(UpdateDate) > date('".$post_update_date."')"; //換傳 phone_number_information 的資訊
	$result=mysqli_query($android_whoscall, $sql_command); //送出查詢
	$nums=mysqli_num_rows($result);
	echo $nums."\n";//讓 client 端知道有幾筆資料
	if($nums != 0){
		while($row=mysqli_fetch_assoc($result)){
			echo $row["Number"]."/".$row["Result"]."/".$row["UpdateDate"]."\n";
		}
	}
?>