package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.User;
import com.exam.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User getByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    public Page<User> page(Integer pageNum, Integer pageSize, String keyword, String role) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> q = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            q.and(w -> w.like(User::getUsername, keyword).or().like(User::getRealName, keyword));
        }
        if (role != null && !role.isBlank()) {
            q.eq(User::getRole, role);
        }
        q.orderByDesc(User::getId);
        return userMapper.selectPage(page, q);
    }

    public void save(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getId() == null) {
            userMapper.insert(user);
        } else {
            User exist = userMapper.selectById(user.getId());
            if (exist != null) {
                if (user.getPassword() == null || user.getPassword().isBlank()) {
                    user.setPassword(exist.getPassword());
                }
                userMapper.updateById(user);
            }
        }
    }

    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    public void resetPassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userMapper.updateById(user);
        }
    }

    public void updatePasswordByUsername(String username, String oldPassword, String newPassword) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) throw new RuntimeException("用户不存在");
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) throw new RuntimeException("原密码错误");
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }
}
