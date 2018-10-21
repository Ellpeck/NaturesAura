package de.ellpeck.naturesaura.aura.chunk;

public class DrainSpot {

    private int amount;

    public DrainSpot(int amount) {
        this.amount = amount;
    }

    public void drain(int amount) {
        this.amount -= amount;
    }

    public void store(int amount) {
        this.amount += amount;
    }

    public int getAmount() {
        return this.amount;
    }

    public boolean isEmpty() {
        return this.getAmount() == 0;
    }
}
