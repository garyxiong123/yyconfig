![logo](./img/logo160x160.png)
# 阿塔云-sword 本地集群安装方案

# <br/><br/><br/>资源需求
使用本地集群安装方案时，ATA sword建立在本地[DC/OS](https://docs.d2iq.com/mesosphere/dcos)集群上，所以，需要先安装定制的本地DC/OS集群。

DC/OS集群由三种类型的节点（master节点、public agent节点和private agent节点）组成。master节点负责调度和管理集群，public agent节点通过负载均衡器（默认使用Haproxy）为集群内容器提供集群外部到集群内部的负载均衡访问，private agent用于部署所有的具体业务容器。
除了集群内节点之外，每个DC/OS安装还需要一个额外的安装引导节点，用于生成DC/OS安装文件。
> 提示：安装引导节点独立于集群之外，仅在集群安装和扩容期间被使用，其他时间空闲。

## 硬件资源需求
节点类型|节点个数|Linux版本|最小配置|生产环境推荐配置
:-:|:-:|:-:|:-:|:-:|
安装引导节点|1|-|-|-
master节点|1个以上，推荐3个|CentOS 7.5|CPU：2核；内存：4G；磁盘：80G|CPU：4核，内存：8G，磁盘：160G
public agent节点|1个以上，推荐2-3个|CentOS 7.5|CPU：2核，内存：4G，磁盘：50G|CPU：4核，内存：8G，磁盘：50G
master节点|1个以上，按业务需求，多个节点可以预防单机故障|CentOS 7.5|CPU：4核，内存：8G，磁盘：50G|CPU和内存按业务需求量，但请保证每个节点磁盘在50G以上

# <br/><br/><br/>安装步骤
## 1. 安装必要软件（master、public agent和private agent节点执行）
```bash
yum update && yum install -y tar xz unzip curl ipset yum-utils chrony
```
## 2. 节点配置（master、public agent和private agent节点执行）
### 2.1 停止firewalld
firewalld会影响docker进程，请停止并禁用firewalld
```bash
sudo systemctl stop firewalld && sudo systemctl disable firewalld
```
### 2.2 停止DNSmasq
DC/OS集群需要使用端口53，为了防止冲突，请停止并禁用dnsmasq
```bash
sudo systemctl stop dnsmasq && sudo systemctl disable dnsmasq.service
```
### 2.3 同步节点时间
安装并使用chronyd以同步时间
```bash
systemctl enable chronyd && systemctl start chronyd && chronyc sources
```
### 2.4 调整SELINUX mode
SELINUX mode调整为禁用
```bash
sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/selinux/config
```
### 2.5 group添加nogroup和docker
```bash
groupadd nogroup && groupadd docker
```
### 2.6 调整网卡名
设置并统一网卡名，统一的网卡名将被填写在安装导引节点的安装配置中
### 2.7 重启节点
重启节点以确保配置生效
```bash
sudo reboot
```
## 3 安装docker（master、public agent和private agent节点执行）
### 3.1 安装docker-ce-18.06.1.ce
```bash
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
yum install -y docker-ce-18.06.1.ce-3.el7.x86_64
systemctl start docker
systemctl enable docker 
```
### 3.2 docker设置
创建保存配置的目录
```bash
mkdir -p /etc/systemd/system/docker.service.d/
```
<br/>写入配置
```bash
cat >> /etc/systemd/system/docker.service.d/override.conf <<EOF
[Service]
ExecStart=
ExecStart=/usr/bin/dockerd --storage-driver=overlay --log-driver=journald --log-opt labels=production_type
EOF
```
### 3.3 docker http镜像库设置
> $PUBLIC_AGENT1和$PUBLIC_AGENT2是您的public agent节点ip，如果public agent节点个数超过两个，请修改json数组。端口5000不变

```bash
cat >> /etc/docker/daemon.json <<EOF
{"insecure-registries":["${PUBLIC_AGENT1}:5000", "${PUBLIC_AGENT2}:5000"]}
EOF
```
### 3.4 重启docker
重启docker，使新配置生效
```bash
systemctl daemon-reload && systemctl restart docker
```
## 4 生成DC/OS安装脚本（安装引导节点执行）
填写配置文件，使用安装脚本生成文件，生成特定DC/OS集群的安装文件
### 4.1 创建特定集群的配置文件目录
假设目录为ata_local_cluster(可以自定义)
```bash
mkdir -p /root/dcos/ata_local_cluster/genconf && cd /root/dcos/ata_local_cluster/
```
### 4.2 配置ip-detect文件
填写在2.6中已经配置好的节点统一网卡名，此处填写为ata-eth0
```bash
cat >> genconf/ip-detect <<EOF
#!/usr/bin/env bash
set -o nounset -o errexit
export PATH=/usr/sbin:/usr/bin:$PATH
echo $(ip addr show ata-eth0 | grep -Eo '[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}' | head -1)
EOF
```
<br/>调整ip-detect文件权限
```bash
chmod +x ip-detect
```
### 4.3 配置config.yaml文件
> bootstrap_url请填写安装引导节点的ip，端口填写一个安装引导节点还未使用的端口

> cluster_name可自行设置，只做显示

> master_list、public_agent_list和agent_list分别填写master、public agent和private agent节点的ip

> resolvers填写您的集群希望使用的DNS server，如果没有本地DNS server，可以填写8.8.4.4和8.8.8.8

```bash
cat >> genconf/config.yaml <<EOF
bootstrap_url: http://10.0.55.150:10000
cluster_name: ATCLOUD LOCAL
exhibitor_storage_backend: static
master_discovery: static
ip_detect_path: genconf/ip-detect
master_list:
- 192.168.1.143
- 192.168.1.145
- 192.168.1.146
resolvers:
- 10.0.12.1
- 10.0.12.2
public_agent_list:
- 192.168.1.147
- 192.168.1.148
agent_list:
- 192.168.1.149
- 192.168.1.150
telemetry_enabled: false
oauth_enabled: 'false'
enable_docker_gc: true
EOF
```
### 4.4 下载并执行dcos_generate_config.sh
下载DC/OS 1.6版本的安装脚本生成器并执行
```bash
curl -O https://downloads.dcos.io/dcos/stable/1.11.6/dcos_generate_config.sh && bash dcos_generate_config.sh
```
### 4.5 生成DC/OS安装脚本下载容器
> 脚本下载容器使用的宿主机端口（此处为10000）需要和config.yaml中bootstrap_url项填写的一致

```bash
docker run -d -p 10000:80 -v /root/dcos/genconf/serve:/usr/share/nginx/html:ro nginx
```
## 5 安装GlusterFS server（master节点执行）
阿塔DC/OS集群需要使用GlusterFS实现容器的持久化挂载，master节点作为GlusterFS server，agent节点挂载GlusterFS
```bash
mkdir -p /data/ata
yum install -y centos-release-gluster41-1.0-3.el7.centos.noarch
yum install -y glusterfs-server-4.1.5-1.el7.x86_64
systemctl start glusterd.service
systemctl enable glusterd.service
```
## 6 启动GlusterFS server（在最后一个master节点执行）
### 6.1 在最后一个master启动时，和其他所有master节点建立连接。
此处假设为3个master
```bash
gluster peer probe 192.168.1.143
gluster peer probe 192.168.1.145
gluster peer status
```
### 6.2 创建volume atadata
> 创建volume。replica设置为master节点个数，此处为3

> 在所有节点创建volume，关联其余所有master节点的/data/ata

```bash
gluster volume create atadata replica 3 192.168.1.143:/data/ata 192.168.1.145:/data/ata 192.168.1.146:/data/ata force
```
<br/>启动并设置volume ata
> 设置limit-usage（GlusterFS存储空间限制）时，请根据master节点磁盘大小调整，此处假设master节点磁盘都为120GB

```bash
gluster volume start atadata
gluster volume set atadata performance.read-ahead on
gluster volume set atadata performance.quick-read on
gluster volume set atadata performance.cache-size 256MB
gluster volume set atadata nfs.addr-namelookup off
gluster volume set atadata nfs.disable on
gluster volume set atadata performance.cache-max-file-size 2MB
gluster volume set atadata performance.cache-refresh-timeout 4
gluster volume set atadata performance.io-thread-count 32
gluster volume quota atadata enable  
gluster volume quota atadata limit-usage / 80GB
```
## 7 挂载GlusterFS（agent节点执行）
### 7.1 安装FlusterFS fuse
```bash
yum install -y glusterfs-3.12.2-18.el7.x86_64 glusterfs-fuse-3.12.2-18.el7.x86_64
```
### 7.2 挂载命令写入rc.local并执行
```bash
mkdir -p /data/ata
chmod +x /etc/rc.local
cat >> /etc/rc.local <<EOF
/bin/mount -t glusterfs -o backup-volfile-servers=192.168.1.143 192.168.1.145:atadata /data/ata
EOF
bash /etc/rc.local
```
## 8 安装DC/OS（master节点执行）
```bash
bash dcos_install.sh master
```
## 9 安装DC/OS（public agent节点执行）
安装DC/OS public agent
```bash
bash dcos_install.sh slave_public
```
<br/>添加crontab
```bash
cat >> /var/spool/cron/root <<EOF
0 1 * * * > /var/log/kern && > /var/log/messages
EOF
```
## 8 安装DC/OS（agent节点执行）
### 8.1 安装DC/OS agent
```bash
bash dcos_install.sh slave
```
### 8.2 设置容器和镜像GC
```bash
cat >> /etc/docker-gc-exclude <<EOF
*jenkins-slave*
*registry*
*gitlab*
EOF
```
### 8.3 设置mesos agent
```bash
cat >> /var/lib/dcos/mesos-slave-common <<EOF
MESOS_CGROUPS_ENABLE_CFS=false
EOF
systemctl restart dcos-mesos-slave
```
## 9. 设置定时清理任务（public agent和agent节点执行）
```bash
cat >> /var/spool/cron/root <<EOF
0 1 * * * > /var/log/kern && > /var/log/messages
EOF
```
## 10. 安装marathon-lb（最后一个agent节点执行）
marathon-lb服务将被DC/OS自动调度，为集群内服务容器提供集群外到集群内的负载均衡服务
> curl中填写任意一个master节点ip，此处假设mater节点ip为192.168.1.143

> json中instances和public agent节点数目一致，cpus和mem参照public agent节点资源情况设置，此处假设节点资源为4核8G

```bash
curl -i -H "Content-type: application/json" -X PUT http://192.168.1.143:8080/v2/apps/atacloud-admin/marathon-lb?force=true -d '{"id":"/atacloud-admin/marathon-lb","cmd":null,"cpus":3,"mem":6144,"disk":0,"instances":2,"constraints":[["hostname","UNIQUE"]],"acceptedResourceRoles":["slave_public"],"container":{"type":"DOCKER","docker":{"forcePullImage":false,"image":"mesosphere/marathon-lb:v1.12.2","parameters":[{"key":"label","value":"created_by=marathon"},{"key":"label","value":"dcos_pkg_name=marathon-lb"}],"privileged":true},"volumes":[{"containerPath":"/dev/log","hostPath":"/dev/log","mode":"RW"}]},"env":{"HAPROXY_GLOBAL_DEFAULT_OPTIONS":"redispatch,http-server-close,dontlognull,httplog","HAPROXY_SSL_CERT":"","HAPROXY_SYSCTL_PARAMS":"net.ipv4.tcp_tw_reuse=1net.ipv4.tcp_fin_timeout=30net.ipv4.tcp_max_syn_backlog=10240net.ipv4.tcp_max_tw_buckets=400000net.ipv4.tcp_max_orphans=60000net.core.somaxconn=10000"},"healthChecks":[{"gracePeriodSeconds":60,"intervalSeconds":5,"maxConsecutiveFailures":2,"path":"/_haproxy_health_check","portIndex":2,"protocol":"MESOS_HTTP","ipProtocol":"IPv4","timeoutSeconds":2,"delaySeconds":15}],"labels":{"DCOS_PACKAGE_VERSION":"1.12.2","DCOS_PACKAGE_NAME":"marathon-lb"},"portDefinitions":[{"port":80,"protocol":"tcp"},{"port":443,"protocol":"tcp"},{"port":9090,"protocol":"tcp"},{"port":9091,"protocol":"tcp"},{"port":10000,"protocol":"tcp"},{"port":10001,"protocol":"tcp"},{"port":10002,"protocol":"tcp"},{"port":10003,"protocol":"tcp"},{"port":10004,"protocol":"tcp"},{"port":10005,"protocol":"tcp"},{"port":10006,"protocol":"tcp"},{"port":10007,"protocol":"tcp"},{"port":10008,"protocol":"tcp"},{"port":10009,"protocol":"tcp"},{"port":10010,"protocol":"tcp"},{"port":10011,"protocol":"tcp"},{"port":10012,"protocol":"tcp"},{"port":10013,"protocol":"tcp"},{"port":10014,"protocol":"tcp"},{"port":10015,"protocol":"tcp"},{"port":10016,"protocol":"tcp"},{"port":10017,"protocol":"tcp"},{"port":10018,"protocol":"tcp"},{"port":10019,"protocol":"tcp"},{"port":10020,"protocol":"tcp"},{"port":10021,"protocol":"tcp"},{"port":10022,"protocol":"tcp"},{"port":10023,"protocol":"tcp"},{"port":10024,"protocol":"tcp"},{"port":10025,"protocol":"tcp"},{"port":10026,"protocol":"tcp"},{"port":10027,"protocol":"tcp"},{"port":10028,"protocol":"tcp"},{"port":10029,"protocol":"tcp"},{"port":10030,"protocol":"tcp"},{"port":10031,"protocol":"tcp"},{"port":10032,"protocol":"tcp"},{"port":10033,"protocol":"tcp"},{"port":10034,"protocol":"tcp"},{"port":10035,"protocol":"tcp"},{"port":10036,"protocol":"tcp"},{"port":10037,"protocol":"tcp"},{"port":10038,"protocol":"tcp"},{"port":10039,"protocol":"tcp"},{"port":10040,"protocol":"tcp"},{"port":10041,"protocol":"tcp"},{"port":10042,"protocol":"tcp"},{"port":10043,"protocol":"tcp"},{"port":10044,"protocol":"tcp"},{"port":10045,"protocol":"tcp"},{"port":10046,"protocol":"tcp"},{"port":10047,"protocol":"tcp"},{"port":10048,"protocol":"tcp"},{"port":10049,"protocol":"tcp"},{"port":10050,"protocol":"tcp"},{"port":10051,"protocol":"tcp"},{"port":10052,"protocol":"tcp"},{"port":10053,"protocol":"tcp"},{"port":10054,"protocol":"tcp"},{"port":10055,"protocol":"tcp"},{"port":10056,"protocol":"tcp"},{"port":10057,"protocol":"tcp"},{"port":10058,"protocol":"tcp"},{"port":10059,"protocol":"tcp"},{"port":10060,"protocol":"tcp"},{"port":10061,"protocol":"tcp"},{"port":10062,"protocol":"tcp"},{"port":10063,"protocol":"tcp"},{"port":10064,"protocol":"tcp"},{"port":10065,"protocol":"tcp"},{"port":10066,"protocol":"tcp"},{"port":10067,"protocol":"tcp"},{"port":10068,"protocol":"tcp"},{"port":10069,"protocol":"tcp"},{"port":10070,"protocol":"tcp"},{"port":10071,"protocol":"tcp"},{"port":10072,"protocol":"tcp"},{"port":10073,"protocol":"tcp"},{"port":10074,"protocol":"tcp"},{"port":10075,"protocol":"tcp"},{"port":10076,"protocol":"tcp"},{"port":10077,"protocol":"tcp"},{"port":10078,"protocol":"tcp"},{"port":10079,"protocol":"tcp"},{"port":10080,"protocol":"tcp"},{"port":10081,"protocol":"tcp"},{"port":10082,"protocol":"tcp"},{"port":10083,"protocol":"tcp"},{"port":10084,"protocol":"tcp"},{"port":10085,"protocol":"tcp"},{"port":10086,"protocol":"tcp"},{"port":10087,"protocol":"tcp"},{"port":10088,"protocol":"tcp"},{"port":10089,"protocol":"tcp"},{"port":10090,"protocol":"tcp"},{"port":10091,"protocol":"tcp"},{"port":10092,"protocol":"tcp"},{"port":10093,"protocol":"tcp"},{"port":10094,"protocol":"tcp"},{"port":10095,"protocol":"tcp"},{"port":10096,"protocol":"tcp"},{"port":10097,"protocol":"tcp"},{"port":10098,"protocol":"tcp"},{"port":10099,"protocol":"tcp"},{"port":10100,"protocol":"tcp"}],"args":["sse","-m","http://marathon.mesos:8080","--health-check","--haproxy-map","--max-reload-retries","10","--reload-interval","10","--group","external"],"requirePorts":true,"upgradeStrategy":{"maximumOverCapacity":0.2,"minimumHealthCapacity":0.5}}'
```
## 11. 安装Gitlab（最后一个agent节点执行）
启动OPS托管Gitlab，OPS将自动管理此Gitlab，自动创建和管理项目和服务，拉取代码时使用token，不需要额外设置凭据
> 此为可选项。使用OPS时可以使用外部git代码仓库，但是需要自行管理Gitlab，并在OPS设置凭据。适合已有Git服务的团队。

### 11.1 下载gitlab data.zip并解压
```bash
mkdir -p /data/ata/gitlab/
cd /data/ata/gitlab/
curl -O https://downloads.ata.cloud.com/cluster/install/0.3.7/gitlab/data.zip
unzip data.zip
EOF
```
### 11.2 启动Gitlab容器
> curl中填写任意一个master节点ip，此处假设mater节点ip为192.168.1.143

> json中instances固定为1，cpus为1，mem为6144（单位为MiB）

> 192.168.1.147是public agent节点ip，使用您的任意一个public agent节点ip代替

```bash
curl -i -H "Content-type: application/json" -X PUT http://192.168.1.143:8080/v2/apps/atacloud-admin/gitlab?force=true -d '{"env":{"GITLAB_OMNIBUS_CONFIG":"external_url\"http://192.168.1.147:7000\";"},"labels":{"HAPROXY_GROUP":"external"},"id":"/atacloud-admin/gitlab","container":{"portMappings":[{"containerPort":7000,"hostPort":0,"protocol":"tcp","servicePort":7000,"name":"default"},{"containerPort":443,"hostPort":0,"protocol":"tcp","servicePort":7003},{"containerPort":22,"hostPort":0,"protocol":"tcp","servicePort":7002}],"type":"DOCKER","volumes":[{"containerPath":"/etc/gitlab","hostPath":"/data/ata/gitlab/etc","mode":"RW"},{"containerPath":"/var/log/gitlab","hostPath":"/data/ata/gitlab/log","mode":"RW"},{"containerPath":"/var/opt/gitlab/postgresql/data/","hostPath":"/data/ata/gitlab/data","mode":"RW"}],"docker":{"image":"registry.gs.youyuwo.com/base/ata-gitlab-ce:10.8.2-ce.0","forcePullImage":false,"privileged":false,"parameters":[]}},"cpus":1,"disk":0,"instances":1,"mem":6144,"networks":[{"mode":"container/bridge"}]}'
```
## 12. 安装Jenkins（最后一个agent节点执行）
### 12.1 下载user.zip和jobs.zip并解压
```bash
mkdir -p /data/ata/jenkins/
cd /data/ata/jenkins/
curl -O https://downloads.ata.cloud.com/cluster/install/0.3.7/jenkins/user.zip
unzip user.zip
curl -O https://downloads.ata.cloud.com/cluster/install/0.3.7/jenkins/jons.zip
unzip jobs.zip
EOF
```
### 12.2 启动Gitlab容器
> curl中填写任意一个master节点ip，此处假设mater节点ip为192.168.1.143

> json中instances固定为1，cpus为0.5，mem为3072（单位为MiB）

```bash
curl -i -H "Content-type: application/json" -X PUT http://192.168.1.143:8080/v2/apps/atacloud-admin/jenkins?force=true -d '{"id":"/atacloud-admin/jenkins","cmd":null,"cpus":0.5,"mem":3072,"disk":0,"instances":1,"acceptedResourceRoles":["*"],"container":{"type":"DOCKER","docker":{"forcePullImage":false,"image":"registry.gs.youyuwo.com/base/ata-jenkins:2.150.1-v4","parameters":[],"privileged":false},"volumes":[{"containerPath":"/var/jenkins_home/users","hostPath":"/data/ata/jenkins_home/users","mode":"RW"},{"containerPath":"/var/jenkins_home/jobs","hostPath":"/data/ata/jenkins_home/jobs","mode":"RW"}]},"env":{"SSH_KNOWN_HOSTS":"github.com","JENKINS_CONTEXT":"/service/jenkins","JENKINS_SLAVE_AGENT_PORT":"50000","JENKINS_AGENT_ROLE":"*","JVM_OPTS":"-Xms1024m-Xmx1024m","JENKINS_MESOS_MASTER":"zk://zk-1.zk:2181,zk-2.zk:2181,zk-3.zk:2181,zk-4.zk:2181,zk-5.zk:2181/mesos","JENKINS_AGENT_USER":"root","JENKINS_OPTS":"","JENKINS_FRAMEWORK_NAME":"jenkins","MARATHON_NAME":"marathon"},"healthChecks":[{"gracePeriodSeconds":30,"ignoreHttp1xx":false,"intervalSeconds":60,"maxConsecutiveFailures":3,"path":"/service/jenkins","portIndex":0,"protocol":"HTTP","ipProtocol":"IPv4","timeoutSeconds":20,"delaySeconds":15}],"labels":{"DCOS_PACKAGE_OPTIONS":"","DCOS_SERVICE_SCHEME":"http","DCOS_PACKAGE_SOURCE":"https://universe.mesosphere.com/repo","DCOS_PACKAGE_METADATA":"","DCOS_SERVICE_NAME":"jenkins","DCOS_PACKAGE_FRAMEWORK_NAME":"jenkins","DCOS_SERVICE_PORT_INDEX":"0","DCOS_PACKAGE_DEFINITION":"""DCOS_PACKAGE_VERSION":"3.5.4-2.150.1","DCOS_PACKAGE_NAME":"jenkins","MARATHON_SINGLE_INSTANCE_APP":"true"},"portDefinitions":[{"port":10113,"name":"nginx","protocol":"tcp"},{"port":10120,"name":"jenkins","protocol":"tcp"}],"upgradeStrategy":{"maximumOverCapacity":0,"minimumHealthCapacity":0}}'
```
## 13. 安装镜像库（最后一个agent节点执行）
### 12.1 下载registry.config.yml
```bash
mkdir -p /data/ata/registry/
cd /data/ata/registry/
curl -O https://downloads.ata.cloud.com/cluster/install/0.3.7/jenkins/registry.config.yml
EOF
```
### 12.2 启动registry容器
> curl中填写任意一个master节点ip，此处假设mater节点ip为192.168.1.143

> json中instances固定为1，cpus为0.5，mem为512（单位为MiB）

```bash
curl -i -H "Content-type: application/json" -X PUT http://192.168.1.143:8080/v2/apps/atacloud-admin/registry?force=true -d '{"labels":{"HAPROXY_GROUP":"external"},"id":"/atacloud-admin/registry","backoffFactor":1.15,"backoffSeconds":1,"container":{"portMappings":[{"containerPort":5000,"hostPort":0,"labels":{"VIP_0":"192.168.0.1:443"},"protocol":"tcp","servicePort":5000,"name":"default"}],"type":"DOCKER","volumes":[{"containerPath":"/var/lib/registry","hostPath":"/data/ata/docker-registry","mode":"RW"}],"docker":{"image":"registry","forcePullImage":false,"privileged":false,"parameters":[]}},"cpus":0.5,"disk":0,"healthChecks":[{"gracePeriodSeconds":60,"intervalSeconds":60,"maxConsecutiveFailures":3,"portIndex":0,"timeoutSeconds":20,"delaySeconds":15,"protocol":"TCP","ipProtocol":"IPv4"}],"instances":1,"mem":512,"networks":[{"mode":"container/bridge"}],"requirePorts":false}'
```
## 13. 安装ata sword（最后一个agent节点执行）
### 12.1 下载data.zip
```bash
mkdir -p /data/ata/sword/
cd /data/ata/sword/
curl -O https://downloads.ata.cloud.com/cluster/install/0.3.7/sword/data.zip
unzip data.zip
EOF
```
### 12.2 启动sword容器
> curl中填写任意一个master节点ip，此处假设mater节点ip为192.168.1.143

```bash
curl -i -H "Content-type: application/json" -X PUT http://192.168.1.143:8080/v2/apps/atacloud-admin/sword?force=true -d 'json还未整理，等一期代码开发完整'
```