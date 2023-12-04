package fr.istic.vv;

public class Person {
    private int age;
    private String name;
    
    public String getName() { return name; }

    public int getAge() { return age; }

    public boolean isAdult() {
        return age > 17;
    }
}
