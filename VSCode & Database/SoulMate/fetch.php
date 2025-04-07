
<?php
include 'db_config.php';

$sql = "SELECT * FROM products";
$result = mysqli_query($conn, $sql);
$data = array();
while ($row = mysqli_fetch_assoc($result)) {
    $data[] = $row;
}
echo json_encode($data);
mysqli_close($conn);
?>