package nio;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TestMappedByteBuffer {
    private static int length = 0x2FFFFFFF;//1G
//private static int length = 0x4CCCCCC;//100m
//private static int length = 0x7AE147;//10m
//    private static int length = 0x189374;//2m

    private abstract static class Tester {
        private String name;
        public Tester(String name) {
            this.name = name;
        }
        public void runTest() {
            System.out.print(name + ": ");
            long start = System.currentTimeMillis();
            test();
            System.out.println(System.currentTimeMillis()-start+" ms");
        }
        public abstract void test();
    }
    private static Tester[] testers = {
//        new Tester("Stream RW") {
//            public void test() {
//                try (FileInputStream fis = new FileInputStream(
//                        "/Users/iss/src/a.txt");
//                     DataInputStream dis = new DataInputStream(fis);
//                     FileOutputStream fos = new FileOutputStream(
//                                "/Users/iss/src/a.txt");
//                     DataOutputStream dos = new DataOutputStream(fos);) {
//
//                    byte b = (byte)0;
//                    for(int i=0;i<length;i++) {
//                        dos.writeByte(b);
//                        dos.flush();
//                    }
//                    while (dis.read()!= -1) {
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        },
        new Tester("Mapped RW") {
            public void test() {
                try (FileChannel channel = FileChannel.open(Paths.get("/Users/iss/src/b.txt"),
                        StandardOpenOption.READ, StandardOpenOption.WRITE);) {
                    MappedByteBuffer mapBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, length);
                    for(int i=0;i<length;i++) {
                        mapBuffer.put((byte)0);
                    }
                    mapBuffer.flip();
                    while(mapBuffer.hasRemaining()) {
                        mapBuffer.get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },
        new Tester("Mapped PRIVATE") {
            public void test() {
                try (FileChannel channel = FileChannel.open(Paths.get("/Users/iss/src/c.txt"),
                        StandardOpenOption.READ, StandardOpenOption.WRITE);) {
                    MappedByteBuffer mapBuffer = channel.map(FileChannel.MapMode.PRIVATE, 0, length);
                    for(int i=0;i<length;i++) {
                        mapBuffer.put((byte)0);
                    }
                    mapBuffer.flip();
                    while(mapBuffer.hasRemaining()) {
                        mapBuffer.get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    public static void main(String[] args) {
        for(Tester tester:testers) {
            tester.runTest();
        }
    }
}