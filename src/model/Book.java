package model;

public  class Book {
    private String name;
    private String author;
    private String key;

    private Book(String name,String author,String key){
        this.name = name;
        this.author = author;
        this.key = key;
    }

    public static Book getBook(String name,String author,String key) {
        return new Book(name,author,key);
    }


    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}