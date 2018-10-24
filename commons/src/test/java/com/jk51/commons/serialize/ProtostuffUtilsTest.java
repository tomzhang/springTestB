package com.jk51.commons.serialize;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-02-20
 * 修改记录:
 */
public class ProtostuffUtilsTest {

    private Logger logger = LoggerFactory.getLogger(ProtostuffUtilsTest.class);

    @Test
    public void testSerializeAndDe() throws SerializeException {
        byte[] bytes = ProtostuffUtils.serialize(new ProtostuffTestInstance("U100063000001", "小熊"));
        logger.info("序列化后的字节码:{}", new String(bytes));
        logger.info("deserialize(Class<T> clazz,byte[] bytes) 反序列化后的对象:{}",
                ProtostuffUtils.deserialize(ProtostuffTestInstance.class, bytes));
        ProtostuffTestInstance user = new ProtostuffTestInstance();
        ProtostuffUtils.deserialize(user, bytes);
        logger.info("ProtostuffUtils.deserialize(T instance,byte[] bytes) 反序列化后的对象:{}", user);
    }

    public static class ProtostuffTestInstance {

        private String userId;

        private String name;

        public ProtostuffTestInstance() {

        }

        public ProtostuffTestInstance(String userId, String name) {
            this.userId = userId;
            this.name = name;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("User{");
            sb.append("userId='").append(userId).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
