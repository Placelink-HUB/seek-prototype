/*
 * SEEK
 * Copyright (C) 2025 placelink
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * =========================================================================
 *
 * 상업적 이용 또는 AGPL-3.0의 공개 의무를 면제받기
 * 위해서는, placelink로부터 별도의 상업용 라이선스(Commercial License)를 구매해야 합니다.
 * For commercial use or to obtain an exemption from the AGPL-3.0 license
 * requirements, please purchase a commercial license from placelink.
 * *** 문의처: help@placelink.shop (README.md 참조)
 */
import {hideS2Loading, showS2Loading} from './s2.loading.js';

/**
 * 템플릿 리터럴(다중 라인 문자열)에서 들여쓰기를 제거
 *
 * @returns {string} 들여쓰기가 제거된 문자열
 */
String.prototype.dedent = function () {
    // !!s2!! this 가 문자열 객체(String Object)를 참조 하도록 정규 함수를 사용하여야 한다. (화살표 함수 사용시 this 바인딩 문제 발생)
    const lines = this.split('\n');
    const nonBlankLines = lines.filter((line) => line.trim());

    if (nonBlankLines.length === 0) {
        return this.trim(); // 전체 문자열을 trim하여 반환
    }

    const minIndent = Math.min(...nonBlankLines.map((line) => line.match(/^(\s*)/)[0].length));

    return lines
        .map((line) => line.slice(minIndent))
        .join('\n')
        .trim();
};

/**
 * @fileoverview S2Util은 클라이언트 측에서 자주 사용되는 다양한 유틸리티 함수(Utility Functions)를 제공하는 라이브러리 객체이다.
 * 주로 DOM 조작, 템플릿 처리, AJAX 요청(fetch), 로컬 스토리지 관리, 푸시 알림 구독 등 웹 애플리케이션 개발에 필수적인 기능을 포함한다.
 *
 * 이 객체는 ES 모듈로 export 되어 사용된다.
 * ES2015(ES6) 이상의 환경에서 사용 가능하며 export/import 구문을 통해 모듈로서 활용할 수 있다.
 * (그에 따라 <script type="module">을 지원하지 않는 구형 브라우저(대략 2018년 5월 이전)에서는 사용이 제한될 수 있다.)
 * ※ document.querySelector('.create-article-btn')?.addEventListener('click', () => {}); 처럼 옵셔널 체이닝 연산자(?.)도 ES2020 이상에서 지원하여 사용 안함
 *
 * @version 1.0.0
 * @author devers2 (Daejeon, Korea)
 *
 * @exports S2Util
 *
 * @example
 * 1. S2Util 사용 예시:
 * <script type="module">
 *     import { S2Util } from './js/s2.util.js'; // Thymeleaf 는 '[[@{/js/s2.util.js}]]' 형태로 경로 지정
 *     S2Util.alert('Hello, S2Util!');
 *     ...
 * </script>
 *
 * 2. initializeS2DomEvents 함수와 같이 사용 예시:
 * <script type="module">
 *     import { S2Util, initializeS2DomEvents } from './js/s2.util.js'; // Thymeleaf 는 '[[@{/js/s2.util.js}]]' 형태로 경로 지정
 *     document.addEventListener('DOMContentLoaded', initializeS2DomEvents);
 *     S2Util.alert('Hello, S2Util!');
 *     ...
 * </script>
 */
