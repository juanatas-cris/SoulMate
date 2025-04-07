<?php
// Database connection
$host = 'localhost';
$user = 'root';
$password = '';
$dbname = 'soulmate';

// Connect to MySQL
$conn = new mysqli($host, $user, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get data from POST
$id = $_POST['id'];

// Delete query
$sql = "DELETE FROM products WHERE id = '$id'";

// Check if deletion was successful
if ($conn->query($sql) === TRUE) {
    echo "Product deleted successfully!";
} else {
    echo "Error deleting product: " . $conn->error;
}

$conn->close();
?>
