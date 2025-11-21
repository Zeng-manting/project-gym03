package com.gym.controller;

import com.gym.entity.MembershipCard;
import com.gym.service.MembershipCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 会员卡控制器
 * 处理会员卡相关的HTTP请求
 */
@Controller
public class MembershipCardController {

    private final MembershipCardService membershipCardService;

    @Autowired
    public MembershipCardController(MembershipCardService membershipCardService) {
        this.membershipCardService = membershipCardService;
    }

    /**
     * 显示会员卡列表
     * @param model 模型对象
     * @return 会员卡列表视图
     */
    @GetMapping("/admin/membership-cards")
    @PreAuthorize("hasRole('ADMIN')")
    public String listCards(Model model) {
        model.addAttribute("cards", membershipCardService.findAllCards());
        return "admin/membership-cards";
    }

    /**
     * 显示创建会员卡页面
     * @param model 模型对象
     * @return 创建会员卡视图
     */
    @GetMapping("/admin/membership-cards/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("card", new MembershipCard());
        return "admin/create-membership-card";
    }

    /**
     * 创建新会员卡
     * @param card 会员卡对象
     * @param attributes 重定向属性
     * @return 重定向到会员卡列表页面
     */
    @PostMapping("/admin/membership-cards")
    @PreAuthorize("hasRole('ADMIN')")
    public String createCard(@ModelAttribute MembershipCard card, RedirectAttributes attributes) {
        try {
            membershipCardService.createCard(card);
            attributes.addFlashAttribute("message", "会员卡创建成功");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "会员卡创建失败：" + e.getMessage());
            return "redirect:/admin/membership-cards/create";
        }
        return "redirect:/admin/membership-cards";
    }

    /**
     * 显示编辑会员卡页面
     * @param id 会员卡ID
     * @param model 模型对象
     * @return 编辑会员卡视图
     */
    @GetMapping("/admin/membership-cards/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        MembershipCard card = membershipCardService.findCardById(id);
        if (card == null) {
            return "redirect:/admin/membership-cards";
        }
        model.addAttribute("card", card);
        return "admin/edit-membership-card";
    }

    /**
     * 更新会员卡
     * @param id 会员卡ID
     * @param card 会员卡对象
     * @param attributes 重定向属性
     * @return 重定向到会员卡列表页面
     */
    @PostMapping("/admin/membership-cards/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCard(@PathVariable("id") Long id, @ModelAttribute MembershipCard card, RedirectAttributes attributes) {
        try {
            // 确保ID一致
            card.setId(id);
            membershipCardService.updateCard(card);
            attributes.addFlashAttribute("message", "会员卡更新成功");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "会员卡更新失败：" + e.getMessage());
            return "redirect:/admin/membership-cards/edit/" + id;
        }
        return "redirect:/admin/membership-cards";
    }

    /**
     * 删除会员卡
     * @param id 会员卡ID
     * @param attributes 重定向属性
     * @return 重定向到会员卡列表页面
     */
    @PostMapping("/admin/membership-cards/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCard(@PathVariable("id") Long id, RedirectAttributes attributes) {
        try {
            membershipCardService.deleteCard(id);
            attributes.addFlashAttribute("message", "会员卡删除成功");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "会员卡删除失败：" + e.getMessage());
        }
        return "redirect:/admin/membership-cards";
    }

    /**
     * 更新会员卡状态
     * @param id 会员卡ID
     * @param status 新状态
     * @param attributes 重定向属性
     * @return 重定向到会员卡列表页面
     */
    @PostMapping("/admin/membership-cards/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCardStatus(@PathVariable("id") Long id, @RequestParam("status") String status, RedirectAttributes attributes) {
        try {
            MembershipCard card = membershipCardService.findCardById(id);
            if (card != null) {
                card.setStatus(status);
                membershipCardService.updateCard(card);
                attributes.addFlashAttribute("message", "会员卡状态更新成功");
            }
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "会员卡状态更新失败：" + e.getMessage());
        }
        return "redirect:/admin/membership-cards";
    }
}