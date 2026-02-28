package com.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.Result;
import com.exam.entity.User;
import com.exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public Result<User> info(@AuthenticationPrincipal(errorOnInvalidType = false) String username) {
        if (username == null) return Result.fail("未登录");
        User user = userService.getByUsername(username);
        if (user != null) user.setPassword(null);
        return Result.ok(user);
    }

    @PutMapping("/info")
    public Result<Void> updateInfo(@AuthenticationPrincipal(errorOnInvalidType = false) String username, @RequestBody User dto) {
        User user = userService.getByUsername(username);
        if (user == null) return Result.fail("用户不存在");
        if (dto.getRealName() != null) user.setRealName(dto.getRealName());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        userService.save(user);
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@AuthenticationPrincipal(errorOnInvalidType = false) String username,
                                       @RequestParam String oldPassword, @RequestParam String newPassword) {
        userService.updatePasswordByUsername(username, oldPassword, newPassword);
        return Result.ok();
    }

    @GetMapping("/page")
    public Result<Page<User>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String role) {
        Page<User> page = userService.page(pageNum, pageSize, keyword, role);
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(page);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) user.setPassword(null);
        return Result.ok(user);
    }

    @PostMapping
    public Result<Void> save(@RequestBody User user) {
        userService.save(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return Result.ok();
    }

    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String password) {
        userService.resetPassword(id, password);
        return Result.ok();
    }
}
