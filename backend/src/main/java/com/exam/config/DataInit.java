package com.exam.config;

import com.exam.entity.User;
import com.exam.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInit {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initAdmin() {
        return args -> {
            insertIfNotExists("admin", "admin123", "admin", "管理员");
            insertIfNotExists("teacher", "123456", "teacher", "张老师");
            insertIfNotExists("student1", "123456", "student", "李明");
            insertIfNotExists("student2", "123456", "student", "王芳");
        };
    }

    private void insertIfNotExists(String username, String password, String role, String realName) {
        Long n = userMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username));
        if (n == null || n == 0) {
            User u = new User();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(password));
            u.setRole(role);
            u.setRealName(realName);
            userMapper.insert(u);
        }
    }
}
