package weightround;

import lombok.Data;

import java.util.Date;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/5/31 21:52
 */
@Data
public class Server {
    private String ip;
    /**
     * 该服务器的权重，这个值是固定不变的；
     */
    private int weight;
    /**
     * 该服务器的权重，这个值是固定不变的；
     */
    private int effectiveWeight;
    /**
     * 服务器目前的权重。一开始为0，之后会动态调整。
     */
    private int currentWeight;
    private boolean down;
    public Date checkedDate;

    Server(String ip, int weight) {
        super();
        this.ip = ip;
        this.weight = weight;
        this.effectiveWeight = this.weight;
        this.currentWeight = 0;
        this.down = this.weight < 0;
    }
}
