# Proper Replay Parser

A big cleanup of [the other parser](https://github.com/bwarren2/replay_parser).  Currently only a sketch of classes and their interactions.

`Worker` is the major entry point that is intended to run in Docker containers on ECS.  The work done here should be idempotent; parsing the same file multiple times just overwrites the old output files on S3.  There is a theoretically bad condition where:

    1. A previous parser run generates good files people start using.
    2. A new parse is run and the files only partially upload (somehow)

but because sending confirmation back to Python happens after file upload, this case should self correct; a new parse request should be made to some Java container to handle things.

`FileBox` is an abstraction for all the data management that happens.  It gets populated by the Parser.

`Parser` takes a Replay and FileBox, ripping the former apart to populate the latter.

`Replay` takes a file URL and does local management to make that file accessible for everything else.

`QueueManager` handles AMQP stuff so `Worker` doesn't have to.

`MatchRequest` and `MatchResponse` codify the message-sending back to RabbitMQ.  They might turn into locals of `QueueManager` depending on how factoring goes.
