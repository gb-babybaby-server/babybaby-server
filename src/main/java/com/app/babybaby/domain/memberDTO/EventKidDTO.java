package com.app.babybaby.domain.memberDTO;

import com.app.babybaby.domain.calendarDTO.CalendarDTO;
import com.app.babybaby.domain.fileDTO.eventFileDTO.EventFileDTO;
import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.calendar.Calendar;
import com.app.babybaby.entity.embeddable.Address;
import com.app.babybaby.entity.member.Kid;
import com.app.babybaby.type.CategoryType;
import com.app.babybaby.type.GenderType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@RequiredArgsConstructor
public class EventKidDTO {
    private Long eventId;
    private String boardTitle;
    private String boardContent;
    private Long eventRecruitCount;
    private Address eventLocation;
    private Long eventPrice;
    private CategoryType category;
    private Long kidId;
    private String kidName;
    private Integer kidAge;
    private GenderType kidGender;
    private CalendarDTO calendarDTO;
    private List<EventFileDTO> files;

    @Builder
    public EventKidDTO(Long eventId, String boardTitle, String boardContent, Long eventRecruitCount, Address eventLocation, Long eventPrice, CategoryType category, Long kidId, String kidName, Integer kidAge, GenderType kidGender, CalendarDTO calendarDTO, List<EventFileDTO> files) {
        this.eventId = eventId;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.eventRecruitCount = eventRecruitCount;
        this.eventLocation = eventLocation;
        this.eventPrice = eventPrice;
        this.category = category;
        this.kidId = kidId;
        this.kidName = kidName;
        this.kidAge = kidAge;
        this.kidGender = kidGender;
        this.calendarDTO = calendarDTO;
        this.files = files;
    }

    //    @QueryProjection
//    public EventKidDTO(Event event, Kid kid) {
//        this.eventId = event.getId();
//        this.boardTitle = event.getBoardTitle();
//        this.boardContent = event.getBoardContent();
//        this.eventRecruitCount = event.getEventRecruitCount();
//        this.eventLocation = event.getEventLocation();
//        this.eventPrice = event.getEventPrice();
//        this.category = event.getCategory();
//
//        this.kidId = kid.getId();
//        this.kidName = kid.getKidName();
//        this.kidAge = kid.getKidAge();
//        this.kidGender = kid.getKidGender();
//    }



}
