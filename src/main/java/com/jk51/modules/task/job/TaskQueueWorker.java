package com.jk51.modules.task.job;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.BStores;
import com.jk51.model.StoreAdmin;
import com.jk51.model.task.*;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.task.domain.RewardRule;
import com.jk51.modules.task.domain.TaskPlanHelper;
import com.jk51.modules.task.domain.count.Counts;
import com.jk51.modules.task.domain.reward.RewardResult;
import com.jk51.modules.task.mapper.*;
import com.jk51.modules.task.service.TaskService;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.jk51.model.task.HandleStatus.SUCCESS;
import static com.jk51.model.task.MsgType.COUNT;

/**
 * 任务计划队列处理
 */
@RunMsgWorker(queueName = Counts.QUEUE_NAME)
public class TaskQueueWorker implements MessageWorker {
    public static final Logger logger = LoggerFactory.getLogger(TaskQueueWorker.class);

    @Autowired
    TaskService taskService;

    @Autowired
    BTaskrewardMapper bTaskrewardMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    BTaskcountMapper bTaskcountMapper;

    @Autowired
    BTaskMapper bTaskMapper;

    @Autowired
    BTaskplanMapper bTaskplanMapper;

    @Autowired
    BStoresMapper bStoresMapper;

    @Autowired
    StoreAdminMapper storeAdminMapper;

    @Autowired
    StoreAdminExtMapper storeAdminExtMapper;

    @Autowired
    BTaskExecuteMapper bTaskExecuteMapper;

    @Autowired
    BTaskrewardLogMapper bTaskrewardLogMapper;

    @Autowired
    TaskPlanHelper taskPlanHelper;

    @Autowired
    BTaskConsumeLogMapper bTaskConsumeLogMapper;

    /**
     * 每次增加一条统计记录 奖励值全部计算
     * @param message
     * @throws Exception
     */
    @Override
    public void consume(Message message) throws Exception {
        BTaskcount bTaskcount;
        try {
            bTaskcount = JSONObject.parseObject(message.getMessageBody(), BTaskcount.class);
            Objects.requireNonNull(bTaskcount, "统计记录不能为空");
        } catch (Exception e) {
            logger.error("消息 {} 错误的消息内容 {} {} ", message, e.getMessage(), e);
            return;
        }

        new RewardConsume(bTaskcount, message).handle();
    }

    /**
     * 使用内部类保存状态 避免TaskQueueWorker可能产生的线程安全问题
     */
    class RewardConsume {
        private BTaskcount bTaskcount;
        private BTask bTask;
        private RewardRule rewardRule;
        private BTaskplan bTaskplan;
        private String remark;
        private Message message;

        public RewardConsume(BTaskcount bTaskcount, Message message) {
            this.bTaskcount = bTaskcount;
            this.message = message;
        }

        public void handle() {
            BTaskConsumeLogExample bTaskConsumeLogExample = new BTaskConsumeLogExample();
            bTaskConsumeLogExample.createCriteria()
                .andMsgTypeEqualTo(COUNT.toByte())
                .andTypeIdEqualTo(bTaskcount.getId())
                .andMsgIdEqualTo(message.getMessageId() != null ? message.getMessageId() : "")
                .andHandleStatusEqualTo(SUCCESS.toByte());
            List<BTaskConsumeLog> bTaskConsumeLogs = bTaskConsumeLogMapper.selectByExample(bTaskConsumeLogExample);
            if (CollectionUtils.isNotEmpty(bTaskConsumeLogs)) {
                // 消息已经被成功消费
                logger.warn("消息已经被成功消费");
                return;
            }

            bTask = taskService.selectByPrimaryKey(bTaskcount.getTaskId());
            if (Objects.isNull(bTask) || StringUtils.isEmpty(bTask.getRewardDetail())) {
                logger.info("根据任务id{} 没有找到任务记录或者奖励内容为空 {}", bTaskcount.getId(), bTask);
                return;
            }

            bTaskplan = bTaskplanMapper.selectByPrimaryKey(bTaskcount.getPlanId());
            if (Objects.isNull(bTaskplan)) {
                logger.info("根据任务计划id{} 没有找到记录", bTaskcount.getId());
                return;
            }

            BTaskExecute bTaskExecute = bTaskExecuteMapper.selectByPrimaryKey(bTaskcount.getExecuteId());
            if (Objects.isNull(bTaskExecute)) {
                logger.info("根据任务执行计划id{} 没有找到记录", bTaskcount.getExecuteId());
                return;
            }

            rewardRule = JSONObject.parseObject(bTask.getRewardDetail(), RewardRule.class);
            final int totalCountValue = sumCount();
            RewardResult rewardResult = taskPlanHelper.calcReward(totalCountValue, rewardRule);
            int rewardValue = rewardResult.getReward();
            if (bTaskExecute.getComplete() && bTask.getLowTarget() > 0 && totalCountValue < bTask.getLowTarget()) {
                // 有惩罚
                rewardValue = rewardResult.getReward() - bTask.getPunish();
                remark = "任务未达成惩罚最低条件";
            }

            try {
                addTaskRewardRecord(rewardValue, totalCountValue);
            } catch (BusinessLogicException e) {
                logger.error("添加任务奖励记录出错 {} {}", e.getMessage(), e);
            }
        }

