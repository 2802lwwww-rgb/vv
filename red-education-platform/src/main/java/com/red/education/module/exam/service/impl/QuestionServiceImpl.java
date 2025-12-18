package com.red.education.module.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.exception.BusinessException;
import com.red.education.module.exam.dto.QuestionDTO;
import com.red.education.module.exam.entity.Question;
import com.red.education.module.exam.mapper.QuestionMapper;
import com.red.education.module.exam.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 题目Service实现类
 */
@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Page<Question> listQuestions(Integer current, Integer size, String type, Integer difficulty,
            String category) {
        Page<Question> page = new Page<>(current, size);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 条件筛选
        if (StringUtils.hasText(type)) {
            wrapper.eq(Question::getType, type);
        }
        if (difficulty != null) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Question::getCategory, category);
        }

        wrapper.orderByAsc(Question::getId);
        return questionMapper.selectPage(page, wrapper);
    }

    @Override
    public Question getQuestionById(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException("题目不存在");
        }
        return question;
    }

    @Override
    public void addQuestion(QuestionDTO questionDTO) {
        Question question = new Question();
        BeanUtil.copyProperties(questionDTO, question);
        questionMapper.insert(question);
        log.info("添加题目成功：{}", question.getId());
    }

    @Override
    public void updateQuestion(Long id, QuestionDTO questionDTO) {
        Question question = getQuestionById(id);
        BeanUtil.copyProperties(questionDTO, question);
        questionMapper.updateById(question);
        log.info("更新题目成功：{}", id);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionMapper.deleteById(id);
        log.info("删除题目成功：{}", id);
    }

    @Override
    public int batchImport(MultipartFile file) throws Exception {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<Map<String, Object>> rows = reader.readAll();

        List<Question> questions = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Question question = new Question();
            question.setType(String.valueOf(row.getOrDefault("类型", "SINGLE_CHOICE")));
            question.setContent(String.valueOf(row.getOrDefault("题目内容", "")));
            question.setOptionA(String.valueOf(row.getOrDefault("选项A", "")));
            question.setOptionB(String.valueOf(row.getOrDefault("选项B", "")));
            question.setOptionC(row.get("选项C") != null ? String.valueOf(row.get("选项C")) : null);
            question.setOptionD(row.get("选项D") != null ? String.valueOf(row.get("选项D")) : null);
            question.setCorrectAnswer(String.valueOf(row.getOrDefault("正确答案", "")));
            question.setExplanation(row.get("答案解析") != null ? String.valueOf(row.get("答案解析")) : null);

            Object diffObj = row.get("难度");
            question.setDifficulty(diffObj != null ? Integer.parseInt(String.valueOf(diffObj)) : 1);

            Object scoreObj = row.get("分值");
            question.setScore(scoreObj != null ? Integer.parseInt(String.valueOf(scoreObj)) : 5);

            question.setCategory(row.get("分类") != null ? String.valueOf(row.get("分类")) : null);

            if (StringUtils.hasText(question.getContent())) {
                questions.add(question);
            }
        }

        for (Question question : questions) {
            questionMapper.insert(question);
        }

        log.info("批量导入题目成功：共{}道", questions.size());
        return questions.size();
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) throws Exception {
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 设置表头
        writer.addHeaderAlias("type", "类型");
        writer.addHeaderAlias("content", "题目内容");
        writer.addHeaderAlias("optionA", "选项A");
        writer.addHeaderAlias("optionB", "选项B");
        writer.addHeaderAlias("optionC", "选项C");
        writer.addHeaderAlias("optionD", "选项D");
        writer.addHeaderAlias("correctAnswer", "正确答案");
        writer.addHeaderAlias("explanation", "答案解析");
        writer.addHeaderAlias("difficulty", "难度");
        writer.addHeaderAlias("score", "分值");
        writer.addHeaderAlias("category", "分类");

        // 写入示例数据
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("类型", "题目内容", "选项A", "选项B", "选项C", "选项D", "正确答案", "答案解析", "难度", "分值", "分类"));
        rows.add(List.of("SINGLE_CHOICE", "中国共产党成立于哪一年?", "1919年", "1921年", "1922年", "1927年", "B",
                "中国共产党正式成立于1921年7月23日", "1", "5", "党史"));
        rows.add(List.of("JUDGE", "毛泽东是中国共产党的主要创始人之一", "正确", "错误", "", "", "A", "毛泽东是中国共产党的主要缔造者", "1", "5", "党史"));

        writer.write(rows, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("题目导入模板.xlsx", "UTF-8"));

        writer.flush(response.getOutputStream(), true);
        writer.close();
    }
}
