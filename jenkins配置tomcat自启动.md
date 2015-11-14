# jenkins自动启动tomcat脚本

```sh

BUILD_ID=DONTKILLME 使jenkins不杀死这个shell脚本
pid=`ps -ef | grep "ethank-ktv-manager-dev" | grep -v grep | awk '{print $2}'` 
if [ "$pid" != "" ] ; then
kill -9 $pid
rm -f /opt/ethank-ktv-manager-dev/pid
else 
rm -f /opt/ethank-ktv-manager-dev/pid
fi
cd /root/.jenkins/workspace/ethank-ktv-manager-dev/ethank-ktv-manager/target
cp /root/.jenkins/workspace/ethank-ktv-manager-dev/ethank-ktv-manager/target/ethank-ktv-manager*.war /opt/ethank-ktv-manager-dev/webapps/
cd /opt/ethank-ktv-manager-dev/webapps/
mv ethank-ktv-manager*SNAPSHOT.war ethank-ktv-manager.war
rm -rf ethank-ktv-manager
chown -R root:root ethank-ktv-manager.war
sh /opt/ethank-ktv-manager-dev/bin/startup.sh

```
