/* 
 * REST API helper for Alpine.js
 * provided by Hidekazu Kubota under the BSD License.
 */

// Each module version should match the versions
// specified in the dependencies section of your build.gradle file. 
import axios from './webjars/axios/1.7.2/dist/esm/axios.js';
import Alpine from './webjars/alpinejs/3.14.1/dist/module.esm.js';

let client = null;

const start = (rootEndpointURL, csrfToken) => {
	client = axios.create({
		baseURL: rootEndpointURL,
		headers: {
			'Content-Type': 'application/json',
			'X-CSRF-TOKEN': csrfToken
		},
	});
	delete api.start;
	window.api = api;
	window.Alpine = Alpine;
	Alpine.start();
};

const apiRequest = async (method, url, sendJson = null) => {
	const receivedJson = (await client[method](url, sendJson)).data;
	console.debug(receivedJson);
	return receivedJson;
};

const api = {
	start,
	get: (url) => apiRequest('get', url),
	post: (url, json) => apiRequest('post', url, json),
	put: (url, json) => apiRequest('put', url, json),
	delete: (url) => apiRequest('delete', url),
};
export default api;
