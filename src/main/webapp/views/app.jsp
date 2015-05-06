<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
<title>Users</title>
	<link href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" rel="stylesheet">
	<script src="https://code.jquery.com/jquery-2.1.3.js"></script>	
	<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
	<style>
		.row { margin-bottom: 1em; } 
		img.thumb { max-width: 120px; max-height: 100px; border : 1px solid gray;} 
		.media { border-radius: 5px; padding: .5em; margin-bottom: 1em; background-color: rgb(250,250,250);}
		.avatar-circle { border-radius: 50%; display : inline-block; width:150px; height: 150px; overflow: hidden; margin-right: 3em;}
		.avatar-circle img { display: block; width: 100%; }	
	</style>	
	<script>
		user = {};
		$(document).ready(function() {
			userid = $('#userid').html();
			$.ajax({
				url : "users/" + userid,
				dataType : 'json',
				type : 'get',
				success : function(data) {				
					user = data;
					updateBleets();
				},
				failure: function(err) {
					console.log("There is an error with retrieving user: " + err);
				}
			});	
		});
		
		addBleet = function() {		
			bleet = $('#bleet').val();
			privateCommment = $('#privateComment').val();
			clearForm();			
			$.ajax({
				url : "users/" + userid + "/bleets",
				dataType : 'json',
				type : 'post',
				data : { bleet : bleet, privateComment : privateComment },
				success : function(data) {				
					user = data;
					updateBleets();
				},
				failure: function(err) {
					console.log("There is an error with adding a bleet: " + err);
				}
			});					
		}
		
		updateBleet = function(id) {		
			bleet = $('#bleet').val();
			privateCommment = $('#privateComment').val();
			clearForm();			
			$.ajax({
				url : "users/" + userid + "/bleets" + id,
				dataType : 'json',
				type : 'put',
				data : { bleet : bleet, privateComment : privateComment },
				success : function(data) {				
					user = data;
					updateBleets();
				},
				failure: function(err) {
					console.log("There is an error with updating a bleet: " + err);
				}
			});					
		}

		addAvatar = function() {
			imageFile = document.getElementById('avatar').files[0];
			var data = new FormData();
			data.append('avatar', imageFile);
			$('#avatar').replaceWith( $('#avatar').clone(true) );
			
			$.ajax({
				url : 'users/' + userid + '/avatar',
				type : 'POST',
				data : data,
				cache : false,
				dataType : 'text',
				processData : false, 
				contentType : false, 
				success : function(data, textStatus, jqXHR) {
					d = new Date();
					var src = $("#avatarImage").attr("src");
					$('#avatarImage').attr('src', src + '?' + d.getTime());
				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log('ERRORS: ' + textStatus);
				}
			});
						
		}

		deleteBleet = function(id) {
			clearForm();
			$.ajax({
				url : "users/" + userid + "/bleets/" + id,
				dataType : 'json',
				type : 'delete',
				success : function(data) {
					user = data;
					updateBleets();
				},
				failure : function(err) {
					console.log("There is an error with adding a bleet: " + err);
				}
			});
		}

		clearForm = function() {
			$('#bleet').val("");
			$('#privateComment').val("");
		}

		updateBleets = function() {
			var imageList = $('#bleetList');
			imageList.empty();
			$(user.bleets)
					.each(
							function(key, val) {
								var imageDiv = $('<div class="media">');
								imageDiv
										.append('<div class="media-left media-top"><a href="' + val.url + '"><img class="thumb media-object" src="' + val.url + '"></a></div>');
								imageDiv
										.append('<div class="media-body">'
												+ val.comment
												+ '<div onclick="deleteImage(\''
												+ val.id
												+ '\')" class="pull-right btn btn-xs btn-warning">&times;</div></div>');
								imageDiv.appendTo(imageList);
							});
		}
	</script>
</head>
<body background="http://www.munciehighstreet.org/wp-content/uploads/2014/05/website-background-wincustomize-explore-clouds-284048.jpg">
	<div class="container">
		<nav class="navbar navbar-default">
			<div class="container-fluid">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed"
						data-toggle="collapse" data-target="#navbar" aria-expanded="false"
						aria-controls="navbar">
						<span class="sr-only">Toggle navigation</span> <span
							class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="#">Bleeter</a>
				</div>

				<div id="navbar" class="navbar-collapse collapse">
					<ul class="nav navbar-nav navbar-right">
						<li><a href="/logout">Logout</a></li>
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li><a>Welcome <span id="usernameSpan">${user.username}</span></a></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
			<!--/.container-fluid -->
		</nav>
		<span style="display:none;" id="userid">${user.id}</span>
		<div class="row">
			<div class="avatar-circle pull-left"><img src="users/${user.id}/avatar" id="avatarImage" class="avatar"></div>	
			<div>	
				<input type="file" id="avatar" name="avatar"> <br>
				<div class="btn btn-sm btn-primary" onclick="addAvatar()">Add avatar</div>
			</div>
		</div>
		
		<div class="page-header">
			<h2>Bleeter</h2>
		</div>		
		

		<div class="row">
			Bleet description: <input type="text" id="bleet"> Make comment private?: 
			<input type="checkbox" id="privateComment">
			<div class="btn btn-sm btn-primary" onclick="addImage()">Add bleet</div>

		</div>

		<div id="imageList">
		</div>
	</div>
</body>
</html>