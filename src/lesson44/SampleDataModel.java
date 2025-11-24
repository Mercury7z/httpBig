package lesson44;

import model.Book;
import util.JsonCreateReadWrite;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SampleDataModel {
    private Book book = new Book("Apache", "FreeMarker");
    private List<Book> books = new ArrayList<>();


    public SampleDataModel() {

        books.add(new Book("lOTR","Tolkin"));
        books.add(new Book("lOTR2","Tolkin"));
        books.add(new Book("lOTR3","Tolkin"));

        JsonCreateReadWrite.write("data.json",this);
    }

//    public void writeData() {
//        JsonCreateReadWrite.write("data.json" ,this);
//    }


    public Book getBook() {
        return book;
    }

    public List<Book> getBooks() {
        return books;
    }

}
