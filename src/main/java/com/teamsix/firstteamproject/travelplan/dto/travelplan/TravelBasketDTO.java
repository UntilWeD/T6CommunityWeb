package com.teamsix.firstteamproject.travelplan.dto.travelplan;

import com.teamsix.firstteamproject.travelplan.entity.TravelBasket;
import com.teamsix.firstteamproject.travelplan.entity.TravelPlan;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class TravelBasketDTO {

    private Long id;
    private List<BasketItemDTO> basketItems;

    public TravelBasket toEntity(){
        return TravelBasket.builder()
                .id(this.id)
                .basketItems(this.basketItems != null ?
                        this.basketItems.stream()
                        .map(BasketItemDTO::toEntity)
                        .collect(Collectors.toList()) : null
                ).build();
    }

}


//    // 알맞은 파일 이름 이미지와 URL 매핑하기
//    public List<String> mappingImageNameAndUrl(List<String> imageUrls){
//        Map<String, String> imageNametoUrlMap = new HashMap<>();
//        Map<String, String> imageNametoStoredImageMap = new HashMap<>();
//        List<String> result = new ArrayList<>();
//
//        //이미지 이름과 URL을 매핑
//        for (String url : imageUrls){
//            String originalFileName = extractOriginalFileName(url);
//            imageNametoUrlMap.put(originalFileName, url);
//            imageNametoStoredImageMap.put(originalFileName, extractFileName(url));
//            result.add(extractFileName(url));
//        }
//
//
//        //BasketItem의 이미지 이름과 매칭되는 URL 설정
//        for(BasketItemDTO item: basketItems){
//            String matchedUrl = imageNametoUrlMap.get(item.getImageName());
//            String matchedFileName = imageNametoStoredImageMap.get(item.getImageName());
//            if(matchedUrl != null){
//                item.setImageUrl(matchedUrl);
//                item.setImageName(matchedFileName);
//            }
//        }
//
//
//        return result;
//    }
//    private String extractOriginalFileName(String url){
//        return url.substring(url.lastIndexOf('-') + 1, url.lastIndexOf("."));
//    }
//
//    private String extractFileName(String url){
//        return url.substring(url.lastIndexOf('/') + 1);
//    }