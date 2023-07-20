<?php
if(isset($_REQUEST['user_id']) && !empty($_REQUEST['user_id']) && isset($_REQUEST['latitude']) && !empty($_REQUEST['latitude']) && isset($_REQUEST['longitude']) && !empty($_REQUEST['longitude']) && isset($_REQUEST['intensity']) && !empty($_REQUEST['intensity'])){
	
	extract($_REQUEST);
$con=mysqli_connect("localhost","u700859437_phd","QJsR8Uj>","u700859437_phd");
$con=mysqli_connect("localhost","u118398853_chiddarwarayus","he^Iy3i4q=U","u118398853_chiddarwarayus");

	$sql="INSERT INTO phdata(user_id,latitude,longitude,intensity)VALUES('$user_id','$latitude','$longitude','$intensity')";
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

//http://www.phd.com/process_phdata.php?user_id=&latitude=123&longitude=&intensity=
?>
	 