export const S2Util = {
    /**
     * 대상 DOM 요소의 모든 자식 노드를 안전하게 삭제하거나, 주어진 새로운 노드들로 한 번에 대체한다.
     *
     * 이 유틸리티는 일부 레거시 환경에서 지원하지 않을 수 있는 **네이티브 Element.replaceChildren (ES2021)** 메서드를
     * 안전하게 대체하기 위해 설계되었다. 이를 통해 target.innerHTML = '';와 같은 파괴적인 DOM 조작 대신
     * DOM 노드의 이벤트 리스너 파괴 위험 없이 자식 노드를 처리한다.
     * * 두 번째 인자가 없고 options.isAppend를 true로 설정하지 않는다면, 기존 자식을 모두 삭제하여 안전하게 초기화하는 역할을 수행한다.
     *
     * @typedef {object} ReplaceChildrenOptions
     * @property {boolean} [isAppend=false] - 기존 대상 요소의 자식 노드를 삭제하지 않고 유지할지 여부. true: 기존 자식 뒤에 새로운 노드가 추가(append 모드), false: 기존 자식을 모두 삭제하고 새로운 노드로 대체(replace 모드)
     * @property {function(Node, number): void} [onNodeReady=null] - 각 새 노드(Node 객체)가 DocumentFragment에 추가되기 직전에 실행되는 콜백 함수 (주로 이벤트 리스너 바인딩, 속성 추가 등 DOM 삽입 전 최종 작업을 수행하는 데 사용된다.)
     *
     * @param {Element|string} target - 자식을 대체할 대상 DOM 요소 또는 CSS 선택자 문자열.
     * @param {Array<Element|string|DocumentFragment>|Element|string|DocumentFragment} [newChildren=[]] - 대상의 새로운 자식으로 삽입할 노드(Element), DocumentFragment, 또는 HTML 문자열/노드 배열.
     * @param {ReplaceChildrenOptions} [options={}] - 옵션 객체
     * @returns {Element|null} - 자식이 대체된 대상 DOM 요소 (target)를 반환한다.
     */
    replaceChildren(target, newChildren = [], options = {}) {
        const defaultOptions = {
            isAppend: false, // 기본값: 기존 자식을 지운다 (replace)
            onNodeReady: null
        };
        const finalOptions = {...defaultOptions, ...options};

        // 대상 DOM 요소 확보
        const targetEl = typeof target === 'string' ? document.querySelector(target) : target;

        if (!targetEl || !(targetEl instanceof Element)) {
            console.error('replaceChildren: 유효한 DOM 요소를 찾을 수 없습니다. 대상:', target);
            return null;
        }

        let nodesToInsert = [];

        // 인자 처리: 새로운 자식 노드 배열 생성
        if (newChildren) {
            // 단일 값이면 배열로 만든다
            if (!Array.isArray(newChildren)) {
                newChildren = [newChildren];
            }

            newChildren.forEach((child) => {
                if (child instanceof Element || child instanceof DocumentFragment) {
                    // DOM 노드나 Fragment는 그대로 추가한다
                    nodesToInsert.push(child);
                } else if (typeof child === 'string') {
                    // HTML 문자열이면 임시 컨테이너를 이용해 DOM 객체로 안전하게 변환한다
                    nodesToInsert.push(...S2Util.createNodeFragment(child));
                } else {
                    // 그 외 값(숫자 등)은 text node로 전달 가능하니 그대로 추가한다
                    nodesToInsert.push(child);
                }
            });
        }

        {
            /*
             * [ES Module 환경에서 안전한 DOM 삽입]
             * !!s2!! 기존 DOM 구조를 파괴하지 않기 위해 임시 컨테이너(tempContainer)를 생성한 후 HTML을 삽입한다.
             * document.body.innerHTML += modalHTML; 와 같은 직접적인 innerHTML 조작은 대입 연산자(=)든 덧셈 대입 연산자(+=)든
             * 해당 요소(여기서는 body 전체)의 내부 DOM 구조를 문자열로 대체하여 기존에 바인딩된 모든 이벤트 리스너를 제거하므로 사용하지 않는다.
             * 대신, appendChild()를 사용하여 기존 DOM 트리를 파괴하지 않고 새로운 요소만 안전하게 추가한다.
             */
            // !!s2!! 메모리에만 존재하는 DocumentFragment를 활용한 최종 노드 준비 (성능 최적화)
            const fragment = document.createDocumentFragment();
            // !!s2!! 삽입할 노드들을 실제 DOM이 아닌 fragment에 추가 (리플로우 방지)
            nodesToInsert.forEach((node, index) => {
                if (typeof finalOptions.onNodeReady === 'function') {
                    // Node가 준비되었을 때 콜백에 현재 노드와 인덱스를 전달하여 필요한 이벤트 바인딩 및 추가 작업을 수행할 수 있도록 한다.
                    finalOptions.onNodeReady(node, index);
                }
                fragment.appendChild(node);
            });

            if (!finalOptions.isAppend) {
                // 기존 자식 노드를 모두 안전하게 삭제한다.
                while (targetEl.firstChild) {
                    targetEl.removeChild(targetEl.firstChild);
                }
            }

            // !!s2!! fragment의 내용을 appendChild로 한 번에 추가한다. (최적화, DocumentFragment를 사용하여 단 한 번의 DOM 조작으로 삽입한다.)
            targetEl.appendChild(fragment);
        }

        return targetEl;
    },
    /**
     * HTML 문자열을 파싱하여 DOM 노드 배열을 생성한다.
     *
     * @param {string} htmlString - 파싱할 HTML 문자열 (예: '<tr><td>...</td></tr>')
     * @returns {Node[]} Node 객체의 배열
     */
    createNodeFragment(htmlString) {
        // 유효성 검사 및 문자열 정리
        if (typeof htmlString !== 'string') {
            return [];
        }
        const trimmedHtml = htmlString.trim();
        if (trimmedHtml.length === 0) {
            return [];
        }

        // HTML 문자열의 시작 부분을 효율적으로 확인하기 위해 최대 7글자만 추출한다.
        const htmlPrefix = trimmedHtml.substring(0, Math.min(trimmedHtml.length, 7)).toLowerCase();

        let containerHtml;
        let extractionSelector;

        // DOMParser 사용을 위해 비표준 태그를 보정한다. (테이블 파편화 문제 해결)
        if (htmlPrefix.startsWith('<tbody')) {
            containerHtml = `<table>${trimmedHtml}</table>`;
            extractionSelector = 'table';
        } else if (htmlPrefix.startsWith('<tr')) {
            containerHtml = `<table><tbody>${trimmedHtml}</tbody></table>`;
            extractionSelector = 'table > tbody';
        } else if (htmlPrefix.startsWith('<td') || htmlPrefix.startsWith('<th')) {
            containerHtml = `<table><tbody><tr>${trimmedHtml}</tr></tbody></table>`;
            extractionSelector = 'table > tbody >tr';
        } else if (htmlPrefix.startsWith('<li')) {
            containerHtml = `<ul>${trimmedHtml}</ul>`;
            extractionSelector = 'ul';
        } else {
            containerHtml = `<div>${trimmedHtml}</div>`;
            extractionSelector = 'div';
        }

        // DOMParser 생성 (XSS 방지를 위해 innerHTML 대신 DOMParser 사용)
        const parser = new DOMParser();
        const parsedDocument = parser.parseFromString(containerHtml, 'text/html');
        const container = parsedDocument.querySelector(extractionSelector);

        if (container) {
            return Array.from(container.children);
        }

        return [];
    },
    /**
     * 자바스크립트의 기본 fetch API를 활용하여 다양한 요청 처리
     *
     * @typedef {object} FetchConfig
     * @property {'GET'|'POST'|'PUT'|'PATCH'|'DELETE'} [method='GET'] 요청 메소드
     * @property {'JSON'|'FORM'} [dataType] 요청 본문(body) 데이터 타입. 'JSON'일 경우 Content-Type: application/json 설정 및 서버 Controller 에서 @RequestBody 로 처리.
     * @property {'JSON'|'BLOB'|'HTML'} [responseType='JSON'] 응답 데이터 타입.
     * @property {boolean} [disableDefaultErrorHandler=false] 오류 발생 시 기본 오류 핸들링 처리(alert, confirm 등) 비활성화 여부
     * @property {number} [timeout=600000] 응답 대기 시간 (밀리초). 기본 10분(600000ms).
     * @property {boolean} [showOverlay=false] 응답 대기 로딩 오버레이 표시 여부
     * @property {boolean} [hideLoading=false] 응답 대기 로딩 표시 숨김 여부
     *
     * @param {string} url 요청을 보낼 서버 엔드포인트 URL
     * @param {object|FormData|string} param 요청 설정값(FetchConfig의 속성)과 서버로 전송할 데이터가 담긴 객체 (JSON, FormData, 또는 QueryString 형태)
     * @param {function(any): void} [success] 요청 성공 시 호출될 콜백 함수. 응답 데이터(JSON/HTML/BLOB URL)를 인자로 받는다.
     * @param {function(Error): void} [fail] 요청 실패 시(HTTP 오류, 타임아웃, 서버 커스텀 오류 등) 호출될 콜백 함수. Error 객체를 인자로 받는다.
     * @returns {void}
     */
    fetch(url, param, success, fail) {
        console.debug('fetch start:', new Date());
        const option = {
            headers: {
                'X-S2-Request': 's2-fetch'
            }
        };

        let paramType = '';
        let method = 'GET';
        let methodChecker = '';
        let dataType = '';
        let dataTypeChecker = '';
        let responseType = 'JSON';
        let responseTypeChecker = '';
        let disableDefaultErrorHandler;
        let timeout;
        let showOverlay;
        let hideLoading;

        if (S2Util.isFormData(param)) {
            paramType = 'FormData';
            methodChecker = param.get('method');
            dataTypeChecker = param.get('dataType');
            responseTypeChecker = param.get('responseType');
            disableDefaultErrorHandler = param.get('disableDefaultErrorHandler');
            timeout = param.get('timeout');
            showOverlay = param.get('showOverlay');
            hideLoading = param.get('hideLoading');

            param.delete('method');
            param.delete('dataType');
            param.delete('responseType');
            param.delete('disableDefaultErrorHandler');
            param.delete('timeout');
            param.delete('showOverlay');
            param.delete('hideLoading');
        } else if (S2Util.isJSON(param)) {
            paramType = 'JSON';
            methodChecker = param.method;
            dataTypeChecker = param.dataType;
            responseTypeChecker = param.responseType;
            disableDefaultErrorHandler = param.disableDefaultErrorHandler;
            timeout = param.timeout;
            showOverlay = param.showOverlay;
            hideLoading = param.hideLoading;

            delete param.method;
            delete param.dataType;
            delete param.responseType;
            delete param.disableDefaultErrorHandler;
            delete param.timeout;
            delete param.showOverlay;
            delete param.hideLoading;
        } else if (S2Util.isQueryString(param)) {
            paramType = 'QueryString';
            methodChecker = S2Util.getQueryStringParameter(param, 'method');
            dataTypeChecker = S2Util.getQueryStringParameter(param, 'dataType');
            responseTypeChecker = S2Util.getQueryStringParameter(param, 'responseType');
            disableDefaultErrorHandler = S2Util.getQueryStringParameter(param, 'disableDefaultErrorHandler');
            timeout = S2Util.getQueryStringParameter(param, 'timeout');
            showOverlay = S2Util.getQueryStringParameter(param, 'showOverlay');
            hideLoading = S2Util.getQueryStringParameter(param, 'hideLoading');

            param = S2Util.removeQueryStringParameter(param, 'method');
            param = S2Util.removeQueryStringParameter(param, 'dataType');
            param = S2Util.removeQueryStringParameter(param, 'responseType');
            param = S2Util.removeQueryStringParameter(param, 'disableDefaultErrorHandler');
            param = S2Util.removeQueryStringParameter(param, 'timeout');
            param = S2Util.removeQueryStringParameter(param, 'showOverlay');
            param = S2Util.removeQueryStringParameter(param, 'hideLoading');
            param = param.trim();
        }

        if (typeof methodChecker === 'string' && methodChecker.trim()) {
            method = methodChecker.toUpperCase();
        }
        if (typeof dataTypeChecker === 'string' && dataTypeChecker.trim()) {
            dataType = dataTypeChecker.toUpperCase();
        }
        if (typeof responseTypeChecker === 'string' && responseTypeChecker.trim()) {
            responseType = responseTypeChecker.toUpperCase();
        }

        // 타임아웃 설정 (param.timeout 이 없다면 기본 10분)
        let controller;
        let timeoutId;
        if (typeof AbortController !== 'undefined') {
            controller = new AbortController();
            option.signal = controller.signal;
            timeoutId = timeoutId = setTimeout(() => controller.abort(), timeout && !isNaN(timeout) ? timeout : 600000);
        }

        switch (method) {
            case 'POST':
            case 'PUT':
                if (dataType === 'JSON') {
                    // JSON 데이터를 전달하는 경우
                    // 서버 Controller 에서 @RequestBody 로 처리
                    option['headers']['Content-Type'] = 'application/json';
                } else {
                    // 파일 업로드(multipart/form-data)를 포함한 그 외의 경우
                    // 서버 Controller 에서 @RequestParam / @RequestPart / @ModelAttribute / MultipartHttpServletRequest 로 처리
                }

                switch (paramType) {
                    case 'FormData':
                        if (!param.keys().next().done) {
                            option['body'] = dataType === 'JSON' ? JSON.stringify(S2Util.formDataToJson(param)) : param;
                        }
                        break;
                    case 'JSON':
                        if (Object.keys(param).length > 0) {
                            /* empty */
                        }
                        option['body'] = dataType === 'JSON' ? JSON.stringify(param) : S2Util.jsonToFormData(param);
                        break;
                    case 'QueryString':
                        if (param) {
                            option['body'] = param;
                        }
                        break;
                }
                break;
            default:
                switch (paramType) {
                    case 'FormData':
                        if (!param.keys().next().done) {
                            url = S2Util.jsonToQueryString(S2Util.formDataToJson(param), url);
                        }
                        break;
                    case 'JSON':
                        if (Object.keys(param).length > 0) {
                            url = S2Util.jsonToQueryString(param, url);
                        }
                        break;
                    case 'QueryString':
                        if (param) {
                            if (url.indexOf('?') !== -1) {
                                url += param.startsWith('&') ? param : `&${param}`;
                            } else {
                                url += param.startsWith('?') ? param : `?${param}`;
                            }
                        }
                        break;
                }
                break;
        }

        option['method'] = method;

        if (hideLoading !== true && hideLoading !== 'true') {
            showS2Loading({showOverlay: showOverlay});
        }

        fetch(url, option)
            .then((response) => {
                if (!response.ok) {
                    // HTTP 상태 코드가 2xx 범위가 아닌 경우 오류
                    return response.text().then((errorData) => {
                        // 서버에서 전송한 오류 메시지 포함
                        const error = new Error(`HTTP error! status: ${response.status}`);
                        error.status = response.status;
                        error.statusText = response.statusText;
                        error.message = errorData;
                        throw error;
                    });
                }

                const result = {
                    type: responseType
                };

                switch (result.type) {
                    case 'BLOB': {
                        let filename = '';

                        const contentDisposition = response.headers.get('Content-Disposition');
                        if (contentDisposition && contentDisposition.includes('attachment')) {
                            const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                            const matches = filenameRegex.exec(contentDisposition);
                            if (matches != null && matches[1]) {
                                filename = matches[1].replace(/['"]/g, '');
                            }
                        }

                        result['data'] = response.blob();
                        result['filename'] = decodeURIComponent(filename);
                        break;
                    }
                    case 'HTML':
                        result['data'] = response.text();
                        break;
                    default:
                        result['data'] = response.json();
                        break;
                }

                return result;
            })
            .then((result) => {
                if (result.type === 'BLOB') {
                    result.data.then((data) => {
                        const url = window.URL.createObjectURL(data);
                        const link = document.createElement('a');
                        link.href = url;
                        link.download = result.filename || 'downloadFile';
                        document.body.appendChild(link);
                        link.click();
                        document.body.removeChild(link);
                        window.URL.revokeObjectURL(url);

                        if (success && typeof success === 'function') {
                            success();
                        }
                    });
                } else {
                    result.data.then((data) => {
                        if (S2Util.isJSON(data)) {
                            if (!data.status) {
                                const error = new Error('Result error!');
                                error.message = data.message;
                                throw error;
                            } else if (data.status !== 'SUCCESS' && data.status.result !== 'SUCCESS') {
                                const error = new Error('Result error!');
                                error.status = data.status;
                                error.message = data.status.message ? data.status.message : data.message;
                                throw error;
                            }
                        }

                        if (success && typeof success === 'function') {
                            success(data);
                        }
                    });
                }

                {
                    // .finally (ES2018) 대체
                    if (hideLoading !== true && hideLoading !== 'true') {
                        hideS2Loading();
                    }
                    if (timeoutId) {
                        clearTimeout(timeoutId); // 완료 시 타임아웃 해제
                    }
                    console.debug('fetch end:', new Date());
                    return result;
                }
            })
            .catch((error) => {
                if (disableDefaultErrorHandler !== true && disableDefaultErrorHandler !== 'true') {
                    if (error.name === 'AbortError') {
                        S2Util.alert('요청 시간이 초과되었습니다.');
                    } else if (typeof error.message === 'string' && (error.message.startsWith('S2Exception:') || error.message.startsWith('S2RuntimeException:'))) {
                        S2Util.alert(error.message);
                    } else if (error.status === 401) {
                        S2Util.confirm('인증이 필요합니다.<br/>로그인 페이지로 이동하시겠습니까?', function () {
                            // 로그인 페이지로 리다이렉트 등 추가 처리 가능
                            location.href = '/login';
                        });
                    } else if (error.status === 403) {
                        S2Util.alert('접근 권한이 없습니다.');
                    } else {
                        S2Util.alert(error.message && error.message.length < 100 ? error.message : '오류가 발생했습니다.');
                    }
                }

                if (fail && typeof fail === 'function') {
                    fail(error);
                }

                {
                    // .finally (ES2018) 대체
                    if (hideLoading !== true && hideLoading !== 'true') {
                        hideS2Loading();
                    }
                    if (timeoutId) {
                        clearTimeout(timeoutId); // 완료 시 타임아웃 해제
                    }
                    console.debug('fetch end:', new Date());
                    throw error;
                }
            });
    },
    /**
     * HTML 문자열 또는 템플릿 ID를 받아 데이터를 치환하여 HTML을 생성한다.
     *
     * @param {string} targetTemplate 템플릿으로 사용할 HTML 문자열 또는 해당 HTML이 담긴 템플릿(<script> 태그/요소) ID
     * @param {Object|Object[]} data 템플릿에 치환할 데이터. 단일 객체이거나, 반복 처리할 객체 배열
     * @returns {string} - 데이터가 치환되고 조건부 속성(if/else)이 처리된 최종 HTML 문자열.
     *
     * @example
     * 1. HTML 템플릿 예시
     * <script id="template_id" type="text/template">
     *     <div if="isKey">{{=key}}</div>
     *     <div else="isKey">{{=key2}}</div>
     * </script>
     *
     * 2. 단일 객체 치환 및 조건부 처리
     * S2Util.template('template_id', {key: 'Value', isValid: true});
     *
     * 3. 데이터 배열 반복 처리
     * S2Util.template('template_id', [{key: 'A'}, {key: 'B'}]);
     *
     * ex1: S2Util.template('You Are So {{=key}}.', {key: 'Beautiful'}); → 'You Are So Beautiful.'
     * ex2: S2Util.template('You Are So {{=key}}.', [{key: 'Beautiful'}, {key: 'Sweet'}]); → 'You Are So Beautiful.You Are So Sweet.'
     * ex3: S2Util.template('You Are So {{=key}}.', {}); → 'You Are So'
     * ex4: S2Util.template('You Are So {{=key}}.', null); → ''
     * ex5: S2Util.template('You Are So <span if="isLove">{{=key}}</span><span else="isLove">{{=key2}}</span>.', {isLove: true, key: 'Beautiful', key2: 'Ordinary'});
     *      → 'You Are So <span>Beautiful</span>.'
     * ex6: S2Util.template('You Are So <span if="isLove">{{=key}}</span><span else="isLove">{{=key2}}</span>.', {isLove: false, key: 'Beautiful', key2: 'Ordinary'});
     *      → 'You Are So <span>Ordinary</span>.'
     */
    template(targetTemplate, data) {
        let resultHtml = '';
        let template = document.getElementById(targetTemplate) ? document.getElementById(targetTemplate).innerHTML : targetTemplate;
        const propsArr = data && Array.isArray(data) ? data : [data];

        if (template) {
            template = template.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, ''); // trim
            if (propsArr && Array.isArray(propsArr) && propsArr.length > 0) {
                propsArr.forEach((props) => {
                    if (props && props instanceof Object) {
                        let replacedHtml = template;

                        // 템플릿 변수 치환
                        for (const key in props) {
                            const value = props[key];
                            replacedHtml = replacedHtml.replace(new RegExp(`{{=${key}}}`, 'g'), value || value === 0 ? value : '');
                        }

                        const trimmedHtml = replacedHtml.trim();
                        const htmlPrefix = trimmedHtml.substring(0, Math.min(trimmedHtml.length, 7)).toLowerCase();

                        const resultContainer = document.createElement(htmlPrefix.startsWith('<tr') ? 'tbody' : 'div');
                        S2Util.replaceChildren(resultContainer, trimmedHtml);

                        const elements = resultContainer.querySelectorAll('[if], [else]');
                        elements.forEach((element) => {
                            if (element.hasAttribute('if')) {
                                // if 속성에 공백으로 2개 이상의 속성을 줄수도 있음
                                const conditionAttributes = element.getAttribute('if').split(/\s+/);
                                let validCount = 0;

                                for (const conditionAttribute of conditionAttributes) {
                                    validCount += props[conditionAttribute] ? 1 : 0;
                                }

                                if (conditionAttributes.length > 0 && conditionAttributes.length === validCount) {
                                    element.removeAttribute('if');
                                } else {
                                    element.remove();
                                }
                            }

                            if (element.hasAttribute('else')) {
                                // else 속성에 공백으로 2개 이상의 속성을 줄수도 있음
                                const conditionAttributes = element.getAttribute('else').split(/\s+/);
                                let validCount = 0;

                                for (const conditionAttribute of conditionAttributes) {
                                    validCount += !props[conditionAttribute] ? 1 : 0;
                                }

                                if (conditionAttributes.length > 0 && conditionAttributes.length === validCount) {
                                    element.removeAttribute('else');
                                } else {
                                    element.remove();
                                }
                            }
                        });

                        resultHtml += resultContainer.innerHTML;
                    }
                });
            }
        }
        return resultHtml.replace(/({{=([^}}]+)}})/g, '');
    },
    /**
     * 데이터를 기반으로 <select> 요소의 <option> 목록을 동적으로 생성한다.
     *
     * @typedef {object} OptionConfig
     * @property {string|HTMLElement} target - <select> 태그의 CSS 선택자(selector) 또는 DOM 요소 객체. (필수)
     * @property {Array<Object>|Object} items - <option>을 생성하는 데 사용할 데이터 배열 또는 단일 객체. (필수)
     * @property {string} itemValue - <option>의 `value` 속성으로 사용될 데이터 필드 이름 또는 템플릿 문자열 (예: 'fieldNm' 또는 '{{=fieldNm1}}.{{=fieldNm2}}'). (필수)
     * @property {string} [itemLabel] - <option>의 표시 텍스트로 사용될 데이터 필드 이름 또는 템플릿 문자열. (생략 가능)
     * @property {string|number|null} [initVal] - 생성 후 <select>에 초기 값으로 설정할 값. (생략 가능)
     * @property {string|null} [clear=null] - <option>을 제거하는 기준. null이면 모든 <option> 제거, 문자열이면 해당 CSS 선택자(selector)에 해당하는 <option>만 제거. (기본값: null)
     *
     * @param {OptionConfig} option - 옵션 설정을 담고 있는 객체.
     * @returns {void}
     */
    options(option) {
        if (!option) {
            return;
        }
        const target = typeof option.target === 'string' ? document.querySelector(option.target) : option.target;
        if (target) {
            if (!option.clear) {
                while (target.firstChild) {
                    target.removeChild(target.firstChild);
                }
            } else if (typeof option.clear === 'string') {
                const elementsToRemove = target.querySelectorAll(option.clear);
                elementsToRemove.forEach((el) => el.remove());
            }

            if (option.items && option.itemValue) {
                if (!Array.isArray(option.items)) {
                    option.items = [option.items];
                }

                const optionElements = option.items.map((item) => {
                    const itemValue = String(option.itemValue);
                    const itemLabel = String(option.itemLabel);

                    // 값 및 레이블 계산 로직 (기존 로직 유지)
                    const value = itemValue.match(/{{=([^}}]+)}}/) ? S2Util.template(itemValue, item) : item[itemValue];
                    const label = itemLabel && itemLabel.match(/{{=([^}}]+)}}/) ? S2Util.template(itemLabel, item) : item[itemLabel];

                    // <option> 요소 생성
                    const optionElement = document.createElement('option');
                    optionElement.value = value || '';
                    optionElement.textContent = label || value;

                    // S2Util.replaceChildren에 전달할 Node 객체 배열로 반환
                    return optionElement;
                });

                S2Util.replaceChildren(target, optionElements, {isAppend: true});

                if (option.initVal) {
                    target.value = option.initVal;
                }
            }
        }
    },
    /**
     * 주어진 데이터가 유효한 JSON 문자열이거나, JSON 객체/배열인지 확인한다.
     * (HTMLFormElement와 같이 JSON으로 변환할 수 없는 일부 객체는 제외)
     *
     * @param {*} data 검증할 데이터
     * @returns {boolean} 데이터가 유효한 JSON 문자열이거나 객체/배열이면 true, 아니면 false.
     */
    isJSON(data) {
        if (!data || Object.prototype.toString.call(data) === '[object HTMLFormElement]') {
            return false;
        } else {
            try {
                let jsonString = data;
                if (typeof jsonString !== 'string') {
                    jsonString = JSON.stringify(jsonString);
                }
                JSON.parse(jsonString);
                return true;
            } catch {
                return false;
            }
        }
    },
    /**
     * 주어진 데이터가 유효한 배열인지 확인한다.
     * (null 또는 undefined를 안전하게 처리)
     *
     * @param {*} data - 검증할 데이터.
     * @returns {boolean} - 데이터가 배열이면 true, 아니면 false.
     */
    isArray(data) {
        return data && Array.isArray(data);
    },
    /**
     * 주어진 숫자에 천 단위 구분 기호(쉼표)를 추가하여 문자열로 반환한다.
     *
     * @param {number|string} number - 쉼표를 추가할 숫자 또는 숫자형 문자열.
     * @returns {string} - 쉼표가 추가된 문자열.
     */
    comma(number) {
        return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },
    /**
     * 페이징 정보를 기반으로 HTML 페이지네이션 마크업을 생성한다.
     *
     * @typedef {object} PaginationInfo
     * @property {number} firstPageNoOnPageList - 현재 페이지 목록(블록)의 첫 번째 페이지 번호.
     * @property {number} lastPageNoOnPageList - 현재 페이지 목록(블록)의 마지막 페이지 번호.
     * @property {number} lastPageNo - 전체 페이지의 마지막 페이지 번호 (총 페이지 수).
     * @property {number} pageNo - 현재 페이지 번호.
     *
     * @param {PaginationInfo} paginationInfo - 페이징 처리에 필요한 정보 객체.
     * @param {string} jsFunction - 페이지 이동 시 호출할 자바스크립트 함수명 (예: 'fn_selectList').
     * @param {Array<string|number>|string|number} [jsParams] - 페이지 처리 함수에 추가로 전달할 매개변수 (페이지 번호는 함수 내부에서 자동으로 추가됨).
     * @returns {string} - `<ul class="pagination">...</ul>` 형태의 HTML 페이지네이션 마크업 문자열.
     */
    pagination(paginationInfo, jsFunction, jsParams) {
        let pagination = '';
        let functionParamStr = '';

        if (jsParams || jsParams === 0) {
            if (!Array.isArray(jsParams)) {
                jsParams = [jsParams];
            }
            for (const idx in jsParams) {
                const param = jsParams[idx];
                if (typeof param === 'string') {
                    functionParamStr += `'${param}', `;
                } else {
                    functionParamStr += `${param}, `;
                }
            }
        }

        const firstPageNoOnPageList = paginationInfo['firstPageNoOnPageList'];
        const lastPageNoOnPageList = paginationInfo['lastPageNoOnPageList'];

        if (firstPageNoOnPageList > 1) {
            pagination += `<li class="paginate_button prev"><a href="#" onclick="${jsFunction}(${functionParamStr + (firstPageNoOnPageList - 1)}); return false;"><i class="fa fa-chevron-left"></i></a></li>`;
        }
        for (let pageNo = firstPageNoOnPageList; pageNo <= lastPageNoOnPageList; pageNo++) {
            if (pageNo === paginationInfo.pageNo) {
                pagination += `<li class="paginate_button active"><a href="#">${pageNo}</a></li>`;
            } else {
                pagination += `<li class="paginate_button"><a href="#" onclick="${jsFunction}(${functionParamStr + pageNo}); return false;">${pageNo}</a></li>`;
            }
        }
        if (lastPageNoOnPageList < paginationInfo['lastPageNo']) {
            pagination += `<li class="paginate_button next"><a href="#" onclick="${jsFunction}(${functionParamStr + (lastPageNoOnPageList + 1)}); return false;"><i class="fa fa-chevron-right"></i></a></li>`;
        }

        return `<ul class="pagination pagination-xs">${pagination}</ul>`;
    },
    /**
     * Pagination 의 Record 번호를 계산한다.
     * (페이징된 목록에서 주어진 현재 페이지의 레코드 번호를 기준으로, 데이터베이스 상의 레코드의 실제 순번(전체 목록 기준)을 계산하여 반환한다)
     *
     * @typedef {object} PaginationRecordInfo
     * @property {number} totalRecordCount - 전체 레코드 수.
     * @property {number} pageUnit - 한 페이지당 표시되는 레코드 수.
     * @property {number} pageNo - 현재 페이지 번호.
     *
     * @param {PaginationRecordInfo} paginationInfo - 레코드 순번 계산에 필요한 정보 객체.
     * @param {number} recordNoPerPage - 현재 페이지 목록 내에서의 레코드 순번 (1부터 시작).
     * @param {'ASC'|'DESC'} [order='ASC'] - 정렬 순서. 'ASC' (오름차순) 또는 'DESC' (내림차순).
     * @returns {number} - 전체 목록을 기준으로 계산된 레코드의 실제 순번.
     */
    paginationRecordNo(paginationInfo, recordNoPerPage, order) {
        let resultRecordNo = 0;
        if (paginationInfo) {
            const totalRecordCount = paginationInfo['totalRecordCount'];
            const pageUnit = paginationInfo.pageUnit;
            const pageNo = paginationInfo.pageNo;
            let vOrder = (order || '').toUpperCase();

            if (vOrder !== 'ASC' && vOrder !== 'DESC') {
                vOrder = 'ASC';
            }

            if (vOrder === 'DESC') {
                resultRecordNo = totalRecordCount + 1 - ((pageNo - 1) * pageUnit + recordNoPerPage);
            } else {
                resultRecordNo = (pageNo - 1) * pageUnit + recordNoPerPage;
            }
        }
        return resultRecordNo;
    },
    /**
     * 테이블의 헤더에서 정렬 조건을 가져온다.
     * (테이블 헤더(`<th>`) 요소의 시각적 정렬 아이콘(fa-sort-up/fa-sort-down) 상태를 분석하여, 서버 전송용 ORDER BY 쿼리 문자열을 생성한다)
     *
     * @param {string} selector - 정렬 정보를 담고 있는 테이블의 컨테이너를 가리키는 CSS 선택자 (예: '#listTable').
     * @param {string} [defaultOrderBy] - 정렬 조건이 없을 경우 사용할 기본 ORDER BY 문자열 (예: 'column_name ASC').
     * @returns {string} - 'column_name ASC, another_column DESC' 형태의 ORDER BY 문자열.
     */
    getThOrderBy(selector, defaultOrderBy) {
        let orderBy = '';

        document.querySelectorAll(`${selector} [sort-nm]`).forEach((header) => {
            const sortNm = header.getAttribute('sort-nm');
            const sortIcon = header.querySelector('.fas.fa-sort');

            if (sortIcon) {
                let direction = '';

                if (sortIcon.classList.contains('fa-sort-up')) {
                    direction = 'ASC';
                } else if (sortIcon.classList.contains('fa-sort-down')) {
                    direction = 'DESC';
                }

                if (sortNm && direction) {
                    if (orderBy) {
                        orderBy += ', ';
                    }
                    orderBy += `${sortNm} ${direction}`;
                }
            }
        });

        if (!orderBy && defaultOrderBy) {
            orderBy = defaultOrderBy;
        }

        return orderBy;
    },
    /**
     * 주어진 문자열에서 숫자만 추출하여 지정된 구분 기호로 날짜 형식(YYYY-MM-DD)을 만든다.
     *
     * @param {string|number} dateStr - 포매팅할 날짜 문자열 또는 숫자 (예: '20251025', '2025/10/25').
     * @param {string} [pDelimiter='-'] - 연, 월, 일을 구분할 기호. (선택적, 기본값: '-')
     * @returns {string} - 포맷된 날짜 문자열 (예: '2025-10-25') 또는 유효하지 않을 경우 빈 문자열.
     */
    dateFormat(dateStr, pDelimiter) {
        if (dateStr) {
            dateStr = String(dateStr).replace(/[^0-9]/g, '');
            const delimiter = pDelimiter ? pDelimiter : '-';

            if (dateStr.length >= 8) {
                dateStr = dateStr.substring(0, 8);
                const errMsg = S2Util.validateDate(dateStr);

                if (!errMsg) {
                    dateStr = dateStr.replace(/(\d{4})(\d{2})(\d{2})/, `$1${delimiter}$2${delimiter}$3`);
                } else {
                    dateStr = '';
                    if (document.getElementById('dialog-confirm') === null) {
                        try {
                            S2Util.alert(errMsg);
                        } catch {
                            alert(errMsg);
                        }
                    }
                }
            } else if (dateStr.length > 6) {
                dateStr = dateStr.substring(0, 6).replace(/(\d{4})(\d{2})/, `$1${delimiter}$2`);
            } else if (dateStr.length > 4) {
                dateStr = dateStr.substring(0, 4);
            }
        }
        return dateStr;
    },
    /**
     * 주어진 키와 값 쌍을 로컬 스토리지(localStorage)에 저장한다.
     * (로컬 스토리지 접근이 불가능하거나 실패할 경우 (예: 용량 초과, 사생활 보호 모드), 해당 데이터를 30일 만료 기한의 쿠키로 대체 저장한다.)
     *
     * @param {string} key - 저장할 데이터의 키.
     * @param {string|number|boolean} value - 저장할 데이터 값.
     * @returns {void}
     */
    setLocalStorage(key, value) {
        try {
            localStorage.setItem(key, value);
        } catch {
            const d = new Date();
            d.setTime(d.getTime() + 30 * 24 * 60 * 60 * 1000);
            const expires = `expires=${d.toUTCString()}`;

            document.cookie = `${key}=${encodeURIComponent(value)};${expires};path=/;`;
        }
    },
    /**
     * 주어진 키에 해당하는 값을 로컬 스토리지(localStorage)에서 조회한다.
     * - localStorage 조회에 실패하면 (예: 접근 불가, 사생활 보호 모드), 쿠키에서 대체 조회한다.
     * - 쿠키에서 조회된 값은 decodeURIComponent()를 사용하여 안전하게 디코딩된다.
     *
     * @param {string} key - 조회할 데이터의 키.
     * @returns {string|null} - 조회된 데이터 문자열. 키가 없거나 스토리지에 접근할 수 없으면 빈 문자열 또는 null이 반환될 수 있습니다. (localStorage는 키가 없을 때 null 반환)
     */
    getLocalStorage(key) {
        let value = '';
        try {
            value = localStorage.getItem(key);
        } catch {
            const cookie = `; ${document.cookie}`;
            const parts = cookie.split(`; ${key}=`);
            if (parts.length === 2) value = parts.pop().split(';').shift();
        }
        return value;
    },
    /**
     * 주어진 키에 해당하는 데이터를 로컬 스토리지(localStorage)에서 제거한다.
     * (localStorage 접근이 불가능하거나 실패할 경우, 해당 키의 쿠키를 만료시켜 제거한다.)
     *
     * @param {string} key - 제거할 데이터의 키.
     * @returns {void}
     */
    removeLocalStorage(key) {
        try {
            localStorage.removeItem(key);
        } catch {
            const d = new Date();
            d.setTime(d.getTime() - 1);
            const expires = `expires=${d.toUTCString()}`;

            document.cookie = `${key}=;${expires};path=/;`;
        }
    },
    /**
     * 지정된 DOM 요소 내에 's2ActForm' 이름의 숨겨진 폼(form)을 생성하거나 재설정하고 액션 전송을 준비한다.
     * 매개변수 객체(param)의 속성들과 CSRF 토큰을 hidden 필드로 폼에 추가한다.
     *
     * @param {object} target - 폼이 생성될 대상 DOM 객체 (예: window.document, iframe의 document).
     * @param {object} [param] - 폼에 hidden 필드로 추가될 매개변수 객체. method 속성으로 form의 method를 지정할 수 있다.
     * @returns {HTMLFormElement} - 생성되거나 재설정된 폼(form) 요소.
     */
    prepareActionForm(target, param) {
        if (target['s2ActForm']) {
            // 기존에 폼이 존재한다면 삭제
            const delForm = target.getElementsByName('s2ActForm')[0];
            delForm.parentNode.removeChild(delForm);
        }

        // 새로운 폼 생성
        const actionFormElement = target.createElement('form');
        actionFormElement.setAttribute('method', param && param.method ? param.method : 'post');
        actionFormElement.setAttribute('name', 's2ActForm');
        actionFormElement.style.display = 'none';
        target.body.appendChild(actionFormElement);

        // 매개변수 설정
        if (param && typeof param === 'object') {
            for (const paramKey in param) {
                const paramVal = param[paramKey];

                if (paramVal) {
                    if (!actionFormElement[paramKey]) {
                        const s2Field = target.createElement('input');
                        s2Field.setAttribute('type', 'hidden');
                        s2Field.setAttribute('name', paramKey);
                        actionFormElement.appendChild(s2Field);
                    }

                    actionFormElement[paramKey].value = paramVal;
                }
            }
        }

        {
            // CSRF TOKEN 설정
            const csrfKey = S2Util.getLocalStorage('s2CsrfTokenKey');
            const csrfValue = S2Util.getLocalStorage('s2CsrfTokenValue');

            if (csrfKey && csrfValue) {
                if (!actionFormElement[csrfKey]) {
                    const s2Field = target.createElement('input');
                    s2Field.setAttribute('type', 'hidden');
                    s2Field.setAttribute('name', csrfKey);
                    actionFormElement.appendChild(s2Field);
                }

                actionFormElement[csrfKey].value = csrfValue;
            }
        }

        return actionFormElement;
    },
    /**
     * CSRF 토큰과 매개변수를 포함하는 숨겨진 폼을 준비하고, 해당 폼을 POST 방식으로 전송하여 페이지 이동을 수행한다.
     * prepareActionForm에서 반환된 폼 요소를 사용해 action URL을 설정하고 submit 한다.
     *
     * @param {string} url - 이동할 대상 URL. URL에 포함된 커스텀 인코딩 문자열(!q, !n, !e)을 표준 URL 문자열(?, &, =)로 복원하여 적용한다.
     * @param {object} [param] - 폼에 hidden 필드로 추가될 매개변수 객체.
     * @param {Window} [pTarget=window] - 폼 전송이 일어날 대상 window 객체 (예: iframe의 window 객체). 생략 시 현재 window를 대상으로 한다.
     * @returns {void}
     */
    goPage(url, param, pTarget) {
        // CSRF 토큰 적용 페이지 이동
        const target = pTarget ? pTarget.document : document;

        if (url) {
            const actionFormElement = S2Util.prepareActionForm(target, param);

            actionFormElement.action = url.replace(/!q/g, '?').replace(/!n/g, '&').replace(/!e/g, '=');
            actionFormElement.submit();
            try {
                target.focus();
            } catch (e) {
                if (window.console) {
                    console.error(e);
                }
            }
        }
    },
    /**
     * 주어진 문자열이 유효한 날짜 형식(YYYYMMDD 또는 YYYYMM)인지 확인한다.
     * 문자열에서 숫자만 추출하여 검사하며, 유효하지 않을 경우 오류 메시지 문자열을 반환한다.
     * 유효성 검사에는 연도의 유효 범위(현재로부터 100년 전까지)와 월/일의 범위 검사가 포함된다.
     *
     * @param {string|number} dateStr - 유효성을 검사할 날짜 문자열 또는 숫자 (예: '20251025', '2025/10/25').
     * @returns {string} - 유효한 경우 빈 문자열(`''`), 유효하지 않을 경우 오류 메시지 문자열.
     *
     * @requires dayjs - 연도 범위 검사를 위해 dayjs 라이브러리에 의존한다.
     */
    validateDate(dateStr) {
        let errMsg = '';

        try {
            if (dateStr) {
                dateStr = String(dateStr).replace(/[^0-9]/g, '');
                let year;
                let month;
                let day;

                switch (dateStr.length) {
                    case 8:
                        year = parseInt(dateStr.substring(0, 4));
                        month = parseInt(dateStr.substring(4, 6));
                        day = parseInt(dateStr.substring(6, 8));

                        if (year < dayjs().subtract(100, 'years').format('YYYY') || month < 1 || month > 12) {
                            errMsg = '날짜형식이 올바르지 않습니다.';
                        } else {
                            const date = new Date(year, month, 0); // 다음달의 0일 (이달의 마지막일);
                            const lastDay = date.getDate();

                            if (day < 1 || day > lastDay) {
                                errMsg = '잘못된 날짜입니다.';
                            }
                        }
                        break;
                    case 6:
                        year = parseInt(dateStr.substring(0, 4));
                        month = parseInt(dateStr.substring(4, 6));

                        if (year < dayjs().subtract(100, 'years').format('YYYY') || month < 1 || month > 12) {
                            errMsg = '날짜형식이 올바르지 않습니다.';
                        }
                        break;
                    default:
                        errMsg = '날짜형식이 올바르지 않습니다.';
                        break;
                }
            }
        } catch {
            errMsg = '날짜형식이 올바르지 않습니다.';
        }

        return errMsg;
    },
    /**
     * 주어진 폼(form) 요소의 모든 입력 필드에 설정된 HTML 속성(required, length, dataType, mask 등)을 기반으로 유효성 검사를 수행한다.
     * 유효성 검사에서 문제가 발견되면 false를 반환하고, 오류 메시지 배열을 S2Util.alert를 통해 사용자에게 보여준다.
     *
     * @param {string|HTMLFormElement} form - 검사할 폼을 나타내는 CSS 선택자 문자열 또는 실제 form DOM 요소.
     * @returns {boolean} - 폼의 모든 필드가 유효하면 true, 하나라도 유효하지 않으면 false.
     *
     * @requires S2Util.setFormErrorMessage
     * @requires S2Util.validateDate
     * @requires S2Util.alert
     *
     * @example
     * S2Util.validate('#myForm'); // myForm ID를 가진 폼 검사
     * S2Util.validate(document.getElementById('myForm')); // 폼 요소 직접 전달
     */
    validate(form) {
        // Form element 가져오기
        const formElement = typeof form === 'string' ? document.querySelector(form) : form;
        let valid = true;
        const messageArr = [];

        if (formElement) {
            const errMsaSuf1 = window.g_errMsaSuf1 || '은(는) 필수 항목 입니다.';
            const errMsaSuf2 = window.g_errMsaSuf2 || '은(는) 유효하지 않은 값입니다.';

            formElement.querySelectorAll(`[data-form-error]`).forEach((element) => {
                element.textContent = '';
            });

            // 모든 input, select, textarea 요소 선택
            const elements = formElement.querySelectorAll('input, select, textarea');

            elements.forEach((element) => {
                // no-validate="true" 상위 요소 확인
                if (element.closest('[no-validate="true"]')) {
                    return;
                }

                const checkList = [];

                if (element.hasAttribute('required')) checkList.push('required');
                if (element.hasAttribute('length')) checkList.push('length');
                if (element.hasAttribute('maxlength')) checkList.push('maxlength');
                if (element.hasAttribute('minlength')) checkList.push('minlength');
                if (element.hasAttribute('minValue')) checkList.push('minValue');
                if (element.hasAttribute('mask')) checkList.push('mask');
                if (element.hasAttribute('dataType')) checkList.push('dataType');
                if (element.hasAttribute('group')) checkList.push('group');

                if (checkList.length > 0) {
                    for (const check of checkList) {
                        const type = element.type;
                        const datepickerElement = formElement.querySelector(`.datepicker[name="${element.getAttribute('name')}_text"]`);
                        const title = element.getAttribute('title') || (datepickerElement && datepickerElement.getAttribute('title'));
                        let checkValid = true;
                        let checkMessage = '';

                        switch (check) {
                            case 'required':
                                if (type === 'checkbox') {
                                    const checkedBoxes = formElement.querySelectorAll(`[type=checkbox][name="${element.name}"]:checked`);
                                    if (checkedBoxes.length === 0) {
                                        checkValid = false;
                                    }
                                } else if (type === 'radio') {
                                    const checkedRadios = formElement.querySelectorAll(`[type=radio][name="${element.name}"]:checked`);
                                    if (checkedRadios.length === 0) {
                                        checkValid = false;
                                    }
                                } else if (!element.value.trim()) {
                                    checkValid = false;
                                }

                                if (!checkValid) {
                                    valid = false;
                                    checkMessage = `[${title}]${errMsaSuf1}`;
                                    S2Util.setFormErrorMessage(checkMessage, formElement.querySelector(`[data-form-error="${element.name}"]`), messageArr);
                                }
                                break;

                            case 'length':
                            case 'maxlength':
                            case 'minlength': {
                                const length = Number(element.getAttribute(check).replace(/[^0-9]/g, ''));
                                if (length) {
                                    if (type === 'checkbox') {
                                        const checkedCount = formElement.querySelectorAll(`[type=checkbox][name="${element.name}"]:checked`).length;
                                        if (check === 'length' && length !== checkedCount) {
                                            checkValid = false;
                                            checkMessage = `[${title}]은(는) ${length}개를 선택해야 합니다.`;
                                        } else if (check === 'maxlength' && length < checkedCount) {
                                            checkValid = false;
                                            checkMessage = `[${title}]은(는) ${length}개를 초과할 수 없습니다.`;
                                        } else if (check === 'minlength' && length > checkedCount) {
                                            checkValid = false;
                                            checkMessage = `[${title}]은(는) ${length}개 미만일 수 없습니다.`;
                                        }
                                    } else {
                                        const valueLength = element.value.trim().length;
                                        if (check === 'length' && length !== valueLength) {
                                            checkValid = false;
                                            checkMessage = `[${title}]은(는) ${length}자리를 입력해야 합니다.`;
                                        } else if (check === 'maxlength' && length < valueLength) {
                                            checkValid = false;
                                            checkMessage = `[${title}]은(는) ${length}자리를 초과할 수 없습니다.`;
                                        } else if (check === 'minlength' && length > valueLength) {
                                            checkValid = false;
                                            checkMessage = `[${title}]은(는) ${length}자리 미만일 수 없습니다.`;
                                        }
                                    }

                                    if (!checkValid) {
                                        valid = false;
                                        S2Util.setFormErrorMessage(checkMessage, formElement.querySelector(`[data-form-error="${element.name}"]`), messageArr);
                                    }
                                }
                                break;
                            }

                            case 'minValue': {
                                const minValue = Number(element.getAttribute(check).replace(/[^0-9]/g, ''));
                                if (minValue) {
                                    let value = '$undefined';

                                    switch (type) {
                                        case 'checkbox':
                                            break;
                                        case 'radio':
                                            value = formElement.find(`[type=radio][name="${element.name}"]:checked`).val();
                                            break;
                                        default:
                                            value = element.value;
                                            break;
                                    }

                                    if (value !== '$undefined' && minValue > value) {
                                        valid = false;
                                        checkMessage = `[${title}]은(는) ${minValue}보다 적을수 없습니다.`;
                                        S2Util.setFormErrorMessage(checkMessage, formElement.querySelector(`[data-form-error="${element.name}"]`), messageArr);
                                    }
                                }
                                break;
                            }
                            case 'mask': {
                                const value = element.value;
                                let mask = element.getAttribute(check);

                                switch (mask) {
                                    case 'CHK_NUMBER':
                                        mask = /^[0-9]*$/;
                                        break;
                                    case 'CHK_TEXT_INTACT':
                                        // 텍스트(숫자/특문 제외)
                                        mask = /^[가-힣a-zA-Z]*$/;
                                        break;
                                    case 'CHK_TEXT_COMBINE':
                                        // 특수문자 제외 텍스트
                                        // eslint-disable-next-line no-useless-escape
                                        mask = /^[가-힣a-zA-Z0-9\s\-\(\)\,\.]*$/;
                                        break;
                                    case 'CHK_MPHONE_NO':
                                        // 휴대전화번호
                                        mask = /^01([0|1|6|7|8|9]?)[-]([0-9]{3,4})[-]([0-9]{4})$/;
                                        break;
                                    case 'CHK_TEL_NO':
                                        // 전화번호
                                        mask = /^([0-9]{2,4})[-]([0-9]{3,4})[-]([0-9]{3,4})$/;
                                        break;
                                    case 'CHK_FAX_NO':
                                        // 팩스번호
                                        mask = /^([0-9])*[-]([0-9])*[-]([0-9])*$/;
                                        break;
                                    case 'CHK_LOGIN_ID':
                                        // 로그인 ID
                                        mask = /^[a-zA-Z0-9]*$/;
                                        break;
                                    case 'CHK_PASSWORD':
                                        // 비밀번호
                                        mask = /^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-zA-Z]).{9,16}$/;
                                        break;
                                    case 'CHK_PASSWORD_ANSWR':
                                        // 비밀번호 찾기 질문 답변
                                        mask = /^[가-힣a-zA-Z0-9\s]*$/;
                                        break;
                                    case 'CHK_EMAIL':
                                        // 이메일
                                        // eslint-disable-next-line no-useless-escape
                                        mask = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
                                        break;
                                }

                                if (mask) {
                                    if (!mask.test(value)) {
                                        valid = false;
                                        checkMessage = `[${title}]${errMsaSuf2}`;
                                        S2Util.setFormErrorMessage(checkMessage, formElement.querySelector(`[data-form-error="${element.name}"]`), messageArr);
                                    }
                                }
                                break;
                            }
                            case 'dataType': {
                                const dataTypeInfo = String(element.getAttribute(check)).split(':');
                                const dataType = dataTypeInfo[0];

                                switch (dataType) {
                                    case 'date':
                                        switch (type) {
                                            case 'checkbox':
                                                break;
                                            case 'radio':
                                                break;
                                            default: {
                                                const startDate = element.value;
                                                const errMsg = S2Util.validateDate(startDate);
                                                if (errMsg) {
                                                    checkValid = false;
                                                    checkMessage = `[${title}]은(는) ${errMsg}`;
                                                } else {
                                                    if (dataTypeInfo[1] && dataTypeInfo[1].indexOf('start') === 0) {
                                                        const $endDateForm = formElement.find(`[dataType="date:end${dataTypeInfo[1].replace('start', '')}"]`);
                                                        if ($endDateForm.length > 0) {
                                                            const endDate = $endDateForm.val();
                                                            if (startDate && endDate && S2Util.validateDate(endDate) && startDate > endDate) {
                                                                checkValid = false;
                                                                checkMessage = `[${$endDateForm.attr('title')}]은(는) [${title}]보다 빠를수 없습니다.`;
                                                            }
                                                        }
                                                    }
                                                }

                                                if (!checkValid) {
                                                    valid = false;
                                                    S2Util.setFormErrorMessage(checkMessage, formElement.querySelector(`[data-form-error="${element.name}"]`), messageArr);
                                                }
                                                break;
                                            }
                                        }
                                        break;
                                }
                                break;
                            }
                            case 'group': {
                                let chkVal = element.value;
                                let chkFlag = true;
                                const groupNmList = String(element.getAttribute(check)).split(',');
                                let emptyGroupNm = '';

                                if (!chkVal) {
                                    chkFlag = false;
                                    emptyGroupNm = element.getAttribute('name');
                                }

                                for (const in_idx in groupNmList) {
                                    const groupNm = groupNmList[in_idx];
                                    const groupVal = formElement.find(`[name="${groupNm}"]`).val();
                                    if (groupVal) {
                                        chkVal += groupVal;
                                    } else if (!emptyGroupNm) {
                                        chkFlag = false;
                                        emptyGroupNm = groupNm;
                                    }
                                }

                                if (chkVal && !chkFlag) {
                                    valid = false;
                                    checkMessage = `[${formElement.find(`[name="${emptyGroupNm}"]`).attr('title')}] 항목을 입력해주세요.`;
                                    S2Util.setFormErrorMessage(checkMessage, formElement.querySelector(`[data-form-error="${element.name}"]`), messageArr);
                                }
                                break;
                            }
                        }
                    }
                }
            });
        }

        if (!valid && messageArr.length) {
            S2Util.alert(messageArr.join('<br/>'), null, {size: 'default'});
        }

        return valid;
    },
    /**
     * 유효성 검사 오류 메시지를 처리한다.
     * 오류 메시지 출력 영역(errArea)이 있으면 그곳에 메시지를 직접 표시하고,
     * 없으면 오류 메시지를 배열(messageArr)에 추가하여 나중에 일괄 처리할 수 있도록 준비한다.
     *
     * @param {string} errMessage - 폼 유효성 검사 중 발생한 오류 메시지 문자열.
     * @param {HTMLElement} [errArea] - 오류 메시지를 직접 표시할 DOM 요소 (예: <span data-form-error="field_name"></span>).
     * @param {string[]} messageArr - 모든 오류 메시지를 수집할 배열.
     * @returns {void}
     *
     * @example
     * 1. 특정 영역에 오류 메시지 표시
     * const errorSpan = document.querySelector('[data-form-error="name"]');
     * S2Util.setFormErrorMessage('[이름]은(는) 필수 항목 입니다.', errorSpan, []);
     *
     * 2. 오류 메시지 배열에 추가 (errArea가 null이거나 없는 경우)
     * const allErrors = [];
     * S2Util.setFormErrorMessage('[제목]은(는) 필수 항목 입니다.', null, allErrors);
     * allErrors -> ['[제목]은(는) 필수 항목 입니다.']
     */
    setFormErrorMessage(errMessage, errArea, messageArr) {
        if (errMessage && Array.isArray(messageArr)) {
            if (errArea) {
                errArea.textContent = errMessage;
            } else {
                messageArr.push(errMessage);
            }
        }
    },
    /**
     * 주어진 데이터의 정확한 자바스크립트 데이터 유형을 추론하여 반환한다.
     * 원시 타입 외에도 jQuery 객체, DOM 요소, JSON 객체/문자열 등 복합적인 타입을 상세하게 구분한다.
     *
     * @param {*} data - 유형을 확인할 데이터.
     * @returns {{type: string, detailType: string}} - 데이터의 주 유형(type)과 상세 유형(detailType)을 담은 객체.
     * * @example
     * S2Util.getDataType({a:1});        // {type: 'json', detailType: 'object'}
     * S2Util.getDataType('{"a":1}');    // {type: 'json', detailType: 'string'}
     * S2Util.getDataType(document.body);// {type: 'object', detailType: ''} (일반 DOM 요소는 object로 간주)
     * S2Util.getDataType(function(){}); // {type: 'function', detailType: ''}
     */
    s2DataType(data) {
        let type = '';
        let detailType = '';

        if (data != null) {
            try {
                // Object.prototype.toString.call 로 클래스명을 확인할 때 [object Function]으로 구분하므로 먼저 확인한다.
                if (data instanceof jQuery) {
                    type = 'jquery';
                }
            } catch {
                // jQuery 객체가 없다면 오류가 발생하여 확인할 수 없음
                type = '';
            }

            if (!type) {
                try {
                    const className = Object.prototype.toString.call(data);
                    switch (className) {
                        case '[object Undefined]':
                            type = 'undefined';
                            break;
                        case '[object Null]':
                            type = 'null';
                            break;
                        case '[object Window]':
                            type = 'window';
                            break;
                        case '[object HTMLDocument]':
                            type = 'document';
                            break;
                        case '[object HTMLFormElement]':
                            type = 'form';
                            break;
                        case '[object FormData]':
                            type = 'formData';
                            break;
                        case '[object Function]':
                            type = 'function';
                            break;
                        case '[object Array]':
                            type = 'array';
                            break;
                        case '[object Boolean]':
                            type = 'boolean';
                            break;
                        case '[object Number]':
                            type = 'number';
                            break;
                        case '[object Object]':
                        case '[object String]':
                            detailType = typeof data;

                            try {
                                let chkData = null;

                                if (detailType === 'object') {
                                    // JSON 객체로 추정하며 JSON.parse 까지 성공하면 JSON 객체로 확정 {type: 'json', detailType: 'object'}
                                    chkData = JSON.stringify(data);
                                } else if (detailType === 'string') {
                                    // JSON 문자열로 추정하며 JSON.parse 까지 성공하면 JSON 문자열로 확정 {type: 'json', detailType: 'string'}
                                    chkData = data;
                                }

                                if (chkData) {
                                    // JSON.parse 가 성공하면 JSON 타입이다.
                                    JSON.parse(chkData);
                                    type = 'json';
                                }
                            } catch {
                                // JSON.parse 가 실패하면 detailType 이(typeof 값) type 이 되고 detailType 은 초기화 한다.
                                type = detailType;
                                detailType = '';
                            }
                            break;
                    }
                } catch {
                    type = '';
                }
            }
        }

        return {
            type: type ? type : typeof data,
            detailType: type ? detailType : ''
        };
    },
    /**
     * 주어진 매개변수가 HTML 폼 데이터(FormData) 객체인지 확인한다.
     *
     * @param {*} param - 검사할 대상 매개변수.
     * @returns {boolean} - 매개변수가 FormData 객체이면 true, 아니면 false.
     *
     * @example
     * const data = new FormData();
     * S2Util.isFormData(data); // true 반환
     * S2Util.isFormData({});   // false 반환
     */
    isFormData(param) {
        return param instanceof FormData;
    },
    /**
     * 주어진 HTML 폼(form) 요소의 모든 입력 필드 값을 읽어 JSON 객체로 직렬화하여 반환한다.
     * 폼이 유효한 DOM Form Element인 경우에만 작동하며, 내부적으로 FormData와 S2Util.formDataToJson을 사용한다.
     *
     * @param {HTMLFormElement} form - 직렬화할 대상 폼 DOM 요소.
     * @returns {object} - 폼 필드들이 키-값 쌍으로 포함된 JSON 객체. 폼이 유효하지 않으면 빈 객체({})를 반환한다.
     *
     * @requires S2Util.formDataToJson
     *
     * @example
     * <form id="dataForm"><input name="id" value="user123"></form>
     * const formElement = document.getElementById('dataForm');
     * S2Util.serializeFormToJson(formElement); // {id: 'user123'} 반환
     */
    serializeFormToJson(form) {
        if (form && form.nodeName === 'FORM') {
            return S2Util.formDataToJson(new FormData(form));
        }
        return {};
    },
    /**
     * FormData 객체(폼 데이터를 key/value 쌍으로 가진 객체)를 일반 JSON 객체로 변환한다.
     * 동일한 key(name)을 가진 필드가 여러 개 있을 경우 (예: 체크박스), 해당 key의 value를 배열로 묶어 처리한다.
     *
     * @param {FormData} formData - 변환할 대상 FormData 객체.
     * @returns {object} - FormData의 내용이 변환된 JSON 객체. 유효한 FormData가 아니면 빈 객체({})를 반환한다.
     *
     * @requires S2Util.isFormData
     *
     * @example
     * FormData에 {id: 'u1', role: 'admin', role: 'user'}가 있다면
     * const json = S2Util.formDataToJson(formData);
     * {id: 'u1', role: ['admin', 'user']} 반환
     */
    formDataToJson(formData) {
        const json = {};
        if (S2Util.isFormData(formData)) {
            formData.forEach((value, key) => {
                if (json[key]) {
                    if (!Array.isArray(json[key])) json[key] = [json[key]];
                    json[key].push(value);
                } else {
                    json[key] = value;
                }
            });
        }
        return json;
    },
    /**
     * 주어진 JSON 객체 또는 JSON 문자열을 FormData 객체로 변환한다.
     * JSON 값이 배열일 경우, FormData에 동일한 키(name)로 값을 반복하여 추가함으로써 멀티 파트 데이터 형식에 대응한다.
     *
     * @param {object|string} json - FormData로 변환할 대상 JSON 객체 또는 JSON 문자열.
     * @returns {FormData} - 변환된 FormData 객체. 유효한 JSON이 아니거나 변환에 실패하면 빈 FormData 객체를 반환한다.
     *
     * @requires S2Util.isJSON
     *
     * @example
     * JSON 객체 예시
     * const json = {id: 'user1', roles: ['admin', 'user']};
     * const formData = S2Util.jsonToFormData(json);
     * formData는 'id=user1', 'roles=admin', 'roles=user'를 포함한다.
     */
    jsonToFormData(json) {
        const formData = new FormData();
        let parsedJson = json;
        if (S2Util.isJSON(json)) {
            if (typeof json === 'string') {
                parsedJson = JSON.parse(json);
            }

            for (const [key, value] of Object.entries(parsedJson)) {
                if (Array.isArray(value)) {
                    value.forEach((item) => formData.append(key, item));
                } else {
                    formData.append(key, value);
                }
            }
        }
        return formData;
    },
    /**
     * 주어진 문자열이 유효한 쿼리 문자열 형식(예: 'a=1&b=2' 또는 '?a=1&b=2')인지 확인한다.
     * 내부적으로 URLSearchParams를 사용하여 실제 쿼리 매개변수로 파싱될 수 있는지 검증한다.
     *
     * @param {string} param - 검사할 대상 문자열.
     * @returns {boolean} - 유효한 쿼리 매개변수가 포함되어 있으면 true, 아니면 false.
     *
     * @example
     * S2Util.isQueryString('a=1&b=2');      // true 반환
     * S2Util.isQueryString('?name=gemini'); // true 반환
     * S2Util.isQueryString('a&b=');         // (파싱 결과에 따라) true 반환
     * S2Util.isQueryString('invalid string'); // false 반환 (에러 발생 혹은 파싱 결과 없음)
     */
    isQueryString(param) {
        try {
            const query = param.startsWith('?') ? param : `?${param}`;
            const params = new URLSearchParams(query);
            return params.toString().length > 0;
        } catch {
            return false;
        }
    },
    /**
     * 주어진 쿼리 문자열에서 특정 키(key)에 해당하는 값을 가져온다.
     * 쿼리 문자열은 '?'로 시작해도 되고 생략되어도 상관없다.
     * 값이 없을 경우 빈 문자열('')을, 값이 하나일 경우 해당 문자열을, 값이 두 개 이상일 경우 문자열 배열을 반환한다.
     *
     * @param {string} queryString - 파싱할 쿼리 문자열 (예: 'a=1&b=2' 또는 '?a=1&b=2').
     * @param {string} key - 가져오려는 값의 키 이름.
     * @returns {string|string[]|string} - 키에 해당하는 값. (값이 없으면 빈 문자열, 하나면 문자열, 여러 개면 배열)
     *
     * @example
     * S2Util.getQueryStringParameter('a=1&b=2', 'a');      // '1' 반환
     * S2Util.getQueryStringParameter('a=1&a=2&c=3', 'a');  // ['1', '2'] 반환
     * S2Util.getQueryStringParameter('a=1', 'b');          // '' 반환
     */
    getQueryStringParameter(queryString, key) {
        if (!queryString || !key) {
            return queryString;
        }

        const cleanQuery = queryString.startsWith('?') ? queryString.slice(1) : queryString;
        const params = new URLSearchParams(cleanQuery);
        const values = params.getAll(key); // 모든 값 배열로 가져오기

        if (values.length === 0) return ''; // 값 없으면 빈 문자열
        if (values.length === 1) return values[0]; // 값이 하나면 문자열 반환
        return values; // 값이 두 개 이상이면 배열 반환
    },
    /**
     * 주어진 쿼리 문자열에서 특정 키(key)와 그에 해당하는 모든 값을 제거한다.
     * 쿼리 문자열의 시작에 '?'가 있어도 상관없다. 제거 후에는 '?' 없이 순수한 쿼리 문자열만 반환한다.
     *
     * @param {string} queryString - 매개변수를 제거할 원본 쿼리 문자열 (예: 'a=1&b=2&a=3').
     * @param {string} key - 제거하려는 대상 매개변수의 키 이름.
     * @returns {string} - 키가 제거된 새로운 쿼리 문자열. (예: 'b=2')
     *
     * @example
     * S2Util.removeQueryStringParameter('?page=1&id=10&page=2', 'page'); // 'id=10' 반환
     * S2Util.removeQueryStringParameter('a=1&b=2', 'c'); // 'a=1&b=2' 반환
     */
    removeQueryStringParameter(queryString, key) {
        if (!queryString) {
            return queryString;
        }

        const params = new URLSearchParams(queryString.startsWith('?') ? queryString.slice(1) : queryString);
        params.delete(key);
        return params.toString();
    },
    /**
     * JSON 객체의 내용을 쿼리 문자열로 변환한다.
     * 선택적으로 기존 쿼리 문자열(또는 URL)과 병합할 수 있으며,
     * JSON에 동일한 키가 있으면 기존 쿼리 문자열의 값을 덮어쓴다.
     *
     * @param {object|string} paramJSON - 쿼리 문자열로 변환할 JSON 객체 또는 JSON 문자열.
     * @param {string} [paramQueryString] - 기존 URL, 기존 쿼리 문자열 또는 이 둘을 포함하는 문자열.
     * @returns {string} - 병합/변환된 새로운 URL 또는 쿼리 문자열.
     *
     * @requires S2Util.s2DataType
     *
     * @example
     * S2Util.jsonToQueryString({a: 1, b: 2}); // 'a=1&b=2'
     * S2Util.jsonToQueryString({a: 1, b: 2}, 'test.do'); // 'test.do?a=1&b=2'
     * S2Util.jsonToQueryString({a: 1, b: 2, x: 'z'}, 'test.do?wrong&x=y'); // 'test.do?wrong=&x=z&a=1&b=2'
     */
    jsonToQueryString(paramJSON, paramQueryString) {
        let vUrl = '';
        let vQueryString = '';
        let vJSONObject = {};

        if (paramQueryString) {
            const qIdx = paramQueryString.indexOf('?');
            if (qIdx > -1) {
                // 기존 문자열에 ?가 포함되어 있다면 ?를 포함한 앞부분을 url 로 처리한다.
                vUrl = paramQueryString.substring(0, qIdx);

                const vCheckString = paramQueryString.substring(qIdx + 1);
                if (vCheckString) {
                    if (vCheckString.indexOf('=') > -1) {
                        // 기존 문자열이 쿼리문자열이라면 새로 추가할 매개변수와 중복 키값을 확인하기 위한 변수에 담는다.
                        vQueryString = vCheckString;
                    } else {
                        // url 뒷부분의 문자열이 키와 값 쌍으로 되어 있지 않더라도 키와 값 쌍으로 처리하기 위해 JSON 객체에 추가한다.
                        vJSONObject[vCheckString] = '';
                    }
                }
            } else if (paramQueryString.indexOf('=') > -1) {
                // 기존 문자열이 쿼리문자열이라면 새로 추가할 매개변수와 중복 키값을 확인하기 위한 변수에 담는다.
                vQueryString = paramQueryString;
            } else {
                // 기존 문자열이 쿼리문자열이 아니고 앞서 url 이 없었다면 url 로 처리한다.
                vUrl = paramQueryString;
            }
        }

        if (vQueryString) {
            // 기존 문자열에 쿼리문자열이 있다면 중복된 키값이 있을때 덮어쓰기 위해 JSON 객체로 변환한다.(이후 이때 생성한 JSON 객체에 새로 추가할 키와 값을 추가한다.)
            const params = vQueryString.split('&');
            if (params && Array.isArray(params)) {
                for (const idx in params) {
                    const param = params[idx].split('=');
                    const paramKey = param[0];
                    const paramValue = param.length === 2 ? param[1] : '';
                    const prevValue = vJSONObject[paramKey];
                    if (prevValue) {
                        if (!Array.isArray(prevValue)) {
                            vJSONObject[paramKey] = [prevValue];
                        }
                        vJSONObject[paramKey].push(paramValue);
                    } else {
                        vJSONObject[paramKey] = paramValue;
                    }
                }
            }
        }

        const paramJSONType = S2Util.s2DataType(paramJSON);
        if (paramJSONType.type === 'json') {
            const paramJSONObject = paramJSONType.detailType === 'string' ? JSON.parse(paramJSON) : paramJSON;

            if (Object.keys(vJSONObject).length > 0) {
                // 기존 쿼리문자열로 생성한 JSON 객체가 있다면 변환할 JSON 을 추가하여 둘사이에 중복된 키값을 제거한다.
                for (const key in paramJSONObject) {
                    const value = paramJSONObject[key];
                    if (value) {
                        vJSONObject[key] = value;
                    }
                }
            } else {
                vJSONObject = paramJSONObject;
            }
        }

        let resultQueryString = '';
        for (const key in vJSONObject) {
            const valueArr = Array.isArray(vJSONObject[key]) ? vJSONObject[key] : [vJSONObject[key]];
            for (const idx in valueArr) {
                const value = valueArr[idx];
                if (value) {
                    if (resultQueryString) {
                        resultQueryString += '&';
                    }
                    resultQueryString += `${key}=${value}`;
                }
            }
        }
        return vUrl + (vUrl && resultQueryString ? '?' : '') + resultQueryString;
    },
    /**
     * 쿼리 문자열(예: 'key1=value1&key2=value2')을 JSON 객체로 변환한다.
     * 쿼리 문자열의 시작에 '?'는 있어도 되고 없어도 된다.
     * 키에 값이 할당되지 않은 경우(예: 'key='), 해당 키의 값은 null로 처리된다.
     *
     * @param {string} paramQueryString - JSON 객체로 변환할 쿼리 문자열.
     * @returns {object} - 쿼리 문자열이 변환된 JSON 객체. 유효하지 않은 입력은 빈 객체({})를 반환한다.
     *
     * @example
     * S2Util.queryStringToJson('a=1&b=2');      // {a: '1', b: '2'} 반환
     * S2Util.queryStringToJson('?name=gemini&key='); // {name: 'gemini', key: null} 반환 (주의: 이 코드는 '='가 없는 'key'를 null로 처리하지 않음)
     */
    queryStringToJson(paramQueryString) {
        const result = {};
        if (paramQueryString && typeof paramQueryString == 'string') {
            const params = paramQueryString.split('&');
            for (const idx in params) {
                const param = params[idx].split('=');
                if (param && param.length > 0) {
                    result[param[0]] = param.length > 1 ? param[1] : null;
                }
            }
        }
        return result;
    },
    /**
     * 값 치환: 주어진 'value'가 null, undefined, 0, false, 빈 문자열('') 등의 falsy 값일 경우
     * 'defaultValue'로 치환하여 반환한다.
     *
     * @param {*} value - 치환할 대상 값.
     * @param {*} [defaultValue=''] - value가 falsy일 경우 반환할 기본값. 기본값은 빈 문자열('')이다.
     * @returns {*} - value가 truthy이면 value를, falsy이면 defaultValue를 반환한다.
     *
     * @example
     * S2Util.cast('hello', 'default'); // 'hello' 반환
     * S2Util.cast(null, 'default');    // 'default' 반환
     * S2Util.cast(0, 100);             // 100 반환 (0은 falsy)
     * S2Util.cast(undefined);          // '' 반환 (기본값)
     */
    cast(value, defaultValue = '') {
        return value ? value : defaultValue;
    },
    /**
     * 숨겨진 iframe을 사용하여 지정된 URL로 GET 요청을 보내 파일 다운로드를 트리거한다.
     * 이전에 생성된 'hiddenIframe'을 모두 제거한 후, 새로운 iframe을 생성하여 요청을 보낸다.
     *
     * @param {string} url - 파일을 다운로드할 서버의 URL.
     * @param {object} [param] - URL 쿼리 문자열로 변환하여 요청에 추가할 JSON 형태의 매개변수 객체.
     * @returns {void}
     *
     * @requires S2Util.jsonToQueryString
     *
     * @example
     * S2Util.downloadFile('/api/download', {fileId: 10, type: 'pdf'});
     */
    downloadFile(url, param) {
        const frames = document.getElementsByName('hiddenIframe');

        if (frames != null && frames.length > 0) {
            for (let i = 0; i < frames.length; i++) {
                frames[i].parentElement.removeChild(frames[i]);
            }
        }

        const iframe = document.createElement('iframe');
        iframe.setAttribute('src', S2Util.jsonToQueryString(param, url));
        iframe.setAttribute('name', 'hiddenIframe');
        iframe.setAttribute('width', '0px');
        iframe.setAttribute('height', '0px');

        document.body.appendChild(iframe);
    },
    /**
     * Base64로 인코딩된 문자열을 ArrayBuffer 형태의 바이너리 데이터로 변환한다.
     * 이 함수는 일반적으로 파일 다운로드 또는 Blob 생성 시 필요하다.
     *
     * @param {string} base64 - Base64 인코딩된 문자열. (예: 'SGVsbG8gV29ybGQ=')
     * @returns {ArrayBuffer} - 변환된 ArrayBuffer 바이너리 데이터.
     *
     * @example
     * const base64String = 'SGVsbG8gV29ybGQ='; // "Hello World"
     * const arrayBuffer = S2Util.base64ToArrayBuffer(base64String);
     * arrayBuffer는 이제 11바이트의 ArrayBuffer 객체가 된다.
     */
    base64ToArrayBuffer(base64) {
        const binaryString = atob(base64);
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
        }
        return bytes.buffer;
    },
    /**
     * Base64 인코딩된 문자열을 파일명과 파일 타입을 지정하여 File 객체로 변환한다.
     * File 객체는 주로 폼 데이터(FormData)를 통해 서버로 파일을 전송할 때 사용된다.
     *
     * @param {string} base64 - Base64 인코딩된 파일 데이터 문자열. (Data URL에서 헤더(data:type/subtype;base64,)를 제외한 부분)
     * @param {string} fileName - 생성될 File 객체의 파일명.
     * @param {string} fileType - 생성될 File 객체의 MIME 타입 (예: 'image/png', 'application/pdf').
     * @returns {File} - 생성된 File 객체.
     *
     * @requires S2Util.base64ToArrayBuffer
     *
     * @example
     * const base64Data = 'iVBORw0KGgoAAAANSUhEUgA...';
     * const file = S2Util.base64ToFile(base64Data, 'image.png', 'image/png');
     * file은 File 객체로, FormData에 추가하여 업로드할 수 있다.
     */
    base64ToFile(base64, fileName, fileType) {
        return new File([S2Util.base64ToArrayBuffer(base64)], fileName, {type: fileType});
    },
    /**
     * Base64 인코딩된 문자열을 File 객체를 거쳐 Blob 객체로 변환하고, 파일 이름과 Blob 객체를 포함하는 JSON 객체를 반환한다.
     * Blob 객체는 주로 이미지 미리보기 또는 파일 다운로드를 위해 클라이언트 측 메모리에서 바이너리 데이터를 처리할 때 사용된다.
     *
     * @param {string} base64 - Base64 인코딩된 파일 데이터 문자열.
     * @param {string} fileName - 생성될 Blob에 연관된 파일명.
     * @param {string} fileType - 생성될 Blob의 MIME 타입 (예: 'image/jpeg').
     * @returns {{name: string, blob: Blob}} - 파일 이름(name)과 Blob 객체(blob)를 포함하는 객체.
     *
     * @requires S2Util.base64ToFile
     *
     * @example
     * const base64 = 'iVBORw0KGgoAAAA...';
     * const result = S2Util.base64ToBlob(base64, 'myImage.jpg', 'image/jpeg');
     * // result는 {name: 'myImage.jpg', blob: Blob} 형태가 된다.
     */
    base64ToBlob(base64, fileName, fileType) {
        const file = S2Util.base64ToFile(base64, fileName, fileType);
        return {
            name: file.name,
            blob: new Blob([file], {type: file.type})
        };
    },
    /**
     * URL 및 파일 이름 안전 Base64(Base64URL) 문자열을 Uint8Array 형태의 바이너리 데이터로 변환한다.
     * Base64URL 형식의 문자열은 일반 Base64와 달리 '+', '/' 대신 '-', '_'를 사용하며, 패딩이 생략될 수 있다.
     *
     * @param {string} base64String - 변환할 Base64URL 문자열. (패딩이 생략되어 있을 수 있음)
     * @returns {Uint8Array} - 변환된 Uint8Array 바이너리 데이터.
     *
     * @example
     * const urlBase64 = 'some-safe-string';
     * const byteArray = S2Util.urlBase64ToUint8Array(urlBase64);
     */
    urlBase64ToUint8Array(base64String) {
        const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
        const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');

        const rawData = window.atob(base64);
        const outputArray = new Uint8Array(rawData.length);

        for (let i = 0; i < rawData.length; ++i) {
            outputArray[i] = rawData.charCodeAt(i);
        }
        return outputArray;
    },
    /**
     * 커스텀 DOM 기반의 alert 창을 생성하여 메시지를 표시하고, '확인' 버튼 클릭 시 콜백 함수를 실행한다.
     * 이 함수는 브라우저 기본 alert 창을 대체하며, 스타일링을 위해 'alert' ID와 's2' 클래스를 사용한다.
     *
     * @param {string} message - 알림창에 표시할 HTML 메시지 문자열.
     * @param {function} [callback] - '확인' 버튼 클릭 시 실행할 콜백 함수.
     * @returns {void}
     *
     * @example
     * S2Util.alert('작업이 완료되었습니다.', function() {
     * console.log('알림 확인!');
     * });
     */
    alert(message, callback) {
        const alertDiv = document.createElement('div');
        alertDiv.id = 'alert';
        alertDiv.classList.add('s2');

        const alertElement = document.createElement('div');

        const alertWrapper = document.createElement('div');
        alertWrapper.id = 'alert-wrapper';

        const alertTitle = document.createElement('div');
        alertTitle.id = 'alert-title';

        const span = document.createElement('span');
        span.innerHTML = message;

        const alertButton = document.createElement('button');
        alertButton.id = 'alert-button';
        alertButton.textContent = '확인';
        alertButton.addEventListener('click', function () {
            if (typeof callback === 'function') {
                callback();
            }
            document.querySelector('#alert.s2').remove();
        });

        alertTitle.appendChild(span);
        alertWrapper.appendChild(alertTitle);
        alertWrapper.appendChild(alertButton);
        alertElement.appendChild(alertWrapper);
        alertDiv.appendChild(alertElement);

        document.body.appendChild(alertDiv);
    },
    /**
     * 커스텀 DOM 기반의 confirm 창을 생성하여 사용자에게 확인 메시지를 표시한다.
     * 이 함수는 '확인'과 '취소' 버튼을 제공하며, '확인' 버튼 클릭 시에만 콜백 함수를 실행한다.
     * 스타일링을 위해 'confirm' ID와 's2' 클래스를 사용한다.
     *
     * @param {string} message - 확인창에 표시할 HTML 메시지 문자열.
     * @param {function} [callback] - '확인' 버튼 클릭 시 실행할 콜백 함수.
     * @returns {void}
     *
     * @example
     * S2Util.confirm('정말로 삭제하시겠습니까?', function() {
     * console.log('사용자가 확인했습니다.');
     * });
     */
    confirm(message, callback) {
        const confirmDiv = document.createElement('div');
        confirmDiv.id = 'confirm';
        confirmDiv.classList.add('s2');

        const innerDiv = document.createElement('div');

        const confirmWrapper = document.createElement('div');
        confirmWrapper.id = 'confirm-wrapper';

        const confirmTitle = document.createElement('div');
        confirmTitle.id = 'confirm-title';

        const titleSpan = document.createElement('span');
        titleSpan.innerHTML = message;

        const confirmTitleDesc = document.createElement('div');
        confirmTitleDesc.id = 'confirm-title-desc';

        const desc = '';
        let descSpan;
        if (desc) {
            descSpan = document.createElement('span');
            descSpan.innerHTML = message;
        }

        const confirmButtonWrapper = document.createElement('div');
        confirmButtonWrapper.id = 'confirm-button-wrapper';

        const confirmButton1 = document.createElement('button');
        confirmButton1.id = 'confirm-button1';
        confirmButton1.textContent = '취소';
        confirmButton1.addEventListener('click', function () {
            document.querySelector('#confirm.s2').remove();
        });

        const confirmButton2 = document.createElement('button');
        confirmButton2.id = 'confirm-button2';
        confirmButton2.textContent = '확인';
        confirmButton2.addEventListener('click', function () {
            if (typeof callback === 'function') {
                callback();
            }
            document.querySelector('#confirm.s2').remove();
        });

        confirmTitle.appendChild(titleSpan);
        if (descSpan) {
            confirmTitleDesc.appendChild(descSpan);
        }
        confirmButtonWrapper.appendChild(confirmButton1);
        confirmButtonWrapper.appendChild(confirmButton2);

        confirmWrapper.appendChild(confirmTitle);
        confirmWrapper.appendChild(confirmTitleDesc);
        confirmWrapper.appendChild(confirmButtonWrapper);

        innerDiv.appendChild(confirmWrapper);
        confirmDiv.appendChild(innerDiv);

        document.body.appendChild(confirmDiv);
    },
    /**
     * 주어진 HTML 내용을 담는 커스텀 모달 창을 생성하여 화면에 표시한다.
     * 모달은 body에 직접 추가되며, 여러 개의 모달을 중첩할 수 있도록 자동으로 ID를 부여한다.
     *
     * @param {string} content - 모달 본문(modal-body)에 들어갈 HTML 콘텐츠.
     * @param {object} [option] - 모달 설정 옵션 객체.
     * @param {string} [option.width='80%'] - 모달 창의 너비 (CSS 값).
     * @param {string} [option.title=''] - 모달 헤더에 표시될 제목.
     * @param {string} [option.titleAlign='center'] - 제목의 텍스트 정렬 (CSS 값).
     * @param {string} [option.titleSize='1.125rem'] - 제목의 폰트 크기 (CSS 값).
     * @param {string} [option.headerHtml=''] - 제목 외에 헤더에 추가될 HTML 콘텐츠.
     * @param {function} [callback] - 모달이 DOM에 추가된 후 실행될 콜백 함수. 첫 번째 인자로 모달의 셀렉터(#s2-modal-N)를 전달한다.
     * @returns {string} - 생성된 모달의 CSS 셀렉터 문자열 (예: '#s2-modal-1').
     *
     * @example
     * S2Util.showModal('<h1>내용</h1>', {title: '알림', width: '500px'}, (selector) => {
     * console.log(`${selector} 모달이 생성됨`);
     * });
     */
    showModal(content, option, callback) {
        if (!option) {
            option = {};
        }

        const modelNo = document.querySelectorAll('.s2modal').length + 1;
        const modalSelector = `#s2-modal-${modelNo}`;

        S2Util.replaceChildren(
            document.body,
            `
                <div id="s2-modal-${modelNo}" class="s2-modal" role="dialog" aria-modal="true" aria-labelledby="s2-modal-title-${modelNo}" aria-describedby="s2-modal-description-${modelNo}">
                    <div class="modal-content" style="width: ${option.width ? option.width : '80%'}">
                        <div class="modal-header">
                            <h2 class="modal-title" id="s2-modal-title-${modelNo}" style="text-align: ${option.titleAlign ? option.titleAlign : 'center'}; font-size: ${option.titleSize ? option.titleSize : '1.125rem'}">${option.title || ''}&nbsp;</h2>
                            ${option.headerHtml ? option.headerHtml : ''}
                            <button class="close-button" aria-label="닫기">&times;</button>
                        </div>
                        <div class="modal-body" id="s2-modal-description-${modelNo}">
                            ${content}
                        </div>
                    </div>
                </div>
            `,
            {
                isAppend: true,
                onNodeReady: (node) => {
                    if (node) {
                        const closeButton = node.querySelector('.close-button');
                        if (closeButton) {
                            closeButton.addEventListener('click', () => {
                                node.remove();
                            });
                        }
                    }
                }
            }
        );

        if (typeof callback === 'function') {
            callback(modalSelector, option);
        }

        return modalSelector;
    },
    /**
     * 지정된 CSS 선택자를 가진 모달 창을 닫는다.
     *
     * @param {string} modalSelector - 닫을 모달의 CSS 선택자
     * @returns {void}
     */
    hideModal(modalSelector) {
        const closeModalBtn = document.querySelector(`${modalSelector} .close-button`);
        if (closeModalBtn) {
            closeModalBtn.click();
        }
    },
    /**
     * 사용자에게 간결한 알림 메시지(Toast)를 화면에 표시한다.
     * 토스트는 고유 ID가 부여되며, 일정 시간 후 자동으로 사라진다.
     *
     * @param {string} message - 토스트 본문에 표시할 메시지.
     * @param {object} [option] - 토스트 설정 옵션 객체.
     * @param {string} [option.title='알림'] - 토스트 상단에 표시될 제목.
     * @param {number} [option.delay=30000] - 토스트가 화면에 표시될 시간(밀리초).
     * @returns {void}
     *
     * @requires S2Util.uuid
     * @requires S2Util.hideToast
     *
     * @example
     * S2Util.showToast('파일 업로드가 완료되었습니다.', {title: '성공', delay: 5000});
     */
    showToast(message, option) {
        const toastId = S2Util.uuid();

        const tempContainer = document.createElement('div');
        tempContainer.innerHTML = `
            <div class="s2-toast" id="${toastId}" class="toast-sty01 no-select">
                <div class="flex-sty04">
                    <strong class="ma-r10">${option && option.title ? option.title : '알림'}</strong>
                    <a href="#" class="fa-close close-sty02 btn-close-s2-toast" data-toast-id="${toastId}"><span class="close-btn" aria-label="닫기"></span></a>
                </div>
                <div class="ma-t15">
                    <span id="toastMessage">${message}</span>
                </div>
            </div>
        `;

        const toastElement = tempContainer.querySelector('.s2-toast');
        toastElement.querySelector('.btn-close-s2-toast').addEventListener('click', (event) => {
            event.preventDefault();
            S2Util.hideToast(event.currentTarget.dataset.toastId);
        });

        document.body.appendChild(toastElement);

        setTimeout(
            () => {
                S2Util.hideToast(toastId);
            },
            option && !isNaN(option.delay) ? option.delay : 30000
        );
    },
    /**
     * 지정된 ID를 가진 토스트 알림 또는 모든 토스트 알림을 화면에서 숨기고 DOM에서 제거한다.
     * 제거 시 'fade-out' 클래스를 추가하여 CSS 트랜지션(transition)을 통한 부드러운 애니메이션 효과를 적용한다.
     *
     * @param {string} [toastId] - 제거할 대상 토스트의 고유 ID. ID를 지정하지 않으면 모든 토스트를 제거한다.
     * @returns {void}
     *
     * @example
     * // ID가 'abc-123'인 토스트 제거
     * S2Util.hideToast('abc-123');
     *
     * // 현재 활성화된 모든 토스트 제거
     * S2Util.hideToast();
     */
    hideToast(toastId) {
        const toastList = document.querySelectorAll('.s2-toast');
        if (toastList) {
            for (const toast of toastList) {
                if (toast && (!toastId || toast.id === toastId)) {
                    toast.classList.add('fade-out');
                    toast.addEventListener(
                        'transitionend',
                        function () {
                            toast.remove();
                        },
                        {once: true}
                    );
                }
            }
        }
    },
    /**
     * 범용 고유 식별자(UUID) 버전 4를 생성하여 반환한다.
     * 가능하다면 window.crypto.randomUUID()를 사용하고, 지원하지 않는 경우
     * 암호화적으로 안전한 난수(getRandomValues)를 사용하여 UUID v4 형식에 맞게 생성한다.
     *
     * @returns {string} - 표준 하이픈(-)으로 구분된 UUID v4 형식의 문자열 (예: 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx').
     *
     * @example
     * S2Util.uuid(); // '550e8400-e29b-41d4-a716-446655440000' 형태의 문자열 반환
     */
    uuid() {
        let uuid = '';
        try {
            const cryptoObj = typeof crypto !== 'undefined' ? crypto : window.msCrypto;

            if (cryptoObj.randomUUID) {
                uuid = cryptoObj.randomUUID();
            } else {
                const bytes = cryptoObj.getRandomValues(new Uint8Array(16));
                // UUID v4: 버전 4 (6번째 바이트 상위 4비트 0100)
                bytes[6] = (bytes[6] & 0x0f) | 0x40;
                // UUID v4: 변형 (8번째 바이트 상위 2비트 10)
                bytes[8] = (bytes[8] & 0x3f) | 0x80;
                const hex = Array.from(bytes)
                    .map((b) => b.toString(16).padStart(2, '0'))
                    .join('');
                uuid = `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}`;
            }
        } catch (e) {
            console.error('UUID 생성 실패:', e);
        }
        return uuid;
    },
    /**
     * 지정된 요소(들)의 숫자를 애니메이션으로 카운트업하는 함수
     * @param {HTMLElement | HTMLElement[] | NodeList} elements - 숫자가 표시될 HTML 요소(들)
     * @param {Object} [options] - 설정 옵션
     * @param {number} [options.duration=2000] - 애니메이션 지속 시간 (밀리초)
     * @param {string} [options.locale='ko-KR'] - 'ko-KR': 한국식(1,234,567), 'de-DE' 유럽식(1.234.567), 지정하지 않음: 숫자만 표시
     * @returns {Object} - 애니메이션 제어 객체 { stop: (index) => void, stopAll: () => void }
     *   - stop(index): 지정된 인덱스의 요소 애니메이션 중지
     *   - stopAll(): 모든 요소의 애니메이션 중지
     *
     * - 요소(들)의 innerText에서 숫자를 추출하여 0부터 목표값까지 부드럽게 증가.
     * - 애니메이션은 options.duration 후 자동 종료.
     * - 입력값이 유효하지 않은 경우 경고를 출력하고 0을 표시.
     *
     * @example
     * // 단일 요소 (한국식 포맷)
     * const singleElement = document.querySelector('.counter');
     * const singleCounter = animateNumber(singleElement, { duration: 3000 }); // 3초 후 자동 종료
     *
     * // 다중 요소 (미국식 포맷)
     * const multipleElements = document.querySelectorAll('.counter');
     * const multiCounter = animateNumber(multipleElements, {
     *   duration: 3000,
     *   locale: 'de-DE' // 유럽식 포맷
     * }); // 3초 후 자동 종료
     *
     * // 수동 중지
     * setTimeout(() => multiCounter.stop(0), 1000); // 첫 번째 요소 1초 후 중지
     * setTimeout(() => multiCounter.stopAll(), 2000); // 모든 요소 2초 후 중지
     */
    animateNumber: function (elements, options) {
        const defaultOptions = {
            duration: 2000,
            locale: ''
        };

        // 1. 객체 전개 구문 ({...}) 대신 Object.assign() 사용 (ES6)
        var settings = Object.assign({}, defaultOptions, options);

        // 2. 옵셔널 체이닝 (?.) 대신 명시적인 조건문 사용
        // options && Number.isFinite(options.duration) 로직으로 대체
        settings.duration = options && Number.isFinite(options.duration) ? options.duration : defaultOptions.duration;

        // options && typeof options.locale === 'string' 로직으로 대체
        settings.locale = options && typeof options.locale === 'string' ? options.locale : null;

        // 단일 요소를 배열로 변환
        // Array.from()은 ES6 문법입니다.
        const elementArray = elements instanceof NodeList ? Array.from(elements) : Array.isArray(elements) ? elements : [elements];

        // 각 요소별 애니메이션 상태 관리
        const animations = elementArray.map(() => ({frameId: null}));

        function formatNumber(num) {
            if (settings.locale) {
                // toLocaleString은 ES1(1997)부터 존재하지만, options 객체를 받는 것은 ES2018입니다.
                // 하지만 locale 인자만 받는 기본 형태는 구형 브라우저에서도 지원될 수 있으므로 유지합니다.
                return Math.round(num).toLocaleString(settings.locale);
            }
            return Math.round(num).toString(); // 콤마 없이 숫자만
        }

        // 각 요소에 대해 애니메이션 실행
        elementArray.forEach((element, index) => {
            const targetString = element.innerText;
            const cleanedString = targetString.replace(/,/g, '');
            const target = parseInt(cleanedString, 10);

            if (!Number.isFinite(target)) {
                console.warn('유효하지 않은 숫자 형식 (요소 ' + index + '):', targetString); // 템플릿 리터럴(ES6) 대신 문자열 연결 사용
                element.innerText = '0';
                return;
            }

            const startTime = performance.now();

            // 화살표 함수는 ES6 문법이므로 유지합니다.
            const animate = function (currentTime) {
                // 함수 선언식으로 변경해도 무방하지만, const animate = (currentTime) => ... 도 ES6이므로 유지
                const elapsed = currentTime - startTime;
                const progress = Math.min(elapsed / settings.duration, 1);
                const current = target * progress;
                element.innerText = formatNumber(current);

                if (progress < 1) {
                    animations[index].frameId = requestAnimationFrame(animate);
                } else {
                    element.innerText = targetString; // 원본 문자열 복원
                }
            };

            animations[index].frameId = requestAnimationFrame(animate);
        });

        return {
            stop: function (index) {
                if (Number.isFinite(index) && animations[index]) {
                    cancelAnimationFrame(animations[index].frameId);
                }
            },
            stopAll: function () {
                animations.forEach(function (anim) {
                    // 화살표 함수 대신 function 키워드 사용 (호환성 확보)
                    cancelAnimationFrame(anim.frameId);
                });
            }
        };
    },
    /**
     * 웹 푸시 알림을 위한 Service Worker를 등록하고, PushManager를 통해 사용자를 구독(Subscription)시킨 후,
     * 구독 정보를 서버에 전송하여 저장한다.
     *
     * @async
     * @param {string} publicKey - VAPID Public Key (Base64URL 형식).
     * @returns {void}
     *
     * @requires S2Util.getLocalStorage
     * @requires S2Util.setLocalStorage
     * @requires S2Util.urlBase64ToUint8Array
     * @requires S2Util.confirm (선택적)
     * @requires dayjs (날짜 비교용)
     *
     * @example
     * S2Util.subscribeServiceWorker('BObv3x9...');
     */
    subscribeServiceWorker: async function (publicKey) {
        if ('PushManager' in window && 'serviceWorker' in navigator) {
            const lastSubscriptionsDate = S2Util.getLocalStorage('plSubscriptionsDate');
            const today = dayjs().format('YYYYMMDD');

            // 알림 컨펌 사용 여부 ("구노에서 알림 받기를 수락하시겠습니까?")
            const isOpenNotificationConfirm = false;

            if (!publicKey) {
                console.debug('Public key 가 없습니다.');
                return;
            } else if (today === lastSubscriptionsDate) {
                console.debug('금일 구독 완료');
                return;
            }

            // 금일 구독을 안했다면 구독을 신청(갱신)한다.
            try {
                let confirmResult = !!lastSubscriptionsDate;

                const registration = await navigator.serviceWorker.register('/public/js/service-worker.s2.js', {scope: '/public/js/'}).then((reg) => {
                    reg.update();
                    return reg;
                });

                if (!confirmResult) {
                    // 기존 구독 정보가 전혀 없다면 구독 여부를 확인한다.
                    confirmResult = await new Promise((resolve) => {
                        if (isOpenNotificationConfirm) {
                            S2Util.confirm('구노에서 알림 받기를 수락하시겠습니까?', function () {
                                resolve(true);
                            });
                        } else {
                            resolve(true);
                        }
                    });
                }

                if (confirmResult) {
                    const subscription = await registration.pushManager.subscribe({
                        userVisibleOnly: true,
                        applicationServerKey: S2Util.urlBase64ToUint8Array(publicKey)
                    });

                    // 서버에 구독 정보 전송
                    const response = await fetch('/public/serviceworker/subscribe.ar', {
                        method: 'POST',
                        body: JSON.stringify(subscription),
                        headers: {
                            'content-type': 'application/json'
                        }
                    });
                    if (response.ok) {
                        S2Util.setLocalStorage('plSubscriptionsDate', today);
                        console.debug('Push 구독 정보 서버 전송 성공');
                    }
                }
            } catch (error) {
                console.error('Service Worker 등록 또는 구독 과정에서 오류 발생:', error);
            }
        }
    },
    /**
     * Service Worker로부터 메시지 이벤트('message')를 수신하도록 리스너를 등록한다.
     * 수신된 데이터의 타입이 'notification'일 경우, 해당 메시지를 showToast를 사용하여 사용자에게 표시한다.
     * 이 함수는 일반적으로 메인 스크립트(클라이언트)에서 Service Worker와 통신하기 위해 사용된다.
     *
     * @returns {void}
     *
     * @requires S2Util.showToast
     *
     * @example
     * // 페이지 로드 시점에 호출하여 Service Worker의 이벤트를 수신 대기한다.
     * S2Util.receiveServiceWorkerEvents();
     */
    receiveServiceWorkerEvents: function () {
        navigator.serviceWorker.addEventListener('message', (event) => {
            if (event.data.type === 'notification') {
                console.debug('ReceiveServiceWorker Notification', event.data);
                S2Util.showToast(event.data.message);
            }
        });
    }
};

