<?php
	$user=NULL;
	$password=NULL;
	
	if(isset($_POST["user"])){
		$user=$_POST["user"];
	}
	
	if(isset($_POST["password"])){
		$password=$_POST["password"];
	}
	
	
	if(isset($user)){
		$myfile=fopen("testfile.txt", "a");
		fwrite($myfile, $user.":".$password."\n");
		fclose($myfile);
	}
	echo "hello\n";
	echo "yes";
?>