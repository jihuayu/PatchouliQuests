//from kiwi
package jihuayu.patchoulitask.net.kiwi;

public class ClientPacket extends Packet {

    @Override
    public void send() {
        NetworkChannel.sendToServer(this);
    }

}
