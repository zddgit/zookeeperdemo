package com.zdd.zk.javaapi;

import org.apache.zookeeper.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class TestJavaAPI {
    //ZooKeeper服务地址
    private static final String SERVER = "127.0.0.1:2181";
    //会话超时时间
    private final int SESSION_TIMEOUT = 30000;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void testSession1() throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(SERVER,SESSION_TIMEOUT,null);
        System.out.println(zooKeeper.getState());
        zooKeeper.close();
    }

    @Test
    public void testSession2() throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(SERVER, SESSION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
                if(event.getState() == Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                    System.out.println("获得连接");
                }
            }
        });
        countDownLatch.await();
        System.out.println(zooKeeper.getState());
        zooKeeper.close();
    }

    @Test
    public  void pathOps() throws IOException, KeeperException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(SERVER,SESSION_TIMEOUT,null);
        String path = "/study";
        zooKeeper.create(path,"studyvalue".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("设置完成");
        Thread.sleep(5000);
        zooKeeper.setData(path,"study1".getBytes(),-1);
        System.out.println("更新完成");
        Thread.sleep(5000);
        zooKeeper.delete(path,-1);
        System.out.println("删除完成");
        zooKeeper.close();
    }
    @Test
    public void watch() throws IOException, InterruptedException, KeeperException {
       final ZooKeeper zooKeeper = new ZooKeeper(SERVER, SESSION_TIMEOUT, null);
        zooKeeper.exists("/study", new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println(event.getPath());
                try {
                    zooKeeper.exists("/study",this);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        countDownLatch.await();
    }


}
