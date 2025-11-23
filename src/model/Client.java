package model;

public  class Client {
    private String name;
    private String author;

    private Client(String name){
        this.name = name;
    }

    public static Client getClient(String name) {
        return new Client(name);
    }
}
