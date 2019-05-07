/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50723
 Source Host           : localhost:3306
 Source Schema         : seckill

 Target Server Type    : MySQL
 Target Server Version : 50723
 File Encoding         : 65001

 Date: 07/05/2019 13:42:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for seckill
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill`  (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀商品id',
  `name` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '秒杀商品名称',
  `number` int(11) NOT NULL COMMENT '秒杀商品数量',
  `start_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '秒杀开始时间',
  `end_time` timestamp(0) NOT NULL COMMENT '秒杀结束时间',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`) USING BTREE,
  INDEX `idx_start_time`(`start_time`) USING BTREE,
  INDEX `idx_end_time`(`end_time`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1004 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品秒杀表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seckill
-- ----------------------------
INSERT INTO `seckill` VALUES (1000, '1000秒杀iPhoneX', 98, '2019-05-05 13:16:52', '2019-05-06 00:00:00', '2019-05-03 11:27:55');
INSERT INTO `seckill` VALUES (1001, '500秒杀iPad', 200, '2019-05-08 00:00:00', '2019-05-12 00:00:00', '2019-05-03 11:27:55');
INSERT INTO `seckill` VALUES (1002, '400秒杀小米9', 299, '2019-05-06 12:45:54', '2019-05-10 00:00:00', '2019-05-03 11:27:55');
INSERT INTO `seckill` VALUES (1003, '200秒杀红米note6', 398, '2019-05-07 13:14:05', '2019-05-31 00:00:00', '2019-05-03 11:27:55');

-- ----------------------------
-- Table structure for successkilled
-- ----------------------------
DROP TABLE IF EXISTS `successkilled`;
CREATE TABLE `successkilled`  (
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀商品表的主键',
  `user_phone` bigint(20) NOT NULL COMMENT '用户手机号，模拟用户登录请求',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `state` tinyint(4) NOT NULL COMMENT '状态，-1：无效，0：成功，1：已付款，2：已发货',
  PRIMARY KEY (`seckill_id`, `user_phone`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '秒杀成功明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of successkilled
-- ----------------------------
INSERT INTO `successkilled` VALUES (1000, 13851580482, '2019-05-05 13:38:16', 0);
INSERT INTO `successkilled` VALUES (1002, 13851580482, '2019-05-07 13:03:42', 0);
INSERT INTO `successkilled` VALUES (1003, 13555555555, '2019-05-07 13:14:05', 0);
INSERT INTO `successkilled` VALUES (1003, 13851580482, '2019-05-05 21:52:17', 0);

-- ----------------------------
-- Procedure structure for execute_seckill
-- ----------------------------
DROP PROCEDURE IF EXISTS `execute_seckill`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `execute_seckill`(in v_seckill_id bigint,
     in v_phone bigint,
     in v_kill_time timestamp,
     out r_result int)
BEGIN
        DECLARE insert_count int default 0;
        START TRANSACTION;
        insert ignore into successkilled
            (seckill_id, user_phone, create_time)
            values (v_seckill_id, v_phone, v_kill_time);
        select row_count() into insert_count;
        IF (insert_count = 0) THEN
            ROLLBACK;
            set r_result = -1;
        ELSEIF (insert_count < 0) THEN
            ROLLBACK;
            set r_result = -2;
        ELSE
            update seckill
            set number = number - 1
            where seckill_id = v_seckill_id
            and end_time > v_kill_time
            and start_time < v_kill_time
            and number > 0;
            select row_count() into insert_count;
            IF (insert_count = 0) THEN
                ROLLBACK;
                set r_result = 0;
            ELSEIF (insert_count < 0) THEN
                ROLLBACK;
                set r_result = -2;
            ELSE
                COMMIT;
                set r_result = 1;
            END IF;
        END IF;
    END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
