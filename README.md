[![Apache Sling](https://sling.apache.org/res/logos/sling.png)](https://sling.apache.org)

&#32;[![Build Status](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-launchpad-comparator/job/master/badge/icon)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-launchpad-comparator/job/master/)&#32;[![Test Status](https://img.shields.io/jenkins/tests.svg?jobUrl=https://ci-builds.apache.org/job/Sling/job/modules/job/sling-launchpad-comparator/job/master/)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-launchpad-comparator/job/master/test/?width=800&height=600)&#32;[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-launchpad-comparator&metric=coverage)](https://sonarcloud.io/dashboard?id=apache_sling-launchpad-comparator)&#32;[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-launchpad-comparator&metric=alert_status)](https://sonarcloud.io/dashboard?id=apache_sling-launchpad-comparator) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Launchpad Comparator

This module is part of the [Apache Sling](https://sling.apache.org) project.

Command line utility which helps with comparing the artifacts contained by two launchpad/starter instances.

## Usage

This tool expects to be part of a `repo` checkout to generate the changelogs between Sling modules. It is
recommended to run the following command to make sure all the latest changes are present in the local
checkout:

    repo forall -c 'git fetch'

Afterwards build and run this project

    mvn clean package
    java -jar target/launchpad-comparator-0.9.0-SNAPSHOT.jar 10 11-SNAPSHOT

The report will list:

    * added dependencies
    * removed dependencies
    * changed dependencies

For Sling artifacts that have changed it will also list the Jira issues which were potentially fixed between
the releases. It does so by parsing the commit messages, but does not verify that the issues are actually
fixed.
