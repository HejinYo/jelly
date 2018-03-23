# 一、Docker准备
> 简介：Docker系统有两个程序：docker服务端和docker客户端。其中docker服务端是一个服务进程，管理着所有的容器。

## 准备开始
Docker系统有两个程序：docker服务端和docker客户端。其中docker服务端是一个服务进程，管理着所有的容器。d
ocker客户端则扮演着docker服务端的远程控制器，可以用来控制docker的服务端进程。
大部分情况下，docker服务端和客户端运行在一台机器上。检查docker的版本，这样可以用来确认docker服务在运行并可通过客户端链接。

## 提示：
可以通过在终端输入docker命令来查看所有的参数。

官网的在线模拟器只提供了有限的命令，无法保证所有的命令可以正确执行。
```text
[root@hejinyo ~]# docker version
Client:
 Version:         1.13.1
 API version:     1.26
 Package version: <unknown>
 Go version:      go1.8.3
 Git commit:      774336d/1.13.1
 Built:           Wed Mar  7 17:06:16 2018
 OS/Arch:         linux/amd64

Server:
 Version:         1.13.1
 API version:     1.26 (minimum version 1.12)
 Package version: <unknown>
 Go version:      go1.8.3
 Git commit:      774336d/1.13.1
 Built:           Wed Mar  7 17:06:16 2018
 OS/Arch:         linux/amd64
 Experimental:    false
[root@hejinyo ~]# 

```
# 2、搜索可用docker镜像
> 简介：这一步的目标是学会使用docker search命令来检索可用镜像。

