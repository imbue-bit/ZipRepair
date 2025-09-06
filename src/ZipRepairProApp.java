import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ZipRepairProApp extends JFrame {

    private final JTextField sourceField;
    private final JTextField targetField;
    private final JLabel statusLabel;
    private final JButton repairButton;

    private final CardLayout cardLayout;
    private final JPanel rightPanel;
    private static final String SINGLE_REPAIR_PANEL = "SingleRepairPanel";
    private static final String BATCH_REPAIR_PANEL = "BatchRepairPanel";


    public ZipRepairProApp() {
        setTitle("ZipRepair Pro for macOS");
        setSize(620, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中
        setResizable(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    this,
                    "This program is designed for macOS.\n" +
                            "It may not function correctly on other operating systems.",
                    "Compatibility Warning",
                    JOptionPane.WARNING_MESSAGE
            ));
        }

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About ZipRepair Pro");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);

        JPanel singleRepairOuterPanel = new JPanel(new BorderLayout());
        JPanel singleRepairPanel = new JPanel(new GridBagLayout());
        singleRepairPanel.setBorder(BorderFactory.createTitledBorder("Repair Zip File"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        singleRepairPanel.add(new JLabel("Select file to repair:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        sourceField = new JTextField(30);
        singleRepairPanel.add(sourceField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.0;
        JButton browseSourceButton = new JButton("Browse");
        browseSourceButton.addActionListener(this::browseSourceFile);
        singleRepairPanel.add(browseSourceButton, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        singleRepairPanel.add(new JLabel("Save repaired file as:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        targetField = new JTextField(30);
        singleRepairPanel.add(targetField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.0;
        JButton browseTargetButton = new JButton("Browse");
        browseTargetButton.addActionListener(this::browseTargetFile);
        singleRepairPanel.add(browseTargetButton, gbc);
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        statusLabel = new JLabel("Ready.");
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        repairButton = new JButton("Repair");
        repairButton.addActionListener(this::startRepair);
        bottomPanel.add(repairButton, BorderLayout.EAST);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; gbc.insets = new Insets(15, 5, 5, 5);
        singleRepairPanel.add(bottomPanel, gbc);
        singleRepairOuterPanel.add(singleRepairPanel, BorderLayout.NORTH);

        BatchRepairPanel batchRepairPanel = new BatchRepairPanel();

        // 将两个面板添加到CardLayout中
        rightPanel.add(singleRepairOuterPanel, SINGLE_REPAIR_PANEL);
        rightPanel.add(batchRepairPanel, BATCH_REPAIR_PANEL);


        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(180, 0));

        JPanel repairSection = createSectionPanel("Repair");
        JButton singleRepairButton = new JButton("Repair Zip File");
        singleRepairButton.addActionListener(e -> cardLayout.show(rightPanel, SINGLE_REPAIR_PANEL));
        repairSection.add(singleRepairButton);

        JButton batchRepairButton = new JButton("Batch Zip Repair");
        batchRepairButton.addActionListener(e -> cardLayout.show(rightPanel, BATCH_REPAIR_PANEL));
        repairSection.add(batchRepairButton);

        JPanel optionsSection = createSectionPanel("Options");
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        optionsSection.add(exitButton);

        JPanel infoSection = createSectionPanel("Info");
        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(e -> showHelpDialog());
        infoSection.add(helpButton);
        JButton aboutButton = new JButton("About");
        aboutButton.addActionListener(e -> showAboutDialog());
        infoSection.add(aboutButton);

        panel.add(repairSection);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(optionsSection);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(infoSection);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title, TitledBorder.LEFT, TitledBorder.TOP));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        for(Component comp : panel.getComponents()) {
            ((JComponent)comp).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return panel;
    }

    private void browseSourceFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a broken Zip file");
        chooser.setFileFilter(new FileNameExtensionFilter("ZIP Archives", "zip"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            sourceField.setText(file.getAbsolutePath());
            if (targetField.getText().isEmpty()) {
                String sourcePath = file.getAbsolutePath();
                String targetPath = sourcePath.substring(0, sourcePath.lastIndexOf('.')) + "_fixed.zip";
                targetField.setText(targetPath);
            }
        }
    }

    private void browseTargetFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save repaired Zip file as");
        chooser.setFileFilter(new FileNameExtensionFilter("ZIP Archives", "zip"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".zip")) {
                path += ".zip";
            }
            targetField.setText(path);
        }
    }

    private void startRepair(ActionEvent e) {
        String sourcePath = sourceField.getText();
        String targetPath = targetField.getText();

        if (sourcePath.isEmpty() || targetPath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please specify both source and target file paths.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        repairButton.setEnabled(false);
        statusLabel.setText("Repairing, please wait...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return executeRepairCommand(sourcePath, targetPath);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    statusLabel.setText("Repair completed successfully.");
                    JOptionPane.showMessageDialog(ZipRepairProApp.this,
                            "File has been repaired!\n\nDetails:\n" + result,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    statusLabel.setText("Repair failed.");
                    JOptionPane.showMessageDialog(ZipRepairProApp.this,
                            "Could not repair the file.\n\nDetails:\n" + ex.getMessage(),
                            "Repair Failed", JOptionPane.ERROR_MESSAGE);
                } finally {
                    repairButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    // 将命令行执行逻辑提取到一个公共方法中
    public static String executeRepairCommand(String sourcePath, String targetPath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("zip", "-FF", sourcePath, "--out", targetPath);
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            return "Success:\n" + (output.toString().trim().isEmpty() ? "Command executed successfully for " + new File(sourcePath).getName() : output.toString());
        } else {
            throw new IOException("Process failed with exit code " + exitCode + " for " + new File(sourcePath).getName() + ".\n" + output);
        }
    }

    private void showHelpDialog() {
        JOptionPane.showMessageDialog(this,
                "Single File Repair:\n" +
                        "1. Click 'Browse' to select the corrupted ZIP file.\n" +
                        "2. Choose a name and location for the repaired file.\n" +
                        "3. Click the 'Repair' button.\n\n" +
                        "Batch Repair:\n" +
                        "1. Select a source folder containing your ZIP files.\n" +
                        "2. Select a target folder to save the repaired files.\n" +
                        "3. Click 'Start Batch Repair'.",
                "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "About ZipRepair Pro", true);
        aboutDialog.setSize(380, 220);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("ZipRepair Pro for macOS");
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("Version: 1.1 (Batch Mode Enabled)");
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("A utility for fixing corrupted Zip archives.");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel linkLabel = new JLabel("<html><a href=''>https://github.com/imbue-bit/ziprepair</a></html>");
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/imbue-bit/ziprepair"));
                } catch (IOException | URISyntaxException e) {
                    System.err.println("Failed to open link: " + e.getMessage());
                }
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> aboutDialog.dispose());

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(versionLabel);
        panel.add(descLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(linkLabel);
        panel.add(Box.createVerticalGlue());
        panel.add(closeButton);

        aboutDialog.add(panel);
        aboutDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ZipRepairProApp app = new ZipRepairProApp();
            app.setVisible(true);
        });
    }

    // --- 内部类：批量修复面板 ---
    static class BatchRepairPanel extends JPanel {
        private final JTextField sourceDirField = new JTextField(30);
        private final JTextField targetDirField = new JTextField(30);
        private final JButton browseSourceDirButton = new JButton("Browse");
        private final JButton browseTargetDirButton = new JButton("Browse");
        private final JButton startBatchButton = new JButton("Start Batch Repair");
        private final JList<String> fileList = new JList<>();
        private final JTextArea logArea = new JTextArea();
        private final JProgressBar progressBar = new JProgressBar();
        private final JLabel progressLabel = new JLabel("Ready for batch processing.");

        BatchRepairPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createTitledBorder("Batch Zip Repair"));

            // --- Top Panel for Directory Selection ---
            JPanel topPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            topPanel.add(new JLabel("Source folder:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            topPanel.add(sourceDirField, gbc);
            gbc.gridx = 2; gbc.weightx = 0;
            topPanel.add(browseSourceDirButton, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            topPanel.add(new JLabel("Target folder:"), gbc);
            gbc.gridx = 1;
            topPanel.add(targetDirField, gbc);
            gbc.gridx = 2;
            topPanel.add(browseTargetDirButton, gbc);

            // --- Center Panel for File List and Log ---
            JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
            JScrollPane fileListScrollPane = new JScrollPane(fileList);
            fileListScrollPane.setBorder(BorderFactory.createTitledBorder("Files to Process"));
            centerPanel.add(fileListScrollPane);

            logArea.setEditable(false);
            logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            JScrollPane logScrollPane = new JScrollPane(logArea);
            logScrollPane.setBorder(BorderFactory.createTitledBorder("Log"));
            centerPanel.add(logScrollPane);

            // --- Bottom Panel for Progress and Start Button ---
            JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
            JPanel progressPanel = new JPanel(new BorderLayout(5, 0));
            progressPanel.add(progressLabel, BorderLayout.NORTH);
            progressPanel.add(progressBar, BorderLayout.CENTER);
            bottomPanel.add(progressPanel, BorderLayout.CENTER);
            bottomPanel.add(startBatchButton, BorderLayout.EAST);

            // --- Add panels to the main layout ---
            add(topPanel, BorderLayout.NORTH);
            add(centerPanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            // --- Add Action Listeners ---
            browseSourceDirButton.addActionListener(this::selectSourceDirectory);
            browseTargetDirButton.addActionListener(this::selectTargetDirectory);
            startBatchButton.addActionListener(this::startBatchRepair);
        }

        private void selectSourceDirectory(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Folder with Zip Files");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File dir = chooser.getSelectedFile();
                sourceDirField.setText(dir.getAbsolutePath());
                if (targetDirField.getText().isEmpty()) {
                    targetDirField.setText(new File(dir, "repaired_files").getAbsolutePath());
                }
                loadZipFiles(dir);
            }
        }

        private void selectTargetDirectory(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Folder to Save Repaired Files");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                targetDirField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        private void loadZipFiles(File directory) {
            DefaultListModel<String> model = new DefaultListModel<>();
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));
            if (files != null) {
                for (File file : files) {
                    model.addElement(file.getName());
                }
            }
            fileList.setModel(model);
            progressLabel.setText(model.getSize() + " zip file(s) found.");
        }

        private void setAllControlsEnabled(boolean enabled) {
            sourceDirField.setEnabled(enabled);
            targetDirField.setEnabled(enabled);
            browseSourceDirButton.setEnabled(enabled);
            browseTargetDirButton.setEnabled(enabled);
            startBatchButton.setEnabled(enabled);
            fileList.setEnabled(enabled);
        }

        private void startBatchRepair(ActionEvent e) {
            String sourceDirPath = sourceDirField.getText();
            String targetDirPath = targetDirField.getText();

            if (sourceDirPath.isEmpty() || targetDirPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select both source and target directories.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File sourceDir = new File(sourceDirPath);
            File targetDir = new File(targetDirPath);

            if (!sourceDir.isDirectory()) {
                JOptionPane.showMessageDialog(this, "Source path is not a valid directory.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!targetDir.exists()) {
                if (!targetDir.mkdirs()) {
                    JOptionPane.showMessageDialog(this, "Could not create the target directory.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            ListModel<String> model = fileList.getModel();
            if (model.getSize() == 0) {
                JOptionPane.showMessageDialog(this, "No zip files found in the source directory.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 使用 SwingWorker 执行批量任务
            BatchRepairWorker worker = new BatchRepairWorker(sourceDir, targetDir, model);
            worker.execute();
        }

        // 内部类：处理批量修复的SwingWorker
        private class BatchRepairWorker extends SwingWorker<Void, String> {
            private final File sourceDir;
            private final File targetDir;
            private final ListModel<String> fileListModel;
            private int successCount = 0;
            private int failCount = 0;

            BatchRepairWorker(File sourceDir, File targetDir, ListModel<String> model) {
                this.sourceDir = sourceDir;
                this.targetDir = targetDir;
                this.fileListModel = model;

                // 绑定进度条更新
                addPropertyChangeListener(evt -> {
                    if ("progress".equals(evt.getPropertyName())) {
                        progressBar.setValue((Integer) evt.getNewValue());
                    }
                });
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    logArea.append(message + "\n");
                }
            }

            @Override
            protected Void doInBackground() throws Exception {
                // 任务开始前禁用控件并清空日志
                SwingUtilities.invokeLater(() -> {
                    setAllControlsEnabled(false);
                    logArea.setText("");
                    progressBar.setValue(0);
                });

                int totalFiles = fileListModel.getSize();
                for (int i = 0; i < totalFiles; i++) {
                    // 如果任务被取消，则退出循环
                    if (isCancelled()) {
                        break;
                    }

                    String fileName = fileListModel.getElementAt(i);
                    File sourceFile = new File(sourceDir, fileName);

                    String baseName = fileName.lastIndexOf('.') > 0 ?
                            fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                    File targetFile = new File(targetDir, baseName + "_fixed.zip");

                    final int currentFileNum = i + 1;
                    SwingUtilities.invokeLater(() -> progressLabel.setText("Processing " + currentFileNum + "/" + totalFiles + ": " + fileName));

                    publish("----------------------------------------");
                    publish("Processing: " + fileName);

                    try {
                        String result = ZipRepairProApp.executeRepairCommand(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath());
                        publish("Status: SUCCESS");
                        publish("Output: " + result.replace("Success:\n", "").trim());
                        successCount++;
                    } catch (IOException | InterruptedException ex) {
                        publish("Status: FAILED");
                        publish("Error: " + ex.getMessage().trim());
                        failCount++;
                    }

                    int progress = (int) Math.round(((double) (i + 1) / totalFiles) * 100);
                    setProgress(progress);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // 检查是否有未捕获的异常
                    progressLabel.setText("Batch processing complete.");
                    logArea.append("========================================\n");
                    logArea.append("Finished. Success: " + successCount + ", Failed: " + failCount + "\n");
                    JOptionPane.showMessageDialog(BatchRepairPanel.this,
                            "Batch repair finished!\n\n" +
                                    "Successfully repaired: " + successCount + "\n" +
                                    "Failed to repair: " + failCount,
                            "Batch Complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException e) {
                    // 处理在 doInBackground 中抛出的异常
                    progressLabel.setText("An unexpected error occurred.");
                    logArea.append("FATAL ERROR: " + e.getCause().getMessage() + "\n");
                    JOptionPane.showMessageDialog(BatchRepairPanel.this,
                            "An unexpected error occurred during batch processing: \n" + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // 任务结束后，无论成功与否，都重新启用控件
                    setAllControlsEnabled(true);
                    progressBar.setValue(100); // 确保进度条填满
                }
            }
        }
    }
}
