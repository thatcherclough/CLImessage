# CLImessage
CLImessage is a command line interface SMS text messaging program.

## Features
CLImessage allows the sending and receiving of SMS text messages to and from a computer using a G-Mail account.
This is done by sending emails to an address that forwards them to a phone number.

When run for the fist time, CLImessage:
- Creates directory 'userdata' to store user information and conversations.
- Prompts user for G-Mail account credentials and stores them in 'userdata/creds.properties'.
- Allows the creation of new conversations.

When creating a conversation, CLImessage:
- Prompts user for name and phone number of new conversation.
- Gets the email address that forwards to this phone number.
- Opens the messenger and allows sending and receiving of messages.
- Logs sent and received messages to 'userdata/ConversationName-Number.convo'.

When opening a previously created conversation, CLImessage:
- Opens the messenger and allows sending and receiving of messages.
- Displays the last 10 logged messages.
- Logs all sent and received messages.

Other features:
- When receiving a message, CLImessage will play a notification sound.

## Demo
<a href="https://asciinema.org/a/RD8LOwo4d6tmkWLIXlus6vGqD" target="_blank"><img src="https://asciinema.org/a/RD8LOwo4d6tmkWLIXlus6vGqD.svg" width="600"/></a>

## Requirements
- A Java JDK distribution >=8 must be installed and added to PATH.
- The G-Mail account to be used must have 3rd party access enabled at https://myaccount.google.com/lesssecureapps.
- Any phone numbers used must have one of the following carriers:
  - AT&T
  - T-Mobile
  - Verizon
  - Spring
  - MetroPCS
  - Boost Mobile
  - Cricket Wireless

## Compatibility
CLImessage is compatible with Mac and Linux.

## Installation
```
# clone CLImessage
git clone https://github.com/thatcherclough/CLImessage.git

# change the working directory to CLImessage
cd CLImessage

# build CLImessage with Maven
# for Linux and Mac run
sh mvnw clean package
```

Alternatively, you can download the jar from the [release page](https://github.com/thatcherclough/CLImessage/releases).

## Usage
```
java -jar climessage.jar
```

## License
- [MIT](https://choosealicense.com/licenses/mit/)
- Copyright 2020 © Thatcher Clough.
