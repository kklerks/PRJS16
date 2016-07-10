<?php
	/*
	 * check_username.php
	 * Checks for the existence of the post parameter "signupUsername" in the user_name column of the users database, and returns 0 if it exists
	 *
	 * usage:
	 * http://myvmlab.senecacollege.ca:5311/check_username.php
	 */

	if ($_SERVER['REQUEST_METHOD'] == 'POST') {
		if (empty($_POST['signupUsername'])) {
			echo 0;
			exit();
		}

		$username = $_POST['signupUsername'];

		$db_host = 'localhost';
		$db_user = 'root';
		$db_pass = 'sdTN3267';
		$db_name = array('test', 'tabletop');
		$db_port = array(5313, 5316);
		$db_socket = array('/var/run/mysqld/mysqld1.sock', '/var/run/mysqld/mysqld2.sock');

		$dbADown = false;
		$dbBDown = false;

		$dbA = new mysqli($db_host, $db_user, $db_pass, $db_name[0], $db_port[0],$db_socket[0]);
		if ($dbA->connect_error) {
			//die("Database A connection failed: " . $dbA->connect_error);
			$dbADown = true;
		}
		$dbB = new mysqli($db_host, $db_user, $db_pass, $db_name[1], $db_port[1],$db_socket[1]);
		if ($dbB->connect_error) {
//			die("Database B connection failed: " . $dbB->connect_error);
			$dbBDown = true;
		}

		// If database A is down let's just give up on client-side validation
		if ($dbADown) {
			echo 1;
			exit();
		}

		$stmt = $dbA->prepare("SELECT user_name FROM users WHERE user_name=?");
		$stmt->bind_param("s", $username);
		$stmt->execute();
		
		if (!$result = $stmt->get_result()) {
			echo 1;
			exit();
		}
		else {
			// If we get at least one result, the username exists
			if ($result->num_rows) {
				echo 0;
				$stmt->close();
				exit();
			}
		}
		
		$stmt->close();

		// If we made it this far, the username doesn't exist
		echo 1;
	}
?>
