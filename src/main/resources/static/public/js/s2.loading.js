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
import {S2Util} from './s2.util.js';

let g_interval;

/**
 * 프로그레스 바의 애니메이션 진행을 제어합니다.
 * @private
 * @param {number} timeout - 인터벌 시간 (밀리초)
 */
const controlProgressBar = (timeout) => {
    let progress = 0;
    g_interval = setInterval(
        function () {
            if (progress >= 98) {
                clearInterval(g_interval);
                return;
            }
            setProgressBar(++progress);
        },
        timeout && !isNaN(timeout) ? timeout : 1200
    );
};

/**
 * 프로그레스 바의 너비와 텍스트를 업데이트합니다.
 * @private
 * @param {number} percent - 진행률 (0-100)
 */
const setProgressBar = (percent) => {
    const progressBar = document.querySelector('.progress-bar');
    const progressPercent = document.querySelector('.progress-text');
    if (progressBar) progressBar.style.width = percent + '%';
    if (progressPercent) progressPercent.textContent = percent + '%';
};

/**
 * 로딩 페이지 오버레이를 화면에 표시합니다.
 * 옵션에 따라 프로그레스 바를 표시하고 애니메이션을 시작합니다.
 *
 * @param {Object} [options] - 로딩 설정 옵션
 * @param {boolean} [options.showOverlay=false] - 오버레이 표시 여부
 * @param {boolean} [options.showProgress=false] - 프로그레스 바 표시 여부
 * @param {number} [options.timeout=1200] - 프로그레스 바 진행 속도 (인터벌 시간, 기본 1200ms)
 * @returns {void}
 *
 * @example
 * import { showS2Loading, hideS2Loading } from './js/s2.loading.js'; // Thymeleaf 는 '[[@{/js/s2.loading.js}]]' 형태로 경로 지정
 *
 * ex1) 프로그레스 바 없이 로딩 표시
 * showS2Loading();
 * ex2) 프로그레스 바를 표시하며 로딩 시작
 * showS2Loading({ showProgress: true, timeout: 800 });
 */
export const showS2Loading = (options) => {
    // 기존에 존재하는 로딩 오버레이 제거
    hideS2Loading();

    const loadingHtml = `
        <div id="s2-loading-overlay">
            <div class="loader-container">
                <div class="hexagon-wrap">
                    <div>
                        <div class="hexagon hex1 blue">
                            <div class="inner-line"></div>
                        </div>
                    </div>
                    <div>
                        <div class="hexagon hex2 gray">
                            <div class="inner-line"></div>
                            <div class="hexagon-line"></div>
                        </div>
                        <div class="hexagon hex3 gray">
                            <div class="inner-line"></div>
                            <div class="hexagon-line"></div>
                        </div>
                    </div>
                    <div>
                        <div class="hexagon hex4 blue">
                            <div class="inner-line"></div>
                            <div class="hexagon-line blue line1"></div>
                            <div class="hexagon-line blue line2"></div>
                            <div class="hexagon-line blue line3"></div>
                        </div>
                    </div>
                </div>
                <div class="progress-wrap ${options && options.showProgress ? '' : 'd-none'}">
                    <span class="progress-text"></span>
                    <div class="progress-bar"></div>
                </div>
            </div>
        </div>
    `;

    S2Util.replaceChildren(document.body, loadingHtml, {
        isAppend: true,
        onNodeReady: (node) => {
            if (options && (options.showOverlay || options.showProgress)) {
                node.classList.add('background');
            }
        }
    });

    if (options && options.showProgress) {
        setProgressBar(0);
        controlProgressBar(options ? options.timeout : 0);
    }
};

/**
 * 화면에 표시된 로딩 페이지 오버레이를 제거하고, 프로그레스 바 애니메이션을 중지합니다.
 *
 * @returns {void}
 *
 * @example
 * import { hideS2Loading } from './js/s2.loading.js'; // Thymeleaf 는 '[[@{/js/s2.loading.js}]]' 형태로 경로 지정
 * ex) 로딩 페이지 숨기기
 * hideS2Loading();
 */
export const hideS2Loading = () => {
    // 기존에 설정된 인터벌 제거
    if (g_interval) {
        clearInterval(g_interval);
    }
    const loadingOverlay = document.querySelector('#s2-loading-overlay');
    if (loadingOverlay) {
        loadingOverlay.remove();
    }
};
