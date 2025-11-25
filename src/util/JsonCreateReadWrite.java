package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ServerLogic.DataModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public final class JsonCreateReadWrite {

    private static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public static DataModel read(String fileName) {
        var filePath = Path.of("data/dataBase/", fileName);
        DataModel data = new DataModel();
        try {
            data = getGson().fromJson(Files.readString(filePath), DataModel.class);
            String content = Files.readString(filePath);
            if (content.trim().isEmpty()) {
                System.out.println("ASD");
                return new DataModel();
            }
        } catch (IOException e) {
            System.out.println("Выбранного файла не существует, он пустой или содержит данные в неверном формате.");
            e.printStackTrace();
        }
        return data;
    }

    public static void write(String fileName, DataModel data) {
        var filePath = Path.of("data/dataBase/", fileName);
        var json = getGson().toJson(data);
        try {
            Files.writeString(filePath, json);
        } catch (IOException e) {
            System.out.println("Запись не удалась. Повторите попытку.");
            e.printStackTrace();
        }
    }
}
