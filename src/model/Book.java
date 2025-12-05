package model;

public  class Book {
    private String name;
    private String author;
    private String key;
    private String path;

    private Book(String name,String author,String key){
        this.name = name;
        this.author = author;
        this.key = key;
        this.path = "/data/img/1.jpg";
    }

    public static Book getBook(String name,String author,String key) {
        return new Book(name,author,key);
    }


    public String getPath() {
        return path;
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