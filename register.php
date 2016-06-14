<?php
	function validateUsername($str) {
		return preg_match("/^[A-Za-z0-9]{1,36}$/", $str);
	}

	function validatePassword($str) {
		return preg_match("/^[A-Za-z0-9]{1,36}$/", $str);
	}

	session_start();

	if ($_SERVER['REQUEST_METHOD'] == 'POST') {
		if (empty($_POST['signupUsername']) || empty($_POST['signupPassword']) || empty($_POST['signupEmail'])) {
			echo 'Error processing input.';
			exit();
		}

		$db_host = 'localhost';
		$db_user = 'root';
		$db_pass = 'sdTN3267';
		$db_name = array('test', 'tabletop');
		$db_port = array(5313, 5316);
		$db_socket = array('/var/run/mysqld/mysqld1.sock', '/var/run/mysqld/mysqld2.sock');

		$dbA = new mysqli($db_host, $db_user, $db_pass, $db_name[0], $db_port[0],$db_socket[0]);
		$dbB = new mysqli($db_host, $db_user, $db_pass, $db_name[1], $db_port[1],$db_socket[1]);
		if ($dbA->connect_error) {
			die("Database A connection failed: " . $dbA->connect_error);
		}
		if ($dbB->connect_error) {
			die("Database B connection failed: " . $dbB->connect_error);
		}
		
		echo 'Connected to both databases.';
		$username = $_POST['signupUsername'];
		if (!validateUsername($username)) {
			echo 'Invalid username' . $username;
			exit();
		}

		$password = $_POST['signupPassword'];
		if (!validatePassword($password)) {
			echo 'Invalid password.';
			exit();
		}
		$hash = password_hash($password, PASSWORD_BCRYPT);

		$email = $_POST['signupEmail'];
		if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
			echo 'Invalid email.';
			exit();
		}

		$stmt = $dbA->prepare("SELECT user_name FROM users WHERE user_name=?");
		$stmt->bind_param("s", $username);
		$stmt->execute();
		
		if (!$result = $stmt->get_result()) {
			die("Error running query.");
		}
		else {
			if ($result->num_rows) {
				echo 'Username already in use.';
				exit();
			}
		}
		
		$stmt->close();

		$stmt = $dbA->prepare("SELECT email FROM users WHERE email=?");
		$stmt->bind_param("s", $email);
		$stmt->execute();
		
		if (!$result = $stmt->get_result()) {
			die("Error running query.");
		}
		else {
			if ($result->num_rows) {
				echo 'Email already in use.';
				exit();
			}
		}

		$stmt->close();

		$fail = 0;

		$stmt = $dbA->prepare('INSERT INTO users (user_name, email, password_hash, registered) VALUES(?, ?, ?, false)');
		$stmt->bind_param("sss", $username, $email, $hash);
		
		if (!$stmt->execute()) {
			echo 'Failed to insert data into database A';
			$fail += 1;
		}
		
		$stmt->close();

		$stmt = $dbB->prepare('INSERT INTO users (user_name, email, password_hash, registered) VALUES(?, ?, ?, false)');
		$stmt->bind_param("sss", $username, $email, $hash);

		if (!$stmt->execute()) {
			echo 'Failed to insert data into database B';
			$fail += 2;
		}

		$stmt->close();
		
		$dbA->close();
		$dbB->close();

		// Both databases failed
		if ($fail == 3) {
			die('Terrible error! Couldn\'t add data to any database!');
		}
		else {
			//$_SESSION['username'] = $username;
			//header('Location: check.php');
			echo 'Okay you are registered ... please confirm your email before signing in.';
			exit();
		}
	}
?>
