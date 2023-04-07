package com.wcj.forest.transport.server;

import com.wcj.forest.core.route.Node;
import com.wcj.forest.transport.codec.Bencode;
import com.wcj.forest.util.DHTUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @author wengchengjian
 * @date 2023/4/7-17:06
 */
@Slf4j
@Data
@AllArgsConstructor
public class Sender {

    private Map<SocketAddress, Channel> channels;

    private Bencode bencode;

    public void add(SocketAddress address, Channel channel){
        channels.put(address,channel);
    }

    /**
     * find_node
     * @param node 自己的id
     * @param target 目标id
     * @param address 请求地址
     * @param num
     */
    public void findNode(String node, String target, InetSocketAddress address){
        if(!channels.get(address).isWritable()){
            return;
        }
        FindNodeRequest findNodeRequest=new FindNodeRequest(node,target);
        channels.get(address).writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bencode.encode(DHTUtil.beanToMap(findNodeRequest))), address));
    }
    /**
     *
     * 回复find_node的回复
     */
    public  void findNodeReceive(String messageId, InetSocketAddress address, String nodeId, List<Node> nodeList) {
        if(!channels.get(address).isWritable()){
            return;
        }
        FindNodeResponse findNodeResponse=new FindNodeResponse(messageId,nodeId,new String(Node.toBytes(nodeList), CharsetUtil.ISO_8859_1));
        channels.get(address).writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bencode.encode(DHTUtil.beanToMap(findNodeResponse))), address));
    }

    /**
     * 回复get_peers
     */
    public  void getPeersReceive(String messageId, InetSocketAddress address, String nodeId, String token, List<Node> nodeList) {
        if(!channels.get(address).isWritable()){
            return;
        }
        GetPeersResponse getPeersResponse = new GetPeersResponse(messageId,nodeId, token, new String(Node.toBytes(nodeList), CharsetUtil.ISO_8859_1));
        channels.get(address).writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bencode.encode(DHTUtil.beanToMap(getPeersResponse))), address));
    }


    /**
     * 回复ping请求
     */
    public  void pingReceive(InetSocketAddress address, String nodeID,String messageId) {
        if(!channels.get(address).isWritable()){
            return;
        }
        PingResponse pingResponse=new PingResponse(messageId,nodeID);
        channels.get(address).writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bencode.encode(DHTUtil.beanToMap(pingResponse))), address));
    }

    /**
     * 回复ping请求
     */
    public  void ping(InetSocketAddress address, String nodeID) {
        if(!channels.get(address).isWritable()){
            return;
        }
        PingRequest pingRequest=new PingRequest(nodeID);
        channels.get(address).writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bencode.encode(DHTUtil.beanToMap(pingRequest))), address));
    }


    /**
     * 批量发送get_peers
     */
    public  void getPeersBatch(List<InetSocketAddress> addresses, String nodeId,String infoHash,String messageId) {
        if(!channels.get(addresses).isWritable()){
            return;
        }
        GetPeersRequest request = new GetPeersRequest(messageId,nodeId, infoHash);
        for (InetSocketAddress address : addresses) {
            try {
                channels.get(addresses).writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bencode.encode(DHTUtil.beanToMap(request))), address));
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("发送GET_PEERS,失败.e:{}",e.getMessage());
            }
        }
    }


    /**
     * 回复announce_peer
     */
    public  void announcePeerReceive(String messageId,InetSocketAddress address, String nodeId) {
        if(!channels.get(address).isWritable()){
            return;
        }
        AnnouncePeersResponse response = new AnnouncePeersResponse(messageId,nodeId);
        channels.get(address).writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bencode.encode(DHTUtil.beanToMap(response))), address));
    }

    /**
     * announce_peer
     */
    public  void announcePeer(String id,Integer impliedPort,String infoHash,Integer port,String token,InetSocketAddress address) {
        if(!channels.get(address).isWritable()){
            return;
        }
        AnnouncePeersRequest announcePeersRequest = new AnnouncePeersRequest(id,impliedPort,infoHash,port,token);
        channels.get(address).writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bencode.encode(DHTUtil.beanToMap(announcePeersRequest))), address));
    }
}
