package minborg.piproject;

import com.pi4j.io.gpio.*;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public final class Patterns {

    private static final long JIFFY_MS = 50;
    private static final int REPEAT = 8;
    private static final int MAX_PWM = 1024;

    public static void main(String[] args) throws InterruptedException {

        // Pin-out:
        // https://pi4j.com/1.2/pins/model-b-plus.html

        System.out.println("Started " + Patterns.class.getSimpleName());

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        final List<Pin> allPins = Arrays.asList(
                RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03,
                RaspiPin.GPIO_04, RaspiPin.GPIO_05, RaspiPin.GPIO_06, RaspiPin.GPIO_07);

        System.out.println("Pins " + allPins);

        System.out.println("Provisioning pins");
        final long start = System.currentTimeMillis();

        final List<GpioPinDigitalOutput> outputPins = allPins.stream()
                .map(p -> gpio.provisionDigitalOutputPin(p, p.toString(), PinState.LOW))
                .collect(toList());

        final List<GpioPinDigitalOutput> outputPinsReversed = new ArrayList<>(outputPins);
        Collections.reverse(outputPinsReversed);

        final long duration = System.currentTimeMillis() - start;
        System.out.println("Provisioning took " + duration + " ms");

        outputPins.forEach(p -> p.setShutdownOptions(true, PinState.LOW));

        System.out.println("GpioPinDigitalOutput " + outputPins);

        System.out.println("Flash");
        flash(outputPins);

        System.out.println("PingPong");
        pingPong(outputPins);

        System.out.println("Glow");
        outputPins.forEach(Patterns::glow);
        outputPinsReversed.forEach(Patterns::glow);


/*        final PwmThread pwmThread = new PwmThread(outputPins.get(0));
        pwmThread.start();

        for (int i = 0; i < Long.SIZE; i++) {
            final float ratio = ((float) i) / Long.SIZE;
            pwmThread.ratio(ratio);
            Thread.sleep(1000);
        }
        pwmThread.close();*/

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();

        System.out.println("Exiting " + Patterns.class.getSimpleName());

    }

    private static void flash(final List<GpioPinDigitalOutput> outputPins) throws InterruptedException {
        for (int i = 0; i < REPEAT; i++) {
            outputPins.forEach(GpioPinDigitalOutput::high);
            Thread.sleep(JIFFY_MS);
            outputPins.forEach(GpioPinDigitalOutput::low);
            Thread.sleep(JIFFY_MS);
        }
    }

    private static void pingPong(final List<GpioPinDigitalOutput> outputPins) throws InterruptedException {
        for (int i = 0; i < REPEAT; i++) {
            for (int j = 0; j < outputPins.size(); j++) {
                GpioPinDigitalOutput digitalOutput = outputPins.get(j);
                digitalOutput.high();
                Thread.sleep(JIFFY_MS);
                digitalOutput.low();
            }
            for (int j = outputPins.size() - 1; j >= 0; j--) {
                GpioPinDigitalOutput digitalOutput = outputPins.get(j);
                digitalOutput.high();
                Thread.sleep(JIFFY_MS);
                digitalOutput.low();
            }
        }
    }

    private static void glow(GpioPinDigitalOutput outputPin) {
        final PwmThread pwmThread = new PwmThread(outputPin);
        pwmThread.start();
        try {
            for (int i = 0; i < Long.SIZE; i++) {
                final float ratio = ((float) i) / Long.SIZE;
                pwmThread.ratio(ratio);
                Thread.sleep(6);
            }
            for (int i = Long.SIZE; i >= 0; i--) {
                final float ratio = ((float) i) / Long.SIZE;
                pwmThread.ratio(ratio);
                Thread.sleep(6);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pwmThread.close();
        }
    }

    private static void pingPongPwm(final List<GpioPinPwmOutput> outputPins) throws InterruptedException {
        for (int i = 0; i < REPEAT; i++) {
            final int pwm = (int) (((double) i) * MAX_PWM / REPEAT);
            System.out.println("pwm = " + pwm);
            for (int j = 0; j < outputPins.size(); j++) {
                GpioPinPwmOutput digitalOutput = outputPins.get(j);
                digitalOutput.setPwm(pwm);
                Thread.sleep(JIFFY_MS);
                digitalOutput.setPwm(0);
            }
            for (int j = outputPins.size() - 1; j >= 0; j--) {
                GpioPinPwmOutput digitalOutput = outputPins.get(j);
                digitalOutput.setPwm(pwm);
                Thread.sleep(JIFFY_MS);
                digitalOutput.setPwm(0);
            }
        }
    }

    private static final class PwmThread extends Thread {

        private final GpioPinDigitalOutput output;
        private volatile boolean closed;
        private volatile long ratioMap;

        public PwmThread(final GpioPinDigitalOutput output) {
            this.output = output;
        }

        @Override
        public void run() {
            while (!closed) {

                for (int i = 0; i < Long.SIZE; i++) {
                    if ((ratioMap & (1L << i)) != 0) {
                        output.high();
                    } else {
                        output.low();
                    }

                    // Spin-wait.

/*                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        return;
                    }*/
                }
            }
        }

        public void ratio(float ratio) {
            ratioMap = patternFor(ratio);
            //System.out.println("ratio = " + ratio + " : " + Long.toBinaryString(ratioMap));
        }

        public void close() {
            this.closed = true;
        }

    }

    static long patternFor(float ratio) {
        float sum = 0;
        long newRatioMap = 0;
        for (int i = 0; i < Long.SIZE; i++) {
            sum += ratio;
            if (sum >= 1) {
                newRatioMap |= 1L << i;
                sum -= 1;
            }
        }
        return newRatioMap;
    }


}