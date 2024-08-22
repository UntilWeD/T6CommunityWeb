package com.teamsix.firstteamproject.travelplan.dto.travelplan;


import com.teamsix.firstteamproject.travelplan.entity.TravelPlan;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TravelPlanDTO {

    @NotBlank
    private Long userId;

    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private Date createdAt;

    private TravelBasketDTO travelBasket;


    // DTO -> Entity
    public TravelPlan toEntity(TravelPlanDTO travelPlanDTO){
        return TravelPlan.builder()

                .build();
    }



//    private String destination;
//    private String description;
//    private LocalDate startDate;
//    private LocalDate endDate;
}
