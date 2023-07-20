<?php

if(isset($_REQUEST['username']) && !empty($_REQUEST['username']) && isset($_REQUEST['password']) && !empty($_REQUEST['password'])){
	
	extract($_REQUEST);
		
	$pwd=md5($password);
	
$con=mysqli_connect("localhost","u700859437_phd","QJsR8Uj>","u700859437_phd");
$con=mysqli_connect("localhost","u118398853_chiddarwarayus","he^Iy3i4q=U","u118398853_chiddarwarayus");

	$sql="SELECT * FROM user WHERE username='{$username}' AND password='{$pwd}'";
	
	$res=mysqli_query($con,$sql);
	
	$responce_data=array();
	
	if(mysqli_num_rows($res)>0){
		
		while($row=mysqli_fetch_array($res,MYSQLI_ASSOC)){
array_push($responce_data,array('status'=>'success','msg'=>'','user_id'=>$row['user_id']));
echo json_encode(array('server_response'=>$responce_data));
die();			
		}
		

		
	}else{
		
array_push($responce_data,array('status'=>'error','msg'=>'Wrong Username or Password','user_id'=>''));
	echo json_encode(array('server_response'=>$responce_data));
	die();
	
	}
	
}else{
	echo"111";
}

?> 