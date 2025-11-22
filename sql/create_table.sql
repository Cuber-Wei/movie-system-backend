# 数据库初始化
-- 创建库
create database if not exists movie_db;

-- 切换库
use movie_db;

-- 用户表
create table if not exists user
(
    userId       bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userGender   varchar(4)   default '未知'            null comment '用户性别',
    userPhone    varchar(256)                           null comment '用户手机',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    birthday     datetime                               not null comment '用户生日',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;

-- 电影榜单表
create table if not exists movie_list
(
    movieListId      bigint auto_increment comment 'id' primary key,
    userId           bigint                             not null comment '创建用户 id',
    title            varchar(512)                       null comment '标题',
    listIntroduction text                               null comment '简介',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete         tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '电影榜单' collate = utf8mb4_unicode_ci;

-- 电影榜单归属表
create table if not exists movie_in_list
(
    movieInListId bigint auto_increment comment 'id' primary key,
    movieListId   bigint                             not null comment '电影榜单 id',
    movieId       bigint                             not null comment '电影 id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    index idx_movieListId (movieListId),
    index idx_movieId (movieId)
) comment '题目提交';

-- 电影收藏表
create table if not exists collect
(
    collectId  bigint auto_increment comment 'id' primary key,
    userId     bigint                             not null comment '创建用户 id',
    movieId    bigint                             not null comment '题目 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_movieId (movieId),
    index idx_userId (userId)
) comment '电影收藏' collate = utf8mb4_unicode_ci;

-- 电影表
create table if not exists movie
(
    movieId      bigint auto_increment comment 'id' primary key,
    title        varchar(512)                       null comment '电影名称',
    introduction text                               null comment '电影介绍',
    actors       text                               null comment '演员信息',
    duration     int      default 0                 not null comment '时长',
    tag          varchar(1024)                      null comment '标签列表（json 数组）',
    publishDate  datetime default '1970-1-1'        not null comment '出版日期',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
) comment '电影' collate = utf8mb4_unicode_ci;

-- 电影评论表
create table if not exists movie_comment
(
    movieCommentId bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '创建用户 id',
    movieId        bigint                             not null comment '电影 id',
    content        text                               not null comment '评论内容',
    score          float    default 0.0               not null comment '评分',
    reviewStatus   int      default 0                 not null comment '审核状态（0 - 待审核、1 - 审核通过、2 - 审核未通过）',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除',
    index idx_movieId (movieId),
    index idx_userId (userId)
) comment '电影评论' collate = utf8mb4_unicode_ci;

INSERT INTO `user` (`userId`, `userAccount`, `userPassword`, `userRole`, `userPhone`,
                    `createTime`, `updateTime`, `isDelete`, `userGender`, `birthday`)
VALUES (1855858177690660865, 'admin', '2dea76c10c63f5fd1d236e6a5578a68e', 'admin',
        '18255555555', '2024-11-11 14:20:44', '2024-11-12 11:13:15', 0, '未知',
        '2004-11-11 00:00:0');
