import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// O(logn) in-memory database
public class SimpleDB {
    private Map<Long, Record> records = new TreeMap<>();
    private Map<String, Set<Record>> nameIndex = new TreeMap<>();
    private Map<Double, Set<Record>> valueIndex = new TreeMap<>();

    public SimpleDB() {
    }

    // Test
    public SimpleDB(String csv) {
        try (var bufferedReader = new BufferedReader(new FileReader(csv))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] recordArr = line.split(",");
                long account = Long.parseLong(recordArr[0]);
                String name = recordArr[1];
                double value = Double.parseDouble(recordArr[2]);

                add(new Record(account, name, value));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean add(Record record) {
        if (records.containsKey(record.account)) {
            return false;
        }

        records.put(record.account, record);
        nameIndex.computeIfAbsent(record.name, k -> new HashSet<Record>())
                .add(record);
        valueIndex.computeIfAbsent(record.value, k -> new HashSet<Record>())
                .add(record);

        return true;
    }

    public boolean remove(long account) {
        if (!records.containsKey(account)) {
            return false;
        }

        Record record = records.get(account);

        records.remove(account);
        removeFromIndex(nameIndex, record.name, record);
        removeFromIndex(valueIndex, record.value, record);

        return true;
    }

    private <T> void removeFromIndex(
            Map<T, Set<Record>> index,
            T key,
            Record account
    ) {
        Set<Record> accounts = index.get(key);
        accounts.remove(account);
        if (accounts.isEmpty()) {
            index.remove(key);
        }
    }

    public boolean change(long accountOld, long accountNew) {
        if (!records.containsKey(accountOld)
                || records.containsKey(accountNew)) {
            return false;
        }

        Record record = records.get(accountOld);
        remove(accountOld);
        add(new Record(accountNew, record.name, record.value));

        return true;
    }

    public boolean change(long account, String name) {
        if (!records.containsKey(account)) {
            return false;
        }

        Record record = records.get(account);
        remove(account);
        add(new Record(record.account, name, record.value));

        return true;
    }

    public boolean change(long account, Double value) {
        if (!records.containsKey(account)) {
            return false;
        }

        Record record = records.get(account);
        remove(account);
        add(new Record(record.account, record.name, value));

        return true;
    }

    public Record get(long account) {
        return records.get(account);
    }

    public Set<Record> get(String name) {
        if (nameIndex.get(name) == null) {
            return null;
        }

        return nameIndex.get(name);
    }

    public Set<Record> get(Double value) {
        if (valueIndex.get(value) == null) {
            return null;
        }

        return valueIndex.get(value);
    }
}

class Record {
    final long account;
    final String name;
    final double value;

    public Record(long account, String name, double value) {
        this.account = account;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return account + " " + name + " " + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return account == ((Record) obj).account;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(account);
    }
}
