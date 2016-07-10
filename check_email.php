<?php
	/*
	 * check_email.php
	 * Checks for the existence of the post parameter "signupEmail" in the email column of the users database, and returns 0 if it exists
	 *
	 * usage:
	 * http://myvmlab.senecacollege.ca:5311/check_email.php
	 */

	if ($_SERVER['REQUEST_METHOD'] == 'POST') {
		if (empty($_POST['signupEmail'])) {
			echo 0;
			exit();
		}

		$email= $_POST['signupEmail'];

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

		$stmt = $dbA->prepare("SELECT email FROM users WHERE email=?");
		$stmt->bind_param("s", $email);
		$stmt->execute();
		
		if (!$result = $stmt->get_result()) {
			echo 1;
			exit();
		}
		else {
			// If we get at least one row, the email exists
			if ($result->num_rows) {
				echo 0;
				$stmt->close();
				exit();
			}
		}

		$stmt->close();

		// If we get here, the email doesn't exist
		echo 1;
	}
?>
