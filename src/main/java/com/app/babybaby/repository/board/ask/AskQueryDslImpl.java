package com.app.babybaby.repository.board.ask;

import com.app.babybaby.entity.board.ask.Ask;
import com.app.babybaby.entity.board.ask.QAsk;
import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.search.admin.AdminAskSearch;
import com.app.babybaby.search.board.parentsBoard.EventBoardSearch;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static com.app.babybaby.entity.board.ask.QAsk.ask;
import static com.app.babybaby.entity.board.event.QEvent.event;

@RequiredArgsConstructor
public class AskQueryDslImpl implements AskQueryDsl {


    private final JPAQueryFactory query;

    //  [관리자] 문의 목록 조회
    @Override
    public Page<Ask> findAllAsk_queryDSL(Pageable pageable, AdminAskSearch adminAskSearch) {
        BooleanExpression askTitleEq = adminAskSearch.getAskTitle() == null ? null : ask.boardTitle.eq(adminAskSearch.getAskTitle());

        QAsk ask = QAsk.ask;
        List<Ask> foundAsk = query.select(ask)
                .from(ask)
                .where(askTitleEq)
                .orderBy(ask.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = query.select(ask.count())
                .from(ask)
                .where(askTitleEq)
                .fetchOne();

        return new PageImpl<>(foundAsk, pageable, count);
    }

    @Override
    //  [관리자] 문의 상세보기
    public List<Ask> findAllAskDetail_queryDSL() {
        List<Ask> foundAsktDetail = query.select(ask)
                .from(ask)
                .fetch();
        return foundAsktDetail;
    }



    //  [관리자] 문의 삭제하기
    @Override
    public void deleteAskByIds_queryDSL(Long askId) {
        query.delete(ask)
                .where(ask.id.in(askId))
                .execute();
    }

    //  [관리자] 문의 아이디로 찾기
    @Override
    public Ask findAskById_queryDSL(Long askId) {
        return query.select(ask)
                .from(ask)
                .where(ask.id.eq(askId))
                .fetchOne();

    }








}
