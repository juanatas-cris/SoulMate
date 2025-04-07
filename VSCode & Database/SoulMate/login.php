<?php
include 'db_config.php';  // Include your DB connection file

$username = $_POST['username'];
$password = $_POST['password'];

// Check if user exists
$query = "SELECT * FROM users WHERE username='$username' AND password='$password'";
$result = mysqli_query($conn, $query);

if (mysqli_num_rows($result) > 0) {
    echo "success";
} else {
    echo "error";
}
?>
