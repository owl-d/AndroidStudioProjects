<?php
$connect = mysqli_connect("localhost", "sequence", "password", "test");
mysqli_query($connect, 'SET NAMES utf8');

$number = $_POST['number'];

if ($number != '') {
	$query = "DELETE FROM list WHERE _list = '$number'";
}

if (mysqli_query($connect, $query)) {
	echo '삭제';
} else {
	echo '삭제 실패';
}

mysqli_close($connect);
?>