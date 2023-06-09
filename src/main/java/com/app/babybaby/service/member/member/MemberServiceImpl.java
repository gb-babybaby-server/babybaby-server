package com.app.babybaby.service.member.member;

import com.app.babybaby.controller.provider.UserDetail;
import com.app.babybaby.domain.boardDTO.eventDTO.EventDTO;
import com.app.babybaby.domain.boardDTO.parentsBoardDTO.ParentsBoardDTO;
import com.app.babybaby.domain.boardDTO.reviewDTO.ReviewDTO;
import com.app.babybaby.domain.memberDTO.CompanyDTO;
import com.app.babybaby.domain.memberDTO.MailDTO;
import com.app.babybaby.domain.memberDTO.MemberDTO;
import com.app.babybaby.domain.memberDTO.MemberDetailDTO;
import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.board.parentsBoard.ParentsBoard;
import com.app.babybaby.entity.board.review.Review;
import com.app.babybaby.entity.member.Member;
import com.app.babybaby.entity.purchase.coupon.Coupon;
import com.app.babybaby.repository.board.event.EventRepository;
import com.app.babybaby.repository.board.parentsBoard.ParentsBoardRepository;
import com.app.babybaby.repository.board.review.ReviewRepository;
import com.app.babybaby.repository.member.follow.FollowRepository;
import com.app.babybaby.repository.member.member.MemberRepository;
import com.app.babybaby.repository.purchase.coupon.CouponRepository;
import com.app.babybaby.service.board.parentsBoard.ParentsBoardService;
import com.app.babybaby.service.board.review.ReviewService;
import com.app.babybaby.type.AcceptanceType;
import com.app.babybaby.type.MemberType;
import com.app.babybaby.type.Role;
import com.app.babybaby.type.SleepType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Qualifier("member") @Primary
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final ReviewService reviewService;

    private final MemberRepository memberRepository;

    private final ReviewRepository reviewRepository;

    private final EventRepository eventRepository;

    private final FollowRepository followRepository;

//    private final CouponRepository couponRepository;

    private final ParentsBoardService parentsBoardService;

    private final ParentsBoardRepository parentsBoardRepository;


    @Autowired
    private JavaMailSender mailSender;

    @Override
    public Optional<Member> getMemberById(Long memberId) {
        return memberRepository.findById(memberId);

    }
//    [회원 상세] 회사의 정보 가져오기
    @Override
    public CompanyDTO getAllCompanyInfo(Long companyId) {
        Member member = memberRepository.findById(companyId).get();
        CompanyDTO companyDTO = toCompanyDTO(member);

        List<ReviewDTO> reviews = companyDTO.getEvents().stream()
                .flatMap(eventDTO -> reviewRepository.findAllReivewByEventId(eventDTO.getId()).stream())
                .map(reviewService::ReviewToDTO)
                .collect(Collectors.toList());
        companyDTO.setReviews(reviews);

        List<EventDTO> finishedEvents = eventRepository.findAllFinishedEvents_QueryDSL(companyId, Pageable.ofSize(2)).stream()
                .map(this::eventToDTO)
                .collect(Collectors.toList());
        List<EventDTO> upcommingEvents = eventRepository.findAllUpcommingEvents_QueryDSL(companyId, Pageable.ofSize(2)).stream()
                .map(this::eventToDTO)
                .collect(Collectors.toList());
        List<EventDTO> nowEvents = eventRepository.findAllNowEvents_QueryDSL(companyId, Pageable.ofSize(2)).stream()
                .map(this::eventToDTO)
                .collect(Collectors.toList());

        companyDTO.setFinishedEvents(finishedEvents);
        companyDTO.setUpcommingEvents(upcommingEvents);
        companyDTO.setNowEvents(nowEvents);
        companyDTO.setTotalReviewCount(reviewRepository.countAllReviewsByMemberId_QueryDSL(companyId));
        companyDTO.setTotalEventsCount(eventRepository.findAllFinishedEventsCount(companyId));

        return companyDTO;
    }
    
