package Laba.Graphics;

import Laba.Crypto.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SignatureGUI extends Frame {
    private CSSStyler css;
    private SignatureAlgorithm currentAlgorithm;

    private Choice algorithmChoice;
    private TextField fileField, privateKeyField, publicKeyField, signatureField;
    private Button browseFileBtn, browsePrivateKeyBtn, browsePublicKeyBtn, browseSignatureBtn;
    private Button generateKeysBtn, signBtn, verifyBtn, clearBtn;
    private TextArea logArea;
    private Label titleLabel;

    public SignatureGUI() {
        super("🔐 Crypto Signer");
        css = new CSSStyler("theme.properties");
        setupGUI();
        setupEvents();
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(900, 700);
        setLocationRelativeTo(null);
        css.styleFrame(this);

        titleLabel = new Label("Digital Signature Tool", Label.CENTER);
        css.styleLabel(titleLabel, "title");
        add(titleLabel, BorderLayout.NORTH);

        Panel mainContent = new Panel(new GridLayout(4, 1, 10, 10));
        css.stylePanel(mainContent, "primary");
        add(mainContent, BorderLayout.CENTER);
        Panel algorithmPanel = createAlgorithmPanel();
        mainContent.add(algorithmPanel);
        Panel inputPanel = createInputPanel();
        mainContent.add(inputPanel);
        Panel actionPanel = createActionPanel();
        mainContent.add(actionPanel);
        Panel logPanel = createLogPanel();
        add(logPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
                System.exit(0);
            }
        });
    }

    private Panel createAlgorithmPanel() {
        Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT));
        css.stylePanel(panel, "secondary");

        Label label = new Label("Algorithm:");
        css.styleLabel(label, "accent");
        panel.add(label);

        algorithmChoice = new Choice();
        algorithmChoice.add("RSA");
        algorithmChoice.add("EL_GAMAL");
        algorithmChoice.add("GOST");
        css.styleChoice(algorithmChoice);
        panel.add(algorithmChoice);

        return panel;
    }

    private Panel createInputPanel() {
        Panel panel = new Panel(new GridLayout(4, 2, 8, 8));
        css.stylePanel(panel, "secondary");
        addFileRow(panel, "📄 File:", "fileField", "browseFileBtn");
        addFileRow(panel, "🔑 Private Key:", "privateKeyField", "browsePrivateKeyBtn");
        addFileRow(panel, "🔓 Public Key:", "publicKeyField", "browsePublicKeyBtn");
        addFileRow(panel, "✍️ Signature:", "signatureField", "browseSignatureBtn");

        return panel;
    }

    private void addFileRow(Panel parent, String labelText, String fieldName, String buttonName) {
        Label label = new Label(labelText);
        css.styleLabel(label, "primary");
        parent.add(label);

        Panel fieldPanel = new Panel(new BorderLayout(5, 0));
        css.stylePanel(fieldPanel, "primary");

        TextField field = new TextField();
        css.styleTextField(field);

        Button browseBtn = new Button("Browse");
        css.styleButton(browseBtn, "secondary");

        fieldPanel.add(field, BorderLayout.CENTER);
        fieldPanel.add(browseBtn, BorderLayout.EAST);
        parent.add(fieldPanel);

        switch (fieldName) {
            case "fileField": fileField = field; browseFileBtn = browseBtn; break;
            case "privateKeyField": privateKeyField = field; browsePrivateKeyBtn = browseBtn; break;
            case "publicKeyField": publicKeyField = field; browsePublicKeyBtn = browseBtn; break;
            case "signatureField": signatureField = field; browseSignatureBtn = browseBtn; break;
        }
    }

    private Panel createActionPanel() {
        Panel panel = new Panel(new FlowLayout());
        css.stylePanel(panel, "secondary");

        generateKeysBtn = new Button("🎯 Generate Keys");
        signBtn = new Button("📝 Sign File");
        verifyBtn = new Button("✅ Verify Signature");
        clearBtn = new Button("🗑️ Clear All");

        css.styleButton(generateKeysBtn, "warning");
        css.styleButton(signBtn, "success");
        css.styleButton(verifyBtn, "secondary");
        css.styleButton(clearBtn, "danger");

        panel.add(generateKeysBtn);
        panel.add(signBtn);
        panel.add(verifyBtn);
        panel.add(clearBtn);

        return panel;
    }

    private Panel createLogPanel() {
        Panel panel = new Panel(new BorderLayout());
        css.stylePanel(panel, "secondary");

        Label logLabel = new Label("📋 Activity Log:");
        css.styleLabel(logLabel, "accent");
        panel.add(logLabel, BorderLayout.NORTH);

        logArea = new TextArea();
        css.styleTextArea(logArea);
        panel.add(logArea, BorderLayout.CENTER);

        return panel;
    }

    private void setupEvents() {

        browseFileBtn.addActionListener(e -> browseFile(fileField));
        browsePrivateKeyBtn.addActionListener(e -> browseFile(privateKeyField));
        browsePublicKeyBtn.addActionListener(e -> browseFile(publicKeyField));
        browseSignatureBtn.addActionListener(e -> browseFile(signatureField));

        generateKeysBtn.addActionListener(e -> generateKeys());
        signBtn.addActionListener(e -> signFile());
        verifyBtn.addActionListener(e -> verifySignature());
        clearBtn.addActionListener(e -> clearAll());

        algorithmChoice.addItemListener(e -> updateAlgorithm());
    }

    private void browseFile(TextField field) {
        FileDialog fd = new FileDialog(this, "Select File", FileDialog.LOAD);
        fd.setVisible(true);

        if (fd.getFile() != null) {
            String filename = fd.getDirectory() + fd.getFile();
            field.setText(filename);
        }
    }

    private void updateAlgorithm() {
        try {
            String algName = algorithmChoice.getSelectedItem();
            SignatureFactory.Algorithm alg = SignatureFactory.Algorithm.valueOf(algName);
            currentAlgorithm = SignatureFactory.createAlgorithm(alg);
            log("Algorithm changed to: " + algName);
        } catch (Exception e) {
            logError("Error changing algorithm: " + e.getMessage());
        }
    }

    private void generateKeys() {
        try {
            if (currentAlgorithm == null) updateAlgorithm();

            log("Generating key pair for " + algorithmChoice.getSelectedItem() + "...");
            KeyPair keyPair = currentAlgorithm.generateKeyPair();

            String baseName = algorithmChoice.getSelectedItem().toLowerCase();
            String privateKeyFile = baseName + "_private.key";
            String publicKeyFile = baseName + "_public.key";

            saveObject(keyPair.getPrivateKey(), privateKeyFile);
            saveObject(keyPair.getPublicKey(), publicKeyFile);

            privateKeyField.setText(privateKeyFile);
            publicKeyField.setText(publicKeyFile);

            log("✓ Key pair generated successfully!");

        } catch (Exception e) {
            logError("Error generating keys: " + e.getMessage());
        }
    }

    private void signFile() {
        try {
            if (currentAlgorithm == null) updateAlgorithm();

            String filePath = fileField.getText();
            String privateKeyPath = privateKeyField.getText();

            if (filePath.isEmpty() || privateKeyPath.isEmpty()) {
                logError("Please select both file and private key");
                return;
            }

            log("Signing file: " + filePath);
            PrivateKey privateKey = (PrivateKey) loadObject(privateKeyPath);
            byte[] fileData = Files.readAllBytes(new File(filePath).toPath());
            byte[] signature = currentAlgorithm.sign(fileData, privateKey);

            String signatureFile = filePath + ".sig";
            Files.write(new File(signatureFile).toPath(), signature);
            signatureField.setText(signatureFile);

            log("✓ File signed successfully!");

        } catch (Exception e) {
            logError("Error signing file: " + e.getMessage());
        }
    }

    private void verifySignature() {
        try {
            if (currentAlgorithm == null) updateAlgorithm();

            String filePath = fileField.getText();
            String signaturePath = signatureField.getText();
            String publicKeyPath = publicKeyField.getText();

            if (filePath.isEmpty() || signaturePath.isEmpty() || publicKeyPath.isEmpty()) {
                logError("Please select file, signature and public key");
                return;
            }

            log("Verifying signature...");
            PublicKey publicKey = (PublicKey) loadObject(publicKeyPath);
            byte[] fileData = Files.readAllBytes(new File(filePath).toPath());
            byte[] signature = Files.readAllBytes(new File(signaturePath).toPath());

            boolean isValid = currentAlgorithm.verify(fileData, signature, publicKey);

            if (isValid) {
                log("✓ SIGNATURE IS VALID");
                showMessageDialog("Success", "Signature verification successful!", "success");
            } else {
                log("✗ SIGNATURE IS INVALID");
                showMessageDialog("Error", "Signature verification failed!", "error");
            }

        } catch (Exception e) {
            logError("Error verifying signature: " + e.getMessage());
        }
    }

    private void clearAll() {
        fileField.setText("");
        privateKeyField.setText("");
        publicKeyField.setText("");
        signatureField.setText("");
        logArea.setText("");
        log("All fields cleared.");
    }

    private void saveObject(Object obj, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(obj);
        }
    }

    private Object loadObject(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        }
    }

    private void log(String message) {
        logArea.append("[" + new java.util.Date() + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getText().length());
    }

    private void logError(String message) {
        logArea.append("[" + new java.util.Date() + "] ❌ " + message + "\n");
        logArea.setCaretPosition(logArea.getText().length());
    }

    private void showMessageDialog(String title, String message, String type) {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(this);

        Color bgColor = type.equals("success") ? css.getColor("accent.success") : css.getColor("accent.error");
        dialog.setBackground(bgColor);

        Panel panel = new Panel(new BorderLayout());
        Label label = new Label(message, Label.CENTER);
        label.setForeground(Color.BLACK);
        label.setFont(css.getFont("medium"));
        panel.add(label, BorderLayout.CENTER);
        panel.setBackground(bgColor);

        Button okBtn = new Button("OK");
        css.styleButton(okBtn, "primary");
        okBtn.addActionListener(e -> dialog.dispose());
        panel.add(okBtn, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SignatureGUI gui = new SignatureGUI();
        gui.setVisible(true);
        gui.log("🚀 Digital Signature Tool started with CSS styling");
        gui.log("💫 Select an algorithm and begin signing files!");
    }
}