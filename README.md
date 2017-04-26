# banking-system

FIXME: description

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

This will start an HTTP web server running on your localhost on port 9000. You can change it if you want to.

It is important to say that this project does not have HTML visualization. Therefore you must interact with it using tools such as [Postman](https://www.getpostman.com/).

You can also run Unit Tests by typing:

    $ lein test

## Examples

This project offers 6 endpoints for you to interact with the Bank.

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
