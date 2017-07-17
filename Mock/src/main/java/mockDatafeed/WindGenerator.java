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
        // North = 0x0000 (0 or -32768) East = 0x4000 (16384) West = -0x4000 (-16384)
        // North-West = 0x6000 (24576) North-East -0x6000 (-24576) South-West = 0x2000 (8192)
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
        if(windSpeed < 3000) {
            windSpeed = (short) (ThreadLocalRandom.current().nextInt(0, 2+1) + windSpeed);
        }
        else if(windSpeed > 8000) {
            windSpeed = (short) (ThreadLocalRandom.current().nextInt(-2, 0) + windSpeed);
        }
        else {
            windSpeed = (short) (ThreadLocalRandom.current().nextInt(-2, 2+1) + windSpeed);
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
