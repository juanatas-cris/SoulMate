<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $name = $_POST['product_name'];
    $price = $_POST['price'];
    $shoesize = $_POST['shoesize'];
    $quantity = $_POST['quantity'];

    if (!empty($name) && !empty($price) && !empty($quantity)) {
        $sql = "INSERT INTO products (product_name, price, shoesize, quantity) VALUES ('$name', '$price','$shoesize', '$quantity')";
        if (mysqli_query($conn, $sql)) {
            echo "success"; // Response to Android
        } else {
            echo "error: " . mysqli_error($conn);
        }
    } else {
        echo "error: Missing Fields";
    }
}
?>
