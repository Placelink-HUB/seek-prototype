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
 */
package biz.placelink.seek.com.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import kr.s2.ext.log.S2LogManager;
import kr.s2.ext.log.S2Logger;
import kr.s2.ext.log.S2LoggerFactory;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 02. 15.      s2          최초생성
 * </pre>
 */
@Configuration
public class S2LoggerAdapter {

    @PostConstruct
    public void initializeLogger() {
        // SLF4J 로거를 사용하는 커스텀 로거 팩토리 설정
        S2LogManager.setLoggerFactory(new S2LoggerFactory() {
            @Override
            public S2Logger getLogger(Class<?> clazz) {
                final Logger logger = LoggerFactory.getLogger(clazz);
                return createSlf4jAdapter(logger);
            }

            @Override
            public S2Logger getLogger(String name) {
                final Logger logger = LoggerFactory.getLogger(name);
                return createSlf4jAdapter(logger);
            }

            private S2Logger createSlf4jAdapter(final Logger logger) {
                return new S2Logger() {
                    @Override
                    public void debug(String message) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(message);
                        }
                    }

                    @Override
                    public void info(String message) {
                        if (logger.isInfoEnabled()) {
                            logger.info(message);
                        }
                    }

                    @Override
                    public void warn(String message) {
                        if (logger.isWarnEnabled()) {
                            logger.warn(message);
                        }
                    }

                    @Override
                    public void error(String message) {
                        if (logger.isErrorEnabled()) {
                            logger.error(message);
                        }
                    }

                    @Override
                    public void error(String message, Throwable t) {
                        if (logger.isErrorEnabled()) {
                            logger.error(message, t);
                        }
                    }

                    @Override
                    public boolean isDebugEnabled() {
                        return logger.isDebugEnabled();
                    }

                    @Override
                    public boolean isInfoEnabled() {
                        return logger.isInfoEnabled();
                    }

                    @Override
                    public boolean isWarnEnabled() {
                        return logger.isWarnEnabled();
                    }

                    @Override
                    public boolean isErrorEnabled() {
                        return logger.isErrorEnabled();
                    }
                };
            }
        });
    }

}
