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
<body
	x-data="{ users: [], success: '', errors: [],
		showErr(e, mes) {
			if(e.response?.data?.errors) this.errors = e.response.data.errors; // 制約条件エラー
			else if(e.response) this.errors.push(mes + ': ' + e.response.status); // その他のサーバエラー
			else this.errors.push('処理できませんでした: ' + e.message); // クライアントエラー
        },
		resetErr(){ this.errors = [] }
    }"
	x-init="
		api.get('/users')
			.then(res => users = res)
			.catch(e => showErr(e, '一覧を取得できませんでした'));
	">
	[<a href="${mvc.basePath}/">ホーム</a>][<a href="${mvc.basePath}/list">メッセージページ</a>][<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>

	<div style="color: green" x-show="success">
		<span x-text="success" @click.outside="success = ''"></span>
	</div>

	<div style="color: red" x-show="errors.length > 0">
		<template x-for="err in errors">
			<div x-text="err"></div>
		</template>
	</div>

	<h1>新規ユーザ追加</h1>

	<form class="row_create" autocomplete="off"
		x-data="{ json: { name: '', role: '', password: ''} }"
		@submit.prevent="
	     			resetErr();
	                api.post('/users', json)
	                	.then(res => {
	                		success = 'ユーザを作成しました。';
	                		users.push(res);
	                		json = { name: '', role: '', password: ''};	                		
	                	})
	                	.catch(e => showErr(e, 'ユーザを作成できませんでした'));
		">
		<span>ユーザ名</span> <span>ロール</span> <span>パスワード</span> <span></span> <input
			type="text" x-model="json.name"> <input type="text"
			x-model="json.role"> <input type="password"
			x-model="json.password">
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
					resetErr();
					api.put('/users/' + name, json)
						.then(res => {
							success = 'ユーザを更新しました。';
							json.password = '';
						})
						.catch(e => showErr(e, 'ユーザを更新できませんでした'));
				},
				deleteUser: (name) => {
				    resetErr();
					api.delete('/users/' + name)
						.then(res => {
							success = 'ユーザを削除しました。';
							users = users.filter(u => u.name != name);
						})
						.catch(e => showErr(e, 'ユーザを削除できませんでした'));
				},
			}">
			<div class="row"
				x-data="{ name: user.name, row: { role: user.role, password: user.password }}">
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
