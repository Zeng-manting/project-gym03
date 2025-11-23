package com.gym.controller;

import com.gym.entity.MembershipCard;
import com.gym.entity.User;
import com.gym.mapper.UserMapper;
import com.gym.service.MembershipCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberCardController {

    @Autowired
    private MembershipCardService membershipCardService;
    
    @Autowired
    private UserMapper userMapper;

    /**
     * 显示会员的会员卡页面
     * @param model 模型对象，用于传递数据到前端
     * @return 会员卡页面
     */
    @GetMapping("/cards")
    @PreAuthorize("hasRole('MEMBER')")
    public String showMemberCards(Model model) {
        try {
            // 获取当前登录用户信息
            User currentUser = getCurrentUser();
            
            // 从数据库中查询会员卡信息
            List<MembershipCard> cards = membershipCardService.findAllCards();
            
            model.addAttribute("cards", cards);
        } catch (Exception e) {
            model.addAttribute("error", "获取会员卡信息失败: " + e.getMessage());
        }
        return "member/cards";
    }
    
    /**
     * 获取当前登录用户信息
     * @return 当前登录的用户对象
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        // 根据手机号（用户名）从数据库查询用户信息
        User user = userMapper.findByPhone(username);
        if (user == null) {
            throw new RuntimeException("用户信息不存在");
        }
        
        return user;
    }
}