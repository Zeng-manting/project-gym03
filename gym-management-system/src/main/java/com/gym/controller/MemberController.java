package com.gym.controller;

import com.gym.dto.BookingDTO;
import com.gym.entity.Course;
import com.gym.entity.MemberInfo;
import com.gym.entity.MembershipCard;
import com.gym.entity.User;
import com.gym.mapper.UserMapper;
import com.gym.service.BookingService;
import com.gym.service.CourseService;
import com.gym.service.MemberInfoService;
import com.gym.service.MembershipCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员控制器
 * 处理会员相关的HTTP请求
 */
@Controller
@RequestMapping("/member")
public class MemberController {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private CourseService courseService;

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private MembershipCardService membershipCardService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private MemberInfoService memberInfoService;

    /**
     * 会员首页，转发到index方法确保数据正确加载
     * @return 会员首页视图名称
     */
    @GetMapping("")
    @PreAuthorize("hasRole('MEMBER')")
    public String memberHome(Model model) {
        logger.info("会员首页访问 - 转发到index方法");
        // 转发到index方法，确保数据查询逻辑被执行
        return index(model);
    }

    /**
     * 显示会员首页
     * @return 会员首页视图名称
     */
    @GetMapping("index")
    @PreAuthorize("hasRole('MEMBER')")
    public String index(Model model) {
        try {
            // 获取当前登录用户信息
            User currentUser = getCurrentUser();
            
            // 查询会员详细信息
            MemberInfo memberInfo = memberInfoService.getMemberInfoByUserId(currentUser.getId());
            model.addAttribute("memberInfo", memberInfo);
            
            // 查询会员卡数据
            List<MembershipCard> cards = membershipCardService.findAllCards();
            model.addAttribute("cards", cards);
            
            // 获取会员卡数量
            int cardCount = cards.size();
            model.addAttribute("cardCount", cardCount);
            
            // 获取今日预约数量（简单实现，实际可能需要根据日期筛选）
            logger.info("查询用户ID {} 的预约数据", currentUser.getId());
            List<BookingDTO> bookings = bookingService.getMyBookings(currentUser.getId());
            logger.info("获取到 {} 条预约记录", bookings.size());
            
            // 修改：今日预约数量直接使用我的预约的总数量
            int todayBookingCount = bookings.size();
            int completedCourseCount = 0;
            int upcomingBookings = 0;
            for (BookingDTO booking : bookings) {
                if (booking.getScheduleTime().isBefore(LocalDateTime.now())) {
                    completedCourseCount++;
                }
                if (booking.getScheduleTime().isAfter(LocalDateTime.now())) {
                    upcomingBookings++;
                }
            }
            logger.info("今日预约(总预约数): {}, 已完成: {}, 即将到来: {}", todayBookingCount, completedCourseCount, upcomingBookings);
            model.addAttribute("todayBookingCount", todayBookingCount);
            model.addAttribute("completedCourseCount", completedCourseCount);
            model.addAttribute("upcomingBookings", upcomingBookings);
            
            // 计算健身天数（简单实现，可以根据实际需求调整）
            model.addAttribute("fitnessDays", completedCourseCount);
            
            // 获取近期预约列表（限制5个）
            List<BookingDTO> recentBookings = bookings.stream()
                    .filter(booking -> booking.getScheduleTime().isAfter(LocalDateTime.now()))
                    .limit(5)
                    .collect(Collectors.toList());
            logger.info("近期预约列表大小: {}", recentBookings.size());
            model.addAttribute("recentBookings", recentBookings);
            
        } catch (Exception e) {
            logger.error("获取会员首页数据失败", e);
            model.addAttribute("error", "获取数据失败: " + e.getMessage());
        }
        return "member/index";
    }

    /**
     * 查看可用课程列表
     * @param model 模型对象，用于传递数据到视图
     * @return 课程列表页面
     */
    @GetMapping("courses")
    @PreAuthorize("hasRole('MEMBER')")
    public String viewCourses(Model model) {
        List<Course> courses = courseService.getAvailableCourses();
        model.addAttribute("courses", courses);
        return "member/courses";
    }

    /**
     * 预约课程
     * @param courseId 课程ID
     * @param redirectAttributes 重定向属性，用于传递消息
     * @return 重定向到会员首页
     */
    @PostMapping("book/{courseId}")
    @PreAuthorize("hasRole('MEMBER')")
    public String bookCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        try {
            // 获取当前登录用户信息
            User currentUser = getCurrentUser();
            logger.info("会员 {} 开始预约课程 {}", currentUser.getId(), courseId);
            
            bookingService.bookCourse(currentUser.getId(), courseId);
            redirectAttributes.addFlashAttribute("success", "预约成功！");
            logger.info("会员 {} 预约课程 {} 成功", currentUser.getId(), courseId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            logger.error("预约课程失败: {}", e.getMessage());
        }
        // 预约成功后重定向到会员首页根路径，确保数据正确加载
        return "redirect:/member";
    }

    /**
     * 查看我的预约列表
     * @param model 模型对象，用于传递数据到视图
     * @return 预约列表页面
     */
    @GetMapping("bookings")
    @PreAuthorize("hasRole('MEMBER')")
    public String viewBookings(Model model) {
        try {
            // 获取当前登录用户信息
            User currentUser = getCurrentUser();
            
            List<BookingDTO> bookings = bookingService.getMyBookings(currentUser.getId());
            model.addAttribute("bookings", bookings);
        } catch (Exception e) {
            model.addAttribute("error", "获取预约列表失败: " + e.getMessage());
        }
        return "member/bookings";
    }

    /**
     * 取消预约
     * @param bookingId 预约ID
     * @param redirectAttributes 重定向属性，用于传递消息
     * @return 重定向到预约列表页面
     */
    @PostMapping("cancel/{bookingId}")
    @PreAuthorize("hasRole('MEMBER')")
    public String cancelBooking(@PathVariable Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(bookingId);
            redirectAttributes.addFlashAttribute("success", "取消预约成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/member/bookings";
    }
    
    /**
     * 查看个人信息页面
     * @param model 模型对象，用于传递数据到视图
     * @return 个人信息页面
     */
    @GetMapping("profile")
    @PreAuthorize("hasRole('MEMBER')")
    public String viewProfile(Model model) {
        try {
            // 获取当前登录用户信息
            User currentUser = getCurrentUser();
            
            // 查询会员详细信息
            MemberInfo memberInfo = memberInfoService.getMemberInfoByUserId(currentUser.getId());
            model.addAttribute("memberInfo", memberInfo);
        } catch (Exception e) {
            model.addAttribute("error", "获取个人信息失败: " + e.getMessage());
        }
        return "member/profile";
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