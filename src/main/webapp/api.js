/* 
 * REST API helper for Alpine.js
 * This software is provided by Hidekazu Kubota under the BSD License.
 */
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

const apiRequest = async (method, url, data = null) => {
    try {
        const json = (await client[method](url, data)).data;
		// For your learning, show the JSON received from the server
        console.log(json);
        return json;
    } catch (err) {
        return err.response.status;
    }
};

const get = (url) => apiRequest('get', url);
const post = (url, formData) => apiRequest('post', url, { formData });
const put = (url, formData) => apiRequest('put', url, { formData });
const del = (url) => apiRequest('delete', url);
const isError = (res) => typeof res === 'number';

const api = {
    start,
    get,
    post,
    put,
    delete: del,
    isError
};
export default api;
