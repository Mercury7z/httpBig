package model;

public  class Client {
    private String name;
    private String password;
    private int id;

    private Client(String name,String password){
        this.name = name;
        this.password = password;
    }

    public static Client getClient(String name,String password) {
        return new Client(name,password);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
