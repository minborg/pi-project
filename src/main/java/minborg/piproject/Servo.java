package minborg.piproject;

import com.pi4j.io.gpio.*;

import java.util.concurrent.locks.LockSupport;

public final class Servo {

    public static void main(String[] args) throws Exception {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput outputPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, RaspiPin.GPIO_01.toString(), PinState.LOW);

        System.out.println("outputPin = " + outputPin);

        for (int i = 0; i < 10; i++) {
            outputPin.high();
            Thread.sleep(100);
            outputPin.low();
            Thread.sleep(100);
        }

        final ServoThread2 thread = new ServoThread2(outputPin);
        thread.start();

        System.out.println("-120 degrees");
        thread.degree(-120);
        Thread.sleep(1000);

        System.out.println("270 degrees");
        thread.degree(270);
        Thread.sleep(1000);

        System.out.println("15 degree steps each second");
        for (int i = -120; i < 270; i = i + 15) {
            thread.degree(i);
            System.out.println(i + " degrees.");
            Thread.sleep(1000);
        }

        System.out.println("1 degree steps each 20 ms");
        for (int i = -120; i < 270; i++) {
            thread.degree(i);
            System.out.println(i + " degrees.");
            Thread.sleep(20);
        }

        System.out.println("Zero degrees before shut down");
        thread.degree(0);
        Thread.sleep(100);

        thread.close();
        outputPin.low();

        gpio.shutdown();

    }

    private static final class ServoThread extends Thread {

        private static final long ONE_MS_IN_NS = 1_000_000;

        private final GpioPinDigitalOutput output;
        private final long startNs = System.nanoTime();
        private volatile boolean closed;
        private volatile long durationNs;

        public ServoThread(final GpioPinDigitalOutput output) {
            this.output = output;
            ratio(0.5f);
        }

        @Override
        public void run() {
            long nextOn = System.nanoTime();
            while (!closed) {
                if (nextOn >= System.nanoTime()) {
                    // spin wait
                }
                output.high();
                final long nextOff = System.nanoTime() + durationNs;
                if (nextOff >= System.nanoTime()) {
                    // spin wait
                }
                output.low();

                nextOn += 20 * ONE_MS_IN_NS;
                //System.out.printf("nextOn=%,d  nextOff=%,d%n", nextOn - startNs, nextOff - startNs);
/*                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // do nothing
                }*/

            }
        }

        public void ratio(float ratio) {
            durationNs = (long) ((1 + ratio) * ONE_MS_IN_NS);
            System.out.printf("%.2f %,d%n", ratio, durationNs);
        }

        public void close() {
            this.closed = true;
        }

        void busyWait(long durationNs) {
            final long start = System.nanoTime();
            while (start + durationNs >= System.nanoTime()) ;
        }


    }

    private static final class ServoThread2 extends Thread {

        private static final long ONE_MS_IN_NS = 1_000_000;

        private final GpioPinDigitalOutput output;
        private final long startNs = System.nanoTime();
        private volatile boolean closed;
        private volatile long durationNs;

        public ServoThread2(final GpioPinDigitalOutput output) {
            this.output = output;
        }

        @Override
        public void run() {
            while (!closed) {
                LockSupport.parkNanos(20 * ONE_MS_IN_NS - durationNs);
                output.high();
                LockSupport.parkNanos(durationNs);
                output.low();
            }
        }

        public void degree(int degree) {
            ratio(((float) degree) / 180);
        }

        public void ratio(float ratio) {
            durationNs = (long) ((1 + ratio) * ONE_MS_IN_NS);
        }

        public void close() {
            this.closed = true;
        }

    }

}
