import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BlackHat2 {
    private Map<Integer,String[]> uniqueUsers;

    public BlackHat2(String filename) throws IOException {
        uniqueUsers = new HashMap<>();

        HashMap<String,String> firstNames = new HashMap<>();
        HashMap<String,String> middleNames = new HashMap<>();
        HashMap<String,String> lastNames = new HashMap<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            // process phone
            String phone = line.split(",")[0];
            phone = phone.substring(2);
            int phoneNumber = (int) (Long.parseLong(phone) - 8_000_000_000L);

            // process full name
            String fullName = line.split(",")[1];
            String[] fullNameArr = fullName.split(" ");

            // код позволяет использовать один объект String для одинаковых ФИО
            fullNameArr[0] = processName(firstNames, fullNameArr[0]);
            fullNameArr[1] = processName(middleNames, fullNameArr[1]);
            fullNameArr[2] = processName(lastNames, fullNameArr[2]);

            uniqueUsers.put(phoneNumber, fullNameArr);
        }
    }

    private String processName(HashMap<String,String> names, String name) {
        if (names.containsKey(name)) {
            return names.get(name);
        } else {
            names.put(name, name);
            return name;
        }
    }

    public String getUser(String phone) {
        if (!phone.matches("\\+79\\d{9}")) {
            return "Неправильный формат номера, шаблон: +79ххххххххх";
        }

        phone = phone.substring(2);
        int phoneNumber = (int) (Long.parseLong(phone) - 8_000_000_000L);
        String[] fullNameArr = uniqueUsers.get(phoneNumber);

        if (fullNameArr == null) {
            return "Not found";
        }

        return fullNameArr[0] + " " + fullNameArr[1] + " " + fullNameArr[2];
    }
}
