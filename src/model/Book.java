package model;

public  class Book {
    private String name;
    private String author;
    private boolean inWork;

    public Book(String name,String author){
        this.name = name;
        this.author = author;
        this.inWork = false;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isInWork() {
        return inWork;
    }
}