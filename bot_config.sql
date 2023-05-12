create database yuki_bot;
-- auto-generated definition
create table bot_config
(
    id                      bigint auto_increment comment '主键'
        primary key,
    bot_identify            varchar(30)                     not null comment '机器人标识别',
    bot_id                  bigint                          not null comment '机器人QQ号',
    bot_pwd                 varchar(50)                     not null comment '机器人密码',
    add_from_allowed_groups char        default '1'         not null comment '是否可以被从受允许的群中申请添加好友 0：不可以 1：可以',
    add_without_permission  char        default '1'         not null comment '是否可以被私加好友 0：不可以 1：可以',
    token_message           varchar(100)                    null comment '被申请添加好友时需要输入的验证消息',
    allowed_groups_mode     varchar(30) default 'unlimited' not null comment '受允许的群组的筛选模式，ids:通过配置的allowed_groups_ids字段中的群号筛选，unlimited：不限制',
    allowed_groups_ids      varchar(500)                    null comment '受允许的群组的群号',
    allowed_friends_mode    varchar(30) default 'unlimited' not null comment '受允许的好友的筛选模式，id:通过配置的allowed_friends_ids字段中的好友qq号筛选，
friend-group：好友分组名称筛选
unlimited：不限制',
    allowed_friends_ids     varchar(500)                    null comment '受允许的好友的QQ号',
    allowed_group_name      varchar(255)                    null comment '受允许的好友分组',
    allowed_strangers       char        default '0'         not null comment '允许陌生人 0：不允许 1：允许',
    online_status           char        default '1'         null comment '0：停用 1：正常'
);

