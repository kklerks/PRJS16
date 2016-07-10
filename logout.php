<?php
	/*
	 * logout.php
	 * Logs the user out if they are logged in, and then redirects the user to the splash page
	 *
	 * usage:
	 * http://myvmlab.senecacollege.ca:5311/logout.php
	 */

	session_start();

	if (isset($_SESSION['username'])) {
		session_destroy();
		echo '<p>You have been logged out.</p><p>Redirecting to front page...<p>';
		// Redirect to the front page after 5 seconds
		echo 
		'<script>setTimeout(function () { window.location.href="index.php"; }, 5000);</script>';
	} 
	else {
		header('Location: index.php');
	} //end if statement
?>
