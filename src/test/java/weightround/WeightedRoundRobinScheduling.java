package weightround;

import com.alibaba.fastjson.JSON;

import java.util.*;
import java.util.Map.Entry;

/**
 * 权重轮询调度算法(WeightedRound-RobinScheduling)-Java实现
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/5/31 21:43
 */
public class WeightedRoundRobinScheduling {

    //服务器集合
    private List<Server> serverList;

    private Server GetBestServer() {
        Server server;
        Server best = null;
        int total = 0;
        // 开始遍历所有节点
        for (Server aServerList : serverList) {
            // 当前服务器对象
            server = aServerList;

            //当前服务器已宕机，排除
            if (server.isDown()) {
                continue;
            }
            // 设置当前选择的权重 = 当前选择的权重+有效权重
            server.setCurrentWeight(server.getCurrentWeight() + server.getEffectiveWeight());
            // 总数累加有效权重
            total += server.getEffectiveWeight();

         /*   // 如果权重小于当前节点权重
            if (server.getEffectiveWeight() < server.getWeight()) {
                // 有效权重+1
                server.setEffectiveWeight(server.getEffectiveWeight() + 1);
            }
            // 如果权重小于当前节点权重
            if (server.getEffectiveWeight() > server.getWeight()) {
                // 有效权重+1
                server.setEffectiveWeight(server.getEffectiveWeight() - 1);
            }
*/
            //如果没有选择出最佳，或则当前节点权重大于最佳节点权重，则设置当前节点为最佳
            if (best == null || server.getCurrentWeight() > best.getCurrentWeight()) {
                best = server;
            }
            System.out.println(JSON.toJSON(server));

        }

        // 没有选择出最佳
        if (best == null) {
            return null;
        }

        best.setCurrentWeight(best.getCurrentWeight() - total);
        best.checkedDate = new Date();
        return best;
    }


    private void init() {
        Server s1 = new Server("192.168.0.103", 3);//3
        Server s2 = new Server("192.168.0.102", 2);//2
        Server s3 = new Server("192.168.0.106", 6);//6
        Server s4 = new Server("192.168.0.104", 4);//4
        Server s5 = new Server("192.168.0.101", 1);//1
        serverList = new ArrayList<Server>();
        serverList.add(s1);
        serverList.add(s2);
        serverList.add(s3);
        serverList.add(s4);
        serverList.add(s5);
    }

    public void add(int i) {
        Server s = new Server("192.168.0.1" + i, i - 15);
        serverList.add(s);
    }

    public void change() {
        serverList.get(2).setWeight(1);
    }

    public Server getServer(int i) {
        if (i < serverList.size()) {
            return serverList.get(i);
        }
        return null;
    }


    public static void main(String[] args) {
        WeightedRoundRobinScheduling obj = new WeightedRoundRobinScheduling();

        obj.init();

        Map<String, Integer> countResult = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            Server s = obj.GetBestServer();
            String log = "ip:" + s.getIp();
            if (countResult.containsKey(log)) {
                countResult.put(log, countResult.get(log) + 1);
            } else {
                countResult.put(log, 1);
            }
            //System.out.println(log + "\t" + i);

            if (i == 10) {
                obj.change();
            }

          /*  //动态添加服务器
            if (i == 20 || i == 22) {
                obj.add(i);
            }

            //动态停止服务器
            if (i == 30) {
                obj.getServer(2).setDown(true);
                obj.getServer(3).setDown(true);
            }*/
        }

        for (Entry<String, Integer> map : countResult.entrySet()) {
            System.out.println("服务器 " + map.getKey() + " 请求次数： " + map.getValue());
        }
    }

}