        /**
         * 求和
         */
        public int sumCount() {
            BTaskcountExample example = new BTaskcountExample();
            example.createCriteria()
                    .andSiteIdEqualTo(bTaskcount.getSiteId())
                    .andExecuteIdEqualTo(bTaskcount.getExecuteId())
                    .andJoinTypeEqualTo(bTaskcount.getJoinType())
                    .andJoinIdEqualTo(bTaskcount.getJoinId());

            return bTaskcountMapper.sumByExample(example);
        }

        /**
         * 奖励记录 存在更新 第一次就插入
         * @param rewardValue
         * @return
         */
        @Transactional(rollbackFor = BusinessLogicException.class)
        public boolean addTaskRewardRecord(int rewardValue, int totalCountValue) throws BusinessLogicException {
            try {
                BTaskreward taskreward = getUniqueTaskReward();
                int diffValue = 0;
                boolean flag;
                if (Objects.nonNull(taskreward)) {
                    diffValue = rewardValue - taskreward.getReward();
                    taskreward.setCountValue(totalCountValue);

                    flag = updateReward(taskreward, rewardValue);
                } else {
                    taskreward = new BTaskreward();
                    // 奖励对象根据任务对象粒度决定
                    taskreward.setJoinType(bTaskcount.getJoinType());
                    taskreward.setJoinId(bTaskcount.getJoinId());
                    String joinName = findJoinNameByType(taskreward.getJoinType(), taskreward.getJoinId());
                    taskreward.setJoinName(joinName);

                    taskreward.setExecuteId(bTaskcount.getExecuteId());
                    taskreward.setPlanId(bTaskcount.getPlanId());
                    // 根据计划id查找名字
                    taskreward.setPlanName(bTaskplan.getName());
                    taskreward.setReward(rewardValue);
                    taskreward.setRewardType(bTask.getRewardType());
                    taskreward.setSiteId(bTaskcount.getSiteId());
                    taskreward.setTaskId(bTask.getId());
                    taskreward.setTaskName(bTask.getName());
                    taskreward.setCountValue(totalCountValue);

                    flag = insertReward(taskreward);
                }
                // 重复消费会导致b_taskreward_log重复记录和b_taskexecute的count_value值不正确
                if (flag) {
                    // 添加日志记录
                    BTaskrewardLog bTaskrewardLog = JacksonUtils.getInstance().convertValue(taskreward, BTaskrewardLog.class);
                    bTaskrewardLog.setRewardId(taskreward.getId());
                    bTaskrewardLog.setRemark(remark);
                    bTaskrewardLog.setValue(diffValue);
                    bTaskrewardLog.setId(null);
                    bTaskrewardLog.setCountValue(bTaskcount.getCountValue());
                    bTaskrewardLogMapper.insertSelective(bTaskrewardLog);

                    // 更新b_task_execute的count_value
                    bTaskExecuteMapper.updateCountValue(bTaskcount.getExecuteId());
                    addConsumeLog();
                }
            } catch (Exception e) {
                throw new BusinessLogicException(e);
            }

            return true;
        }

