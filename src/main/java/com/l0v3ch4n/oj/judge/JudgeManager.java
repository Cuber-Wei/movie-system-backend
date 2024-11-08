package com.l0v3ch4n.oj.judge;

import com.l0v3ch4n.oj.judge.sandbox.model.JudgeInfo;
import com.l0v3ch4n.oj.judge.strategy.DefaultJudgeStrategy;
import com.l0v3ch4n.oj.judge.strategy.JavaLanguageJudgeStrategy;
import com.l0v3ch4n.oj.judge.strategy.JudgeContext;
import com.l0v3ch4n.oj.judge.strategy.JudgeStrategy;
import com.l0v3ch4n.oj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;
/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
