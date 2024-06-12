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
        textArea.setFont(new Font("΢���ź�", Font.PLAIN, 15));
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        add(scrollPane, BorderLayout.CENTER);
        // ���ô��ھ�����ʾ
        setLocationRelativeTo(null);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        chooseFileButton = new JButton("Choose Text File");
        buildGraphButton = new JButton("Build Graph");
        queryButton = new JButton("Query BridgeWords");
        insertButton = new JButton("Insert BridgeWords");
        shortestPathButton = new JButton("Calculate Shortest Path");
        randomPathButton = new JButton("Random Path");
        EXITBUTTON=new JButton("exit!");

        word1Field = new JTextField(10);
        word2Field = new JTextField(10);

        controlPanel.add(chooseFileButton);
        controlPanel.add(buildGraphButton);
        controlPanel.add(queryButton);
        controlPanel.add(insertButton);
        controlPanel.add(shortestPathButton);
        controlPanel.add(randomPathButton);
        controlPanel.add(EXITBUTTON);
        add(controlPanel, BorderLayout.SOUTH);

        //TextToGraph textToGraph = new TextToGraph();

        // ��Ӽ���
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
                // ��ʾ�û�����Ҫ��ѯ�Ĵ�
                String word1 = JOptionPane.showInputDialog(TextToGraph1.this, "Enter the first word:");
                String word2 = JOptionPane.showInputDialog(TextToGraph1.this, "Enter the second word:");

                // ����û�δ�����κ����ݣ��򲻽��в�ѯ
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
                // ִ���ŽӴʲ�ѯ
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
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ��ʾ�û������ı�
                String newText = JOptionPane.showInputDialog(TextToGraph1.this, "Enter a new line of text:");

                // ����û�δ�����κ����ݣ��򲻽��в���
                if (newText == null || newText.isEmpty()) {
                    textArea.append("No text entered. Bridge word insertion canceled.\n");
                    return;
                }

                // ���ɴ����ŽӴʵ����ı�
                String newTextWithBridgeWords = insertBridgeWords(newText);
                textArea.append("your text:" + newText + "\n");
                textArea.append("new text:" + newTextWithBridgeWords + "\n");
            }
        });

        shortestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word1 = JOptionPane.showInputDialog(TextToGraph1.this, "Enter the first word:").toLowerCase();
                String word2 = JOptionPane.showInputDialog(TextToGraph1.this, "Enter the second word(or not):").toLowerCase();
                String dotFilePath = "./result/marked_graph_all.dot"; // ��ע��� DOT �ļ�·��
                String pngFilePath = "./result/marked_graph_all.png";
                if(word2.isEmpty() && textToGraph.containsKey(word1)){
                    textArea.append(shortestPathsFromSingleWord(word1));
                } else if (textToGraph.containsKey(word1) && textToGraph.containsKey(word2)) {
                    Map<List<String>, Integer>  shortestPath = calcShortestPath(word1, word2);
                    if (!shortestPath.isEmpty()) {
                        textArea.append("Shortest path from " + word1 + " to " + word2 + ": " +
                                WordListFormatter(shortestPath,null) + "\n");
                        convertDotFile(dotFilePath,shortestPath);
                        convertDotToImage(dotFilePath,pngFilePath);
                        displayImage(pngFilePath);
                        if (dotFilePath != null) {
                            textArea.append("Shortest path image generated: " + dotFilePath + "\n");
                        } else {
                            textArea.append("Failed to generate shortest path image.\n");
                        }
                    } else {
                        textArea.append("No path found between " + word1 + " and " + word2 + "\n");
                    }
                }else {
                    textArea.append("Invalid input.\n");
                }


            }
        });

        randomPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dotRandFilePath = "./result/marked_rand_graph.dot"; // ��ע��� DOT �ļ�·��
                List<String> randomPath = randomTraversalForGUI(textArea);
                dotRandFilePath = markAndDisplayShortestPath(randomPath,dotRandFilePath);
