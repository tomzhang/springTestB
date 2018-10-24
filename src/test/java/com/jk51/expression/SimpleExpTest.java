package com.jk51.expression;

import com.jk51.expression.model.Member;
import com.jk51.expression.model.Site;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.time.Instant;


/**
 * 文件名:com.jk51.expression.
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-01-20
 * 修改记录:
 */
public class SimpleExpTest {

    public static final Logger logger = LoggerFactory.getLogger(SimpleExpTest.class);

    private Site site;

    @Before
    public void buildParams(){
        site = new Site(100043L,"宏仁堂");
        site.getMembers().add(new Member(1L,"小1",13812345671L));
        site.getMembers().add(new Member(2L,"小2",13812345672L));
        site.getMembers().add(new Member(3L,"小3",13812345673L));
        site.getMembers().add(new Member(4L,"小4",13812345674L));
    }

    @Test
    public void testExp(){

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("s",site);
        String exp = "'连锁名称:'.concat(#s.name)+',总会员数:'+#s.members.size()+'," +
                "第三个会员的手机号码是:'+#s.members[2].mobile+'再测试下bool判断,'+ (#s.members[2].mobile > 123)";
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(exp);
        long start = Instant.now().toEpochMilli();
        Object value = expression.getValue(context);
        Assert.assertEquals("连锁名称:宏仁堂,总会员数:4,第三个会员的手机号码是:13812345673再测试下bool判断,true",value);
        logger.info("表达式[{}]执行结果:\n{}，耗时:{}毫秒",exp,value,(Instant.now().toEpochMilli()-start));
    }

    @Test
    public void testRootObject(){
        String exp = "'连锁名称:'.concat(name)+',总会员数:'+members.size()+'," +
                "第三个会员的手机号码是:'+members[2].mobile+'再测试下bool判断,'+ (members[2].mobile > 123)";
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setRootObject(site);

        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(exp);
        long start = Instant.now().toEpochMilli();
        Object value = expression.getValue(context);
        Assert.assertEquals("连锁名称:宏仁堂,总会员数:4,第三个会员的手机号码是:13812345673再测试下bool判断,true",value);
        logger.info("表达式[{}]执行结果:\n{}，耗时:{}毫秒",exp,value,(Instant.now().toEpochMilli()-start));
    }


    @After
    public void clean(){
        site.getMembers().clear();
        site = null;
    }

}
