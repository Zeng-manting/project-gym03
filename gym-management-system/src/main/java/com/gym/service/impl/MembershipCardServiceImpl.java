package com.gym.service.impl;

import com.gym.entity.MembershipCard;
import com.gym.mapper.MembershipCardMapper;
import com.gym.service.MembershipCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员卡服务实现类
 * 实现会员卡相关的业务逻辑
 */
@Service
public class MembershipCardServiceImpl implements MembershipCardService {

    private final MembershipCardMapper membershipCardMapper;

    @Autowired
    public MembershipCardServiceImpl(MembershipCardMapper membershipCardMapper) {
        this.membershipCardMapper = membershipCardMapper;
    }

    /**
     * 获取所有会员卡
     */
    @Override
    public List<MembershipCard> findAllCards() {
        return membershipCardMapper.findAll();
    }

    /**
     * 根据ID获取会员卡
     */
    @Override
    public MembershipCard findCardById(Long id) {
        return membershipCardMapper.findById(id);
    }

    /**
     * 创建会员卡
     */
    @Override
    public void createCard(MembershipCard card) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        card.setCreateTime(now);
        card.setUpdateTime(now);
        // 设置默认状态为激活
        if (card.getStatus() == null) {
            card.setStatus("active");
        }
        // 插入数据库
        membershipCardMapper.insert(card);
    }

    /**
     * 更新会员卡
     */
    @Override
    public void updateCard(MembershipCard card) {
        // 更新更新时间
        card.setUpdateTime(LocalDateTime.now());
        // 更新数据库
        membershipCardMapper.update(card);
    }

    /**
     * 删除会员卡
     */
    @Override
    public void deleteCard(Long id) {
        membershipCardMapper.delete(id);
    }

    /**
     * 获取激活状态的会员卡
     */
    @Override
    public List<MembershipCard> findActiveCards() {
        return membershipCardMapper.findActiveCards();
    }
}