package com.jk51.interceptor;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author zy
 * 自定义maybatis拦截器：拦截所有的sql语句
 */
@Intercepts(value = {
        @Signature(type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class,
                method = "count",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class,
                        CacheKey.class, BoundSql.class})})
public class DBOperationInterceptor implements Interceptor {
    private static final Logger logger = Logger.getLogger(DBOperationInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object result = null;
        if (target instanceof Executor) {
            //统计方法执行时间
//            long start = System.currentTimeMillis();
            Method method = invocation.getMethod();
            /**执行方法*/
            result = invocation.proceed();
//            long end = System.currentTimeMillis();
            final Object[] args = invocation.getArgs();

            //获取原始的ms
            MappedStatement ms = (MappedStatement) args[0];
            String commandName = ms.getSqlCommandType().name();
//            String name = method.getName();
            String name = "";
            if(commandName.startsWith("INSERT")){
//                name+="=新增";
                name = "INSERT=新增";
            }else if(commandName.startsWith("UPDATE")){
//                name+="=修改";
                name = "UPDATE=修改";
            }else if(commandName.startsWith("DELETE")){
//                name+="=删除";
                name = "DELETE=删除";
            }else if(commandName.startsWith("SELECT")){
//                name+="=查询";
                name = "SELECT=查询";
            }
            //打印出sql语句的执行时间
//            String message = "[SqlInterceptor] execute [" + name + "] cost [" + (end - start) + "] ms";
            StringBuffer stringBuffer = new StringBuffer();
//            stringBuffer.append(message);
            //在控制台换行
            stringBuffer.append("\n");

            //数组中第二个为参数对象
            Object parameterObject = args[1];
            //根据参数对象获取绑定的sql对象
            BoundSql boundSql = ms.getBoundSql(parameterObject);
            //获取sql语句
            String sql = boundSql.getSql();
            //获取参数映射
//            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
//            String parameterObjects = JSON.toJSONString(boundSql.getParameterObject());


            //执行的方法的id
//            String id = ms.getId();
//            stringBuffer.append("id="+id);
//            stringBuffer.append("\r\n");

            stringBuffer.append("执行的SQL语句为: "+sql);
            stringBuffer.append("\n");
            stringBuffer.append("本次执行操作为: [" + name + "]");
            stringBuffer.append("\n");
            //操作的数据库表
            //把sql语句分割成字符串数组
            String[] split = sql.split(" ");
            //一条sql语句可能操作多张表
            //遍历数组获取所有操作的表
            // 新建 MySQL Parser
            //新建mysql parser
            SQLStatementParser parser = new MySqlStatementParser(sql);

            // 使用Parser解析生成AST，这里SQLStatement就是AST
            SQLStatement statement = parser.parseStatement();

            // 使用visitor来访问AST
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            statement.accept(visitor);
//            String currentTable = visitor.getCurrentTable();

            stringBuffer.append("操作的表为: "+ visitor.getTables());
            /*for(int i =0; i < split.length; i++) {
                if(split[i].equalsIgnoreCase("from") || split[i].equalsIgnoreCase("join") || split[i].equalsIgnoreCase("update") || split[i].equalsIgnoreCase("into") ) {
                    //进来说明当前字符串为from,不区分大小写
//                    System.out.println(split[i+1]);
                    stringBuffer.append("["+split[i+1]+"] ");
                }
            }*/
            stringBuffer.append("\n");

//            stringBuffer.append("parameterMappings="+parameterMappings);
//            stringBuffer.append("\n");

            //获取参数值
//            stringBuffer.append("parameterObjects="+parameterObjects);
//            stringBuffer.append("\n");
            // stringBuffer.append("result="+result);
            /*if(result!=null) {
                if (result instanceof List) {
                    //查询结果中List集合的大小
                    stringBuffer.append("result=" + ((List) result).size());
                } else if (result instanceof Collection) {
                    //查询结果为Collection集合的大小
                    stringBuffer.append("result=" + ((Collection) result).size());
                } else {
                    //否则默认为1
                    stringBuffer.append("result=" + 1);
                }
            }else{
                stringBuffer.append("result=NULL");
            }
            stringBuffer.append("\n");*/
            logger.warn(stringBuffer.toString());
            //数组可能为空
            // ParameterMapping mapping = boundSql.getParameterMappings().get(0);
            // Configuration configuration = ms.getConfiguration();
            //  DynamicContext context = new DynamicContext(configuration, parameterObject);
            //   String originSql = context.getSql();
            //  System.out.println("@@@@originSql:"+originSql);
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
    /*public static void main(String args[]){
        System.out.println("你好\n我不好");
    }*/
}
