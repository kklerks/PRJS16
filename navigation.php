<div name='navigation' id='navigation' class='navigation'>
<?php
	session_start();

	?>
	<ul>
		<li><a<?php echo (basename($_SERVER['PHP_SELF'])=='designer.php' ? ' class="active"' : '') ?> href="link to designer?">Designer</a></li>
		<li><a<?php echo (basename($_SERVER['PHP_SELF'])=='check.php' ? ' class="active"' : '') ?> href="check.php">Check</a></li>

		<span class='right-side'>
<?php
	if (isset($_SESSION['username'])) {
?>
			<li><a<?php echo (basename($_SERVER['PHP_SELF'])=='user_panel.php' ? ' class="active"' : '') ?> id="user-id" href="user_panel.php"><?php echo $_SESSION['username'] ?></a></li>
			<li><a<?php echo (basename($_SERVER['PHP_SELF'])=='logout.php' ? ' class="active"' : '') ?> href="logout.php">Log Out</a></li>
<?php
	}
	else {
?>
			<li><a<?php echo (basename($_SERVER['PHP_SELF'])=='login.php' ? ' class="active"' : '') ?> href="login.php">Log In</a></li>
			<li><a<?php echo (basename($_SERVER['PHP_SELF'])=='register.php' ? ' class="active"' : '') ?> href="register.php">Register</a></li>
		</span>
<?php
	}
?>
	</ul>
</div>
