package ServerLogic;
import model.Book;
import model.Client;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private Book book;
    private List<Book> books = new ArrayList<>();
    private Client client;

    public DataModel() {

    }

    public Client getClient() {
        return client;
    }

    public Book getBook() {
        return book;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
