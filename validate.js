var usernameChecked = false;
var emailChecked = false;
var minUsername = 3;
var minPassword = 3;

function check_username_length() {
	var re = new RegExp("^[A-Za-z0-9]{1,36}$");
        if ($('#signupUsername').val().length < minUsername) {
                document.getElementById('signupUsername').setCustomValidity('Username must be at least three characters long!');
        }
        else if (!re.test($('#signupUsername').val())) {
                document.getElementById('signupUsername').setCustomValidity('Username must contain only letters and numbers!');
        }
        else {
                document.getElementById('signupUsername').setCustomValidity('');
        }
}

/*
function check_email_length() {
        document.getElementById('signupEmail').setCustomValidity('');
}
*/

function check_password_length() {
	var re = new RegExp("^[A-Za-z0-9]{1,36}$");
        if ($('#signupPassword').val().length < minPassword) {
                document.getElementById('signupPassword').setCustomValidity('Password must be at least three characters long!');
        }
        else if (!re.test($('#signupPassword').val())) {
                document.getElementById('signupPassword').setCustomValidity('Password must contain only letters and numbers!');
        }
        else {
                document.getElementById('signupPassword').setCustomValidity('');
        }
}

function check_confirm_password() {
        if (document.getElementById('signupPassword').value != document.getElementById('signupConfirmPassword').value) {
                        document.getElementById('signupConfirmPassword').setCustomValidity('Passwords must match!');
        }
        else {
                document.getElementById('signupConfirmPassword').setCustomValidity('');
        }
}

$(document).ready(function() {
        $('#signupForm').on('submit', function(e) {
                e.preventDefault();
                document.getElementById('signupUsername').setCustomValidity('');
                document.getElementById('signupEmail').setCustomValidity('');
                document.getElementById('signupPassword').setCustomValidity('');
                document.getElementById('signupUsername').setCustomValidity('');
                var ok = true;
                if ($('#signupUsername').val().length < minUsername) {
                        document.getElementById('signupUsername').setCustomValidity('Username must be at least three characters long!');
                        ok = false;
                }
                if ($('#signupPassword').val().length < minPassword) {
                        document.getElementById('signupPassword').setCustomValidity('Password must be at least three characters long!');
                        ok = false;
                }
                //if ($('#signupPassword').val() != $('#signupConfirmPassword').val()) {
                if (document.getElementById('signupPassword').value != document.getElementById('signupConfirmPassword').value) {
                        document.getElementById('signupPassword').setCustomValidity('Passwords must match!');
                        document.getElementById('signupConfirmPassword').setCustomValidity('Passwords must match!');
                        ok = false;
                }

                if (ok) {
                        check_username(true);
                        check_email(true);
                }
        });
});

function check_username(submitSignupForm) {
        var username = $('#signupUsername').val();

        $.ajax( {
                type: "POST",
                url: "check_username.php",
                data: { signupUsername: username },
                success: function(result) {
                        if (result == 0) {
                                check_username_length();
                                document.getElementById('signupUsername').setCustomValidity('Username is already in use!');
                                usernameChecked = false;
                        }
                        else {
                                document.getElementById('signupUsername').setCustomValidity('');
                                check_username_length();
                                usernameChecked = true;
                        }
                },
                complete: function(result) {
                        if (submitSignupForm) submitForm();
                }});
}

function check_email(submitSignupForm) {
        var email = $('#signupEmail').val();

        $.ajax( {
                type: "POST",
                url: "check_email.php",
                data: { signupEmail: email},
                success: function(result) {
                        if (result == 0) {
                                document.getElementById('signupEmail').setCustomValidity('Email address is already in use!');
				emailChecked = false;
                        }
                        else {
                                document.getElementById('signupEmail').setCustomValidity('');
                                emailChecked = true;
                        }
                },
                complete: function(result) {
                        if (submitSignupForm) submitForm();
                }});
}

function submitForm() {
        if (usernameChecked && emailChecked) {
                document.getElementById("signupForm").submit();
        }
}
