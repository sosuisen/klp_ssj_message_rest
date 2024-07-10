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
<body x-data="{ messages: []  }">
	[
	<a href="${mvc.basePath}/">ホーム</a>] [
	<a href="${mvc.basePath}/users">ユーザ管理</a>] [
	<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>
	<div>${ mvc.encoders.html(req.getRemoteUser()) }${ req.isUserInRole("ADMIN") ? "[管理者]" : "" }さん、こんにちは！
	</div>

	<div x-data="{ formData: { message: ''} }">
		<form @submit.prevent="
			const mes = await post('${mvc.basePath}/api/messages', $data);
			formData.message = '';
	    	$store.messages.push(mes);
	    ">
			メッセージ：<input type="text" x-model="formData.message">
			<button>送信</button>
		</form>
	</div>
	<form>
		検索語：<input type="text" name="keyword">
		<button>検索</button>
	</form>
	<div style="color: red">
		<c:forEach var="err" items="${messageForm.error}">
            ${err}
        </c:forEach>
	</div>
	<form action="${mvc.basePath}/clear" method="POST">
		<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}" />
		<button>Clear</button>
	</form>
	<hr>
	<h1>メッセージ一覧</h1>
	<div x-init="$store.messages = await get('${mvc.basePath}/api/messages')">
		<template x-for="mes in $store.messages">
			<div>
				<span x-text="mes.name"></span>:<span x-text="mes.message"></span>
			</div>
		</template>
	</div>
	<script defer
		src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
	<script>
    	document.addEventListener('alpine:init', () => Alpine.store('messages', []));
		const get = async (url) => {
			return (await axios.get(url, {
				headers: {
		            'X-CSRF-TOKEN': '${mvc.csrf.token}'
		        },
		    })).data;
		};
		const post = async (url, { formData }) => {
			return (await axios.post(url, formData, {
        		headers: {
            		'Content-Type': 'application/json',
            		'X-CSRF-TOKEN': '${mvc.csrf.token}'
        		},
    		})).data;
		};
	</script>
</body>
</html>
