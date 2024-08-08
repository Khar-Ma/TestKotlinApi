<?php
require 'config.php';
require 'utils.php';


if ($_SERVER['REQUEST_METHOD'] == 'POST' && $_SERVER['REQUEST_URI'] == '/api/v3/test') {
    $input = json_decode(file_get_contents('php://input'), true);

    if (json_last_error() !== JSON_ERROR_NONE) {
        respond(400, 'Invalid JSON');
        exit();
    }

    $dateField = date('Y-m-d', strtotime($input['date_field']));
    $integerNumberField = $input['integer_number_field'];
    $floatNumberField = $input['float_number_field'];
    $booleanField = $input['boolean_field'];
    $textField = $input['text_field'];

    $connection = getDbConnection();
    try{
        $statement = $connection->prepare("INSERT INTO Test (date_field, integer_number_field, float_number_field, boolean_field, text_field) VALUES (?, ?, ?, ?, ?)");
        $success= $statement->execute([$dateField, $integerNumberField, $floatNumberField, $booleanField, $textField]);

        if ($success) {
            respond(201, 'Data inserted successfully');
        } else {
            respond(500, 'Failed to insert data');
        }
    }catch(PDOException $e){
        respond(500, $e->getMessage());
    }
} else {
    respond(404, 'Endpoint not found');
}

