import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class TextToGraph1 extends JFrame{

    //protected static Graph graph;
    private static HashMap<String, Map<String, Integer>> textToGraph = new HashMap<>();
    private JPanel mainPanel, topPanel, centerPanel;
    private JTextArea textArea;
    private JButton chooseFileButton;
    private JButton buildGraphButton;
    private JButton queryButton;
    private JButton insertButton;
    private JButton shortestPathButton;
    private JButton randomPathButton;
    private  JButton EXITBUTTON;

    private JTextField word1Field;
    private JTextField word2Field;

    //private TextToGraph textToGraph;
    private File selectedFile;

    public TextToGraph1() {
        setTitle("Directed Graph Operations");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        add(scrollPane, BorderLayout.CENTER);
        // 设置窗口居中显示
        setLocationRelativeTo(null);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        chooseFileButton = new JButton("Choose Text File");
        buildGraphButton = new JButton("Build Graph");
        queryButton = new JButton("Query BridgeWords");
        EXITBUTTON=new JButton("exit!");


        word1Field = new JTextField(10);
        word2Field = new JTextField(10);

        controlPanel.add(chooseFileButton);
        controlPanel.add(buildGraphButton);
        controlPanel.add(queryButton);
        controlPanel.add(EXITBUTTON);
        add(controlPanel, BorderLayout.SOUTH);

        //TextToGraph textToGraph = new TextToGraph();

        // 添加监听
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
                int result = fileChooser.showOpenDialog(TextToGraph1.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    //buildDirectedGraph(selectedFile.getAbsolutePath());
                    //printGraph();
                    selectedFile = fileChooser.getSelectedFile();
                    textArea.append("Selected file: " + selectedFile.getAbsolutePath() + "\n");
                }
            }
        });

        buildGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    try {
                        String dotPath = "./result/directed_graph.dot";
                        String imagePath = "./result/directed_graph.png";
                        buildDirectedGraph(selectedFile.getAbsolutePath());
                        generateDotFile(dotPath);
                        convertDotToImage(dotPath,imagePath);
                        displayImage(imagePath);
                        textArea.append("Directed graph built successfully.\n");
                    } catch (Exception ex) {
                        textArea.append("Error building directed graph: " + ex.getMessage() + "\n");
                    }
                } else {
                    textArea.append("Please choose a text file first.\n");
                }
            }
        });

        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 提示用户输入要查询的词
                String word1 = JOptionPane.showInputDialog(TextToGraph1.this, "Enter the first word:");
                String word2 = JOptionPane.showInputDialog(TextToGraph1.this, "Enter the second word:");

                // 如果用户未输入任何内容，则不进行查询
                if (word1 == null || word1.isEmpty() || word2 == null || word2.isEmpty()) {
                    textArea.append("No words entered. Bridge word query canceled.\n");
                    return;
                }
                if (!textToGraph.containsKey(word1) && !textToGraph.containsKey(word2)) {
                    textArea.append("No " + word1 + " or " + word2 + " in the graph!\n");
                    return;
                } else if (!textToGraph.containsKey(word1)) {
                    textArea.append("No " + word1 + " in the graph!\n");
                    return;
                } else if (!textToGraph.containsKey(word2)) {
                    textArea.append("No " + word2 + " in the graph!\n");
                    return;
                }
                // 执行桥接词查询
                List<String> bridgeWords = queryBridgeWords(word1.trim().toLowerCase(), word2.trim().toLowerCase(), true);
                if (bridgeWords.isEmpty()) {
                    textArea.append("No bridge words found.\n");
                } else if(bridgeWords.size()==1){
                    textArea.append("The bridge words from " + word1 + " to " + word2 + " is: "+bridgeWords.get(0) + "\n");
                }else{
                    textArea.append("The bridge words from " +word1 + " to " +word2 + " are: ");
                    for (int i = 0; i < bridgeWords.size(); i++) {
                        textArea.append(bridgeWords.get(i));
                        if (i < bridgeWords.size() - 2) {
                            textArea.append(", ");
                        } else if (i == bridgeWords.size() - 2) {
                            textArea.append(", and ");
                        }
                    }
                    textArea.append("\n");
                }
            }
        });
        EXITBUTTON.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TextToGraph1 ui = new TextToGraph1();
                ui.setVisible(true);
            }
        });
    }
    public static void addNode(String node) {
        textToGraph.putIfAbsent(node, new HashMap<>());
    }
    //添加边
    public static void addEdge(String source, String destination) {
        textToGraph.get(source).merge(destination, 1, Integer::sum);
    }
    //功能一：构建有向图
    public static void buildDirectedGraph(String filePath) throws IOException {
        textToGraph.clear();
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(" "); // 将每行内容添加到 content 中，并添加空格作为单词间的分隔符
            }
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

    //打印有向图
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
    //功能2：有向图可视化
    public void generateDotFile(String dotFilePath) {
        try (FileWriter writer = new FileWriter(dotFilePath,false)) {
            writer.write("digraph G {\n");
            for (Map.Entry<String, Map<String, Integer>> entry : textToGraph.entrySet()) {
                String vertex = entry.getKey();
                Map<String, Integer> edges = entry.getValue();
                for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                    String destination = edge.getKey();
                    int weight = edge.getValue();
                    writer.write("\t" + vertex + " -> " + destination + " [label=\"" + weight + "\"];\n");
                }
            }
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void convertDotToImage(String dotFilePath, String imageFilePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", dotFilePath, "-o", imageFilePath);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
//            System.out.println(exitCode);
            if (exitCode == 0) {
                System.out.println("Image file generated: " + imageFilePath);
            } else {
                System.out.println("Failed to generate image file.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void convertDotFile(String dotFilePath, Map<List<String>, Integer>  shortPaths) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dotFilePath))) {
            String[] colors = {"red", "blue", "green", "orange", "purple", "yellow", "brown", "cyan"};

            // 为每个最短路径选择颜色
            Map<List<String>, String> pathColors = new HashMap<>();
            for (Map.Entry<List<String>, Integer> entry : shortPaths.entrySet())
            {
                List<String> path=entry.getKey();
                String color = colors[pathColors.size() % colors.length];
                pathColors.put(path, color);
            }
            try (BufferedReader br = new BufferedReader(new FileReader("./result/directed_graph.dot"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("->")) {
                        String[] parts = line.split("->");
                        String fromNode = parts[0].trim();
                        String toNode = parts[1].trim().split("\\[")[0].trim();
                        for (Map.Entry<List<String>, Integer> entry : shortPaths.entrySet()) {
                            // 如果起始节点和终止节点在最短路径集合中，则修改颜色
                            List<String> path=entry.getKey();
                            if (path.contains(fromNode) && path.contains(toNode)
                                    && !fromNode.equals(path.get(path.size() - 1))
                                    && (path.indexOf(toNode) - path.indexOf(fromNode) == 1)) {
                                if (parts[1].trim().endsWith(";")) {
                                    // 如果是，删除最后一个字符';'
                                    parts[1] = parts[1].trim().substring(0, parts[1].trim().length() - 1);
                                }
                                String color = pathColors.get(path);
                                line = "\t" + parts[0].trim() + " -> " + parts[1] + " [color=" + color + "];";
                                break; // 只需要为同一路径内的边选择一种颜色
                            }
                        }
                    }
                    bw.write(line);
                    bw.newLine();
                }
            }
//            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", "./result/marked_graph_all.dot", "-o", "./result/marked_graph_all.png");
//            Process process = processBuilder.start();
//            int exitCode = process.waitFor();
//            if (exitCode == 0) {
//                System.out.println("Image file generated: " + "marked_graph_all.png");
//                // 在屏幕上显示图像
//                displayImage("./result/marked_graph_all.png");
//            } else {
//                System.out.println("Failed to generate image file.");
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //功能三：桥接词查询
    public List<String> queryBridgeWords(String start, String end, Boolean print) {
        List<String> bridgeWords = new ArrayList<>();
        if (!textToGraph.containsKey(start) && !textToGraph.containsKey(end)&&print)
        {
            System.out.println("No " + start + " and "+ end +" in the graph!");
            return bridgeWords;
        }
        if (!textToGraph.containsKey(start) && print) {
            System.out.println("No " + start + " in the graph!");
            return bridgeWords;
        }
        if (!textToGraph.containsKey(end) && print) {
            System.out.println("No " + end + " in the graph!");
            return bridgeWords;
        }
        Map<String, Integer> edges1 = textToGraph.get(start);
        List<String> neighbors1 = new ArrayList<>(edges1.keySet());
        //System.out.println(neighbors1.contains("life"));
        // Print other information
        for(String neighbor1:neighbors1)
        {
            Map<String, Integer> edges2 = textToGraph.get(neighbor1);
            List<String> neighbors2=new ArrayList<>(edges2.keySet());
            if(neighbors2.contains(end))
            {
                bridgeWords.add(neighbor1);
            }
        }
//        System.out.println(bridgeWords);
        if(print)
        {
            if(bridgeWords.size()>1)
            {
                System.out.print("The bridge words from " + start + " to " + end + " are: ");
                for (int i = 0; i < bridgeWords.size(); i++) {
                    System.out.print(bridgeWords.get(i));
                    if (i < bridgeWords.size() - 2) {
                        System.out.print(", ");
                    } else if (i == bridgeWords.size() - 2) {
                        System.out.print(", and ");
                    }
                }
                System.out.println(".");
            } else if (bridgeWords.size()==1) {
                System.out.println("The bridge words from " + start + " to " + end + " is: "+bridgeWords.get(0));
            } else {
                System.out.println("No bridge words from " + start + " to " + end + "!");
            }
        }
        return bridgeWords;
    }

    // 在屏幕上显示图像
    public static void displayImage(String imagePath) {
//        // 读取 PNG 图像文件
//        ImageIcon icon = new ImageIcon(imagePath);
//
//        // 创建标签，用于显示图像
//        JLabel label = new JLabel(icon);
//
//        // 创建窗口
//        JFrame frame = new JFrame();
//        frame.setTitle("PNG Image Viewer");
//        frame.setSize(600, 600);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        // 添加标签到窗口
//        frame.getContentPane().add(label, BorderLayout.CENTER);
//
//        // 设置窗口可见
//        frame.setVisible(true);

        // 创建标签，用于显示图像
        JLabel label = new JLabel();
        // 设置标签的对齐方式为居中
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);


        // 读取 PNG 图像文件并更新标签的图标
        updateImageIcon(label, imagePath);

        // 创建窗口
        JFrame frame = new JFrame();
        frame.setTitle("PNG Image Viewer");
        frame.setSize(1000, 1000);
        // 设置窗口关闭时不退出程序
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // 使用BorderLayout布局管理器，并将标签添加到中心位置
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(label, BorderLayout.CENTER);

        // 调整窗口大小以适应图像大小
        frame.pack();

        // 设置窗口可见
        frame.setVisible(true);

        // 使窗口在屏幕中间显示
        frame.setLocationRelativeTo(null);

        // 添加窗口关闭监听器，以便在窗口关闭时释放资源
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 可以在这里添加释放资源的代码
                // 释放图像资源
                Image image = label.getIcon() != null ? ((ImageIcon)label.getIcon()).getImage() : null;
                if (image != null) {
                    image.flush();
                }

                // 可以选择在这里设置标签的图标为null，以帮助垃圾回收器回收对象
                label.setIcon(null);
            }
        });

        // 强制刷新窗口
        frame.revalidate();
        frame.repaint();
    }

    private static void updateImageIcon(JLabel label, String imagePath) {
        // 检查文件是否存在，如果不存在则返回
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            System.err.println("Image file does not exist: " + imagePath);
            return;
        }

        // 创建新的 ImageIcon 对象并设置到标签上
        ImageIcon icon = new ImageIcon(imagePath);
        label.setIcon(icon);
    }

}
