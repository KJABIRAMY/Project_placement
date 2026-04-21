import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.datatransfer.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UndoRedoEditor extends JFrame {

    // ─── Color Palette ───────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(13, 13, 18);
    private static final Color BG_PANEL     = new Color(20, 20, 28);
    private static final Color BG_CARD      = new Color(26, 26, 36);
    private static final Color BG_EDITOR    = new Color(16, 16, 22);
    private static final Color ACCENT       = new Color(99, 102, 241);   // indigo
    private static final Color ACCENT_SOFT  = new Color(99, 102, 241, 60);
    private static final Color ACCENT_GLOW  = new Color(139, 92, 246);  // violet
    private static final Color TEXT_PRI     = new Color(237, 237, 245);
    private static final Color TEXT_SEC     = new Color(140, 140, 165);
    private static final Color TEXT_MUTED   = new Color(80, 80, 100);
    private static final Color BORDER_COLOR = new Color(40, 40, 56);
    private static final Color SUCCESS      = new Color(52, 211, 153);
    private static final Color WARNING      = new Color(251, 191, 36);
    private static final Color DANGER       = new Color(248, 113, 113);
    private static final Color HIST_CURRENT = new Color(99, 102, 241, 40);
    private static final Color HIST_FUTURE  = new Color(255, 255, 255, 15);

    // ─── Fonts ───────────────────────────────────────────────────────────────
    private static final Font FONT_EDITOR;
    private static final Font FONT_MONO;
    private static final Font FONT_UI;
    private static final Font FONT_UI_SMALL;
    private static final Font FONT_UI_BOLD;
    private static final Font FONT_TITLE;

    static {
        Font mono = new Font("JetBrains Mono", Font.PLAIN, 15);
        if (!mono.getFamily().equals("JetBrains Mono")) {
            mono = new Font("Consolas", Font.PLAIN, 15);
            if (!mono.getFamily().equals("Consolas"))
                mono = new Font(Font.MONOSPACED, Font.PLAIN, 15);
        }
        FONT_EDITOR    = mono;
        FONT_MONO      = mono.deriveFont(Font.PLAIN, 12f);
        FONT_UI        = new Font("Segoe UI", Font.PLAIN, 13);
        FONT_UI_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
        FONT_UI_BOLD   = new Font("Segoe UI", Font.BOLD, 13);
        FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 14);
    }

    // ─── State ───────────────────────────────────────────────────────────────
    private final UndoManager undoManager = new UndoManager();
    private final ArrayList<HistoryEntry> historyLog = new ArrayList<>();

    // ─── Widgets ─────────────────────────────────────────────────────────────
    private JTextPane textPane;
    private JButton btnUndo, btnRedo, btnClear, btnCopy, btnFind;
    private JLabel lblChars, lblWords, lblLines, lblStatus, lblTime;
    private JList<HistoryEntry> historyList;
    private DefaultListModel<HistoryEntry> historyModel;
    private JPanel statusDot;
    private Timer clockTimer;
    private boolean suppressHistory = false;

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new UndoRedoEditor().setVisible(true));
    }

    public UndoRedoEditor() {
        super("Quill — Modern Text Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(800, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        buildUI();
        setupListeners();
        startClock();
        logHistory("Session started");
        updateToolbarState();

        // Welcome text
        textPane.setText("Welcome to Quill — a sleek undo/redo text editor.\n\nTry typing something, then use Ctrl+Z to undo or Ctrl+Y to redo.\nYou can also click any entry in the History panel to inspect it.\n\nShortcuts:\n  Ctrl+Z   →  Undo\n  Ctrl+Y   →  Redo\n  Ctrl+A   →  Select All\n  Ctrl+C   →  Copy\n  Ctrl+X   →  Cut\n  Ctrl+V   →  Paste");
        textPane.setCaretPosition(0);
        updateStats();
    }

    // ─── UI Construction ─────────────────────────────────────────────────────
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // Title bar
        add(buildTitleBar(), BorderLayout.NORTH);

        // Main split
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildEditorPanel(), buildSidePanel());
        split.setDividerLocation(720);
        split.setDividerSize(1);
        split.setBorder(null);
        split.setBackground(BORDER_COLOR);
        split.setContinuousLayout(true);
        add(split, BorderLayout.CENTER);

        // Status bar
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout(0, 0));
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(0, 52));

        // Logo + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        left.setOpaque(false);

        JLabel logo = new JLabel("✦ Quill") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                GradientPaint gp = new GradientPaint(0, 0, ACCENT, getWidth(), 0, ACCENT_GLOW);
                g2.setPaint(gp);
                g2.drawString(getText(), 0, fm.getAscent());
                g2.dispose();
            }
        };
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(ACCENT);
        logo.setPreferredSize(new Dimension(90, 52));
        left.add(logo);

        // Separator
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 24));
        sep.setForeground(BORDER_COLOR);
        left.add(sep);

        // Toolbar buttons
        btnUndo  = makeToolbarBtn("↩ Undo",  "Ctrl+Z", ACCENT);
        btnRedo  = makeToolbarBtn("↪ Redo",  "Ctrl+Y", ACCENT);
        btnClear = makeToolbarBtn("⊘ Clear", "Reset",  DANGER);
        btnCopy  = makeToolbarBtn("⎘ Copy",  "Ctrl+C", SUCCESS);
        btnFind  = makeToolbarBtn("⌕ Find",  "Ctrl+F", WARNING);

        for (JButton b : new JButton[]{btnUndo, btnRedo, btnClear, btnCopy, btnFind})
            left.add(b);

        bar.add(left, BorderLayout.WEST);

        // Clock
        lblTime = new JLabel("00:00:00");
        lblTime.setFont(FONT_MONO);
        lblTime.setForeground(TEXT_MUTED);
        lblTime.setBorder(new EmptyBorder(0, 0, 0, 20));
        bar.add(lblTime, BorderLayout.EAST);

        return bar;
    }

    private JButton makeToolbarBtn(String text, String tooltip, Color accentColor) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hovered && isEnabled()
                        ? new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30)
                        : new Color(0, 0, 0, 0);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isEnabled() ? (hovered ? accentColor : TEXT_SEC) : TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics(getFont());
                g2.setFont(getFont());
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80) : BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        btn.setFont(FONT_UI);
        btn.setForeground(TEXT_SEC);
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(90, 32));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel buildEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_EDITOR);

        // Line numbers + editor
        textPane = new JTextPane();
        textPane.setBackground(BG_EDITOR);
        textPane.setForeground(TEXT_PRI);
        textPane.setCaretColor(ACCENT);
        textPane.setFont(FONT_EDITOR);
        textPane.setSelectionColor(ACCENT_SOFT);
        textPane.setSelectedTextColor(TEXT_PRI);
        textPane.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Custom caret
        textPane.setCaret(new DefaultCaret() {
            { setBlinkRate(530); }
            @Override public void paint(Graphics g) {
                if (!isVisible()) return;
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(ACCENT);
                try {
                    Rectangle r = textPane.modelToView(getDot());
                    if (r != null) {
                        g2.setStroke(new BasicStroke(2f));
                        g2.drawLine(r.x, r.y, r.x, r.y + r.height);
                    }
                } catch (BadLocationException ignored) {}
            }
        });

        LineNumberComponent lineNums = new LineNumberComponent(textPane);

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBorder(null);
        scroll.setBackground(BG_EDITOR);
        scroll.getViewport().setBackground(BG_EDITOR);
        scroll.setRowHeaderView(lineNums);
        scroll.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new SlimScrollBarUI());

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSidePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR));
        panel.setPreferredSize(new Dimension(280, 0));

        // Stats cards
        panel.add(buildStatsPanel(), BorderLayout.NORTH);

        // History
        panel.add(buildHistoryPanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 8, 0));
        panel.setBackground(BG_PANEL);
        panel.setBorder(new EmptyBorder(14, 12, 10, 12));

        lblChars = new JLabel("0");
        lblWords = new JLabel("0");
        lblLines = new JLabel("1");

        panel.add(makeStatCard(lblChars, "CHARS",  ACCENT));
        panel.add(makeStatCard(lblWords, "WORDS",  ACCENT_GLOW));
        panel.add(makeStatCard(lblLines, "LINES",  SUCCESS));
        return panel;
    }

    private JPanel makeStatCard(JLabel valueLabel, String labelText, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 3)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                // Top accent bar
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), 3, 2, 2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 10, 8, 10));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_PRI);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new EmptyBorder(12, 14, 8, 14));

        JLabel title = new JLabel("⏱  Edit History");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRI);

        JLabel hint = new JLabel("click to jump");
        hint.setFont(FONT_UI_SMALL);
        hint.setForeground(TEXT_MUTED);

        header.add(title, BorderLayout.WEST);
        header.add(hint, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // List
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setBackground(BG_PANEL);
        historyList.setForeground(TEXT_SEC);
        historyList.setFont(FONT_MONO);
        historyList.setFixedCellHeight(44);
        historyList.setBorder(null);
        historyList.setSelectionModel(new DefaultListSelectionModel());
        historyList.setCellRenderer(new HistoryCellRenderer());

        JScrollPane scroll = new JScrollPane(historyList);
        scroll.setBorder(null);
        scroll.setBackground(BG_PANEL);
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.getVerticalScrollBar().setUI(new SlimScrollBarUI());

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(0, 30));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        left.setOpaque(false);

        statusDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillOval(2, 2, 8, 8);
                g2.dispose();
            }
        };
        statusDot.setPreferredSize(new Dimension(12, 12));
        statusDot.setBackground(SUCCESS);
        statusDot.setOpaque(false);

        lblStatus = new JLabel("Ready");
        lblStatus.setFont(FONT_UI_SMALL);
        lblStatus.setForeground(TEXT_SEC);

        left.add(statusDot);
        left.add(lblStatus);
        bar.add(left, BorderLayout.WEST);

        JLabel hint = new JLabel("Ctrl+Z · Undo   |   Ctrl+Y · Redo   |   Ctrl+A · Select All");
        hint.setFont(FONT_UI_SMALL);
        hint.setForeground(TEXT_MUTED);
        hint.setBorder(new EmptyBorder(0, 0, 0, 16));
        bar.add(hint, BorderLayout.EAST);
        return bar;
    }

    // ─── Listeners ────────────────────────────────────────────────────────────
    private void setupListeners() {
        // Undo manager on document
        textPane.getDocument().addUndoableEditListener(e -> {
            undoManager.addEdit(e.getEdit());
            updateToolbarState();
            updateStats();
        });

        // Doc change → log history (debounced via timer)
        final Timer[] debounce = {null};
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { scheduleLog("Typed"); }
            public void removeUpdate(DocumentEvent e)  { scheduleLog("Deleted"); }
            public void changedUpdate(DocumentEvent e) {}
            void scheduleLog(String action) {
                updateStats();
                if (suppressHistory) return;
                if (debounce[0] != null) debounce[0].stop();
                debounce[0] = new Timer(700, ev -> {
                    logHistory(action);
                    debounce[0] = null;
                });
                debounce[0].setRepeats(false);
                debounce[0].start();
            }
        });

        // Toolbar
        btnUndo.addActionListener(e -> performUndo());
        btnRedo.addActionListener(e -> performRedo());
        btnClear.addActionListener(e -> performClear());
        btnCopy.addActionListener(e -> performCopy());
        btnFind.addActionListener(e -> showFindDialog());

        // History click
        historyList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int idx = historyList.locationToIndex(e.getPoint());
                if (idx >= 0) jumpToHistory(idx);
            }
        });

        // Keyboard shortcuts
        KeyStroke undoKey = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        KeyStroke redoKey = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
        textPane.getInputMap().put(undoKey, "undo");
        textPane.getInputMap().put(redoKey, "redo");
        textPane.getActionMap().put("undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { performUndo(); }
        });
        textPane.getActionMap().put("redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { performRedo(); }
        });
    }

    // ─── Actions ──────────────────────────────────────────────────────────────
    private void performUndo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
            updateToolbarState();
            updateStats();
            setStatus("Undone", SUCCESS);
            logHistory("Undo");
        }
    }

    private void performRedo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
            updateToolbarState();
            updateStats();
            setStatus("Redone", ACCENT_GLOW);
            logHistory("Redo");
        }
    }

    private void performClear() {
        int confirm = JOptionPane.showOptionDialog(this,
                "Clear all text? This can be undone.", "Clear Editor",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{"Clear", "Cancel"}, "Cancel");
        if (confirm == 0) {
            textPane.setText("");
            setStatus("Cleared", DANGER);
            logHistory("Cleared");
        }
    }

    private void performCopy() {
        String text = textPane.getText();
        if (text.isEmpty()) return;
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
        setStatus("Copied to clipboard", SUCCESS);
    }

    private void showFindDialog() {
        String term = JOptionPane.showInputDialog(this, "Find text:", "Find", JOptionPane.PLAIN_MESSAGE);
        if (term == null || term.isEmpty()) return;
        String content = textPane.getText();
        int idx = content.indexOf(term);
        if (idx >= 0) {
            textPane.setSelectionStart(idx);
            textPane.setSelectionEnd(idx + term.length());
            textPane.requestFocus();
            setStatus("Found: \"" + term + "\"", SUCCESS);
        } else {
            setStatus("Not found: \"" + term + "\"", DANGER);
        }
    }

    private void jumpToHistory(int idx) {
        if (idx < 0 || idx >= historyModel.size()) return;
        HistoryEntry entry = historyModel.get(idx);
        suppressHistory = true;
        textPane.setText(entry.content);
        textPane.setCaretPosition(Math.min(entry.caretPos, entry.content.length()));
        suppressHistory = false;
        setStatus("Jumped to: " + entry.action, ACCENT_GLOW);
        updateStats();
        historyList.setSelectedIndex(idx);
        historyList.ensureIndexIsVisible(idx);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private void updateToolbarState() {
        btnUndo.setEnabled(undoManager.canUndo());
        btnRedo.setEnabled(undoManager.canRedo());
    }

    private void updateStats() {
        String text = textPane.getText();
        int chars = text.length();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int lines = text.isEmpty() ? 1 : text.split("\n", -1).length;
        lblChars.setText(String.valueOf(chars));
        lblWords.setText(String.valueOf(words));
        lblLines.setText(String.valueOf(lines));
    }

    private void logHistory(String action) {
        String content = textPane.getText();
        int caret = textPane.getCaretPosition();
        HistoryEntry entry = new HistoryEntry(action, content, caret);
        historyModel.addElement(entry);
        historyList.ensureIndexIsVisible(historyModel.size() - 1);
    }

    private void setStatus(String msg, Color color) {
        lblStatus.setText(msg);
        statusDot.setBackground(color);
        Timer t = new Timer(2500, e -> {
            lblStatus.setText("Ready");
            statusDot.setBackground(SUCCESS);
        });
        t.setRepeats(false);
        t.start();
    }

    private void startClock() {
        clockTimer = new Timer(1000, e -> {
            lblTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        clockTimer.start();
        lblTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    // ─── Data Model ──────────────────────────────────────────────────────────
    static class HistoryEntry {
        final String action, content, time;
        final int caretPos;
        HistoryEntry(String action, String content, int caret) {
            this.action = action;
            this.content = content;
            this.caretPos = caret;
            this.time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        String preview() {
            String s = content.replace('\n', ' ').trim();
            return s.isEmpty() ? "(empty)" : (s.length() > 38 ? s.substring(0, 38) + "…" : s);
        }
    }

    // ─── History Cell Renderer ────────────────────────────────────────────────
    class HistoryCellRenderer extends JPanel implements ListCellRenderer<HistoryEntry> {
        private final JLabel lblAction = new JLabel();
        private final JLabel lblPreview = new JLabel();
        private final JLabel lblTime = new JLabel();
        private boolean current = false;

        HistoryCellRenderer() {
            setLayout(new BorderLayout(0, 2));
            setBorder(new EmptyBorder(6, 14, 6, 12));
            setOpaque(false);

            lblAction.setFont(FONT_UI_BOLD);
            lblPreview.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblPreview.setForeground(TEXT_MUTED);
            lblTime.setFont(FONT_MONO.deriveFont(10f));
            lblTime.setForeground(TEXT_MUTED);
            lblTime.setHorizontalAlignment(SwingConstants.RIGHT);

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(lblAction, BorderLayout.WEST);
            top.add(lblTime, BorderLayout.EAST);

            add(top, BorderLayout.NORTH);
            add(lblPreview, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends HistoryEntry> list,
                HistoryEntry value, int index, boolean isSelected, boolean cellHasFocus) {
            current = isSelected;
            lblAction.setText("  " + value.action);
            lblPreview.setText("  " + value.preview());
            lblTime.setText(value.time + "  ");

            // Color badge for action type
            Color badge = switch (value.action) {
                case "Undo"    -> ACCENT;
                case "Redo"    -> ACCENT_GLOW;
                case "Cleared" -> DANGER;
                case "Session started" -> SUCCESS;
                default        -> WARNING;
            };
            lblAction.setForeground(badge);
            return this;
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = current ? HIST_CURRENT : new Color(0, 0, 0, 0);
            g2.setColor(bg);
            g2.fillRoundRect(4, 1, getWidth() - 8, getHeight() - 2, 8, 8);
            if (current) {
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(4, 1, getWidth() - 8 - 1, getHeight() - 2 - 1, 8, 8);
            } else {
                g2.setColor(BORDER_COLOR);
                g2.drawLine(14, getHeight() - 1, getWidth() - 14, getHeight() - 1);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ─── Line Number Component ────────────────────────────────────────────────
    class LineNumberComponent extends JComponent implements DocumentListener, CaretListener {
        private final JTextPane editor;
        private static final int WIDTH = 44;

        LineNumberComponent(JTextPane editor) {
            this.editor = editor;
            editor.getDocument().addDocumentListener(this);
            editor.addCaretListener(this);
            setPreferredSize(new Dimension(WIDTH, 0));
            setBackground(BG_PANEL);
            setOpaque(true);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG_PANEL);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(BORDER_COLOR);
            g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

            FontMetrics fm = g2.getFontMetrics(FONT_EDITOR);
            int lineHeight = fm.getHeight();
            Rectangle clip = g2.getClipBounds();
            int startLine = clip.y / lineHeight;
            int endLine = (clip.y + clip.height) / lineHeight + 1;

            int cursorLine = 0;
            try {
                int caretPos = editor.getCaretPosition();
                cursorLine = editor.getDocument().getDefaultRootElement()
                        .getElementIndex(caretPos);
            } catch (Exception ignored) {}

            int lineCount = editor.getDocument().getDefaultRootElement().getElementCount();
            for (int i = startLine; i < Math.min(endLine, lineCount); i++) {
                boolean isCurrent = (i == cursorLine);
                g2.setFont(FONT_EDITOR.deriveFont(Font.PLAIN, 11f));
                g2.setColor(isCurrent ? ACCENT : TEXT_MUTED);
                String num = String.valueOf(i + 1);
                int x = getWidth() - fm.stringWidth(num) - 8;
                int y = i * lineHeight + fm.getAscent() + 20;
                g2.drawString(num, x, y);
            }
            g2.dispose();
        }

        public void insertUpdate(DocumentEvent e)  { repaint(); }
        public void removeUpdate(DocumentEvent e)  { repaint(); }
        public void changedUpdate(DocumentEvent e) { repaint(); }
        public void caretUpdate(CaretEvent e)      { repaint(); }
    }

    // ─── Slim Scroll Bar ──────────────────────────────────────────────────────
    static class SlimScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = new Color(80, 80, 110);
            trackColor = BG_PANEL;
        }
        @Override protected JButton createDecreaseButton(int o) { return invisibleBtn(); }
        @Override protected JButton createIncreaseButton(int o) { return invisibleBtn(); }
        private JButton invisibleBtn() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(99, 102, 241, 140));
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 6, 6);
            g2.dispose();
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(BG_PANEL);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }
}