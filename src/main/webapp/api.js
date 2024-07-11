const api = axios.create({
	            baseURL: '${mvc.basePath}/api',
	            headers: {
	            	'Content-Type': 'application/json',
    	            'X-CSRF-TOKEN': '${mvc.csrf.token}'
	            },
});
const apiRequest = async (method, url, data = null) => {
    try {
		const json = (await api[method](url, data)).data;
		// 参考のため、受信したJSONをコンソールに表示
		console.log(json);
        return json; 
    } catch (err) {
        return err.response.status;
    }
};
const apiGet = (url) => apiRequest('get', url);
const apiPost = (url, { formData }) => apiRequest('post', url, formData);
const apiPut = (url, { formData }) => apiRequest('put', url, formData);
const apiDelete = (url) => apiRequest('delete', url);
const isError = (res) => typeof res === 'number';