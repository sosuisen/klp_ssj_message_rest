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
<body x-data="{ error: '', messages: [] }"
	x-init="
		api.get('/messages')
			.then(res => messages = res)
			.catch(e => error = '一覧を取得できませんでした[Error: ' + res + ']');
	">
	[<a href="${mvc.basePath}/">ホーム</a>] [<a href="${mvc.basePath}/users">ユーザ管理</a>] [<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>
	<div>${ mvc.encoders.html(req.getRemoteUser()) }${ req.isUserInRole("ADMIN") ? "[管理者]" : "" }さん、こんにちは！
	</div>

	<form x-data="{ json: { message: ''} }"
		@submit.prevent="
			api.post('/messages', json)
				.then(res => {
					json.message = '';
	    			messages.push(res);
				})
				.catch(e => error = '投稿できませんでした[Error: ' + e.response.status + ']');
	    ">
		メッセージ：<input type="text" x-model="json.message">
		<button>送信</button>
	</form>

	<form x-data="{ keyword: '' }"
		@submit.prevent="
			api.get('/messages?keyword=' + keyword)
				.then(res => messages = res)
				.catch(e => error = '検索できませんでした[Error: ' + e.response.status + ']');
		">
		検索語：<input type="text" x-model="keyword">
		<button>検索</button>
	</form>

	<form
		@submit.prevent="
			api.delete('/messages')
				.then(() => messages = []) 
		    	.catch(e => error = '削除できませんでした[Error: ' + e.response.status + ']');
		  ">
		<button>Clear</button>
	</form>

	<div style="color: red" x-text="error" @click.outside="error = ''"></div>

	<h1>メッセージ一覧</h1>
	<div>
		<template x-for="mes in messages">
			<div>
				<span x-text="mes.name"></span>:<span x-text="mes.message"></span>
			</div>
		</template>
	</div>

	<script type="module">
		import api from '${mvc.basePath}/../api.js';
		api.start('${mvc.basePath}/api', '${mvc.csrf.token}');
	</script>
</body>
</html>