        /**
         * 根据奖励对象类型获取名字
         * @param joinType
         * @param joinId
         * @return
         * @throws BusinessLogicException
         */
        public String findJoinNameByType(Byte joinType, Integer joinId) throws BusinessLogicException {
            if (Objects.isNull(joinType) || Objects.isNull(joinId)) {
                throw new IllegalArgumentException("参数不能为空");
            }
            int siteId = bTaskcount.getSiteId();

            // 根据类型查找名字
            if (joinType == 10) {
                // 门店
                BStores bStores = bStoresMapper.selectByPrimaryKey(siteId, joinId);
                if (Objects.isNull(bStores)) {
                    throw new BusinessLogicException("根据门店id" + joinId + "和site_id" + siteId + "未找到门店");
                }
                return bStores.getName();
            } else {
                // 店员
                String name = storeAdminExtMapper.selectNameByStoreAdminId(siteId, joinId);
                if (Objects.isNull(name)) {
                    throw new BusinessLogicException("根据店员id" + joinId + "和site_id" + siteId + "未找到店员");
                }

                return name;
            }
        }

        /**
         * 更新奖励
         * @param bTaskreward
         * @return
         */
        public boolean updateReward(BTaskreward bTaskreward, int newReward) {
            if (updateTaskTotalReward(bTaskreward.getTaskId(), bTaskreward.getReward(), newReward)) {
                bTaskreward.setReward(newReward);
                return bTaskrewardMapper.updateRewardIncre(bTaskreward) != 0;
            }

            return false;
        }

        /**
         * 添加奖励
         * @param bTaskreward
         * @return
         */
        public boolean insertReward(BTaskreward bTaskreward) {
            if (updateTaskTotalReward(bTaskreward.getTaskId(), 0, bTaskreward.getReward())) {
                bTaskreward.setReward(bTaskreward.getReward());
                return bTaskrewardMapper.addRewardOnDuplicateUpdate(bTaskreward) != 0;
            }

            return false;
        }

        /**
         * 更新任务总奖励 只增不减
         * @param taskId 任务Id
         * @param reward 奖励记录之前值
         * @param newReward 新的值
         * @return
         */
        public boolean updateTaskTotalReward(Integer taskId, Integer reward, Integer newReward) {
            int diffValue = newReward - reward;

            return diffValue <= 0 || bTaskMapper.updateByPrimaryKeyNotOverflowLimit(taskId, diffValue) != 0;
        }

        /**
         * 获取之前的奖励数据
         * @return
         */
        public BTaskreward getUniqueTaskReward() {
            BTaskrewardExample bTaskrewardExample = new BTaskrewardExample();
            bTaskrewardExample.createCriteria()
                    .andSiteIdEqualTo(bTaskcount.getSiteId())
                    .andExecuteIdEqualTo(bTaskcount.getExecuteId())
                    .andJoinTypeEqualTo(bTaskcount.getJoinType())
                    .andJoinIdEqualTo(bTaskcount.getJoinId());

            List<BTaskreward> bTaskrewards = bTaskrewardMapper.selectByExample(bTaskrewardExample);

            if (CollectionUtils.isEmpty(bTaskrewards)) {
                return null;
            }

            // 查找的多个id具有联合唯一索引
            return bTaskrewards.get(0);
        }

        /**
         * 增加count消费日志
         * @return
         */
        private boolean addConsumeLog() {
            // 发布到线上 typeHandler失效了
            BTaskConsumeLog consumeLog = new BTaskConsumeLog();
            consumeLog.setMsgType((byte)COUNT.getValue());
            consumeLog.setTypeId(bTaskcount.getId());
            consumeLog.setMsgId(message.getMessageId());
            consumeLog.setHandleStatus((byte)SUCCESS.getValue());

            return bTaskConsumeLogMapper.insertSelective(consumeLog) > 0;
        }
    }
}
