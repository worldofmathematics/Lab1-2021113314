

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class calcShortestPathTest extends TestCase {
    private final static HashMap<String, Map<String, Integer>> textToGraph = new HashMap<>();
    public static void addNode(String node) {
        textToGraph.putIfAbsent(node, new HashMap<>());
    }
    //添加边
    public static void addEdge(String source, String destination) {
        textToGraph.get(source).merge(destination, 1, Integer::sum);
    }

    public static void buildDirectedGraph(String filePath) throws IOException {
        textToGraph.clear();
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
//            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(" "); // 将每行内容添加到 content 中，并添加空格作为单词间的分隔符
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将非字母字符替换为空格，并转换为小写
        String processedContent = content.toString().replaceAll("[^a-zA-Z\\n\\r]", " ").toLowerCase();
        String[] words = processedContent.split("\\s+"); // 按空格分割单词
        for (int i = 0; i < words.length - 1; i++) {
            String currentWord = words[i];
            String nextWord = words[i + 1];
            addNode(currentWord);
            addNode(nextWord);
            addEdge(currentWord, nextWord);
        }
    }
    public void printGraph() {
        System.out.println("Vertices and edges in the graph:");
        for (Map.Entry<String, Map<String, Integer>> entry : textToGraph.entrySet()) {
            String vertex = entry.getKey();
            Map<String, Integer> edges = entry.getValue();
            System.out.print(vertex + ": ");
            for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                String destination = edge.getKey();
                int weight = edge.getValue();
                System.out.print("(" + vertex + " -> " + destination + ": " + weight + "), ");
            }
            System.out.println();
        }
    }
    private void initGraph() throws Exception {
        String Path = "./data/lab3.txt";
        buildDirectedGraph(Path);
    }
    private static String calcShortestPath(HashMap<String, Map<String, Integer>> textToGraph,String word1,String word2) throws Exception
    {
        String result = null;
//        System.out.println(textToGraph.containsKey(word1));
//        System.out.println(textToGraph.containsKey(word2));

        if (textToGraph.containsKey(word1) && textToGraph.containsKey(word2)) {
            Map<List<String>, Integer>  shortestPath = TextToGraph.calcShortestPath(word1, word2,textToGraph);
            if (!shortestPath.isEmpty()){
                result=TextToGraph.formatShortestPath(textToGraph,shortestPath,word1,word2);
            }else {
                result=TextToGraph.formatShortestPath(textToGraph,null,word1,word2);
            }
        }
        else
        {
            result= TextToGraph.formatShortestPath(textToGraph,null,word1,word2);
        }
        return result;
    }
    @Before
    public void setup() throws Exception{
        initGraph();
    }

    @Test
    public void testShortestPath1() throws Exception
    {
        setup();
        String word1="a";
        String word2="a";
        String result=calcShortestPath(textToGraph,word1,word2);
        String expectedOutput = "a" + "\nthe shortest path:0;\n";
        assertEquals(expectedOutput,result);
    }
    @Test
    public void testShortestPath2() throws Exception
    {
        setup();
        String word1="a";
        String word2="b";
        String result=calcShortestPath(textToGraph,word1,word2);
        String expectedOutput = word1 + "→" +word2+ "\nthe shortest path:1;\n";
        assertEquals(expectedOutput,result);
    }
    @Test
    public void testShortestPath3() throws Exception {
        setup();
        String word1 = "a";
        String word2 = "d";
        String result = calcShortestPath(textToGraph, word1, word2);

        // Define expected output strings
        Set<String> expectedOutputs = new HashSet<>();
        expectedOutputs.add("a" + "→" + "c" + "→" + "d\n" + "the shortest path:2;");
        expectedOutputs.add("a" + "→" + "b" + "→" + "d\n" + "the shortest path:2;");

        // Split the result into sections based on semicolons
        String[] resultSections = result.split("(?<=;)");

        // Use a set to track encountered sections
        Set<String> encounteredSections = new HashSet<>();

        boolean allUnique = true;
        boolean foundMatch = false;

        for (String section : resultSections) {
            if (encounteredSections.contains(section)) {
                allUnique = false;
            }
            encounteredSections.add(section);
            if (expectedOutputs.contains(section)) {
                foundMatch = true;
            }
        }
        assertTrue(encounteredSections.size()-1==expectedOutputs.size());
        assertTrue(allUnique);
        assertTrue(foundMatch);
    }
    @Test
    public void testShortestPath4() throws Exception
    {
        setup();
        String word1="m";
        String word2="n";
        String result=calcShortestPath(textToGraph,word1,word2);
        String expectedOutput = word1+" or "+word2+" not in the graph!\n";
        assertEquals(expectedOutput,result);
    }
    @Test
    public void testShortestPath5() throws Exception
    {
        setup();
        String word1="f";
        String word2="c";
        String result=calcShortestPath(textToGraph,word1,word2);
        String expectedOutput = "No path found between " + word1 + " and " + word2 + "\n";
        assertEquals(expectedOutput,result);
    }
    @Test
    public void testShortestPath6() throws Exception
    {
        setup();
        String word1="a";
        String word2="n";
        String result=calcShortestPath(textToGraph,word1,word2);
        String expectedOutput = word1+" or "+word2+" not in the graph!\n";
        assertEquals(expectedOutput,result);
    }
    @Test
    public void testShortestPath7() throws Exception
    {
        setup();
        String word1="m";
        String word2="b";
        String result=calcShortestPath(textToGraph,word1,word2);
        String expectedOutput = word1+" or "+word2+" not in the graph!\n";
        assertEquals(expectedOutput,result);
    }
}