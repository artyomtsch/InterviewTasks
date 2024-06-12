import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class SimpleDB {
    private TreeMap<Long, Record> records = new TreeMap<>();
    private TreeMap<String, List<Long>> nameIndex = new TreeMap<>();
    private TreeMap<Double, List<Long>> valueIndex = new TreeMap<>();

    public SimpleDB(String csv) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new FileReader(csv));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] recordArr = line.split(",");
            long account = Long.parseLong(recordArr[0]);
            String name  = recordArr[1];
            double value = Double.parseDouble(recordArr[2]);

            add(new Record(account, name, value));
        }
    }

    public boolean add(Record record) {
        if (records.containsKey(record.account)) {
            return false;
        }

        records.put(record.account, record);
        nameIndex.computeIfAbsent(record.name, k -> new ArrayList<Long>())
                .add(record.account);
        valueIndex.computeIfAbsent(record.value, k -> new ArrayList<Long>())
                .add(record.account);

        return true;
    }

    public boolean remove(long account) {
        if (!records.containsKey(account)) {
            return false;
        }

        Record record = records.get(account);

        records.remove(account);
        removeFromIndex(nameIndex, record.name, account);
        removeFromIndex(valueIndex, record.value, account);

        return true;
    }

    private <T> void removeFromIndex(
            TreeMap<T, List<Long>> index,
            T key,
            long account
    ) {
        List<Long> accounts = index.get(key);
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

    public List<Record> get(String name) {
        if (nameIndex.get(name) == null) {
            return null;
        }

        List<Record> result = new ArrayList<>();
        for (Long acc : nameIndex.get(name)) {
            result.add(records.get(acc));
        }
        return result;
    }

    public List<Record> get(Double value) {
        if (valueIndex.get(value) == null) {
            return null;
        }

        List<Record> result = new ArrayList<>();
        for (Long acc : valueIndex.get(value)) {
            result.add(records.get(acc));
        }
        return result;
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
}
