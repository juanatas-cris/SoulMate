<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "db_config";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$product_name = $_POST['product_name'];

$sql = "SELECT * FROM products WHERE product_name = '$product_name'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    echo "duplicate";
} else {
    echo "unique";
}
$conn->close();
?>
