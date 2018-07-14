import com.junyou.bus.rolebusiness.entity.RoleBusinessInfo;
import com.sun.management.OperatingSystemMXBean;
import org.junit.Test;

import java.lang.management.ManagementFactory;

public class TestTime {

    @Test
    public void test1() {

        long t1 =  System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            System.currentTimeMillis();
        }
        long t2 =  System.currentTimeMillis();
        //一千万次循环，748ms  几乎可以负略不急
        System.out.println(t1 - t2);
    }


    /**
     * 克隆没有直接new对象速度快        克隆使用的内存：9658290176       byte  9000M
     *                                直接copy使用的内存：8316477440    byte  8000M
     */
    @Test
    public void tes2() {
        long t1 =  System.currentTimeMillis();

        try {  for (int i = 0; i < 100000000; i++) {
            RoleBusinessInfo roleBusinessInfo = new RoleBusinessInfo();
            roleBusinessInfo.clone();
//            roleBusinessInfo.copy();
        }

            OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            // 已使用的物理内存
            long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize());
            System.out.println((usedMemory/1024/1024) +"M");

        } catch (Exception e) {
            e.printStackTrace();
        }

        long t2 =  System.currentTimeMillis();
        //一千万次循环，748ms  几乎可以负略不急
        System.out.println((t2 - t1)+"ms");
        System.gc();
    }
}
