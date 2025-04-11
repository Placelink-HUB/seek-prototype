package biz.placelink.seek.system.file.service;

import biz.placelink.seek.system.file.vo.FileDetailVO;
import biz.placelink.seek.system.file.vo.FileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
@Mapper
public interface FileMapper {

    /**
     * 파일 상세 목록을 조회한다.
     *
     * @param fileId       파일 ID
     * @param fileDetailId 파일 상세 ID
     * @param sortSn       정렬 순번
     * @return 파일 상세 정보
     */
    List<FileDetailVO> selectFileDetailList(@Param("fileId") String fileId, @Param("fileDetailId") String fileDetailId, @Param("sortSn") Integer sortSn);


    /**
     * 파일 상세를 조회한다.
     *
     * @param fileId 파일 ID
     * @return 파일 상세 정보
     */
    FileDetailVO selectFileDetail(@Param("fileId") String fileId);

    /**
     * 파일 정보를 등록한다.
     *
     * @param paramVO 파일 정보
     * @return 등록된 파일 정보 개수
     */
    int insertFile(FileVO paramVO);

    /**
     * 파일 상세 정보를 등록한다.
     *
     * @param paramVO 파일 상세 정보
     * @return 등록된 파일 상세 정보 개수
     */
    int insertFileDtl(FileDetailVO paramVO);

    /**
     * 파일 정보 목록을 등록한다.
     *
     * @param fileList  파일 정보 목록
     * @param createUid 생성자 식별자
     * @return 등록된 파일 정보 개수
     */
    int insertFileList(@Param("fileList") List<FileDetailVO> fileList, @Param("createUid") String createUid);

    /**
     * 파일 상세 정보 목록을 등록한다.
     *
     * @param fileDetailList 파일 상세 정보 목록
     * @param createUid      생성자 식별자
     * @return 등록된 파일 정보 개수
     */
    int insertFileDetailList(@Param("fileDetailList") List<FileDetailVO> fileDetailList, @Param("createUid") String createUid);

}