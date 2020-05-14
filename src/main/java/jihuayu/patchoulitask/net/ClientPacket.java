//from kiwi
package jihuayu.patchoulitask.net;

public class ClientPacket extends Packet
{

    @Override
    public void send()
    {
        NetworkChannel.sendToServer(this);
    }

}