/**
 * DOM 이벤트 초기화(바인딩) 함수
 *
 * @example
 * <script type="module">
 *     import { initializeS2DomEvents } from './js/s2.util.js'; // Thymeleaf 는 '[[@{/js/s2.util.js}]]' 형태로 경로 지정
 *     document.addEventListener('DOMContentLoaded', initializeS2DomEvents);
 * </script>
 */
export const initializeS2DomEvents = () => {
    document.body.addEventListener('click', (event) => {
        const element = event.target;
        // 기본 동작을 막는 조건 설정(클릭시 상단으로 스크롤 되는 동작 방지)
        const shouldPreventDefault = element.tagName.toLowerCase() === 'a' || (element.tagName.toLowerCase() === 'input' && element.type === 'submit') || element.tagName.toLowerCase() === 'button';

        if (shouldPreventDefault) {
            // onClick 이벤트를 큐에 넣어 나중에 실행되도록 함
            setTimeout(() => {
                event.preventDefault(); // 기본 동작 방지
                // event.stopPropagation(); // 이벤트 버블링 방지
            }, 0); // 0ms 딜레이를 주어 after the current call stack runs
        }
    });

    {
        document.addEventListener('ajaxStart', () => {
            // const spinner = document.createElement('div');
            // spinner.id = 'ui-gSpinner';
            // spinner.innerHTML = '<img src="./public/images/spinner-big.gif"/>';
            // document.body.appendChild(spinner);
        });

        document.addEventListener('ajaxStop', () => {
            // const spinner = document.getElementById('ui-gSpinner');
            // if (spinner) {
            //     spinner.remove();
            // }

            if (window.eGovUnitTop) {
                // 세션만료 남은시간을 초기화한다.
                window.eGovUnitTop.latestTime = window.eGovUnitTop.getCookie('egovLatestServerTime');
                window.eGovUnitTop.expireTime = window.eGovUnitTop.getCookie('egovExpireSessionTime');
                window.eGovUnitTop.init();
            }
        });

        // 기존 XMLHttpRequest 오픈 메서드를 저장
        const originalOpen = XMLHttpRequest.prototype.open;

        // AJAX 이벤트를 트리거하는 함수
        const triggerAjaxEvent = (eventType) => {
            const event = new Event(eventType);
            document.dispatchEvent(event);
        };

        // XMLHttpRequest 오픈 메서드 재정의
        XMLHttpRequest.prototype.open = function (...args) {
            this.addEventListener('loadstart', () => triggerAjaxEvent('ajaxStart'));
            this.addEventListener('loadend', () => triggerAjaxEvent('ajaxStop'));
            originalOpen.apply(this, args);
        };

        S2Util.receiveServiceWorkerEvents();
    }

    document.querySelectorAll('[sch-enter]').forEach((element) => {
        element.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                // [sch-enter] 요소에서 Enter 키를 누르면 기본 동작을 방지
                e.preventDefault();
            }
        });

        if (element.type !== 'hidden' && element.type !== 'button') {
            element.addEventListener('keyup', function (e) {
                if (!element.hasAttribute('readonly') && e.key === 'Enter') {
                    const optionArr = element.getAttribute('sch-enter').split(':');
                    const functionName = optionArr[0];

                    if (window[functionName] && typeof window[functionName] === 'function') {
                        if (optionArr.length > 1) {
                            let actionStr = `window["${functionName}"](${optionArr
                                .slice(1)
                                .map((param) => `"${param}"`)
                                .join(', ')});`;
                            new Function(actionStr)();
                        } else {
                            window[functionName]();
                        }
                    }
                    e.preventDefault();
                }
            });
        }
    });

    // input:text[alphabetNumber] 요소 입력 정규화
    document.querySelectorAll('input[type="text"][alphabetNumber]').forEach((element) => {
        element.addEventListener('keyup', () => {
            element.value = element.value.replace(/[^a-zA-Z0-9:_\\.\\-]/gi, '');
        });
    });

    // input:text[alphabetOnly] 요소 입력 정규화
    document.querySelectorAll('input[type="text"][alphabetOnly]').forEach((element) => {
        element.addEventListener('keyup', () => {
            element.value = element.value.replace(/[^a-zA-Z:_]/gi, '');
        });
    });

    // input:text[toLowerCase] 요소 소문자로 변환
    document.querySelectorAll('input[type="text"][toLowerCase]').forEach((element) => {
        element.addEventListener('keyup', () => {
            element.value = element.value.toLowerCase();
        });
    });

    // input:text[toUpperCase] 요소 대문자로 변환
    document.querySelectorAll('input[type="text"][toUpperCase]').forEach((element) => {
        element.addEventListener('keyup', () => {
            element.value = element.value.toUpperCase();
        });
    });

    // input:text[numberOnly] 요소 숫자로만 변환
    document.querySelectorAll('input[type="text"][numberOnly]').forEach((element) => {
        element.addEventListener('keyup', () => {
            if (element.getAttribute('numberOnly') === 'comma') {
                element.value = S2Util.comma(element.value.replace(/[^0-9]/gi, ''));
            } else {
                element.value = element.value.replace(/[^0-9]/gi, '');
            }
        });
    });

    // input:text[engOnly] 요소 입력 정규화
    document.querySelectorAll('input[type="text"][engOnly]').forEach((element) => {
        element.addEventListener('keyup', () => {
            element.value = element.value.replace(/[^a-zA-Z0-9\\s]/gi, '');
        });
    });

    // input:text[datetime] 요소 입력 정규화
    document.querySelectorAll('input[type="text"][datetime]').forEach((element) => {
        element.addEventListener('keyup', () => {
            element.value = element.value.replace(/[^0-9:\\-]/gi, '');
        });
    });

    // input:text[phoneNumber] 요소 입력 정규화
    document.querySelectorAll('input[type="text"][phoneNumber]').forEach((element) => {
        element.addEventListener('keyup', () => {
            element.value = element.value.replace(/[^0-9\\-]/gi, '');
        });
    });

    // input:text[maxByte] 요소 입력 바이트 수 제한
    document.querySelectorAll('input[type="text"][maxByte]').forEach((element) => {
        element.addEventListener('keyup', () => {
            const maxByte = Number(element.getAttribute('maxByte'));
            if (maxByte && !isNaN(maxByte) && maxByte > 0) {
                const str = element.value;
                let byteLength = 0;
                for (let i = 0; i < str.length; i++) {
                    byteLength += str.charCodeAt(i) > 127 ? 2 : 1;
                    if (byteLength > maxByte) {
                        element.value = str.slice(0, i);
                        break;
                    }
                }
            }
        });
    });

    // 목록 테이블 헤더 정렬
    document.querySelectorAll('[sort-nm]').forEach((header) => {
        if (!header.querySelector('> .fas.fa-sort')) {
            header.innerHTML += '<i class="fas fa-sort"></i>';
        }

        header.addEventListener('click', () => {
            const table = header.closest('table');
            if (table && table.querySelectorAll('tbody tr').length <= 1) {
                return;
            }

            const thisSortNm = header.getAttribute('sort-nm');
            const thisSortFnNm = header.getAttribute('sort-fn-nm');
            const sortIcon = header.querySelector('.fas.fa-sort');

            if (sortIcon) {
                if (!sortIcon.classList.contains('fa-sort-up')) {
                    sortIcon.classList.add('fa-sort-up');
                    sortIcon.classList.remove('fa-sort-down');
                } else {
                    sortIcon.classList.add('fa-sort-down');
                    sortIcon.classList.remove('fa-sort-up');
                }
            }

            header
                .closest('thead')
                .querySelectorAll('[sort-nm]')
                .forEach((otherHeader) => {
                    if (otherHeader.getAttribute('sort-nm') !== thisSortNm) {
                        const otherSortIcon = otherHeader.querySelector('.fas.fa-sort');
                        if (otherSortIcon) {
                            otherSortIcon.classList.remove('fa-sort-up', 'fa-sort-down');
                        }
                    }
                });

            if (thisSortFnNm && typeof window[thisSortFnNm] === 'function') {
                window[thisSortFnNm](header);
            }
        });
    });
};
