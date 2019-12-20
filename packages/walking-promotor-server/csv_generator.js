const process = require('process');
const http = require('http');

const WEARING_LIMIT = 8 * 60;
let HOSTS = [
  'http://192.168.0.101:8000',
  'http://192.168.0.102:8000',
  'http://192.168.0.103:8000',
  'http://192.168.0.104:8000',
];
if (process.env.HOSTS) {
  HOSTS = process.env.HOSTS.split(/\s+/);
}
// HOSTS = ["http://127.0.0.1:8000"];

// mark as GLOBAL variables;
const gDates = calcDates();
const gResult = {};
const CRLF = '\r\n';

proceed(HOSTS, 0);

function proceed(hosts, index) {
  if (index < hosts.length) {
    const url = hosts[index] + '/v2/walked.tsv';
    console.error(url);
    http
      .get(url, res => {
        let data = '';
        res.on('data', chunk => {
          data += chunk;
        });
        res.on('end', () => {
          merge(data);
          proceed(hosts, index + 1);
        });
      })
      .on('error', () => {
        proceed(hosts, index + 1);
      });
  } else {
    finish();
  }
}

function calcDates() {
  const ret = [];
  const now = Date.now(); // Number
  const DAY = 24 * 60 * 60 * 1000;
  for (let i = 7; i > 0; i--) {
    const date = new Date(now - i * DAY);
    const str = [date.getFullYear(), zeroPad2(date.getMonth() + 1), zeroPad2(date.getDate())].join('-');
    ret.push(str);
  }
  return ret;
}

function zeroPad2(num) {
  return ('00' + num).slice(-2);
}

function merge(data) {
  const lines = data.split('\n');
  for (let i = 0; i < lines.length; i++) {
    const cells = lines[i].split('\t');
    if (cells.length !== 4) continue;
    const date = cells[0];
    const count = cells[1] | 0;
    const wared = cells[2] | 0;
    const name = cells[3].trim();
    if (count > 0 && wared >= WEARING_LIMIT) {
      register(date, count, name);
    }
  }
}

function register(date, count, name) {
  // console.log("register", date, count, name, gDates);
  const ix = gDates.indexOf(date);
  if (ix === -1) return;

  let ary = gResult[name];
  if (!ary) {
    ary = [];
    gResult[name] = ary;
  }
  ary[ix] = Math.max(ary[ix] || 0, count);
}

function finish() {
  // console.log("finish", JSON.stringify(gResult));
  const table = [];
  for (const name in gResult) {
    const ary = gResult[name];
    let sum = 0;
    let num = 0;
    for (let i = 0; i < ary.length; i++) {
      const value = ary[i];
      if (value != null) {
        num++;
        sum += value;
      }
    }
    const avg = num === 0 ? 0 : sum / num;
    table.push([name, avg]);
  }
  const output = table
    .map(row => {
      return row.join(',');
    })
    .join(CRLF);
  console.log(output);
  console.error(table.length + '件のデータを出力しました。');
}
