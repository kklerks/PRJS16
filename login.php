<?php
	function validateUsername($str) {
		return preg_match("/^[A-Za-z0-9]{1,36}$/", $str);
	}

	function validatePassword($str) {
		return preg_match("/^[A-Za-z0-9]{1,36}$/", $str);
	}

	session_start();

	if (isset($_SESSION['username'])) {
		header('Location: check.php');
	}
	else if ($_SERVER['REQUEST_METHOD'] == 'POST') {
		if (empty($_POST['loginUsername']) || empty($_POST['loginPassword'])) {
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
		$username = $_POST['loginUsername'];
		if (!validateUsername($username)) {
			echo 'Invalid username' . $username;
			exit();
		}

		$password = $_POST['loginPassword'];
		if (!validatePassword($password)) {
			echo 'Invalid password.';
			exit();
		}

		$stmt = $dbA->prepare("SELECT user_name, password_hash, registered FROM users WHERE user_name=?");
		$stmt->bind_param("s", $username);
		
		if (!$stmt->execute()) {
			die("Error running query.");
		}
		else {
			$stmt->bind_result($user, $hash, $reg);
			if (!$stmt->fetch()) {
				echo "Invalid username or password!";
				exit();
			}
		}
		
		$stmt->close();
				
		echo "Found user $user with password hash $hash whose email address is " . ($reg ? '' : 'NOT ') . 'confirmed!';

		if (password_verify($password, $hash)) {
			if ($reg) {
				$_SESSION['username'] = $user;
				$_SESSION['registered'] = $reg;
				echo "Okay you are logged in, $user!";
			}
			else {
				echo 'Please confirm your email before trying to log in.';
			}
		}
		else {
			echo "Invalid username or password!";
			exit();
		}
	}
?>
