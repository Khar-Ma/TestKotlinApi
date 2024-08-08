<?php
define('DB_HOST', 'bts-db-test.cvoec4ukmsab.eu-north-1.rds.amazonaws.com');
define('DB_PORT', 3306);
define('DB_NAME', 'bts_db_test');
define('DB_USER', 'admin');
define('DB_PASS', '00000000');

function getDbConnection() {
    try {
        $dsn = "mysql:host=" . DB_HOST . ";port=" . DB_PORT . ";dbname=" . DB_NAME;
        $pdo = new PDO($dsn, DB_USER, DB_PASS);
        
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        return $pdo;
    } catch (PDOException $e) {
            echo "Connection failed: " . $e->getMessage();
    }
}