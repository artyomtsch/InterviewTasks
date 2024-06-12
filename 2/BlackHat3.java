import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BlackHat3 {
    private Record[] uniqueUsers;

    public BlackHat3(String filename) throws IOException {
        TreeSet<Record> sortedSet = new TreeSet<>(Comparator.comparingInt(r -> r.phoneNumber));

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

            sortedSet.add(new Record(phoneNumber, fullNameArr));
        }

        uniqueUsers = sortedSet.toArray(new Record[0]);
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
        int index = Arrays.binarySearch(
                uniqueUsers,
                new Record(phoneNumber, new String[] {"", "", ""}),
                Comparator.comparingInt(r -> r.phoneNumber)
        );

        if (index < 0) {
            return "Not found";
        }

        Record record = uniqueUsers[index];
        return record.firstName + " " + record.middleName + " " + record.lastName;
    }
}

class Record {
    public final int phoneNumber;
    public final String firstName;
    public final String middleName;
    public final String lastName;

    public Record(int phoneNumber, String[] fullName) {
        this.phoneNumber = phoneNumber;
        this.firstName = fullName[0];
        this.middleName = fullName[1];
        this.lastName = fullName[2];

    }
}
