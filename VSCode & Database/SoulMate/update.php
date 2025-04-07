<?php
require 'db_config.php'; // Ensure this file contains the correct DB connection settings

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get POST values from the Android app
    $id = isset($_POST['id']) ? $_POST['id'] : null;
    $product_name = isset($_POST['product_name']) ? $_POST['product_name'] : null;
    $price = isset($_POST['price']) ? $_POST['price'] : null;
    $quantity = isset($_POST['quantity']) ? $_POST['quantity'] : null;

    // Validate required fields
    if (!$id || !$product_name || !$price || !$quantity) {
        echo json_encode(["status" => "error", "message" => "❌ Missing required fields"]);
        exit();
    }

    // Prepare update query
    $sql = "UPDATE products 
            SET product_name = '$product_name', 
                price = '$price', 
                quantity = '$quantity' 
            WHERE id = $id";

    // Execute query
    if ($conn->query($sql) === TRUE) {
        echo json_encode(["status" => "success", "message" => "✅ Product updated successfully!"]);
    } else {
        echo json_encode(["status" => "error", "message" => "❌ Error: " . $conn->error]);
    }
}

// Close connection
$conn->close();
?>
