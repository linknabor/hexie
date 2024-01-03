package com.yumu.hexie.service.shequ;

import java.util.List;
import java.util.Map;

import com.yumu.hexie.integration.interact.req.InteractReq;
import com.yumu.hexie.integration.interact.req.SaveInteractCommentReq;
import com.yumu.hexie.integration.interact.req.SaveInteractInfoReq;
import com.yumu.hexie.integration.interact.resp.InteractCommentResp;
import com.yumu.hexie.integration.interact.resp.InteractInfoResp;
import com.yumu.hexie.model.user.User;

public interface CommunityService {

    //1 查看自己的帖子
    List<InteractInfoResp> getInteractList(User user, InteractReq req) throws Exception;

    //2.获取互动分类
    List<Map<String, String>> getInteractType(User user) throws Exception;

    //3.添加新帖子
    void addInteract(User user, SaveInteractInfoReq req) throws Exception;

    //4.删除帖子（实际更改帖子状态）
    void deleteInteract(User user, InteractReq req) throws Exception;

    //5.根据帖子ID获取具体的帖子信息(不包含回复信息)
    InteractInfoResp getInteractInfoById(User user, InteractReq req) throws Exception;

    //6.根据帖子获取其相关评论
    List<InteractCommentResp> getCommentByInteractId(User user, InteractReq req) throws Exception;

    //7.添加评论
    InteractCommentResp addComment(User user, SaveInteractCommentReq req) throws Exception;

    //8.删除评论
    void deleteComment(User user, InteractReq req) throws Exception;

    //9.用户评价打分
    InteractInfoResp saveGrade(User user, InteractReq req) throws Exception;
}
