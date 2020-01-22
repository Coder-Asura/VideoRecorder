<?php
/**
 * Created by PhpStorm.
 * User: LOGAN
 * Date: 1/21/2020
 * Time: 3:47 PM
 */


$target_dir = "uploaded/";


if (!file_exists($target_dir)) {
    mkdir($target_dir);
}


$target_file = $target_dir . basename($_FILES["file"]["name"]);
$uploadOk = 1;
$imageFileType = strtolower(pathinfo($target_file, PATHINFO_EXTENSION));
// Check if image file is a actual image or fake image

$error = array();

if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file)) {
    $error['error'] = false;
} else {
	$error['error'] = false;
}

print json_encode($error);