package util;


import ServerLogic.DataModel;
import model.Client;

public class Registration {
    public static boolean registration(DataModel DataModel,String login,String pass) {
        if (!isAlreadyRegistered(DataModel,login,pass)) {
           DataModel.getClients().put(login + pass,Client.getClient(login,pass));

           return true;
        }
        else {
            return false;
        }
    }

    private static boolean isAlreadyRegistered(DataModel DataModel, String login, String pass) {
       return DataModel.getClients().containsKey(login + pass);
    }
}
