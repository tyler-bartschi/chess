# CS 240 Advanced Software Construction Notes

## Java Fundamentals

- Object oriented language
- `javac` to compile and `java` to run

Primitive Types

Signed Integers

- byte: 1 byte, -128 to 127 (inclusive)
- short: 2 bytes, -32,768 to 32,767 (inclusive)
- int: 4 bytes, -2,147,483,648 to 2,147,483,647 (inclusive)
- long: 8 bytes, -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807 (inclusive)

Can use Integer.MIN_VALUE and Integer.MAX_VALUE, same with Long, Short, and Byte

- To write a long literal, use the suffix `L`. For example, `4000000000L`. Bytes and shorts must be cast, like (byte) 127.
- Hexadecimal literals have prefix `0x`, for example `0xCAFEBABE`, and binary values have prefix `0b`, like `0b1001`.
- You can add underscores to number literals like human-readable commas

Floating Point Types

- float: 4 bytes, (6-7 significant decimal digits)
- double: 8 bytes, (15 significant decimal digits)

For type float, has a suffix F. Literals without a suffix default to double, or you can add the D suffix.
Special values include Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, and Double.NaN.

- You can declare multiple variables of the same type in a single statement (but that's bad programming, so why can you do this?)
- `var` can be used when the type of variable is obvious to the compiler, similar to `auto` from C++ I thinks.
- Constants are denoted by the `final` keyword, and are generally in all caps. Declaring a constant outside a method can be done with the `static` keyword, and can be accessed outside the class with the dot operator. They can also be initalized after their declaration, provided it only happens once.
- To check if two strings are equal, use the `.equals` method.
- `==` in Java compares objects in memory, and only returns true if they are the same object in memory.
- `null` is a value that can be assigned to any object variable, indicating that it does not refer to any objects. Testing for null can be done with the `==` operator.
- `equalsIgnoreCase` will compare two strings, ignoring casing
- String class is immutable, all string methods return new strings
- `.length()` returns the last index, not the number of characters
- `Scanner` objects can be constructed to read from input. `nextLine` method on the Scanner object reads a line of input. `next` method reads a single word, delimited by whitespace. `nextInt` reads an integerl. `nextDouble` reads a double, and `hasNextLine`, `hasNext`, `hasNextInt`, `hasNextDouble` do as the name suggests. Scanner class is in the `java.util` package.
- Use `Console` class to read passwords
- You can declare and use multiple variables in a for loop, as long as they are the same type
- Can use a labeled break statement, where you define a label like `outer:` before the loop, then say `break outer;` in the loop (or from within multiple loops) and it will break out of all of them until the label. This can also be used in any statement, not just loops (for example an if).
- `ArrayList` class can be used for arrays that grow and shrink on demand, and is part of the `java.utils` package.
- Declare array types as follows: `int[] nums = new int[10]` - creates an array of 10 ints
- or declare arrays with literal syntax: `int[] primes = {2, 3, 5, 7, 11, 13 };`
- Wrapper classes are for primitive types, and include `Integer`, `Byte`, `Short`, `Long`, `Character`, `Float`, `Double`, and `Boolean`. These can be used with `ArrayList<Integer>`.
- Autoboxing is the automatic conversion between primitive types and their corresponding wrapper types.
- Use `equals` method with wrapper objects, since `==` and `!=` compare actual object references
- Use the `Arrays` class for common array methods, and use the `Collections` class for common ArrayList methods
- args array starts at 0, the call to `java` and the file name are not included.
- 2d arrays are initialzed like this: `int[][] square = new int[4][4]`
- In method parameters, you can use something like this: `average(double... values)`, which can accept any number of arguments. It creates an array of the passed arguments. A parameter of this kind must be the last parameter of the method.
- use an InputStream to read in files from a path object, and an OutputStream to write to a path

### Objects and Classes

- `Object` class is inherited by all Java objects. When inheriting, the class inherits all public and protected methods and fields
- You must override the `equals` method to properly compare objects. If not overridden, it checks if the two objects are the exact same instance, which is not really helpful.
- `hashCode` method is used by collections to check equality. First checks the two hashCodes, and if they match, it then calls the equals method
- Fields are variables within the class and methods are operations the class performs (functions within the class)
- `new` operator is used to create an instance of a class, called an object
- `this` when used within the class code is a reference to the object itself, and only needs to be used in the event of a naming collision.
- Can include a default constructor, one or more explicit constructors, and a copy constructor that takes as a parameter an object of the same type and makes a deep copy of that objects fields and puts them into the new object.
- Data objects only exist to represent a collection of data fields, and exist only to serve as input or output for other objects that operate on them.
- records are an object that represent a data object without you having to program the whole object.
- `record PetRecord(int id, String name, String type) {}` creates a record object, that is immutable, with automatic getters, equals, hashcode, and toString methods
- If needed, can also provide your own methods to the record in the {}