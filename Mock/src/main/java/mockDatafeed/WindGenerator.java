package mockDatafeed;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by psu43 on 17/07/17.
 * Randomly generate wind speed and direction
 */
class WindGenerator {

    private short windSpeedOriginal;
    private short windDirectionOriginal;
    private short windSpeed;
    private short windDirection;
    private long lastUpdateTime = 0;

    /**
     * Constructs a wind generator
     *
     * @param windSpeed     int wind speed initial value
     * @param windDirection int wind direction initial direction
     */
    WindGenerator(int windSpeed, int windDirection) {
        this.windSpeedOriginal = (short) windSpeed;
        
        this.windDirectionOriginal = (short) windDirection;
    }

    short getWindSpeed() {
        if (System.currentTimeMillis() - lastUpdateTime > 5000) {
            lastUpdateTime = System.currentTimeMillis();
            getRandomWindSpeed();
            getRandomWindDirection();
        }
        return windSpeed;
    }

    short getWindDirection() {
        if (System.currentTimeMillis() - lastUpdateTime > 5000) {
            lastUpdateTime = System.currentTimeMillis();
            getRandomWindDirection();
            getRandomWindSpeed();
        }
        return windDirection;
    }


    /**
     * Generates a random value for wind speed.
     */
    private void getRandomWindSpeed() {
        int speedVariance = 100;
        windSpeed = (short) (windSpeedOriginal + ThreadLocalRandom.current().nextGaussian() * speedVariance);


    }

    /**
     * Generates a random value for wind direction
     */
    private void getRandomWindDirection() {

        int directionVariance = 300;
        windDirection = (short) (windDirectionOriginal + ThreadLocalRandom.current().nextGaussian() * directionVariance);
    }
}
