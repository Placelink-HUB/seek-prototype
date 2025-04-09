/**
 * 공통 유틸
 * @author GOONO
 * @since  2021
 * @version 1.0
 * @see
 * Copyright (C)  All right reserved.
*/
var coreCommon = {
	//프로토콜 및 포트포함 URL 정보
	getDomain: function() {
		var url = window.location.protocol + "//" + window.location.host;
		return url;
	},
	//컨텍스트 포함 경로
	getWebRoot: function() {
		return $("#webroot").val();
	},
	//호출 주소 생성
	getUrl: function(url) {
		if (this.getWebRoot() == '/') {
			return url;
		}
		return this.getWebRoot() + url;
	},
	getDomainUrl: function(url) {
		return this.getDomain() + this.getUrl(url);
	},
	//url이동
	href: function(url) {
		if (top !== self) {
			top.location.href = coreCommon.getUrl(url);
		} else {
			location.href = coreCommon.getUrl(url);
		}
	},
	isEmpty : function(text) {
        	if (text == ""
        	|| text == null
        	|| text == undefined
        	|| (text != null && typeof text == "object" && !Object
        	.keys(text).length)) {
        	    return true;
        	}
        	return false;
 	},
	//쿠키 설정
	setCookie : function(name, value, expiredays) {	    
	    var today = new Date();
	    today.setDate( today.getDate() + expiredays );
	    document.cookie = name + "=" + escape( value ) + "; path=/; expires=" + today.toGMTString() + ";"
	},	
	//쿠키확인
	getCookie : function(name) {
	    var cook = document.cookie + ";";
		//console.debug(cook);
		var idx = cook.indexOf(name, 0);
		var val = "";
		if (idx != -1) {
			cook = cook.substring(idx, cook.length);
			begin = cook.indexOf("=", 0) + 1;
			end = cook.indexOf(";", begin);
			val = unescape(cook.substring(begin, end));
		}	
	    return val; 
	},  
	//유니크한 값 만들기
	genUUID : function() {
	    var d = new Date().getTime();
	    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
	    function(c) {
	       var r = (d + Math.random() * 16) % 16 | 0;
	           d = Math.floor(d / 16);
	       return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
	    });
	    return uuid;
	},
	//textarea 문자입력수 카운팅 및 표시
    textareaLimitText : function (textareaId,countId,limitNum) {
        var limitField = $(textareaId);
        var limitCount = $(countId);
        if (limitField.val().length > limitNum) {
            limitField.val(limitField.val().substring(0, limitNum));
        } else {
            limitCount.text(limitNum - limitField.val().length);
        }
    },
	//input 문자입력수 카운팅 및 표시
    inputLimitText : function (inputId, countId, limitNum) {
        var limitField = $(inputId);
        var limitCount = $(countId);
        if (limitField.val().length > limitNum) {
            limitField.val(limitField.val().substring(0, limitNum));
        } else {
            limitCount.text(limitField.val().length);
        }
    },
    getTickCount : function(){
        var currentDate = new Date();
        return currentDate.getTime();
    },
    leadingZeros : function(n, digits) {
        var zero = '';
        n = n.toString();

        if (n.length < digits) {
            for (i = 0; i < digits - n.length; i++)
                zero += '0';
        }
        return zero + n;
    },
	getDateFromDateString : function(strDate){
		if(coreCommon.isEmpty(strDate)){
			return strDate;
		}
		if(strDate.length < 10){ 
			return strDate;
		}
		var date = strDate.substring(0, 10);
		return date;
	},
    //현재시간[20170707111111]
    getNowDateTime : function(timestamp,dash) {
        var d = null;
        if(timestamp == null || timestamp == undefined ){
            d = new Date();
        }else{
            d = new Date(timestamp);
        }

        if(dash == null || dash == undefined ){
            var s =
                this.leadingZeros(d.getFullYear(), 4) +
                this.leadingZeros(d.getMonth() + 1, 2) +
                this.leadingZeros(d.getDate(), 2) +
                this.leadingZeros(d.getHours(), 2) +
                this.leadingZeros(d.getMinutes(), 2) +
                this.leadingZeros(d.getSeconds(), 2);
            return s;
        }

        var s =
            this.leadingZeros(d.getFullYear(), 4) + "-" +
            this.leadingZeros(d.getMonth() + 1, 2) + "-" +
            this.leadingZeros(d.getDate(), 2) + " "+
            this.leadingZeros(d.getHours(), 2) + ":"+
            this.leadingZeros(d.getMinutes(), 2) + ":"+
            this.leadingZeros(d.getSeconds(), 2);
        return s;
    },
	getDate : function(timestamp) {
        var d = null;
        if(timestamp == null || timestamp == undefined ){
            d = new Date();
        }else{
            d = new Date(timestamp);
        }

        var s =
            this.leadingZeros(d.getFullYear(), 4) + "-" +
            this.leadingZeros(d.getMonth() + 1, 2) + "-" +
            this.leadingZeros(d.getDate(), 2) + " ";
        return s;
    },
    getNowDate : function(timestamp,dash) {
        var d = null;
        if(timestamp == null || timestamp == undefined ){
            d = new Date();
        }else{
            d = new Date(timestamp);
        }

        if(dash == null || dash == undefined ){
            var s =
                this.leadingZeros(d.getFullYear(), 4) +
                this.leadingZeros(d.getMonth() + 1, 2) +
                this.leadingZeros(d.getDate(), 2);
            return s;
        }

        var s =
            this.leadingZeros(d.getFullYear(), 4) + "-" +
            this.leadingZeros(d.getMonth() + 1, 2) + "-" +
            this.leadingZeros(d.getDate(), 2) + " ";
        return s;
    },
    //날짜 일수차이  0크면 : 지난일수 , 0작은면 : 남은일수
    //dateDiff('2020-03-26','2020-03-25') => -1
    //dateDiff('2020-03-30','2020-03-25') => 5
    //dateDiff('2020-03-25',new Date())
    dateDiff : function (_date1, _date2) {
        var diffDate_1 = _date1 instanceof Date ? _date1 :new Date(_date1);
        var diffDate_2 = _date2 instanceof Date ? _date2 :new Date(_date2);

        diffDate_1 =new Date(diffDate_1.getFullYear(), diffDate_1.getMonth()+1, diffDate_1.getDate());
        diffDate_2 =new Date(diffDate_2.getFullYear(), diffDate_2.getMonth()+1, diffDate_2.getDate());

        var diff = Math.ceil(diffDate_2.getTime() - diffDate_1.getTime());
        diff = Math.ceil(diff / (1000 * 3600 * 24));
        return diff;
    },
    calcBytes : function(bytes) {
        if(this.isEmpty(bytes)) return "0";
        var bytes = parseInt(bytes);
        var s = ['B', 'KB', 'MB', 'GB', 'TB', 'PB'];
        var e = Math.floor(Math.log(bytes)/Math.log(1024));

        if(e == "-Infinity") return "0 "+s[0];
        else
            return (bytes/Math.pow(1024, Math.floor(e))).toFixed(2)+" "+s[e];
    },
    /**
	 * 파일명에서 확장자명 추출
	 * @param filename   파일명
	 * @returns _fileExt 확장자명
	 */
	getExtensionOfFilename : function (filename) {
	    var _fileLen = filename.length;
	    /** 
	     * lastIndexOf('.') 
	     * 뒤에서부터 '.'의 위치를 찾기위한 함수
	     * 검색 문자의 위치를 반환한다.
	     * 파일 이름에 '.'이 포함되는 경우가 있기 때문에 lastIndexOf() 사용
	     */
	    var _lastDot = filename.lastIndexOf('.');
	 
	    // 확장자 명만 추출한 후 소문자로 변경
	    var _fileExt = filename.substring(_lastDot + 1, _fileLen);
	 
	    return _fileExt;
	},
	getNameOfFilename : function (filename) {
	    var _fileLen = filename.length;
	    /** 
	     * lastIndexOf('.') 
	     * 뒤에서부터 '.'의 위치를 찾기위한 함수
	     * 검색 문자의 위치를 반환한다.
	     * 파일 이름에 '.'이 포함되는 경우가 있기 때문에 lastIndexOf() 사용
	     */
	    var _lastDot = filename.lastIndexOf('.');
	 
	    //파일명
	    var _fileNm = filename.substring(0, _lastDot);
	    	 
	    return _fileNm;
	},
	getSplitFilename : function (filename) {
	    var _fileLen = filename.length;
	    /** 
	     * lastIndexOf('.') 
	     * 뒤에서부터 '.'의 위치를 찾기위한 함수
	     * 검색 문자의 위치를 반환한다.
	     * 파일 이름에 '.'이 포함되는 경우가 있기 때문에 lastIndexOf() 사용
	     */
	    var _lastDot = filename.lastIndexOf('.');
	 
	    // 확장자 명만 추출한 후 소문자로 변경
	    var _fileExt = filename.substring(_lastDot + 1, _fileLen);
	    
	    //파일명
	    var _fileNm = filename.substring(0, _lastDot);
	    	 
	    return { fileNm :  _fileNm, fileExt :  _fileExt };
	},
    //클립보드로 복사
    setClipboard : function (data,cbSuccess,cbFail){
        if (window.clipboardData && window.clipboardData.setData) {
            //IE
            if(  window.clipboardData.setData("Text", data ) ){
                if(typeof cbSuccess === "function"){
                    cbSuccess();
                }
            }else{
                if(typeof cbFail === "function"){
                    cbFail();
                }
            }
        } else if (document.queryCommandSupported && document.queryCommandSupported("Copy")) {
            var textarea = document.createElement("textarea");
            textarea.textContent = data;
            textarea.style.position = "fixed";  // Prevent scrolling to bottom of page in MS Edge.
            document.body.appendChild(textarea);
            //textarea.focus();
            textarea.select();
            try {
                if(document.execCommand("copy")){
                    if(typeof cbSuccess === "function"){
                        cbSuccess();
                    }
                }else{
                    if(typeof cbFail === "function"){
                        cbFail();
                    }
                }
            } catch (ex) {
                if(typeof cbFail === "function"){
                    cbFail();
                }
            } finally {
                document.body.removeChild(textarea);
            }
        }
    },
    //모바일접속유무
    isMobile : function() {
        var filter = "win16|win32|win64|mac|macintel";
        if (navigator.platform ) {
            if (filter.indexOf(navigator.platform.toLowerCase()) < 0) {
                //console.debug("MOBILE");
                return true;
            } else {
                //console.debug("PC");
            }
        }
        return false;
    },
    //숫자만 입력허용
    onKeyOnlyNumber : function(inputObj) {
        $(inputObj).keyup(function() {
            $(this).val($(this).val().replace(/[^0-9]/g, ""));
        });
    },
    //소숫점
    onKeyOnlyDouble : function(inputObj) {
        $(inputObj).keyup(function() {
            $(this).val($(this).val().replace(/[^0-9.]/g, ""));
        });
    },
    //숫자,영문,-_
    onKeyOnlyEngNumber : function(inputObj) {
        $(inputObj).keyup(function() {
            $(this).val($(this).val().replace(/[^0-9a-zA-Z_-]/g, ""));
        });
    },
    //숫자,영문,-_
    onKeyOnlyEngNumSpecial : function(inputObj) {
        $(inputObj).keyup(function() {
            $(this).val($(this).val().replace(/[^0-9a-zA-Z_-\s]/g, ""));
        });
    },
    //숫자만 입력허용(콤마처리)
    onKeyOnlyNumberComma : function(inputObj) {
        $(inputObj).keyup(function() {
            var data = $(this).val().replace(/[^0-9]/g, "");
            $(this).val(data.replace(/\B(?=(\d{3})+(?!\d))/g, ","));
        });
    },
    //영문, 숫자, 특수문자 조합
    onKeyPwd : function(inputObj) {
        $(inputObj).keyup(function() {
            $(this).val($(this).val().replace(/[^0-9a-zA-Z\\d$@$!%*#?&]/g, ""));
        });
    },
    //비밀번호 최소 하나의 문자 + 하나의 숫자 + 하나의 특수 문자 포함, 최소 8자리 ( 미조건시 : false )
    checkPwd8 : function(text) {
		var regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,}$/;
		return regex.test(text);
    },
    getNumberFixed : function(data,fixedCnt) {
	 if(data == 0 || coreCommon.isEmpty(data) ) return 0;
	 return Number.parseFloat(data).toFixed(fixedCnt);
	},
	getOnlyNumberComma : function(data) {
		if(data == 0 || coreCommon.isEmpty(data) ) return 0;
        data = Math.round(data).toString();
        return data.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    },
	escapeHtml : function (str){
		 if (str == null) {
		  	return "";
		 }
		 return str.replace(/&/g, '&amp;') 
		 .replace(/</g, '&lt;') 
		 .replace(/>/g, '&gt;') 
		 .replace(/"/g, '&quot;')
		 .replace(/'/g, '&#39;')
		 .replace(/\//g, '&#x2F;') 
		 .replace(/`/g, '&#x60;')
		 .replace(/=/g, '&#x3D;')
	     .replace(/\n/g,"<br />");
		   
	},
	unescapeHtml : function (str) {
		 if (str == null) {
		  	return "";
		 }
		 return str
		   .replace(/&amp;/g, '&')
		   .replace(/&lt;/g, '<')
		   .replace(/&gt;/g, '>')
		   .replace(/&quot;/g, '"')
		   .replace(/&#039;/g, "'")
		   .replace(/&#39;/g, "'")
		   .replace(/&#x2F;/g, "\/")
		   .replace(/&#x60;/g, "`")
		   .replace(/&#x3D;/g, "=");
	},
	maskingString : function(strName) {
	  if (strName.length > 2) {
	    var originName = strName.split('');
	    originName.forEach(function(name, i) {
	      if (i === 0 || i === originName.length - 1) return;
	      originName[i] = '*';
	    });
	    var joinName = originName.join();
	    return joinName.replace(/,/g, '');
	  } else {
	    var pattern = /.$/; // 정규식
	    return strName.replace(pattern, '*');
	  }
	},
	updateURLParam : function (key,val){
	    var url = window.location.href;
	    var reExp = new RegExp("[\?|\&]"+key + "=[0-9a-zA-Z\_\+\-\|\.\,\;]*");

	    if(reExp.test(url)) {
	        // update
	        var reExp = new RegExp("[\?&]" + key + "=([^&#]*)");
	        var delimiter = reExp.exec(url)[0].charAt(0);
	        url = url.replace(reExp, delimiter + key + "=" + val);
	    } else {
	        // add
	        var newParam = key + "=" + val;
	        if(!url.indexOf('?')){url += '?';}

	        if(url.indexOf('#') > -1){
	            var urlparts = url.split('#');
	            url = urlparts[0] +  "&" + newParam +  (urlparts[1] ?  "#" +urlparts[1] : '');
	        } else {
	            url += "&" + newParam;
	        }
	    }
	    window.history.pushState(null, document.title, url);
	},
	//언어변경
	changeLanguage: function(languagCookieName, lang) {
		var url = '/account/anon/language.do?' + languagCookieName + '=' + lang;
		coreCommon.href(url);
	},
	// counter-up
	counterUp: function(element, options) {
		const defaultOptions = {
			duration:  options && options.duration && !isNaN(options.duration) ? options.duration : 2000,
			delay: 40
		};
		const settings = {...defaultOptions, ...options};

		const targetString = element.innerText;
		const target = parseInt(targetString.replace(/,/g, ''), 10);
		let current = 0;
		const increment = target / (settings.duration / settings.delay);

		function formatNumber(num) {
			return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		}

		const updateCounter = () => {
			current += increment;
			element.innerText = formatNumber(Math.round(current));

			if (current < target) {
				setTimeout(updateCounter, settings.delay);
			} else {
				element.innerText = targetString; // 원본 문자열로 복원 (소수점 등 보존)
			}
		};

		updateCounter();
	}
};