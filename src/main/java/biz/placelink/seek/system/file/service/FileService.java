package biz.placelink.seek.system.file.service;


import biz.placelink.seek.com.util.FileUtils;
import biz.placelink.seek.com.util.SessionUtil;
import biz.placelink.seek.system.file.vo.FileDetailVO;
import biz.placelink.seek.system.file.vo.FileVO;
import kr.s2.ext.exception.S2RuntimeException;
import kr.s2.ext.file.FileManager;
import kr.s2.ext.util.S2Util;
import kr.s2.ext.util.vo.S2RemoteFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 02. 01.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private final FileMapper fileMapper;
    private final FileManager fileManager;

    public FileService(FileMapper fileMapper, FileManager fileManager) {
        this.fileMapper = fileMapper;
        this.fileManager = fileManager;
    }

    @Value("${file.root.path}")
    private String fileRootPath;

    /**
     * 파일 상세 목록을 조회한다.
     *
     * @param fileId 파일 ID
     * @return 파일 상세 정보
     */
    public List<FileDetailVO> selectFileDetailList(String fileId) {
        return fileMapper.selectFileDetailList(fileId, null, null);
    }

    /**
     * 파일 상세 목록을 조회한다.
     *
     * @param fileId       파일 ID
     * @param fileDetailId 파일 상세 ID
     * @return 파일 상세 정보
     */
    public List<FileDetailVO> selectFileDetailList(String fileId, String fileDetailId) {
        return fileMapper.selectFileDetailList(fileId, fileDetailId, null);
    }

    /**
     * 파일 상세 목록을 조회한다.
     *
     * @param fileId 파일 ID
     * @param sortSn 정렬 순번
     * @return 파일 상세 정보
     */
    public List<FileDetailVO> selectFileDetailList(String fileId, Integer sortSn) {
        return fileMapper.selectFileDetailList(fileId, null, sortSn);
    }

    /**
     * 파일 상세를 조회한다.
     *
     * @param fileId 파일 ID
     * @return 파일 상세 정보
     */
    public FileDetailVO selectFileDetail(String fileId) {
        return fileMapper.selectFileDetail(fileId);
    }


    /**
     * 파일 정보를 등록한다.
     *
     * @param paramVO 파일 정보
     * @return 등록된 파일 정보 개수
     */
    public int insertFile(FileVO paramVO) {
        if (paramVO.getCreateUid() == null) {
            // paramVO.setCreateUid(SessionUtil.getSessionUserUid());
            paramVO.setCreateUid("");
        }
        return fileMapper.insertFile(paramVO);
    }

    /**
     * 파일 상세 정보를 등록한다.
     *
     * @param paramVO 파일 상세 정보
     * @return 등록된 파일 상세 정보 개수
     */
    public int insertFileDtl(FileDetailVO paramVO) {
        if (paramVO.getCreateUid() == null) {
            // paramVO.setCreateUid(SessionUtil.getSessionUserUid());
            paramVO.setCreateUid("");
        }
        return fileMapper.insertFileDtl(paramVO);
    }

    /**
     * 파일 상세 정보를 포함한 파일 정보를 등록한다.
     *
     * @param fileDetail 파일 상세 정보
     * @param createUid  생성자 식별자
     * @return 등록된 파일 상세 정보 개수
     */
    public int insertFileWithDetail(FileDetailVO fileDetail, String createUid) {
        List<FileDetailVO> fileDetailList = new ArrayList<>();
        fileDetailList.add(fileDetail);
        return insertFileWithDetailList(fileDetailList, createUid);
    }

    /**
     * 파일 상세 정보를 포함한 파일 정보 목록을 등록한다.
     *
     * @param fileDetailList 파일 상세 정보 목록
     * @param createUid      생성자 식별자
     * @return 등록된 파일 상세 정보 개수
     */
    public int insertFileWithDetailList(List<FileDetailVO> fileDetailList, String createUid) {
        int result = 0;
        if (fileMapper.insertFileList(fileDetailList, createUid) > 0) {
            result = fileMapper.insertFileDetailList(fileDetailList, createUid);
        }
        return result;
    }

    /**
     * 파일 작성
     *
     * @param file           파일
     * @param fileId         파일 ID
     * @param fileSeCcd      파일 구분 공통코드
     * @param saveNameSuffix 저장명 접미사
     * @param chkFileExt     등록 가능한 파일 확장자
     * @param maxSize        등록 가능한 파일 최대 크기
     * @return 등록한 파일 상세 정보
     */
    @Transactional
    public FileDetailVO writeFile(MultipartFile file, String fileId, String fileSeCcd, String saveNameSuffix, String[] chkFileExt, Long maxSize) {
        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file);
        List<FileDetailVO> successFileList = this.writeFile(fileList, fileId, fileSeCcd, saveNameSuffix, chkFileExt, maxSize);
        return S2Util.isNotEmpty(successFileList) ? successFileList.get(0) : null;
    }

    /**
     * 파일 목록 작성
     *
     * @param fileList       파일 목록
     * @param fileId         파일 ID
     * @param fileSeCcd      파일 구분 공통코드
     * @param saveNameSuffix 저장명 접미사
     * @param chkFileExt     등록 가능한 파일 확장자
     * @param maxSize        등록 가능한 파일 최대 크기
     * @return 등록한 파일 상세 정보 목록
     */
    @Transactional
    public List<FileDetailVO> writeFile(List<MultipartFile> fileList, String fileId, String fileSeCcd, String saveNameSuffix, String[] chkFileExt, Long maxSize) {
        List<FileDetailVO> successFileList = new ArrayList<>();
        boolean isFist = true;

        long maxSizeLimit = maxSize * 1024 * 1024;

        // 다중파일 업로드
        if (FileUtils.checkMultipartFileList(fileList, chkFileExt)) {
            // String sessionUserUid = SessionUtil.getSessionUserUid();
            String sessionUserUid = "";

            if (FileUtils.checkFileId(fileId)) {
                // 추가 등록
                isFist = false;
            } else {
                // 신규 등록
                fileId = FileUtils.makeFileId();
            }

            List<FileDetailVO> fileDtlList = new ArrayList<>();

            boolean validSize = true;

            for (MultipartFile file : fileList) {
                if (file.getSize() > maxSizeLimit) {
                    validSize = false;
                    break;
                }

                try (InputStream fileData = file.getInputStream()) {
                    FileDetailVO fileDtlVO = this.writeFile(fileData, fileSeCcd, saveNameSuffix);
                    if (fileDtlVO != null) {
                        fileDtlVO.setFileId(fileId);
                        fileDtlVO.setFileName(FileUtils.getFileNm(file));
                        fileDtlVO.setFileExt(FileUtils.getFileExt(file));
                        fileDtlVO.setFileSize(file.getSize());
                        fileDtlVO.setContentType(file.getContentType());
                        fileDtlVO.setCreateUid(sessionUserUid);

                        fileDtlList.add(fileDtlVO);
                    }
                } catch (IOException e) {
                    throw new S2RuntimeException("파일 등록에 실패했습니다.");
                }
            }

            if (!validSize) {
                // 업로드 도중 비정상 파일이 있다면 이미 작성한 파일 삭제
                FileUtils.deleteFileList(fileDtlList);
                throw new S2RuntimeException(MessageFormat.format("첨부파일은 최대 {0}MB를 초과할 수 없습니다.", maxSize));
            } else {
                try {
                    if (isFist) {
                        // 최초 등록이라면 파일 정보를 등록한다.
                        FileVO fileInfo = new FileVO();
                        fileInfo.setFileId(fileId);
                        fileInfo.setFileSeCcd(fileSeCcd);
                        fileInfo.setCreateUid(sessionUserUid);

                        this.insertFile(fileInfo);
                    }

                    for (FileDetailVO fileDtlInfo : fileDtlList) {
                        fileDtlInfo.setFileDetailId(FileUtils.makeFileId());
                        this.insertFileDtl(fileDtlInfo);
                        successFileList.add(fileDtlInfo);
                    }
                } catch (Exception e) {
                    FileUtils.deleteFileList(fileDtlList);
                    throw new S2RuntimeException("파일 등록에 실패했습니다.");
                }
            }
        }

        return successFileList;
    }

    /**
     * 파일 작성
     *
     * @param fileData       파일 데이터(InputStream)
     * @param fileSeCcd      파일 구분 공통코드
     * @param saveNameSuffix 저장명 접미사
     * @return 작성 파일 상세 정보
     */
    public FileDetailVO writeFile(InputStream fileData, String fileSeCcd, String saveNameSuffix) {
        FileDetailVO result = null;
        String savePath = S2Util.joinPaths(fileRootPath, fileSeCcd, new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date()));
        String saveName = FileUtils.makeFileId(saveNameSuffix);
        if (fileManager.writeFile(fileData, savePath, saveName)) {
            result = new FileDetailVO();
            result.setSavePath(savePath);
            result.setSaveName(saveName);
        }
        return result;
    }

    /**
     * 파일을 다운로드 하고 해당 정보를 리턴한다.
     *
     * @param fileSeCcd 파일 구분
     * @param fileUrl   파일 경로
     * @param fileId    파일 ID
     */
    public FileDetailVO downloadRemoteFile(String fileSeCcd, String fileUrl, String fileId) throws IOException {
        FileDetailVO fileDtlVO = null;

        if (S2Util.isNotEmpty(fileUrl)) {
            try {
                URL url = new URI(fileUrl).toURL();

                try (InputStream fileData = url.openStream()) {
                    String savePath = S2Util.joinPaths(fileRootPath, fileSeCcd, new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date()));
                    String saveName = FileUtils.makeFileId(fileSeCcd);

                    if (fileManager.writeFile(fileData, savePath, saveName)) {
                        S2RemoteFile remoteFileInfo = new S2RemoteFile(url);
                        fileDtlVO = new FileDetailVO();

                        fileDtlVO.setFileId(S2Util.isNotEmpty(fileId) ? fileId : FileUtils.makeFileId());
                        fileDtlVO.setFileSeCcd(fileSeCcd);
                        fileDtlVO.setFileDetailId(FileUtils.makeFileId());
                        fileDtlVO.setFileName(remoteFileInfo.getName());
                        fileDtlVO.setFileExt(remoteFileInfo.getExtension());
                        fileDtlVO.setFileSize(remoteFileInfo.getSize());
                        fileDtlVO.setContentType(remoteFileInfo.getContentType());
                        fileDtlVO.setSavePath(savePath);
                        fileDtlVO.setSaveName(saveName);
                    }
                }
            } catch (URISyntaxException | FileNotFoundException e) {
                logger.error("원격 파일을 찾을 수 없습니다. {}", fileUrl, e);
            }
        }

        return fileDtlVO;
    }

}