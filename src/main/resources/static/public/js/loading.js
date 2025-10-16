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
let g_interval;

function controlProgressBar(timeout) {
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
}

function setProgressBar(percent) {
    const progressBar = document.querySelector('.progress-bar');
    const progressPercent = document.querySelector('.progress-text');
    progressBar.style.width = percent + '%';
    progressPercent.textContent = percent + '%';
}

// eslint-disable-next-line no-unused-vars
function showLoadingPage(options) {
    const loadingOverlay = document.createElement('div');
    loadingOverlay.id = 'loading-overlay';
    loadingOverlay.innerHTML = `
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
    `;
    document.body.appendChild(loadingOverlay);
    if (options && options.showProgress) {
        loadingOverlay.classList.add('background');
        setProgressBar(0);
        controlProgressBar(options ? options.timeout : 0);
    }
}

// eslint-disable-next-line no-unused-vars
function hideLoadingPage() {
    document.querySelectorAll('#loading-overlay').forEach((element) => {
        if (element) element.remove();
    });
}
