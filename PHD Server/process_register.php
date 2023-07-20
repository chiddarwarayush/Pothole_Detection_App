<?php
if(isset($_REQUEST['fullname']) && isset($_REQUEST['mobilenum']) && isset($_REQUEST['username']) && isset($_REQUEST['password']) && !empty($_REQUEST['fullname']) && !empty($_REQUEST['mobilenum']) && !empty($_REQUEST['username']) && !empty($_REQUEST['password'])){

	extract($_REQUEST);
	
$con=mysqli_connect("localhost","u700859437_phd","QJsR8Uj>","u700859437_phd");
$con=mysqli_connect("localhost","u118398853_chiddarwarayus","he^Iy3i4q=U","u118398853_chiddarwarayus");

	$password=md5($password); 
$sql="INSERT INTO user(full_name,mobile_number,username,password) VALUES('$fullname','$mobilenum','$username','$password')";

$responce_data=array();

if(mysqli_query($con,$sql)){
	array_push($responce_data,array('status'=>'success','msg'=>''));
	echo json_encode(array('server_response'=>$responce_data));
	die();
}else{
	$eoor=mysqli_error($con);
array_push($responce_data,array('status'=>'error','msg'=>$eoor));
	echo json_encode(array('server_response'=>$responce_data));
	die();
}

}

//http://www.phd.com/process_register.php?fullname=&mobilenum=&username=&password=123	
//http://www.phd.com/process_login.php?username=&password=123	
?> 

