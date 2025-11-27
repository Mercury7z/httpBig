package ServerLogic;
import model.Book;
import model.Client;
import util.JsonCreateReadWrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataModel {
    private Book book;
    private List<Book> books = new ArrayList<>();
    private Client client;
    private HashMap<String,Client> clients = new HashMap<>();

    public DataModel() {

    }

    public HashMap<String, Client> getClients() {
        return clients;
    }

    public void write() {
        JsonCreateReadWrite.write("data.json",this);
    }

    public void setClients(HashMap<String, Client> clients) {
        this.clients = clients;
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
