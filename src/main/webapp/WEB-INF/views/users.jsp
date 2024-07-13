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
	x-data="{ users: []	}"
	x-init="$get('/users', { error: '一覧を取得できませんでした' }).then(res => if(res.status==200) users = res)">

	[<a href="${mvc.basePath}/">ホーム</a>][<a href="${mvc.basePath}/list">メッセージページ</a>][<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>

	<div style="color: green" x-show="$store.successes.length > 0" @click.outside="$store.successes.length=0">
		<template x-for="success in $store.successes">
			<div x-text="success"></div>
		</template> 
	</div>

	<div style="color: red" x-show="$store.errors.length > 0">
		<template x-for="error in $store.errors">
			<div x-text="error"></div>
		</template> 
	</div>

	<h1>新規ユーザ追加</h1>

	<div class="row_create" autocomplete="off"
		x-data="{ param: { name: '', role: '', password: ''} }"
		@submit.prevent="$post('/users', { param, success: 'ユーザを作成しました', error: 'ユーザを作成できませんでした' })
			.then(res => { if (res.status==201) users.push(res), json = { name: '', role: '', password: ''} })">
		<span>ユーザ名</span> <span>ロール</span> <span>パスワード</span> <span></span>
		<input type="text" x-model="param.name">
		<input type="text" x-model="param.role">
		<input type="password" x-model="param.password">
		<button>追加</button>
	</div>
	<hr>
	<h1>ユーザ一覧</h1>
	<div>
		<div class="row">
			<div>ユーザ名</div>
			<div>ロール</div>
			<div>パスワード</div>
		</div>
		<template x-for="user in users" :key="user.name">
			<div class="row"
				x-data="{ name: user.name, param: { role: user.role, password: user.password }}">
				<span x-text="name"></span>
				<input type="text" x-model="param.role">
				<input type="password" x-model="param.password">
				<button @click="$put('/users/' + name, { param, success: '更新しました', error: '更新できませんでした' })
					.then(() => { if (res.status==200) row.password = '' })">更新</button>
				<button @click="$delete('/users/' + name, { success: '削除しました。', error: '削除できませんでした' })
					.then(() => { if (res.status==204) users = users.filter(u => u.name != name) })">削除</button>
			</div>
		</template>
	</div>

	<script type="module">
		import rest from '${mvc.basePath}/../rest.js';
		rest.start('${mvc.basePath}/api', '${mvc.csrf.token}');
	</script>
</body>
</html>