//                textToGraph.displayImage(dotRandFilePath);
                textArea.append(WordListFormatter(null, randomPath) + "\n");
                textArea.append("Random traversal completed. Results written to './result/random_traversal.txt'.\n");
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
    //��ӱ�
    public static void addEdge(String source, String destination) {
        textToGraph.get(source).merge(destination, 1, Integer::sum);
    }
    //����һ����������ͼ
    public static void buildDirectedGraph(String filePath) throws IOException {
        textToGraph.clear();
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(" "); // ��ÿ��������ӵ� content �У�����ӿո���Ϊ���ʼ�ķָ���
            }
        }
        // ������ĸ�ַ��滻Ϊ�ո񣬲�ת��ΪСд
        String processedContent = content.toString().replaceAll("[^a-zA-Z\\n\\r]", " ").toLowerCase();
        String[] words = processedContent.split("\\s+"); // ���ո�ָ��
        for (int i = 0; i < words.length - 1; i++) {
            String currentWord = words[i];
            String nextWord = words[i + 1];
            addNode(currentWord);
            addNode(nextWord);
            addEdge(currentWord, nextWord);
        }
    }

    //��ӡ����ͼ
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
    //����2������ͼ���ӻ�
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

            // Ϊÿ�����·��ѡ����ɫ
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
                            // �����ʼ�ڵ����ֹ�ڵ������·�������У����޸���ɫ
                            List<String> path=entry.getKey();
                            if (path.contains(fromNode) && path.contains(toNode)
                                    && !fromNode.equals(path.get(path.size() - 1))
                                    && (path.indexOf(toNode) - path.indexOf(fromNode) == 1)) {
                                if (parts[1].trim().endsWith(";")) {
                                    // ����ǣ�ɾ�����һ���ַ�';'
                                    parts[1] = parts[1].trim().substring(0, parts[1].trim().length() - 1);
                                }
                                String color = pathColors.get(path);
                                line = "\t" + parts[0].trim() + " -> " + parts[1] + " [color=" + color + "];";
                                break; // ֻ��ҪΪͬһ·���ڵı�ѡ��һ����ɫ
                            }
                        }
                    }
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //���������ŽӴʲ�ѯ
    public List<String> queryBridgeWords(String start, String end, Boolean print) {
        List<String> bridgeWords = new ArrayList<>();
        if (!textToGraph.containsKey(start) && !textToGraph.containsKey(end)&&print)
        {
            return bridgeWords;
        }
        if (!textToGraph.containsKey(start) && print) {
            return bridgeWords;
        }
        if (!textToGraph.containsKey(end) && print) {
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
        return bridgeWords;
    }
    //����4������bridge word�������ı�
    public String insertBridgeWords(String text) {
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+"); // ���ո�ָ��
        for (int i = 0; i < words.length - 1; i++) {
            String currentWord = words[i];
            String nextWord = words[i + 1];
            result.append(currentWord).append(" ");
            if (textToGraph.containsKey(currentWord) && textToGraph.containsKey(nextWord)) {
                List<String> bridgeWords = queryBridgeWords(currentWord, nextWord, false);
                if (!bridgeWords.isEmpty()) {
                    // ��������ŽӴʣ������ѡ��һ���ŽӴʲ���
                    Random random = new Random();
                    String selectedBridgeWord = new ArrayList<>(bridgeWords).get(random.nextInt(bridgeWords.size()));
                    result.append(selectedBridgeWord).append(" ");
                }
            }
        }
        result.append(words[words.length - 1]); // ������һ������
        return result.toString();
    }
    //����5�����·��
    public static Map<List<String>, Integer> calcShortestPath(String start, String end) {
        Map<List<String>, Integer> allPaths = findAll(start, end, textToGraph);
        // �ҵ����·���ĳ���
        int shortestLength = Integer.MAX_VALUE;
        for (int length : allPaths.values()) {
            if (length < shortestLength) {
                shortestLength = length;
            }
        }

        // ɸѡ���������·��
        Map<List<String>, Integer> shortestPaths = new HashMap<>();
        for (Map.Entry<List<String>, Integer> entry : allPaths.entrySet()) {
            List<String> path = entry.getKey();
            int length = entry.getValue();
            if (length == shortestLength) {
                shortestPaths.put(path,length);
            }
        }

        return shortestPaths;
    }
    public static Map<List<String>, Integer> findAll(String start, String end, Map<String, Map<String, Integer>> textToGraph) {
        Map<List<String>, Integer> allPathsWithLength = new HashMap<>();
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        List<String> initialPath = new ArrayList<>();
        initialPath.add(start);
        queue.add(initialPath);
        int weight=0;
        //allPathsWithLength.put(new ArrayList<>(), weight);
        while (!queue.isEmpty()) {

            List<String> currentPath = queue.poll();
            String current = currentPath.get(currentPath.size() - 1);

            if (current.equals(end)) {
                weight=calcWeight(currentPath,textToGraph);
                allPathsWithLength.put(new ArrayList<>(currentPath), weight);
                continue;
            }

            Map<String, Integer> neighbors = textToGraph.getOrDefault(current, Collections.emptyMap());
            for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                String next = neighbor.getKey();
                if(!visited.contains(next))
                {
                    List<String> newPath = new ArrayList<>(currentPath);
                    newPath.add(next);
                    queue.add(newPath);
                }
            }
            visited.add(current);

        }

        return allPathsWithLength;
    }

    private static int calcWeight(List<String> path, Map<String, Map<String, Integer>> textToGraph) {
        int weight = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String currentNode = path.get(i);
            String nextNode = path.get(i + 1);
            Map<String, Integer> neighbors = textToGraph.getOrDefault(currentNode, Collections.emptyMap());
            weight += neighbors.getOrDefault(nextNode, 0);
        }
        return weight;
    }

    public static String markAndDisplayShortestPath(List<String> shortestPath,String markedDotFilePath) {
//        String dotFilePath = "./result/marked_graph.dot"; // ��ע��� DOT �ļ�·��
        try {
            // д���ע��� DOT �ļ�
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(markedDotFilePath))) {
                bw.write("digraph G {\n");
                try (BufferedReader br = new BufferedReader(new FileReader("./result/directed_graph.dot"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.contains("->")) {
                            String[] parts = line.split("->");
                            String fromNode = parts[0].trim();
                            String toNode = parts[1].trim().split("\\[")[0].trim();
                            // �����ǰ�������·���ϵ�һ���֣����޸ļ�ͷ��ɫΪ��ɫ
                            if (shortestPath.contains(fromNode) && shortestPath.contains(toNode) ) {
                                if (parts[1].trim().endsWith(";")) {
                                    // ����ǣ�ɾ�����һ���ַ�';'
                                    parts[1] = parts[1].trim().substring(0, parts[1].trim().length() - 1);
                                }
                                bw.write("\t"+parts[0].trim() + " -> " + parts[1] + " [color=red];");
                                bw.newLine();
                            } else {
                                bw.write(line);
                                bw.newLine();
                            }
                        }
                    }
                    bw.write("}"); // ��������ͼ
                }
            }

            // ���� Graphviz ���� PNG �ļ�
            String pngName = markedDotFilePath.substring(0, markedDotFilePath.lastIndexOf(".")) + ".png";
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", markedDotFilePath, "-o", pngName);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Image file generated: " + pngName);
                // ����Ļ����ʾͼ��
                displayImage(pngName);
            } else {
                System.out.println("Failed to generate image file.");
            }

            return markedDotFilePath; // ���ر�ע��� DOT �ļ�·��
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String shortestPathsFromSingleWord(String word) {
        StringBuilder sb = new StringBuilder();
        for (String node : textToGraph.keySet()) {
            if (!node.equals(word)) {
                Map<List<String>, Integer> shortestPath = calcShortestPath(word, node);
                if(shortestPath.isEmpty()){
                    sb.append("no path from " + word + " to " + node + ".\n");
                }else {
                    sb.append("Shortest path from " + word + " to " + node + ": " + WordListFormatter(shortestPath,null) + "\n");
                }
//                System.out.println("Shortest path from " + word + " to " + node + ": " + WordListFormatter(shortestPath));
            }
        }
        return sb.toString();
    }

    public static String  WordListFormatter (Map<List<String>, Integer> wordList,List<String> rand){
        StringBuilder formattedString = new StringBuilder();
        if(wordList!=null)
        {
            for (Map.Entry<List<String>, Integer> entry : wordList.entrySet())
            {
                List<String> node=entry.getKey();
                for(int i=0;i< node.size();i++)
                {
                    formattedString.append(node.get(i));
                    if(i<node.size()-1)
                    {
                        formattedString.append("��");
                    }

                }
                formattedString.append("\n");
                formattedString.append("the shortest path:"+entry.getValue()+"\n");
            }
        }else {
            for (int i = 0; i < rand.size(); i++) {
                formattedString.append(rand.get(i));
                if (wordList!=null &&i < wordList.size() - 1) {
                    formattedString.append(" �� ");
                }
                formattedString.append("\n");
            }
        }


        return formattedString.toString();
    }
    // ����Ļ����ʾͼ��
    public static void displayImage(String imagePath) {
//        // ��ȡ PNG ͼ���ļ�
//        ImageIcon icon = new ImageIcon(imagePath);
//
//        // ������ǩ��������ʾͼ��
//        JLabel label = new JLabel(icon);
//
//        // ��������
//        JFrame frame = new JFrame();
//        frame.setTitle("PNG Image Viewer");
//        frame.setSize(600, 600);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        // ��ӱ�ǩ������
//        frame.getContentPane().add(label, BorderLayout.CENTER);
//
//        // ���ô��ڿɼ�
//        frame.setVisible(true);

        // ������ǩ��������ʾͼ��
        JLabel label = new JLabel();
        // ���ñ�ǩ�Ķ��뷽ʽΪ����
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);


        // ��ȡ PNG ͼ���ļ������±�ǩ��ͼ��
        updateImageIcon(label, imagePath);

        // ��������
        JFrame frame = new JFrame();
        frame.setTitle("PNG Image Viewer");
        frame.setSize(1000, 1000);
        // ���ô��ڹر�ʱ���˳�����
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // ʹ��BorderLayout���ֹ�������������ǩ��ӵ�����λ��
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(label, BorderLayout.CENTER);

        // �������ڴ�С����Ӧͼ���С
        frame.pack();

        // ���ô��ڿɼ�
        frame.setVisible(true);

        // ʹ��������Ļ�м���ʾ
        frame.setLocationRelativeTo(null);

        // ��Ӵ��ڹرռ��������Ա��ڴ��ڹر�ʱ�ͷ���Դ
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // ��������������ͷ���Դ�Ĵ���
                // �ͷ�ͼ����Դ
                Image image = label.getIcon() != null ? ((ImageIcon)label.getIcon()).getImage() : null;
                if (image != null) {
                    image.flush();
                }

                // ����ѡ�����������ñ�ǩ��ͼ��Ϊnull���԰����������������ն���
                label.setIcon(null);
            }
        });

        // ǿ��ˢ�´���
        frame.revalidate();
        frame.repaint();
    }

    private static void updateImageIcon(JLabel label, String imagePath) {
        // ����ļ��Ƿ���ڣ�����������򷵻�
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            System.err.println("Image file does not exist: " + imagePath);
            return;
        }

        // �����µ� ImageIcon �������õ���ǩ��
        ImageIcon icon = new ImageIcon(imagePath);
        label.setIcon(icon);
    }

    //����6������·��
    public static void randomTraversal() {
        try {
            // �����ļ�д����
            PrintWriter writer = new PrintWriter(new FileWriter("./result/random_traversal.txt"));

            Random random = new Random();
            List<String> nodesVisited = new ArrayList<>(); // ��¼�����Ľڵ�
            Set<String> edgesVisited = new HashSet<>(); // ��¼�����ı�

            // ���ѡ����ʼ�ڵ�
            List<String> nodes = new ArrayList<>(textToGraph.keySet());
            String currentNode = nodes.get(random.nextInt(nodes.size()));
            nodesVisited.add(currentNode);

            // ��ʼ�������
            while (true) {
                // ����û��Ƿ�ϣ��ֹͣ����
                if (shouldStopTraversal()) {
                    break;
                }

                // ��ȡ��ǰ�ڵ�ĳ���
                Map<String, Integer> edges = textToGraph.get(currentNode);
                if (edges == null || edges.isEmpty()) {
                    break; // ��ǰ�ڵ�û�г��ߣ���������
                }

                // ���ѡ����һ���ڵ�
                List<String> nextNodes = new ArrayList<>(edges.keySet());
                String nextNode = nextNodes.get(random.nextInt(nextNodes.size()));

                // ��¼�����ı�
                String edge = currentNode + " -> " + nextNode;
                if (edgesVisited.contains(edge)) {
                    break; // �����ظ��ıߣ���������
                }
                edgesVisited.add(edge);

                // ��¼�����Ľڵ�
                nodesVisited.add(nextNode);

                // ���µ�ǰ�ڵ�
                currentNode = nextNode;
            }

            // �������Ľڵ�д���ļ�
            for (String node : nodesVisited) {
                writer.print(node+" ");
            }
            writer.println();
            // �ر��ļ�д����
            writer.close();

            // ��ʾ�û���������ɲ��ļ�������
            System.out.println("Random traversal completed. Results written to './result/random_traversal.txt'.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ����û��Ƿ�ϣ��ֹͣ�������
    private static boolean shouldStopTraversal() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Press 'q' to stop random traversal, or press any other key to continue: ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("q");
    }

    public static List<String> randomTraversalForGUI(JTextArea textArea) {
        List<String> nodesVisited = new ArrayList<>(); // ��¼�����Ľڵ�
        try {
            // �����ļ�д����
            PrintWriter writer = new PrintWriter(new FileWriter("./result/random_traversal.txt"));
            Random random = new Random();

            List<String> nodes = new ArrayList<>(textToGraph.keySet());

//            String currentNode = nodes.get(random.nextInt(nodes.size()));
            // first node ���ѡ����ʼ�ڵ�
            final String[] currentNode = {nodes.get(random.nextInt(nodes.size()))}; // ����Ϊ final ������(final String���У�
            nodesVisited.add(currentNode[0]);


            // begin
            while (true) {
                // �ڿ���̨�����ǰ����
                System.out.println("Current node: " + currentNode[0]);

                // �� UI ����ʾ��ǰ����
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        textArea.append("Current node: " + currentNode[0] + "\n");
                        textArea.setCaretPosition(textArea.getDocument().getLength()); // ���ı�������������һ��
                    }
                });
                // continue?
                int option = JOptionPane.showConfirmDialog(null, "Continue traversal?", "Continue", JOptionPane.YES_NO_OPTION);
                if (option != JOptionPane.YES_OPTION) {
//                    System.out.println("3");
                    textArea.append("quit!\n");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                    break;
                }

                // ��ȡ��ǰ�ڵ�ĳ���
                Map<String, Integer> edges = textToGraph.get(currentNode[0]);
                if (edges == null || edges.isEmpty()) {
//                    System.out.println("1");
                    textArea.append("no edge!\n");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                    break; // ��ǰ�ڵ�û�г��ߣ���������
                }

                // ���ѡ����һ���ڵ�
                List<String> nextNodes = new ArrayList<>(edges.keySet());
                String nextNode = nextNodes.get(random.nextInt(nextNodes.size()));

//                String edge = currentNode[0] + " -> " + nextNode;
                // �����ظ��Ķ��㣬��������
                if (nodesVisited.contains(nextNode)) {
                    // ����ظ��ڵ�
                    nodesVisited.add(nextNode);
//                    System.out.println("2");
                    textArea.append("Current node: " + nextNode + "\n");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                    textArea.append("node repeat!\n");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                    break;
                }

                // ��¼�����Ľڵ�
                nodesVisited.add(nextNode);

                // ���µ�ǰ�ڵ�
                currentNode[0] = nextNode;


            }

            // �������Ľڵ�д���ļ�
            for (String node : nodesVisited) {
                writer.print(node+" ");
            }
            writer.println();
            // �ر��ļ�д����
            writer.close();

            // ��ʾ�û���������ɲ��ļ�������
//            System.out.println(nodesVisited);
//            System.out.println("Random traversal completed. Results written to './result/random_traversal.txt'.");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return WordListFormatter(nodesVisited);
        return nodesVisited;
    }


}
