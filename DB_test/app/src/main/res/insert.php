<?php
$connect = mysqli_connect("localhost", "sequence", "password", "test");
mysqli_query($connect, 'SET NAMES utf8');

$content = $_POST['content'];	// 포스트로 변수 받아오기

if ($content != '') {	// 사이트에 접속했을 때 공백 입력되는 거 방지
	$query = "INSERT INTO list (content) VALUES ('$content')";	// 여러개면 , 찍고 뒤에 이어서 적기
}

if (mysqli_query($connect, $query)) {	// 쿼리 실행하고 성공인지 실패인지 알려주는 조건문
	echo '성공';
} else {
	echo '실패';
}

mysqli_close($connect);
?>