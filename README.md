# Proper com.datadrivendota.parser.Replay com.datadrivendota.parser.Parser

A big cleanup of [the other parser](https://github.com/bwarren2/replay_parser).  Currently only a sketch of classes and their interactions.

`com.datadrivendota.parser.Worker` is the major entry point that is intended to run in Docker containers on ECS.  The work done here should be idempotent; parsing the same file multiple times just overwrites the old output files on S3.  There is a theoretically bad condition where:

    1. A previous parser run generates good files people start using.
    2. A new parse is run and the files only partially upload (somehow)

but because sending confirmation back to Python happens after file upload, this case should self correct; a new parse request should be made to some Java container to handle things.

`com.datadrivendota.parser.FileBox` is an abstraction for all the data management that happens.  It gets populated by the com.datadrivendota.parser.Parser.

`com.datadrivendota.parser.Parser` takes a com.datadrivendota.parser.Replay and com.datadrivendota.parser.FileBox, ripping the former apart to populate the latter.

`com.datadrivendota.parser.Replay` takes a file URL and does local management to make that file accessible for everything else.

`com.datadrivendota.parser.MatchRequest` and `com.datadrivendota.parser.MatchResponse` codify the message-sending back to RabbitMQ.  They might turn into locals of `com.datadrivendota.parser.QueueManager` depending on how factoring goes.

We target Java 1.7 because Jackson has troubles with 1.8.  (Unable to discover "Versioned".)  This might be fixable with more effort than I am putting in now, but I don't need 8 yet and 7 works, so 7 it is.