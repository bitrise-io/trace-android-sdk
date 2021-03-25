// this script validates the most recent commits against our conventional commit rules
// to use please run `node commit-linter.js`
// it supports two optional arguments for the number of commits to look at,
// and whether it should be run in debug mode
// e.g. to run in debug mode with 100 commits `node commit-linter 100 debug`

let maxTitleLength = 72;
let maxBodyLineLength = 72;

let debug = false; // if running debug it logs out each commits pass message
let maxNumberOfCommits = 10; // maximum number of commits to evaluate

if (process.argv[2] != undefined) {
  maxNumberOfCommits = process.argv[2];
}

if (process.argv[3] == "debug") {
  debug = true;
}

function getAllCommitHashes() {
  require('child_process').exec('git rev-list main', function(err, hashBlock) {

      // split the hashBlock into seperate hashes
      // https://stackoverflow.com/a/21895354
      let hashes = hashBlock.split(/\r?\n/);

      // set a default for how many to look back at
      let counter = 1;

      for (let index = 0; index < maxNumberOfCommits; index++) {
        getSpecifiCommit(hashes[index]);
      }

  });
}

function getSpecifiCommit(sha1) {
  require('child_process').exec(`git show -s --format='%B' ${sha1}`, function(err, message) {

    let messageParts = message.split(/\r?\n/);
    let commitStatus = `${sha1} - `;
    let failures = false;

    // check title length
    if (messageParts[0].length <= maxTitleLength) {
       commitStatus += "✅"
    } else {
      commitStatus += "❌"
      failures = true;
    }

    // check title matches expected format
    let scopeTypes = "[feat\(]|[fix\(]|[refactor\(]"
    let simpleTypes = "feat:|fix:|refactor:|docs:|refactor:|test:|ci:|chore:";
    if (messageParts[0].match(`(${simpleTypes}${scopeTypes}).*`)) {
      commitStatus += "✅"
    } else {
      commitStatus += "❌"
      failures = true;
    }

    // check each following line
    if (checkAllLinesAreLessThan72Chars(messageParts)) {
        commitStatus += "✅"
    } else {
      commitStatus += "❌"
      failures = true;
    }

    // check last line should be a ticket number with APM-
    if (messageParts[messageParts.length -2].match(`(APM-).*`) ) {
      commitStatus += "✅"
    } else {
      commitStatus += "❌"
      failures = true;
    }

    // check the line after the title should be empty
    if (messageParts[1].length == 0) {
      commitStatus += "✅"
    } else {
      commitStatus += "❌"
      failures = true;
    }

    // check the line before the ticket should be empty
    if (messageParts[messageParts.length -3].length == 0) {
      commitStatus += "✅"
    } else {
      commitStatus += "❌"
      failures = true;
    }

    if (debug || failures) {
      console.log(commitStatus)
    }

  });
}

// returns true if all lines ARE less than maxBodyLineLength chars
function checkAllLinesAreLessThan72Chars(messageParts) {
  let isUnder72Chars = true;
  for (var index in messageParts) {
    let message = messageParts[index];
    if (message.length > maxBodyLineLength) {
      isUnder72Chars = false;
    }
  }

  return isUnder72Chars;
}

getAllCommitHashes();
