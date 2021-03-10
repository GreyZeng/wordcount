import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;

//则会统计input.txt中的以下几个指标
//
//        1. **统计文件的字符数（对应输出第一行）**：
//
//        - 只需要统计Ascii码，汉字不需考虑
//        - 空格，水平制表符，换行符，均**算**字符
//
//        2. 统计文件的**单词总数（对应输出第二行）**，单词：至少以**4**个英文字母开头，跟上字母数字符号，单词以分隔符分割，**不区分大小写**。
//
//        - 英文字母： A-Z，a-z
//        - 字母数字符号：A-Z， a-z，0-9
//        - 分割符：空格，非字母数字符号
//        - **例**：file123是一个单词， 123file不是一个单词。file，File和FILE是同一个单词
//
//        3. 统计文件的**有效行数（对应输出第三行）**：任何包含**非空白**字符的行，都需要统计。
//
//        4. 统计文件中各**单词的出现次数（对应输出接下来10行）**，最终只输出频率最高的**10**个。
//
//        - 频率相同的单词，优先输出字典序靠前的单词。
//        > 例如，windows95，windows98和windows2000同时出现时，则先输出windows2000
//        - 输出的单词统一为小写格式
//
//        然后将统计结果输出到output.txt，输出的格式如下；其中`word1`和`word2` 对应具体的单词，`number`为统计出的个数；换行使用'\n'，编码统一使用UTF-8。
//
//        ```
//        characters: number
//        words: number
//        lines: number
//        word1: number
//        word2: number
//        ```
public class WordCount {
    public static char[] getChars(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes).flip();
        CharBuffer cb = UTF_8.decode(bb);
        return cb.array();
    }

    public static void main(String[] args) throws IOException {
        if (args == null || args.length < 2) {
            System.out.println("参数不合法");
            return;
        }
        String inputPath = args[0];
        String outputPath = args[1];
        // 这个方法有大小限制
        byte[] bytes = readAllBytes(Paths.get(inputPath));
        int charNum = bytes.length;
        char[] c = getChars(bytes);
        String text = String.valueOf(c);
        Map<String, Integer> result = new HashMap<>();
        // 在求line num的时候，顺带把有效的line存到这里
        List<String> lines = getAllLine(text);
        // 在求word数量的时候，把有效的word存在这里，之所以用TreeSet是保持输出的word可以按要求排序
        Map<String, Integer> words = new HashMap<>();
        // 大根堆 保证词频大的在堆顶，词频一样保证字典序小的在堆顶
        PriorityQueue<Word> topWords = new PriorityQueue<>(new WordComparator());
        // 默认显示前十个
        int top = 10;

        int lineNum = lines.size();
        int wordNum = wordNum(lines, words);
        result.put("characters", charNum);
        result.put("words", wordNum);
        result.put("lines", lineNum);
        fillTopWords(words, topWords);
        // System.out.println(result);
        output(result, topWords, top, outputPath);
    }

    private static List<String> getAllLine(String text) throws IOException {
        String[] split = text.split("\n");
        List<String> lines = new ArrayList<>();
        for (String line : split) {
            if (!line.trim().isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static void output(Map<String, Integer> result, PriorityQueue<Word> topWords, int top, String outputPath) {
        StringBuilder sb = new StringBuilder();
        sb.append("characters: ").append(result.get("characters")).append("\n")
                .append("words: ").append(result.get("words")).append("\n")
                .append("lines: ").append(result.get("lines"));

        while (!topWords.isEmpty() && top != 0) {
            sb.append("\n");
            top--;
            Word topWord = topWords.poll();
            sb.append(topWord.value).append(": ").append(topWord.times);
        }
        contentToFile(sb.toString(), outputPath);
    }

    /**
     * 将内容写入文件，返回文件路径
     *
     * @param content      写入内容
     * @param fileLocation 文件路径
     * @return
     */
    public static void contentToFile(String content, String fileLocation) {
        try (FileWriter fw = new FileWriter(fileLocation)) {
            //将str里面的内容读取到fw所指定的文件中
            fw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Word {
        public String value;
        public int times;

        public Word(String v, int t) {
            value = v;
            times = t;
        }
    }

    /**
     * 定义比较器 先看字符出现的次数，次数出现多的在前 如果次数一样，字典序小的在前面
     */
    public static class WordComparator implements Comparator<Word> {

        @Override
        public int compare(Word o1, Word o2) {
            // 之所以直接使用o2.times - o1.times
            // 是因为怕times的差会超过Integer.MIN_VALUE, 导致出错
            if (o1.times > o2.times) {
                return -1;
            } else if (o1.times < o2.times) {
                return 1;
            } else {
                return o1.value.compareTo(o2.value);
            }
        }
    }

    /**
     * 获取单词数量
     *
     * @param lines   lines中的每一行肯定是不全是空白符的有效行数，且trim过了
     * @param wordMap 获取数量的同时，把单词直接放到words中
     * @return
     */
    public static int wordNum(List<String> lines, Map<String, Integer> wordMap) {
        int wordNum = 0;
        for (String line : lines) {
            // 直接统一转换成小写
            line = line.toLowerCase();
            wordNum += wordNum(line, wordMap);
        }
        return wordNum;
    }

    public static int wordNum(String line, Map<String, Integer> wordMap) {
        char[] str = line.toCharArray();
        int wordNum = 0;
        // TODO
        return -1;
    }


    /**
     * 判断一个字符串
     *
     * @param str
     * @return
     */
    public static boolean isValidWord(String str) {
        if (str.length() < 4) {
            return false;
        }
        int len = str.length();
        boolean result;
        for (int i = 0; i < 4; i++) {
            char c = str.charAt(i);
            result = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
            if (!result) {
                return false;
            }
        }
        for (int i = 4; i < len; i++) {
            char c = str.charAt(i);
            result = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
            if (!result) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取词频最高的前十个单词
     *
     * @param words 单词表
     * @param heap  按规定排序的单词列表
     */
    public static void fillTopWords(Map<String, Integer> words, PriorityQueue<Word> heap) {
        Set<Map.Entry<String, Integer>> entries = words.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            heap.offer(new Word(entry.getKey(), entry.getValue()));
        }
    }
}
