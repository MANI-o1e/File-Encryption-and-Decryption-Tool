package filecrypt.ui;

import filecrypt.algo.CaesarCipher;
import filecrypt.algo.Cipher;
import filecrypt.algo.VigenereCipher;
import filecrypt.algo.XorCipher;
import filecrypt.db.DatabaseManager;
import filecrypt.exception.InvalidKeyException;
import filecrypt.model.OperationRecord;
import filecrypt.util.FileHandler;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Main application window for the File Encryption and Decryption Tool.
 * Features a structured layout preventing text overlap, high-contrast readability,
 * and robust file and JDBC operations.
 */
public class MainFrame extends JFrame {

    private final Map<String, Cipher> ciphers = new LinkedHashMap<>();
    private final DatabaseManager dbManager;

    private File currentSelectedFile = null;

    // UI Components
    private JLabel fileNameLabel;
    private JLabel filePathLabel;
    private JLabel fileSizeLabel;
    private JComboBox<String> cipherComboBox;
    private JTextField keyInputField;
    private JLabel keyHintLabel;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JLabel inputCharCountLabel;
    private JLabel outputCharCountLabel;
    private JLabel statusLabel;

    public MainFrame() {
        super("File Encryption & Decryption Tool");
        this.dbManager = DatabaseManager.getInstance();

        initCiphers();
        initUI();
        updateKeyHint();
    }

    private void initCiphers() {
        Cipher caesar = new CaesarCipher();
        Cipher vigenere = new VigenereCipher();
        Cipher xor = new XorCipher();

        ciphers.put(caesar.getAlgorithmName(), caesar);
        ciphers.put(vigenere.getAlgorithmName(), vigenere);
        ciphers.put(xor.getAlgorithmName(), xor);
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 780);
        setMinimumSize(new Dimension(920, 640));
        setLocationRelativeTo(null);

        // Root container uses BorderLayout
        JPanel rootPanel = new JPanel(new BorderLayout(0, 0));
        rootPanel.setBackground(UITheme.BG_APP);
        setContentPane(rootPanel);

        // =====================================================================
        // 1. NORTH: App Header Banner & Two Structured Configuration Cards
        // =====================================================================
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.setBackground(UITheme.BG_APP);

