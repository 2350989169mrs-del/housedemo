# Region/区域表

```
# t_region / 区域表（省/市/区三级，无街道）

id              int/主键
province        varchar/省
city            varchar/市
region_name     varchar/区/县名称
status          tinyint/状态 1=正常,0=停用
```

# Community/小区表

```
# t_community / 小区表

id              int/主键
region_id       int/所属区域ID（关联t_region.id）
name            varchar/小区名称
address         varchar/详细地址
building_age    varchar/建筑年代
total_units     int/总单元数
floor_count     int/楼层高度（层）
has_property    tinyint/是否有物业 1=有,0=无
property_name   varchar/物业公司名称
longitude       decimal/地图经度
latitude        decimal/地图纬度
status          tinyint/状态 1=正常,0=停用
```

# Store/门店表

```
# t_store / 门店表

id              int/主键
store_name      varchar/门店名称
region_id       int/所在区域ID（关联t_region.id）
address         varchar/详细地址
phone           varchar/门店电话
status          tinyint/状态 1=营业,0=停业
```

# House/房屋表

```
# t_house / 房屋表

id              int/主键
community_id    int/所属小区ID（关联t_community.id）
location        varchar/对外展示地址
building        varchar/楼栋号（内部，用户不可见）
unit            varchar/单元（内部，用户不可见）
room_number     varchar/房间号（内部，用户不可见）
rental_method   tinyint/租赁方式 1=整租,2=合租
lease_term      varchar/租期要求
rent            decimal/月租金
floor           int/楼层（数字）
decoration      tinyint/装修方式（字典decoration）
elevator_type   tinyint/电梯 1=有,0=无
layout          tinyint/户型（字典layout）
house_type      tinyint/房屋类型（字典house_type）
orientation     tinyint/朝向（字典orientation）
area            decimal/面积（平方米）
facilities      varchar/设施（逗号分隔字典facilities的value）
listing_time    datetime/上架时间(内部)
publisher_id    int/发布者用户ID（关联t_user.id）
cover_image     varchar/封面图片URL
description     text/房屋描述
sales_status    tinyint/销售状态 1=上架,0=下架

ALTER TABLE t_house
    ADD COLUMN audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态: 0=待审核, 1=审核通过, 2=审核拒绝' AFTER sales_status,
    ADD COLUMN auditor_id INT DEFAULT NULL COMMENT '审核经纪人ID' AFTER audit_status,
    ADD COLUMN audit_time DATETIME DEFAULT NULL COMMENT '审核时间' AFTER auditor_id,
    ADD COLUMN audit_remark VARCHAR(500) DEFAULT NULL COMMENT '审核意见/拒绝原因' AFTER audit_time;

```

# House_Image /户型图，房屋图

```
# t_house_image / 房屋图片表

id              int/主键
house_id        int/房屋ID（关联t_house.id）
url             varchar/图片URL（Nginx）
type            tinyint/类型 1=户型图,2=预览图
sort            int/排序
```

------------------------------------------------------------------------------------------------

# Agent_region/经纪人负责区域表

```
# t_agent_region / 经纪人负责区域（灵活绑定区+街道）

id              int/主键
agent_user_id   int/经纪人用户ID（关联t_user.id）
region_id       int/区域ID（关联t_region.id）
street_name     varchar/街道名称（为空表示全区）
```



------------------------------------------------------------------------------------------------

# User-用户表(包含用户，会员，管理员和经纪人)

```
# t_user / 用户主表（所有角色通用登录）

id              int/主键
avatar          varchar/头像URL
name            varchar/用户名/昵称
password        varchar/密码（加密存储）
account         varchar/手机号/登录账号
create_time     datetime/注册时间
account_status  tinyint/账户状态 1=正常,2=封禁,3=注销
role_id         int/角色ID（关联t_role.id）
user_type       tinyint/用户类型 1=普通用户,2=会员,3=经纪人,4=管理员,5=超级管理员
```

------------------------------------------------------------------------------------------------

# Role-角色表

```
# t_role / 角色表

id              int/主键
role_name       varchar/角色名称
role_key        varchar/角色标识（如 ROLE_ADMIN）
description     varchar/角色说明
status          tinyint/状态 1=正常,0=停用

INSERT INTO t_role (id, role_name, role_key, status) VALUES
(1, '普通用户', 'ROLE_USER', 1),
(2, '会员', 'ROLE_VIP', 1),
(3, '经纪人', 'ROLE_AGENT', 1),
(4, '管理员', 'ROLE_ADMIN', 1),
(5, '超级管理员', 'ROLE_SUPER_ADMIN', 1);
```

# User_info/普通会员/会员扩展信息

```
# t_user_info / 普通用户/会员扩展信息

id              int/主键
user_id         int/关联t_user.id（一对一）
real_name       varchar/真实姓名
id_card         varchar/身份证号
gender          tinyint/性别 0=未知,1=男,2=女
birthday        date/出生日期
email           varchar/邮箱
```

