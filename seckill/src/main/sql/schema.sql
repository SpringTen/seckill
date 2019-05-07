-- 秒杀数据库
CREATE DATABASE seckill;
-- 使用数据库
USE seckill;
-- 创建秒杀表
CREATE TABLE seckill
(
`seckill_id` bigint not null auto_increment COMMENT '秒杀商品id',
`name` varchar(120) not null comment '秒杀商品名称',
`number` int not null comment '秒杀商品数量',
`start_time` timestamp not null comment '秒杀开始时间',
`end_time` timestamp not null comment '秒杀结束时间',
`create_time` timestamp not null default current_timestamp comment '创建时间',
primary key (seckill_id),
key idx_start_time (start_time),
key idx_end_time (end_time),
key idx_create_time (create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=UTF8 COMMENT '商品秒杀表';

-- 插入数据
insert into
    seckill(name, number, start_time, end_time)
values
    ('1000秒杀iPhoneX', 100, '2019-05-05 00:00:00', '2019-05-06 00:00:00'),
    ('500秒杀iPad', 200, '2019-05-05 00:00:00', '2019-05-06 00:00:00'),
    ('400秒杀小米9', 300, '2019-05-05 00:00:00', '2019-05-06 00:00:00'),
    ('200秒杀红米note6', 400, '2019-05-05 00:00:00', '2019-05-06 00:00:00');

-- 创建秒杀成功明细表
CREATE TABLE success_killed
(
`seckill_id` bigint not null comment '秒杀商品表的主键',
`user_phone` bitint not null comment '用户手机号，模拟用户登录请求',
`state` tinyint not null default -1 comment '状态，-1：无效，0：成功，1：已付款，2：已发货',
`create_time` timestamp not null comment '创建时间',
primary key (seckill_id, user_phone), /*联合主键，表示唯一性，防止单个用户多次重复下单某一商品*/
key idx_create_time (create_time)
)ENGINE=InnoDB default charset=utf8 comment '秒杀成功明细表';