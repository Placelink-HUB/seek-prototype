import css from '@eslint/css';
import js from '@eslint/js';
import htmlPlugin from 'eslint-plugin-html';
import {defineConfig} from 'eslint/config';
import globals from 'globals';

export default defineConfig([
    {
        // ESLint의 검사(린팅) 대상에서 제외할 파일 및 디렉토리 설정(글로벌 설정, 각 패일 패턴 별로 설정 가능)
        ignores: ['node_modules/', 'build/', 'dist/', 'bin/', '**/*.min.js', '**/*.bundle.js', '**/*.map.js']
    },
    {
        files: ['**/*.{js,mjs,cjs}'],
        plugins: {js},
        extends: ['js/recommended'],
        languageOptions: {
            globals: {
                // 브라우저 전역 변수 (window, document, console, fetch 등)
                ...globals.browser,
                // 전역으로 사용되는 다른 변수가 있다면 여기에 추가:
                S2Util: true,
                jQuery: true,
                $: true,
                dayjs: true
            }
        }
    },
    {
        files: ['**/*.js'],
        languageOptions: {sourceType: 'script'}
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
