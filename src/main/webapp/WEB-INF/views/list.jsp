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
<body x-data="{ messages: [] }">
	[<a href="${mvc.basePath}/">ホーム</a>]
	[<a href="${mvc.basePath}/users">ユーザ管理</a>]
	[<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>
	<div>${ mvc.encoders.html(req.getRemoteUser()) }${ req.isUserInRole("ADMIN") ? "[管理者]" : "" }さん、こんにちは！
	</div>

	<form
		x-data="{ formData: { message: ''} }"
		@submit.prevent="
			const mes = await apiPost('${mvc.basePath}/api/messages', $data);
			if (mes != null) {  
				formData.message = '';
	    		$store.messages.push(mes);
	    	}
	    ">
		メッセージ：<input type="text" x-model="formData.message">
		<button>送信</button>
	</form>

	<form 
		x-data="{ keyword: '' }"
		@submit.prevent="
			$store.messages = await apiGet('${mvc.basePath}/api/messages?keyword=' + keyword);
		">
		検索語：<input type="text" x-model="keyword">
		<button>検索</button>
	</form>

	<div style="color: red">
		<c:forEach var="err" items="${messageForm.error}">
            ${err}
        </c:forEach>
	</div>

	<form @submit.prevent="
		    const success = await apiDelete('${mvc.basePath}/api/messages');
		    if (success) $store.messages = [];
		  ">
		<button>Clear</button>
	</form>

	<hr>
	<h1>メッセージ一覧</h1>
	<template
		x-init="$store.messages = await apiGet('${mvc.basePath}/api/messages')"
		x-for="mes in $store.messages"> 
		<div>
			<span x-text="mes.name"></span>:<span x-text="mes.message"></span>
		</div>
	</template>

	<script defer
		src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
	<script>
		// messagesは複数箇所で使われるため、グローバルの$storeで共有します。
    	document.addEventListener('alpine:init', () => Alpine.store('messages', []));
		const apiGet = async (url) => {
			try {
				return (await axios.get(url, {
					headers: {
			            'X-CSRF-TOKEN': '${mvc.csrf.token}'
		    	    },
			    })).data;
		    }
    		catch (err) {
        		alert("Error: " + err.response.status);
        		return null;
    		}
		};
		// ここはx-dataで宣言されたデータのスコープ外のため、$data経由でデータを受け取ります。
		const apiPost = async (url, { formData }) => {
			try {
				return (await axios.post(url, formData, {
        			headers: {
            		'Content-Type': 'application/json',
            		'X-CSRF-TOKEN': '${mvc.csrf.token}'
	        		},
    			})).data;
    		}
    		catch (err) {
        		alert("Error: " + err.response.status);
        		return null;
    		}
		};
		const apiDelete = async (url) => {
			try {
				await axios.delete(url, {
					headers: {
		            	'X-CSRF-TOKEN': '${mvc.csrf.token}'
		        	},
		    	});
    			return true;
    		}
    		catch (err) {
        		alert("Error: " + err.response.status);
        		return false;
    		}
		};
	</script>
</body>
</html>