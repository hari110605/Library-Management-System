import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class LibraryManagementSystem {

    private JFrame mainFrame;
    private JTextField titleField, authorField, isbnField, yearField, searchField;
    private JComboBox<String> genreComboBox;
    private JCheckBox availabilityCheckBox;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private ArrayList<Book> bookList = new ArrayList<>();
    private HashMap<String, Book> bookMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryManagementSystem().createAndShowGUI());
    }

    private void createAndShowGUI() {
        mainFrame = new JFrame("Library Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        
        // Creating MenuBar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        mainFrame.setJMenuBar(menuBar);

        // Creating ToolBar
        JToolBar toolBar = new JToolBar();
        JButton addButton = new JButton("Add Book");
        JButton removeButton = new JButton("Remove Book");
        JButton searchButton = new JButton("Search");
        toolBar.add(addButton);
        toolBar.add(removeButton);
        toolBar.add(searchButton);

        // Main Panels organized using JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Book Details Panel
        JPanel bookDetailsPanel = new JPanel(new GridLayout(12, 4, 20, 20));
        bookDetailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        bookDetailsPanel.add(new JLabel("Book Title:"));
        titleField = new JTextField();
        bookDetailsPanel.add(titleField);

        bookDetailsPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        bookDetailsPanel.add(authorField);

        bookDetailsPanel.add(new JLabel("ISBN:"));
        isbnField = new JTextField();
        bookDetailsPanel.add(isbnField);

        bookDetailsPanel.add(new JLabel("Publication Year:"));
        yearField = new JTextField();
        bookDetailsPanel.add(yearField);

        bookDetailsPanel.add(new JLabel("Genre:"));
        String[] genres = {"Fiction", "Non-Fiction", "Science", "Fantasy", "Other"};
        genreComboBox = new JComboBox<>(genres);
        bookDetailsPanel.add(genreComboBox);

        bookDetailsPanel.add(new JLabel("Available:"));
        availabilityCheckBox = new JCheckBox();
        bookDetailsPanel.add(availabilityCheckBox);

        JButton addBookButton = new JButton("Add Book");
        JButton updateBookButton = new JButton("Update Book");
        bookDetailsPanel.add(addBookButton);
        bookDetailsPanel.add(updateBookButton);

        tabbedPane.add("Book Details", bookDetailsPanel);

        // Book List Panel
        JPanel bookListPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Title", "Author", "ISBN", "Genre", "Available"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchBarButton = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBarButton);

        bookListPanel.add(searchPanel, BorderLayout.NORTH);
        bookListPanel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.add("Book List", bookListPanel);

        mainFrame.add(toolBar, BorderLayout.NORTH);
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        
        // Event Handling
        addBookButton.addActionListener(e -> addBook());
        updateBookButton.addActionListener(e -> updateBook());
        removeButton.addActionListener(e -> removeBook());
        searchBarButton.addActionListener(e -> searchBook());

        mainFrame.setVisible(true);
    }

    // Book Class
    class Book {
        String title, author, isbn, genre;
        int year;
        boolean available;

        Book(String title, String author, String isbn, int year, String genre, boolean available) {
            this.title = title;
            this.author = author;
            this.isbn = isbn;
            this.year = year;
            this.genre = genre;
            this.available = available;
        }
    }

    // Add Book
    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();
        int year = Integer.parseInt(yearField.getText());
        String genre = genreComboBox.getSelectedItem().toString();
        boolean available = availabilityCheckBox.isSelected();

        if (!bookMap.containsKey(isbn)) {
            Book book = new Book(title, author, isbn, year, genre, available);
            bookList.add(book);
            bookMap.put(isbn, book);

            tableModel.addRow(new Object[]{title, author, isbn, genre, available});
            clearFields();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Book with this ISBN already exists!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update Book
    private void updateBook() {
        String isbn = isbnField.getText();
        if (bookMap.containsKey(isbn)) {
            Book book = bookMap.get(isbn);
            book.title = titleField.getText();
            book.author = authorField.getText();
            book.year = Integer.parseInt(yearField.getText());
            book.genre = genreComboBox.getSelectedItem().toString();
            book.available = availabilityCheckBox.isSelected();

            int row = findRowByISBN(isbn);
            if (row != -1) {
                tableModel.setValueAt(book.title, row, 0);
                tableModel.setValueAt(book.author, row, 1);
                tableModel.setValueAt(book.isbn, row, 2);
                tableModel.setValueAt(book.genre, row, 3);
                tableModel.setValueAt(book.available, row, 4);
            }
            clearFields();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Remove Book
    private void removeBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            String isbn = tableModel.getValueAt(selectedRow, 2).toString();
            bookList.removeIf(book -> book.isbn.equals(isbn));
            bookMap.remove(isbn);
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "No book selected to remove!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Search Book
    private void searchBook() {
        String searchText = searchField.getText();
        for (int i = 0; i < bookTable.getRowCount(); i++) {
            if (bookTable.getValueAt(i, 0).toString().contains(searchText) ||
                bookTable.getValueAt(i, 1).toString().contains(searchText) ||
                bookTable.getValueAt(i, 2).toString().contains(searchText)) {
                bookTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    // Clear input fields
    private void clearFields() {
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        yearField.setText("");
        genreComboBox.setSelectedIndex(0);
        availabilityCheckBox.setSelected(false);
    }

    // Find row by ISBN
    private int findRowByISBN(String isbn) {
        for (int i = 0; i < bookTable.getRowCount(); i++) {
            if (bookTable.getValueAt(i, 2).toString().equals(isbn)) {
                return i;
            }
        }
        return -1;
    }
}