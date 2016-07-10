<?php
	/*
	 * delete_user.php
	 * Deletes the user associated with the given registration token
	 *
	 * usage:
	 * http://myvmlab.senecacollege.ca:5311/delete_user.php?reg=testregstring
	 */

	function writeErrorLog($str) {
		if ($str) {
			$time = new DateTime();
			file_put_contents('error.log', '[' . $time->format('Y-m-d H:i:s') . ']: ' . $str, FILE_APPEND);
		}
	}

	function terminate($msg, $errorMessage = NULL) {
		writeErrorLog($errorMessage);
		echo "<p>$msg</p>";
		echo '<p>Redirecting to front page...</p>';

		// Redirect to the front page after 5 seconds
		echo 
		'<script>setTimeout(function () { window.location.href="index.php"; }, 5000);</script>';
		exit();
	}

	$errors = '';
	$dbADown = false;
	$dbBDown = false;

	if (empty($_GET['reg'])) {
		terminate('Error processing input');
	}

	// Connect to the databases
	$db_host = 'localhost';
	$db_user = 'root';
	$db_pass = 'sdTN3267';
	$db_name = array('test', 'tabletop');
	$db_port = array(5313, 5316);
	$db_socket = array('/var/run/mysqld/mysqld1.sock', '/var/run/mysqld/mysqld2.sock');

	// Connect to the databases
	$dbA = new mysqli($db_host, $db_user, $db_pass, $db_name[0], $db_port[0], $db_socket[0]);
	$dbB = new mysqli($db_host, $db_user, $db_pass, $db_name[1], $db_port[1], $db_socket[1]);

	if ($dbA->connect_error) {
		$errors .= "Database A connection failed: " . $dbA->connect_error . "\n";
		$dbADown = true;
	}
	if ($dbB->connect_error) {
		$errors .= "Database B connection failed: " . $dbB->connect_error . "\n";
		$dbBDown = true;
	}

	// If we couldn't connect to either databases, quit
	if ($dbADown && $dbBDown) {
		terminate('There is an internal system error and we cannot process your request. Please try again later.', $errors);
	}
	
	$receivedtoken = $_GET['reg'];
	
	if (!$dbADown) {
		$stmt = $dbA->prepare('SELECT c.user_name, u.email FROM confirmation_tokens c, users u WHERE c.user_name = u.user_name AND c.token = ?');
		$stmt->bind_param("s", $receivedtoken);
		
		if (!$stmt->execute()) {
			die("Error running query.");
		}
		else {
			// Bind the returned user_name and email values to variables here
			$stmt->bind_result($user, $email);
			if (!$stmt->fetch()) {
				$errors .= "An attempt has been made to delete the user with the token $receivedtoken, but that token doesn't exist.\n";
				terminate("Invalid token!", $errors);
			}
		}
	
		$stmt->close();
	}

	// For when we implement confirmation tokens on database B
	/*
	if (!$dbBDown) {
		$stmt = $dbB->prepare('SELECT c.user_name, u.email FROM confirmation_tokens c, users u WHERE c.user_name = u.user_name AND c.token = ?');
		$stmt->bind_param("s", $receivedtoken);
		
		if (!$stmt->execute()) {
			die("Error running query.");
		}
		else {
			// Bind the returned user_name and email values to variables here
			$stmt->bind_result($user, $email);
			if (!$stmt->fetch()) {
				$errors .= "An attempt has been made to delete the user with the token $receivedtoken, but that token doesn't exist.\n";
				terminate("Invalid token!", $errors);
			}
		}
	
		$stmt->close();
	}
	*/
	
	// If the confirmation token is valid we should always find a user associated with it, but just in case
	if ($user) {
		// Delete from the users table of both databases
		if (!$dbADown) {
			$stmt = $dbA->prepare('DELETE FROM users WHERE user_name = ?');
			$stmt->bind_param("s", $user);
			
			if (!$stmt->execute()) {
				die("Error running query on database A.");
			}

			$stmt->close();

			$stmt = $dbA->prepare('DELETE FROM confirmation_tokens WHERE user_name = ?');
			$stmt->bind_param("s", $user);
			
			if (!$stmt->execute()) {
				die("Error running query on database A.");
			}
			$stmt->close();
		}
		else {
			writeErrorLog("DATABASE A WAS DOWN AT THE MOMENT OF USER DELETION:\nMust remove user $user from Database A users and confirmation_tokens");
		}
		if (!$dbBDown) {
			$stmt = $dbB->prepare('DELETE FROM users WHERE user_name = ?');
			$stmt->bind_param("s", $user);
			
			if (!$stmt->execute()) {
				die("Error running query on database B.");
			}

			$stmt->close();

			$stmt = $dbB->prepare('DELETE FROM confirmation_tokens WHERE user_name = ?');
			$stmt->bind_param("s", $user);
			
			if (!$stmt->execute()) {
				die("Error running query on database B.");
			}
			$stmt->close();
		}
		else {
			writeErrorLog("DATABASE B WAS DOWN AT THE MOMENT OF USER DELETION:\nMust remove user $user from user and confirmation_tokens in Database B");
		}
	}
	else {
		terminate('No user exists associated with the given token', "TOKEN $receivedtoken EXISTS IN DATABASE WITH NO CORRESPONDING USER!");
	}
	
	echo "<p>The user $user with the email address $email has been removed from the database!</p>";
	echo '<p>Redirecting to front page...</p>';

	// Redirect to the front page after 5 seconds
	echo 
	'<script>setTimeout(function () { window.location.href="index.php"; }, 5000);</script>';
?>
