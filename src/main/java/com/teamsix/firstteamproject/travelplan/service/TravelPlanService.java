package com.teamsix.firstteamproject.travelplan.service;

import com.teamsix.firstteamproject.travelplan.dto.travelplan.BasketItemDTO;
import com.teamsix.firstteamproject.travelplan.dto.travelplan.TravelPlanDTO;
import com.teamsix.firstteamproject.travelplan.entity.TravelPlan;
import com.teamsix.firstteamproject.travelplan.repository.TravelPlanRepository;
import com.teamsix.firstteamproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TravelPlanService {

    private final AwsS3Service awsS3Service;
    private final TravelPlanRepository travelPlanRepository;
    private final UserRepository userRepository;

    public TravelPlanService(AwsS3Service awsS3Service, TravelPlanRepository travelPlanRepository,
                             UserRepository userRepository) {
        this.awsS3Service = awsS3Service;
        this.travelPlanRepository = travelPlanRepository;
        this.userRepository = userRepository;
    }

    /**
     * ====== 저장 과정 ======
     * 1. 변환작업 후 AwsS3Serice에서 이미지리스트를 저장 한 후 이미지 URL 반환
     * 2. 반환된 이미지 URL을 basketItem의 ImageUrl속성에 넣음
     * 3. imageUrl을 포함한 travePlanDTO의 .toentity Method를 실행한 다음 엔티티 반환
     * 4. 반환된 엔티티를 TravelPlanRepository에 저장
     * 5. 기존의 엔티티객체를 그대로 반환 (후에 변경)
     * @param
     * @return
     */
    public TravelPlanDTO saveTravelPlan(List<MultipartFile> images, Long userId, TravelPlanDTO travelPlanDTO){
        List<BasketItemDTO> basketItems = travelPlanDTO.getTravelBasket().getBasketItems();
        List<String> imageUrls = awsS3Service.uploadImageList(images, userId);

        travelPlanDTO.getTravelBasket().mappingImageNameAndUrl(imageUrls);

        TravelPlan travelPlan = travelPlanDTO.toEntity(travelPlanDTO, userRepository.findUserById(userId));
        return TravelPlan.toDto(travelPlanRepository.save(travelPlan));
    }

    public List<TravelPlanDTO> getTravelPlans(Long userId) {
        return travelPlanRepository.findByUser_Id(userId)
                .stream().map(travelPlan -> TravelPlan.toDto(travelPlan))
                .collect(Collectors.toList());
    }

    /**
     * ====== 삭제 과정 ======
     * 1. s3서비스를 통하여 이미지 삭제
     * 2. db에서 삭제
     * @param travelPlanId
     */
    public void deleteTravelPlan(Long travelPlanId) {
        //[리팩토링 필수] 나중에 다른 repository를 생성하던가 구조를 바꾸어 해결해야함
        TravelPlan travelPlan = travelPlanRepository.findById(travelPlanId).get();
        List<String> fileNames = travelPlan.getTravelBasket().getBasketItems()
                .stream().map(basketItem -> basketItem.getImageName())
                .collect(Collectors.toList());
        awsS3Service.deleteImage(fileNames, travelPlan.getUser().getId());
        travelPlanRepository.delete(travelPlan);
        return;
    }

    /**
     * === 수정 과정 ===
     * 이미지 처리
     * 1. basketItem의 이미지 이름들과 저장소 내에 이미지파일들을 비교
     * 2. 만약 basketItems 이미지 이름이 없을 경우 삭제리스트에 추가하여 해당 이미지들을 삭제
     * 3. 새로운 images 업로드 ( 이미지 원본파일이 오는 것은 새로 추가된 이미지 밖에 없기 때문)
     * 객체 처리
     * 1. findById로 TravelPlan객체 조회
     * 2. 가져온 dto와 객체와 데이터를 비교 후 수정
     * 3. 수정하면 영속성 컨텍스트에 있기때문에 자동으로 수정
     * @param images
     * @param travelPlanDTO
     * @return
     */
    public TravelPlanDTO updateTravelPlan(List<MultipartFile> images, TravelPlanDTO travelPlanDTO) {
        // 기존의 TravlPlanDTO의 basketItem이미지이름리스트와 현재 저장소의 이미지이름리스트 비교
        List<String> imageNames = travelPlanDTO.getTravelBasket().getBasketItems()
                .stream().map(basketItemDTO -> basketItemDTO.getImageName()).collect(Collectors.toList());
        List<String> preImageNames = awsS3Service.getImageListInFolder(travelPlanDTO.getUserId());




        TravelPlan travelPlan = travelPlanRepository.findById(travelPlanDTO.getId()).get();

        return TravelPlanDTO.builder().build();
    }

}
