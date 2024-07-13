/* 
 * REST API helper for Alpine.js
 * provided by Hidekazu Kubota under the BSD License.
 */

// Each module version should match the versions
// specified in the dependencies section of your build.gradle file. 
import axios from './webjars/axios/1.7.2/dist/esm/axios.js';
import Alpine from './webjars/alpinejs/3.14.1/dist/module.esm.js';

/**
 * 初期化関数。必ず最初に呼び出すこと。
 * @param {string} rootEndpointURL - ルートエンドポイントのURL
 * @param {string} csrfToken - CSRFトークン
 */
const start = (rootEndpointURL, csrfToken) => {
	delete rest.start;

	// Alpine.jsのストア。html側のAplineコンポーネントからは$successes, $errorsで参照可能
	Alpine.store('successes', []);
	Alpine.store('errors', []);

	const client = axios.create({
		baseURL: rootEndpointURL,
		headers: {
			'Content-Type': 'application/json',
			'X-CSRF-TOKEN': csrfToken
		},
	});

	const showError = (e, defaultErrorMessage = 'Error') => {
		// レスポンスの本文はaxiosのエラーオブジェクトからe.response.dataで取得
		// ここでは400エラーのときに、サーバが次の本文を返すことを想定
		// ｛type: 'constraint_error', errors: ['エラー１', 'エラー2',...] }
		if (e.response?.data?.type == 'constraint_error' && e.response?.data?.errors) {
			Alpine.store('errors').push(...e.response.data.errors);
		}
		// その他のエラーレスポンス
		else if (e.response) {
			Alpine.store('errors').push(defaultErrorMessage + ': ' + e.response.status + " " + e.response.statusText);
		}
		// e.responseがない場合はクライアント側で発生したエラー 
		else {
			Alpine.store('errors').push(defaultErrorMessage + ': ' + e.message);
		}
	};

	/**
	 * 指定メソッドのREST API呼び出しを行う関数を返す
	 * @param {string} method - 使用するHTTPメソッド（'get', 'post', 'put', 'delete'など）。
	 */
	const restFetch = (method) =>
		/**
		 * REST API呼び出しの共通処理
		 * $successes, $errorsに処理結果に対応するメッセージを追加し、レスポンスを返す。
		 * 
		 * @param {string} endpoint - ルートエンドポイント以降のURL
		 * @param {Object} options - リクエストオプション。
		 * @param {Object} options.param - JSONとして送信するオブジェクト。
		 * @param {string} [options.success] - 成功時のメッセージ。
		 * @param {string} [options.error] - エラー時のデフォルトメッセージ。
		 * @returns {Promise<Object>} レスポンスのステータスとデータを含むオブジェクトを返す。
		 * @returns {number} return.status - HTTPレスポンスステータス。
		 * @returns {Object|null} return.data - レスポンスデータまたはnull（エラー時）。
		 */
		async (endpoint, { param, success, error }) => {
			Alpine.store('successes').length = 0;
			Alpine.store('errors').length = 0;
			let status;
			return client[method](endpoint, param)
				.then(res => {
					status = res.status;
					return res.data;
				})
				.then(receivedJson => {
					if (success) Alpine.store('successes').push(success)
					console.debug(receivedJson);
					return { status, data: receivedJson };
				})
				.catch(e => {
					showError(e, error);
					return { status: e.response.status, data: null };
				});
		};
	Alpine.magic('get', () => restFetch('get'));
	Alpine.magic('post', () => restFetch('post'));
	Alpine.magic('put', () => restFetch('put'));
	Alpine.magic('delete', () => restFetch('delete'));
	Alpine.magic('rest', () => method => restFetch(method));

	window.Alpine = Alpine;
	Alpine.start();
};

const rest = {
	start,
};
export default rest;
