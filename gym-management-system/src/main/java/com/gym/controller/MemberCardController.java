package com.gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/member")
public class MemberCardController {

    /**
     * 显示会员的会员卡页面
     * @param model 模型对象，用于传递数据到前端
     * @return 会员卡页面
     */
    @GetMapping("/cards")
    public String showMemberCards(Model model) {
        // 这里模拟会员卡数据
        // 实际应用中，应该从数据库中查询会员卡信息
        List<Map<String, Object>> cards = getMockMemberCards();
        
        model.addAttribute("cards", cards);
        return "member/cards";
    }
    
    /**
     * 获取模拟的会员卡数据
     * @return 会员卡列表
     */
    private List<Map<String, Object>> getMockMemberCards() {
        List<Map<String, Object>> cards = new ArrayList<>();
        
        // 添加一张活跃的年卡
        Map<String, Object> annualCard = new HashMap<>();
        annualCard.put("id", 1);
        annualCard.put("member_id", 1);
        annualCard.put("cardNumber", "FZP2024001001");
        annualCard.put("cardType", "年卡会员");
        annualCard.put("amount", 1980.0);
        annualCard.put("balance", 1980.0);
        annualCard.put("remainingTimes", 365);
        annualCard.put("startDate", "2024-01-01");
        annualCard.put("endDate", "2025-01-01");
        annualCard.put("status", "ACTIVE");
        cards.add(annualCard);
        
        // 添加一张活跃的次卡
        Map<String, Object> timesCard = new HashMap<>();
        timesCard.put("id", 2);
        timesCard.put("member_id", 1);
        timesCard.put("cardNumber", "FZP2024002001");
        timesCard.put("cardType", "次卡会员(50次)");
        timesCard.put("amount", 980.0);
        timesCard.put("balance", 784.0);
        timesCard.put("remainingTimes", 40);
        timesCard.put("startDate", "2024-03-15");
        timesCard.put("endDate", "2024-09-15");
        timesCard.put("status", "ACTIVE");
        cards.add(timesCard);
        
        // 添加一张过期的月卡
        Map<String, Object> expiredCard = new HashMap<>();
        expiredCard.put("id", 3);
        expiredCard.put("member_id", 1);
        expiredCard.put("cardNumber", "FZP2023003001");
        expiredCard.put("cardType", "月卡会员");
        expiredCard.put("amount", 298.0);
        expiredCard.put("balance", 0.0);
        expiredCard.put("remainingTimes", 0);
        expiredCard.put("startDate", "2023-12-01");
        expiredCard.put("endDate", "2024-01-01");
        expiredCard.put("status", "EXPIRED");
        cards.add(expiredCard);
        
        return cards;
    }
}