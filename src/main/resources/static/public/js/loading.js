let g_interval;

function controlProgressBar(timeout) {
    let progress = 0;
    g_interval = setInterval(function () {
        if (progress >= 98) {
            clearInterval(g_interval);
            return;
        }
        setProgressBar(++progress);
    }, timeout && !isNaN(timeout) ? timeout : 1200);
}

function setProgressBar(percent) {
    const progressBar = document.querySelector('.progress-bar');
    const progressPercent = document.querySelector('.progress-text');
    progressBar.style.width = percent + '%';
    progressPercent.textContent = percent + '%';
}

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

function hideLoadingPage() {
    document.querySelectorAll('#loading-overlay').forEach((element) => {
        if (element) element.remove();
    });
}