package com.gym.controller;

import com.gym.entity.Course;
import com.gym.entity.User;
import com.gym.entity.CoachInfo;
import com.gym.service.BookingService;
import com.gym.service.CourseService;
import com.gym.service.CoachInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.gym.mapper.UserMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教练控制器
 * 处理教练相关的HTTP请求
 */
@Controller
@RequestMapping("/coach")
public class CoachController {

    @Autowired
    private CourseService courseService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CoachInfoService coachInfoService;

    /**
     * 显示教练首页
     * @return 教练首页视图名称
     */
    @GetMapping("")
    @PreAuthorize("hasRole('TRAINER')")
    public String index(Model model) {
        return loadCoachDashboardData(model);
    }
    
    /**
     * 显示教练首页（支持/coach/index路径）
     * @return 教练首页视图名称
     */
    @GetMapping("index")
    @PreAuthorize("hasRole('TRAINER')")
    public String indexAlternative(Model model) {
        return loadCoachDashboardData(model);
    }
    
    /**
     * 加载教练仪表板数据
     * @param model 模型对象，用于传递数据到视图
     * @return 教练首页视图名称
     */
    private String loadCoachDashboardData(Model model) {
        // 获取当前登录的教练用户
        User user = getCurrentUser();
        
        // 获取当前教练的所有课程
        List<Course> courses = courseService.getMyCourses(user.getId());
        
        // 计算统计数据
        int courseCount = courses.size();
        
        // 计算今日预约数量：获取教练今天的所有预约数
        LocalDate today = LocalDate.now();
        int todayBookingCount = bookingService.countTodayBookingsByTrainerId(user.getId(), today);
        
        // 计算学员数量：获取预约过该教练课程的唯一学员数量
        int studentCount = bookingService.countUniqueStudentsByTrainerId(user.getId());
        
        // 添加数据到模型中
        model.addAttribute("courses", courses);
        model.addAttribute("courseCount", courseCount);
        model.addAttribute("todayBookingCount", todayBookingCount);
        model.addAttribute("studentCount", studentCount);
        
        return "coach/index";
    }

    /**
     * 查看教练负责的所有课程
     * @param model 模型对象，用于传递数据到视图
     * @param session HTTP会话，用于获取当前登录用户信息
     * @return 课程列表页面
     */
    @GetMapping("courses")
    @PreAuthorize("hasRole('TRAINER')")
    public String viewCourses(Model model) {
        // 获取当前登录的教练用户
        User user = getCurrentUser();
        // 获取当前教练的所有课程
        List<Course> courses = courseService.getMyCourses(user.getId());
        // 添加课程列表到模型中
        model.addAttribute("courses", courses);
        return "coach/courses";
    }

    /**
     * 查看指定课程的所有预约会员
     * @param courseId 课程ID
     * @param model 模型对象，用于传递数据到视图
     * @return 课程会员列表页面
     */
    @GetMapping("course/{courseId}/members")
    @PreAuthorize("hasRole('TRAINER')")
    public String viewCourseMembers(@PathVariable Long courseId, Model model) {
        // 查询该课程的所有预约会员信息
        List<Map<String, Object>> members = bookingService.getCourseMembers(courseId);
        // 添加会员列表到模型中
        model.addAttribute("members", members);
        model.addAttribute("courseId", courseId);
        return "coach/course-members";
    }
    
