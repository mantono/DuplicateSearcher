#!bin/bashi
cd /tmp
git clone https://git-wip-us.apache.org/repos/asf/ant-ivy.git &&
cd ant-ivy &&
ant jar &&
mkdir -pv ~/.ant/lib &&
cp -frv build/artifact/org.apache.ivy*.jar ~/.ant/lib/ &&
echo "Ivy installed"
