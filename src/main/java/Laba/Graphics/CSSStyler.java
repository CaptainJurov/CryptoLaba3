package Laba.Graphics;


import java.awt.*;
import java.io.*;
import java.util.*;

public class CSSStyler {
    private Properties theme;

    public CSSStyler(String themeFile) {
        theme = new Properties();
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream(themeFile);
            if (input == null) {
                input = new FileInputStream("src/main/resources/" + themeFile);
            }
            if (input != null) {
                theme.load(input);
            }
        } catch (IOException e) {
            System.err.println("Failed to load theme: " + e.getMessage());
            loadDefaultTheme();
        }
    }

    private void loadDefaultTheme() {
        theme.setProperty("background.primary", "#0f1419");
        theme.setProperty("background.secondary", "#1a2634");
        theme.setProperty("text.primary", "#ffffff");
    }

    public Color getColor(String key) {
        String value = theme.getProperty(key);
        if (value != null && value.startsWith("#")) {
            try {
                return Color.decode(value);
            } catch (NumberFormatException e) {
                System.err.println("Invalid color: " + value);
            }
        }
        return Color.GRAY;
    }

    public Font getFont(String type) {
        String family = theme.getProperty("font.family." + type, "Segoe UI");
        int size = getInt("font.size." + type, 12);
        return new Font(family, Font.PLAIN, size);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(theme.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String getString(String key, String defaultValue) {
        return theme.getProperty(key, defaultValue);
    }
    public void styleFrame(Frame frame) {
        frame.setBackground(getColor("background.primary"));
    }

    public void stylePanel(Panel panel, String type) {
        switch (type) {
            case "primary":
                panel.setBackground(getColor("background.primary"));
                break;
            case "secondary":
                panel.setBackground(getColor("background.secondary"));
                break;
            case "card":
                panel.setBackground(getColor("background.card"));
                break;
            default:
                panel.setBackground(getColor("background.tertiary"));
        }
    }

    public void styleLabel(Label label, String type) {
        label.setFont(getFont("medium"));
        switch (type) {
            case "title":
                label.setFont(getFont("large"));
                label.setForeground(getColor("accent.primary"));
                break;
            case "accent":
                label.setForeground(getColor("accent.secondary"));
                break;
            case "muted":
                label.setForeground(getColor("text.muted"));
                break;
            default:
                label.setForeground(getColor("text.primary"));
        }
    }

    public void styleButton(Button button, String variant) {
        button.setFont(getFont("medium"));

        switch (variant) {
            case "primary":
                button.setBackground(getColor("button.primary.background"));
                button.setForeground(getColor("button.primary.foreground"));
                break;
            case "secondary":
                button.setBackground(getColor("button.secondary.background"));
                button.setForeground(getColor("button.secondary.foreground"));
                break;
            case "success":
                button.setBackground(getColor("button.success.background"));
                button.setForeground(getColor("button.success.foreground"));
                break;
            case "warning":
                button.setBackground(getColor("button.warning.background"));
                button.setForeground(getColor("button.warning.foreground"));
                break;
            case "danger":
                button.setBackground(getColor("button.danger.background"));
                button.setForeground(getColor("button.danger.foreground"));
                break;
            default:
                button.setBackground(getColor("button.primary.background"));
                button.setForeground(getColor("button.primary.foreground"));
        }


        button.setPreferredSize(new Dimension(
                getInt("button.width", 120),
                getInt("button.height", 35)
        ));
    }

    public void styleTextField(TextField field) {
        field.setFont(getFont("small"));
        field.setBackground(getColor("background.tertiary"));
        field.setForeground(getColor("text.primary"));
        field.setColumns(getInt("textfield.columns", 25));
    }

    public void styleTextArea(TextArea area) {
        area.setFont(getFont("mono"));
        area.setBackground(getColor("background.card"));
        area.setForeground(getColor("accent.success"));
        area.setRows(getInt("textarea.rows", 8));
        area.setColumns(getInt("textarea.columns", 60));
    }

    public void styleChoice(Choice choice) {
        choice.setFont(getFont("small"));
        choice.setBackground(getColor("background.tertiary"));
        choice.setForeground(getColor("text.primary"));
    }
}