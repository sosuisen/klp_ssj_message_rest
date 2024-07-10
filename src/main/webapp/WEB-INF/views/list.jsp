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
<body x-data="{ error: '', messages: [] }">
	[<a href="${mvc.basePath}/">ホーム</a>] [<a href="${mvc.basePath}/users">ユーザ管理</a>] [<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>
	<div>${ mvc.encoders.html(req.getRemoteUser()) }${ req.isUserInRole("ADMIN") ? "[管理者]" : "" }さん、こんにちは！
	</div>

	<form x-data="{ formData: { message: ''} }"
		@submit.prevent="
			const res = await apiPost('/messages', $data);
			if(isError(res)) error = '投稿できませんでした[Error: ' + res + ']';
			else{
				formData.message = '';
	    		messages.push(res);
	    	}
	    ">
		メッセージ：<input type="text" x-model="formData.message">
		<button>送信</button>
	</form>

	<form x-data="{ keyword: '' }"
		@submit.prevent="
			const res = await apiGet('/messages?keyword=' + keyword);
			if(isError(res)) error = '検索できませんでした[Error: ' + res + ']';
			else messages = res;
		">
		検索語：<input type="text" x-model="keyword">
		<button>検索</button>
	</form>

	<form
		@submit.prevent="
		    const res = await apiDelete('/messages');
			if(isError(res)) error = '削除できませんでした[Error: ' + res + ']';
		    else messages = [];
		  ">
		<button>Clear</button>
	</form>
	<div style="color: red" x-text="error" @click.outside="error = ''"></div>
	<hr>
	<h1>メッセージ一覧</h1>
	<div
		x-init="
			const res = await apiGet('/messages');
			if(isError(res)) error = '一覧を取得できませんでした[Error: ' + res + ']';
			else messages = res;
		">
		<template x-for="mes in messages">
			<div>
				<span x-text="mes.name"></span>:<span x-text="mes.message"></span>
			</div>
		</template>
	</div>

	<script defer
		src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
	<script>
    	const api = axios.create({
        	            baseURL: '${mvc.basePath}/api',
        	            headers: {
        	            	'Content-Type': 'application/json',
            	            'X-CSRF-TOKEN': '${mvc.csrf.token}'
        	            },
    	});
    	const apiRequest = async (method, url, data = null) => {
    	    try {
    	        return (await api[method](url, data)).data;
    	    } catch (err) {
    	        return err.response.status;
    	    }
    	};
    	const apiGet = (url) => apiRequest('get', url);
    	const apiPost = (url, { formData }) => apiRequest('post', url, formData);
    	const apiDelete = (url) => apiRequest('delete', url);
    	const isError = (res) => typeof res === 'number';
	</script>
</body>
</html>