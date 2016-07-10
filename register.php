<?php
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

		$errors = '';
		$dbADown = false;
		$dbBDown = false;

		$dbA = new mysqli($db_host, $db_user, $db_pass, $db_name[0], $db_port[0],$db_socket[0]);
		if ($dbA->connect_error) {
			//die("Database A connection failed: " . $dbA->connect_error);
			$dbADown = true;
		}
		$dbB = new mysqli($db_host, $db_user, $db_pass, $db_name[1], $db_port[1],$db_socket[1]);
		if ($dbB->connect_error) {
			//die("Database B connection failed: " . $dbB->connect_error);
			$dbBDown = true;
		}

		if ($dbADown && $dbBDown) {
			terminate('There is an internal service error and we cannot process your request at this time. Please try again later.', 'BOTH DATABASES WERE DOWN AT THE MOMENT OF REGISTRATION.\n');
		}
		
		//echo 'Connected to both databases.';
		$username = $_POST['signupUsername'];
		if (!validateUsername($username)) {
			terminate("The username $username is invalid!");
		}

		$password = $_POST['signupPassword'];
		if (!validatePassword($password)) {
			terminate("The password $password is invalid!");
		}
		$hash = password_hash($password, PASSWORD_BCRYPT);

		$email = $_POST['signupEmail'];
		if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
			terminate("The email addredd $email is invalid!");
		}

		if (!$dbADown) {
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
		}

		if (!$dbBDown) {
			$stmt = $dbB->prepare("SELECT user_name FROM users WHERE user_name=?");
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

			$stmt = $dbB->prepare("SELECT email FROM users WHERE email=?");
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

			$stmt = $dbB->prepare('INSERT INTO users (user_name, email, password_hash, registered) VALUES(?, ?, ?, false)');
			$stmt->bind_param("sss", $username, $email, $hash);

			if (!$stmt->execute()) {
				echo 'Failed to insert data into database B';
				$fail += 2;
			}

			$stmt->close();
		}

		if ($fail < 3) {
			if ($dbADown) writeErrorLog("DATABASE A WAS DOWN WHEN $username WITH EMAIL $email AND PASSWORD HASH $hash REGISTERED; this user must be added manually.\n");
			if ($dbBDown) writeErrorLog("DATABASE B WAS DOWN WHEN $username WITH EMAIL $email AND PASSWORD HASH $hash REGISTERED; this user must be added manually.\n");	
		}
		// Both databases failed
		if ($fail == 3) {
			terminate('There is an internal server error and we can\'t register you at the moment. Please try again later.', 'Both databases failed upon user creation!');
		}
		else {

			/*
			 * Generate unique token
			 */
			$token = sha1(uniqid($username)); //based on sha1 + uniqid() of username
			$debug .= "Generated token: $token\n";


			/*
			 * Add entry to token database
			 */
			$timestamp = $_SERVER['REQUEST_TIME']; //unix time
			$expirydate = $timestamp + 86400; //86400 seconds in a day
			$debug .= "Timestamp: $timestamp  Expirydate: $expirydate\n";

			if (!$dbADown) {			
				if (!($sql = $dbA->prepare("INSERT INTO confirmation_tokens (user_name,token,expires) VALUES (?, ?, ?)"))) {
					$debug .= "Prepare failed. Reason: $dbA->error\n";
				}
				if (!($sql->bind_param("ssi",$username,$token,$expirydate))) {
					$debug .= "Binding failed. Reason: $sql->error\n";
				}
				if (!($sql->execute())) {
					$debug .= "Execute failed. Reason: $sql->error\n";
				}
				$sql->close();
			}
				
			if (!$dbBDown) {			
				if (!($sql = $dbB->prepare("INSERT INTO confirmation_tokens (user_name,token,expires) VALUES (?, ?, ?)"))) {
					$debug .= "Prepare failed. Reason: $dbB->error\n";
				}
				if (!($sql->bind_param("ssi",$username,$token,$expirydate))) {
					$debug .= "Binding failed. Reason: $sql->error\n";
				}
				if (!($sql->execute())) {
					$debug .= "Execute failed. Reason: $sql->error\n";
				}
				$sql->close();
			}
			

			/*
			 * Generate and send mail
			 */
			$subject = "Tabletop Assistant Account Verification";
			$reply = "hzhuang3@myseneca.ca";
			$headers = "From: no-reply@myvmlab.senecacollege.ca" . "\r\n" . "Reply-to: $reply";
			$link = "http://myvmlab.senecacollege.ca:5311/confirm.php?reg=$token";
			$del_link = "http://myvmlab.senecacollege.ca:5311/delete_user.php?reg=$token";
			$message = 
				"Hello " . $username . "\n\n" .
				"Thank you for signing up for Tabletop Assistant." . "\n\n" .
				"Please visit the link below within the next 24 hours" .
				" to verify your account:\n\n" . $link . "\n\n" .
				"After your account is verified you can begin to use the application." .
				" We hope you enjoy using the Tabletop Assistant!" . "\n\n" . "The Tabletop Assistant Team" . "\n\n" .
				"------------------------------------------------------" . "\n\n" .
				"This email was automatically sent because someone attempted to create an account using this email address." . "\n\n" .
				"Please use the following link to delete the account in the case you erronously recieved this email: " . "\n\n" . $del_link;

			$debug .= "Message: " . $message . "\n";

			if (mail($email,$subject,$message,$headers)) {
				$debug .= "Message sent.\n";
			} else {
				$debug .= "Failed to send mail.\n";
			}

			echo 'Okay you are registered ... please confirm your email before signing in.';
			writeErrorLog($debug);
			//echo "<pre>$debug</pre>";

		}
		
		exit();

	}
?>
