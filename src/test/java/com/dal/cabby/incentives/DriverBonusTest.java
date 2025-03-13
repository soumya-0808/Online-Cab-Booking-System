package com.dal.cabby.incentives;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DriverBonusTest {

    @Test
    void giveDriverBonusFiveStars() {
        DriverBonus driverBonus = new DriverBonus();
        int bonus = driverBonus.giveDriverBonus(5);
        Assertions.assertEquals(2, bonus, "Incentive not correct.");
    }

    @Test
    void giveDriverBonusFourStars() {
        DriverBonus driverBonus = new DriverBonus();
        int bonus = driverBonus.giveDriverBonus(4);
        Assertions.assertEquals(1, bonus, "Incentive not correct.");
    }

    @Test
    void giveDriverBonusThreeStars() {
        DriverBonus driverBonus = new DriverBonus();
        int bonus = driverBonus.giveDriverBonus(3);
        Assertions.assertEquals(0, bonus, "Incentive not correct.");
    }

}