<?php
// Upload script to upload Whatsapp database
// This script is for testing purposes only.
 
$uploaddir = "/home/tadl/whatsapp/";
 
if ($_FILES["file"]["error"] > 0)
  {
  echo "Error: " . $_FILES["file"]["error"] . "<br>";
  }
else
  {
  $uploadfile = $uploaddir . $_GET['n'] . "." . basename($_FILES['file']['name']);
  move_uploaded_file($_FILES['file']['tmp_name'], $uploadfile);
  }
?>
