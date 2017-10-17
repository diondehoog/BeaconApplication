<?php 
	$msg1 = $_POST['24:0A:C4:00:70:D0'];
	$str1 =  "24:0A:C4:00:70:D0 = $msg1";
	$date = date('Y-m-d h:i:s');
	//echo $date;
	$result ="[" . $date . "] " . $str1 . "\n";
		
	file_put_contents("test.txt", $result, FILE_APPEND);
	echo $result;
?>