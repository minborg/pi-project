package minborg.piproject;

import com.pi4j.io.gpio.*;
import com.pi4j.system.SystemInfo;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class FlashAll {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Started " + FlashAll.class.getSimpleName());

        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        final List<Pin> allPins = Stream.of(RaspiPin.allPins((SystemInfo.BoardType) null)).collect(toList());

        final List<GpioPinDigitalOutput> outputPins = allPins.stream()
                .map(p -> gpio.provisionDigitalOutputPin(p, p.toString(), PinState.HIGH))
                .collect(toList());

        outputPins.forEach(p -> p.setShutdownOptions(true, PinState.LOW));

        System.out.println("--> GPIO state should be: ON");

        outputPins.forEach(GpioPinDigitalOutput::low);

        for (int i = 0; i < 20; i++) {
            Thread.sleep(1000);
            outputPins.forEach(GpioPinDigitalOutput::toggle);
        }

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();

        System.out.println("Exiting " + FlashAll.class.getSimpleName());

    }

}