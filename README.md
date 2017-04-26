# banking-system

HTTP server that exposes 6 endpoints that allow users to create accounts, insert operations (Take or put money), get current account balance, generate account statements with configurable periods of time and get periods of time where an account had negative balance.

This project does not implement any databases or authentication steps. It assumes that users were already authenticated while communicating with it.

Future work would be refine some unit tests and error handling, do some function optimizations and enable HTTPS.

## Installation

First of all, you need the JVM and its associated tools, the JDK. In order to check if you already have it installed, run:

    $ which java

You should see:

    $ /usr/bin/javac

If not, you must install it. You can find more information regarding the installation [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html). After you install it, you will need a Clojure build tool called [Leiningen](https://leiningen.org/). You can have it installed by typing the following commands:

    $ mkdir -p ~/bin
    $ cd ~/bin
    $ curl -O https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
    $ chmod a+x lein

If you are a Windows user, their website will guide you through the installation process. After this, check if your installation was completed by typing the following command:

    $ lein -v

If you see an output, you are ready to go!

## Usage

First of all, you need to have this project in your machine. Please type:

    $ git clone https://github.com/lucasbfernandes/banking-system.git

And:

    $ cd banking-system/

This is the root of the project and the place you need to be in order to have it running. You can start to interact with it by typing:

    $ lein run 9000

This will start an HTTP web server running on your localhost on port 9000. You can change it if you want to. You can also generate a jar and run it by:

    $ lein compile
    $ lein uberjar
    $ cd target/uberjar
    $ java -jar banking-system-0.1.0-SNAPSHOT-standalone.jar 9000

It is important to say that this project does not have HTML visualization. Therefore you must interact with it using tools such as [Postman](https://www.getpostman.com/).

You can also run Unit Tests by typing:

    $ lein test

## Specs

This project offers 6 endpoints for you to interact with the banking system. They are:

#### Account creation:

**URL**: /account/create

**JSON input:**
```json
{
  "name": "Name",
  "email": "email@email.com"
}

```
**JSON return:**
```json
{
  "status": true,
  "message": "Operation was successful.",
  "account-number": "161546"
}
```

#### Debit operation (Take money):

**URL**: /account/debit

**JSON input:**
```json
{
  "account-number": "123456",
  "description": "description",
  "amount": "1000.0",
  "date": "2017-02-09"
}
```
**JSON return:**
```json
{
  "status": true,
  "message": "Operation was successful."
}
```

#### Credit operation (Put money):

**URL**: /account/credit

**JSON input:**
```json
{
  "account-number": "123456",
  "description": "description",
  "amount": "1000.0",
  "date": "2017-02-09"
}
```

**JSON return:**
```json
{
  "status": true,
  "message": "Operation was successful."
}
```

#### Current balance operation:

**URL**: /account/balance

**JSON input:**
```json
{
  "account-number": "123456"
}
```

**JSON return:**
```json
{
  "status": true,
  "message": "Operation was successful.",
  "balance": 75.0
}
```

#### Statement operation:

**URL**: /account/statement

**JSON input:**
```json
{
  "account-number": "123456",
  "begin-date": "2017-03-05",
  "end-date": "2017-08-25"
}
```

**JSON return:**
```json
{
  "status": true,
  "message": "Operation was successful.",
  "statement": {
    "2017-05-08": {
      "balance": 1600,
      "operations": [
        {
          "description": "description",
          "amount": 800,
          "type": "C"
        },
        {
          "description": "description",
          "amount": 800,
          "type": "C"
        }
      ]
    }
  }
}
```

#### Debt periods operation:

**URL**: /account/debt

**JSON input:**
```json
{
  "account-number": "123456",
  "begin-date": "2017-03-05",
  "end-date": "2017-08-25"
}
```

**JSON return:**
```json
{
  "status": true,
  "message": "Operation was successful.",
  "debt-periods": [
    {
      "principal": 1400,
      "start": "2017-05-10"
    }
  ]
}
```

Note that values must be in the correct form, otherwise the service will return failure: 

```json
{
  "status": false,
  "message": "Invalid parameters."
}
```

## License

Copyright © 2017

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
