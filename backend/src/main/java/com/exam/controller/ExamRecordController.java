package com.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.Result;
import com.exam.entity.AnswerRecord;
import com.exam.entity.ExamRecord;
import com.exam.entity.User;
import com.exam.service.ExamRecordService;
import com.exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exam-record")
@RequiredArgsConstructor
public class ExamRecordController {

    private final ExamRecordService examRecordService;
    private final UserService userService;

    /** 开始考试 */
    @PostMapping("/start")
    public Result<ExamRecord> start(@AuthenticationPrincipal(errorOnInvalidType = false) String username, @RequestParam Long examId) {
        User user = userService.getByUsername(username);
        if (user == null) return Result.fail("用户不存在");
        ExamRecord record = examRecordService.startExam(user.getId(), examId);
        return Result.ok(record);
    }

    /** 提交答案。body 的 key 为题目 id（可字符串或数字），value 为考生答案 */
    @PostMapping("/{recordId}/submit")
    public Result<Void> submit(@AuthenticationPrincipal(errorOnInvalidType = false) String username,
                               @PathVariable Long recordId,
                               @RequestBody Map<String, String> answers) {
        User user = userService.getByUsername(username);
        if (user == null) return Result.fail("用户不存在");
        Map<Long, String> map = Map.of();
        if (answers != null && !answers.isEmpty()) {
            map = answers.entrySet().stream()
                    .filter(e -> e.getKey() != null && e.getValue() != null)
                    .collect(Collectors.toMap(e -> Long.parseLong(e.getKey().trim()), Map.Entry::getValue, (a, b) -> b));
        }
        examRecordService.submitAnswer(recordId, user.getId(), map);
        return Result.ok();
    }

    /** 教师批改主观题 */
    @PutMapping("/{recordId}/grade")
    public Result<Void> grade(@PathVariable Long recordId, @RequestBody Map<Long, Integer> scores) {
        examRecordService.gradeSubjective(recordId, scores);
        return Result.ok();
    }

    @GetMapping("/my")
    public Result<List<ExamRecord>> myRecords(@AuthenticationPrincipal(errorOnInvalidType = false) String username) {
        User user = userService.getByUsername(username);
        if (user == null) return Result.fail("用户不存在");
        return Result.ok(examRecordService.listByUserId(user.getId()));
    }

    @GetMapping("/exam/{examId}")
    public Result<Page<ExamRecord>> pageByExam(@PathVariable Long examId,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(examRecordService.pageByExamId(examId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ExamRecord> getById(@PathVariable Long id) {
        return Result.ok(examRecordService.getById(id));
    }

    @GetMapping("/{id}/answers")
    public Result<List<AnswerRecord>> getAnswers(@PathVariable Long id) {
        return Result.ok(examRecordService.listAnswerByRecordId(id));
    }
}
