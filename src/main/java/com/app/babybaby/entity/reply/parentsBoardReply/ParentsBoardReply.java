package com.app.babybaby.entity.reply.parentsBoardReply;

import com.app.babybaby.entity.audit.Period;
import com.app.babybaby.entity.board.parentsBoard.ParentsBoard;
import com.app.babybaby.entity.member.Member;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString(exclude = {"member", "parentsBoard"})
@Table(name = "TBL_PARENTS_BOARD_REPLY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParentsBoardReply extends Period {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull
    private String ParentsBoardReplyContent;

    @ManyToOne(fetch = FetchType.LAZY)
    private ParentsBoard parentsBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public ParentsBoardReply(String parentsBoardReplyContent, ParentsBoard parentsBoard, Member member) {
        ParentsBoardReplyContent = parentsBoardReplyContent;
        this.parentsBoard = parentsBoard;
        this.member = member;
    }

//    public ParentsBoardReply(Long id, ParentsBoard parentsBoard) {
//        this.id = id;
//        this.parentsBoard = parentsBoard;
//    }

    public void setParentsBoardReplyContent(String parentsBoardReplyContent) {
        ParentsBoardReplyContent = parentsBoardReplyContent;
    }
}