## 搜索可用的docker镜像
使用docker最简单的方式莫过于从现有的容器镜像开始。
Docker官方网站专门有一个页面来存储所有可用的镜像，
网址是： [http://index.docker.io](http://index.docker.io)。
你可以通过浏览这个网页来查找你想要使用的镜像，或
者使用命令行的工具来检索。

## 提示：
命令行的格式为：docker search 镜像名字
```text
[root@hejinyo ~]# 
[root@hejinyo ~]# docker search tutorial
INDEX       NAME                                                    DESCRIPTION                                     STARS     OFFICIAL   AUTOMATED
docker.io   docker.io/learn/tutorial                                                                                36                   
docker.io   docker.io/georgeyord/reactjs-tutorial                   This is the backend of the React comment b...   5                    [OK]
docker.io   docker.io/chris24walsh/flask-aws-tutorial               Runs a simple flask webapp demo, with the ...   1                    [OK]
...
```

# 3、下载容器镜像
> 简介：学会使用docker pull命令下载一个镜像。

## 学会使用docker命令来下载镜像
下载镜像的命令非常简单，使用docker pull命令即可。(译者按：docker命令和git有一些类似的地方）。在docker的镜像索引网站上面，镜像都是按照 用户名/ 镜像名的方式来存储的。有一组比较特殊的镜像，比如ubuntu这类基础镜像，经过官方的验证，值得信任，可以直接用 镜像名来检索到。

提示：
执行pull命令的时候要写完整的名字，比如"learn/tutorial"。
```text
[root@hejinyo ~]# docker pull learn/tutorial
Using default tag: latest
Trying to pull repository docker.io/learn/tutorial ... 
latest: Pulling from docker.io/learn/tutorial
271134aeb542: Downloading [=============>                                     ] 19.17 MB/71.04 MB

271134aeb542: Pull complete 
Digest: sha256:2933b82e7c2a72ad8ea89d58af5d1472e35dacd5b7233577483f58ff8f9338bd
Status: Downloaded newer image for docker.io/learn/tutorial:latest
[root@hejinyo ~]# 

```

# 在docker容器中运行hello world!
> 简介：通过docker run命令可以启动某一个镜像，并运行一个命令。

## 在docker容器中运行hello world!
docker容器可以理解为在沙盒中运行的进程。这个沙盒包含了该进程运行所必须的资源，包括文件系统、系统类库、shell 环境等等。但这个沙盒默认是不会运行任何程序的。你需要在沙盒中运行一个进程来启动某一个容器。这个进程是该容器的唯一进程，所以当该进程结束的时候，容器也会完全的停止。

## 提示：
docker run命令有两个参数，一个是镜像名，一个是要在镜像中运行的命令。
```text
root@hejinyo ~]# docker run learn/tutorial echo "hello world"
hello world
[root@hejinyo ~]# 
```
# 在容器中安装新的程序
## 在容器中安装新的程序
下一步我们要做的事情是在容器里面安装一个简单的程序(ping)。我们之前下载的tutorial镜像是基于ubuntu的，所以你可以使用ubuntu的apt-get命令来安装ping程序：apt-get install -y ping。

> 备注：apt-get 命令执行完毕之后，容器就会停止，但对容器的改动不会丢失。

## 提示：
在执行apt-get 命令的时候，要带上-y参数。如果不指定-y参数的话，apt-get命令会进入交互模式，
需要用户输入命令来进行确认，**[但在docker环境中是无法响应这种交互的]**。
```text
[root@hejinyo ~]# docker run learn/tutorial apt-get install -y ping
Reading package lists...
Building dependency tree...
The following NEW packages will be installed:
  iputils-ping
0 upgraded, 1 newly installed, 0 to remove and 0 not upgraded.
Need to get 56.1 kB of archives.
After this operation, 143 kB of additional disk space will be used.
Get:1 http://archive.ubuntu.com/ubuntu/ precise/main iputils-ping amd64 3:20101006-1ubuntu1 [56.1 kB]
debconf: delaying package configuration, since apt-utils is not installed
Fetched 56.1 kB in 4s (13.6 kB/s)
Selecting previously unselected package iputils-ping.
(Reading database ... 7545 files and directories currently installed.)
Unpacking iputils-ping (from .../iputils-ping_3%3a20101006-1ubuntu1_amd64.deb) ...
Setting up iputils-ping (3:20101006-1ubuntu1) ...
[root@hejinyo ~]# 
```
# 保存对容器的修改
> 简介：通过docker commit命令保存对容器的修改

## 保存对容器的修改
当你对某一个容器做了修改之后（通过在容器中运行某一个命令），可以把对容器的修改保存下来，
这样下次可以从保存后的最新状态运行该容器
。docker中保存状态的过程称之为committing，它保存的新旧状态之间的区别，从而产生一个新的版本。

## 目标：
首先使用ocker ps -l命令获得安装完ping命令之后容器的id。然后把这个镜像保存为learn/ping。

## 提示：
1. 运行docker commit，可以查看该命令的参数列表。

2. 你需要指定要提交保存容器的ID。(译者按：通过docker ps -l 命令获得)

3. 无需拷贝完整的id，通常来讲最开始的三至四个字母即可区分。（译者按：非常类似git里面的版本号)
```text
[root@hejinyo ~]# docker ps -l
CONTAINER ID        IMAGE               COMMAND                CREATED             STATUS              PORTS               NAMES
58a683290b10        learn/tutorial      "ping www.baidu.com"   37 seconds ago      Created                                 stoic_leavitt
[root@hejinyo ~]# docker commit 58a6 learn/ping
sha256:b70b000b90b3d7868454ec46c1bfb4654e62ee0d02e5d5f6f817db3d2336ea9c
```
执行完docker commit命令之后，会返回新版本镜像的id号。
# 检查运行中的镜像
> 简介：使用docker ps命令可以查看所有正在运行中的容器列表，使用docker inspect命令我们可以查看更详细的关于某一个容器的信息。

## 检查运行中的镜像
现在你已经运行了一个docker容器，让我们来看下正在运行的容器。

使用docker ps命令可以查看所有正在运行中的容器列表，使用docker inspect命令我们可以查看更详细的关于某一个容器的信息。

## 目标：
查找某一个运行中容器的id，然后使用docker inspect命令查看容器的信息。

## 提示：
可以使用镜像id的前面部分，不需要完整的id。
```text
[root@hejinyo ~]# docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS               NAMES
81d366ac7a9b        learn/ping          "apt-get install a..."   7 minutes ago       Up 7 minutes                            infallible_bassi
[root@hejinyo ~]# docker inspect 81
[
    {
        "Id": "81d366ac7a9b647362f5f9b4958df6374f7161018f9b864204a1d2b6734afb49",
        "Created": "2018-03-23T14:39:42.129822872Z",
        "Path": "apt-get",
        "Args": [
            "install",
            "apt-utils",
            "-y"
        ],
        "State": {
            "Status": "running",
......
```
# 发布自己的镜像
> 简介：我们也可以把我们自己编译的镜像发布到索引页面，一方面可以自己重用，另一方面也可以分享给其他人使用。

## 发布docker镜像
现在我们已经验证了新镜像可以正常工作，下一步我们可以将其发布到官方的索引网站。
还记得我们最开始下载的learn/tutorial镜像吧，
我们也可以把我们自己编译的镜像发布到索引页面，一方面可以自己重用，
另一方面也可以分享给其他人使用。

## 目标：
把learn/ping镜像发布到docker的index网站。

## 提示：
1. docker images命令可以列出所有安装过的镜像。

2. docker push命令可以将某一个镜像发布到官方网站。

3. 你只能将镜像发布到自己的空间下面。这个模拟器登录的是learn帐号。
## 预期的命令：
```text
$ docker push learn/ping
```
