# 添加repo源,版本21有问题，使用20版本
```text
vim /etc/yum.repos.d/CentOS-Base.repo
# In /etc/yum.repos.d/rabbitmq-erlang.repo
[rabbitmq-erlang]
name=rabbitmq-erlang
baseurl=https://dl.bintray.com/rabbitmq/rpm/erlang/20/el/7
gpgcheck=1
gpgkey=https://dl.bintray.com/rabbitmq/Keys/rabbitmq-release-signing-key.asc
repo_gpgcheck=0
enabled=1

```
# 安装 erlang 和 socat
```text
yum install erlang
yum -y install socat
```

# 安装 rabbitmq
```text
wget https://dl.bintray.com/rabbitmq/all/rabbitmq-server/3.7.6/rabbitmq-server-3.7.6-1.el7.noarch.rpm
rpm -ivh rabbitmq-server-3.7.7-1.el7.noarch.rpm
// 安装管理界面插件
rabbitmq-plugins enable rabbitmq_management
//添加用户
rabbitmqctl add_user admin admin
//设置权限
rabbitmqctl set_user_tags admin administrator 
rabbitmqctl set_permissions admin ".*" ".*" ".*" 
rabbitmqctl list_user_permissions admin
// 启动
systemctl start rabbitmq-server
管理端口 locahost:15672
// 修改密码
rabbitmqctl  change_password admin admin
```
[RabbitMQ消息队列-Centos7下安装RabbitMQ3.6.1](https://blog.csdn.net/super_rd/article/details/70241007)

[springboot 集成rabbitmq 并采用ack模式 以及封装队列定义](https://my.oschina.net/u/2948566/blog/1624963)