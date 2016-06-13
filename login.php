<?php
	// Testing user authentication using sessions
	session_start();

	if ($_POST) {
		if (isset($_POST['logout'])) {
			unset($_SESSION['username']);
			header('Location: login.php');
			exit();
		}
		$db_host = 'localhost';
		$db_user = 'root';
		$db_pass = 'sdTN3267';
		$db_name = array('test', 'tabletop');
		$db_port = array(5313, 5316);
		$db_socket = array('/var/run/mysqld/mysqld1.sock', '/var/run/mysqld/mysqld2.sock');

		$db = new mysqli($db_host, $db_user, $db_pass, $db_name[0], $db_port[0],$db_socket[0]);
		
		$username = $_POST['username'];
		$password = $_POST['password'];

		// Hella insecure
		$sql = "SELECT user_name, password_hash, registered FROM users WHERE user_name='$username'";
		
		if (!$result = $db->query($sql)) {
			die("Error running query.");
		}
		else {
			if ($row = $result->fetch_assoc()){
				$hash = $row['password_hash'];
				if (password_verify($password, $hash)) {
					$_SESSION['username'] = $row['user_name'];
					$_SESSION['msg'] = "";
					$_SESSION['registered'] = $row['registered'];
				} else {
					$_SESSION['msg'] = "Invalid username or password.";
				}
			} else {
				$_SESSION['msg'] = "Invalid username or password.";
			}
			header('Location: login.php');
		}
	}
	else if (isset($_SESSION['username'])) {
		/*
		echo "Hello ";
		echo $_SESSION['username'];
		?>
		<form action="test_login.php" method="post">
		<input name="logout" type="submit" value="Log out" />
		</form>	
		<?php
		*/
		header('Location: check.php');
	}
	else {
		$pageTitle = 'Log in';
		$loginActive = ' class="active"';
		require('header.php');
		require('navigation.php');
	?>
		<p><?php echo $_SESSION['msg'] ?></p>
		<form action="login.php" method="post">
		<label for="username">Username:</label>
		<input name="username" type="text" /><br />
		<label for="password">Password:</label>
		<input name="password" type="password" /><br />
		<input name="submit" type="submit" value="Log in" />
		</form>
	<?php
		include 'footer.php';
	}
?>
