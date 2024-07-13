<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="${mvc.basePath}/../app.css" rel="stylesheet">
<title>メッセージアプリ：メッセージ</title>
</head>
<body
	x-data="{ messages: [] }"
	x-init="$get('/messages', { error: '一覧を取得できませんでした' }).then(res => { if (res.status==200) messages = res.data })">

	[<a href="${mvc.basePath}/">ホーム</a>][<a href="${mvc.basePath}/users">ユーザ管理</a>][<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>
	<div>${ mvc.encoders.html(req.getRemoteUser()) }${ req.isUserInRole("ADMIN") ? "[管理者]" : "" }さん、こんにちは！
	</div>

	<form x-data="{ param: { message: ''} }"
		@submit.prevent="$post('/messages', { param, error: '投稿できませんでした' })
			.then(res => { if (res.status==201) messages.push(res.data), json.message = '' })">
		メッセージ：<input type="text" x-model="param.message">
		<button>送信</button>
	</form>

	<form x-data="{ keyword: '' }"
		@submit.prevent="$get('/messages?keyword=' + keyword, { error: '検索できませんでした' })
			.then(res => { if (res.status==200) messages = res.data })">
		検索語：<input type="text" x-model="keyword">
		<button>検索</button>
	</form>

	<div>
		<button @click="$delete('/messages', { success: '全メッセージを削除しました', error: '削除できませんでした' })
			.then(res => { if (res.status==204) messages.length=0 })">Clear</button>
	</div>

	<div style="color: green" x-show="$store.successes.length > 0">
		<template x-for="success in $store.successes">
			<div x-text="success"></div>
		</template> 
	</div>

	<div style="color: red" x-show="$store.errors.length > 0">
		<template x-for="error in $store.errors">
			<div x-text="error"></div>
		</template> 
	</div>
	
	<h1>メッセージ一覧</h1>
	<div>
		<template x-for="mes in messages">
			<div>
				<span x-text="mes.name"></span>:<span x-text="mes.message"></span>
			</div>
		</template>
	</div>

	<script type="module">
		import rest from '${mvc.basePath}/../rest.js';
		rest.start('${mvc.basePath}/api', '${mvc.csrf.token}');
	</script>
</body>
</html>