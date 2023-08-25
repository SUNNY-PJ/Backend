package com.sunny.backend.service.community;

import com.sunny.backend.dto.request.CommunityRequest;
import com.sunny.backend.dto.response.CommunityResponse;
import com.sunny.backend.dto.response.PageResponse;
import com.sunny.backend.dto.response.Response;
import com.sunny.backend.entity.BoardType;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Photo;

import com.sunny.backend.entity.SearchType;
import com.sunny.backend.repository.photo.PhotoRepository;
import com.sunny.backend.repository.community.CommunityRepository;
import com.sunny.backend.service.s3.S3Service;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor //쓰임새에 대해
@Slf4j
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final PhotoRepository photoRepository;
    private final Response response;
    private final UserRepository usersRepository;

    private String uploadPath="/Users/eom-yelim/Downloads/backend/fileStore";

    private final S3Service s3Service;
    private static final String COMMUNITY_VIEW_COUNT_KEY = "community:viewCount:";
    private static final int EXPIRATION_DAYS = 1;
    private final RedisTemplate redisTemplate;

    public class RedisUtil {
        public static long getUnixTime(LocalDateTime localDateTime) {
            return localDateTime.toEpochSecond(ZoneOffset.UTC);
        }
    }



    //검색 기능 (제목, 제목 + 내용, 작성자 ) , 정렬 기능 (최신순, 좋아요 많은 순) , 카테고리 클릭
    public PageImpl<PageResponse> getCommunityList(Pageable pageable) {
        PageImpl<PageResponse> result = communityRepository.getCommunityList(pageable);
        return result;


    }

    public PageImpl<PageResponse> getPageListWithSearch(BoardType boardType, SearchType searchCondition, Pageable pageable){
        PageImpl<PageResponse> result = communityRepository.getQuestionListPageWithSearch(boardType, searchCondition, pageable);
        return result;
    }


    public ResponseEntity findById(Users users, Long communityId) {
        try {
            String contestViewCountKey = COMMUNITY_VIEW_COUNT_KEY + communityId;
            Community community = communityRepository.findById(communityId).orElseThrow(() -> new RuntimeException("Community post not found"));
            HashOperations<String, String, Long> hashOps = redisTemplate.opsForHash();

            // 현재 시간을 UTC 기준으로 계산
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            LocalDateTime expirationTime = now.plusDays(EXPIRATION_DAYS);

            long unixTime = RedisUtil.getUnixTime(expirationTime);
            long viewCount = 0;

            // Redis hash에 사용자 이름과 만료 시간을 저장
            boolean isNewUser = hashOps.putIfAbsent(contestViewCountKey, users.getName(), expirationTime.toEpochSecond(ZoneOffset.UTC));
            if (!isNewUser) {
                // 이미 사용자가 저장되어 있으면 만료 시간을 가져옴
                Long expirationTimestamp = hashOps.get(contestViewCountKey, users.getName());
                if (expirationTimestamp != null && expirationTimestamp < now.toEpochSecond(ZoneOffset.UTC)) {
                    // 저장된 만료 시간이 지났으면 사용자 아이디와 만료 시간을 갱신
                    System.out.println("시간 만료");
                    hashOps.put(contestViewCountKey, users.getName(), expirationTime.toEpochSecond(ZoneOffset.UTC));
                } else {
                    // 아직 만료 시간이 지나지 않았으면 조회수를 증가시키지 않음
                     community = communityRepository.findById(communityId).orElseThrow(() -> new RuntimeException("Community post not found"));
                    System.out.println(community.getView_cnt());

                }
            } else {
                // 새로운 사용자라면 만료 시간(하루) 설정
                redisTemplate.expireAt(contestViewCountKey, Instant.ofEpochSecond(unixTime));
                viewCount = hashOps.increment(contestViewCountKey, "viewCount", 1L);


                community = communityRepository.findById(communityId).orElseThrow(() -> new RuntimeException("Community post not found"));
                community.setView_cnt((int) viewCount); //setter 없이 사용?
                communityRepository.save(community);
                System.out.println(community.getView_cnt());
              //  return response.success(new CommunityResponse(community), "커뮤니티 상세 글 확인", HttpStatus.OK);
            }
            return response.success(new CommunityResponse(community), "커뮤니티 상세 글 확인", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail("커뮤니티 상세 글 확인 실패", HttpStatus.BAD_REQUEST);
        }


        // 조회수를 증가시키고 결과를 반환

    }


    //커뮤니티 게시글 생성
    @Transactional
    public ResponseEntity createCommunity(Users users, CommunityRequest communityRequest, List<String> files) throws IOException {
        try {
            Community community = Community.builder()
                    .title(communityRequest.getTitle())
                    .writer(users.getName())
                    .contents(communityRequest.getContents())
                    .boardType(communityRequest.getCategory())
                    .users(users)
                    .build();

            Community saveCommunity=communityRepository.save(community);
            Users saveUsers = usersRepository.findById(users.getId()).get(); // 이 작업이 필요한 작업인지?
            saveUsers.getCommunityList().add(community);

            List<String> photoList=new ArrayList<>();

            for (String file : files) {
                Photo photo =new Photo(file,community);
                photoRepository.save(photo);
                saveCommunity.writePhoto(photo);
                photoList.add(photo.getFileUrl());

            }


            return response.success(new CommunityResponse(community), "컨테스트 글 등록 성공", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail(e,"컨테스트 글 등록 실패",HttpStatus.BAD_REQUEST);
        }
    }



    //수정
    @Transactional
    public ResponseEntity updateCommunity(Users users, Long communityId, CommunityRequest communityRequest , List<String> files) throws IOException {
        try {
            Community community = communityRepository.findById(communityId).orElseThrow(() -> new IllegalArgumentException("Community not found!"));

            if (!checkCommunityLoginUser(users, community)) {
                return response.fail("커뮤니티 수정 실패", HttpStatus.UNAUTHORIZED);
            }

            community.setTitle(communityRequest.getTitle());
            community.setContents(communityRequest.getContents());
            community.setBoardType(communityRequest.getCategory());
            communityRepository.save(community);

            if (!files.isEmpty()) {
                List<Photo> existingPhotos = photoRepository.findByCommunityId(communityId);

                // 기존 photo 삭제
                photoRepository.deleteAll(existingPhotos);

                List<Photo> newPhotos = new ArrayList<>();
                for (String file : files) {
                    Photo newPhoto = new Photo(file, community);
                    newPhotos.add(newPhoto);
                }

                // 새로운 사진 등록
                photoRepository.saveAll(newPhotos);
                community.setPhotoList(newPhotos);
            }

            Users saveUsers = usersRepository.findById(users.getId()).orElseThrow(() -> new IllegalArgumentException("User not found!"));
            saveUsers.getCommunityList().add(community);
            usersRepository.save(saveUsers);

            return response.success(new CommunityResponse(community), "Community 업데이트 성공", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail("community 업데이트 실패", HttpStatus.BAD_REQUEST);

        }
    }

    //삭제
    @Transactional
    public ResponseEntity deleteCommunity(Users users, Long communityId) {
        try {
            Community community = communityRepository.findById(communityId).orElseThrow(() -> new IllegalArgumentException(String.format("community is not Found!")));
            List<Photo> photo = photoRepository.findByCommunityId(communityId);
            if (checkCommunityLoginUser(users, community)) {

                for (Photo existingFile : photo) {
                    s3Service.deleteFile(existingFile.getFileUrl());
                }

                photoRepository.deleteByCommunityId(communityId);
                communityRepository.deleteById(communityId);
            }
            return response.success(new CommunityResponse(community), "커뮤니티 글 삭제 성공", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail("커뮤니티 글 삭제 실패",HttpStatus.BAD_REQUEST);
        }
    }



    //수정 및 삭제 권한 체크
    private boolean checkCommunityLoginUser(Users users, Community community) {
        if (!Objects.equals(community.getUsers().getName(), users.getName())) {
            return false;
        }
        return true;

    }

}
