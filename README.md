# Git history cleaner

Git history cleaner is tool you can use to recreate your git history in half-automatic manner in order to make it more clean, squash messy commits and anonymise authors.

Git history is fetched from **Private repository**, changed and then published in **Public Repository**.

Package contains set of tools to perform steps in order to publish clean history in **Public Repository**. All tools are modules in Java CLI application, you can pick module providing it's name/alias as argument.

- **Initial Squasher** - squash first commits in repository as initial commit

- **Merge Analyser** - create history of commits to your master branch and save to **History File**

- **Jira Fetcher** - fetch Jira ticket names for items in **History File**

- **History Rewriter** - rewrite commit history from one repository to another based on **History File**
 
- **Continous Publisher** - keep two repositories in sync, updating public repository with clean merges from private (similar to **History Rewriter** but meant to be run on regular basis)


## Initial Squasher

With this tool you can squash some first commits in **Private Repository** in to single "initial commit". 

The range squashed as initial commit is set up in configuration *privateRepository* section: *firstCommitInRepository* is start and *lastSquashedCommit* is end of this range.

### Run
```
java -jar git-history-cleaner.jar initial-squash
```

## Merge Analyser
Writes commit history to **History File**. Only direct commits to branch are fetched (--first-parent). 

Start of this range is next commit after *lastSquashedCommit* set in *privateRepository* configuration.

### Run
```
java -jar git-history-cleaner.jar analyse-merges
```

## Jira Fetcher
Jira fetcher allows to fetch ticket name from ticket number put in commit message. Ticket number format and Jira URL is configurable in config.yml. Jira username and password needs to be provided in arguments.
### Run
```
java -jar git-history-cleaner.jar jira-fetch ${jira_username} ${jira_password}
```

**Jira Fetcher** goes through branch history in **History File**. For each **History Item** it fetches Jira ticket name and updates **History Item's** story numbers and ouput messages.

Jira fetcher support multiple ticket numbers in commit message. If few ticket numbers are mentioned in commit, output message contains all ticket titles separated by semicolon.


## Manual validation - diff importer
Not all tickets are meant to have Jira tickets assigned. For such tickets you have ability to provide amended commit messages in json file.

Json file contains list of items containing hash and commit message.

```
[
  {
    "hash": "5d7f804adb48107856d075a24e312ba2cadefd61",
    "message": "Feature add autodocs testcase report generator"
  },
  ...
]
```
### Run
```
java -jar git-history-cleaner.jar import-diff
```

Diff importer fetches commit messages to be amended from /resources/diff.json.
Amended history is saved in **History File**.

## History Rewriter
History Rewriter is main tool in this set. It recreates history of one **Private Repository** to **Public Repository**. History should be prepared in **History File** using **Merge Analyser** and **Jira Fetcher** (optional).

Let's assume you have messy history on your master branch and/or feature branches in **Private Repository**. With history like in left column **History Rewriter** run will result with history as in right column. It squashes history on every feature branch so there will be no messy or meaningless commits in **Public Repository**. Each feature branch results in one merge commit and one commit with squashed history.

Table presents result of running **History Rewriter** on examplary repositories.

|Private Repository (master)|Public repository (master)|
|---------------------------|--------------------------|
|Merge "feature/F-1"        |Merge "feature/branch/F-1"|
|F-1 intial commit          |F-1 First Feature         |
|F-1 work in progress       |                          |
|F-1 almost done            |                          |
|F-1 unit tests             |                          |
|Bugfix 1                   |Bugfix 1                  |
|Bugfix 2                   |Bugfix 2                  |
|Merge "feature/F-2"        |Merge "feature/branch/F-2"|
|F-2 intial commit          |F-2 Second Feature        |
|F-2 done                   |                          |

###History Items

Target history of public repository is being kept in json file containing **History Items** named **History File** in this readme.

**History Item** is basically GIT commit extended with some meta data.

```
{
	hash: "907066a19dad35043ec4d1a2c70bbc470d1a49e8",
	originalMessage: "original commit message",
	outputMessage: "processed messaged to be commited in public repository",
	storyNumbers: ["F-1", "F-2"],
	date: "2012-04-23T18:25:43.511Z",
}
```

###Skipped Commits
If you find some commits in history you want to skip, e.g. they bring on value, or are supposed to be squashed with other commits you can add hash of commit to *skippedCommits* in *Private Repository* section. If commit is skipped, **Jira Fetcher** will not try to fetch title for it, also **Histor Rewriter** will squash this commit with next one.

### Run
```
java -jar git-history-cleaner.jar history-rewrite
```

**History Rewriter** takes **History Items** from **History File**, from oldest one and squashes every commit on source branch (**Private Repository**) till next **History Item**.

Note: this tool doesn't push changed history into **Public Repository**. Push needs to be done manually.

## Continous Publisher

Continous Publisher keeps **Private Repository** and **Public Repository** in sync by patching clean merges from one to another.

**Patch History** is used to keep history of applied patches.


### Run
```
java -jar git-history-cleaner.jar publish
```

## Configuration

Configuration is stored in /src/main/resources/config.yml

@todo add ```yaml if this page lands on github

```
publicRepository:
  authorName: "Public Author"
  authorEmail: "public@authors.com"
  authorFullName: "Public Author <public@authors.com>"
  initialCommitDate: "Wed Jun 17 14:00 2015 +0100"
  publishingHistoryFileName: "git-history.json"
  publishingDiffFileName: "src/main/resources/diff.json"
  publicRepoUrl: "git@github.com:YourRepository/public.git"
  initialCommitMessage: "Inital commit"
  destinationBranchName: "the-new-master"
  skippedCommitMessage: "##SKIP"

privateRepository:
  patchFilePath: "/tmp/patch.patch"
  firstCommitInRepository: "9a86b3d7741d84c6774b75319ccf6581d7adf5cd"
  lastSquashedCommit: "61f3b67a5163087cdedb037074cc80a59842d151"
  sourceBranchName: "master"
  skippedCommits:
    - "be672b6eb00f35c58e6fa020b24945a77af91687"
    - "f62199d3dc535d0aba83ddc70df71b3852fa29c9"
    - "da41bb875c32f5a297845b0d2a5fbb9a70f722c6"

jira:
  ticketNumberFormat: "((BL|MDM|VM)-[0-9]{1,6})"
  jiraApiUrl: "https://jira/rest/api/latest/issue/"
```

## Jenkins job example
@todo or not @todo?