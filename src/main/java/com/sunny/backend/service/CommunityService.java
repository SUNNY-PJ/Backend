package com.sunny.backend.service;

import com.sunny.backend.dto.request.CommunityRequest;
import com.sunny.backend.dto.response.CommunityResponse;
import com.sunny.backend.dto.response.PageResponse;
import com.sunny.backend.dto.response.Response;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Photo;

import com.sunny.backend.repository.PhotoRepository;
import com.sunny.backend.repository.community.CommunityRepository;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor //쓰임새에 대해
@Slf4j
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final PhotoRepository photoRepository;
    private final Response response;
    private final UserRepository usersRepository;
    private String uploadPath="/Users/eom-yelim/Downloads/backend/fileStore";

    //커뮤니티 게시판 조회
    public ResponseEntity getCommunityList(Pageable pageable, String title, String contents) {
        try {
            List<PageResponse> communityResponses = communityRepository.findPageCommunity(pageable, title, contents);
            return response.success(communityResponses, "커뮤니티 페이지 조회 성공", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail("커무니티 페이지 조회 실패", HttpStatus.BAD_REQUEST);
        }

    }
    //커뮤니티 게시글 생성
//    @Transactional
    public ResponseEntity createContest(Users users, CommunityRequest communityRequest, List<MultipartFile> files) throws IOException {
        try {
            Community community = Community.builder()
                    .title(communityRequest.getTitle())
                    .writer(communityRequest.getWriter())  //유저 값 생기면 수정 .users.getName()
                    .contents(communityRequest.getContents())
                    .users(users)
                    .build();

            Community saveCommunity=communityRepository.save(community);
            Users saveUsers = usersRepository.findById(users.getId()).get(); // 이 작업이 필요한 작업인지?
            saveUsers.getContestList().add(community);
            List<String> photoList=new ArrayList<>();

            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename();
                String savedFileName=createSaveFileName(file.getOriginalFilename());
                File saveFile = new File(uploadPath, savedFileName);
                file.transferTo(saveFile);

                log.info(file.getOriginalFilename());
                Photo photo = Photo.builder()
                        .filename(originalName)
                        .savedFileName(savedFileName)
                        .fileSize(file.getSize())
                        .build();
                saveCommunity.writePhoto(photo); //photoname 바로 전달?
                photoList.add(photo.getSavedFileName());
            }


            return response.success(new CommunityResponse(community), "컨테스트 글 등록 성공", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail(e,"컨테스트 글 등록 실패",HttpStatus.BAD_REQUEST);
        }
    }
    private String createSaveFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }
    // 확장자명 구하기
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    // fullPath
    private String getFullPath(String filename) {
        return uploadPath + filename;
    }

    @Transactional
    public ResponseEntity updateCommunity(Long communityId, CommunityRequest communityRequest) throws IOException {

        try {
            Community community = communityRepository.findById(communityId).orElseThrow(() -> new IllegalArgumentException(String.format("community is not Found!")));
            List<Photo> photo = photoRepository.findByCommunityId(communityId);
//            if (checkCommunityLoginUser(users, contest)) {
//                community.setTitle(communityRequest.getTitle());
//                community.setWriter(users.getName());
//                community.setContents(communityRequest.getContents());
//            }

//            usersRepository.save(users);
            return response.success(new CommunityResponse(community), "커뮤니티 글 수정 성공", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail("컨테스트 글 수정 실패",HttpStatus.BAD_REQUEST);
        }
    }

    //삭제
    @Transactional
    public ResponseEntity deleteCommunityById(Users users, Long communityId) {
        try {
            Community community = communityRepository.findById(communityId).orElseThrow(() -> new IllegalArgumentException(String.format("stydy is not Found!")));
            List<Photo> photo = photoRepository.findByCommunityId(communityId);
            if (checkCommunityLoginUser(users, community)) {
//
//                for (Photo existingFile : photo) {
//                    s3Service.deleteFile(existingFile.getFileUrl()); // - fileStore를 비우는 걸로 해야할 듯ㅎ
//                }

                photoRepository.deleteByCommunityId(communityId);
                communityRepository.deleteById(communityId);
            }
            return response.success(new CommunityResponse(community), "컨테스트 글 삭제 성공", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail("컨테스트 글 삭제 실패",HttpStatus.BAD_REQUEST);
        }
    }

    //수정 및 삭제 권한 체크
    private boolean checkCommunityLoginUser(Users users, Community community) {
//        if (!Objects.equals(community.getUsers().getName(), users.getName())) {
//            return false;
//        }
        return true;

    }

}