//    [회원 상세] 유저의 회사타입이 아닌 일반 유저 상세페이지
    public MemberDTO getAllUserInfo(Long memberId, Long sessionId){
        Member member = memberRepository.findById(memberId).get();
        MemberDTO memberDTO = toMemberDTO(member);
        memberDTO.setFollowerCount(followRepository.findFollowingMemberCountByMemberId_QueryDSL(memberId));
        memberDTO.setFollowingCount(followRepository.findFollowerMemberCountByMemberId_QueryDSL(memberId));

        memberDTO.setParentsBoards(
                followRepository.findAllParentsBoardByMemberId_QueryDSL(memberId).stream().map(parentsBoardService::toParentsBoardDTO).collect(Collectors.toList())
        );
        memberDTO.setReviews(
                followRepository.findALlReviewByMemberId_QueryDSL(memberId).stream().map(reviewService::ReviewToDTO).collect(Collectors.toList())
        );

        memberDTO.setIsFollowed(followRepository.getIsFollowedByMemberId(sessionId, memberId));
        return memberDTO;
    }

    @Override
    public MemberDTO getUserInfoForPurchase(Long memberId, Long eventId) {
        Member member = memberRepository.findById(memberId).get();
        MemberDTO memberDTO = this.toMemberDTO(member);
//        List<Coupon> coupons = couponRepository.findAllUnusedCoupon(memberId);
//        memberDTO.setCoupons(coupons);
//        memberDTO.setTotalUnusedCouponCount(couponRepository.totalUnusedCouponCount(memberId));
//        memberDTO.setTotalCouponCount(couponRepository.totalCouponCount(memberId));
//
        return memberDTO;
    }

    //    통솔자로 변경쓰~
    @Override
    public void save(MemberDTO memberDTO,Long memberId) {
        Member member = memberRepository.findMemberById(memberId);
                    member.setMemberFileOriginalName(memberDTO.getMemberFileOriginalName());
                    member.setMemberFilePath(memberDTO.getMemberFilePath());
                    member.setMemberFileUUID(memberDTO.getMemberFileUUID());
                    member.setMemberHiSentence(memberDTO.getMemberHiSentence());
                    member.setMemberGuideType(memberDTO.getMemberGuideType());
                    member.setMemberType(memberDTO.getMemberType());
                    member.setMemberGuideInterest(memberDTO.getMemberGuideInterest());
                    member.setMemberGuideStatus(AcceptanceType.HOLD);
                    member.setMemberType(MemberType.GENERAL);
                    memberRepository.save(member);
    }


    //    회원상세 ajax로 회원 이벤트 들고오기
    public CompanyDTO getEventsInfoByMemberId(Long companyId, Pageable pageable){
        CompanyDTO companyDTO = new CompanyDTO();
        List<EventDTO> finishedEvents = eventRepository.findAllFinishedEvents_QueryDSL(companyId, pageable).stream().map(this::eventToDTO).collect(Collectors.toList());
        List<EventDTO> upcommingEvents = eventRepository.findAllUpcommingEvents_QueryDSL(companyId, pageable).stream().map(this::eventToDTO).collect(Collectors.toList());
        List<EventDTO> nowEvents = eventRepository.findAllNowEvents_QueryDSL(companyId, pageable).stream().map(this::eventToDTO).collect(Collectors.toList());
        companyDTO.setFinishedEvents(finishedEvents);
        companyDTO.setUpcommingEvents(upcommingEvents);
        companyDTO.setNowEvents(nowEvents);
        companyDTO.setTotalEventsCount(eventRepository.findAllFinishedEventsCount(companyId));
        companyDTO.setTotalReviewCount(reviewRepository.countAllReviewsByMemberId_QueryDSL(companyId));

        return companyDTO;
    }

    @Override
    public CompanyDTO getAllReviewInfoByMemberId(Long companyId, Pageable pageable) {
        CompanyDTO companyDTO = new CompanyDTO();
        List<Review> reviews = reviewRepository.findAllReviewByMemberId_QueryDSL(companyId, pageable);
        Long reviewCount = reviewRepository.countAllReviewsByMemberId_QueryDSL(companyId);
        companyDTO.setTotalReviewCount(reviewCount);
        List<ReviewDTO> reviewDTO = reviews.stream().map(reviewService::ReviewToDTO).collect(Collectors.toList());
        companyDTO.setReviews(reviewDTO);
        return companyDTO;
    }

    public MemberDetailDTO getAllGeneralMemberInfo(Long memberId, Pageable pageable){
        MemberDetailDTO memberDetailDTO = new MemberDetailDTO();
        Page<ParentsBoard> parentsBoards= parentsBoardRepository.findAllByMemberId(memberId, pageable);
        Page<Review> reviews = reviewRepository.findAllByMemberId(memberId, pageable);

        List<ParentsBoardDTO> parentsBoardDTOS = parentsBoards.stream().map(parentsBoardService::toParentsBoardDTO).collect(Collectors.toList());
        List<ReviewDTO> reviewDTOS = reviews.stream().map(reviewService::ReviewToDTO).collect(Collectors.toList());
        memberDetailDTO.setParentsBoards(parentsBoardDTOS);
        memberDetailDTO.setReviews(reviewDTOS);
        memberDetailDTO.setTotalParentsBoardCount(reviewRepository.findAllParentsBoardCountByMemberId_QueryDSL(memberId));
        memberDetailDTO.setTotalReviewCount(reviewRepository.findAllReviewCountByMemberId_QueryDSL(memberId));

        return memberDetailDTO;
    }

    @Override
    public void joinGeneral(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
        memberDTO.setMemberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()));
        memberDTO.setMemberRole(Role.GENERAL);
        memberDTO.setMemberType(MemberType.GENERAL);
        memberDTO.setMemberRegisterDate(LocalDateTime.now());
        memberRepository.save(memberDTOToEntity(memberDTO));
    }

    @Override
    public void joinCompany(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
        log.info("=======PW: {}", memberDTO.getMemberPassword());
        log.info("=======PW-ENCODED: {}", passwordEncoder.encode(memberDTO.getMemberPassword()));
        memberDTO.setMemberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()));
        memberDTO.setMemberRole(Role.COMPANY);
        memberDTO.setMemberType(MemberType.COMPANY);
        memberDTO.setMemberRegisterDate(LocalDateTime.now());
        memberDTO.setMemberNickname(memberDTO.getMemberName());
        memberRepository.save(memberDTOToEntity(memberDTO));
    }

    @Override
    public Long overlapByMemberEmail(String memberEmail) {
        return (memberRepository.overlapByMemberEmail_QueryDSL(memberEmail));
    }

    @Override
    public Long overlapByPhone(String memberPhone) {
        return (memberRepository.overlapByPhone_QueryDSL(memberPhone));
    }

    @Override
    public Long overlapByMemberNickname(String memberNickname) {
        return (memberRepository.overlapByMemberNickname_QueryDSL(memberNickname));
    }

    @Override
    public Member findByMemberEmail(String memberEmail) {
        return (memberRepository.findByMemberEmail_QueryDSL(memberEmail));
    }

    /* 비밀 번호 변경 */
    @Override
    public void updatePassword(Long id, String memberPassword, PasswordEncoder passwordEncoder) {
        Member member = memberRepository.findMemberById(id);
        member.updatePassword(passwordEncoder.encode(memberPassword));
    }

    @Override
    public void updateMemberStatus(Long id, SleepType memberSleep) {

    }

    @Override
    public void setMemberInfoById(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
        memberDTO.setMemberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()));
        memberRepository.setMemberInfoMyId(memberDTOToEntity(memberDTO));