        // Top App Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.BG_HEADER);
        headerPanel.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel titleLabel = new JLabel("File Encryption & Decryption Tool");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_WHITE);

        JLabel subtitleLabel = new JLabel("Secure Text Cryptography • Caesar, Vigenère & XOR Ciphers");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_ACCENT);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(subtitleLabel, BorderLayout.EAST);
        northContainer.add(headerPanel);

        // Outer padding wrapper for cards
        JPanel cardsWrapper = new JPanel();
        cardsWrapper.setLayout(new BoxLayout(cardsWrapper, BoxLayout.Y_AXIS));
        cardsWrapper.setBackground(UITheme.BG_APP);
        cardsWrapper.setBorder(new EmptyBorder(12, 20, 10, 20));

        // -------------------------------------------------------------
        // CARD 1: File Selection Card (Clean 2-row layout)
        // -------------------------------------------------------------
        JPanel fileCard = new JPanel(new BorderLayout(10, 6));
        fileCard.setBackground(UITheme.BG_PANEL);
        fileCard.setBorder(UITheme.createCardBorder());

        // Row 1 of File Card
        JPanel fileRow1 = new JPanel(new BorderLayout(10, 0));
        fileRow1.setOpaque(false);

        JPanel fileRow1Left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        fileRow1Left.setOpaque(false);

        JButton browseButton = UITheme.createButton("Select File...", UITheme.PRIMARY, UITheme.PRIMARY_HOVER, UITheme.TEXT_WHITE);
        browseButton.setPreferredSize(new Dimension(130, 34));
        browseButton.addActionListener(e -> chooseFile());

        JLabel targetFilePrefix = new JLabel("Target File:");
        targetFilePrefix.setFont(UITheme.FONT_BOLD);
        targetFilePrefix.setForeground(UITheme.TEXT_DARK);

        fileNameLabel = new JLabel("No file selected (Direct text input mode)");
        fileNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fileNameLabel.setForeground(new Color(30, 41, 59));

        fileRow1Left.add(browseButton);
        fileRow1Left.add(targetFilePrefix);
        fileRow1Left.add(fileNameLabel);

        JPanel fileRow1Right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        fileRow1Right.setOpaque(false);

        fileSizeLabel = new JLabel("Size: 0 Bytes");
        fileSizeLabel.setFont(UITheme.FONT_BOLD);
        fileSizeLabel.setForeground(UITheme.TEXT_DARK);

        JButton reloadButton = UITheme.createSecondaryButton("Reload");
        reloadButton.addActionListener(e -> reloadCurrentFile());

        JButton clearFileButton = UITheme.createSecondaryButton("Clear File");
        clearFileButton.addActionListener(e -> clearSelectedFile());

        fileRow1Right.add(fileSizeLabel);
        fileRow1Right.add(reloadButton);
        fileRow1Right.add(clearFileButton);

        fileRow1.add(fileRow1Left, BorderLayout.WEST);
        fileRow1.add(fileRow1Right, BorderLayout.EAST);
        fileCard.add(fileRow1, BorderLayout.NORTH);

        // Row 2 of File Card: File Path details
        filePathLabel = new JLabel(" ");
        filePathLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        filePathLabel.setForeground(new Color(71, 85, 105));
        filePathLabel.setBorder(new EmptyBorder(2, 6, 0, 0));
        fileCard.add(filePathLabel, BorderLayout.SOUTH);

        cardsWrapper.add(fileCard);
        cardsWrapper.add(Box.createVerticalStrut(10));

        // -------------------------------------------------------------
        // CARD 2: Cipher & Key Configuration Card
        // Uses a clean 2-row layout so the Key Hint is placed on its OWN LINE
        // and NEVER overlaps or collides with the Encrypt/Decrypt buttons!
        // -------------------------------------------------------------
        JPanel cipherCard = new JPanel(new BorderLayout(10, 8));
        cipherCard.setBackground(UITheme.BG_PANEL);
        cipherCard.setBorder(UITheme.createCardBorder());

        // Top Row: Controls & Action Buttons
        JPanel cipherTopRow = new JPanel(new BorderLayout(12, 0));
        cipherTopRow.setOpaque(false);

        // Left Controls: Algorithm + Secret Key
        JPanel cipherInputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        cipherInputs.setOpaque(false);

        JLabel cipherLabel = new JLabel("Algorithm:");
        cipherLabel.setFont(UITheme.FONT_BOLD);
        cipherLabel.setForeground(UITheme.TEXT_DARK);

        cipherComboBox = new JComboBox<>(ciphers.keySet().toArray(new String[0]));
        cipherComboBox.setFont(UITheme.FONT_BOLD);
        cipherComboBox.setForeground(UITheme.TEXT_DARK);
        cipherComboBox.setBackground(Color.WHITE);
        cipherComboBox.setPreferredSize(new Dimension(160, 35));
        cipherComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateKeyHint();
            }
        });

        JLabel keyLabel = new JLabel("Secret Key:");
        keyLabel.setFont(UITheme.FONT_BOLD);
        keyLabel.setForeground(UITheme.TEXT_DARK);

        keyInputField = new JTextField(18);
        keyInputField.setFont(new Font("Consolas", Font.BOLD, 14));
        keyInputField.setForeground(Color.BLACK);
        keyInputField.setBackground(Color.WHITE);
        keyInputField.setCaretColor(Color.BLACK);
        keyInputField.setPreferredSize(new Dimension(200, 35));
        keyInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        cipherInputs.add(cipherLabel);
        cipherInputs.add(cipherComboBox);
        cipherInputs.add(Box.createHorizontalStrut(8));
        cipherInputs.add(keyLabel);
        cipherInputs.add(keyInputField);

        // Right Controls: Encrypt and Decrypt Buttons
        JPanel cipherButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cipherButtons.setOpaque(false);

        JButton encryptButton = UITheme.createButton("Encrypt Content", UITheme.SUCCESS, UITheme.SUCCESS_HOVER, UITheme.TEXT_WHITE);
        encryptButton.setPreferredSize(new Dimension(155, 36));
        encryptButton.addActionListener(e -> processCryptography(true));

        JButton decryptButton = UITheme.createButton("Decrypt Content", UITheme.PRIMARY, UITheme.PRIMARY_HOVER, UITheme.TEXT_WHITE);
        decryptButton.setPreferredSize(new Dimension(155, 36));
        decryptButton.addActionListener(e -> processCryptography(false));

        cipherButtons.add(encryptButton);
        cipherButtons.add(decryptButton);

        cipherTopRow.add(cipherInputs, BorderLayout.WEST);
        cipherTopRow.add(cipherButtons, BorderLayout.EAST);
        cipherCard.add(cipherTopRow, BorderLayout.NORTH);

        // Bottom Row: Dedicated Key Requirement Banner (No overlap possible!)
        JPanel hintPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        hintPanel.setOpaque(false);

        JLabel infoIcon = new JLabel("Key Format:");
        infoIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        infoIcon.setForeground(new Color(15, 118, 110)); // Deep Teal

        keyHintLabel = new JLabel();
        keyHintLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        keyHintLabel.setForeground(new Color(15, 118, 110));

        hintPanel.add(infoIcon);
        hintPanel.add(keyHintLabel);
        cipherCard.add(hintPanel, BorderLayout.SOUTH);

        cardsWrapper.add(cipherCard);
        northContainer.add(cardsWrapper);
        rootPanel.add(northContainer, BorderLayout.NORTH);

        // =====================================================================
        // 2. CENTER: Text Presentation Areas with BorderLayout
        // =====================================================================
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        centerPanel.setBackground(UITheme.BG_APP);
        centerPanel.setBorder(new EmptyBorder(0, 20, 10, 20));

        // Left Panel: Source Text Area
        JPanel inputPanel = new JPanel(new BorderLayout(0, 8));
        inputPanel.setBackground(UITheme.BG_PANEL);
        inputPanel.setBorder(UITheme.createPanelBorder("Source / File Preview Text"));

        inputTextArea = new JTextArea();
        inputTextArea.setFont(UITheme.FONT_CODE);
        inputTextArea.setForeground(Color.BLACK);
        inputTextArea.setBackground(Color.WHITE);
        inputTextArea.setCaretColor(Color.BLACK);
        inputTextArea.setSelectionColor(new Color(191, 219, 254));
        inputTextArea.setSelectedTextColor(Color.BLACK);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        inputTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane inputScroll = new JScrollPane(inputTextArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        inputPanel.add(inputScroll, BorderLayout.CENTER);

        JPanel inputFooter = new JPanel(new BorderLayout());
        inputFooter.setOpaque(false);
        inputCharCountLabel = new JLabel("0 characters | 0 lines");
        inputCharCountLabel.setFont(UITheme.FONT_BOLD);
        inputCharCountLabel.setForeground(UITheme.TEXT_MUTED);

        JButton pasteBtn = UITheme.createSecondaryButton("Paste Clipboard");
        pasteBtn.addActionListener(e -> inputTextArea.paste());

        inputFooter.add(inputCharCountLabel, BorderLayout.WEST);
        inputFooter.add(pasteBtn, BorderLayout.EAST);
        inputPanel.add(inputFooter, BorderLayout.SOUTH);

        // Right Panel: Processed Output Area
        JPanel outputPanel = new JPanel(new BorderLayout(0, 8));
        outputPanel.setBackground(UITheme.BG_PANEL);
        outputPanel.setBorder(UITheme.createPanelBorder("Processed Output"));

        outputTextArea = new JTextArea();
        outputTextArea.setFont(UITheme.FONT_CODE);
        outputTextArea.setForeground(Color.BLACK);
        outputTextArea.setBackground(Color.WHITE);
        outputTextArea.setCaretColor(Color.BLACK);
        outputTextArea.setSelectionColor(new Color(191, 219, 254));
        outputTextArea.setSelectedTextColor(Color.BLACK);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane outputScroll = new JScrollPane(outputTextArea);
        outputScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        outputPanel.add(outputScroll, BorderLayout.CENTER);

        JPanel outputFooter = new JPanel(new BorderLayout());
        outputFooter.setOpaque(false);
        outputCharCountLabel = new JLabel("0 characters | 0 lines");
        outputCharCountLabel.setFont(UITheme.FONT_BOLD);
        outputCharCountLabel.setForeground(UITheme.TEXT_MUTED);

        JPanel outputActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        outputActions.setOpaque(false);

        JButton copyBtn = UITheme.createSecondaryButton("Copy Output");
        copyBtn.addActionListener(e -> copyOutputToClipboard());

        JButton useAsInputBtn = UITheme.createSecondaryButton("Transfer to Input ->");
        useAsInputBtn.addActionListener(e -> {
            inputTextArea.setText(outputTextArea.getText());
            setStatus("Output transferred to source text area.");
        });

        outputActions.add(copyBtn);
        outputActions.add(useAsInputBtn);

        outputFooter.add(outputCharCountLabel, BorderLayout.WEST);
        outputFooter.add(outputActions, BorderLayout.EAST);
        outputPanel.add(outputFooter, BorderLayout.SOUTH);

        centerPanel.add(inputPanel);
        centerPanel.add(outputPanel);
        rootPanel.add(centerPanel, BorderLayout.CENTER);

        setupCharCounters();

        // =====================================================================
        // 3. SOUTH: FlowLayout Action Toolbar & Status Bar
        // =====================================================================
        JPanel southContainer = new JPanel();
        southContainer.setLayout(new BoxLayout(southContainer, BoxLayout.Y_AXIS));

        // FlowLayout Action Toolbar
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 10));
        actionToolbar.setBackground(UITheme.BG_APP);

        JButton saveToFileBtn = UITheme.createButton("Save Output to File...", UITheme.SUCCESS, UITheme.SUCCESS_HOVER, UITheme.TEXT_WHITE);
        saveToFileBtn.setPreferredSize(new Dimension(190, 36));
        saveToFileBtn.addActionListener(e -> saveOutputToFile());

        JButton viewHistoryBtn = UITheme.createButton("View Operation History (DB)", UITheme.ACCENT, UITheme.ACCENT_HOVER, UITheme.TEXT_WHITE);
        viewHistoryBtn.setPreferredSize(new Dimension(230, 36));
        viewHistoryBtn.addActionListener(e -> openHistoryDialog());

        JButton clearAllBtn = UITheme.createButton("Reset Workspace", UITheme.DANGER, UITheme.DANGER_HOVER, UITheme.TEXT_WHITE);
        clearAllBtn.setPreferredSize(new Dimension(155, 36));
        clearAllBtn.addActionListener(e -> resetWorkspace());

        actionToolbar.add(saveToFileBtn);
        actionToolbar.add(viewHistoryBtn);
        actionToolbar.add(clearAllBtn);
        southContainer.add(actionToolbar);

        // High-contrast Status Bar at bottom
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(226, 232, 240));
        statusBar.setBorder(new EmptyBorder(8, 22, 8, 22));

        statusLabel = new JLabel("Ready. Select a file or paste text to begin.");
        statusLabel.setFont(UITheme.FONT_STATUS);
        statusLabel.setForeground(UITheme.TEXT_DARK);

        JLabel dbStatusLabel = new JLabel("Database: Connected (Table 'operations')");
        dbStatusLabel.setFont(UITheme.FONT_STATUS);
        dbStatusLabel.setForeground(new Color(4, 120, 87));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(dbStatusLabel, BorderLayout.EAST);
        southContainer.add(statusBar);

        rootPanel.add(southContainer, BorderLayout.SOUTH);
    }

    private void setupCharCounters() {
        DocumentListener inputListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateCounts(); }
            @Override public void removeUpdate(DocumentEvent e) { updateCounts(); }
            @Override public void changedUpdate(DocumentEvent e) { updateCounts(); }

            private void updateCounts() {
                String text = inputTextArea.getText();
                int chars = text.length();
                int lines = text.isEmpty() ? 0 : text.split("\r\n|\r|\n").length;
                inputCharCountLabel.setText(String.format("%,d characters | %,d lines", chars, lines));
            }
        };
        inputTextArea.getDocument().addDocumentListener(inputListener);

        DocumentListener outputListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateCounts(); }
            @Override public void removeUpdate(DocumentEvent e) { updateCounts(); }
            @Override public void changedUpdate(DocumentEvent e) { updateCounts(); }

            private void updateCounts() {
                String text = outputTextArea.getText();
                int chars = text.length();
                int lines = text.isEmpty() ? 0 : text.split("\r\n|\r|\n").length;
                outputCharCountLabel.setText(String.format("%,d characters | %,d lines", chars, lines));
            }
        };
        outputTextArea.getDocument().addDocumentListener(outputListener);
    }

    private void updateKeyHint() {
        String selected = (String) cipherComboBox.getSelectedItem();
        Cipher cipher = ciphers.get(selected);
        if (cipher != null) {
            keyHintLabel.setText(cipher.getKeyRequirementDescription());
        }
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Text File to Encrypt/Decrypt");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt, *.dat, *.log, *.csv)", "txt", "dat", "log", "csv"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            loadFile(file);
        }
    }

    private void loadFile(File file) {
        if (file == null) return;
        try {
            String content = FileHandler.readFile(file);
            currentSelectedFile = file;
            fileNameLabel.setText(file.getName());
            filePathLabel.setText("Path: " + file.getAbsolutePath());
            fileSizeLabel.setText("Size: " + FileHandler.formatFileSize(file.length()));
            inputTextArea.setText(content);
            inputTextArea.setCaretPosition(0);
            setStatus("Loaded file: " + file.getName() + " (" + FileHandler.formatFileSize(file.length()) + ")");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Selected file was not found:\n" + e.getMessage(),
                    "File Not Found Error",
                    JOptionPane.ERROR_MESSAGE);
            setStatus("Error: File not found.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred while reading the file:\n" + e.getMessage(),
                    "File Read Error",
                    JOptionPane.ERROR_MESSAGE);
            setStatus("Error reading file.");
        }
    }

    private void reloadCurrentFile() {
        if (currentSelectedFile != null) {
            loadFile(currentSelectedFile);
        } else {
            JOptionPane.showMessageDialog(this, "No file is currently selected to reload.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearSelectedFile() {
        currentSelectedFile = null;
        fileNameLabel.setText("No file selected (Direct text input mode)");
        filePathLabel.setText(" ");
        fileSizeLabel.setText("Size: 0 Bytes");
        inputTextArea.setText("");
        outputTextArea.setText("");
        setStatus("Workspace cleared.");
    }

    private void processCryptography(boolean isEncrypt) {
        String inputText = inputTextArea.getText();
        if (inputText == null || inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please provide input text or select a non-empty file first.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cipherName = (String) cipherComboBox.getSelectedItem();
        Cipher cipher = ciphers.get(cipherName);
        if (cipher == null) {
            JOptionPane.showMessageDialog(this, "Unknown cipher selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String key = keyInputField.getText();
        String mode = isEncrypt ? "ENCRYPT" : "DECRYPT";
        String fileName = (currentSelectedFile != null) ? currentSelectedFile.getName() : "DirectInput.txt";
        String fileSizeStr = FileHandler.formatFileSize(inputText.getBytes().length);

        try {
            cipher.validateKey(key);

            String resultText;
            if (isEncrypt) {
                resultText = cipher.encrypt(inputText, key);
            } else {
                resultText = cipher.decrypt(inputText, key);
            }

            outputTextArea.setText(resultText);
            outputTextArea.setCaretPosition(0);

            OperationRecord record = new OperationRecord(
                    0,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    fileName,
                    cipherName,
                    mode,
                    key,
                    fileSizeStr,
                    "SUCCESS"
            );
            dbManager.logOperation(record);

            setStatus(String.format("Successfully performed %s using %s (%s characters). Logged to DB.",
                    mode, cipherName, resultText.length()));

            JOptionPane.showMessageDialog(this,
                    String.format("%s completed successfully!\nAlgorithm: %s\nInput Size: %s\nOutput Characters: %d",
                            mode, cipherName, fileSizeStr, resultText.length()),
                    "Operation Succeeded",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (InvalidKeyException e) {
            JOptionPane.showMessageDialog(this,
                    "Key Validation Failed:\n" + e.getMessage() + "\n\nRequirement: " + cipher.getKeyRequirementDescription(),
                    "Invalid Key",
                    JOptionPane.ERROR_MESSAGE);

            OperationRecord failureRecord = new OperationRecord(
                    0,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    fileName,
                    cipherName,
                    mode,
                    key,
                    fileSizeStr,
                    "FAILED"
            );
            dbManager.logOperation(failureRecord);

            setStatus("Operation failed: " + e.getMessage());
            keyInputField.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "An unexpected cryptographic error occurred:\n" + e.getMessage(),
                    "Execution Error",
                    JOptionPane.ERROR_MESSAGE);
            setStatus("Error executing cryptography: " + e.getMessage());
        }
    }

    private void saveOutputToFile() {
        String outputText = outputTextArea.getText();
        if (outputText == null || outputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There is no output content to save.", "Empty Output", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Output Content To File");

        String defaultName = "processed_output.txt";
        if (currentSelectedFile != null) {
            String orig = currentSelectedFile.getName();
            int dot = orig.lastIndexOf('.');
            String base = (dot > 0) ? orig.substring(0, dot) : orig;
            defaultName = base + "_output.txt";
        }
        chooser.setSelectedFile(new File(defaultName));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File destination = chooser.getSelectedFile();
            try {
                FileHandler.writeFile(destination, outputText);
                JOptionPane.showMessageDialog(this,
                        "File successfully saved:\n" + destination.getAbsolutePath(),
                        "Save Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                setStatus("Saved output to " + destination.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Failed to save file:\n" + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
                setStatus("Error saving output file.");
            }
        }
    }

    private void copyOutputToClipboard() {
        String text = outputTextArea.getText();
        if (text != null && !text.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            setStatus("Output copied to clipboard.");
            JOptionPane.showMessageDialog(this, "Output copied to system clipboard.", "Copied", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to copy.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openHistoryDialog() {
        HistoryDialog dialog = new HistoryDialog(this);
        dialog.setVisible(true);
    }

    private void resetWorkspace() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset all input, output, and key fields?",
                "Reset Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            clearSelectedFile();
            keyInputField.setText("");
            cipherComboBox.setSelectedIndex(0);
            setStatus("Workspace reset to default.");
        }
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
