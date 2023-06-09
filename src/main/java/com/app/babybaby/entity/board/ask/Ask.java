package com.app.babybaby.entity.board.ask;

import com.app.babybaby.entity.board.BoardInfo;
import com.app.babybaby.entity.member.Member;
import com.app.babybaby.type.ProcessType;
import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@ToString(callSuper = true, exclude = {"member"})
@Table(name = "TBL_ASK")
@PrimaryKeyJoinColumn(name = "ID")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@DynamicInsert
@SequenceGenerator(name="ask_seq", initialValue=1, allocationSize=1)
public class Ask extends BoardInfo {

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'HOLD'")
    private ProcessType askStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "ask")
    private AskAnswer askAnswer;

    public Ask(ProcessType askStatus) {
        this.askStatus = askStatus;
    }

    public Ask(String boardTitle, String boardContent, ProcessType askStatus,Member member) {
        super(boardTitle, boardContent);
        this.askStatus = askStatus;
        this.member = member;
    }

    public Ask(String boardTitle, String boardContent, ProcessType askStatus) {
        super(boardTitle, boardContent);
        this.askStatus = askStatus;
    }

    @Builder
    public Ask(Long id, String boardTitle, String boardContent, ProcessType askStatus, Member member, AskAnswer askAnswer) {
        super(id, boardTitle, boardContent);
        this.askStatus = askStatus;
        this.member = member;
        this.askAnswer = askAnswer;
    }
}
