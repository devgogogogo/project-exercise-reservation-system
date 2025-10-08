package com.fastcampus.exercisereservationsystem.domain.comment.service;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.request.CreateCommentRequest;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.request.UpdateCommentRequest;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.CreateCommentResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.GetCommentListResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.UpdateCommentResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.entity.CommentEntity;
import com.fastcampus.exercisereservationsystem.domain.comment.exception.CommentErrorCode;
import com.fastcampus.exercisereservationsystem.domain.comment.repository.CommentRepository;
import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import com.fastcampus.exercisereservationsystem.domain.notice.exception.NoticeErrorCode;
import com.fastcampus.exercisereservationsystem.domain.notice.repository.NoticeRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public CreateCommentResponse createComment(UserEntity userEntity, Long noticeId, CreateCommentRequest request) {
        NoticeEntity noticeEntity = noticeRepository.findByIdWithUser(noticeId).orElseThrow(() -> new BizException(NoticeErrorCode.NOTICE_NOT_FOUND));
        CommentEntity commentEntity = new CommentEntity(noticeEntity, userEntity, request.description());
        commentRepository.save(commentEntity);
        return CreateCommentResponse.from(commentEntity);
    }

   /**
     * 예시
     * 댓글 전체 = [1][2][3][4][5][6][7][8][9][10][11][12][13][14][15][16][17][18][19][20][21][22][23][24][25]
     * page=0, size=10  → [1][2][3][4][5][6][7][8][9][10]
     * page=1, size=10  → [11][12][13][14][15][16][17][18][19][20]
     * page=2, size=10  → [21][22][23][24][25]
     */
    @Transactional(readOnly = true)
    public Page<GetCommentListResponse> getCommentPage(Long noticeId, int page, int size) {
        //방어코드
        //사용자가 ?page=0이나 ?page=-5처럼 이상한 값을 넣으면 그대로 들어 올수 있음
        int safePage = Math.max(1, page) -1;
        int safeSize = Math.max(1, size);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<CommentEntity> commentEntityPage = commentRepository.findByNoticeIdWithUser(noticeId, pageable);
        Page<GetCommentListResponse> responsesPage = commentEntityPage.map(entity -> GetCommentListResponse.from(entity));
        return responsesPage;
    }

    @Transactional
    public UpdateCommentResponse updateComment(UserEntity userEntity, Long noticeId, Long commentId, UpdateCommentRequest request) {
        CommentEntity commentEntity = commentRepository.findByIdAndNoticeIdWithUser(commentId, noticeId).orElseThrow(() -> new BizException(CommentErrorCode.COMMENT_NOT_FOUND));
        if (userEntity.getId().equals(commentEntity.getUser().getId())) {
            commentEntity.updateComment(request.description());
            return UpdateCommentResponse.from(commentEntity);
        } else {
            throw new BizException(CommentErrorCode.COMMENT_FORBIDDEN);
        }
    }

    @Transactional
    public void deleteComment(UserEntity userEntity, Long noticeId, Long commentId) {
        CommentEntity commentEntity = commentRepository.findByIdAndNoticeIdWithUser(commentId, noticeId).orElseThrow(() -> new BizException(CommentErrorCode.COMMENT_NOT_FOUND));
        if (userEntity.getId().equals(commentEntity.getUser().getId())) {
            commentRepository.deleteById(commentId);
        } else {
            throw new BizException(UserErrorCode.USER_NOT_OWNER);
        }
    }
}
