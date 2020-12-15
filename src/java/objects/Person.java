package objects;

public class Person {
    
    private final String name;
    private final String lastname;
    private final long identifier;
    private final byte age;

    public Person(String name, String lastname, long identifier, byte age) {
        this.name = name;
        this.lastname = lastname;
        this.identifier = identifier;
        this.age = age;
    }

    public String getCompleteName() {
        return name + ' ' + lastname;
    }

    public long getIdentifier() {
        return identifier;
    }

    public byte getAge() {
        return age;
    }
    
    
    
}
