# nexfilesd

A build-automation agent customized for [nexfiles.kjonigsen.net](http://nexfiles.kjonigsen.net).

It may be useful for other purposes, but probably not.

## Installation

Clone repo. Customize builds in src/nexfilesd/core.clj.

Setup mount-points to remote location in local file-system via sshfs, curlftpfs or similar.

## Usage

    $ lein run

## Options

Optionally run tool with the additional parameter "test" to run it using a predefined test-configuration.

    $ lein run test

This is suitable for development-time testing.

## License

Copyright © 2013 Jostein Kjønigsen

Distributed under the Eclipse Public License, the same as Clojure.
