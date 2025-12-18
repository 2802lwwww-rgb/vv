package com.red.education.module.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.red.education.common.exception.BusinessException;
import com.red.education.common.utils.EmailUtils;
import com.red.education.common.utils.JwtUtils;
import com.red.education.common.utils.RedisUtils;
import com.red.education.module.user.dto.UserLoginDTO;
import com.red.education.module.user.dto.UserRegisterDTO;
import com.red.education.module.user.dto.UserUpdateDTO;
import com.red.education.module.user.entity.User;
import com.red.education.module.user.mapper.UserMapper;
import com.red.education.module.user.service.LoginLogService;
import com.red.education.module.user.service.UserService;
import com.red.education.module.user.vo.UserInfoVO;
import com.red.education.module.user.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 用户Service实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO registerDTO) {
        // 验证用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, registerDTO.getUsername());
        if (this.count(usernameWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 验证邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, registerDTO.getEmail());
        if (this.count(emailWrapper) > 0) {
            throw new BusinessException("邮箱已被注册");
        }

        // 验证两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次密码不一致");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setNickname(registerDTO.getUsername()); // 默认昵称为用户名
        user.setRole("USER");
        user.setPoints(0);
        user.setStatus(1);

        this.save(user);
    }

    @Override
    public UserLoginVO login(UserLoginDTO loginDTO) {
        HttpServletRequest request = getHttpServletRequest();

        // 查询用户（支持用户名或邮箱登录）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(User::getUsername, loginDTO.getUsernameOrEmail())
                .or().eq(User::getEmail, loginDTO.getUsernameOrEmail()));
        User user = this.getOne(wrapper);

        if (user == null) {
            // 记录登录失败日志
            loginLogService.recordLoginLog(null, 0, "用户不存在", request);
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            // 记录登录失败日志
            loginLogService.recordLoginLog(user.getId(), 0, "密码错误", request);
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            // 记录登录失败日志
            loginLogService.recordLoginLog(user.getId(), 0, "账号已被禁用", request);
            throw new BusinessException("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 记录登录成功日志
        loginLogService.recordLoginLog(user.getId(), 1, "登录成功", request);

        // 构建响应
        UserLoginVO loginVO = new UserLoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setRole(user.getRole());
        loginVO.setPoints(user.getPoints());

        return loginVO;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserInfoVO infoVO = new UserInfoVO();
        BeanUtil.copyProperties(user, infoVO);
        return infoVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息
        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getIntro() != null) {
            user.setIntro(updateDTO.getIntro());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }

        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }

    @Override
    public void sendResetCode(String email) {
        // 查询邮箱是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new BusinessException("该邮箱未注册");
        }

        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);

        // 将验证码存入Redis，有效期5分钟
        String redisKey = "reset_code:" + email;
        redisUtils.set(redisKey, code, 5, TimeUnit.MINUTES);

        // 发送邮件
        emailUtils.sendVerificationCode(email, code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String email, String code, String newPassword) {
        // 验证邮箱是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new BusinessException("该邮箱未注册");
        }

        // 验证验证码
        String redisKey = "reset_code:" + email;
        Object savedCode = redisUtils.get(redisKey);

        if (savedCode == null) {
            throw new BusinessException("验证码已过期或不存在");
        }

        if (!code.equals(savedCode.toString())) {
            throw new BusinessException("验证码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);

        // 删除验证码
        redisUtils.delete(redisKey);
    }

    /**
     * 获取当前HttpServletRequest
     */
    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("无法获取请求上下文");
        }
        return attributes.getRequest();
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.red.education.module.user.vo.UserAdminVO> getUserList(
            Integer current, Integer size, String keyword) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                current, size);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getEmail, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> userPage = this.page(page, wrapper);

        // 转换为VO
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.red.education.module.user.vo.UserAdminVO> voPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                userPage.getCurrent(), userPage.getSize(), userPage.getTotal());

        java.util.List<com.red.education.module.user.vo.UserAdminVO> voList = userPage.getRecords().stream()
                .map(this::convertToAdminVO)
                .collect(java.util.stream.Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(Long userId, String role) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证角色有效性
        if (!role.equals("USER") && !role.equals("CONTENT_ADMIN") && !role.equals("SYS_ADMIN")) {
            throw new BusinessException("无效的角色");
        }

        user.setRole(role);
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, Integer status) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证状态有效性
        if (status != 0 && status != 1) {
            throw new BusinessException("无效的状态");
        }

        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminResetPassword(Long userId, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 管理员重置密码，无需验证旧密码
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }

    /**
     * 转换为管理端VO
     */
    private com.red.education.module.user.vo.UserAdminVO convertToAdminVO(User user) {
        com.red.education.module.user.vo.UserAdminVO vo = new com.red.education.module.user.vo.UserAdminVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setPoints(user.getPoints());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        // 设置角色名称
        switch (user.getRole()) {
            case "USER":
                vo.setRoleName("普通用户");
                break;
            case "CONTENT_ADMIN":
                vo.setRoleName("内容管理员");
                break;
            case "SYS_ADMIN":
                vo.setRoleName("系统管理员");
                break;
            default:
                vo.setRoleName("未知");
        }

        // 设置状态名称
        vo.setStatusName(user.getStatus() == 1 ? "正常" : "禁用");

        return vo;
    }
}
