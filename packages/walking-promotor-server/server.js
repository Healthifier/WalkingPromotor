const fs = require('fs');
const http = require('http');
const path = require('path');
const process = require('process');
const encoding = require('encoding-japanese');

const PORT = process.env.PORT | 0 || 8000;
const contentDir = 'UserData';
const trailingNumber = /[0-9]+$/;

const SQUARE_STEP_IDS = [10, 13, 23, 19, 25, 29, 39];

function createTsvSync(apiVersion) {
  const d = contentDir;
  const ret = [];
  fs.readdirSync(d).forEach(name => {
    const dirPath = path.join(d, name);
    if (fs.statSync(dirPath).isDirectory() === false) return;
    const csvPath = path.join(d, name, name + '.csv');
    if (fs.existsSync(csvPath) === false) return;

    const content = fs.readFileSync(csvPath, { flag: 'r' });
    const unicodeArray = encoding.convert(content, 'UNICODE', 'AUTO');
    const string = encoding.codeToString(unicodeArray);
    const lines = string.split('\n');
    for (let i = 3; i < lines.length; i++) {
      const cells = lines[i].split(',');
      if (cells.length <= 1) continue;
      const date = cells[0].split('/').join('-');
      const walked = cells[11] | 0;
      const wared = cells[21] | 0;

      const name2 = name.replace(trailingNumber, '');
      let retRow = [date, walked, name2];
      if (apiVersion === 2) {
        retRow = [date, walked, wared, name2];
      }
      ret.push(retRow.join('\t'));
    }
  });
  return ret.join('\n') + '\n';
}

function getSquareStepIdsAsTsv() {
  return SQUARE_STEP_IDS.join('\t') + '\n';
}

function respondText(res, text, status) {
  res.writeHead(status || 200, {
    'Content-Type': 'text/plain; charset=utf-8',
    'Content-Length': Buffer.byteLength(text, 'utf8'),
  });
  res.end(text);
}

const server = http.createServer((req, res) => {
  console.log('request ' + req.url + ' at ' + new Date().toISOString());
  try {
    if (req.url === '/walked.tsv') {
      respondText(res, createTsvSync(1));
    } else if (req.url === '/v2/walked.tsv') {
      respondText(res, createTsvSync(2));
    } else if (req.url === '/steps.tsv') {
      respondText(res, getSquareStepIdsAsTsv());
    } else {
      respondText(res, 'Not Found\n', 404);
    }
  } catch (e) {
    console.error(e);
    console.error(e.stack);
    respondText(res, 'Server Error\n', 500);
  }
});

server.listen(PORT);
console.log('Server running at http://127.0.0.1:' + PORT + '/');
