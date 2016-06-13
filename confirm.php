<?php


	session_start();

	if (isset($_GET['reg'])) {
		$_SESSION['registration_code'] = $_GET['reg'];
	} else if (!isset($_SESSION['registration_code'])) {
		header('Location: index.php');
	}

?>

<html>

<?php
	require('header.php');
	require('navigation.php');
?>

	<body>

		<?php
			$db_host = 'localhost';
			$db_user = 'root';
			$db_pass = 'sdTN3267';
			$db_name = array('test', 'tabletop');
			$db_port = array(5313, 5316);
			$db_socket = array('/var/run/mysqld/mysqld1.sock', '/var/run/mysqld/mysqld2.sock');
			$db = new mysqli($db_host, $db_user, $db_pass, $db_name[0], $db_port[0],$db_socket[0]);

			if (isset($_SESSION['username'])) {
				//user was still logged into session when attempting to confirm account

				$sql = "SELECT registered FROM users WHERE user_name = " . $_SESSION['username'];
				$result = $db->query($sql);

				echo "<pre>";
				var_dump($_SESSION);
				echo $sql;
				echo $result;
				echo "</pre>";



			} else {
				//user was not logged in when attempting to confirm account; redirect to login

				$_SESSION['msg'] = "Please log in to confirm your account.";
				header('Location: login.php');

			}
		?>





	</body>

</html>
