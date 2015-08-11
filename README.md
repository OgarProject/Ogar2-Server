# Ogar 2
An open-source Agar.io server implementation written in Java. Ogar 2 is the successor to [Ogar 1.0, the old Node.js implementation](https://github.com/OgarProject/Ogar).

## Why the rewrite?
Ogar 2 is still under heavy development, but will ultimately replace Ogar 1.0 when it is complete. There's are two primary reasons for this:
* *Performance* - Node.js isn't multi-threaded, and this creates a large performance bottleneck when using Ogar 1.0 with any more than a few players.
* *Extensibility* - With Ogar 1.0, modifying the server's behavior (to add new game types, for example) required modifying the server's source code. A plugin API is in the works for Ogar 2, which will allow for a cleaner method for extending and modifying the server's behavior: individual plugin JAR files, placed into a plugin directory.

We're doing everything we can to make the eventual transition from Ogar 1.0 to Ogar 2 as easy and painless as possible.

## Obtaining and Using
Ogar 2 is built into a ready-to-run JAR file. Once you've obtained a JAR file, either by compiling the source yourself or from downloading one of our precompiled builds, all you need to do is run it from the command line.

```sh
~$ git clone git://github.com/OgarProject/Ogar2-Server.git Ogar
# Build the project using Maven.
~$ cd Ogar
~$ java -jar target/ogar-server-2.0-SNAPSHOT.jar
```

By default, Ogar listens on port 443. This is customizable in the configuration file. On some systems, you may have to run the process with elevated privileges to allow it to listen on the proper ports. However, for security reasons, this isn't preferred; instead, you should modify the configuration file and choose a higher port (on *nix systems, ports above 1024 usually work without superuser privileges).

Once the game server is running, you can connect (locally) by going to the Agar.io website. Once the game is loaded, in your address bar, replace the URL with `javascript:connect("ws://127.0.0.1:443","");` (replace the port in that code with the port you specified in your configuration), and press Enter.

## Configuring Ogar
Use "config.json" to modify Ogar's configuration. Ogar 2 supports importing legacy configuration files (gameserver.ini) from Ogar 1.0 - if a legacy configuration file is in the server directory, it will be automatically imported.

## Contributing
Please see [CONTRIBUTING.md](https://github.com/OgarProject/Ogar2-Server/blob/master/CONTRIBUTING.md) for contribution guidelines.

## License
Please see [LICENSE.txt](https://github.com/OgarProject/Ogar2-Server/blob/master/LICENSE.txt).