//        memberDTOToEntity --> 빌더 다 안 들어가 있을 수도 있으니까 확인하고 말해주기!!
    }


//   회원정보 수정 (정표 var.) 변경감지로 그냥 될거같은데 함 해볼게 현수
    @Override
    public void setInfoMemberById(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
        Member member = memberRepository.findMemberById(memberDTO.getId());

        if (member.getMemberPassword().matches(memberDTO.getMemberPassword())) {
            member.setMemberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()));
        }

        member.setMemberEmail(memberDTO.getMemberEmail());
        member.setMemberAddress(memberDTO.getMemberAddress());
        member.setMemberPhone(memberDTO.getMemberPhone());
        member.setMemberNickname(memberDTO.getMemberNickname());
        member.setMemberProfileOriginalName(memberDTO.getMemberProfileOriginalName());
        member.setMemberProfilePath(memberDTO.getMemberProfilePath());
        member.setMemberProfileUUID(memberDTO.getMemberProfileUUID());
        memberRepository.save(member);
    }

    @Override
    public MemberDTO findByMemberId(Long memberId) {
        MemberDTO memberDTO = entityToMemberDTO(memberRepository.findMemberById(memberId));
        return memberDTO;
    }



    @Override
    public Member findMemberByRandomKey(String randomKey) {
        return memberRepository.findMemberByRandomKey(randomKey);
    }

    @Override
    public Member findMemberByMemberEmailAndRandomKey(String memberEmail, String randomKey) {
        return findMemberByMemberEmailAndRandomKey(memberEmail, randomKey);
    }

    @Override
    public void sendMail(MailDTO mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getAddress());
        message.setFrom("heykiddoruready@gmail.com");
//        from 값을 설정하지 않으면 application.yml의 username값이 설정됩니다.
        message.setSubject(mail.getTitle());
        message.setText(mail.getMessage());

        mailSender.send(message);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
        return UserDetail.builder()
                .id(member.getId())
                .memberEmail(member.getMemberEmail())
                .memberPassword(member.getMemberPassword())
                .memberRole(member.getMemberRole())
                .memberType(member.getMemberType())
                .memberRegisterDate(member.getMemberRegisterDate())
                .build();
    }
}
