package com.gym.controller;

import com.gym.entity.MemberInfo;
import com.gym.service.MemberInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 会员个人资料控制器
 * 只处理会员个人资料保存功能
 */
@Controller
@RequestMapping("/member")
public class MemberProfileController {

    @Autowired
    private MemberInfoService memberInfoService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 保存会员信息并重定向到个人信息页面
     * @param params 会员信息参数
     * @param attributes 重定向属性，用于传递消息
     * @return 重定向到个人信息页面
     */
    @PostMapping("/profile/save")
    public String saveProfile(@RequestParam Map<String, String> params, RedirectAttributes attributes) {
        Long userId = getCurrentUserId();
        
        try {
            // 手动创建MemberInfo对象并设置属性
            MemberInfo memberInfo = createMemberInfoFromParams(params);
            memberInfo.setUserId(userId);
            
            // 检查是否存在
            if (memberInfoService.existsMemberInfo(userId)) {
                // 更新
                memberInfoService.updateMemberInfo(memberInfo);
                attributes.addFlashAttribute("message", "个人信息更新成功");
            } else {
                // 新增
                memberInfoService.addMemberInfo(memberInfo);
                attributes.addFlashAttribute("message", "个人信息保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "保存失败：" + e.getMessage());
        }
        
        // 重定向到个人信息页面
        return "redirect:/member/profile";
    }
    
    /**
     * 从表单参数创建MemberInfo对象
     */
    private MemberInfo createMemberInfoFromParams(Map<String, String> params) {
        MemberInfo memberInfo = new MemberInfo();
        
        // 设置基本属性
        memberInfo.setName(params.get("name"));
        memberInfo.setGender(params.get("gender"));
        memberInfo.setPhone(params.get("phone"));
        memberInfo.setEmail(params.get("email"));
        memberInfo.setAddress(params.get("address"));
        memberInfo.setEmergencyContact(params.get("emergencyContact"));
        memberInfo.setEmergencyPhone(params.get("emergencyPhone"));
        memberInfo.setHealthCondition(params.get("healthCondition"));
        
        // 手动处理日期格式转换
        String birthDateStr = params.get("birthDate");
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            try {
                memberInfo.setBirthDate(LocalDate.parse(birthDateStr, dateFormatter));
            } catch (Exception e) {
                // 如果解析失败，尝试其他常见格式或设置为null
                try {
                    memberInfo.setBirthDate(LocalDate.parse(birthDateStr));
                } catch (Exception ex) {
                    // 忽略日期解析错误，保持birthDate为null
                }
            }
        }
        
        String cardIssueDateStr = params.get("cardIssueDate");
        if (cardIssueDateStr != null && !cardIssueDateStr.isEmpty()) {
            try {
                memberInfo.setCardIssueDate(LocalDate.parse(cardIssueDateStr, dateFormatter));
            } catch (Exception e) {
                try {
                    memberInfo.setCardIssueDate(LocalDate.parse(cardIssueDateStr));
                } catch (Exception ex) {
                    // 忽略日期解析错误，保持cardIssueDate为null
                }
            }
        }
        
        // 处理数值类型
        try {
            if (params.containsKey("height") && !params.get("height").isEmpty()) {
                memberInfo.setHeight(new java.math.BigDecimal(params.get("height")));
            }
            if (params.containsKey("weight") && !params.get("weight").isEmpty()) {
                memberInfo.setWeight(new java.math.BigDecimal(params.get("weight")));
            }
        } catch (NumberFormatException e) {
            // 忽略数值格式错误
        }
        
        return memberInfo;
    }

    /**
     * 获取当前登录用户的ID
     * @return 当前用户ID
     */
    private Long getCurrentUserId() {
        // 从安全上下文中获取当前认证的用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        
        // 获取当前登录用户的用户名（手机号）
        String phone = authentication.getName();
        
        // 通过手机号获取用户ID
        return memberInfoService.getUserIdByPhone(phone);
    }
}