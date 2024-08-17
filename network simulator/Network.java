import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class Router {
    private final int maxConnections;
    private final Semaphore semaphore;
    private final List<String> connections;

    public Router(int maxConnections) {
        this.maxConnections = maxConnections;
        this.semaphore = new Semaphore(maxConnections);
        this.connections = new ArrayList<>();
    }

    public void connect(String deviceName) throws InterruptedException {
        semaphore.acquire();
        connections.add(deviceName);
        System.out.println("Connection " + connections.size() + ": " + deviceName + " Occupied");
    }

    public void disconnect(String deviceName) {
        connections.remove(deviceName);
        semaphore.release();
    }

    public List<String> getConnections() {
        return new ArrayList<>(connections);
    }
}

class Device implements Runnable {
    private final String deviceName;
    private final String deviceType;
    private final Router router;

    public Device(String deviceName, String deviceType, Router router) {
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.router = router;
    }

    public void connect() throws InterruptedException {
        System.out.println("(" + deviceName + ")(" + deviceType + ") arrived");
        router.connect(deviceName);

        Thread.sleep((long) (Math.random() * 1000));
        System.out.println("Connection " + router.getConnections().size() + ": " + deviceName + " login");

        Thread.sleep((long) (Math.random() * 1000));
        System.out.println("Connection " + router.getConnections().size() + ": " + deviceName + " performs online activity");

        Thread.sleep((long) (Math.random() * 1000));
        System.out.println("Connection " + router.getConnections().size() + ": " + deviceName + " Logged out");

        router.disconnect(deviceName);
    }

    @Override
    public void run() {
        try {
            connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Network {
    public static void main(String[] args) {
        int maxConnections = 2;
        int totalDevices = 4;

        Router router = new Router(maxConnections);
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= totalDevices; i++) {
            String deviceName = "C" + i;
            String deviceType = "pc";
            threads.add(new Thread(new Device(deviceName, deviceType, router)));
        }

        for (Thread thread : threads) {
            thread.start();
        }
    }
}