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
package biz.placelink.seek.com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.system.file.vo.FileDetailVO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.exception.S2RuntimeException;
import kr.s2.ext.util.S2StreamUtil;
import kr.s2.ext.util.S2Util;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2024. 07. 04.      s2          최초생성
 * </pre>
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * <pre>
     * 파일 존재 여부 체크 시
     * </pre>
     *
     * @param strPath 경로
     * @return true = 존재 , false = 미존재
     */
    public static boolean exists(String strPath) {
        boolean result = false;
        if (S2Util.isNotEmpty(strPath)) {
            File file = new File(strPath);
            if (file != null && file.length() > 0) {
                result = file.exists();
            }
        }
        return result;
    }

    /**
     * <pre>
     * 디렉토리 생성
     * </pre>
     *
     * @param path 생성할 디렉토리의 경로
     * @return 없음
     */
    public static boolean makeDirectory(String path) {
        boolean result = false;
        if (S2Util.isNotEmpty(path)) {
            File directory = new File(path);
            result = directory.exists() || directory.mkdirs();
        }
        return result;
    }

    /**
     * 특정 파일을 특정 위치에 copy 처리.
     *
     * @param strSrc 원본 경로(파일 또는 디렉토리)
     * @param strTar 대상 경로(파일 또는 디렉토리)
     * @throws IOException
     */
    public static void fileCopy(String strSrc, String strTar) throws IOException {
        File srcFile = new File(strSrc);
        File tarFile = new File(strTar);

        if (!srcFile.exists()) {
            throw new IOException();
        }

        if (srcFile.isDirectory()) {
            if (!tarFile.exists()) {
                tarFile.mkdir();
            }

            File[] files = srcFile.listFiles();

            if (files != null) {
                for (File file : files) {
                    FileUtils.fileCopy(file.getPath(), strTar + "/" + file.getName());
                }
            }
        } else {
            if (!FileUtils.exists(tarFile.getParent())) {
                FileUtils.makeDirectory(tarFile.getParent());
            }

            InputStream in = null;
            OutputStream out = null;

            try {
                in = new FileInputStream(srcFile);
                out = new FileOutputStream(tarFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (FileNotFoundException fe) {
                logger.debug("FileUtil.fileCopy : FileNotFoundException");
            } catch (IOException ie) {
                logger.debug("FileUtil.fileCopy : IOException");
            } finally {
                S2StreamUtil.closeStream(in);
                S2StreamUtil.closeStream(out);
            }
        }
    } // fileCopy

    /**
     * 특정 대상 폴더안의 모든 파일 목록을 가져온다.
     *
     * @param folder : 파일 목록을 조회하고자 하는 폴더
     * @return
     */
    public static List<String> getFileList(String folder) {
        List<String> fileList = new ArrayList<>();

        File[] files = new File(folder).listFiles();

        if (files != null) {
            for (File file : files) {
                fileList.add(file.getPath());
            }
        }
        return fileList;
    }

    /**
     * <pre>
     * 파일 삭제
     * </pre>
     *
     * @param strPath 파일 경로
     * @return true = 성공, false = 실패
     */
    public static boolean deleteFile(String strPath) {
        if (FileUtils.exists(strPath)) {
            return FileUtils.getFilePath(strPath).delete();
        } else {
            return false;
        }
    }

    /**
     * 파일 목록 삭제
     *
     * @param delFileList
     * @return
     */
    public static int deleteFileList(List<FileDetailVO> delFileList) {
        int result = 0;
        if (delFileList != null) {
            for (FileDetailVO delFile : delFileList) {
                if (delFile != null) {
                    FileUtils.deleteFile(S2Util.joinPaths(delFile.getSavePath(), delFile.getSaveName()));
                }
            }
        }
        return result;
    }

    /**
     * <pre>
     * 파일 경로에 대한 FILE 인스턴스 획득 시
     * </pre>
     *
     * @param dest 경로
     * @return File
     */
    public static File getFilePath(String dest) {
        return new File(dest);
    }

    public static byte[] getByteArray(File resume) throws IOException {

        byte[] fileContent = new byte[(int) resume.length()];
        InputStream is = null;

        try {
            is = new FileInputStream(resume);

            int offset = 0;
            int numRead = 0;

            while (offset < fileContent.length
                    && (numRead = is.read(fileContent, offset, fileContent.length - offset)) >= 0) {
                offset += numRead;
            }
        } catch (FileNotFoundException fe) {
            logger.debug("FileUtil.fileCopy : FileNotFoundException");
        } catch (IOException ie) {
            logger.debug("FileUtil.fileCopy : IOException");
        } finally {
            S2StreamUtil.closeStream(is);
        }

        return fileContent;
    }

    public static String getContentType(String extension) {
        String contentType = "application/octet-stream";

        if (".JPG".equals(extension)) {
            contentType = "image/jpeg";
        } else if (".JPEG".equals(extension)) {
            contentType = "image/jpeg";
        } else if (".PNG".equals(extension)) {
            contentType = "image/png";
        } else if (".BMP".equals(extension)) {
            contentType = "image/bmp";
        } else if (".GIF".equals(extension)) {
            contentType = "image/gif";
        } else if (".WAV".equals(extension)) {
            contentType = "audio/wav";
        } else if (".MP3".equals(extension)) {
            contentType = "audio/mpeg";
        } else if (".ZIP".equals(extension)) {
            contentType = "application/zip";
        } else if (".XLS".equals(extension)) {
            contentType = "application/vnd.ms-excel";
        } else if (".DOC".equals(extension)) {
            contentType = "application/msword";
        }
        return contentType;
    }

    public static String getFileExtension(String name) {
        int nLastIndexOf = name.lastIndexOf(".");
        String strFileExt = "";

        if (nLastIndexOf > -1) {
            strFileExt = name.substring(nLastIndexOf);
        }

        return strFileExt;
    }

    public static void downloadFileFromPath(HttpServletResponse response, String filePath, String fileName, Long fileLength) throws IOException {

        ServletOutputStream os = response.getOutputStream();
        FileInputStream fis = null;

        try {
            File file = FileUtils.getFilePath(filePath);

            if (!file.exists()) {
                throw new FileNotFoundException(
                        "[FileUtils.downloadFileFromPath throw Exception : File Not Exist]");
            }

            String ext = FileUtils.getFileExtension(fileName).toUpperCase();
            String contentType = getContentType(ext);

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Content-Length", "" + (fileLength != null ? fileLength : file.length()));

            fis = new FileInputStream(file);

            int readCount = 0;
            byte[] buf = new byte[1024];

            while ((readCount = fis.read(buf)) != -1) {
                os.write(buf, 0, readCount);
            }

            os.flush();

            response.setStatus(500);
        } catch (FileNotFoundException fe) {
            logger.debug("FileUtil.fileCopy : FileNotFoundException");
        } catch (IOException ie) {
            logger.debug("FileUtil.fileCopy : IOException");
        } finally {
            S2StreamUtil.closeStream(os);
            S2StreamUtil.closeStream(fis);
        }
    }

    public static void downloadFile(HttpServletResponse response, byte[] byteArray, String outputFileName, Long fileLength) throws IOException {

        try {
            String ext = FileUtils.getFileExtension(outputFileName).toUpperCase();
            String contentType = FileUtils.getContentType(ext);

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(outputFileName, "UTF-8"));
            response.setHeader("Content-Length", "" + fileLength);
            response.getOutputStream().write(byteArray);
            response.getOutputStream().flush();
            response.getOutputStream().close();
            response.setStatus(500);
        } catch (UnsupportedEncodingException uee) {
            response.getOutputStream().close();
        } catch (IOException e) {
            response.getOutputStream().close();
        } finally {
            response.getOutputStream().close();
        }
    }

    /**
     * 특정 파일을 특정 위치에 copy 처리.
     *
     * @param strSrc 원본 경로(파일 또는 디렉토리)
     * @param strTar 대상 경로(파일 또는 디렉토리) @
     */
    public static void nioFileCopy(String strSrc, String strTar) throws IOException {
        File srcFile = new File(strSrc);
        File tarFile = new File(strTar);

        if (!srcFile.exists()) {
            throw new IOException();
        }

        if (srcFile.isDirectory()) {
            if (!tarFile.exists()) {
                tarFile.mkdir();
            }

            File[] files = srcFile.listFiles();

            if (files != null) {
                for (File file : files) {
                    FileUtils.fileCopy(file.getPath(), strTar + "/" + file.getName());
                }
            }
        } else {
            if (!FileUtils.exists(tarFile.getParent())) {
                FileUtils.makeDirectory(tarFile.getParent());
            }

            FileInputStream in = null;
            FileOutputStream out = null;

            FileChannel fcin = null;
            FileChannel fcout = null;

            try {

                // Channel을 이용한 네이티브OS 기능 사용하기
                in = new FileInputStream(srcFile);
                out = new FileOutputStream(tarFile);

                fcin = in.getChannel();
                fcout = out.getChannel();

                long size = fcin.size();

                fcin.transferTo(0, size, fcout);
            } catch (FileNotFoundException fe) {
                logger.debug("FileUtil.fileCopy : FileNotFoundException");
            } catch (IOException ie) {
                logger.debug("FileUtil.fileCopy : IOException");
            } finally {
                S2StreamUtil.closeStream(fcout);
                S2StreamUtil.closeStream(fcin);
                S2StreamUtil.closeStream(in);
                S2StreamUtil.closeStream(out);
            }
        }
    } // fileCopy

    /**
     * UUID를 사용하여 고유한 파일 ID를 생성합니다.
     *
     * @return 생성된 파일 ID
     */
    public static String makeFileId() {
        return UUID.randomUUID().toString();
    }

    /**
     * UUID를 사용하여 접미사가 포함된 고유한 파일 ID를 생성합니다.
     *
     * @param suffix 파일 ID에 추가할 접미사
     * @return 접미사가 포함된 파일 ID
     */
    public static String makeFileId(String suffix) {
        return makeFileId() + (S2Util.isNotEmpty(suffix) ? "-" + suffix : "");
    }

    /**
     * MultipartFile의 파일명을 확장자를 제외하고 반환합니다.
     *
     * @param file MultipartFile 객체
     * @return 확장자를 제외한 파일명
     */
    public static @NonNull String getFileNm(MultipartFile file) {
        String ext = "";
        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                ext = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            }
        }
        return ext;
    }

    /**
     * MultipartFile의 확장자를 반환합니다.
     *
     * @param file MultipartFile 객체
     * @return 파일 확장자
     */
    public static @NonNull String getFileExt(MultipartFile file) {
        String ext = "";
        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }
        }
        return ext;
    }

    /**
     * 파일 ID가 유효한지 확인합니다.
     *
     * @param fileId 검사할 파일 ID
     * @return 파일 ID 유효성 여부
     */
    public static boolean checkFileId(String fileId) {
        return S2Util.isNotEmpty(fileId) && fileId.length() == 36;
    }

    /**
     * 단일 파일이 등록 가능한 파일인지 확인합니다.
     *
     * @param file       검사할 MultipartFile
     * @param chkFileExt 허용된 파일 확장자 배열
     * @return 파일 등록 가능 여부
     */
    public static boolean checkMultipartFile(MultipartFile file, String[] chkFileExt) {
        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file);
        return FileUtils.checkMultipartFileList(fileList, chkFileExt);
    }

    /**
     * 여러 파일이 등록 가능한 파일인지 확인합니다.
     *
     * @param fileList   검사할 MultipartFile 리스트
     * @param chkFileExt 허용된 파일 확장자 배열
     * @return 파일 등록 가능 여부
     * @throws S2RuntimeException 파일이 등록 불가능한 경우
     */
    public static boolean checkMultipartFileList(List<MultipartFile> fileList, String[] chkFileExt) {
        boolean result = false;
        if (fileList != null && !fileList.isEmpty() && !FileUtils.allEmptyFile(fileList)) {
            if (FileUtils.checkFileExtSafe(fileList, chkFileExt)) {
                result = true;
            } else {
                throw new S2RuntimeException("등록 가능한 파일이 아닙니다.");
            }
        } else {
            throw new S2RuntimeException("등록 가능한 파일이 없습니다.");
        }
        return result;
    }

    /**
     * MultipartFile 리스트에 빈 파일이 있는지 검사합니다.
     *
     * @param files 검사할 MultipartFile 리스트
     * @return 빈 파일 존재 여부
     */
    public boolean hasEmptyFile(final List<MultipartFile> files) {
        if (files == null) {
            return true;
        }
        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * MultipartFile 리스트의 모든 파일이 빈 파일인지 검사합니다.
     *
     * @param files 검사할 MultipartFile 리스트
     * @return 모든 파일이 빈 파일인지 여부
     */
    public static boolean allEmptyFile(final List<MultipartFile> files) {
        if (files == null) {
            return true;
        }

        int count = 0;
        int empty = 0;

        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                empty++;
            }
            count++;
        }

        return count == empty;
    }

    /**
     * 파일들의 확장자가 허용된 확장자 목록에 포함되어 있는지 검사합니다.
     *
     * @param files     검사할 MultipartFile 리스트
     * @param whiteExts 허용된 확장자 배열
     * @return 모든 파일의 확장자가 허용되었는지 여부
     * @throws S2RuntimeException 허용되지 않은 파일 타입인 경우
     */
    @SuppressWarnings("null")
    public static boolean checkFileExtSafe(final List<MultipartFile> files, String[] whiteExts) {
        for (MultipartFile file : files) {
            String originFileName = file != null ? file.getOriginalFilename() : "";

            /*
             * 원 파일명이 없는 경우 통과. (첨부가 되지 않은 input file type)
             */
            if (S2Util.isEmpty(originFileName)) {
                continue;
            }

            String contentType = file != null && file.getContentType() != null ? file.getContentType() : "";

            switch (contentType) {
                // case "application/octet-stream": // 확장자가 없거나 판단하지 못할때
                case "application/x-csh":
                case "application/java-archive":
                case "application/x-sh":
                case "application/js":
                case "application/x-javascript":
                case "text/html":
                case "text/javascript":
                    throw new S2RuntimeException("등록 가능한 파일이 아닙니다.");
            }

            boolean chkBlack = false;
            boolean chkWhite = false;

            int index = originFileName.lastIndexOf(".");
            String fileExt = originFileName.substring(index + 1);

            for (String ext : Constants.ARR_NOT_ALLOWED_EXT) {
                if (fileExt.equalsIgnoreCase(ext)) {
                    chkBlack = true;
                    break;
                }
            }

            if (!chkBlack && whiteExts != null) {
                for (String ext : whiteExts) {
                    if (fileExt.equalsIgnoreCase(ext)) {
                        chkWhite = true;
                        break;
                    }
                }
            }

            if (chkBlack || !chkWhite) {
                return false;
            }
        }

        return true;
    }

}
