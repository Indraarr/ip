# Margit User Guide

![Margit interface](Ui.png)

Margit, the Fell Task Keeper, is a desktop task manager for recording,
reviewing, and planning the burdens before thee. Commands are entered in the
chat input at the bottom of the window. Margit saves the task list after every
change, so it is available the next time the application opens.

## Getting started

Launch Margit from your IDE by running `margit.Launcher`, or build the project
with Gradle and run the generated JAR file. Type a command and press Enter or
select **Send**. Margit greets you when the application opens.

## Task types

Margit manages three kinds of task:

- **Todo**: a task without a date or time.
- **Deadline**: a task due by a date or date-time.
- **Event**: a task that has a start and end date or date-time.

Tasks are saved automatically in `data/Margit.txt` relative to the folder from
which the application is launched.

## Date and time formats

Use either a date or a date-time when creating deadlines and events. Margit
accepts the following common formats:

```text
19/9/2026
2026-09-19
19/9/2026 1900
2026-09-19 19:00
```

A deadline given as a date only is treated as due at 23:59 on that date when
tasks are sorted.
