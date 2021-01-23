package minborg.piproject;

import com.pi4j.io.gpio.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

public final class Pwm {

    /**
     * [ARGUMENT/OPTION "--pin (#)" | "-p (#)" ]
     * This example program accepts an optional argument for specifying the GPIO pin (by number)
     * to use with this GPIO listener example. If no argument is provided, then GPIO #1 will be used.
     * -- EXAMPLE: "--pin 4" or "-p 0".
     *
     * @param args a
     * @throws InterruptedException Is
     */
    public static void main(String[] args) throws InterruptedException {


        // https://raspberrypi.stackexchange.com/questions/40105/access-gpio-pins-without-root-no-access-to-dev-mem-try-running-as-root



        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
        final Console console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "PWM Example");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // create GPIO controller instance
        GpioController gpio = GpioFactory.getInstance();

        // All Raspberry Pi models support a hardware PWM pin on GPIO_01.
        // Raspberry Pi models A+, B+, 2B, 3B also support hardware PWM pins: GPIO_23, GPIO_24, GPIO_26
        //
        // by default we will use gpio pin #01; however, if an argument
        // has been provided, then lookup the pin by address
        Pin pin = CommandArgumentParser.getPin(
                RaspiPin.class,    // pin provider class to obtain pin instance from
                RaspiPin.GPIO_01,  // default pin if no pin argument found
                args);             // argument array to search in

        GpioPinPwmOutput pwm = gpio.provisionPwmOutputPin(pin);

        // you can optionally use these wiringPi methods to further customize the PWM generator
        // see: http://wiringpi.com/reference/raspberry-pi-specifics/
        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
        com.pi4j.wiringpi.Gpio.pwmSetClock(500);

        // set the PWM rate to 500
        pwm.setPwm(500);
        console.println("PWM rate is: " + pwm.getPwm());

        console.println("Press ENTER to set the PWM to a rate of 250");
        System.console().readLine();

        // set the PWM rate to 250
        pwm.setPwm(250);
        console.println("PWM rate is: " + pwm.getPwm());


        console.println("Press ENTER to set the PWM to a rate to 0 (stop PWM)");
        System.console().readLine();

        // set the PWM rate to 0
        pwm.setPwm(0);
        console.println("PWM rate is: " + pwm.getPwm());

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();
    }
}