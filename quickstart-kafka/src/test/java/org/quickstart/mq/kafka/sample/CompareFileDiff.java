package org.quickstart.mq.kafka.sample;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompareFileDiff {

    @Test
    public void testCompareFileDiff() throws IOException {

        String basePath = "/Users/lengfeng/git/quickstart-mq/quickstart-kafka/src/test/java/org/quickstart/mq/kafka/sample/";

        String sourcePath = basePath + "dddd.txt";
        Path path = Paths.get(sourcePath);
        List<String> lines = Files.readAllLines(path);

        String targetPath = basePath + "ddddd.txt";
        List<String> targetLines = Files.readAllLines(Paths.get(targetPath));

        List<String> diff = getDiffrent4(lines, targetLines);
        diff.forEach(System.out::println);

    }

    // 方法1，两层遍历查找，遍历次数为list1.size()*list2.size()，有点蠢
    private static List<String> getDiffrent1(List<String> list1, List<String> list2) {
        // diff 存放不同的元素
        List<String> diff = new ArrayList<String>();
        // 开始查找的时间，用于计时
        long start = System.currentTimeMillis();
        for (String str : list1) {
            if (!list2.contains(str)) {
                diff.add(str);
            }
        }
        // 计时
        System.out.println("方法1 耗时：" + (System.currentTimeMillis() - start) + " 毫秒");
        return diff;
    }

    // 方法2，两层遍历查找，用retainAll()方法查找，也很蠢，方法底层依旧是两层遍历
    private static List<String> getDiffrent2(List<String> list1, List<String> list2) {
        long start = System.currentTimeMillis();
        list1.retainAll(list2);// 返回值是boolean
        System.out.println("方法2 耗时：" + (System.currentTimeMillis() - start) + " 毫秒");
        return list1;
    }

    // 方法3，用Map存放List1和List2的元素作为key，value为其在List1和List2中出现的次数
    // 出现次数为1的即为不同元素，查找次数为list1.size() + list2.size()，较方法1和2，是极大简化
    private static List<String> getDiffrent3(List<String> list1, List<String> list2) {
        List<String> diff = new ArrayList<String>();
        long start = System.currentTimeMillis();
        Map<String, Integer> map = new HashMap<String, Integer>(list1.size() + list2.size());
        // 将List1元素放入Map，计数1
        for (String string : list1) {
            map.put(string, 1);
        }
        // 遍历List2，在Map中查找List2的元素，找到则计数+1；未找到则放入map，计数1
        for (String string : list2) {
            Integer count = map.get(string);
            if (count != null) {
                map.put(string, ++count);// 此处可优化，减少put次数，即为方法4
                continue;
            }
            map.put(string, 1);
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diff.add(entry.getKey());
            }
        }
        System.out.println("方法3 耗时：" + (System.currentTimeMillis() - start) + " 毫秒");
        return diff;
    }

    // 优化方法3，减少put次数
    private static List<String> getDiffrent4(List<String> list1, List<String> list2) {
        List<String> diff = new ArrayList<String>();
        long start = System.currentTimeMillis();
        Map<String, Integer> map = new HashMap<String, Integer>(list1.size() + list2.size());
        List<String> maxList = list1;
        List<String> minList = list2;
        if (list2.size() > list1.size()) {
            maxList = list2;
            minList = list1;
        }
        for (String string : maxList) {
            map.put(string, 1);
        }
        for (String string : minList) {
            Integer count = map.get(string);
            if (count != null) {
                map.put(string, ++count);
                continue;
            }
            map.put(string, 1);
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diff.add(entry.getKey());
            }
        }
        System.out.println("方法4 耗时：" + (System.currentTimeMillis() - start) + " 毫秒");
        return diff;

    }

}
