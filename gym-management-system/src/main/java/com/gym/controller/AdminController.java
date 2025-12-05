package com.gym.controller;

import com.gym.entity.CoachInfo;
import com.gym.entity.User;
import com.gym.mapper.CoachInfoMapper;
import com.gym.mapper.UserMapper;
import com.gym.service.UserService;
import com.gym.service.CourseService;
import com.gym.service.MembershipCardService;
import com.gym.service.CoachInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员控制器
 * 处理管理员相关的HTTP请求
 */
@Controller
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final MembershipCardService membershipCardService;
    private final CoachInfoMapper coachInfoMapper;
    private final CoachInfoService coachInfoService;
    private final UserMapper userMapper;

    @Autowired
    public AdminController(UserService userService, CourseService courseService, MembershipCardService membershipCardService, CoachInfoMapper coachInfoMapper, CoachInfoService coachInfoService, UserMapper userMapper) {
        this.userService = userService;
        this.courseService = courseService;
        this.membershipCardService = membershipCardService;
        this.coachInfoMapper = coachInfoMapper;
        this.coachInfoService = coachInfoService;
        this.userMapper = userMapper;
    }

    /**
     * 显示管理员首页
     * @param model 模型对象，用于传递数据到视图
     * @return 管理员首页视图名称
     */
    @GetMapping({"/admin", "/admin/index"})
    @PreAuthorize("hasRole('ADMIN')")
    public String index(Model model) {
        // 确保模型中始终有必要的属性，防止从其他页面返回时属性丢失
        if (!model.containsAttribute("memberCount")) {
            try {
                // 获取会员数量
                int memberCount = userService.countMembers();
                model.addAttribute("memberCount", memberCount);
                
                // 获取教练数量
                int coachCount = userService.countTrainers();
                model.addAttribute("coachCount", coachCount);
                
                // 获取课程数量
                int courseCount = courseService.countCourses();
                model.addAttribute("courseCount", courseCount);
                
                // 获取会员卡类型数量
                int cardTypeCount = membershipCardService.countCardTypes();
                model.addAttribute("cardTypeCount", cardTypeCount);
            } catch (Exception e) {
                // 如果发生异常，设置默认值
                model.addAttribute("memberCount", 0);
                model.addAttribute("coachCount", 0);
                model.addAttribute("courseCount", 0);
                model.addAttribute("cardTypeCount", 0);
            }
        }
        
        // 确保返回完整的视图路径
        return "admin/index";
    }

    /**
     * 显示会员列表页面
     * @param name 会员姓名
     * @param phone 会员手机号
     * @param status 会员状态
     * @param cardType 会员卡类型
     * @param model 模型对象
     * @return 会员列表视图名称
     */
    @GetMapping("/admin/members")
    @PreAuthorize("hasRole('ADMIN')")
    public String listMembers(Model model, 
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String phone,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String cardType) {
        // 创建查询参数Map
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("phone", phone);
        params.put("status", status);
        params.put("cardType", cardType);
        
        // 调用多条件搜索方法
        List<User> members = userService.searchMembers(params);
        
        // 将搜索条件和结果添加到模型
        model.addAttribute("members", members);
        model.addAttribute("name", name);
        model.addAttribute("phone", phone);
        model.addAttribute("status", status);
        model.addAttribute("cardType", cardType);
        
        // 获取会员总数
        model.addAttribute("totalMembers", userService.countMembers());
        return "admin/members";
    }

    /**
     * 禁用会员账号
     * @param id 会员ID
     * @param attributes 重定向属性
     * @return 重定向到会员列表页面
     */
    @PostMapping("/admin/members/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public String disableMember(@PathVariable("id") Long id, RedirectAttributes attributes) {
        userService.disableUser(id);
        attributes.addFlashAttribute("message", "会员账号已成功禁用");
        return "redirect:/admin/members";
    }
    
    /**
     * 获取会员详情
     */
    @GetMapping("/admin/members/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public User getMember(@PathVariable Long id) {
        return userService.getMemberById(id);
    }

    /**
     * 添加会员
     */
    @PostMapping("/admin/members")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Map<String, Object> addMember(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 设置默认密码为手机号后6位
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(user.getPhone().substring(user.getPhone().length() - 6));
            }
            userService.addMember(user);
            result.put("success", true);
            result.put("message", "会员添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "会员添加失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 更新会员信息
     */
    @PutMapping("/admin/members/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Map<String, Object> updateMember(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            user.setId(id);
            userService.updateMember(user);
            result.put("success", true);
            result.put("message", "会员更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "会员更新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 删除会员
     */
    @DeleteMapping("/admin/members/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Map<String, Object> deleteMember(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.deleteMember(id);
            result.put("success", true);
            result.put("message", "会员删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "会员删除失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 显示所有教练列表
     * @param model 模型对象，用于传递数据到视图
     * @return 教练列表页面
     */
    @GetMapping("/admin/coaches")
    @PreAuthorize("hasRole('ADMIN')")
    public String listCoaches(Model model) {
        List<User> coaches = userService.findTrainers();
        
        // 创建一个包含所有必要字段的列表
        List<Map<String, Object>> coachList = new ArrayList<>();
        for (User user : coaches) {
            Map<String, Object> coachInfo = new HashMap<>();
            coachInfo.put("id", user.getId());
            coachInfo.put("phone", user.getPhone());
            coachInfo.put("name", user.getName() != null ? user.getName() : "未设置");
            coachInfo.put("gender", user.getGender() != null ? user.getGender() : "未知");
            coachInfo.put("avatar", user.getAvatar());
            coachInfo.put("status", user.getStatus() != null ? user.getStatus() : "active");
            
            // 添加模板需要的额外字段
            coachInfo.put("title", "健身教练"); // 默认职称
            
            // 使用注入的coachInfoMapper从coach_info表获取专长信息
            CoachInfo details = coachInfoMapper.findByUserId(user.getId());
            if (details != null && details.getSpecialty() != null) {
                coachInfo.put("specialty", details.getSpecialty());
            } else {
                coachInfo.put("specialty", "未设置");
            }
            
            coachList.add(coachInfo);
        }
        
        model.addAttribute("coaches", coachList);
        return "admin/coaches";
    }
    
    /**
     * 创建教练用户
     * @param name 姓名
     * @param phone 手机号
     * @param gender 性别
     * @param specialty 专长
     * @param status 状态
     * @param description 描述
     * @return JSON响应结果
     */
    @PostMapping("/admin/coaches")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Map<String, Object> createCoach(@RequestParam("name") String name,
                                          @RequestParam("phone") String phone,
                                          @RequestParam("gender") String gender,
                                          @RequestParam("specialty") String specialty,
                                          @RequestParam("status") String status,
                                          @RequestParam("description") String description,
                                          @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                          @RequestParam(value = "age", required = false) Integer age,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "salary", required = false) BigDecimal salary) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查手机号是否已存在
            User existingUser = userService.findByPhone(phone);
            if (existingUser != null) {
                result.put("success", false);
                result.put("message", "手机号已被注册");
                return result;
            }

            // 检查手机号长度是否足够生成默认密码
            if (phone.length() < 6) {
                result.put("success", false);
                result.put("message", "手机号长度不足，无法生成默认密码");
                return result;
            }

            // 为新教练生成默认密码（手机号后6位）
            String defaultPassword = phone.substring(phone.length() - 6);

            // 创建教练用户
            userService.createTrainer(phone, defaultPassword);

            // 获取刚刚创建的用户对象
            User user = userService.findByPhone(phone);
            if (user != null) {
                // 创建教练详细信息
                CoachInfo coachInfo = new CoachInfo();
                coachInfo.setUserId(user.getId());
                coachInfo.setName(name);
                coachInfo.setGender(gender);
                coachInfo.setSpecialty(specialty);
                coachInfo.setStatus(status);
                coachInfo.setIntroduction(description);
                coachInfo.setCreatedAt(LocalDateTime.now());
                coachInfo.setUpdatedAt(LocalDateTime.now());

                // 处理头像文件
                if (avatar != null && !avatar.isEmpty()) {
                    // 这里可以添加头像文件上传逻辑
                    // 暂时不处理头像上传
                }

                // 保存教练详细信息
                coachInfoService.saveOrUpdateCoachInfo(coachInfo);
            }

            result.put("success", true);
            result.put("message", "教练创建成功，初始密码为手机号后6位");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "教练创建失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 逻辑删除教练（更新状态为禁用）
     * @param id 教练ID
     * @return JSON响应结果
     */
    @DeleteMapping("/admin/coaches/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Map<String, Object> deleteCoach(@PathVariable("id") Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.disableUser(id);
            result.put("success", true);
            result.put("message", "教练账号已成功禁用");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "教练账号禁用失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 更新教练信息
     */
    @PutMapping("/admin/coaches/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Map<String, Object> updateCoach(@PathVariable Long id, @RequestBody Map<String, Object> coachData) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 更新用户表中的基本信息
            User user = userService.getMemberById(id);
            if (user == null || !"trainer".equals(user.getRole())) {
                throw new RuntimeException("教练不存在");
            }
            
            // 更新基本信息
            if (coachData.containsKey("name")) {
                user.setName((String) coachData.get("name"));
            }
            if (coachData.containsKey("phone")) {
                user.setPhone((String) coachData.get("phone"));
            }
            if (coachData.containsKey("gender")) {
                user.setGender((String) coachData.get("gender"));
            }
            if (coachData.containsKey("status")) {
                user.setStatus((String) coachData.get("status"));
            }
            userService.updateMember(user);
            
            // 更新教练详细信息
            CoachInfo coachInfo = coachInfoMapper.findByUserId(id);
            if (coachInfo == null) {
                coachInfo = new CoachInfo();
                coachInfo.setUserId(id);
                coachInfo.setUser(user);
            }
            
            // 更新教练详细信息字段
            if (coachData.containsKey("name")) {
                coachInfo.setName((String) coachData.get("name"));
            }
            if (coachData.containsKey("phone")) {
                coachInfo.setPhone((String) coachData.get("phone"));
            }
            if (coachData.containsKey("gender")) {
                coachInfo.setGender((String) coachData.get("gender"));
            }
            if (coachData.containsKey("specialty")) {
                coachInfo.setSpecialty((String) coachData.get("specialty"));
            }
            if (coachData.containsKey("title")) {
                // 可以根据需要扩展，目前title字段在前端是固定的
            }
            if (coachData.containsKey("description")) {
                coachInfo.setIntroduction((String) coachData.get("description"));
            }
            
            // 保存更新后的教练详细信息
            coachInfoMapper.update(coachInfo);
            
            result.put("success", true);
            result.put("message", "教练信息更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "教练信息更新失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取教练详情
     * @param id 教练ID
     * @return 教练详细信息
     */
    @GetMapping("/admin/coaches/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Map<String, Object> getCoachDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取教练用户信息
            User coach = userMapper.selectById(id);
            if (coach == null || !"trainer".equals(coach.getRole())) {
                result.put("success", false);
                result.put("message", "未找到该教练信息");
                return result;
            }
            
            // 获取教练详情信息
            CoachInfo coachInfo = coachInfoMapper.findByUserId(id);
            
            // 构建返回数据
            result.put("success", true);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", coach.getId());
            
            // 优先从coach_info表获取name信息，如果不存在则使用user表或默认值
            String coachName = (coachInfo != null && coachInfo.getName() != null && !coachInfo.getName().trim().isEmpty()) ? 
                coachInfo.getName() : 
                (coach.getName() != null && !coach.getName().trim().isEmpty() ? coach.getName() : "未设置姓名");
            dataMap.put("name", coachName);
            
            dataMap.put("phone", coach.getPhone());
            
            // 优先从coach_info表获取gender信息，如果不存在则使用user表或默认值
            String coachGender = (coachInfo != null && coachInfo.getGender() != null && !coachInfo.getGender().trim().isEmpty()) ? 
                coachInfo.getGender() : 
                (coach.getGender() != null && !coach.getGender().trim().isEmpty() ? coach.getGender() : "未设置性别");
            dataMap.put("gender", coachGender);
            
            dataMap.put("avatar", coachInfo != null && coachInfo.getAvatar() != null ? "/uploads/" + coachInfo.getAvatar() : "/static/images/default-avatar.png");
            dataMap.put("status", coach.getStatus() != null ? coach.getStatus() : "active");
            dataMap.put("title", coachInfo != null && coachInfo.getSpecialty() != null ? coachInfo.getSpecialty() : "健身教练");
            dataMap.put("specialty", coachInfo != null && coachInfo.getSpecialty() != null ? coachInfo.getSpecialty() : "未设置");
            dataMap.put("description", coachInfo != null && coachInfo.getIntroduction() != null ? coachInfo.getIntroduction() : "暂无简介");
            result.put("data", dataMap);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取教练详情失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 显示课程管理页面
     * @param model 模型对象
     * @return 课程管理视图名称
     */
    @GetMapping("/admin/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCourses(Model model) {
        // 获取所有教练列表
        List<User> trainers = userService.findTrainers();
        // 获取所有可用课程
        List<com.gym.entity.Course> courses = courseService.getAvailableCourses();
        model.addAttribute("trainers", trainers);
        model.addAttribute("courses", courses);
        return "admin/courses";
    }
    

}
