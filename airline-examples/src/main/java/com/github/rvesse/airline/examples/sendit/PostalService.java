/**
 * Copyright (C) 2010-16 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rvesse.airline.examples.sendit;

/**
 * Grades of postal service
 * 
 * @author rvesse
 *
 */
public enum PostalService {
    SecondClass(0.5), FirstClass(1.0), Tracked48(2.0), NextDay(4.0), NextDayAM(10.0);

    private final double pricePerKilogram;

    PostalService(double pricePerKilogram) {
        this.pricePerKilogram = pricePerKilogram;
    }

    /**
     * Calculate the cost of a parcel
     * 
     * @param weight
     *            Parcel weight
     * @return Cost
     */
    public double calculateCost(double weight) {
        return this.pricePerKilogram * weight;
    }
}
