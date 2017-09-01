package mockDatafeed;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by psu43 on 17/07/17.
 * Randomly generate wind speed and direction
 */
class WindGenerator {

    private short windSpeed;
    private short windDirection;

    /**
     * Constructs a wind generator
     * @param windSpeed int wind speed initial value
     * @param windDirection int wind direction initial direction
     */
    WindGenerator(int windSpeed, int windDirection) {
        this.windSpeed = (short) windSpeed;
        this.windDirection = (short) windDirection;
    }

    short getWindSpeed() {
        return getRandomWindSpeed();
    }

    short getWindDirection() {
        return getRandomWindDirection();
    }



    /**
     * Generates a random value for wind speed.
     * @return short wind speed
     */
    private short getRandomWindSpeed() {
        if(windSpeed < 4000) {
            windSpeed = (short) (ThreadLocalRandom.current().nextInt(0, 10) + windSpeed);
        }
        else if(windSpeed > 8000) {
            windSpeed = (short) (ThreadLocalRandom.current().nextInt(-10, 0) + windSpeed);
        }
        else {
            windSpeed = (short) (ThreadLocalRandom.current().nextInt(-10, 11) + windSpeed);
        }

        return windSpeed;

    }

    /**
     * Generates a random value for wind direction
     * @return short wind direction
     */
    private short getRandomWindDirection() {
        windDirection = (short) (ThreadLocalRandom.current().nextInt(-30, 30+1) + windDirection);
        return windDirection;
    }
}
