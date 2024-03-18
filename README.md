# Jenkins CloudEvents Plugin

This is a Jenkins plugin that integrates with CloudEvents. It provides functionality to send events related to the run of a job and to build and send CloudEvents to a specified sink.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java
- Maven
- Jenkins

### Installing

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install` to build the project

## Usage

The plugin provides two main classes:

- `CurrentStage.java`: This class handles the job model's status updates and determines whether to send a build event based on the event type and result.
- `HTTPSink.java`: This class is responsible for building and sending CloudEvents to a specified sink.


## Screenshot
[]()
