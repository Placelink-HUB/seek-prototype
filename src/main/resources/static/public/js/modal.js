function showModal(content, option, callback) {
    if (!option) {
        option = {}
    }

    const modelNo = document.querySelectorAll('.s2modal').length + 1;

    document.body.innerHTML += `
        <div id="s2-modal-${modelNo}" class="s2-modal" role="dialog" aria-modal="true" aria-labelledby="s2-modal-title-${modelNo}" aria-describedby="s2-modal-description-${modelNo}">
            <div class="modal-content" style="width: ${option.width ? option.width : '80%'}">
                <div class="modal-header">
                    <h2 class="modal-title" id="s2-modal-title-${modelNo}" style="text-align: ${option.titleAlign ? option.titleAlign : 'center'}; font-size: ${option.titleSize ? option.titleSize : '18px'}">${option.title || ''}&nbsp;</h2>
                    ${option.headerHtml ? option.headerHtml : ''}
                    <button class="close-button" aria-label="닫기">&times;</button>
                </div>
                <div class="modal-body" id="s2-modal-description-${modelNo}">
                    ${content}
                </div>
            </div>
        </div>
    `;

    document.querySelector('.s2-modal > .modal-content > .modal-header > .close-button').addEventListener('click', (event) => {
        if (event.target.closest('.s2-modal')) {
            event.target.closest('.s2-modal').remove();
        }
    });

    const modalSelector = `#s2-modal-${modelNo}`;

    if (typeof callback === 'function') {
        callback(modalSelector, option);
    }

    return modalSelector;
}
