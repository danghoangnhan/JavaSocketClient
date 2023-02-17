#Check Java‘s Processes
ps -aux|grep "Java"
#Check All Processes And Port
sudo lsof -i -P -n | grep LISTEN

nohup java -Xmx1G -Xms1G -jar Java_JAS106_Gateway.jar &

nohup java -Xmx1G -Xms1G -jar Java_JAS208_Gateway.jar &

nohup java -Xmx4G -Xms4G -jar Java_DataReceiver.jar &

nohup java -Xmx1G -Xms1G -jar Java_Scheduler.jar &

nohup java -Xmx1G -Xms1G -jar kafdrop.jar &


netsh interface portproxy add v4tov4 listenport=2375 listenaddress=60.251.157.48 connectaddress=127.0.0.1
connectport=2375

export $(grep -v '^#' development.env | xargs -d '\n')

unset $(grep -v '^#' development.env | sed -E 's/(.*)=.*/\1/' | xargs)
