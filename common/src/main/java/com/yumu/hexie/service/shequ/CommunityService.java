package com.yumu.hexie.service.shequ;

import java.util.List;

import com.yumu.hexie.integration.wuye.req.OpinionRequest;
import com.yumu.hexie.service.shequ.req.CommunitySummary;
import com.yumu.hexie.service.shequ.req.Ratio;
import com.yumu.hexie.service.shequ.req.RatioDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.yumu.hexie.model.community.Annoucement;
import com.yumu.hexie.model.community.CommunityInfo;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.community.ThreadComment;
import com.yumu.hexie.model.user.User;

public interface CommunityService {

    /**
     * 0.获取帖子列表
     *
     * @param userSectId
     * @param page
     * @return
     */
    List<Thread> getThreadList(String userSectId, Pageable page);


    /**
     * 获取用户自己帖子列表
     *
     * @param userId
     * @param page
     * @return
     */
    List<Thread> getThreadListByUserId(long userId, Pageable page);

    /**
     * 0 A)获取帖子列表,查看所有小区的
     *
     * @param page
     * @return
     */
    List<Thread> getThreadList(Pageable page);

    /**
     * 根据分类获取帖子列表
     *
     * @param userId
     * @param category
     * @param userSectId
     * @param page
     * @return
     */
    List<Thread> getThreadListByCategory(Long userId, int category, String userSectId, Pageable page);

    /**
     * 1 A)根据分类获取帖子列表，查看所有小区的
     *
     * @param userId
     * @param category
     * @param page
     * @return
     */
    List<Thread> getThreadListByCategory(long userId, int category, Pageable page);


    /**
     * 2.添加新帖子
     *
     * @param thread
     * @return
     */
    Thread addThread(User user, Thread thread);

    /**
     * 3.删除帖子（实际更改帖子状态）
     *
     * @param user
     * @param threadId
     * @return
     */
    void deleteThread(User user, long threadId);

    /**
     * 4.编辑帖子
     *
     * @param thread
     * @return
     */
    void updateThread(Thread thread);


    /**
     * 5.添加评论
     *
     * @param user
     * @param threadComment
     * @return
     */
    ThreadComment addComment(User user, ThreadComment threadComment);

    /**
     * 6.根据帖子ID获取具体的帖子信息
     *
     * @param threadId
     * @return
     */
    Thread getThreadByTreadId(long threadId);

    /**
     * 7.根据帖子获取其相关评论
     *
     * @param threadId
     * @return
     */
    List<ThreadComment> getCommentListByThreadId(long threadId);

    /**
     * 8.删除评论
     *
     * @param user
     * @param threadCommentId
     */
    void deleteComment(User user, long threadCommentId);

    /**
     * 9.获取我的发布
     *
     * @param userId
     * @param sort
     * @return
     */
    List<Thread> getThreadListByUserId(long userId, Sort sort);

    /**
     * 10.获取社区百事通信息
     */
    List<CommunityInfo> getCommunityInfoBySectId(long sectId, Sort sort);

    /**
     * 11.获取社区百事通信息
     */
    List<CommunityInfo> getCommunityInfoByRegionId(long regionId, Sort sort);

    /**
     * 12.获取社区百事通信息
     */
    List<CommunityInfo> getCommunityInfoByCityIdAndInfoType(long cityId, String infoType, Sort sort);

    /**
     * 13.获取社区公告信息
     */
    List<Annoucement> getAnnoucementList(Sort sort);

    /**
     * 14.获取社区详细信息
     *
     * @param annoucement
     * @return
     */
    Annoucement getAnnoucementById(long annoucement);

    /**
     * 15.获取未评论帖子的数量
     *
     * @param threadStatus
     * @param toUserId
     * @return
     */
    int getUnreadCommentsCount(String threadStatus, long toUserId);

    /**
     * 16.更新评论状态
     *
     * @param threadId
     */
    void updateCommentReaded(long toUserId, long threadId);

    /**
     * 获取帖子列表（新分类:即叽歪和二手）
     *
     * @param category
     * @param userSectId
     * @param page
     * @return
     */
    List<Thread> getThreadListByNewCategory(int category, String userSectId, Pageable page);


    /**
     * 获取帖子列表
     *
     * @param category
     * @param page
     * @return
     */
    List<Thread> getThreadListByNewCategory(int category, Pageable page);


    Page<Thread> getThreadList(String nickName, String createDate, String sectId, List<String> sectIds, Pageable pageable);

    void deleteThread(String[] threadIds);

    void saveThreadComment(Long threadId, String content, Long userId, String userName);

    /**
     * 根据帖子回复信息ID获取具体的帖子信息
     *
     * @param threadCommentId
     * @return
     */
    ThreadComment getThreadCommentByTreadId(long threadCommentId);

    void updateThreadComment(ThreadComment thread);

    Boolean sendNotification(User user, OpinionRequest opinionRequest) throws Exception;

    List<Ratio> getThreadByCycleSummary(CommunitySummary communitySummary) throws Exception;

    //根据小区ID和区间汇总意见信息
    List<RatioDetail> getThreadBySectIdsAndCycle(String sectId, String dateCycle);

}
