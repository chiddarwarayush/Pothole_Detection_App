<?php
if(isset($_REQUEST['userid']) && !empty($_REQUEST['userid'])){
    $uid=$_REQUEST['userid'];
}else{
    $uid=null;
}

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>Untitled Document</title>


</head>
<body style=" background: url('./bg7.jpeg') no-repeat center center fixed; 
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;">
    <b>

<?php 
$con=mysqli_connect("localhost","u118398853_chiddarwarayus","he^Iy3i4q=U","u118398853_chiddarwarayus");

$sql="SELECT * FROM phdata WHERE user_id='{$uid}' ORDER BY date_ad_time ASC";

$res=mysqli_query($con,$sql);
if(mysqli_num_rows($res)>0){
    $dat=null;
    $count=0;
    $newcounter=1;
    while($row=mysqli_fetch_array($res,MYSQLI_ASSOC)){
        extract($row);
        $newdt= substr($date_ad_time,0,10);
        if($count==0){
            $dat=$newdt;
            $newcounter=1;
        }
        if($dat==$newdt){
        
if($newcounter==1){
          ?>
<table width="100%" border="1px solid black" cellspacing="5" cellpadding="5" class="table">
  <tr>
    <td colspan="2"><div align="left">DATE : <?php echo"$newdt"; ?></div></td>
    <td width="45%"><div align="right"></div></td>
  </tr>
  <tr>
    <td width="8%"><div align="center">SR. NO </div></td>
    <td width="47%"><div align="center">LOCATION</div></td>
    <td><div align="center">INTENSITY</div></td>
  </tr>
<?php    
}        
          ?>
  <tr>
    <td><div align="center"><?php echo $newcounter; ?></div></td>
    <td><div align="center"><?php if(isset($latitude) && isset($longitude)){
        ?>
        <a href="https://www.google.co.in/maps/@<?php echo"$latitude"; ?>,<?php echo"$longitude"; ?>,18z" target="_blank">
        <?php
$url = "https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=$latitude&longitude=$longitude&localityLanguage=en";
      $json = file_get_contents($url);
    
      $data=json_decode($json,true);
   echo  $Address=$data['localityInfo']['administrative'][3]['name'];
   echo", <br/>";
   echo  $District=$data['localityInfo']['administrative'][2]['name'];
   echo", <br/>";
    echo $State=$data['localityInfo']['administrative'][1]['name'];
   echo", <br/>";
    echo $country=$data['localityInfo']['administrative'][0]['name'];
   
    
    
     ?></a>
     <?php
    } ?></div></td>
    <td><div align="center"><?php echo"$intensity"; ?></div></td>
  </tr>
          <?php
            
        $newcounter=$newcounter+1;    
        }else{
            ?>
           </table> 
            <?php
            $dat=$newdt;
            $newcounter=1;
            
            if($newcounter==1){
          ?>
          <br/><br/>
<table width="100%" border="1px solid black" cellspacing="5" cellpadding="5" class="table">
  <tr>
    <td colspan="2"><div align="left">DATE :<?php echo"$newdt"; ?></div></td>
    <td width="45%"><div align="right"></div></td>
  </tr>
  <tr>
    <td width="8%"><div align="center">SR. NO </div></td>
    <td width="47%"><div align="center">LOCATION</div></td>
    <td><div align="center">INTENSITY</div></td>
  </tr>
<?php    
}        
          ?>
  <tr>
    <td><div align="center"><?php echo $newcounter; ?></div></td>
    <td><div align="center"><?php echo"latitude: $latitude | longitude: $longitude";?></div></td>
    <td><div align="center"><?php echo"$intensity"; ?></div></td>
  </tr>
          <?php
          
        }
        
        
       $count=$count+1; 
    }
    ?>
    </table>
    <?php
}else{
  echo"<br/><h3>No Records Found!</h3>";  
}
?>
</b>
</body>
</html>
