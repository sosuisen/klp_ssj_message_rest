<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="${mvc.basePath}/../app.css" rel="stylesheet">
<title>メッセージアプリ：ユーザ管理</title>
<style>
.row_create {
	display: grid;
	grid-template-columns: 100px 100px 100px 50px;
}

.row {
	display: grid;
	grid-template-columns: 100px 100px 100px 50px 50px;
}
</style>
</head>
<body x-data="{ error: '', users: [] }"
	x-init="
		api.get('/users')
			.then(res => users = res)
			.catch(e => error = '一覧を取得できませんでした[Error: ' + res + ']');
	">
	[<a href="${mvc.basePath}/">ホーム</a>]	[	<a href="${mvc.basePath}/list">メッセージページ</a>] [	<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>
	<div style="color: green">
		<c:forEach var="msg" items="${userForm.message}">
			<c:choose>
				<c:when test="${msg == 'succeed_create'}">ユーザを作成しました。</c:when>
				<c:when test="${msg == 'succeed_update'}">ユーザを更新しました。</c:when>
				<c:when test="${msg == 'succeed_delete'}">ユーザを削除しました。</c:when>
				<c:otherwise>${msg}</c:otherwise>
			</c:choose>
			<br />
		</c:forEach>
	</div>
	<div style="color: red">
		<c:forEach var="err" items="${userForm.error}">
			${mvc.encoders.html(err)}<br />
		</c:forEach>
	</div>

	<h1>新規ユーザ追加</h1>

	<form class="row_create" autocomplete="off"
		x-data="{ json: { name: '', role: '', password: ''} }"
	    @submit.prevent="
	                api.post('/users', json)
	                	.then(res => {
	                		json = { name: '', role: '', password: ''};
	                		// 成功表示を追加
	                		users.push(res);
	                	})
	                	// どうやって複数のエラーを表示するか。JSONでエラー集合を返すべきだね。
	                	.catch(e => console.error(e));
		">	                		
		<span>ユーザ名</span> <span>ロール</span> <span>パスワード</span> <span></span>
		<input type="text" x-model="json.name">
		<input type="text" name="json.role">
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
				updateUser: (json) => {
					api.put('/users/' + user.name + '/put', json)
						.then(res => {
							// 成功メッセージ表示
						})
						.catch(e => console.error(e));
				},
				deleteUser: (json) => {
					api.delete('/users/' + user.name + '/delete, json)
						.then(res => {
							// 成功メッセージ表示
						})
						.catch(e => console.error(e));
				},
			}">
			<div class="row">               		
				<input type="text" x-model="json.role">
				<input type="password" x-model="json.password">
				<button @click="updateUser(user)">更新</button>
				<button @click="deleteUser(user)">削除</button>
			</div>
		</template>
	</div>

	<script type="module">
		import api from '${mvc.basePath}/../api.js';
		api.start('${mvc.basePath}/api', '${mvc.csrf.token}');
	</script>
</body>
</html>
