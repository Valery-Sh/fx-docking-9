/*
 * Copyright 2018 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.demo.worker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 *
 * @author Valery
 */
public class PrimeFinderTask extends Task<ObservableList<Long>> {

    private long lowerLimit = 1;
    private long upperLimit = 30;
    private long sleepTimeInMillis = 500;

    public PrimeFinderTask() {
    }

    public PrimeFinderTask(long lowerLimit, long upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    public PrimeFinderTask(long lowerLimit, long upperLimit, long sleepTimeInMillis) {
        this(lowerLimit, upperLimit);
        this.sleepTimeInMillis = sleepTimeInMillis;
    }

    // The task implementation
    @Override
    protected ObservableList<Long> call() {
        // An observable list to represent the results
        final ObservableList<Long> results = FXCollections.<Long>observableArrayList();
        // Update the title
        this.updateTitle("Prime Number Finder Task");
        long count = this.upperLimit - this.lowerLimit + 1;
        long counter = 0;
        // Find the prime numbers
        for (long i = lowerLimit; i <= upperLimit; i++) {
            // Check if the task is cancelled
            if (this.isCancelled()) {
                break;
            }
            // Increment the counter
            counter++;
            // Update message
            this.updateMessage("Checking " + i + " for a prime number");
            // Sleep for some time
            try {
                Thread.sleep(this.sleepTimeInMillis);
            } catch (InterruptedException e) {
                // Check if the task is cancelled
                if (this.isCancelled()) {
                    break;
                }
            }
            // Check if the number is a prime number
            if (PrimeUtil.isPrime(i)) {
                // Add to the list
                results.add(i);
                // Publish the read-only list to give the GUI access to the
                // partial results
                updateValue(
                        FXCollections.<Long>unmodifiableObservableList(
                                results));
            }
            // Update the progress
            updateProgress(counter, count);
        }
        return results;
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        updateMessage("The task was cancelled.");
    }

    @Override
    protected void failed() {
        super.failed();
        updateMessage("The task failed.");
    }

    @Override
    public void succeeded() {
        super.succeeded();
        updateMessage("The task finished successfully.");
    }
}
