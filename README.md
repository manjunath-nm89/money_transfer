# Money Transfer

A simple application to transfer accounts between different accounts.

### Features
* Create Users
* Create Accounts (Checking and Savings)
* Deposit Money into Accounts
* Transfer Money between Accounts

### Pre-Requisites
* Java 8
* Gradle (https://gradle.org/install)

### Quick Start
```java
> git clone https://github.com/manjunath-nm89/money_transfer.git
> cd money_transfer
> java -jar build/libs/money_transfer-0.0.1-all.jar  server environments/money_transfer-dev.yml

Open http://localhost:8080/swagger on the browser
```

##### Generate and run a fresh jar
```java
> ./gradlew -g ./cache/.gradle clean shadowJar 
> java -jar build/libs/money_transfer-0.0.1-all.jar  server environments/money_transfer-dev.yml
```