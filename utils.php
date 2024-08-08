<?php

function respond($status, $message) {
    http_response_code($status);
    echo json_encode(['message' => $message]);
}