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
	x-data="{ messages: [], success: '', errors: [],
		showErr(e, mes) {
			if(e.response?.data?.errors) this.errors = e.response.data.errors; // 制約条件エラー
			else if(e.response) this.errors.push(mes + ': ' + e.response.status); // その他のサーバエラー
			else this.errors.push('処理できませんでした: ' + e.message); // クライアントエラー
        },
		resetErr(){ this.errors = [] }
	}"
	x-init="
		api.get('/messages')
			.then(res => messages = res)
			.catch(e => errors.push('一覧を取得できませんでした[Error: ' + res + ']'));
	">
	[<a href="${mvc.basePath}/">ホーム</a>][<a href="${mvc.basePath}/users">ユーザ管理</a>][<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>
	<div>${ mvc.encoders.html(req.getRemoteUser()) }${ req.isUserInRole("ADMIN") ? "[管理者]" : "" }さん、こんにちは！
	</div>

	<form x-data="{ json: { message: ''} }"
		@submit.prevent="
			resetErr();
			api.post('/messages', json)
				.then(res => {
	    			messages.push(res);
					json.message = '';	    			
				})
				.catch(e => showErr(e, '投稿できませんでした'));
	    ">
		メッセージ：<input type="text" x-model="json.message">
		<button>送信</button>
	</form>

	<form x-data="{ keyword: '' }"
		@submit.prevent="
		    resetErr();
			api.get('/messages?keyword=' + keyword)
				.then(res => messages = res)
				.catch(e => showErr(e, '検索できませんでした'));
		">
		検索語：<input type="text" x-model="keyword">
		<button>検索</button>
	</form>

	<form
		@submit.prevent="
		    resetErr();
			api.delete('/messages')
				.then(() => {
					messages = [];
					success = '全メッセージを削除しました。';
				}) 
				.catch(e => showErr(e, '削除できませんでした'));
		  ">
		<button>Clear</button>
	</form>

	<div style="color: green" x-show="success">
		<span x-text="success" @click.outside="success = ''"></span>
	</div>

	<div style="color: red" x-show="errors.length > 0">
		<template x-for="err in errors">
			<div x-text="err"></div>
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
		import api from '${mvc.basePath}/../api.js';
		api.start('${mvc.basePath}/api', '${mvc.csrf.token}');
	</script>
</body>
</html>