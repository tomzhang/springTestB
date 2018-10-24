/*
package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

*/
/**
 * execute sql
 * INSERT INTO `51jk_db`.`schedule_meta` (`name`, `bean_name`, `method`, `server_addr`, `cron_exp`, `status`, `enabled`) VALUE('同步分销商品数据', 'SyncGoodsTask', 'run', 'http://172.20.10.174:8765', '0/1 * * * * ?', '0', '0');
 * INSERT INTO `51jk_db`.`schedule_meta` (`name`, `bean_name`, `method`, `server_addr`, `cron_exp`, `status`, `enabled`) VALUE('同步分销商品数据', 'SyncGoodsTask', 'run', 'http://172.20.12.71:8765', '0/1 * * * * ?', '0', '0');
 *//*

class V1_0_7__20170521_addjob implements SpringJdbcMigration {
    */
/**
     * @param jdbcTemplate
     * @throws Exception
     *//*

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        String[] serverAddrs = new String[]{"http://172.20.10.174:8765", "http://172.20.12.71:8765"};
        for (String serverAddr : serverAddrs) {
            String sql = String.format("INSERT INTO `51jk_db`.`schedule_meta` (" +
                    "`name`, `bean_name`, `method`, `server_addr`, `cron_exp`, `status`, `enabled`" +
                    ") VALUE(" +
                    "'同步分销商品数据', 'SyncGoodsTask', 'run', '%s', '0/1 * * * * ?', '0', '0'" +
                    ")", serverAddr);

            jdbcTemplate.execute(sql);
        }
    }
}*/

INSERT INTO `51jk_db`.`schedule_meta` (
	`name`,
	`bean_name`,
	`method`,
	`server_addr`,
	`cron_exp`,
	`status`,
	`enabled`
) SELECT
	'同步分销商品数据',
	'SyncGoodsTask',
	'run',
	b.s,
	'0/1 * * * * ?',
	'0',
	'0'
FROM
	(
		SELECT
			'http://172.20.10.174:8765' AS s
		UNION
			SELECT
				'http://172.20.12.71:8765' AS s
	) AS b