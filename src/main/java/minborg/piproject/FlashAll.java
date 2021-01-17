package minborg.piproject;

import com.pi4j.io.gpio.*;
import com.pi4j.system.SystemInfo;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class FlashAll {

    public static void main(String[] args) throws InterruptedException {

        // Pin-out:
        // https://pi4j.com/1.2/pins/model-b-plus.html

        System.out.println("Started " + FlashAll.class.getSimpleName());

        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        final List<Pin> allPins = Stream.of(RaspiPin.allPins((SystemInfo.BoardType) null))
                .collect(toList());

        System.out.println("Pins " + allPins);

        final List<GpioPinDigitalOutput> outputPins = allPins.stream()
                .map(p -> gpio.provisionDigitalOutputPin(p, p.toString(), PinState.HIGH))
                .collect(toList());

        System.out.println("GpioPinDigitalOutput " + allPins);

        outputPins.forEach(p -> p.setShutdownOptions(true, PinState.LOW));

        System.out.println("--> GPIO state should be: ON");

        outputPins.forEach(GpioPinDigitalOutput::low);

        System.out.println("Flashing all");

        for (int i = 0; i < 6; i++) {
            Thread.sleep(500);
            outputPins.forEach(GpioPinDigitalOutput::toggle);
        }

        for (GpioPinDigitalOutput outputPin : outputPins) {
            System.out.println("Flashing " + outputPin);
            for (int i = 0; i < 6; i++) {
                Thread.sleep(500);
                outputPin.toggle();
            }
        }

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();

        System.out.println("Exiting " + FlashAll.class.getSimpleName());

    }

}