import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BlackHat {
    private Map<String,String> uniqueUsers;

    public BlackHat(String filename) throws IOException {
        uniqueUsers = new HashMap<>();

        BufferedReader bufferedReader =
                new BufferedReader(new FileReader(filename));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String phone = line.split(",")[0];
            String fullName = line.split(",")[1];
            uniqueUsers.put(phone, fullName);
        }

        HashMap<Integer, int[]> map = new HashMap<>();
    }

    public String getUser(String phone) {
        if (!phone.matches("\\+79\\d{9}")) {
            return "Неправильный формат номера, шаблон: +79ххххххххх";
        }
        return uniqueUsers.getOrDefault(phone, "Not found");
    }
}
