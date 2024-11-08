# 数据库初始化
-- 创建库
create database if not exists oj_db;

-- 切换库
use oj_db;

-- 用户表
create table if not exists user
(
    userId       bigint                                 auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userPhone    varchar(256)                           null comment '用户手机',
    userMail     varchar(256)                           null comment '用户邮箱',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 题目表
create table if not exists question
(
    questionId      bigint                             auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '创建用户 id',
    title           varchar(512)                       null comment '标题',
    content         text                               null comment '内容',
    tag             varchar(1024)                               null comment '标签列表（json 数组）',
    answer          text                               null comment '参考答案',
    judgeCase       varchar(1024)                               null comment '判题用例（json 数组）',
    judgeConfig     varchar(1024)                               null comment '判题配置（json 对象）',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目' collate = utf8mb4_unicode_ci;

-- 题目提交表
create table if not exists question_submit
(
    questionSubmitId    bigint                             auto_increment comment 'id' primary key,
    userId              bigint                             not null comment '创建用户 id',
    questionId          bigint                             not null comment '题目 id',
    language            varchar(128)                       not null comment '编程语言',
    code                text                               not null comment '用户代码',
    judgeInfo           varchar(1024)                               null comment '判题信息（json 对象）',
    status              int      default 0                 not null comment '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    createTime          datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime          datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete            tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '题目提交';

-- 题解表
create table if not exists write_up
(
    writeUpId       bigint                             auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '创建用户 id',
    questionId      bigint                             not null comment '题目 id',
    title           varchar(512)                       null comment '标题',
    content         text                               null comment '内容',
    tag             varchar(1024)                               null comment '标签列表（json 数组）',
    reviewStatus    int      default 0                 not null comment '审核状态（0 - 待审核、1 - 审核通过、2 - 审核未通过）',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '题解' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    postId          bigint                             auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '创建用户 id',
    title           varchar(512)                       null comment '标题',
    content         text                               null comment '内容',
    tag             varchar(1024)                               null comment '标签列表（json 数组）',
    reviewStatus    int      default 0                 not null comment '审核状态（0 - 待审核、1 - 审核通过、2 - 审核未通过）',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子评论表
create table if not exists post_comment
(
    postCommentId   bigint                             auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '创建用户 id',
    postId          bigint                             not null comment '帖子 id',
    content         text                               not null comment '评论内容',
    reviewStatus    int      default 0                 not null comment '审核状态（0 - 待审核、1 - 审核通过、2 - 审核未通过）',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子评论' collate = utf8mb4_unicode_ci;

-- INSERT INTO question (userId, title, judgeCase) VALUES (1, 'title', '[{"input":"3 4","output":"5 6"},{"input":"5 6","output":"7 8"}]');
-- [{"input":"3 4","output":"5 6"},{"input":"5 6","output":"7 8"}]