# ♕ Tyler's CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

Sequence Diagram: https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN79lGjAALYotQBEMHMANDC46gDu0BzziysoU8BICDtzAL6YwjUwFazsXJT1YwOTMycra6qbUNsLyzD7h2Ov3ObE43FgN0uonqUEy2TAlAAFBksjlKBkAI6pHIASgu1VE10qsnkShU6nq9hQYAAqgNEeMoC8UHiSYplGpVESjDpagAxJCcGB0yhsmA6SwwRnMzA6WHAADWwoGMHWSDA8Slz2mhmACHlHElKAAHvCNGyyZzrpCCSp6iKoGz8SIVNbKpcHjAFPqUMBDe1FegAKLGlTYAgFZ1XUo3YrmeqBJzBYYjGbqYBU+pzINQbx1LUTHUwPUGyXyBXoM5mTiYC0c9Ruqou2YwNA+BAIKOEm3N+ogeUIh0M7Uzd76T5bPYHI54qGum518mqep+jhDh1s1naS0Nm7GWoKDgcZWi7Rd+fu20t-u+hEKHwa4eFmazq-cxecldH++Pn-xTe1tu9ZcnuvKHsef5OnOhgxpUoL3HUKLwuiajtlg8Hgo2Hp1E8z6zL8KzAA+8TtBAFZoCc5zYY2cYYLU4ROE4KbSjqbzFsRpHkZR1YcKYXi+AE0DsFSMR8nAQbSHACgwAAMhAWSFLRzA9jU9TNG0XS9AY6j5GgzEjvhuyrOOXw-IsVHVJQ3IYQ8uFMqxBHGRsWzcTZEKXr2MAIPJgqInJCmYtiYCvs275AUulLUkOLEvoBpLAdy+4CkKG7aOKkoxWIcq+kqAByEAFrAaoaqs3hUEgHAoMemVxeyS5YVe9Rth254wSpDz5Q6fK+JwIZhhGhTUbB8DIPGiZMaMaaqBmLbZrm0BNQVjLpTAGpyIVzJVugvEfruKnQiejpntB3LQfUNRIAAZpYTQmVsiIfKZKypfIeLZYqMAAJJoCA0CwuAt3Od8rUNdQnrfb9uYoADd3fH1KDhrpUZWcNSm1ImACMKZTTN8w5nm9SCpD-3MI9WxbTWJ37XaN5yCgf5PvZsVU8S4WfsW37EYikHaFu8X1aB9TgV6xFQW+w1ufU-mChkqhoZgbmg6pMB2cybFERqnHoNxQ2xqNdEwAxE2qw5RkayRZHa8CPF8d4fj+F4KDoDEcSJI7zv+b4WBKadln5g00hBjJQbtEG3Q9Npqi6cM5ta4NfvWXc4KPLHlsUdbivUy23n2F7fnyV7gVqMFIMLmzFIwFSYAM6n5F83VVqCzAyUQaLaUSuxmtp7K8pKmgBU8-ItU7iBWdNe2nZU+1+b5X+0BIAAXnkBTw4jka65UaMwJj2Ocrjiz4wtUqTMR88L1VLherCMCWL4xY+n6ko51Sx5ewA-HMNu7aPHnlXaIsajFqFMu-N2aVW4HeLmg9gD1xHolXkMgUAQMMNAlatd0ClxuJLWSBcHyy3lpnaetQRgWTBhCPWJQwD1CNimc421bYCX8LCY8-hsCCiVDJeEABxHUGgfZEMaFwkO4d7A6hjhxbuQ04JJ09CMdB6dzIKxke5Jsf9s7cN4X5eERdcSl1ZqAiuVca4SLrsPBKTcW4AP-O3SU8ie45VbAPNuQ9v5KwOs1Se4sBGz1PlARey80CrwGsjchm99ZUO3k4LGk096ZjmgTY+5sz5VQpjtcuP9VEHWgXomQ6TIpgB4WmThOQzEC0qPuSu1JvQIEKWoUpjcx5eg7LUoBajGzYOKQU3h+CEDoWUUrWRoi0xqRGEMlAn1pD1AxuEYIgQVjrHiOqFADo1aOWSKABUKzTZ-DGblbZpxOikOjBQ+MhtGIpjGcuRooydQTKmTMuZqpFmDgMmxdZIBNmvMcrs-ZhybaeDtgEDgAB2NwTgUBOBiEGYIHIABs8AByGFqUUcJvsyFqSaDSTSPQxniK7uRC5Oo9mvGtlI24YJZHyLYj8klijM6-wOrTBEtTERwERTokuLNckGOXEYrm8jYHmPKbySxqCO52PenlJxgCzyuOGmdDxmDf6eh8RqM+ASglIw3iNSh6Mom73THEw++YfAnzVX48+HBUn1L2gy-+2SuVnRgEylALTtCIhpSyHJ38vwcBZW616NqQLCqFkeGAyKp7SIpfmNlt4UA9L6dGgZ+YbkzDuTAaZsyjkoxOQbGhowxnpszYEOhNYAWMMsEg7y6wXZIASGAStHYIA1oAFIQEFOGos-h3kKhRZQtFytmhYpEWIs1FsCWjGwAgYAlaoBwAgN5Oo3zbmTNJQnLByiU4mKtkZKdM7KDzsXdSldrl+mNIAFbtrQCyttMsUBYmLiFNpICG6GKqfy7daBBVlJ5PUS6gpW4yvkGgz99iPr5SsU6OVjTFWRtUSqiAc8LUatDAjYJ2qt47xiYa-Cxr6imqSRalJpa0k8rcXaB14t9GvquVXFlhbpDfsbiG5uAHO0zDFB3BjYGlTfSoHqCqlcV1BvIy2WDXjlX5j4wJv1K7NXr3XbmiJ40DXTTidJ5Ax4GPWug3alsAbgA5KdSAeI0MFSHugC0bA6AHrTtnRZqAKwDNvV7vABd0BhSTH40cXQ3AlXwZje5qANIvOAl8ygeT8cyE0XCQmJMqncbxKPiWX0hpVqBjQNax1jUpTYC0MynUbJEQGfeHZg9QWViZSfd2ajI98N5bpv6wrvMRNNxpA1hE7GUCcclNxuD2Db1oATUopN8q-aPGzaEnVpz80kP+fxe2XgZ21vrUtyUiBfSwGANgKdhAAl9vMAOz0Acg4hzDjAQYxgQmJ2jY8cB1IWztCgKkEEZ69P1HuwiVpNXuU0Y+0gh7UH0nwPqBkKYEAaD3wQEYM820gA

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
