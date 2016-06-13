<?php
	//check.php
	//echo if the user is logged in or not

	$pageTitle = 'Check';
	require('header.php');
	require('navigation.php');
	session_start();

	echo '<pre>';
	echo "Session id: ";
	echo session_id();
	echo "<br>";
	var_dump($_SESSION);
	echo '</pre>';

	if (isset($_SESSION['username'])) {

		if (isset($_SESSION['registration_code'])) {
			header('Location: confirm.php');

		} else {
			echo "hello " . $_SESSION['username'];
			echo "<br>";
			?>
			<form action="logout.php">
			<input type="submit" value="logout">
			</form>
			<?php
		}
	} else {
		echo "You are not logged in.";
		echo "<br>";
		echo "<a href='login.php'>Click here to login</a>";
		//header('Location: test_login.php');
	} //end if statement
	require('footer.php');
?>
