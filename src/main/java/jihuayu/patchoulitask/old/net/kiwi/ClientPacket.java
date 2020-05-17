//from kiwi
package jihuayu.patchoulitask.old.net.kiwi;

public class ClientPacket extends Packet {

    @Override
    public void send() {
        NetworkChannel.sendToServer(this);
    }

}
