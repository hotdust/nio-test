package nio;

import java.nio.channels.FileChannel;

public class TestMappedAndChannel {
    public static void main(String[] args) {
        // 3，再测试 10 批数据，分批写入 buffer，然后一次通过 channel 写入文件，然后 force，和“分10次写入 mapped，并且每次都 force”。看看哪个速度快。

//        Integer a = 10000;
//        Integer b = 10000;

//        Integer a = new Integer(10);
//        Integer b = new Integer(10);

//        String a = "aabadafsda";
//        String b = "aabadafsda";

//        String a = new String("aabadafsda");
//        String b = new String("aabadafsda");


//        System.out.println(a==b);

//        FileChannel ch = new FileChannel;
//        ch.transferTo()
    }
}
