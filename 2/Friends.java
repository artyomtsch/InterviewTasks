import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Friends {
    private BlockingQueue<Map.Entry<String,String>> sceneLines;
    private String[] characterNames;
    private String filename;

    public Friends(String filename, String[] characterNames) {
        this.sceneLines = new ArrayBlockingQueue<>(1);
        this.characterNames = characterNames;
        this.filename = filename;
    }

    public void playScene() throws InterruptedException {
        Thread linesProducer =
                new Thread(new LinesProducer(sceneLines, filename));
        linesProducer.start();

        List<Thread> threads = new ArrayList<>();

        for (String characterName : characterNames) {
            Thread thread =
                    new Thread(new LinesConsumer(sceneLines), characterName);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}

class LinesProducer implements Runnable {
    private String filename;
    private BlockingQueue<Map.Entry<String,String>> queue;
    private final Map.Entry<String,String> DUMMY = Map.entry("DUMMY", "DUMMY");

    public LinesProducer(BlockingQueue<Map.Entry<String,String>> queue,
                         String filename) {
        this.filename = filename;
        this.queue = queue;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = initialize();
        String curLine;
        while ((curLine = readLine(bufferedReader)) != null) {
            String name = curLine.split(":", 2)[0];
            put(Map.entry(name, curLine));
        }

        put(DUMMY);
    }

    private BufferedReader initialize() {
        try {
            return new BufferedReader(new FileReader(this.filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String readLine(BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void put(Map.Entry<String,String> value) {
        try {
            queue.put(value);
        } catch (InterruptedException e) {
        }
    }
}

class LinesConsumer implements Runnable {
    private BlockingQueue<Map.Entry<String,String>> queue;
    private final Map.Entry<String,String> DUMMY = Map.entry("DUMMY", "DUMMY");

    public LinesConsumer(BlockingQueue<Map.Entry<String,String>> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        String myName = Thread.currentThread().getName();
        while (true) {
            Map.Entry<String,String> characterLine = queue.peek();
            if (characterLine == null) {
                sleep(1);
            } else if (characterLine.equals(DUMMY)) {
                break;
            } else if (myName.equals(characterLine.getKey())) {
                String line = queue.poll().getValue();
                System.out.println(line);
                sleep(1);
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}
