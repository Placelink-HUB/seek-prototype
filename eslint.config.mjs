import css from '@eslint/css';
import js from '@eslint/js';
import htmlPlugin from 'eslint-plugin-html';
import {defineConfig} from 'eslint/config';
import globals from 'globals';

export default defineConfig([
    {
        files: ['**/*.{js,mjs,cjs}'],
        plugins: {js},
        extends: ['js/recommended']
    },
    {
        files: ['**/*.js'],
        languageOptions: {sourceType: 'script'}
    },
    {
        files: ['**/*.{js,mjs,cjs}'],
        languageOptions: {
            globals: {
                // 브라우저 전역 변수 (window, document, console, fetch 등)
                ...globals.browser,
                // 전역으로 사용되는 다른 변수가 있다면 여기에 추가:
                dayjs: true,
                jQuery: true
            }
        }
    },
    {
        files: ['**/*.css'],
        plugins: {css},
        language: 'css/css',
        extends: ['css/recommended']
    },
    {
        files: ['**/*.html'],
        plugins: {
            html: htmlPlugin // HTML 플러그인
        },
        settings: {
            'html/html-extensions': ['.html']
        },
        languageOptions: {
            sourceType: 'script' // <script type="text/javascript"> 처리
        },
        rules: {
            'no-unused-vars': 'error',
            'no-console': 'warn',
            'no-var': 'error'
        }
    }
]);