    /**
     * 获取当前登录用户信息
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
    
    /**
     * 展示教练个人资料页面
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('TRAINER')")
    public String showProfile(Model model) {
        User currentUser = getCurrentUser();
        
        // 获取教练详细信息
        CoachInfo coachInfo = coachInfoService.getCoachInfoByUserId(currentUser.getId());
        
        // 如果不存在，创建一个新的教练信息对象
        if (coachInfo == null) {
            coachInfo = new CoachInfo();
            coachInfo.setUserId(currentUser.getId());
            coachInfo.setUser(currentUser);
            coachInfo.setPhone(currentUser.getPhone());
            // 使用手机号作为临时名称，因为User类没有name字段
            coachInfo.setName(currentUser.getPhone());
        } else {
            // 确保设置用户对象，以便在更新时正确关联
            coachInfo.setUser(currentUser);
        }
        
        // 添加到模型中
        model.addAttribute("coach", coachInfo);
        
        return "coach/profile";
    }
    
    /**
     * 保存教练个人资料
     */
    @PostMapping("/profile")
    @PreAuthorize("hasRole('TRAINER')")
    public String saveProfile(CoachInfo coachInfo, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        coachInfo.setUserId(currentUser.getId());
        coachInfo.setUser(currentUser);
        
        // 保存或更新教练信息
        coachInfoService.saveOrUpdateCoachInfo(coachInfo);
        
        redirectAttributes.addFlashAttribute("successMessage", "个人资料保存成功");
        return "redirect:/coach/profile";
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/profile/change-password")
    @PreAuthorize("hasRole('TRAINER')")
    public String changePassword(@RequestParam String oldPassword, 
                                @RequestParam String newPassword,
                                RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        
        try {
            // 修改密码
            coachInfoService.updatePassword(currentUser.getId(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "密码修改成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/coach/profile";
    }
    
    /**
     * 预约信息扩展类，用于页面展示
     */
    private static class BookingInfo {
        private Long id;
        private String courseName;
        private String memberName;
        private String memberPhone;
        private Long memberId;
        private LocalDateTime bookingTime;
        private String status;
        
        // getter and setter
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getMemberName() { return memberName; }
        public void setMemberName(String memberName) { this.memberName = memberName; }
        public String getMemberPhone() { return memberPhone; }
        public void setMemberPhone(String memberPhone) { this.memberPhone = memberPhone; }
        public Long getMemberId() { return memberId; }
        public void setMemberId(Long memberId) { this.memberId = memberId; }
        public LocalDateTime getBookingTime() { return bookingTime; }
        public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    /**
     * 查看教练所有课程的预约记录
     * 支持分页和筛选
     */
    @GetMapping("bookings")
    @PreAuthorize("hasRole('TRAINER')")
    public String viewBookings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status,
            Model model) {
        // 获取当前登录的教练用户
        User currentUser = getCurrentUser();
        
        // 获取当前教练的所有课程
        List<Course> courses = courseService.getMyCourses(currentUser.getId());
        
        // 从课程中提取课程ID列表
        List<Long> courseIds = new ArrayList<>();
        for (Course course : courses) {
            courseIds.add(course.getId());
        }
        
        // 构建预约信息列表
        List<BookingInfo> allBookings = new ArrayList<>();
        
        // 查询每个课程的预约记录
        for (Long courseId : courseIds) {
            List<Map<String, Object>> courseBookings = bookingService.getCourseMembers(courseId);
            
            // 从已获取的courses列表中查找对应的课程
            Course course = courses.stream()
                .filter(c -> c.getId().equals(courseId))
                .findFirst()
                .orElse(null);
            
            if (course == null) continue; // 如果未找到对应课程，则跳过
            
            // 转换为BookingInfo对象
            for (Map<String, Object> booking : courseBookings) {
                BookingInfo bookingInfo = new BookingInfo();
                // 安全的类型转换
                Object bookingIdObj = booking.get("booking_id");
                if (bookingIdObj != null) {
                    bookingInfo.setId(bookingIdObj instanceof Long ? (Long) bookingIdObj : Long.valueOf(bookingIdObj.toString()));
                }
                
                bookingInfo.setCourseName(course.getName());
                
                Object nameObj = booking.get("name");
                bookingInfo.setMemberName(nameObj != null ? nameObj.toString() : "");
                
                Object phoneObj = booking.get("phone");
                if (phoneObj != null) {
                    bookingInfo.setMemberPhone(phoneObj.toString());
                }
                
                Object userIdObj = booking.get("user_id");
                if (userIdObj != null) {
                    bookingInfo.setMemberId(userIdObj instanceof Long ? (Long) userIdObj : Long.valueOf(userIdObj.toString()));
                }
                
                Object bookingTimeObj = booking.get("booking_time");
                if (bookingTimeObj instanceof LocalDateTime) {
                    bookingInfo.setBookingTime((LocalDateTime) bookingTimeObj);
                }
                
                bookingInfo.setStatus("BOOKED"); // 默认状态为已预约
                
                // 应用筛选条件
                boolean matchKeyword = keyword == null || keyword.isEmpty() || 
                        bookingInfo.getMemberPhone().contains(keyword) || 
                        bookingInfo.getCourseName().contains(keyword) ||
                        bookingInfo.getMemberName().contains(keyword);
                
                boolean matchDate = date == null || date.isEmpty() || 
                        bookingInfo.getBookingTime().toLocalDate().toString().equals(date);
                
                boolean matchStatus = status == null || status.isEmpty() || 
                        bookingInfo.getStatus().equals(status);
                
                if (matchKeyword && matchDate && matchStatus) {
                    allBookings.add(bookingInfo);
                }
            }
        }
        
        // 分页处理
        int pageSize = 10;
        int totalItems = allBookings.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        // 确保页码有效
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        // 获取当前页的数据
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<BookingInfo> currentPageBookings;
        
        if (startIndex < totalItems) {
            currentPageBookings = allBookings.subList(startIndex, endIndex);
        } else {
            currentPageBookings = new ArrayList<>();
        }
        
        // 添加数据到模型中
        model.addAttribute("bookings", currentPageBookings);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("total", totalItems);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("date", date != null ? date : "");
        model.addAttribute("status", status != null ? status : "");
        
        return "coach/bookings";
    }
    
    /**
     * 取消预约
     */
    @PostMapping("bookings/cancel/{bookingId}")
    @PreAuthorize("hasRole('TRAINER')")
    public String cancelBooking(@PathVariable Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            // 调用服务层取消预约
            bookingService.cancelBooking(bookingId);
            redirectAttributes.addFlashAttribute("message", "取消预约成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "取消预约失败: " + e.getMessage());
        }
        
        // 重定向回预约列表页面
        return "redirect:/coach/bookings";
    }
    
    /**
     * 获取会员详情（AJAX接口）
     */
    @GetMapping("members/{memberId}")
    @PreAuthorize("hasRole('TRAINER')")
    @ResponseBody
    public Map<String, Object> getMemberDetails(@PathVariable Long memberId) {
        // 注意：UserMapper中目前没有findById方法，这里需要修改为合适的查询方式
        // 在实际实现中，应该在UserMapper中添加findById方法或使用其他方式获取用户信息
        // 这里暂时返回基本信息框架
        Map<String, Object> memberDetails = new HashMap<>();
        
        // 暂时返回带有ID的基本信息
        memberDetails.put("id", memberId);
        memberDetails.put("name", "会员" + memberId);
        memberDetails.put("message", "注意：需要在UserMapper中添加findById方法以获取完整的会员信息");
        
        return memberDetails;
    }
}