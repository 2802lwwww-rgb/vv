package com.red.education.module.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.module.exam.dto.QuestionDTO;
import com.red.education.module.exam.entity.Question;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 题目Service接口
 */
public interface QuestionService {

    /**
     * 分页查询题目列表
     */
    Page<Question> listQuestions(Integer current, Integer size, String type, Integer difficulty, String category);

    /**
     * 根据ID查询题目
     */
    Question getQuestionById(Long id);

    /**
     * 添加题目
     */
    void addQuestion(QuestionDTO questionDTO);

    /**
     * 更新题目
     */
    void updateQuestion(Long id, QuestionDTO questionDTO);

    /**
     * 删除题目
     */
    void deleteQuestion(Long id);

    /**
     * 批量导入题目
     */
    int batchImport(MultipartFile file) throws Exception;

    /**
     * 下载导入模板
     */
    void downloadTemplate(HttpServletResponse response) throws Exception;
}
