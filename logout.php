<?php
	//logout.php
	//logs the user out if they are logged in

	$pageTitle = 'Log out';
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
		echo "You are now logged out.";
		session_destroy();
	} else {
		echo "You are not logged in.";
		//header('Location: test_login.php');
	} //end if statement

	echo "<br>";
	echo "<a href='login.php'>Return to login</a>";
	require('footer.php');

?>
