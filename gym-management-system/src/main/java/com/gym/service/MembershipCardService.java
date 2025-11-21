package com.gym.service;

import com.gym.entity.MembershipCard;

import java.util.List;

/**
 * 会员卡服务接口
 * 提供会员卡相关的业务逻辑操作
 */
public interface MembershipCardService {

    /**
     * 获取所有会员卡
     * @return 会员卡列表
     */
    List<MembershipCard> findAllCards();

    /**
     * 根据ID获取会员卡
     * @param id 会员卡ID
     * @return 会员卡对象，如果不存在则返回null
     */
    MembershipCard findCardById(Long id);

    /**
     * 创建会员卡
     * @param card 会员卡对象
     */
    void createCard(MembershipCard card);

    /**
     * 更新会员卡
     * @param card 会员卡对象
     */
    void updateCard(MembershipCard card);

    /**
     * 删除会员卡
     * @param id 会员卡ID
     */
    void deleteCard(Long id);

    /**
     * 获取激活状态的会员卡
     * @return 激活状态的会员卡列表
     */
    List<MembershipCard> findActiveCards();
}