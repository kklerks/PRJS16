<?php
	session_start();

	if ($_POST) {
		unset($_SESSION['error']);
		if (isset($_POST['logout'])) {
			unset($_SESSION['username']);
			header('Location: register.php');
			exit();
		}
		if (!$_POST['username'] || !$_POST['password'] || !$_POST['email']) {
			$_SESSION['error'] = "Field cannot be blank.";
			header('Location: register.php');
			exit();
		}
		$db_host = 'localhost';
		$db_user = 'root';
		$db_pass = 'sdTN3267';
		$db_name = array('test', 'tabletop');
		$db_port = array(5313, 5316);
		$db_socket = array('/var/run/mysqld/mysqld1.sock', '/var/run/mysqld/mysqld2.sock');

		$dbA = new mysqli($db_host, $db_user, $db_pass, $db_name[0], $db_port[0],$db_socket[0]);
		if ($dbA->connect_error) {
			die("Database A connection failed: " . $dbA->connect_error);
		}
		if ($dbB->connect_error) {
			die("Database B connection failed: " . $dbA->connect_error);
		}
		$dbB = new mysqli($db_host, $db_user, $db_pass, $db_name[1], $db_port[1],$db_socket[1]);
		
		$username = $_POST['username'];
		$password = $_POST['password'];
		$hash = password_hash($password, PASSWORD_BCRYPT);
		$email = $_POST['email'];
		if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
			$_SESSION['error'] = "Invalid email.";
			header('Location: register.php');
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
				$_SESSION['error'] = "Username already exists.";
				header('Location: register.php');
				exit();
			}
		}
		$sql = "SELECT email FROM users WHERE email='$email'";
		
		if (!$result = $dbA->query($sql)) {
			die("Error running query.");
		}
		else {
			if ($result->num_rows) {
				$_SESSION['error'] = "Email address already exists.";
				header('Location: register.php');
				exit();
			}
		}
		$sql = "INSERT INTO users (user_name, email, password_hash, registered) VALUES('$username', '$email', '$hash', false)";
		
		if (!$result = $dbA->query($sql)) {
			die("Error running query.");
		}
		else {
			$_SESSION['username'] = $username;
			header('Location: login.php');
			exit();
		}
	}
	else if (isset($_SESSION['username'])) {
		echo "You are already logged in as ";
		echo $_SESSION['username'];
		?>
		<form action="register.php" method="post">
		<input name="logout" type="submit" value="Log out" />
		</form>	
		<?php
	}
	else {
		$pageTitle = 'Register';
		$registerActive = ' class="active"';
		require('header.php');
		require('navigation.php');
		echo $_SESSION['error'];
	?>
		<form action="register.php" method="post">
		<label for="username">Username:</label>
		<input name="username" type="text" /><br />
		<label for="password">Password:</label>
		<input name="password" type="password" /><br />
		<label for="email">Email:</label>
		<input name="email" type="text" /><br />
		<input name="submit" type="submit" value="Register" />
		</form>
	<?php
		include 'footer.php';
	}
?>
