import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.undo.UndoManager;

public class BasicPad extends JFrame implements ActionListener {
  JTextArea textArea;
  JList < String > lineNumbers;
  DefaultListModel < String > listModel;
  JScrollPane scrollPane;
  JMenuBar menuBar;
  JMenu fileMenu, editMenu;
  JMenuItem openItem, saveItem, aboutItem, exitItem, undoItem, redoItem;
  UndoManager undoManager;

  public BasicPad() {
    setLayout(new BorderLayout());

    // Create JTextArea
    textArea = new JTextArea();
    // Initialize the UndoManager
    undoManager = new UndoManager();
    // Attach the UndoManager to the textArea
    textArea.getDocument().addUndoableEditListener(undoManager);

    // Create list model and JList for line numbers
    listModel = new DefaultListModel < > ();
    lineNumbers = new JList < > (listModel);
    lineNumbers.setFixedCellWidth(90);
    lineNumbers.setCellRenderer(new LineNumberRenderer());
    lineNumbers.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#aaaaaa")));

    Font font = new Font("Monospaced", Font.PLAIN, 16);
    textArea.setFont(font);
    lineNumbers.setFont(font);

    textArea.setMargin(new Insets(0, 10, 0, 0));

    // Attach line numbers to JTextArea
    textArea.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateLineNumbers();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateLineNumbers();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {}

      private void updateLineNumbers() {
        listModel.clear();
        int lines = textArea.getLineCount();
        for (int i = 1; i <= lines; i++) {
          listModel.addElement(Integer.toString(i));
        }
      }
    });

    // Create JScrollPane containing the JTextArea
    scrollPane = new JScrollPane(textArea);
    scrollPane.setRowHeaderView(lineNumbers);

    // Add the JScrollPane to the frame
    add(scrollPane);

    // Create menu items and set action listeners
    menuBar = new JMenuBar();
    fileMenu = new JMenu("File");
    openItem = new JMenuItem("Open");
    saveItem = new JMenuItem("Save");
    aboutItem = new JMenuItem("About");
    exitItem = new JMenuItem("Exit");
    openItem.addActionListener(this);
    saveItem.addActionListener(this);
    aboutItem.addActionListener(this);
    exitItem.addActionListener(this);
    fileMenu.add(openItem);
    fileMenu.add(saveItem);
    fileMenu.add(aboutItem);
    fileMenu.add(exitItem);
    menuBar.add(fileMenu);

    editMenu = new JMenu("Edit");
    undoItem = new JMenuItem("Undo");
    redoItem = new JMenuItem("Redo");
    undoItem.addActionListener(this);
    redoItem.addActionListener(this);
    editMenu.add(undoItem);
    editMenu.add(redoItem);
    menuBar.add(editMenu); // Add this line to attach

    setJMenuBar(menuBar);

    setTitle("Basicpad");
    setSize(500, 300);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public void actionPerformed(ActionEvent e) {
    String action = e.getActionCommand();
    if ("Open".equals(action)) {
      JFileChooser chooser = new JFileChooser();
      int returnVal = chooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        try (BufferedReader reader = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
          textArea.read(reader, null);
          updateLineNumbers();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    } else if ("Undo".equals(action)) {
      if (undoManager.canUndo()) {
        undoManager.undo();
      }
    } else if ("Redo".equals(action)) {
      if (undoManager.canRedo()) {
        undoManager.redo();
      }
    } else if ("Save".equals(action)) {
      JFileChooser chooser = new JFileChooser();
      int returnVal = chooser.showSaveDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        try (FileWriter writer = new FileWriter(chooser.getSelectedFile())) {
          textArea.write(writer);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    } else if ("About".equals(action)) {

      JEditorPane jEditorPane = new JEditorPane();
      jEditorPane.setContentType("text/html");
      jEditorPane.setText("<html>Made by Damir Sijakovic.<br><a href='https://damir-sijakovic.web.app'>https://damir-sijakovic.web.app</a></html>");
      jEditorPane.setEditable(false);
      jEditorPane.setOpaque(false);
      jEditorPane.addHyperlinkListener(ev -> {
        if (ev.getEventType().equals(javax.swing.event.HyperlinkEvent.EventType.ACTIVATED)) {
          // Open link in browser
          try {
            Desktop.getDesktop().browse(ev.getURL().toURI());
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Can't open browser.");
          }
        }
      });

      JOptionPane.showMessageDialog(null, jEditorPane);

    } else if ("Exit".equals(action)) {
      System.exit(0);
    }

  }

  private class LineNumberRenderer extends DefaultListCellRenderer {
    private Insets padding = new Insets(0, 0, 0, 10);

    public Component getListCellRendererComponent(JList < ? > list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      ((JLabel) renderer).setHorizontalAlignment(JLabel.RIGHT);
      ((JLabel) renderer).setBorder(BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right));
      return renderer;
    }
  }

  private void updateLineNumbers() {
    listModel.clear();
    int lines = textArea.getLineCount();
    for (int i = 1; i <= lines; i++) {
      listModel.addElement(Integer.toString(i));
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new BasicPad().setVisible(true));
  }
}