# Admin_info/管理员扩展信息

```
# t_admin_info / 管理员扩展信息

id              int/主键
user_id         int/关联t_user.id（一对一）
real_name       varchar/真实姓名
id_card         varchar/身份证号
department      varchar/部门
```

# Agent_info/经纪人扩展信息

```
# t_agent_info / 经纪人扩展信息

id              int/主键
user_id         int/关联t_user.id（一对一）
real_name       varchar/真实姓名
id_card         varchar/身份证号
phone           varchar/联系电话（可与account不同）
store_id        int/所属门店ID（关联t_store.id）
license_number  varchar/经纪资格证号
service_years   int/从业年限
introduction    varchar/个人简介
```

# appointment/预约看房表

```
# t_appointment / 预约看房表

id               int/主键
house_id         int/房屋ID（关联t_house.id）
client_user_id   int/预约用户ID（关联t_user.id）
agent_user_id    int/受理经纪人ID（关联t_user.id，确认后填入）
appointment_time datetime/用户期望看房时间
status           tinyint/状态 1=待确认,2=已确认,3=已完成,4=已取消,5=已拒绝
create_time      datetime/创建时间
update_time      datetime/更新时间
```

# Showing_record/带看记录表

```
# t_showing_record / 带看记录表（经纪人填写）

id                   int/主键
appointment_id       int/关联预约ID（一对一，关联t_appointment.id）
agent_user_id        int/实际带看经纪人ID（关联t_user.id）
showing_time         datetime/实际带看时间
client_intention     tinyint/用户意向 1=高,2=中,3=低,4=暂无意向
client_satisfaction  tinyint/用户满意度 1=非常满意,2=满意,3=一般,4=不满意
remark               varchar/经纪人备注
create_time          datetime/创建时间
```

# Favorite-收藏表

```
# t_favorite / 收藏表

id              int/主键
user_id         int/用户ID（关联t_user.id）
house_id        int/房屋ID（关联t_house.id）
create_time     datetime/收藏时间
```

# dict_type-字典类型表

```
# t_dict_type / 字典类型表

dict_id         int/主键
dict_name       varchar/字典名称（如：房屋类型）
dict_type       varchar/字典类型编码（如：house_type，唯一）
status          tinyint/状态 0=停用,1=正常
remark          varchar/备注
```

## dict_data-字典数据表

```
# t_dict_data / 字典数据表

data_id         int/主键
dict_type       varchar/字典类型编码（关联t_dict_type.dict_type）
dict_label      varchar/显示标签（如：整租）
dict_value      varchar/实际存储值（如：1）
sort            int/排序
status          tinyint/状态 0=停用,1=正常
remark          varchar/备注
```

# **字典初始化数据摘要**

| 字典类型 `dict_type` | 包含的标签                                                   |
| :------------------- | :----------------------------------------------------------- |
| decoration           | 毛坯、简装、精装、豪装                                       |
| layout               | 一居、两居、三居、四居及以上                                 |
| house_type           | 普通住宅、公寓、别墅、平房、复式                             |
| orientation          | 东、南、西、北、南北、东南、西南、东北、西北                 |
| facilities           | 冰箱、洗衣机、空调、热水器、电视、宽带、沙发、床、衣柜、天然气、暖气、电梯、阳台、独立卫生间 |



# Vue3+element plus实现Web-测试没有问题之后打包



思路大纲初版

首页是选择地点，选择地点后进入下一层对应城市房屋首页

对应城市房屋首页，上面面是顶部总站导航有 二手房(暂不实现)/新房(暂不实现)/租房 用户登录，其次是标题下面搜索框，里面有地图(高德)样式

&#x09;对应程序租房页面纵向布局，顶部总站导航多一个发布房源，区域模糊查询栏，列表页面包屑，筛选项，找到xx条数据，排序，房屋列表（缩略图，标题是数据库几个关键对应列名，价格，地点，面积，户型），分页

不登录也可以访问，但只能看到普通用户视角的租房默认排序，详细信息和查询均需登录。

下架的房屋在收藏也可以显示但没有详情。

注册时只注册部分信息(手机号和密码等)默认是用户，完善信息成为会员。

会员可以查看自己发布的房屋。经纪人看见的的是表格，并且房屋详细信息比会员看到的多。

管理员可以注册经纪人，管理经纪人。超级管理员可以注册管理员，管理管理员。

用户会员和经纪人管理员看的租房页面不一样，用户会员是卡片式，经纪人管理员是表格式

真正删除只有超级管理员有权限，经纪人和管理员只有逻辑删除。

------------------------------------------------------------------------------------------------

# 后端实现功能（Spring Boot+Mybatis plus/三层架构）

&#x09;

