<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="${mvc.basePath}/../app.css" rel="stylesheet">
<title>メッセージアプリ：ユーザ管理</title>
</head>
<body x-data="{ success: '', error: '', constraintErrors: [], users: [] }"
	x-init="
		api.get('/users')
			.then(res => users = res)
			.catch(e => error = '一覧を取得できませんでした[Error: ' + res + ']'); 
	">
	[<a href="${mvc.basePath}/">ホーム</a>][<a href="${mvc.basePath}/list">メッセージページ</a>][<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>
	<div style="color: green">
		<span x-text="success" @click.outside="success = ''"></span>
	</div>
	<div style="color: red" @click.outside="error = ''; errors = []">
		<span x-text="error"></span>
		<template x-for="err in constraintErrors">
			<div x-text="err"></div>
		</template> 
	</div>

	<h1>新規ユーザ追加</h1>

	<form class="row_create" autocomplete="off"
		x-data="{ json: { name: '', role: '', password: ''} }"
	    @submit.prevent="
	                api.post('/users', json)
	                	.then(res => {
	                		success = 'ユーザを作成しました。';
	                		users.push(res);
	                		json = { name: '', role: '', password: ''};	                		
	                	})
	                	.catch(e => {
	                		if(e.response.data?.errors != null) constraintErrors = e.response.data.errors;
	                		else error = 'ユーザの作成に失敗しました[Error: ' + e.response.status + ']'; 
	                	});
		">	                		
		<span>ユーザ名</span> <span>ロール</span> <span>パスワード</span> <span></span>
		<input type="text" x-model="json.name">
		<input type="text" x-model="json.role">
	    <input type="password" x-model="json.password">
		<button>追加</button>
	</form>
	<hr>
	<h1>ユーザ一覧</h1>
	<div>
		<div class="row">
			<div>ユーザ名</div>
			<div>ロール</div>
			<div>パスワード</div>
		</div>
		<template x-for="user in users" :key="user.name" 
			x-data="{
				updateUser: (name, json) => {
					api.put('/users/' + name, json)
						.then(res => {
							success = 'ユーザを更新しました。';
							json.password = '';
						})
						.catch(e => {
	                		if(e.response.data?.errors != null) constraintErrors = e.response.data.errors;
	                		else error = 'ユーザの更新に失敗しました[Error: ' + e.response.status + ']'; 
	                	});
				},
				deleteUser: (name) => {
					api.delete('/users/' + name)
						.then(res => {
							success = 'ユーザを削除しました。';
							users = users.filter(u => u.name != name);
						})
						.catch(e => {
	                		if(e.response.data?.errors != null) constraintErrors = e.response.data.errors;
	                		else error = 'ユーザの削除に失敗しました[Error: ' + e.response.status + ']'; 
	                	});
				},
			}">
			<div class="row"
					x-data="{ name: user.name, 
							row: {
								name: user.name, 
                				role: user.role,
                				password: user.password
							}}">
				<span x-text="name"></span>
				<input type="text" x-model="row.role">
				<input type="password" x-model="row.password">
				<button @click="updateUser(name, row)">更新</button>
				<button @click="deleteUser(name)">削除</button>
			</div>
		</template>
	</div>

	<script type="module">
		import api from '${mvc.basePath}/../api.js';
		api.start('${mvc.basePath}/api', '${mvc.csrf.token}');
	</script>
</body>
</html>
