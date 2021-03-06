-- 2.1.0 升级

-- 更新app namespace 中 format 字段格式
ALTER TABLE `apollo_mini`.`app_namespace`
MODIFY COLUMN `format` varchar(255) NULL DEFAULT NULL AFTER `comment`;

-- 更新数据
UPDATE app_namespace set format = 'Properties'  WHERE format = 0;
-- 数据填充
UPDATE app_namespace set app_namespace_type = SUBSTR(type,11);
-- 处理hibernate 鉴别器字段必填（不删方便回滚时候的处理）
ALTER TABLE `apollo_mini`.`app_namespace`
MODIFY COLUMN `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL FIRST;



-- 更新ReleaseMessage表的字段名 ;show FULL COLUMNs FROM release_message;
ALTER TABLE release_message change message namespace_key varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;

-- 更新命名空间授权应用关联表
UPDATE `apollo_mini`.`app_namespace_authorized_app`
SET `app_namespace_id` = app_namespace4protect_id
WHERE
	`app_namespace_id` = 0;

ALTER TABLE `apollo_mini`.`app_namespace_authorized_app`
MODIFY COLUMN `app_namespace4protect_id` bigint(20) NOT NULL DEFAULT 0 FIRST,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`authorized_app_id`, `app_namespace_id`) USING BTREE;