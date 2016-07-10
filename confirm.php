<?php
	/*
	 * confirm.php
	 * verifies an account with token given via GET
	 *
	 * usage:
	 * http://myvmlab.senecacollege.ca:5311/confirm.php?reg=testregstring
	 */

	$debug = "\n";
	$msg = "\n";

	/*
	 * Check if registration code exists (via GET)
	 */
	if (isset($_GET['reg'])) {
		$receivedtoken = $_GET['reg'];
		$debug .= "Registration code found was: $receivedtoken\n";
		
	} else {	
		$debug .= "No registration code found; should redirect elsewhere.\n";
		$msg = "Invalid token.";
	}


	$dbDown = false;
	$db2Down = false;
	/*
	 * Connect to database 
	 */
	$db_user = 'root';
	$db_pass = 'sdTN3267';
	$db_name = array('test', 'tabletop');
	$db_port = array(5313, 5316);
	$db_socket = array('/var/run/mysqld/mysqld1.sock', '/var/run/mysqld/mysqld2.sock');
	$db = new mysqli($db_host, $db_user, $db_pass, $db_name[0], $db_port[0],$db_socket[0]);
	if ($db->connect_errno) {
		$debug .= "Failed to connect. Reason: $db->connect_error\n";
		$dbDown = true;
	}
	$db2 = new mysqli($db_host, $db_user, $db_pass, $db_name[1], $db_port[1],$db_socket[1]);
	if ($db2->connect_errno) {
		$debug .= "Failed to connect. Reason: $db2->connect_error\n";
		$db2Down = true;
	}

	if ($dbDown && $db2Down) die('There is an internal server error and we could not confirm your account. Please try the confirmation link again later.');

	if (!$dbDown) {
		/*
		 * Check account registration status
		 */
		if (!($sql = $db->prepare("SELECT user_name,expires from confirmation_tokens WHERE token = ?"))) {
			$debug .= "Prepare failed. Reason: $db->error\n";
		}
		if (!($sql->bind_param("s",$receivedtoken))) {
			$debug .= "Binding failed. Reason: $sql->error\n";
		}
		if (!($sql->execute())) {
			$debug .= "Execute failed. Reason: $sql->error\n";
		} else {
			$res = $sql->get_result();
			$row = $res->fetch_assoc();
			$user_name = $row['user_name'];
			$expires = $row['expires'];
		}
		if (!isset($user_name)) {
			$debug .= "Could not find row with token of: $receivedtoken in token table. Row was probably deleted or did not exist.\n";
			$debug .= "Should display invalid token to user.\n";
			$msg = "Invalid token.";
		} else {
			$debug .= "Retrieved from server: Username: $user_name  Token expiry: $expires.\n";
		
		
			/*
			 * Check expiry date
			 */
			$now = $_SERVER['REQUEST_TIME'];
			$debug .= "Current time: $now  Token expires: $expires\n";
			if ($now > $expires) {

				$x = $now - $expires;
				$debug .= "Token has expired. (Expired $x seconds ago.) " .
					"Should display invalid token to user.\n";
				$msg = "Invalid token.";
			} else {
				$x = $expires - $now;
				$debug .= "Token is still valid. (Will expire in $x seconds)\n";


				/*
				 * Query database for user registration status
				 */
				if (!($sql2 = $db->prepare("SELECT registered from users WHERE user_name = ?"))) {
					$debug .= "Prepare failed. Reason: $db->error\n";
				}
				if (!($sql2->bind_param("s",$user_name))) {
					$debug .= "Binding failed. Reason: $sql2->error\n";
				}
				if (!($sql2->execute())) {
					$debug .= "Execute failed. Reason: $sql2->error\n";
				} else {
					$res = $sql2->get_result();
					$row = $res->fetch_assoc();
					$isRegistered = $row['registered'];
				}
				if (!isset($isRegistered)) {
					$debug .= "Attempted to validate nonexistant user: $user_name!\n";
				} else {
					$debug .= "Registration status for $user_name: $isRegistered\n";
					if ($isRegistered == 1) {
						$debug .= "$user_name already confirmed their account. " .
						"Either redirect or tell user they're already registered.\n";
						$msg = "You have already registered your account.";
					} else if ($isRegistered == 0) {
						/*
						 * Confirm the account (update database)
						 */
						$debug .= "$user_name was not registered yet.\n";

						if (!($sql3 = $db->prepare("UPDATE users SET registered=1 WHERE user_name = ?"))) {
							$debug .= "Prepare failed. Reason: $db->error\n";
						}
						if (!($sql3->bind_param("s",$user_name))) {
							$debug .= "Binding failed. Reason: $sql3->error\n";
						}
						if (!($sql3->execute())) {
							$debug .= "Execute failed. Reason: $sql3->error\n";
						} else {

							$debug .= "Successfully confirmed account $user_name\n";
							$debug .= "Display success message.\n";
							$msg = "Congratulations! Your account has now been verified.";
						}
					} 
				}
			}
		}
	}
	if (!$db2Down) {
		/*
		 * Check account registration status
		 */
		if (!($sql = $db2->prepare("SELECT user_name,expires from confirmation_tokens WHERE token = ?"))) {
			$debug .= "Prepare failed. Reason: $db2->error\n";
		}
		if (!($sql->bind_param("s",$receivedtoken))) {
			$debug .= "Binding failed. Reason: $sql->error\n";
		}
		if (!($sql->execute())) {
			$debug .= "Execute failed. Reason: $sql->error\n";
		} else {
			$res = $sql->get_result();
			$row = $res->fetch_assoc();
			$user_name = $row['user_name'];
			$expires = $row['expires'];
		}
		if (!isset($user_name)) {
			$debug .= "Could not find row with token of: $receivedtoken in token table. Row was probably deleted or did not exist.\n";
			$debug .= "Should display invalid token to user.\n";
			$msg = "Invalid token.";
		} else {
			$debug .= "Retrieved from server: Username: $user_name  Token expiry: $expires.\n";
		
		
			/*
			 * Check expiry date
			 */
			$now = $_SERVER['REQUEST_TIME'];
			$debug .= "Current time: $now  Token expires: $expires\n";
			if ($now > $expires) {

				$x = $now - $expires;
				$debug .= "Token has expired. (Expired $x seconds ago.) " .
					"Should display invalid token to user.\n";
				$msg = "Invalid token.";
			} else {
				$x = $expires - $now;
				$debug .= "Token is still valid. (Will expire in $x seconds)\n";


				/*
				 * Query database for user registration status
				 */
				if (!($sql2 = $db2->prepare("SELECT registered from users WHERE user_name = ?"))) {
					$debug .= "Prepare failed. Reason: $db2->error\n";
				}
				if (!($sql2->bind_param("s",$user_name))) {
					$debug .= "Binding failed. Reason: $sql2->error\n";
				}
				if (!($sql2->execute())) {
					$debug .= "Execute failed. Reason: $sql2->error\n";
				} else {
					$res = $sql2->get_result();
					$row = $res->fetch_assoc();
					$isRegistered = $row['registered'];
				}
				if (!isset($isRegistered)) {
					$debug .= "Attempted to validate nonexistant user: $user_name!\n";
				} else {
					$debug .= "Registration status for $user_name: $isRegistered\n";
					if ($isRegistered == 1) {
						$debug .= "$user_name already confirmed their account. " .
						"Either redirect or tell user they're already registered.\n";
						$msg = "You have already registered your account.";
					} else if ($isRegistered == 0) {
						/*
						 * Confirm the account (update database)
						 */
						$debug .= "$user_name was not registered yet.\n";

						if (!($sql3 = $db2->prepare("UPDATE users SET registered=1 WHERE user_name = ?"))) {
							$debug .= "Prepare failed. Reason: $db2->error\n";
						}
						if (!($sql3->bind_param("s",$user_name))) {
							$debug .= "Binding failed. Reason: $sql3->error\n";
						}
						if (!($sql3->execute())) {
							$debug .= "Execute failed. Reason: $sql3->error\n";
						} else {

							$debug .= "Successfully confirmed account $user_name\n";
							$debug .= "Display success message.\n";
							$msg = "Congratulations! Your account has now been verified.";
						}
					} 
				}
			}
		}
	}

?>

<html>
	<body>
		<?php 
			//echo "<pre>$debug</pre>";
			echo "<pre>$msg</pre>";
		 ?>
	</body>

</html>
